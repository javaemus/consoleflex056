/******************************************************************************
 PeT mess@utanet.at 2000
******************************************************************************/
/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class studio2
{
	
	
	/*
	  emulation of the rcata10171v1 (cdp1861) video controller
	 */
	
	
	static struct {
		UINT8 data[128][8];
		void *timer;
		int line;
		int dma_activ;
		int state;
		int count;
	} studio2_video={ { {0} } };
	
	public static VhStartPtr studio2_vh_start = new VhStartPtr() { public int handler() 
	{
	    return 0;
	} };
	
	public static VhStopPtr studio2_vh_stop = new VhStopPtr() { public void handler() 
	{
	} };
	
	int studio2_get_vsync(void)
	{
		return !studio2_video.dma_activ;
	}
	
	#define COLUMNS 14
	#define LINES 256
	void studio2_video_dma(int cycles)
	{
		int i;
	
		switch (studio2_video.state) {
		case 0: // deactivated
			break;
		case 1:
			studio2_video.count=COLUMNS-cycles;
			studio2_video.state++;
			cpu_set_irq_line(0, CDP1802_IRQ, PULSE_LINE);
			studio2_video.dma_activ=1;
			break;
		case 2:
			studio2_video.count-=cycles;
			if (studio2_video.count<0) {
				studio2_video.line=0;
				studio2_video.state=10;
				studio2_video.count+=COLUMNS;
			}
			break;
		case 10:
			studio2_video.count-=cycles;
			if (studio2_video.count<0) {
				for (i=0; i<8; i++) 
					studio2_video.data[studio2_video.line][i]=cdp1802_dma_read();
				studio2_video.count+=COLUMNS-8;
				if (++studio2_video.line>=128) {
					studio2_video.dma_activ=0;
					// turn off irq line !?
					studio2_video.count+=2*COLUMNS;
					studio2_video.state++;
				}
			}
			break;
		case 11:
			studio2_video.count-=cycles;
			if (studio2_video.count<0) {
	// while dma_activ is high Register0 is corrected for doublescanning
	// after is it waiting for it going high again
				studio2_video.dma_activ=1; 
				studio2_video.count+=COLUMNS;
				studio2_video.state++;
				if (++studio2_video.line>=256) studio2_video.state=1;
			}
			break;
		case 12:
			studio2_video.count-=cycles;
			if (studio2_video.count<0) {
				studio2_video.dma_activ=0; 
				studio2_video.count+=COLUMNS;
				studio2_video.state=11;
				if (++studio2_video.line>=256) studio2_video.state=1;
			}
			break;
		}
	}
	
	void studio2_video_start(void)
	{
		studio2_video.state=1;
	}
	
	void studio2_video_stop(void)
	{
		studio2_video.state=0;
	}
	
	void studio2_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh)
	{
		int x, y, j;
	
		for (y=0; y<128;y++) {
			for (x=0, j=0; j<8;j++,x+=8*4)
				drawgfx(bitmap, Machine.gfx[0], studio2_video.data[y][j],0,
						0,0,x,y,
						0, TRANSPARENCY_NONE,0);
		}
	}
}
