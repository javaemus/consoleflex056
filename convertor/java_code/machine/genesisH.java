extern int genesis_sharedram_size;
extern int genesis_soundram_size;
extern unsigned char genesis_sharedram[];
extern UBytePtr  genesis_soundram;

int genesis_init_cart (int id);

WRITE16_HANDLER ( genesis_io_w );
READ16_HANDLER  ( genesis_io_r );
READ16_HANDLER  ( genesis_ctrl_r );
WRITE16_HANDLER ( genesis_ctrl_w );

/*READ_HANDLER  ( cartridge_ram_r );
WRITE_HANDLER ( cartridge_ram_w );*/

/* #define SINGLE_BYTE_ACCESS(d) (((d & 0xffff0000) == 0xff000000) ||
			       ((d & 0xffff0000) == 0x00ff0000))				  */

#ifdef LSB_FIRST
#define ACTUAL_BYTE_ADDRESS(a) ((a) ^ 1)
#else
#define ACTUAL_BYTE_ADDRESS(a) ((a))
#endif

/* machine/genesis.c */
UINT32 genesis_partialcrc(const UBytePtr ,unsigned int);

void genesis_background_w(int offset,int data);

extern int z80running;

/* sndhrdw/genesis.c */


WRITE16_HANDLER ( YM2612_68000_w );
READ16_HANDLER  ( YM2612_68000_r );
