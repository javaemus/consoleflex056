#ifndef IWM_H
#define IWM_H

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package machine;

public class iwmH
{
	
	typedef struct iwm_interface
	{
		void (*set_lines)(int lines);
		void (*set_enable_lines)(int enable_mask);
	
		int (*read_data)(void);
		void (*write_data)(int data);
		int (*read_status)(void);
	} iwm_interface;
	
	void iwm_init(const iwm_interface *intf);
	int iwm_r(int offset);
	void iwm_w(int offset, int data);
	
	/* mask for IWM lines */
	enum {
		IWM_PH0		= 0x01,
		IWM_PH1		= 0x02,
		IWM_PH2		= 0x04,
		IWM_PH3		= 0x08,
		IWM_MOTOR	= 0x10,	/* private, do not use ! */
		IWM_DRIVE	= 0x20,	/* private, do not use ! */
		IWM_Q6		= 0x40,	/* private, do not use ! */
		IWM_Q7		= 0x80	/* private, do not use ! */
	};
	
	
	#endif /* IWM_H */
}
