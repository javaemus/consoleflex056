/* machine/galaxy.c */

extern extern extern void galaxy_exit_snap (int id);
extern int galaxy_load_snap (int id);
extern int galaxy_init_wav(int);
extern void galaxy_exit_wav(int);
extern extern extern int galaxy_interrupts_enabled;

/* vidhrdw/galaxy.c */

extern extern extern void galaxy_vh_screenrefresh (struct mame_bitmap *bitmap,
												int full_refresh);
extern extern UBytePtr galaxy_charram;
extern size_t galaxy_charram_size;

/* systems/galaxy.c */

extern struct GfxLayout galaxy_charlayout;
												
