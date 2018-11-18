/***************************************************************************

  nc.c

  Functions to emulate the video hardware of the Amstrad PCW.

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class nc
{
	
	/***************************************************************************
	  Start the video hardware emulation.
	***************************************************************************/
	
	public static VhStartPtr nc_vh_start = new VhStartPtr() { public int handler() 
	{
	
		return 0;
	} };
	
	public static VhStopPtr nc_vh_stop = new VhStopPtr() { public void handler() 
	{
	} };
	
	/* two colours */
	static unsigned short nc_colour_table[NC_NUM_COLOURS] =
	{
		0, 1,2,3
	};
	
	/* black/white */
	static unsigned char nc_palette[NC_NUM_COLOURS * 3] =
	{
		0x080, 0x0a0, 0x060,
	    0x000, 0x000, 0x000,
		0x060, 0x060, 0x060,
		0x000, 0x000, 0x000
	};
	
	
	/* Initialise the palette */
	void nc_init_palette(UBytePtr sys_palette, unsigned short *sys_colortable, const UBytePtr color_prom)
	{
	        memcpy(sys_palette, nc_palette, sizeof (nc_palette));
	        memcpy(sys_colortable, nc_colour_table, sizeof (nc_colour_table));
	}
	
	extern int nc_display_memory_start;
	extern char *nc_memory;
	extern UINT8 nc_type;
	
	static int nc200_backlight = 0;
	
	void nc200_video_set_backlight(int state)
	{
		nc200_backlight = state;
	}
	
	
	/***************************************************************************
	  Draw the game screen in the given mame_bitmap.
	  Do NOT call osd_update_display() from this function,
	  it will be called by the main emulation engine.
	***************************************************************************/
	void nc_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh)
	{
		int y;
		int b;
		int x;
		int height, width;
		int pens[2];
	
	
	
	    if (nc_type==NC_TYPE_200)
	    {
	        height = NC200_SCREEN_HEIGHT;
	        width = NC200_SCREEN_WIDTH;
	
			if (nc200_backlight != 0)
			{
				pens[0] = Machine.pens[2];
				pens[1] = Machine.pens[3];
			}
			else
			{
				pens[0] = Machine.pens[0];
				pens[1] = Machine.pens[1];
			}
	    }
	    else
	    {
			height = NC_SCREEN_HEIGHT;
			width = NC_SCREEN_WIDTH;
			pens[0] = Machine.pens[2];
			pens[1] = Machine.pens[3];	
		}
	
	
	    for (y=0; y<height; y++)
	    {
			int by;
			/* 64 bytes per line */
			char *line_ptr = nc_memory + nc_display_memory_start + (y<<6);
	
			x = 0;
			for (by=0; by<width>>3; by++)
			{
				int px;
				unsigned char byte;
	
				byte = line_ptr[0];
	
				px = x;
				for (b=0; b<8; b++)
				{
					plot_pixel(bitmap, px, y, pens[(byte>>7) & 0x01]);
	
					byte = byte<<1;
					
					px++;
				}
	
				x = px;
								
				line_ptr = line_ptr+1;
			}
		}
	}
	
}
