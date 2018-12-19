/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess.includes;

public class lynxH
{
	
	//extern UINT32 lynx_palette[0x10];
	
	//void lynx_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh);
	//void lynx_draw_lines(int newline);
	
	
	//extern UINT32 lynx_partialcrc(const UBytePtr ,unsigned int);
	
	//extern int debug_pos;
	//extern char debug_strings[16][30];
	
	
	public static int PAD_UP    = 0x80;
	public static int PAD_DOWN  = 0x40;
	public static int PAD_LEFT  = 0x20;
	public static int PAD_RIGHT = 0x10;
	
	//extern 
	//extern UINT16 lynx_granularity;
	//extern int lynx_rotate;
	
	public static class MIKEY {
            public int[] data= new int[0x100];
	};
	
        public static MIKEY mikey=new MIKEY();
        
	//WRITE_HANDLER(mikey_write);
	//READ_HANDLER(mikey_read);
	//WRITE_HANDLER(suzy_write);
	//READ_HANDLER(suzy_read);
	//void lynx_timer_count_down(int nr);
	
	//#ifdef RUNTIME_LOADER
	//# ifdef __cplusplus
	//extern "C" # else
	//extern # endif
	//#endif
	
	//void lynx_audio_debug(struct mame_bitmap *bitmap);
	//void lynx_audio_write(int offset, UINT8 data);
	//UINT8 lynx_audio_read(int offset);
	//void lynx_audio_count_down(int nr);
	//extern int lynx_custom_start (const struct MachineSound *driver);
	//extern int lynx2_custom_start (const struct MachineSound *driver);
	//extern extern #endif
	
	/*TODO*/////#ifdef INCLUDE_LYNX_LINE_FUNCTION
	/*TODO*/////	int j, xi, wi, i;
	/*TODO*/////	int b, p, color;
	/*TODO*/////
	/*TODO*/////	i=blitter.mem[blitter.bitmap];
	/*TODO*/////	blitter.memory_accesses++;
	/*TODO*/////	for (xi=blitter.x, p=0, b=0, j=1, wi=0; (j<i);) {
	/*TODO*/////	    if (p<bits) {
	/*TODO*/////		b=(b<<8)|blitter.mem[blitter.bitmap+j];
	/*TODO*/////		j++;
	/*TODO*/////		p+=8;
	/*TODO*/////		blitter.memory_accesses++;
	/*TODO*/////	    }
	/*TODO*/////	    for (;(p>=bits);) {
	/*TODO*/////		color=blitter.color[(b>>(p-bits))&mask]; p-=bits;
	/*TODO*/////		for (;(wi<blitter.width);wi+=0x100, xi+=xdir) {
	/*TODO*/////		    if ((xi>=0)&&(xi<160)) {
	/*TODO*/////			lynx_plot_pixel(blitter.mode, xi, y, color);
	/*TODO*/////		    }
	/*TODO*/////		}
	/*TODO*/////		wi-=blitter.width;
	/*TODO*/////	    }
	/*TODO*/////	}
	/*TODO*/////#endif
	
	/*TODO*/////#ifdef INCLUDE_LYNX_LINE_RLE_FUNCTION
	
	/*TODO*/////	int wi, xi;
	/*TODO*/////	int b, p, j;
	/*TODO*/////	int t, count, color;
	
	/*TODO*/////	for( p=0, j=0, b=0, xi=blitter.x, wi=0; ; ) { // through the rle entries
	/*TODO*/////	    if (p<5+bits) { // under 7 bits no complete entry
	/*TODO*/////		j++;
	/*TODO*/////		if (j>=blitter.mem[blitter.bitmap]) return;
	/*TODO*/////		p+=8;
	/*TODO*/////		b=(b<<8)|blitter.mem[blitter.bitmap+j];
	/*TODO*/////		blitter.memory_accesses++;
	/*TODO*/////	    }
	/*TODO*/////	    t=(b>>(p-1))&1;p--;
	/*TODO*/////	    count=((b>>(p-4))&0xf)+1;p-=4;
	/*TODO*/////	    if (t != 0) { // count of different pixels
	/*TODO*/////		for (;count; count--) {
	/*TODO*/////		    if (p<bits) {
	/*TODO*/////			j++;
	/*TODO*/////			if (j>=blitter.mem[blitter.bitmap]) return;
	/*TODO*/////			p+=8;
	/*TODO*/////			b=(b<<8)|blitter.mem[blitter.bitmap+j];
	/*TODO*/////			blitter.memory_accesses++;
	/*TODO*/////		    }
	/*TODO*/////		    color=blitter.color[(b>>(p-bits))&mask];p-=bits;
	/*TODO*/////		    for (;(wi<blitter.width);wi+=0x100, xi+=xdir) {
	/*TODO*/////			if ((xi>=0)&&(xi<160)) {
	/*TODO*/////			    lynx_plot_pixel(blitter.mode, xi, y, color);
	/*TODO*/////			}
	/*TODO*/////		    }
	/*TODO*/////		    wi-=blitter.width;
	/*TODO*/////		}
	/*TODO*/////	    } else { // count of same pixels
	/*TODO*/////		if (count==0) return;
	/*TODO*/////		if (p<bits) {
	/*TODO*/////		    j++;
	/*TODO*/////		    if (j>=blitter.mem[blitter.bitmap]) return;
	/*TODO*/////		    p+=8;
	/*TODO*/////		    b=(b<<8)|blitter.mem[blitter.bitmap+j];
	/*TODO*/////		    blitter.memory_accesses++;
	/*TODO*/////		}
	/*TODO*/////		color=blitter.color[(b>>(p-bits))&mask];p-=bits;
	/*TODO*/////		for (;count; count--) {
	/*TODO*/////		    for (;(wi<blitter.width);wi+=0x100, xi+=xdir) {
	/*TODO*/////			if ((xi>=0)&&(xi<160)) {
	/*TODO*/////			    lynx_plot_pixel(blitter.mode, xi, y, color);
	/*TODO*/////			}
	/*TODO*/////		    }
	/*TODO*/////		    wi-=blitter.width;
	/*TODO*/////		}
	/*TODO*/////	    }
	/*TODO*/////	}
	
	/*TODO*/////#endif
}
