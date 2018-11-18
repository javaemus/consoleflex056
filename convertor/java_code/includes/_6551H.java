/* 6551 ACIA */

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class _6551H
{
	
	WRITE_HANDLER(acia_6551_w);
	READ_HANDLER(acia_6551_r);
	
	void	acia_6551_set_irq_callback(void (*callback)(int));
	
	void	acia_6551_connect_to_serial_device(int id);
	
}
