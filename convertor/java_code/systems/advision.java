/***************************************************************************

  /systems/advision.c

  Driver file to handle emulation of the Entex Adventurevision.

  by Daniel Boris (dboris@home.com)  1/20/2000

***************************************************************************/

/**********************************************
8048 Ports:
P1 	Bit 0..1  - RAM bank select
	Bit 3..7  - Keypad input:

P2 	Bit 0..3  - A8-A11
	Bit 4..7  - Sound control

T1	Mirror sync pulse

***********************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package systems;

public class advision
{
	
	static MEMORY_READ_START( readmem )
	    { 0x0000, 0x03FF,  MRA_BANK1 },
	    { 0x0400, 0x0fff,  MRA_ROM },
		{ 0x2000, 0x23ff,  MRA_RAM },	/* MAINRAM four banks */
	MEMORY_END
	
	static MEMORY_WRITE_START( writemem )
	    { 0x0000, 0x0fff, MWA_ROM },
		{ 0x2000, 0x23ff, MWA_RAM },	/* MAINRAM four banks */
	MEMORY_END
	
	static PORT_READ_START( readport )
	    { 0x00,     0xff,     advision_MAINRAM_r},
	    { I8039_p1, I8039_p1, advision_getp1 },
	    { I8039_p2, I8039_p2, advision_getp2 },
	    { I8039_t0, I8039_t0, advision_gett0 },
	    { I8039_t1, I8039_t1, advision_gett1 },
	PORT_END
	
	static PORT_WRITE_START( writeport )
	    { 0x00,     0xff,     advision_MAINRAM_w },
	    { I8039_p1, I8039_p1, advision_putp1 },
	    { I8039_p2, I8039_p2, advision_putp2 },
	PORT_END
	
	static InputPortPtr input_ports_advision = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
	    PORT_BIT ( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON4);
	    PORT_BIT ( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON3);
	    PORT_BIT ( 0x04, IP_ACTIVE_HIGH, IPT_BUTTON2);
	    PORT_BIT ( 0x08, IP_ACTIVE_HIGH, IPT_BUTTON1);
	    PORT_BIT ( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN);
	    PORT_BIT ( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_UP);
	    PORT_BIT ( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT);
	    PORT_BIT ( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT);
	INPUT_PORTS_END(); }}; 
	
	static MachineDriver machine_driver_advision = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
	            CPU_I8048,
	            14000000/15,
	            readmem,writemem,readport,writeport,
				ignore_interrupt,1
			)
		},
		8*15, DEFAULT_REAL_60HZ_VBLANK_DURATION,
		1,
		advision_init_machine,	/* init_machine */
		null,						/* stop_machine */
	
		/* video hardware */
		320,200, new rectangle(0,320-1,0,200-1),
		NULL,
		(8+2)*3,
		8*2,
		advision_vh_init_palette,
	
		VIDEO_TYPE_RASTER,
		null,
	    advision_vh_start,
	    advision_vh_stop,
	    advision_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
	);
	
	
	ROM_START (advision)
		ROM_REGION(0x2800,REGION_CPU1, 0);
	    ROM_LOAD ("avbios.rom", 0x1000, 0x400, 0x279e33d1);
	ROM_END(); }}; 
	
	static const struct IODevice io_advision[] = {
		{
			IO_CARTSLOT,		/* type */
			1,					/* count */
			"bin\0",            /* file extensions */
			IO_RESET_ALL,		/* reset if file changed */
			0,					/* id */
			advision_load_rom, 	/* init */
			NULL,				/* exit */
			NULL,				/* info */
			NULL,				/* open */
			NULL,				/* close */
			NULL,				/* status */
			NULL,				/* seek */
			NULL,				/* tell */
	        NULL,               /* input */
			NULL,				/* output */
			NULL,				/* input_chunk */
			NULL				/* output_chunk */
	    },
	    { IO_END }
	};
	
	/*    YEAR  NAME      PARENT    MACHINE   INPUT     INIT      COMPANY   FULLNAME */
	CONSX( 1982, advision, 0,		advision, advision,	0,		  "Entex",  "Adventurevision", GAME_NO_SOUND )
	
}
