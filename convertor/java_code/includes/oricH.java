/* from machine/oric.c */

READ_HANDLER ( oric_IO_r );
WRITE_HANDLER ( oric_IO_w );

/* from vidhrdw/oric.c */
void oric_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);

extern WRITE_HANDLER(oric_psg_porta_write);

int	oric_floppy_init(int id);
void oric_floppy_exit(int id);


extern int oric_cassette_init(int id);
extern void oric_cassette_exit(int id);

/* Telestrat specific */

READ_HANDLER ( telestrat_IO_r );
WRITE_HANDLER ( telestrat_IO_w );

