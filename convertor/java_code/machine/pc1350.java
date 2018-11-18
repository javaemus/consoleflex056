/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package machine;

public class pc1350
{
	
	
	static UINT8 outa,outb;
	
	static int power=1; /* simulates pressed cce when mess is started */
	
	void pc1350_outa(int data)
	{
		outa=data;
	}
	
	void pc1350_outb(int data)
	{
		outb=data;
	}
	
	void pc1350_outc(int data)
	{
	
	}
	
	int pc1350_ina(void)
	{
		int data=outa;
		int t=pc1350_keyboard_line_r();
		if ((t & 1) != 0) {
			if (PC1350_KEY_BRACE_CLOSE != 0) data|=1;
			if (PC1350_KEY_COLON != 0) data|=2;
			if (PC1350_KEY_SEMICOLON != 0) data|=4;
			if (PC1350_KEY_COMMA != 0) data|=8;
			if (PC1350_KEY_SML != 0) data|=0x10;
			if (PC1350_KEY_DEF != 0) data|=0x20;
			// not sure if both shifts are wired to the same contacts
			if (PC1350_KEY_LSHIFT||PC1350_KEY_RSHIFT) data|=0x40;
		}
		if ((t & 2) != 0) {
			if (PC1350_KEY_BRACE_OPEN != 0) data|=1;
			if (PC1350_KEY_SLASH != 0) data|=2;
			if (PC1350_KEY_ASTERIX != 0) data|=4;
			if (PC1350_KEY_MINUS != 0) data|=8;
			if (PC1350_KEY_Z != 0) data|=0x10;
			if (PC1350_KEY_A != 0) data|=0x20;
			if (PC1350_KEY_Q != 0) data|=0x40;
		}
		if ((t & 4) != 0) {
			if (PC1350_KEY_9 != 0) data|=1;
			if (PC1350_KEY_6 != 0) data|=2;
			if (PC1350_KEY_3 != 0) data|=4;
			if (PC1350_KEY_PLUS != 0) data|=8;
			if (PC1350_KEY_X != 0) data|=0x10;
			if (PC1350_KEY_S != 0) data|=0x20;
			if (PC1350_KEY_W != 0) data|=0x40;
		}
		if ((t & 8) != 0) {
			if (PC1350_KEY_8 != 0) data|=1;
			if (PC1350_KEY_5 != 0) data|=2;
			if (PC1350_KEY_2 != 0) data|=4;
			if (PC1350_KEY_POINT != 0) data|=8;
			if (PC1350_KEY_C != 0) data|=0x10;
			if (PC1350_KEY_D != 0) data|=0x20;
			if (PC1350_KEY_E != 0) data|=0x40;
		}
		if ((t & 0x10) != 0) {
			if (PC1350_KEY_7 != 0) data|=1;
			if (PC1350_KEY_4 != 0) data|=2;
			if (PC1350_KEY_1 != 0) data|=4;
			if (PC1350_KEY_0 != 0) data|=8;
			if (PC1350_KEY_V != 0) data|=0x10;
			if (PC1350_KEY_F != 0) data|=0x20;
			if (PC1350_KEY_R != 0) data|=0x40;
		}
		if ((t & 0x20) != 0) {
			if (PC1350_KEY_UP != 0) data|=1;
			if (PC1350_KEY_DOWN != 0) data|=2;
			if (PC1350_KEY_LEFT != 0) data|=4; 
			if (PC1350_KEY_RIGHT != 0) data|=8;
			if (PC1350_KEY_B != 0) data|=0x10;
			if (PC1350_KEY_G != 0) data|=0x20;
			if (PC1350_KEY_T != 0) data|=0x40;
		}
		if ((outa & 1) != 0) {
	//		if (PC1350_KEY_1 != 0) data|=2; //?
			if (PC1350_KEY_INS != 0) data|=4;
			if (PC1350_KEY_DEL != 0) data|=8;
			if (PC1350_KEY_N != 0) data|=0x10;
			if (PC1350_KEY_H != 0) data|=0x20;
			if (PC1350_KEY_Y != 0) data|=0x40;
		}
		if ((outa & 2) != 0) {
	//		if (PC1350_KEY_2 != 0) data|=4; //?
			if (PC1350_KEY_MODE != 0) data|=8;
			if (PC1350_KEY_M != 0) data|=0x10;
			if (PC1350_KEY_J != 0) data|=0x20;
			if (PC1350_KEY_U != 0) data|=0x40;
		}
		if ((outa & 4) != 0) {
			if (power||PC1350_KEY_CLS) data|=8;
			if (PC1350_KEY_SPACE != 0) data|=0x10;
			if (PC1350_KEY_K != 0) data|=0x20;
			if (PC1350_KEY_I != 0) data|=0x40;
		}
		if ((outa & 8) != 0) {
			if (PC1350_KEY_ENTER != 0) data|=0x10;
			if (PC1350_KEY_L != 0) data|=0x20;
			if (PC1350_KEY_O != 0) data|=0x40;
		}
		if ((outa & 0x10) != 0) {
			if (PC1350_KEY_EQUALS != 0) data|=0x20;
			if (PC1350_KEY_P != 0) data|=0x40;
		}
		if (PC1350_KEY_OFF&&(outa&0xc0) ) data|=0xc0;
	
		// missing lshift
		
		return data;
	}
	
	int pc1350_inb(void)
	{
		int data=outb;
		return data;
	}
	
	bool pc1350_brk(void)
	{
		return PC1350_KEY_BRK;
	}
	
	/* currently enough to save the external ram */
	static void pc1350_load(void)
	{
		FILE *file;
		UINT8 *ram=memory_region(REGION_CPU1)+0x2000,
			*cpu=sc61860_internal_ram();
	
		if ( (file=(FILE*)osd_fopen(Machine.gamedrv.name,
							 Machine.gamedrv.name, OSD_FILETYPE_NVRAM, 0))==NULL) {
			power=0;
			return;
		}
	
		osd_fread(file, cpu, 96);
		osd_fread(file, ram, 0x5000);
		osd_fclose(file);
	}
	
	static void pc1350_save(void)
	{
		FILE *file;
		UINT8 *ram=memory_region(REGION_CPU1)+0x2000,
			*cpu=sc61860_internal_ram();
	
		if ( (file=(FILE*)osd_fopen(Machine.gamedrv.name,
							 Machine.gamedrv.name, OSD_FILETYPE_NVRAM, 1))==NULL)
			return;
	
		osd_fwrite(file, cpu, 96);
		osd_fwrite(file, ram, 0x5000);
		osd_fclose(file);
	}
	
	static void pc1350_power_up(int param)
	{
		power=0;
	}
	
	public static InitDriverPtr init_pc1350 = new InitDriverPtr() { public void handler() 
	{
		int i;
		UINT8 *gfx=memory_region(REGION_GFX1);
		for (i=0; i<256; i++) gfx[i]=i;
	
		pc1350_load();
		timer_pulse(1/500.0, 0,sc61860_2ms_tick);
		timer_set(1,0,pc1350_power_up);
	} };
	
	public static InitMachinePtr pc1350_machine_init = new InitMachinePtr() { public void handler() 
	{
		if (PC1350_RAM20K != 0) {
			install_mem_write_handler (0, 0x2000, 0x5fff, MWA_RAM);
		} else if (PC1350_RAM12K != 0) {
			install_mem_write_handler (0, 0x2000, 0x3fff, MWA_NOP);
			install_mem_write_handler (0, 0x4000, 0x5fff, MWA_RAM);
		} else {
			install_mem_write_handler (0, 0x2000, 0x5fff, MWA_NOP);
		}
	} };
	
	void pc1350_machine_stop(void)
	{
		pc1350_save();
	}
}
