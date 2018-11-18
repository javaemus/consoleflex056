
/* in vidhrdw/channelf.c */
extern int channelf_val_reg;
extern int channelf_row_reg;
extern int channelf_col_reg;

void channelf_init_palette(UBytePtr sys_palette,
                           unsigned short *sys_colortable,
                           const UBytePtr color_prom);
void channelf_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);

/* in sndhrdw/channelf.c */
void channelf_sound_w(int);

int channelf_sh_custom_start(const struct MachineSound* driver);

