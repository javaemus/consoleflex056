#ifndef _NESINTF_H_
#define _NESINTF_H_

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package sound;

public class nesintfH
{
	
	#define MAX_NESPSG 2
	
	struct NESinterface {
		int num;		/* total number of chips in the machine */
		int basefreq;
		int volume[MAX_NESPSG];
		int cpunum[MAX_NESPSG];
		mem_write_handler apu_callback_w[MAX_NESPSG];
		mem_read_handler apu_callback_r[MAX_NESPSG];
	};
	
	extern extern extern 
	extern READ_HANDLER(NESPSG_0_r);
	extern READ_HANDLER(NESPSG_1_r);
	extern WRITE_HANDLER(NESPSG_0_w);
	extern WRITE_HANDLER(NESPSG_1_w);
	
	
	#endif /* !_NESINTF_H_ */
}
