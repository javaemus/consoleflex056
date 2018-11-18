/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package includes;

public class exidyH
{
	#define EXIDY_NUM_COLOURS 2
	
	/* 64 chars wide, 30 chars tall */
	#define EXIDY_SCREEN_WIDTH        (64*8)
	#define EXIDY_SCREEN_HEIGHT       (30*8)
	
	void exidy_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);
	void exidy_init_palette(UBytePtr sys_palette, unsigned short *sys_colortable, const UBytePtr color_prom);
	
	int exidy_cassette_init(int id);
	void exidy_cassette_exit(int id);
}
