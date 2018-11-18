/* vidhrdw/a7800.c */
extern extern extern void a7800_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);
extern extern READ_HANDLER  ( a7800_MARIA_r);
extern WRITE_HANDLER ( a7800_MARIA_w );


/* machine/a7800.c */
extern UBytePtr a7800_ram;
extern UBytePtr a7800_cartridge_rom;
extern extern extern UINT32 a7800_partialcrc(const UBytePtr ,unsigned int);
extern int a7800_init_cart (int id);
extern void a7800_exit_rom (int id);
extern READ_HANDLER  ( a7800_TIA_r );
extern WRITE_HANDLER ( a7800_TIA_w );
extern READ_HANDLER  ( a7800_RIOT_r );
extern WRITE_HANDLER ( a7800_RIOT_w );
extern READ_HANDLER  ( a7800_MAINRAM_r );
extern WRITE_HANDLER ( a7800_MAINRAM_w );
extern READ_HANDLER  ( a7800_RAM0_r );
extern WRITE_HANDLER ( a7800_RAM0_w );
extern READ_HANDLER  ( a7800_RAM1_r );
extern WRITE_HANDLER ( a7800_RAM1_w );
extern WRITE_HANDLER ( a7800_cart_w );

#ifdef RUNTIME_LOADER
# ifdef __cplusplus
	extern "C" # else
	extern # endif
#endif
