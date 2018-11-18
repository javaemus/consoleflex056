#ifndef __INTV_H
#define __INTV_H

/* in vidhrdw/intv.c */
void intv_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);

void intvkbd_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);

/* in machine/intv.c */

/*  for the console alone... */
extern UINT8 intv_gram[];
extern UINT8 intv_gramdirty[];
extern UINT16 intv_ram16[];

int intv_load_rom (int id);


READ16_HANDLER( intv_gram_r );
WRITE16_HANDLER( intv_gram_w );
READ16_HANDLER( intv_empty_r );
READ16_HANDLER( intv_ram8_r );
WRITE16_HANDLER( intv_ram8_w );
READ16_HANDLER( intv_ram16_r );
WRITE16_HANDLER( intv_ram16_w );



/* for the console + keyboard component... */
extern int intvkbd_text_blanked;

int intvkbd_load_rom (int id);

READ16_HANDLER ( intvkbd_dualport16_r );
WRITE16_HANDLER ( intvkbd_dualport16_w );
READ_HANDLER ( intvkbd_dualport8_lsb_r );
WRITE_HANDLER ( intvkbd_dualport8_lsb_w );
READ_HANDLER ( intvkbd_dualport8_msb_r );
WRITE_HANDLER ( intvkbd_dualport8_msb_w );

READ_HANDLER ( intvkbd_tms9927_r );
WRITE_HANDLER ( intvkbd_tms9927_w );

/* in sndhrdw/intv.c */
READ16_HANDLER( AY8914_directread_port_0_lsb_r );
WRITE16_HANDLER( AY8914_directwrite_port_0_lsb_w );

#endif

