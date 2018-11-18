/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class vc4000H
{
	
	#ifdef RUNTIME_LOADER
	# ifdef __cplusplus
		extern "C" # else
		extern # endif
	#endif
	
	// define this for debugging sessions
	//#define DEBUG
	
	// define this to use digital inputs instead of the slow
	// autocentering analog mame joys
	#define ANALOG_HACK
	
	READ_HANDLER(vc4000_vsync_r);
	
	READ_HANDLER(vc4000_video_r);
	WRITE_HANDLER(vc4000_video_w);
	
	
	// space vultures sprites above
	// combat below and invisible
	#define YPOS 8
	#define YBOTTOM_SIZE 40
	// grand slam sprites left and right
	// space vultures left
	// space attack left
	#define XPOS 48
	extern extern extern void vc4000_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh);
	
	extern struct CustomSound_interface vc4000_sound_interface;
	extern int vc4000_custom_start (const struct MachineSound *driver);
	extern extern extern void vc4000_soundport_w (int mode, int data);
}
