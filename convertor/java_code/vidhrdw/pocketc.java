/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class pocketc
{
	
	
	/* PC126x
	   24x2 5x7 space between char
	   2000 .. 203b, 2800 .. 283b
	   2040 .. 207b, 2840 .. 287b
	  203d: 0 BUSY, 1 PRINT, 3 JAPAN, 4 SMALL, 5 SHIFT, 6 DEF
	  207c: 1 DEF 1 RAD 2 GRAD 5 ERROR 6 FLAG */
	
	unsigned char pocketc_palette[248][3] =
	{
		{ 99,107,99 },
	
		{ 94,111,103 },
	
		{ 255,255,255 },
		{ 255,255,255 },
	
		{ 60, 66, 60 },
	
	
		{ 0, 0, 0 }
	};
	
	unsigned short pocketc_colortable[8][2] = {
		{ 0, 4 },
		{ 0, 4 },
		{ 0, 4 },
		{ 0, 4 },
		{ 1, 5 },
		{ 1, 5 },
		{ 1, 5 },
		{ 1, 5 }
	};
	
	void pocketc_init_colors (UBytePtr sys_palette,
							  unsigned short *sys_colortable,
							  const UBytePtr color_prom)
	{
		memcpy (sys_palette, pocketc_palette, sizeof (pocketc_palette));
		memcpy(sys_colortable,pocketc_colortable,sizeof(pocketc_colortable));
	}
	
	
	public static VhStartPtr pocketc_vh_start = new VhStartPtr() { public int handler() 
	{
	    videoram_size[0] = 6 * 2 + 24;
	    videoram = (UINT8*)auto_malloc (videoram_size[0]);
		if (!videoram)
	        return 1;
	
		{
			char backdrop_name[200];
		    /* try to load a backdrop for the machine */
			sprintf(backdrop_name, "%s.png", Machine.gamedrv.name);
			backdrop_load(backdrop_name, 8);
		}
	
		return generic_vh_start();
	} };
	
	public static VhStopPtr pocketc_vh_stop = new VhStopPtr() { public void handler() 
	{
		generic_vh_stop();
	} };
	
	void pocketc_draw_special(struct mame_bitmap *bitmap,
							  int x, int y, const POCKETC_FIGURE fig, int color)
	{
		int i,j;
		for (i=0;fig[i];i++,y++) {
			for (j=0;fig[i][j]!=0;j++) {
				switch(fig[i][j]) {
				case '1':
					plot_pixel(bitmap, x+j, y, color);
					osd_mark_dirty(x+j,y,x+j,y);
					break;
				case 'e': return;
				}
			}
		}
	}
	
}
