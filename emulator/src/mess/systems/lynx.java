/******************************************************************************
 PeT mess@utanet.at 2000,2001

 info found in bastian schick's bll
 and in cc65 for lynx

******************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess.systems;

import static WIP.mame.memoryH.*;
import static WIP.mame.osdependH.*;
import static WIP2.mame.mameH.*;
import static mess.mess.*;
import static old.mame.inptportH.*;
import static old.mame.inptport.*;
import static WIP.arcadeflex.fucPtr.*;
import static WIP.mame.mame.*;
import static WIP.mame.sndintrfH.*;
import static WIP.mame.sndintrf.*;
import static WIP2.mame.commonH.REGION_CPU1;
import static WIP2.mess.osdepend.fileio.*;
import static old.mame.drawgfx.*;
import static old.mame.drawgfxH.*;
import static old.mame.inputH.*;
import static old.mame.driverH.*;
import static mame.commonH.*;
import static old.arcadeflex.libc_old.*;

import static mess.messH.*;
import static mess.deviceH.*;
import static WIP2.mame.usrintrf.*;
import static old.mame.cpuintrf.*;
import static old.mame.cpuintrfH.*;
import static old.mame.cpuintrf.*;
import static old.arcadeflex.video.*;
import static WIP.arcadeflex.video.*;

import static common.ptr.*;
import static consoleflex.funcPtr.*;
import static mess_spec.common.*;
import static old.arcadeflex.osdepend.logerror;

import static mess.includes.lynxH.*;
import static mess.machine.lynx.*;
import static mess.sndhrdw.lynx.*;



public class lynx
{
	
	
	public static int rotate=0;
	public static int lynx_rotate;
	static int lynx_line_y;
	public static int[] lynx_palette=new int[0x10];
        
        public static final int IMAGE_VERIFY_PASS = 1;
        public static final int IMAGE_VERIFY_FAIL = 0;
	
	static MemoryReadAddress lynx_readmem[] = {
            new MemoryReadAddress( 0x0000, 0xfbff, MRA_RAM ),
            new MemoryReadAddress( 0xfc00, 0xfcff, MRA_BANK1 ),
            new MemoryReadAddress( 0xfd00, 0xfdff, MRA_BANK2 ),
            new MemoryReadAddress( 0xfe00, 0xfff7, MRA_BANK3 ),
            new MemoryReadAddress( 0xfff8, 0xfff9, MRA_RAM ),
            new MemoryReadAddress( 0xfffa, 0xffff, MRA_BANK4 ),
            new MemoryReadAddress(-1) /* end of table */};
	
	static MemoryWriteAddress lynx_writemem[] = {
            new MemoryWriteAddress( 0x0000, 0xfbff, MWA_RAM ),
            new MemoryWriteAddress( 0xfc00, 0xfcff, MWA_BANK1 ),
            new MemoryWriteAddress( 0xfd00, 0xfdff, MWA_BANK2 ),
            new MemoryWriteAddress( 0xfe00, 0xfff8, MWA_RAM ),
            new MemoryWriteAddress( 0xfff9, 0xfff9, lynx_memory_config ),
            new MemoryWriteAddress( 0xfffa, 0xffff, MWA_RAM ),
            new MemoryWriteAddress(-1) /* end of table */};
	
	static InputPortPtr input_ports_lynx = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BITX( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1, "A", CODE_DEFAULT, CODE_DEFAULT );
		PORT_BITX( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON2, "B", CODE_DEFAULT, CODE_DEFAULT );
		PORT_BITX( 0x04, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Opt 2", KEYCODE_2, IP_JOY_DEFAULT );
		PORT_BITX( 0x08, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Opt 1",  KEYCODE_1, IP_JOY_DEFAULT );
	    PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT);
	    PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT );
	    PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN );
	    PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP   );
		PORT_START(); 
		PORT_BITX( 0x01, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Pause",  KEYCODE_3, IP_JOY_DEFAULT );
		// power on and power off buttons
		PORT_START(); 
		PORT_DIPNAME ( 0x03, 3, "90 Degree Rotation");
		PORT_DIPSETTING(	2, "counterclockwise" );
		PORT_DIPSETTING(	1, "clockwise" );
		PORT_DIPSETTING(	0, "None" );
		PORT_DIPSETTING(	3, "crcfile" );
	INPUT_PORTS_END(); }}; 
	
	//static int lynx_frame_int()
	public static InterruptPtr lynx_frame_int = new InterruptPtr() {
            public int handler() {
                lynx_rotate=rotate;
                if ((readinputport(2)&3)!=3) lynx_rotate=readinputport(2)&3;
                return ignore_interrupt.handler();
            }
        };
	
	public static VhStartPtr lynx_vh_start = new VhStartPtr() { public int handler() 
	{
	    return 0;
	} };
	
	public static VhStopPtr lynx_vh_stop = new VhStopPtr() { public void handler() 
	{
	} };
	
	static char[][] debug_strings=new char[16][30];
	static int debug_pos=0;
	/*
	DISPCTL EQU $FD92       ; set to $D by INITMIKEY
	
	; B7..B4        0
	; B3    1 EQU color
	; B2    1 EQU 4 bit mode
	; B1    1 EQU flip screen
	; B0    1 EQU video DMA enabled
	*/
	public static void lynx_draw_lines(int newline)
	{
	    int height=-1, width=-1;
	    int h,w;
	    int x, yend;
	    int j; // clipping needed!
	    UBytePtr mem=memory_region(REGION_CPU1);
	
	    if (osd_skip_this_frame() != 0) newline=-1;
	
	    if (newline==-1) yend=102;
	    else yend=newline;
	
	    if (yend>102) yend=102;
	    if (yend==lynx_line_y) {
		if (newline==-1) lynx_line_y=0;
		return;
	    }
	
	    j=(mikey.data[0x94]|(mikey.data[0x95]<<8))+lynx_line_y*160/2;
	    if ((mikey.data[0x92]&2) != 0) {
		j-=160*102/2-1;
	    }
	
	    if ((lynx_rotate & 3) != 0) { // rotation
		h=160; w=102;
		if ( ((lynx_rotate==1)&&((mikey.data[0x92]&2)!=0))
		     ||( (lynx_rotate==2)&&(mikey.data[0x92]&2)==0) ) {
		    for (;lynx_line_y<yend;lynx_line_y++) {
			for (x=160-2;x>=0;j++,x-=2) {
			    plot_pixel.handler(Machine.scrbitmap, lynx_line_y, x+1, lynx_palette[mem.read(j)>>4]);
			    plot_pixel.handler(Machine.scrbitmap, lynx_line_y, x, lynx_palette[mem.read(j)&0xf]);
			}
		    }
		} else {
		    for (;lynx_line_y<yend;lynx_line_y++) {
			for (x=0;x<160;j++,x+=2) {
			    plot_pixel.handler(Machine.scrbitmap, 102-1-lynx_line_y, x, lynx_palette[mem.read(j)>>4]);
			    plot_pixel.handler(Machine.scrbitmap, 102-1-lynx_line_y, x+1, lynx_palette[mem.read(j)&0xf]);
			}
		    }
		}
	    } else {
		w=160; h=102;
		if ( (mikey.data[0x92]&2) != 0 ) {
		    for (;lynx_line_y<yend;lynx_line_y++) {
			for (x=160-2;x>=0;j++,x-=2) {
			    plot_pixel.handler(Machine.scrbitmap, x+1, 102-1-lynx_line_y, lynx_palette[mem.read(j)>>4]);
			    plot_pixel.handler(Machine.scrbitmap, x, 102-1-lynx_line_y, lynx_palette[mem.read(j)&0xf]);
			}
		    }
		} else {
		    for (;lynx_line_y<yend;lynx_line_y++) {
			for (x=0;x<160;j++,x+=2) {
			    plot_pixel.handler(Machine.scrbitmap, x, lynx_line_y, lynx_palette[mem.read(j)>>4]);
			    plot_pixel.handler(Machine.scrbitmap, x+1, lynx_line_y, lynx_palette[mem.read(j)&0xf]);
			}
		    }
		}
	    }
	    if (newline==-1) {
		lynx_line_y=0;
		if ((w!=width)||(h!=height)) {
		    width=w;
		    height=h;
		    osd_set_visible_area(0,width-1,0, height-1);
		}
	    }
	}
	
	//void lynx_vh_screenrefresh (mame_bitmap bitmap, int full_refresh)
	public static VhUpdatePtr lynx_vh_screenrefresh = new VhUpdatePtr() {
            public void handler(mame_bitmap bitmap, int full_refresh) {
                int j;
	
                /*TODO*/////lynx_audio_debug(bitmap);

                for (j=0; j<debug_pos; j++) {
                    ui_text(bitmap, debug_strings[j].toString(), 0, j*8);
                }
                debug_pos=0;
            }
        };
	    
	//static void lynx_init_colors (int[][] sys_palette, short[] sys_colortable, UBytePtr color_prom)
	 public static VhConvertColorPromPtr lynx_init_colors = new VhConvertColorPromPtr() {
            public void handler(char[] sys_palette, char[] sys_colortable, UBytePtr color_prom) {
	    int i;
	    //int[][] palette=new int[0x1000][3];
            sys_palette = new char[0x1000*3];
	
	    for (i=0; i<(0x1000*3); i+=3) {
		//palette[i][0]=(i&0xf)*16;
		//palette[i][1]=((i&0xf0)>>4)*16;
		//palette[i][2]=((i&0xf00)>>8)*16;
                sys_palette[i]=(char)((i&0xf)*16);
                sys_palette[i+1]=(char)(((i&0xf0)>>4)*16);
                sys_palette[i+2]=(char)(((i&0xf00)>>8)*16);
	    }
	
	    //memcpy (sys_palette, palette, palette.length);
	//	memcpy(sys_colortable,lynx_colortable,sizeof(lynx_colortable));
	}};
	
	static CustomSound_interface lynx_sound_interface = new CustomSound_interface
	(
		lynx_custom_start,
		lynx_custom_stop,
		lynx_custom_update
	);
	
	static CustomSound_interface lynx2_sound_interface = new CustomSound_interface
	(
		lynx2_custom_start,
		lynx_custom_stop,
		lynx_custom_update
	);
	
	
	static MachineDriver machine_driver_lynx = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				/*TODO*/////CPU_M65SC02, // vti core, integrated in vlsi, stz, but not bbr bbs
                                CPU_M6502, // vti core, integrated in vlsi, stz, but not bbr bbs
				4000000,
				lynx_readmem,lynx_writemem,null,null,
				lynx_frame_int, 1
	        )
		},
		/* frames per second, VBL duration */
		30, DEFAULT_60HZ_VBLANK_DURATION, // lcd!, varies
		1,				/* single CPU */
		lynx_machine_init,
		null,//pc1401_machine_stop,
	
		// 160 x 102
	//	160, 102, { 0, 160 - 1, 0, 102 - 1},
		160, 160, new rectangle( 0, 160 - 1, 0, 102 - 1),
		null, //lynx_gfxdecodeinfo,			   /* graphics decode info */
		// 16 out of 4096
		0x1000,
		0, //sizeof (lynx_colortable) / sizeof(lynx_colortable[0][0]),
		lynx_init_colors,		/* convert color prom */
	
		VIDEO_TYPE_RASTER,	/* video flags */
		null,						/* obsolete */
                lynx_vh_start,
		lynx_vh_stop,
		lynx_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
		new MachineSound[] {
			new MachineSound(SOUND_CUSTOM, lynx_sound_interface)
                }
	);
	
	static MachineDriver machine_driver_lynx2 = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_M65SC02, // vti core, integrated in vlsi, stz, but not bbr bbs
				4000000,
				lynx_readmem,lynx_writemem,null,null,
				lynx_frame_int, 1
	        )
		},
		/* frames per second, VBL duration */
		30, DEFAULT_60HZ_VBLANK_DURATION, // lcd!
		1,				/* single CPU */
		lynx_machine_init,
		null,//pc1401_machine_stop,
	
		// 160 x 102
	//	160, 102, { 0, 160 - 1, 0, 102 - 1},
		160, 160, new rectangle( 0, 160 - 1, 0, 102 - 1),
		null, //lynx_gfxdecodeinfo,			   /* graphics decode info */
		// 16 out of 4096
		0x1000,
		0, //sizeof (lynx_colortable) / sizeof(lynx_colortable[0][0]),
		lynx_init_colors,		/* convert color prom */
	
		VIDEO_TYPE_RASTER,	/* video flags */
		null,						/* obsolete */
                lynx_vh_start,
		lynx_vh_stop,
		lynx_vh_screenrefresh,
	
		/* sound hardware */
		SOUND_SUPPORTS_STEREO,0,0,0,
		new MachineSound[] {
			new MachineSound(SOUND_CUSTOM, lynx2_sound_interface)
                }
	);
	
	/* these 2 dumps are saved from an running machine,
	   and therefor the rom byte at 0xff09 is not readable!
	   (memory configuration)
	   these 2 dumps differ only in this byte!
	*/
	static RomLoadPtr rom_lynx = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10200,REGION_CPU1, 0);
		ROM_LOAD("lynx.bin", 0x10000, 0x200, 0xe1ffecb6);
		ROM_REGION(0x100,REGION_GFX1, 0);
		ROM_REGION(0x100000, REGION_USER1, 0);
	ROM_END(); }}; 
	
	static RomLoadPtr rom_lynxa = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10200,REGION_CPU1, 0);
		ROM_LOAD("lynxa.bin", 0x10000, 0x200, 0x0d973c9d);
		ROM_REGION(0x100,REGION_GFX1, 0);
		ROM_REGION(0x100000, REGION_USER1, 0);
	ROM_END(); }}; 
	
	static RomLoadPtr rom_lynx2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10200,REGION_CPU1, 0);
		ROM_LOAD("lynx2.bin", 0x10000, 0x200, 0x0);
		ROM_REGION(0x100,REGION_GFX1, 0);
		ROM_REGION(0x100000, REGION_USER1, 0);
	ROM_END(); }}; 
	
	//int lynx_partialcrc(UBytePtr buf, int size)
        public static io_partialcrcPtr lynx_partialcrc = new io_partialcrcPtr(){
            public int handler(UBytePtr buf, int size) {
                int crc=0;
	
		if (size < 65) return 0;
		/*TODO*/////crc = crc32(0L,buf.read(64),size-64);
		logerror("Lynx Partial CRC: %08lx %ld\n",crc,size);
		/* printf("Lynx Partial CRC: %08x %d\n",crc,size); */
		return crc;
            }
        }; 
	
		
	public static int lynx_verify_cart (char[] header)
	{
	
		logerror("Trying Header Compare\n");
	
		if ( ("LYNX".equals(String.valueOf(header).substring(0, 4)))
                        &&
                     ("BS9".equals(String.valueOf(header).substring(6, 3))) )
                {
			logerror("Not an valid Lynx image\n");
			return IMAGE_VERIFY_FAIL;
		}
		logerror("returning ID_OK\n");
		return IMAGE_VERIFY_PASS;
	}
	
	static void lynx_crc_keyword(int io_device, int id)
	{
	    String info;
	    info=device_extrainfo(io_device, id);
	    rotate=0;
	    if (info!=null) {
		if (strcmp(info, "ROTATE90DEGREE")==0) rotate=1;
		else if (strcmp(info, "ROTATE270DEGREE")==0) rotate=2;
	    }
	}
	
	
	//static int lynx_init_cart(int id)
        public static io_initPtr lynx_init_cart = new io_initPtr(){
            public int handler(int id) {
                Object cartfile;
		UBytePtr rom = memory_region(REGION_USER1);
		int size;
		char[] header=new char[0x40];
                /* 64 byte header
                   LYNX
                   intelword lower counter size
                   0 0 1 0
                   32 chars name
                   22 chars manufacturer
                */
                
                System.out.println("lynx_init_cart");
	
		if (device_filename(IO_CARTSLOT, id) == null)
		{
			return 0;
		}
                System.out.println("lynx_init_cart1");
		if ((cartfile = image_fopen(IO_CARTSLOT, id, OSD_FILETYPE_IMAGE, 0)) == null)
		{
			logerror("%s not found\n",device_filename(IO_CARTSLOT,id));
			return 1;
		}
                System.out.println("lynx_init_cart2");
		size=osd_fsize(cartfile);
                System.out.println("lynx_init_cart3");
		if (osd_fread(cartfile, header, 0x40)!=0x40) {
			logerror("%s load error\n",device_filename(IO_CARTSLOT,id));
			osd_fclose(cartfile);
			return 1;
		}
                
                System.out.println("lynx_init_cart4");
	
		/* Check the image */
		if (lynx_verify_cart(header) != 1)
		{
			osd_fclose(cartfile);
			return INIT_FAIL;
		}
                
                System.out.println("lynx_init_cart5");
	
		size-=0x40;
		lynx_granularity=header[4]|(header[5]<<8);
                
                System.out.println("lynx_init_cart6 "+size);
                //System.out.println(osd_fread(cartfile, rom, size));
	
		/*TODO*/////logerror ("%s %dkb cartridge with %dbyte granularity from %s\n",
		/*TODO*/////		  header+10,size/1024,lynx_granularity, header+42);
	
		/*TODO*/////if (osd_fread(cartfile, rom, size)!=size) {
		/*TODO*/////	logerror("%s load error\n",device_filename(IO_CARTSLOT,id));
		/*TODO*/////	osd_fclose(cartfile);
		/*TODO*/////	return 1;
		/*TODO*/////}
                System.out.println("lynx_init_cart7");
		osd_fread(cartfile, rom, size);
                osd_fclose(cartfile);
	
		lynx_crc_keyword(IO_CARTSLOT, id);
                System.out.println("lynx_init_cart8");
		return 0;
            }
        };
	
		
	
	//static int lynx_quickload(int id)
        public static io_initPtr lynx_quickload = new io_initPtr(){
            public int handler(int id) {
                Object cartfile;
		UBytePtr rom = memory_region(REGION_CPU1);
		int size;
		char[] header=new char[10]; // 80 08 dw Start dw Len B S 9 3
		// maybe the first 2 bytes must be used to identify the endianess of the file
		int start;
	
		if (device_filename(IO_QUICKLOAD, id) == null)
		{
			return 0;
		}
	
		if ((cartfile = (FILE)image_fopen(IO_QUICKLOAD, id, OSD_FILETYPE_IMAGE, 0)) == null)
		{
			logerror("%s not found\n",device_filename(IO_QUICKLOAD,id));
			return 1;
		}
		size=osd_fsize(cartfile);
	
		if (osd_fread(cartfile, header, (header.length))!=(header.length)) {
			logerror("%s load error\n",device_filename(IO_QUICKLOAD,id));
			osd_fclose(cartfile);
			return 1;
		}
		size-=(header.length);
		start=header[3]|(header[2]<<8); //! big endian format in file format for little endian cpu
	
		if (osd_fread(cartfile, new UBytePtr(rom, start), size)!=size) {
			logerror("%s load error\n",device_filename(IO_QUICKLOAD,id));
			osd_fclose(cartfile);
			return 1;
		}
		osd_fclose(cartfile);
	
		rom.write(0xfffc+0x200, start&0xff);
		rom.write(0xfffd+0x200, start>>8);
	
		lynx_crc_keyword(IO_QUICKLOAD, id);
	
		return 0;
            }
        };
	
	static IODevice io_lynx[] = {
		new IODevice(
			IO_CARTSLOT,					/* type */
			1,								/* count */
			"lnx\0",                        /* file extensions */
			IO_RESET_ALL,					/* reset if file changed */
			null,
			lynx_init_cart, 				/* init */
			null,							/* exit */
			null,							/* info */
			null,							/* open */
			null,							/* close */
			null,							/* status */
			null,							/* seek */
			null,							/* tell */
			null,							/* input */
			null,							/* output */
			null,							/* input_chunk */
			null,							/* output_chunk */
			lynx_partialcrc				/* partial crc */
                ),
		new IODevice(
			IO_QUICKLOAD,					/* type */
			1,								/* count */
			"o\0",                        /* file extensions */
			IO_RESET_ALL,					/* reset if file changed */
			null,
			lynx_quickload, 				/* init */
			null,							/* exit */
			null,							/* info */
			null,							/* open */
			null,							/* close */
			null,							/* status */
			null,							/* seek */
			null,							/* tell */
			null,							/* input */
			null,							/* output */
			null,							/* input_chunk */
			null							/* output_chunk */
                ),
	    new IODevice(IO_END)
	};
	
        //static IODevice[] io_lynxa = io_lynx;
	//#define io_lynx2 io_lynx
                
	public static InitDriverPtr init_lynx = new InitDriverPtr() { public void handler() 
	{
		int i;
		UBytePtr gfx=memory_region(REGION_GFX1);
	
		for (i=0; i<256; i++) gfx.write(i, i);
	
		lynx_quickload.handler(0);
	
	
	} };
	
	/*    YEAR  NAME      PARENT    MACHINE   INPUT     INIT      MONITOR	COMPANY   FULLNAME */
	//CONSX( 1989, lynx,	  0, 		lynx,  lynx, 	lynx,	  "Atari",  "Lynx", GAME_NOT_WORKING|GAME_IMPERFECT_SOUND)
        public static GameDriver driver_lynx = new GameDriver("1989", "lynx", "lynx.java", rom_lynx, null, machine_driver_lynx, input_ports_lynx, null, io_lynx, "Atari", "Lynx");
	//CONSX( 1989, lynxa,	  lynx, 	lynx,  lynx, 	lynx,	  "Atari",  "Lynx (alternate rom save!)", GAME_NOT_WORKING|GAME_IMPERFECT_SOUND)
        public static GameDriver driver_lynxa = new GameDriver("1989", "lynxa", "lynx.java", rom_lynxa, null, machine_driver_lynx, input_ports_lynx, null, io_lynx, "Atari", "Lynx (alternate rom save!)");
	//CONSX( 1991, lynx2,	  lynx, 	lynx2,  lynx, 	lynx,	  "Atari",  "Lynx II", GAME_NOT_WORKING|GAME_IMPERFECT_SOUND)
        public static GameDriver driver_lynx2 = new GameDriver("1991", "lynx2", "lynx.java", rom_lynx2, null, machine_driver_lynx2, input_ports_lynx, null, io_lynx, "Atari", "Lynx II");
	
	/*TODO*/////#ifdef RUNTIME_LOADER
	/*TODO*/////extern void lynx_runtime_loader_init(void)
	/*TODO*/////{
	/*TODO*/////	int i;
	/*TODO*/////	for (i=0; drivers[i]; i++) {
	/*TODO*/////		if ( strcmp(drivers[i].name,"lynx")==0) drivers[i]=&driver_lynx;
	/*TODO*/////		if ( strcmp(drivers[i].name,"lynxa")==0) drivers[i]=&driver_lynxa;
	/*TODO*/////		if ( strcmp(drivers[i].name,"lynx2")==0) drivers[i]=&driver_lynx2;
	/*TODO*/////	}
	/*TODO*/////}
	/*TODO*/////#endif
	
}
