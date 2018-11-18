#ifdef RUNTIME_LOADER
# ifdef __cplusplus
	extern "C" # else
	extern # endif
#endif



extern UINT8 mk2_led[5];
extern unsigned short mk2_colortable[1][2];
extern unsigned char mk2_palette[242][3];

void mk2_init_colors (UBytePtr sys_palette,
					  unsigned short *sys_colortable,
					  const UBytePtr color_prom);

void mk2_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh);
