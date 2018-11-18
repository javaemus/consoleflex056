/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class pc_cgaH
{
	
	#if 0
		// cutted from some aga char rom
		// 256 8x8 thick chars
		// 256 8x8 thin chars
	    ROM_LOAD("cga.chr",     0x00000, 0x01000, 0x42009069);
		// first font of above
	    ROM_LOAD("cga2.chr", 0x00000, 0x800, 0xa362ffe6);
	#endif
	
	
	extern unsigned char cga_palette[16][3];
	extern unsigned short cga_colortable[256*2+16*2+2*4];
	extern struct GfxLayout CGA_charlayout;
	extern struct GfxLayout CGA_gfxlayout_1bpp;
	extern struct GfxLayout CGA_gfxlayout_2bpp;
	extern struct GfxDecodeInfo CGA_gfxdecodeinfo[];
	void pc_cga_init_palette(UBytePtr sys_palette, unsigned short *sys_colortable,const UBytePtr color_prom);
	
	extern void pc_cga_init_video(struct _CRTC6845 *crtc);
	
	extern extern extern void pc_cga_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);
	
	// call this with 240 times pre second
	extern 
	extern WRITE_HANDLER ( pc_cga_videoram_w );
	extern WRITE_HANDLER ( pc_CGA_w );
	extern READ_HANDLER ( pc_CGA_r );
	
	#if 0
	extern void pc_cga_blink_textcolors(int on);
	extern void pc_cga_mode_control_w(int data);
	extern void pc_cga_color_select_w(int data);
	extern #endif
	
	// has a special 640x200 in 16 color mode, 4 banks at 0xb8000
	extern extern extern void pc1512_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);
	
	extern WRITE_HANDLER ( pc1512_w );
	extern READ_HANDLER ( pc1512_r );
	extern WRITE_HANDLER ( pc1512_videoram_w );
	
	//internal use
	void pc_cga_cursor(CRTC6845_CURSOR *cursor);
}
