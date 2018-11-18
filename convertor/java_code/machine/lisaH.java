void lisa_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);

int lisa_floppy_init(int id);
void lisa_floppy_exit(int id);
extern 



READ_HANDLER ( lisa_fdc_io_r );
WRITE_HANDLER ( lisa_fdc_io_w );
READ_HANDLER ( lisa_fdc_r );
READ_HANDLER ( lisa210_fdc_r );
WRITE_HANDLER ( lisa_fdc_w );
WRITE_HANDLER ( lisa210_fdc_w );
READ16_HANDLER ( lisa_r );
WRITE16_HANDLER ( lisa_w );


