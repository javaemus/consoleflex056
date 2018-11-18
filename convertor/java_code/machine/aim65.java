/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package machine;

public class aim65
{
	
	
	static UINT8 pia_a, pia_b;
	/*
	  ef7b output char a at position x
	
	 */
	
	static void aim65_pia(void)
	{
		if ( !(pia_a&0x80) ) {
			if (!(pia_a&4)) dl1416a_write(0, pia_a&3, pia_b&0x7f, !(pia_b&0x80) );
			if (!(pia_a&8)) dl1416a_write(1, pia_a&3, pia_b&0x7f, !(pia_b&0x80) );
			if (!(pia_a&0x10)) dl1416a_write(2, pia_a&3, pia_b&0x7f, !(pia_b&0x80) );
			if (!(pia_a&0x20)) dl1416a_write(3, pia_a&3, pia_b&0x7f, !(pia_b&0x80) );
			if (!(pia_a&0x40)) dl1416a_write(4, pia_a&3, pia_b&0x7f, !(pia_b&0x80) );
		}
	}
	
	public static WriteHandlerPtr aim65_pia_a_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		pia_a=data;
		aim65_pia();
	} };
	
	public static WriteHandlerPtr aim65_pia_b_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		pia_b=data;
		aim65_pia();
	} };
	
	static struct pia6821_interface pia= {
		0,//mem_read_handler in_a_func,
		0,//mem_read_handler in_b_func,
		0,//mem_read_handler in_ca1_func,
		0,//mem_read_handler in_cb1_func,
		0,//mem_read_handler in_ca2_func,
		0,//mem_read_handler in_cb2_func,
		aim65_pia_a_w,
		aim65_pia_b_w,
		0,//mem_write_handler out_ca2_func,
		0,//mem_write_handler out_cb2_func,
		0,//void (*irq_a_func)(int state),
		0,//void (*irq_b_func)(int state),
	};
	
	static int aim65_riot_b_r(int chip)
	{
		int data=0xff, a=riot_0_a_r(0);
	
		if (!(a&1)) {
			if (KEY_SPACE != 0) data&=~1;
			//right?
			if (KEY_POINT != 0) data&=~4;
			if (KEY_M != 0) data&=~8;
			if (KEY_B != 0) data&=~0x10;
			if (KEY_C != 0) data&=~0x20;
			if (KEY_Z != 0) data&=~0x40;
			//right?
		}
		if (!(a&2)) {
			if (KEY_DEL != 0) data&=~1; //backspace 0x08
			if (KEY_LF != 0) data&=~2; //0x60
			if (KEY_L != 0) data&=~4;
			if (KEY_J != 0) data&=~8;
			if (KEY_G != 0) data&=~0x10;
			if (KEY_D != 0) data&=~0x20;
			if (KEY_A != 0) data&=~0x40;
			//right?
		}
		if (!(a&4)) {
			//right?
			if (KEY_PRINT != 0) data&=~2; // backslash 
			if (KEY_P != 0) data&=~4;
			if (KEY_I != 0) data&=~8;
			if (KEY_Y != 0) data&=~0x10;
			if (KEY_R != 0) data&=~0x20;
			if (KEY_W != 0) data&=~0x40;
			if (KEY_ESC != 0) data&=~0x80; //0x1b
		}
		if (!(a&8)) {
			if (KEY_RETURN != 0) data&=~1;
			//right?
			if (KEY_MINUS != 0) data&=~4;
			if (KEY_O != 0) data&=~8;
			if (KEY_U != 0) data&=~0x10;
			if (KEY_T != 0) data&=~0x20;
			if (KEY_E != 0) data&=~0x40;
			if (KEY_Q != 0) data&=~0x80;
		}
		if (!(a&0x10)) {
			if (KEY_CRTL != 0) data&=~1;
			//right?
			if (KEY_COLON != 0) data&=~4;
			if (KEY_9 != 0) data&=~8;
			if (KEY_7 != 0) data&=~0x10;
			if (KEY_5 != 0) data&=~0x20;
			if (KEY_3 != 0) data&=~0x40;
			if (KEY_1 != 0) data&=~0x80;
		}
		if (!(a&0x20)) {
			if (KEY_LEFT_SHIFT != 0) data&=~1;
			//right?
			if (KEY_0 != 0) data&=~4;
			if (KEY_8 != 0) data&=~8;
			if (KEY_6 != 0) data&=~0x10;
			if (KEY_4 != 0) data&=~0x20;
			if (KEY_2 != 0) data&=~0x40;
			if (KEY_F3 != 0) data&=~0x80; //^ 0x5e
		}
		if (!(a&0x40)) {
			if (KEY_RIGHT_SHIFT != 0) data&=~1;
			// del 0x7f
			if (KEY_SEMICOLON != 0) data&=~4;
			if (KEY_K != 0) data&=~8;
			if (KEY_H != 0) data&=~0x10;
			if (KEY_F != 0) data&=~0x20;
			if (KEY_S != 0) data&=~0x40;
			if (KEY_F2 != 0) data&=~0x80;
		}
		if (!(a&0x80)) {
			// nothing ?
			//right?
			if (KEY_SLASH != 0) data&=~4;
			if (KEY_COMMA != 0) data&=~8;
			if (KEY_N != 0) data&=~0x10;
			if (KEY_V != 0) data&=~0x20;
			if (KEY_X != 0) data&=~0x40;
			if (KEY_F1 != 0) data&=~0x80;
		}
		return data;
	}
	
	static RIOT_CONFIG riot={
		1000000,
		{ 0, 0 },
		{ aim65_riot_b_r, 0 },
		0
	};
	
	/*
	  aim65 thermal printer (20 characters)
	  10 heat elements (place on 1 line, space between 2 characters(about 14dots))
	  (pa0..pa7,pb0,pb1 1 heat element on)
	
	  cb2 0 motor, heat elements on
	  cb1 output start!? 
	  ca1 input
	
	  normally printer 5x7 characters 
	  (horizontal movement limits not known, normally 2 dots between characters)
	
	  3 dots space between lines?
	 */
	struct {
		void *timer;
		int level;
	} printer={ 0 };
	
	void aim65_printer_timer(int param)
	{
		// hack until printer? is emulated
		via_0_cb1_w(0,printer.level);
		via_0_ca1_w(0,printer.level);
		printer.level^=1;
	}
	
	WRITE_HANDLER(aim65_printer_on)
	{
		if (!data) {
			if (!printer.timer) printer.timer=timer_pulse(1000e-6, 0, aim65_printer_timer);
		} else {
			if (printer.timer) timer_remove(printer.timer);
			printer.timer=0;
		}
	}
	
	public static ReadHandlerPtr aim65_via0_b_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return readinputport(4);
	} };
	
	static struct via6522_interface via0={
		0,//mem_read_handler in_a_func;
		aim65_via0_b_r,//mem_read_handler in_b_func;
		0,//mem_read_handler in_ca1_func;
		0,//mem_read_handler in_cb1_func;
		0,//mem_read_handler in_ca2_func;
		0,//mem_read_handler in_cb2_func;
		0,//mem_write_handler out_a_func;
		0,//mem_write_handler out_b_func;
		0,//mem_write_handler out_ca2_func;
		aim65_printer_on,//mem_write_handler out_cb2_func;
		0,//void (*irq_func)(int state);
	};
	
	
	public static InitDriverPtr init_aim65 = new InitDriverPtr() { public void handler() 
	{
		pia_config(0, PIA_STANDARD_ORDERING, &pia);
		riot_config(0, &riot);
		via_config(0,&via0);
	} };
	
	public static InitMachinePtr aim65_machine_init = new InitMachinePtr() { public void handler() 
	{
	} };
	
	int aim65_frame_int(void)
	{
		return ignore_interrupt();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
