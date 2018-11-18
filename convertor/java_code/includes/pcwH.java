/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package includes;

public class pcwH
{
	#define PCW_BORDER_HEIGHT 8
	#define PCW_BORDER_WIDTH 8
	#define PCW_NUM_COLOURS 2
	#define PCW_DISPLAY_WIDTH 720
	#define PCW_DISPLAY_HEIGHT 256
	
	#define PCW_SCREEN_WIDTH	(PCW_DISPLAY_WIDTH + (PCW_BORDER_WIDTH<<1))
	#define PCW_SCREEN_HEIGHT	(PCW_DISPLAY_HEIGHT  + (PCW_BORDER_HEIGHT<<1))
	
	void pcw_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);
	void pcw_init_palette(UBytePtr sys_palette, unsigned short *sys_colortable, const UBytePtr color_prom);
	void pcw_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);
}
