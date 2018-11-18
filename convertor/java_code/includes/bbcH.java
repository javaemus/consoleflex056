/******************************************************************************
    BBC Model B

    MESS Driver By:

	Gordon Jefferyes
	mess_bbc@gjeffery.dircon.co.uk

******************************************************************************/

extern int startbank;

WRITE_HANDLER ( page_selecta_w );
WRITE_HANDLER ( page_selectb_w );

WRITE_HANDLER ( memory_w );

READ_HANDLER ( BBC_NOP_00_r );
READ_HANDLER ( BBC_NOP_FE_r );
READ_HANDLER ( BBC_NOP_FF_r );


WRITE_HANDLER ( page_selectbp_w );

WRITE_HANDLER ( memorybp0_w );

READ_HANDLER ( memorybp1_r );
WRITE_HANDLER ( memorybp1_w );
WRITE_HANDLER ( memorybp3_w );

WRITE_HANDLER ( memorybp3_128_w );
WRITE_HANDLER ( memorybp4_128_w );

int bbcb_load_rom(int id);







int bbc_floppy_init(int);

void bbc_floppy_exit(int);

READ_HANDLER ( bbc_wd1770_read );
WRITE_HANDLER ( bbc_wd1770_write );





void bbc_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);


WRITE_HANDLER ( videoULA_w );

void setscreenstart(int b4,int b5);
void bbcbp_setvideoshadow(int vdusel);

WRITE_HANDLER ( BBC_6845_w );
READ_HANDLER ( BBC_6845_r );

extern unsigned char vidmem[0x10000];

