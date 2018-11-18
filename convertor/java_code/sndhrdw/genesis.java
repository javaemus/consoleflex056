/*
 *
 *	  Sound emulation hooks for Genesis
 *
 *   ***********************************
 *   ***    C h a n g e   L i s t    ***
 *   ***********************************
 *   Date       Name   Description
 *   ----       ----   -----------
 *   00-Jan-00  GSL    Started
 *	 03-Aug-98	GSL	   Tidied.. at last!
 *
 */
///*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package sndhrdw;

public class genesis
{
	////
	public static InterruptPtr genesis_s_interrupt = new InterruptPtr() { public int handler() 
	{
		// if (errorlog != 0) fprintf(errorlog, "Z80 interrupt ");
		return 0xff;
	
	} };
	
	WRITE16_HANDLER ( YM2612_68000_w )
	{
		switch (offset)
		{
			case 0:
				if (ACCESSING_LSB != 0) YM2612_data_port_0_A_w(offset, data 	   & 0xff);
				if (ACCESSING_MSB != 0) YM2612_control_port_0_A_w(offset, (data >> 8) & 0xff);
				break;
			case 1:
				if (ACCESSING_LSB != 0) YM2612_data_port_0_B_w(offset, data 		& 0xff);
				if (ACCESSING_MSB != 0) YM2612_control_port_0_B_w(offset, (data >> 8) & 0xff);
		}
	}
	
	READ16_HANDLER ( YM2612_68000_r )
	{
		switch (offset)
		{
			case 0:
				return ((YM2612_status_port_0_A_r(offset) << 8) + YM2612_status_port_0_B_r(offset) );
				break;
			case 1:
				return (YM2612_read_port_0_r(offset) << 8);
				break;
		}
		return 0;
	}
}
