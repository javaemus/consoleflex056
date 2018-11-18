/* from machine/vtech1.c */

#define OLD_VIDEO

extern char vtech1_frame_message[64+1];
extern int vtech1_frame_time;

extern int vtech1_latch;

extern extern extern extern extern 
extern int vtech1_cassette_init(int id);
extern void vtech1_cassette_exit(int id);

extern int vtech1_snapshot_init(int id);
extern void vtech1_snapshot_exit(int id);

extern int vtech1_floppy_init(int id);
extern void vtech1_floppy_exit(int id);

extern READ_HANDLER ( vtech1_fdc_r );
extern WRITE_HANDLER ( vtech1_fdc_w );

extern READ_HANDLER ( vtech1_joystick_r );
extern READ_HANDLER ( vtech1_keyboard_r );
extern WRITE_HANDLER ( vtech1_latch_w );

extern #ifdef OLD_VIDEO
/* from vidhrdw/vtech1.c */
extern extern extern void vtech1_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);
#else
extern 
#endif

#ifdef RUNTIME_LOADER
# ifdef __cplusplus
	extern "C" # else
	extern # endif
#endif
