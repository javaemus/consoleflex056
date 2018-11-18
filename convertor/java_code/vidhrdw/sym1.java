/******************************************************************************
	SYM-1

	PeT mess@utanet.at May 2000

******************************************************************************/
/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class sym1
{
	
	
	UINT8 sym1_led[6]= {0};
	
	unsigned char sym1_palette[242][3] =
	{
	  	{ 0x20,0x02,0x05 },
		{ 0xc0, 0, 0 },
	};
	
	public static VhConvertColorPromPtr sym1_init_colors = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) 
	{
		memcpy (palette, sym1_palette, sizeof (sym1_palette));
	} };
	
	public static VhStartPtr sym1_vh_start = new VhStartPtr() { public int handler() 
	{
	    videoram_size[0] = 6 * 2 + 24;
	    videoram = (UINT8*) auto_malloc (videoram_size[0]);
		if (!videoram)
	        return 1;
	
		{
			char backdrop_name[200];
		    /* try to load a backdrop for the machine */
			sprintf (backdrop_name, "%s.png", Machine.gamedrv.name);
			backdrop_load(backdrop_name, 3);
		}  
	
		if (generic_vh_start () != 0)
	        return 1;
	
	    return 0;
	} };
	
	public static VhStopPtr sym1_vh_stop = new VhStopPtr() { public void handler() 
	{
	    videoram = NULL;
	    generic_vh_stop ();
	} };
	
	static const char led[] =
		"          aaaaaaaaa\r" 
		"       ff aaaaaaaaa bb\r"
		"       ff           bb\r"
		"       ff           bb\r"
		"       ff           bb\r" 
		"       ff           bb\r" 
		"      ff           bb\r" 
		"      ff           bb\r" 
		"      ff           bb\r" 
		"      ff           bb\r" 
		"         gggggggg\r" 
		"     ee  gggggggg cc\r" 
		"     ee           cc\r" 
		"     ee           cc\r" 
		"     ee           cc\r" 
		"     ee           cc\r" 
		"    ee           cc\r" 
		"    ee           cc\r" 
		"    ee           cc\r" 
		"    ee           cc\r" 
		"    ee ddddddddd cc\r" 
		"ii     ddddddddd      hh\r"
	    "ii                    hh";
	
	static void sym1_draw_7segment(struct mame_bitmap *bitmap,int value, int x, int y)
	{
		int i, xi, yi, mask, color;
	
		for (i=0, xi=0, yi=0; led[i]; i++) {
			mask=0;
			switch (led[i]) {
			case 'a': mask=1; break;
			case 'b': mask=2; break;
			case 'c': mask=4; break;
			case 'd': mask=8; break;
			case 'e': mask=0x10; break;
			case 'f': mask=0x20; break;
			case 'g': mask=0x40; break;
			case 'h': mask=0x80; break;
			}
			
			if (mask!=0) {
				color=Machine.pens[(value&mask)?1:0];
				plot_pixel(bitmap, x+xi, y+yi, color);
				osd_mark_dirty(x+xi,y+yi,x+xi,y+yi);
			}
			if (led[i]!='\r') xi++;
			else { yi++, xi=0; }
		}
	}
	
	static const struct {
		int x,y;
	} sym1_led_pos[8]={
		{594,262},
		{624,262},
		{653,262},
		{682,262},
		{711,262},
		{741,262},
		{80,228},
		{360,32}
	};
	
	static const char* single_led=
	" 111\r"
	"11111\r"
	"11111\r"
	"11111\r"
	" 111"
	;
	
	static void sym1_draw_led(struct mame_bitmap *bitmap,INT16 color, int x, int y)
	{
		int j, xi=0;
		for (j=0; single_led[j]; j++) {
			switch (single_led[j]) {
			case '1': 
				plot_pixel(bitmap, x+xi, y, color);
				osd_mark_dirty(x+xi,y,x+xi,y);
				xi++;
				break;
			case ' ': 
				xi++;
				break;
			case '\r':
				xi=0;
				y++;
				break;				
			};
		}
	}
	
	void sym1_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh)
	{
		int i;
	
	    if (full_refresh != 0)
	    {
	        osd_mark_dirty (0, 0, bitmap.width, bitmap.height);
	    }
		for (i=0; i<6; i++) {
			sym1_draw_7segment(bitmap, sym1_led[i], sym1_led_pos[i].x, sym1_led_pos[i].y);
	//		sym1_draw_7segment(bitmap, sym1_led[i], sym1_led_pos[i].x-160, sym1_led_pos[i].y-120);
			sym1_led[i]=0;
		}
	
		sym1_draw_led(bitmap, Machine.pens[1], 
					 sym1_led_pos[6].x, sym1_led_pos[6].y);
		sym1_draw_led(bitmap, Machine.pens[1], 
					 sym1_led_pos[7].x, sym1_led_pos[7].y);
	}
	
}
