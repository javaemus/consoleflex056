#ifdef RUNTIME_LOADER
# ifdef __cplusplus
	extern "C" # else
	extern # endif
#endif


/* in vidhrdw/pocketc.c */

extern unsigned char pocketc_palette[248][3];
extern unsigned short pocketc_colortable[8][2];


void pocketc_init_colors (UBytePtr sys_palette,
						  unsigned short *sys_colortable,
						  const UBytePtr color_prom);


typedef char *POCKETC_FIGURE[];
void pocketc_draw_special(struct mame_bitmap *bitmap,
						  int x, int y, const POCKETC_FIGURE fig, int color);
