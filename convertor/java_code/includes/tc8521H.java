/* TOSHIBA TC8521 REAL TIME CLOCK */

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package includes;

public class tc8521H
{
	
	READ_HANDLER(tc8521_r);
	WRITE_HANDLER(tc8521_w);
	
	struct tc8521_interface
	{
		/* output of alarm */
		void (*alarm_output_callback)(int);
	};
	
	void tc8521_init(struct tc8521_interface *);
	
	void	tc8521_load_stream(void *file);
	void	tc8521_save_stream(void *file);
}
