/***************************************************************************

  cgenie.c

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package sndhrdw;

public class cgenie
{
	
	
	static int control_port;
	
	public static ReadHandlerPtr cgenie_sh_control_port_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return control_port;
	} };
	
	public static ReadHandlerPtr cgenie_sh_data_port_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return AY8910_read_port_0_r(offset);
	} };
	
	public static WriteHandlerPtr cgenie_sh_control_port_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		control_port = data;
		AY8910_control_port_0_w(offset, data);
	} };
	
	public static WriteHandlerPtr cgenie_sh_data_port_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		AY8910_write_port_0_w(offset, data);
	} };
	
}
