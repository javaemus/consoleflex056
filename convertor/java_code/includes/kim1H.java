/* from src/mess/machine/kim1.c */
extern extern 
extern int kim1_cassette_init(int id);
extern void kim1_cassette_exit(int id);

extern 
extern READ_HANDLER ( m6530_003_r );
extern READ_HANDLER ( m6530_002_r );
extern READ_HANDLER ( kim1_mirror_r );

extern WRITE_HANDLER ( m6530_003_w );
extern WRITE_HANDLER ( m6530_002_w );
extern WRITE_HANDLER ( kim1_mirror_w );

/* from src/mess/vidhrdw/kim1.c */
extern extern extern extern void kim1_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh);

