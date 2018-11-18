/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package machine;

public class pc1500
{
	
	static struct {
		UINT8 data[0x10];
	} lh5811= { { 0 } };
	
	/* upd1990ac (clock) connected to port c0..c5 b5 b6 
	   40 bit serial data
	   seconds minutes hours day week month
	
	*/
	
	public static ReadHandlerPtr lh5811_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int data=lh5811.data[offset];
		logerror("lh5811 read %x %.2x\n", offset, data);
		return data;
	} };
	
	public static WriteHandlerPtr lh5811_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		logerror("lh5811 write %x %.2x\n", offset, data);
		lh5811.data[offset]=data;
	} };
	
	UINT8 pc1500_in(void)
	{
		int data=0;
		if (lh5811.data[0xc]&1) {
			if (KEY_2 != 0) data|=1;
			if (KEY_5 != 0) data|=2;
			if (KEY_8 != 0) data|=4;
			if (KEY_H != 0) data|=8;
			if (KEY_SHIFT != 0) data|=0x10;
			if (KEY_Y != 0) data|=0x20;
			if (KEY_N != 0) data|=0x40;
			if (KEY_UP != 0) data|=0x80;
		}
		if (lh5811.data[0xc]&2) {
			if (KEY_POINT != 0) data|=1;
			if (KEY_MINUS != 0) data|=2;
			if (KEY_OFF != 0) data|=4;
			if (KEY_S != 0) data|=8;
			if (KEY_CALLSIGN != 0) data|=0x10;
			if (KEY_W != 0) data|=0x20;
			if (KEY_X != 0) data|=0x40;
			if (KEY_RESERVE != 0) data|=0x80;
		}
		if (lh5811.data[0xc]&4) {
			if (KEY_1 != 0) data|=1;
			if (KEY_4 != 0) data|=2;
			if (KEY_7 != 0) data|=4;
			if (KEY_J != 0) data|=8;
			if (KEY_PERCENT != 0) data|=0x10;
			if (KEY_U != 0) data|=0x20;
			if (KEY_M != 0) data|=0x40;
			if (KEY_0 != 0) data|=0x80;
		}
		if (lh5811.data[0xc]&8) {
			if (KEY_CLOSEBRACE != 0) data|=1;
			if (KEY_L != 0) data|=2;
			if (KEY_O != 0) data|=4;
			if (KEY_K != 0) data|=8;
			if (KEY_AMBERSAND != 0) data|=0x10;
			if (KEY_I != 0) data|=0x20;
			if (KEY_OPENBRACE != 0) data|=0x40;
			if (KEY_ENTER != 0) data|=0x80;
		}
		if (lh5811.data[0xc]&0x10) {
			if (KEY_PLUS != 0) data|=1;
			if (KEY_ASTERIX != 0) data|=2;
			if (KEY_SLASH != 0) data|=4;
			if (KEY_D != 0) data|=8;
			if (KEY_QUOTE != 0) data|=0x10;
			if (KEY_E != 0) data|=0x20;
			if (KEY_C != 0) data|=0x40;
			if (KEY_RCL != 0) data|=0x80;
		}
		if (lh5811.data[0xc]&0x20) {
			if (KEY_MINUS != 0) data|=1;
			if (KEY_LEFT != 0) data|=2;
			if (KEY_P != 0) data|=4;
			if (KEY_F != 0) data|=8;
			if (KEY_NUMBER != 0) data|=0x10;
			if (KEY_R != 0) data|=0x20;
			if (KEY_V != 0) data|=0x40;
			if (KEY_SPACE != 0) data|=0x80;
		}
		if (lh5811.data[0xc]&0x40) {
			if (KEY_RIGHT != 0) data|=1;
			if (KEY_MODE != 0) data|=2;
			if (KEY_CL != 0) data|=4;
			if (KEY_A != 0) data|=8;
			if (KEY_DEF != 0) data|=0x10;
			if (KEY_Q != 0) data|=0x20;
			if (KEY_Z != 0) data|=0x40;
			if (KEY_SML != 0) data|=0x80;
		}
		if (lh5811.data[0xc]&0x80) {
			if (KEY_3 != 0) data|=1;
			if (KEY_6 != 0) data|=2;
			if (KEY_9 != 0) data|=4;
			if (KEY_G != 0) data|=8;
			if (KEY_STRING != 0) data|=0x10;
			if (KEY_T != 0) data|=0x20;
			if (KEY_B != 0) data|=0x40;
			if (KEY_DOWN != 0) data|=0x80;
		}
		return data^0xff;
	}
}
