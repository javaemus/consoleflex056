/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package includes;

public class pc_floppH
{
	
	#ifdef __cplusplus
	extern "C" {
	#endif
	
	int pc_floppy_init(int id);
	void pc_floppy_exit(int id);
	
	#ifdef __cplusplus
	}
	#endif
	
}
