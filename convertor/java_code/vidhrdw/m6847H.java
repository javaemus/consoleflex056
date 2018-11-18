/* m6847.h -- Implementation of Motorola 6847 video hardware chip */

#ifndef _M6847_H
#define _M6847_H

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class m6847H
{
	
	#ifdef __cplusplus
	extern "C" {
	#endif
	
	/******************* Initialization & Functionality *******************/
	
	#define M6847_ARTIFACT_COLOR_COUNT	14
	
	#define M6847_TOTAL_COLORS (13 + (M6847_ARTIFACT_COLOR_COUNT * 4))
	
	#define M6847_INTERRUPTS_PER_FRAME	263
	
	enum {
		M6847_VERSION_ORIGINAL_PAL,
		M6847_VERSION_ORIGINAL_NTSC,
		M6847_VERSION_M6847Y_PAL,
		M6847_VERSION_M6847Y_NTSC,
		M6847_VERSION_M6847T1_PAL,
		M6847_VERSION_M6847T1_NTSC
	};
	
	struct m6847_init_params {
		int version;				/* use one of the above initialization constants */
		int artifactdipswitch;		/* dip switch that controls artifacting; -1 if NA */
		UINT8 *ram;					/* the base of RAM */
		int ramsize;				/* the size of accessible RAM */
		void (*charproc)(UINT8 c);	/* the proc that gives the host a chance to change mode bits */
	
		mem_write_handler hs_func;	/* Horizontal sync */
		mem_write_handler fs_func;	/* Field sync */
		double callback_delay;		/* Amount of time to wait before invoking callbacks (this is a CoCo related hack */
	};
	
	/* This call fills out the params structure with defaults; this is so I can
	 * change around the structure without breaking people's code */
	void m6847_vh_normalparams(struct m6847_init_params *params);
	
	int m6847_vh_start(const struct m6847_init_params *params);
	void m6847_vh_update(struct mame_bitmap *bitmap,int full_refresh);
	int m6847_is_t1(int version);
	
	
	/******************* Modifiers *******************/
	
	enum {
		M6847_BORDERCOLOR_BLACK,
		M6847_BORDERCOLOR_GREEN,
		M6847_BORDERCOLOR_WHITE,
		M6847_BORDERCOLOR_ORANGE
	};
	
	
	/* This allows the size of accessable RAM to be resized */
	void m6847_set_ram_size(int ramsize);
	
	/* This is the base of video memory, within the ram specified by m6847_vh_start() */
	void m6847_set_video_offset(int offset);
	
	/* Touches VRAM; offset is from vram base position */
	void m6847_touch_vram(int offset);
	
	/* Changes the height of each row */
	void m6847_set_row_height(int rowheight);
	
	/* Call this function at vblank; so fs and hs will be properly set */
	
	/******************* 1-bit mode port interfaces *******************/
	
	
	
	#ifdef __cplusplus
	}
	#endif
	
	#endif /* _M6847_H */
	
}
