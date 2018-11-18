/* From machine/vectrex.c */
extern UBytePtr vectrex_ram;
extern READ_HANDLER  ( vectrex_mirrorram_r );
extern WRITE_HANDLER ( vectrex_mirrorram_w );
extern int vectrex_init_cart (int id);

/* From machine/vectrex.c */
extern int vectrex_refresh_with_T2;
extern int vectrex_imager_status;
extern UINT32 vectrex_beam_color;
extern unsigned char vectrex_via_out[2];

extern void vectrex_imager_left_eye (double time_);
extern extern READ_HANDLER (v_via_pa_r);
extern READ_HANDLER(v_via_pb_r );
extern void v_via_irq (int level);


/* From vidhrdw/vectrex.c */
extern extern extern void vectrex_vh_update (struct mame_bitmap *bitmap, int full_refresh);

extern extern WRITE_HANDLER  ( raaspec_led_w );
extern extern void raaspec_vh_update (struct mame_bitmap *bitmap, int full_refresh);

/* from vidhrdw/vectrex.c */
extern void vector_add_point_stereo (int x, int y, int color, int intensity);
extern void (*vector_add_point_function) (int, int, int, int);
extern 
#ifdef RUNTIME_LOADER
# ifdef __cplusplus
	extern "C" # else
	extern # endif
#endif
