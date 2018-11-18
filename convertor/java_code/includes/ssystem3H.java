#ifdef RUNTIME_LOADER
# ifdef __cplusplus
	extern "C" # else
	extern # endif
#endif



extern UINT8 ssystem3_led[5];
extern unsigned short ssystem3_colortable[1][2];
extern unsigned char ssystem3_palette[242][3];

void ssystem3_init_colors (UBytePtr sys_palette,
					  unsigned short *sys_colortable,
					  const UBytePtr color_prom);

void ssystem3_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh);
