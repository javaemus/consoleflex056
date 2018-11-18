/*
  fairchild f3853 static ram interface smi
  with integrated interrupt controller and timer

  timer shift register basically the same as in f3851!
*/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package cpu.f8;

public class f3853H
{
	
	typedef struct {
	    int frequency;
	    void (*interrupt_request)(UINT16 addr, bool level);
	} F3853_CONFIG;
	
	void f3853_init(F3853_CONFIG *config);
	
	// ports 0x0c - 0x0f
	READ_HANDLER(f3853_r);
	WRITE_HANDLER(f3853_w);
	
	void f3853_set_external_interrupt_in_line(bool level);
	void f3853_set_priority_in_line(bool level);
	
}
