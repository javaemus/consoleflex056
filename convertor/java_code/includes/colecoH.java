/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class colecoH
{
	
	/* machine/coleco.c */
	extern int coleco_init_cart (int id);
	
	extern READ_HANDLER  ( coleco_paddle_r );
	extern WRITE_HANDLER ( coleco_paddle_toggle_off );
	extern WRITE_HANDLER ( coleco_paddle_toggle_on );
	
}
