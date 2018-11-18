/***************************************************************************

  machine.c

  Functions to emulate general aspects of the machine (RAM, ROM, interrupts,
  I/O ports)

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package machine;

public class enterp
{
	
	extern UBytePtr Enterprise_RAM;
	
	
	public static InitMachinePtr enterprise_init_machine = new InitMachinePtr() { public void handler() 
	{
		/* allocate memory. */
		/* I am allocating it because I control how the ram is
		 * accessed. Mame will not allocate any memory because all
		 * the memory regions have been defined as MRA_BANK?
		*/
		/* 128k memory, 32k for dummy read/write operations
		 * where memory bank is not defined
		 */
		Enterprise_RAM = malloc((128*1024)+32768);
		if (!Enterprise_RAM) return;
	
		/* initialise the hardware */
		Enterprise_Initialise();
	} };
	
	void enterprise_shutdown_machine(void)
	{
		wd179x_exit();
	
		if (Enterprise_RAM != NULL)
			free(Enterprise_RAM);
	
		Enterprise_RAM = NULL;
	
		Dave_Finish();
	}
	
	int enterprise_floppy_init(int id)
	{
		if (device_filename(IO_FLOPPY, id)==NULL)
			return INIT_PASS;
	
		if (strlen(device_filename(IO_FLOPPY, id))==0)
			return INIT_PASS;
	
		if (basicdsk_floppy_init(id)==INIT_PASS)
		{
			basicdsk_set_geometry(id, 80, 2, 9, 512, 1, 0);
			return INIT_PASS;
		}
	
		return INIT_FAIL;
	}
}
