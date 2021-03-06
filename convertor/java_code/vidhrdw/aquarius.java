/***************************************************************************

  aquarius.c

  Functions to emulate the video hardware of the aquarius.

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class aquarius
{
	
	public static VhStartPtr aquarius_vh_start = new VhStartPtr() { public int handler() 
	{
	
		if( generic_vh_start() )
			return 1;
	    return 0;
	} };
	
	public static VhStopPtr aquarius_vh_stop = new VhStopPtr() { public void handler() 
	{
		generic_vh_stop();
	} };
	
	void aquarius_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh)
	{
	
		int	sy, sx;
	
		if (full_refresh != 0)
			memset (dirtybuffer, 1, videoram_size);
	
		for (sy = 0; sy < 24; sy++)
		{
			for (sx = 0; sx < 40; sx++)
			{
				if (dirtybuffer[sy * 40 + sx + 40] || dirtybuffer[sy * 40 + sx + 0x400])
				{
					drawgfx (bitmap, Machine.gfx[0], videoram.read(sy * 40 + sx + 40),
							videoram.read(sy * 40 + sx + 0x400), 0, 0, sx * 8,
							sy * 8, &Machine.visible_area, TRANSPARENCY_NONE, 0);
					dirtybuffer[sy * 40 + sx + 40] = 0;
					dirtybuffer[sy * 40 + sx + 0x400] = 0;
				}
			}
		}
	}
	
}
