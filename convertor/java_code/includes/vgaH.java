
/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class vgaH
{
	
	
	#if 0
	//   include in memory read list
	   { 0xa0000, 0xaffff, MRA_BANK1 }
	   { 0xb0000, 0xb7fff, MRA_BANK2 }
	   { 0xb8000, 0xbffff, MRA_BANK3 }
	   { 0xc0000, 0xc7fff, MRA_ROM }
	
	//   and in memory write list
	   { 0xa0000, 0xaffff, MWA_BANK1 }
	   { 0xb0000, 0xb7fff, MWA_BANK2 }
	   { 0xb8000, 0xbffff, MWA_BANK3 }
	   { 0xc0000, 0xc7fff, MWA_ROM }
	
		/* oti 037 chip */
	    ROM_LOAD("oakvga.bin", 0xc0000, 0x8000, 0x318c5f43);
		/* tseng labs famous et4000 isa vga card (oem) */
	    ROM_LOAD("et4000b.bin", 0xc0000, 0x8000, 0xa903540d);
		/* tseng labs famous et4000 isa vga card */
	    ROM_LOAD("et4000.bin", 0xc0000, 0x8000, 0xf01e4be0);
	#endif
	
	extern unsigned char vga_palette[0x100][3];
	extern unsigned char ega_palette[0x40][3];
	extern unsigned short vga_colortable[];
	extern struct GfxLayout vga_charlayout;
	extern struct GfxDecodeInfo vga_gfxdecodeinfo[];
	void ega_init_palette(UBytePtr sys_palette, unsigned short *sys_colortable,const UBytePtr color_prom);
	void vga_init_palette(UBytePtr sys_palette, unsigned short *sys_colortable,const UBytePtr color_prom);
	
	void vga_init(mem_read_handler read_dipswitch);
	
	
	// include in port access list
	
	
	
	
	
	
	void ega_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);
	void vga_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh);
	
	/*
	  pega notes (paradise)
	  build in amstrad pc1640
	
	  ROM_LOAD("40100", 0xc0000, 0x8000, 0xd2d1f1ae);
	
	  4 additional dipswitches
	  seems to have emulation modes at register level
	  (mda/hgc lines bit 8 not identical to ega/vga)
	
	  standard ega/vga dipswitches
	  00000000	320x200
	  00000001	640x200 hanging
	  00000010	640x200 hanging
	  00000011	640x200 hanging
	
	  00000100	640x350 hanging
	  00000101	640x350 hanging EGA mono
	  00000110	320x200
	  00000111	640x200
	
	  00001000	640x200
	  00001001	640x200
	  00001010	720x350 partial visible
	  00001011	720x350 partial visible
	
	  00001100	320x200
	  00001101	320x200
	  00001110	320x200
	  00001111	320x200
	
	*/
	
	/*
	  oak vga (oti 037 chip)
	  (below bios patch needed for running)
	
	  ROM_LOAD("oakvga.bin", 0xc0000, 0x8000, 0x318c5f43);
	*/
	#if 0
	        int i; 
	        UINT8 *memory=memory_region(REGION_CPU1)+0xc0000;
	        UINT8 chksum;
	
			/* oak vga */
	        /* plausibility check of retrace signals goes wrong */
	        memory[0x00f5]=memory[0x00f6]=memory[0x00f7]=0x90;
	        memory[0x00f8]=memory[0x00f9]=memory[0x00fa]=0x90;
	        for (chksum=0, i=0;i<0x7fff;i++) {
	                chksum+=memory[i];
	        }
	        memory[i]=0x100-chksum;
	#endif
}
