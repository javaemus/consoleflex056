/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package machine;

public class at
{
	
	#ifndef RUNTIME_LOADER
	// support for old i86 core, until new one is in mame
	#else
	# include "cpu/i86/i286intf.h"
	# ifdef HAS_I386
	# include "cpu/i86/i386intf.h"
	# endif
	#endif
	
	
	static DMA8237_CONFIG dma= { DMA8237_AT };
	static SOUNDBLASTER_CONFIG soundblaster = { 1,5, {1,0} };
	
	static PIT8253_CONFIG at_pit8253_config={
		TYPE8254,
		{
			{
				4770000/4,				/* heartbeat IRQ */
				pic8259_0_issue_irq,
				NULL
			}, {
				4770000/4,				/* dram refresh */
				NULL,
				NULL
			}, {
				4770000/4,				/* pio port c pin 4, and speaker polling enough */
				NULL,
				pc_sh_speaker_change_clock
			}
		}
	};
	
	public static InitDriverPtr init_atcga = new InitDriverPtr() { public void handler() 
	{
		AT8042_CONFIG at8042={
			AT8042_STANDARD, i286_set_address_mask
		};
		pc_init_setup(pc_setup_at);
		init_pc_common();
		dma8237_config(dma8237,&dma);
		dma8237_config(dma8237+1,&dma);
		pit8253_config(0,&at_pit8253_config);
		pc_cga_init();
		mc146818_init(MC146818_STANDARD);
		/* initialise keyboard */
		at_keyboard_init();
		at_keyboard_set_scan_code_set(1);
		at_keyboard_set_input_port_base(4);
		at_keyboard_set_type(AT_KEYBOARD_TYPE_AT);
	
		soundblaster_config(&soundblaster);
		at_8042_init(&at8042);
	} };
	
	#ifdef HAS_I386
	public static InitDriverPtr init_at386 = new InitDriverPtr() { public void handler() 
	{
		AT8042_CONFIG at8042={
			AT8042_AT386, i386_set_address_mask
		};
		init_atcga();
		at_8042_init(&at8042);
	} };
	#endif
	
	public static InitDriverPtr init_at_vga = new InitDriverPtr() { public void handler() 
	{
		AT8042_CONFIG at8042={
			AT8042_STANDARD, i286_set_address_mask
		};
		pc_init_setup(pc_setup_at);
		init_pc_common();
		dma8237_config(dma8237,&dma);
		dma8237_config(dma8237+1,&dma);
		pit8253_config(0,&at_pit8253_config);
	
		pc_vga_init();
		mc146818_init(MC146818_STANDARD);
		/* initialise keyboard */
		at_keyboard_init();
		at_keyboard_set_scan_code_set(1);
		at_keyboard_set_input_port_base(4);
		at_keyboard_set_type(AT_KEYBOARD_TYPE_AT);
	
		vga_init(input_port_0_r);
		soundblaster_config(&soundblaster);
		at_8042_init(&at8042);
	} };
	
	public static InitDriverPtr init_ps2m30286 = new InitDriverPtr() { public void handler() 
	{
		AT8042_CONFIG at8042={
			AT8042_PS2, i286_set_address_mask
		};
		init_at_vga();
		at_8042_init(&at8042);
	} };
	
	public static InitMachinePtr at_machine_init = new InitMachinePtr() { public void handler() 
	{
		dma8237_reset(dma8237);
		dma8237_reset(dma8237+1);
	} };
	
	public static InitMachinePtr at_vga_init_machine = new InitMachinePtr() { public void handler() 
	{
		vga_reset();
		dma8237_reset(dma8237);
		dma8237_reset(dma8237+1);
	} };
	
	public static InterruptPtr at_cga_frame_interrupt = new InterruptPtr() { public int handler() 
	{
		static int turboswitch=-1;
	
		if (turboswitch !=(input_port_3_r(0)&2)) {
			if (input_port_3_r(0)&2)
				timer_set_overclock(0, 1);
			else
				timer_set_overclock(0, 4.77/12);
			turboswitch=input_port_3_r(0)&2;
		}
	
		pc_cga_timer();
	
	    if( !onscrd_active() && !setup_active() ) {
			at_keyboard_polling();
			at_8042_time();
		}
	
	    return ignore_interrupt ();
	} };
	
	public static InterruptPtr at_vga_frame_interrupt = new InterruptPtr() { public int handler() 
	{
		static int turboswitch=-1;
	
		if (turboswitch !=(input_port_3_r(0)&2)) {
			if (input_port_3_r(0)&2)
				timer_set_overclock(0, 1);
			else
				timer_set_overclock(0, 4.77/12);
			turboswitch=input_port_3_r(0)&2;
		}
	
	//	vga_timer();
	
	    if( !onscrd_active() && !setup_active() ) {
			at_keyboard_polling();
			at_8042_time();
		}
	
	    return ignore_interrupt ();
	} };
}
