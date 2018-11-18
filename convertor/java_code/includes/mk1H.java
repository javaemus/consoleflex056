#ifdef RUNTIME_LOADER
# ifdef __cplusplus
	extern "C" # else
	extern # endif
#endif

extern UINT8 mk1_led[4];
extern unsigned short mk1_colortable[1][2];
extern unsigned char mk1_palette[242][3];

void mk1_init_colors (UBytePtr sys_palette,
						  unsigned short *sys_colortable,
					  const UBytePtr color_prom);

void mk1_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh);
