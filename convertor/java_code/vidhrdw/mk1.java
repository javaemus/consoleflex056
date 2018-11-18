/******************************************************************************
 PeT mess@utanet.at 2000,2001
******************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class mk1
{
	
	
	unsigned char mk1_palette[242][3] =
	{
		{ 0x20,0x02,0x05 },
		{ 0xc0, 0, 0 },
	};
	
	unsigned short mk1_colortable[1][2] = {
		{ 0, 1 },
	};
	
	void mk1_init_colors (UBytePtr sys_palette,
							  unsigned short *sys_colortable,
							  const UBytePtr color_prom)
	{
		memcpy (sys_palette, mk1_palette, sizeof (mk1_palette));
		memcpy(sys_colortable,mk1_colortable,sizeof(mk1_colortable));
	}
	
	
	public static VhStartPtr mk1_vh_start = new VhStartPtr() { public int handler() 
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
	
	public static VhStopPtr mk1_vh_stop = new VhStopPtr() { public void handler() 
	{
		generic_vh_stop();
	} };
	
	UINT8 mk1_led[4]= {0};
	
	static char led[]={
		" ii          hhhhhhhhhhhh\r"
		"iiii    bbb hhhhhhhhhhhhh ggg\r"
		" ii     bbb hhhhhhhhhhhhh ggg\r"
		"        bbb               ggg\r"
		"        bbb      jjj      ggg\r"
		"       bbb       jjj     ggg\r"
		"       bbb       jjj     ggg\r"
		"       bbb      jjj      ggg\r"
		"       bbb      jjj      ggg\r"
		"      bbb       jjj     ggg\r"
		"      bbb       jjj     ggg\r"
		"      bbb      jjj      ggg\r"
		"      bbb      jjj      ggg\r"
		"     bbb       jjj     ggg\r"
		"     bbb       jjj     ggg\r"
		"     bbb               ggg\r"
	    "     bbb ccccccccccccc ggg\r"
	    "        cccccccccccccc\r"
	    "    ddd ccccccccccccc fff\r"
		"    ddd               fff\r"
		"    ddd      kkk      fff\r"
		"    ddd      kkk      fff\r"
		"   ddd       kkk     fff\r"
		"   ddd       kkk     fff\r"
		"   ddd      kkk      fff\r"
		"   ddd      kkk      fff\r"
		"  ddd       kkk     fff\r"
		"  ddd       kkk     fff\r"
		"  ddd      kkk      fff\r"
		"  ddd      kkk      fff\r"
		" ddd       kkk     fff\r"
		" ddd               fff\r"
	    " ddd eeeeeeeeeeeee fff   aa\r"
	    " ddd eeeeeeeeeeeee fff  aaaa\r"
	    "     eeeeeeeeeeee        aa"
	};
	
	static void mk1_draw_9segment(struct mame_bitmap *bitmap,int value, int x, int y)
	{
		draw_led(bitmap, led, value, x, y);
	}
	
	static struct {
		int x,y;
	} mk1_led_pos[4]={
		{102,79},
		{140,79},
		{178,79},
		{216,79}
	};
	
	void mk1_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh)
	{
		int i;
	
		for (i=0; i<4; i++) {
			mk1_draw_9segment(bitmap, mk1_led[i], mk1_led_pos[i].x, mk1_led_pos[i].y);
			mk1_led[i]=0;
		}
	}
}
