
extern UBytePtr astrocade_videoram;

int astrocade_load_rom(int id);

READ_HANDLER ( astrocade_intercept_r );
WRITE_HANDLER ( astrocade_videoram_w );
WRITE_HANDLER ( astrocade_magic_expand_color_w );
WRITE_HANDLER ( astrocade_magic_control_w );
WRITE_HANDLER ( astrocade_magicram_w );

void astrocade_vh_screenrefresh(struct mame_bitmap *bitmap,int full_refresh);
READ_HANDLER ( astrocade_video_retrace_r );
WRITE_HANDLER ( astrocade_vertical_blank_w );
WRITE_HANDLER ( astrocade_interrupt_enable_w );
WRITE_HANDLER ( astrocade_interrupt_w );

WRITE_HANDLER ( astrocade_mode_w );


WRITE_HANDLER ( astrocade_colour_register_w );
WRITE_HANDLER ( astrocade_colour_block_w );
WRITE_HANDLER ( astrocade_colour_split_w );

void astrocade_copy_line(int line);
