/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package machine;

public class pc1403
{
	
	
	/* C-CE while reset, program will not be destroyed! */
	
	static UINT8 outa;
	UINT8 pc1403_portc;
	
	static int power=1; /* simulates pressed cce when mess is started */
	
	
	/* 
	   port 2:
	     bits 0,1: external rom a14,a15 lines
	   port 3: 
	     bits 0..6 keyboard output select matrix line
	*/
	static UINT8 asic[4];
	
	WRITE_HANDLER(pc1403_asic_write)
	{
	    asic[offset>>9]=data;
	    switch( (offset>>9) ){
	    case 0/*0x3800*/:
		// output
		logerror ("asic write %.4x %.2x\n",offset, data);
		break;
	    case 1/*0x3a00*/:
		logerror ("asic write %.4x %.2x\n",offset, data);
		break;
	    case 2/*0x3c00*/:
		cpu_setbank(1, memory_region(REGION_USER1)+((data&7)<<14));
		logerror ("asic write %.4x %.2x\n",offset, data);
		break;
	    case 3/*0x3e00*/: break;
	    }
	}
	
	READ_HANDLER(pc1403_asic_read)
	{
	    UINT8 data=asic[offset>>9];
	    switch( (offset>>9) ){
	    case 0: case 1: case 2:
		logerror ("asic read %.4x %.2x\n",offset, data);
		break;
	    }
	    return data;
	}
	
	void pc1403_outa(int data)
	{
	    outa=data;
	}
	
	int pc1403_ina(void)
	{
	    UINT8 data=outa;
	
	    if (asic[3]&1) {
		if (KEY_7 != 0) data|=1;
		if (KEY_8 != 0) data|=2;
		if (KEY_9 != 0) data|=4;
		if (KEY_DIV != 0) data|=8;
		if (KEY_XM != 0) data|=0x10;
		// 0x20
		// 0x40
		// 0x80
	    }
	    if (asic[3]&2) {
		if (KEY_4 != 0) data|=1;
		if (KEY_5 != 0) data|=2;
		if (KEY_6 != 0) data|=4;
		if (KEY_MUL != 0) data|=8;
		if (KEY_RM != 0) data|=0x10;
		if (KEY_SHIFT != 0) data|=0x20;
		if (KEY_DEF != 0) data|=0x40;
		if (KEY_SMALL != 0) data|=0x80;
	    }
	    if (asic[3]&4) {
		if (KEY_1 != 0) data|=1;
		if (KEY_2 != 0) data|=2;
		if (KEY_3 != 0) data|=4;
		if (KEY_MINUS != 0) data|=8;
		if (KEY_MPLUS != 0) data|=0x10;
		if (KEY_Q != 0) data|=0x20;
		if (KEY_A != 0) data|=0x40;
		if (KEY_Z != 0) data|=0x80;
	    }
	    if (asic[3]&8) {
		if (KEY_0 != 0) data|=1;
		if (KEY_SIGN != 0) data|=2;
		if (KEY_POINT != 0) data|=4;
		if (KEY_PLUS != 0) data|=8;
		if (KEY_EQUALS != 0) data|=0x10;
		if (KEY_W != 0) data|=0x20;
		if (KEY_S != 0) data|=0x40;
		if (KEY_X != 0) data|=0x80;
	    }
	    if (asic[3]&0x10) {
		if (KEY_HYP != 0) data|=1;
		if (KEY_SIN != 0) data|=2;
		if (KEY_COS != 0) data|=4;
		if (KEY_TAN != 0) data|=8;
		//0x10 toggles indicator 3c bit 0 japan?
		if (KEY_E != 0) data|=0x20;
		if (KEY_D != 0) data|=0x40;
		if (KEY_C != 0) data|=0x80;
	    }
	    if (asic[3]&0x20) {
		if (KEY_HEX != 0) data|=1;
		if (KEY_DEG != 0) data|=2;
		if (KEY_LN != 0) data|=4;
		if (KEY_LOG != 0) data|=8;
		//0x10 tilde
		if (KEY_R != 0) data|=0x20;
		if (KEY_F != 0) data|=0x40;
		if (KEY_V != 0) data|=0x80;
	    }
	    if (asic[3]&0x40) {
		if (KEY_EXP != 0) data|=1;
		if (KEY_POT != 0) data|=2;
		if (KEY_ROOT != 0) data|=4;
		if (KEY_SQUARE != 0) data|=8;
		//0x10 - yen
		if (KEY_T != 0) data|=0x20;
		if (KEY_G != 0) data|=0x40;
		if (KEY_B != 0) data|=0x80;
	    }
	    if ((outa & 1) != 0) {
		if (power||(KEY_CCE)) data|=2;
		if (KEY_STAT != 0) data|=4;
		if (KEY_FE != 0) data|=8;
		if (KEY_DOWN != 0) data|=0x10;
		if (KEY_Y != 0) data|=0x20;
		if (KEY_H != 0) data|=0x40;
		if (KEY_N != 0) data|=0x80;
	    }
	    if ((outa & 2) != 0) {
		if (KEY_BRACE_RIGHT != 0) data|=4;
		if (KEY_1X != 0) data|=8;
		if (KEY_UP != 0) data|=0x10;
		if (KEY_U != 0) data|=0x20;
		if (KEY_J != 0) data|=0x40;
		if (KEY_M != 0) data|=0x80;	
	    }
	    if ((outa & 4) != 0) {
		if (KEY_BRACE_LEFT != 0) data|=8;
		if (KEY_LEFT != 0) data|=0x10;
		if (KEY_I != 0) data|=0x20;
		if (KEY_K != 0) data|=0x40;
		if (KEY_SPC != 0) data|=0x80;
	    }
	    if ((outa & 8) != 0) {
		if (KEY_RIGHT != 0) data|=0x10;
		if (KEY_O != 0) data|=0x20;
		if (KEY_L != 0) data|=0x40;
		if (KEY_ENTER != 0) data|=0x80;
	    }
	    if ((outa & 0x10) != 0) {
		if (KEY_P != 0) data|=0x20;
		if (KEY_COMMA != 0) data|=0x40;
		if (KEY_BASIC != 0) data|=0x80;
	    }
	    if ((outa & 0x20) != 0) {
		//0x40 shift lock
		if (KEY_CAL != 0) data|=0x80;
	    }
	    if ((outa & 0x40) != 0) {
		if (KEY_OFF != 0) data|=0x80;
	    }
	    return data;
	}
	
	#if 0
	int pc1403_inb(void)
	{
		int data=outb;
		if (KEY_OFF != 0) data|=1;
		return data;
	}
	#endif
	
	void pc1403_outc(int data)
	{
	    pc1403_portc=data;
	    logerror("%g pc %.4x outc %.2x\n",timer_get_time(), cpu_get_pc(), data);
	}
	
	
	bool pc1403_brk(void)
	{
		return KEY_BRK;
	}
	
	bool pc1403_reset(void)
	{
		return KEY_RESET;
	}
	
	/* currently enough to save the external ram */
	static void pc1403_load(void)
	{
		FILE *file;
		UINT8 *ram=memory_region(REGION_CPU1)+0x8000,
			*cpu=sc61860_internal_ram();
	
		if ( (file=(FILE*)osd_fopen(Machine.gamedrv.name,
									Machine.gamedrv.name, OSD_FILETYPE_NVRAM, 0))==NULL) {
			power=0;
			return;
		}
	
		osd_fread(file, cpu, 96);
		osd_fread(file, ram, 0x8000);
		osd_fclose(file);
	}
	
	static void pc1403_save(void)
	{
		FILE *file;
		UINT8 *ram=memory_region(REGION_CPU1)+0x8000,
			*cpu=sc61860_internal_ram();
	
		if ( (file=(FILE*)osd_fopen(Machine.gamedrv.name,
							 Machine.gamedrv.name, OSD_FILETYPE_NVRAM, 1))==NULL)
			return;
	
		osd_fwrite(file, cpu, 96);
		osd_fwrite(file, ram, 0x8000);
		osd_fclose(file);
	}
	
	static void pc1403_power_up(int param)
	{
		power=0;
	}
	
	public static InitDriverPtr init_pc1403 = new InitDriverPtr() { public void handler() 
	{
		int i;
		UINT8 *gfx=memory_region(REGION_GFX1);
	
		for (i=0; i<128; i++) gfx[i]=i;
	
		pc1403_load();
		timer_pulse(1/500.0, 0,sc61860_2ms_tick);
		timer_set(1,0,pc1403_power_up);
	} };
	
	public static InitMachinePtr pc1403_machine_init = new InitMachinePtr() { public void handler() 
	{
		cpu_setbank(1, memory_region(REGION_USER1));
		if (RAM32K != 0) {
			install_mem_read_handler (0, 0x8000, 0xdfff, MRA_RAM);
			install_mem_write_handler (0, 0x8000, 0xdfff, MWA_RAM);
		} else {
			install_mem_read_handler (0, 0x8000, 0xdfff, MRA_NOP);
			install_mem_write_handler (0, 0x8000, 0xdfff, MWA_NOP);
		}
	} };
	
	void pc1403_machine_stop(void)
	{
		pc1403_save();
	}
	
}
