/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class comquest
{
	
	
	static struct {
		UINT8 data[128][8];
		void *timer;
		int line;
		int dma_activ;
		int state;
		int count;
	} comquest_video={ { {0} } };
	
	public static VhStartPtr comquest_vh_start = new VhStartPtr() { public int handler() 
	{
		(void) comquest_video;
	    return 0;
	} };
	
	public static VhStopPtr comquest_vh_stop = new VhStopPtr() { public void handler() 
	{
	} };
	
	void comquest_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh)
	{
		int x, y, j;
	
		for (y=0; y<128;y++) {
			for (x=0, j=0; j<8;j++,x+=8*4) {
	#if 0
				drawgfx(bitmap, Machine.gfx[0], studio2_video.data[y][j],0,
						0,0,x,y,
						0, TRANSPARENCY_NONE,0);
	#endif
			}
		}
	}
}
