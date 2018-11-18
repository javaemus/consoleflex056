#ifndef _VIDHRDW_PDP1
#define _VIDHRDW_PDP1 1

#define VIDEO_BITMAP_WIDTH  512
#define VIDEO_BITMAP_HEIGHT 512

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class pdp1H
{
	
	void pdp1_vh_update (struct mame_bitmap *bitmap, int full_refresh);
	
	extern int fio_dec;
	extern int concise;
	extern int case_state;
	extern int reader_buffer;
	
	#endif
}
