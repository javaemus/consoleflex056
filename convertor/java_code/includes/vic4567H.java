#ifndef __VIC4567_H_
#define __VIC4567_H_

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package includes;

public class vic4567H
{
	
	extern unsigned char vic3_palette[0x100 * 3];
	
	extern void vic4567_init (int pal, int (*dma_read) (int),
							  int (*dma_read_color) (int), void (*irq) (int),
							  void (*param_port_changed)(int));
	
	
	/* to be called when writting to port */
	extern WRITE_HANDLER ( vic3_port_w );
	
	/* to be called when reading from port */
	extern READ_HANDLER ( vic3_port_r );
	
	
	#endif
	
}
