extern unsigned short pcjr_colortable[256*2+16*2+2*4+1*16];
extern struct GfxDecodeInfo t1t_gfxdecodeinfo[];
void pcjr_init_palette(UBytePtr sys_palette, unsigned short *sys_colortable,const UBytePtr color_prom);


extern extern extern extern void pc_t1t_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);
extern WRITE_HANDLER ( pc_t1t_videoram_w );
extern READ_HANDLER ( pc_t1t_videoram_r );
extern WRITE_HANDLER ( pc_T1T_w );
extern READ_HANDLER (	pc_T1T_r );

extern 
#if 0
extern void pc_t1t_blink_textcolors(int on);
extern void pc_t1t_index_w(int data);
extern extern void pc_t1t_port_w(int data);
extern extern void pc_t1t_mode_control_w(int data);
extern extern void pc_t1t_color_select_w(int data);
extern extern void pc_t1t_vga_index_w(int data);
extern extern void pc_t1t_lightpen_strobe_w(int data);
extern void pc_t1t_vga_data_w(int data);
extern extern void pc_t1t_bank_w(int data);
extern #endif
