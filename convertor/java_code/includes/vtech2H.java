/* from mame.c */
//extern int bitmap_dirty;

/* from machine/laser350.c */
extern int laser_latch;

extern extern extern extern extern 
extern int laser_rom_init(int id);
extern void laser_rom_exit(int id);

extern int laser_floppy_init(int id);
extern void laser_floppy_exit(int id);

extern int laser_cassette_init(int id);
extern void laser_cassette_exit(int id);

extern READ_HANDLER ( laser_fdc_r );
extern WRITE_HANDLER ( laser_fdc_w );
extern WRITE_HANDLER ( laser_bank_select_w );

/* from vidhrdw/laser350.c */

extern char laser_frame_message[64+1];
extern int laser_frame_time;


extern extern extern void laser_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);
extern WRITE_HANDLER ( laser_bg_mode_w );
extern WRITE_HANDLER ( laser_two_color_w );

#ifdef RUNTIME_LOADER
# ifdef __cplusplus
	extern "C" # else
	extern # endif
#endif
