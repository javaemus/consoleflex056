/******************************************************************************
 PeT mess@utanet.at
******************************************************************************/
/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class mk2
{
	
	
	UINT8 mk2_led[5]= {0};
	
	unsigned char mk2_palette[242][3] =
	{
		{ 0x20,0x02,0x05 },
		{ 0xc0, 0, 0 },
	};
	
	unsigned short mk2_colortable[1][2] = {
		{ 0, 1 },
	};
	
	void mk2_init_colors (UBytePtr sys_palette,
							  unsigned short *sys_colortable,
							  const UBytePtr color_prom)
	{
		memcpy (sys_palette, mk2_palette, sizeof (mk2_palette));
		memcpy(sys_colortable,mk2_colortable,sizeof(mk2_colortable));
	}
	
	public static VhStartPtr mk2_vh_start = new VhStartPtr() { public int handler() 
	{
		// artwork seams to need this
	    videoram_size[0] = 6 * 2 + 24;
	    videoram = (UINT8*)auto_malloc (videoram_size[0]);
		if (!videoram)
	        return 1;
	
		{
			char backdrop_name[200];
		    /* try to load a backdrop for the machine */
			sprintf(backdrop_name, "%s.png", Machine.gamedrv.name);
			backdrop_load(backdrop_name, 2);
		}
	
		return generic_vh_start();
	} };
	
	public static VhStopPtr mk2_vh_stop = new VhStopPtr() { public void handler() 
	{
		generic_vh_stop();
	} };
	
	static const char led[]={
		"      aaaaaaaaaaaaaaa\r"
		"     f aaaaaaaaaaaaa b\r"
		"     ff aaaaaaaaaaa bb\r"
		"     fff           bbb\r"
		"     fff           bbb\r"
		"    fff           bbb\r"
		"    fff           bbb\r"
		"    fff           bbb\r"
		"    fff           bbb\r"
		"   fff           bbb\r"
		"   fff           bbb\r"
		"   ff             bb\r"
	    "   f ggggggggggggg b\r"
	    "    gggggggggggggg\r"
		"  e ggggggggggggg c\r"
		"  ee             cc\r"
		"  eee           ccc\r"
		"  eee           ccc\r"
		" eee           ccc\r"
		" eee           ccc\r"
		" eee           ccc\r"
		" eee           ccc\r"
		"eee           ccc\r"
		"eee           ccc\r"
		"ee ddddddddddd cc   hh\r"
	    "e ddddddddddddd c  hhh\r"
	    " ddddddddddddddd   hh"
	};
	
	static void mk2_draw_7segment(struct mame_bitmap *bitmap,int value, int x, int y)
	{
		draw_led(bitmap, led, value, x, y);
	}
	
	static const struct {
		int x,y;
	} mk2_led_pos[8]={
		{70,96},
		{99,96},
		{128,96},
		{157,96},
		{47,223},
		{85,223},
		{123,223},
		{162,223}
	};
	
	static void mk2_draw_led(struct mame_bitmap *bitmap,INT16 color, int x, int y)
	{
		draw_led(bitmap, radius_2_led, color, x, y);
	}
	
	void mk2_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh)
	{
		int i;
	
		for (i=0; i<4; i++) {
			mk2_draw_7segment(bitmap, mk2_led[i]&0x7f, mk2_led_pos[i].x, mk2_led_pos[i].y);
		}
	
		mk2_draw_led(bitmap, Machine.pens[mk2_led[4]&8?1:0], 
					 mk2_led_pos[4].x, mk2_led_pos[4].y);
		mk2_draw_led(bitmap, Machine.pens[mk2_led[4]&0x20?1:0], 
					 mk2_led_pos[5].x, mk2_led_pos[5].y); //?
		mk2_draw_led(bitmap, Machine.pens[mk2_led[4]&0x10?1:0], 
					 mk2_led_pos[6].x, mk2_led_pos[6].y);
		mk2_draw_led(bitmap, Machine.pens[mk2_led[4]&0x10?0:1], 
					 mk2_led_pos[7].x, mk2_led_pos[7].y);
	
		mk2_led[0]= mk2_led[1]= mk2_led[2]= mk2_led[3]= mk2_led[4]= 0;
	}
}
