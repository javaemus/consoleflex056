#ifdef RUNTIME_LOADER
# ifdef __cplusplus
	extern "C" # else
	extern # endif
#endif


/* machine/odyssey2.c */
extern int odyssey2_framestart;
extern int odyssey2_videobank;

extern extern int odyssey2_load_rom (int id);


/* vidhrdw/odyssey2.c */
extern int odyssey2_vh_hpos;

extern UINT8 odyssey2_colors[24][3];

extern extern void odyssey2_vh_init_palette(UBytePtr game_palette, unsigned short *game_colortable,const UBytePtr color_prom);
extern extern void odyssey2_vh_write(int data);
extern void odyssey2_vh_update(int data);
extern void odyssey2_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);
extern READ_HANDLER ( odyssey2_t1_r );

extern READ_HANDLER ( odyssey2_video_r );
extern WRITE_HANDLER ( odyssey2_video_w );




/* i/o ports */
extern READ_HANDLER ( odyssey2_bus_r );
extern WRITE_HANDLER ( odyssey2_bus_w );

extern extern WRITE_HANDLER ( odyssey2_putp1 );

extern extern WRITE_HANDLER ( odyssey2_putp2 );

extern extern WRITE_HANDLER ( odyssey2_putbus );
