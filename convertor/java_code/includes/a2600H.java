/* vidhrdw/a2600.c */
extern extern extern extern void a2600_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);


/* machine/a2600.c */
extern READ_HANDLER  ( a2600_TIA_r );
extern WRITE_HANDLER ( a2600_TIA_w );
extern READ_HANDLER  ( a2600_riot_r );
extern WRITE_HANDLER ( a2600_riot_w );
extern READ_HANDLER  ( a2600_bs_r );

extern extern extern int  a2600_load_rom(int id);
extern READ_HANDLER ( a2600_ROM_r );

#ifdef RUNTIME_LOADER
# ifdef __cplusplus
	extern "C" # else
	extern # endif
#endif


