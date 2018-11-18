/* PAK file loader
 *
 * TODO: Support .PAK files generated by PC-Dragon
 */

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package formats;

public class cocopak
{
	
	/*	PAK files have the following format:
	 *
	 *	length		(two bytes, little endian)
	 *	base address (two bytes, little endian, typically 0xc000)
	 *	...data... (size is length)
	 *	optional trailer
	 *
	 *	The format for PAK files just plain bites - the extra info is snapshot info
	 *	and it is loaded with internal state specific to Jeff's emulator. What ever
	 *	happened to desiging clean file formats that don't need to be changed with
	 *	every version of ones program?
	 *
	 *	For alignment purposes, some 16 bit values are divided into two UINT8s
	 *
	 *	The PAK file format's trailer defines the state of the machine other then
	 *	the core memory.  This trailer comes in many variations.  I have labeled
	 *	them in the following way (trailer sizes are in parentheses):
	 *
	 *		V12		- The version in JeffV's emulator version 1.2
	 *		V14		- The version in JeffV's emulator version 1.4	(457)
	 *		VOTHER	- From some "other" version of JeffV's emulator
	 *		VLITE	- From yet another version of JeffV's emulator	(35)
	 *
	 *	The following table shows what segments appear in each trailer variant:
	 *
	 *	Trailer Segment		V12		V14		VOTHER	VLITE
	 *	---------------		---		---		------	-----
	 *	pak_trailer1		1		1		1		1
	 *	pak_trailer2		2
	 *	pak_trailer3		3		2		2		2
	 *	pak_trailer4		4		3		3
	 *	pak_trailer5		5
	 *	pak_trailer6		6		5		4
	 *	pak_trailer7		7				5
	 *	pak_trailer8				6
	 *	pak_trailer9		8		7		6
	 *	pak_trailer10		9		4		7
	 *	pak_trailer11		10		8		8
	 *	pak_trailer12				9
	 *	pak_trailer13		11				9
	 */
	
	/* All versions */
	typedef struct {
		char name[33];
	} pak_trailer1;
	
	/* Only version 1.2 */
	typedef struct {
		UINT8 debug_dumpflag;
		UINT8 debug_disassembleflag;
		UINT16 debug_disassembleaddr;
	} pak_trailer2;
	
	/* All versions */
	typedef struct {
		UINT16 reg_pc;
	} pak_trailer3;
	
	/* All versions except lite */
	typedef struct {
		UINT16 reg_x;
		UINT16 reg_y;
		UINT16 reg_u;
		UINT16 reg_s;
		UINT8 dummy_zero1;
		UINT8 reg_dp;
		UINT8 reg_b;
		UINT8 reg_a;
	} pak_trailer4;
	
	/* Only version 1.2 */
	typedef struct {
		UINT16 debug_unknown;
	} pak_trailer5;
	
	/* All versions except lite */
	typedef struct {
		UINT8 flags_8086_lsb; /* ?!? */
		UINT8 flags_8086_msb; /* ?!? */
		UINT8 reg_cc;
	} pak_trailer6;
	
	/* All versions except 1.4 and lite*/
	typedef struct {
		UINT16 lowmem_readseg;
		UINT16 lowmem_writeseg;
		UINT16 himem_readseg;
		UINT16 himem_writeseg;
	} pak_trailer7;
	
	/* Only version 1.4 */
	typedef struct {
		UINT8 page_status;
		UINT8 rom_status;
		UINT8 io_ff02;
		UINT8 io_ff03;
		UINT8 io_ff22;
	} pak_trailer8;
	
	/* All versions except lite */
	typedef struct {
		UINT16 video_base;
		UINT16 video_end;
	} pak_trailer9;
	
	/* All versions except lite; come before pak_trailer6 in 1.4 */
	typedef struct {
		UINT8 dummy_zero2[6];
	} pak_trailer10;
	
	/* Used by PC-Dragon; UINT16's are little endian */
	typedef struct {
		/* Info */
		UINT8 data1[2];
		UINT8 magic1[2];
		UINT8 checksum;
		UINT8 pak_version;
		UINT8 ext_version;
		UINT8 emu_version;
		UINT8 state_only;
		UINT8 architecture;
		UINT8 rom_start;
	
		/* Machine state */
		UINT8 irq_cycles_lsb;
		UINT8 irq_cycles_msb;
		UINT8 screen_size;
		UINT8 pia[9];
		UINT8 last_shift;
		UINT8 filemem[8];
	
		/* Debugger */
		UINT16 last_text_base;
		UINT16 breakpoints[6];
		UINT16 break_operation;
		UINT16 dasm_pcr;
		UINT16 fill_start;
		UINT16 fill_end;
		UINT8 fill_value;
		UINT8 left_window;
		UINT16 temp_break;
		UINT16 break_value;
		UINT8 break_control;
		UINT8 break_type;
		UINT16 internal;
		UINT16 dumpaddr;
	} pcd_info1;
	
	/* Used by PC-Dragon; UINT16's are little endian */
	typedef struct {
		UINT8 pakdata[65];
		UINT8 dummy1;
		UINT8 background;
		UINT8 foreground;
		UINT8 vmode[2][2][6];
		UINT8 border[2][2];
		UINT8 dummy2;
		UINT16 irq_rate;
		UINT8 servicemem[16];
		UINT8 speed;
		UINT8 lpt_and_swapping;
		UINT8 hardjoy_resol;
	} pcd_info2;
	
	/* All versions except lite */
	typedef struct {
		UINT8 writeprotect;
		char disk_directory[66];	/* Or could be a pcd_info1 */
		char disk_name[4][32];		/* Or could be a pcd_info2 */
		char pak_directory[66];
		UINT8 crlf;
		UINT8 keyboard_mode;
		UINT8 speed_lsb;
		UINT8 speed_msb;
		UINT8 left_joystick;
		UINT8 right_joystick;
		UINT8 lowercase_lsb;
		UINT8 lowercase_msb;
		UINT8 sound;
		UINT8 artifact;
		UINT8 dragon_rom;
	} pak_trailer11;
	
	/* Only version 1.4 */
	typedef struct {
		UINT16 joystick_limits[8];
		UINT16 clock;
		UINT8 drive_mode;
		UINT8 volume;
		UINT8 cassette_mode;
		char cassette_directory[66];
		char cassette_name[33];
	} pak_trailer12;
	
	/* All versions except 1.4 and lite */
	typedef struct {
		UINT8 dummy_zero3[4];
		UINT16 video_base2;
		UINT16 video_end2;
		UINT16 dummy_zero4;
		UINT8 io_ff22;
		UINT8 io_ff02;
		UINT8 io_ff03;
	} pak_trailer13;
	
	#define PAK_V12_SIZE	(sizeof(pak_trailer1) + sizeof(pak_trailer2) + \
		sizeof(pak_trailer3) + sizeof(pak_trailer4) + sizeof(pak_trailer5) + \
		sizeof(pak_trailer6) + sizeof(pak_trailer7) + sizeof(pak_trailer9) + \
		sizeof(pak_trailer10) + sizeof(pak_trailer11) + sizeof(pak_trailer13))
	
	#define PAK_V14_SIZE	(sizeof(pak_trailer1) + sizeof(pak_trailer3) + \
		sizeof(pak_trailer4) + sizeof(pak_trailer10) + sizeof(pak_trailer6) + \
		sizeof(pak_trailer8) + sizeof(pak_trailer9) + sizeof(pak_trailer11) + \
		sizeof(pak_trailer12))
	
	#define PAK_VOTHER_SIZE	(sizeof(pak_trailer1) + sizeof(pak_trailer3) + \
		sizeof(pak_trailer4) + sizeof(pak_trailer6) + sizeof(pak_trailer7) + \
		sizeof(pak_trailer9) + sizeof(pak_trailer10) + sizeof(pak_trailer11) + \
		sizeof(pak_trailer13))
	
	#define PAK_VLITE_SIZE (sizeof(pak_trailer1) + sizeof(pak_trailer3))
	
	static UINT16 calculate_sam_video(pak_trailer9 *p9)
	{
		UINT16 sam;
		UINT16 video_base;
		UINT16 video_end;
	
		video_base = LITTLE_ENDIANIZE_INT16(p9.video_base);
		video_end = LITTLE_ENDIANIZE_INT16(p9.video_end);
	
		sam = (video_base >> 6) & 0x3f8;
	
		switch(video_end - video_base) {
		case 512:
			sam |= 0;
			break;
		case 1024:
			sam |= 1;
			break;
		case 1536:
		case 2048:
			sam |= 2;
			break;
		case 3072:
			sam |= 4;
			break;
		case 6144:
		default:
			sam |= 6;
			break;
		}
	
		return sam;
	}
	
	int pak_decode_trailer(UINT8 *rawtrailer, int rawtrailerlen, pak_decodedtrailer *trailer)
	{
		pak_trailer3 p3;
		pak_trailer4 p4;
		pak_trailer6 p6;
		pak_trailer9 p9;
		pak_trailer11 p11;
		pcd_info1 *pcd1 = NULL;
		pcd_info2 *pcd2 = NULL;
	
		union {
			pak_trailer8 p8;
			struct {
				pak_trailer7 p7;
				pak_trailer13 p13;
			} s;
		} u1;
	
		switch(rawtrailerlen) {
		case PAK_V12_SIZE:
		case PAK_V14_SIZE:
		case PAK_VOTHER_SIZE:
		case PAK_VLITE_SIZE:
			break;
		default:
			return 1;
		}
	
		memset(&p3, 0, sizeof(p3));
		memset(&p4, 0, sizeof(p4));
		memset(&p6, 0, sizeof(p6));
		memset(&p9, 0, sizeof(p9));
		memset(&u1, 0, sizeof(u1));
	
		rawtrailer += sizeof(pak_trailer1);
	
		if (rawtrailerlen == PAK_V12_SIZE) {
			rawtrailer += sizeof(pak_trailer2);
		}
	
		memcpy(&p3, rawtrailer, sizeof(pak_trailer3));
		rawtrailer += sizeof(pak_trailer3);
	
		if (rawtrailerlen != PAK_VLITE_SIZE) {
			memcpy(&p4, rawtrailer, sizeof(pak_trailer4));
			rawtrailer += sizeof(pak_trailer4);
		}
	
		if (rawtrailerlen == PAK_V14_SIZE) {
			rawtrailer += sizeof(pak_trailer10);
		}
		else if (rawtrailerlen == PAK_V12_SIZE) {
			rawtrailer += sizeof(pak_trailer5);
		}
	
		if (rawtrailerlen != PAK_VLITE_SIZE) {
			memcpy(&p6, rawtrailer, sizeof(pak_trailer6));
			rawtrailer += sizeof(pak_trailer6);
		}
	
		if (rawtrailerlen == PAK_V14_SIZE) {
			memcpy(&u1.p8, rawtrailer, sizeof(pak_trailer8));
			rawtrailer += sizeof(pak_trailer8);
		}
		else if (rawtrailerlen != PAK_VLITE_SIZE) {
			memcpy(&u1.s.p7, rawtrailer, sizeof(pak_trailer7));
			rawtrailer += sizeof(pak_trailer7);
		}
	
		if (rawtrailerlen != PAK_VLITE_SIZE) {
			memcpy(&p9, rawtrailer, sizeof(pak_trailer9));
			rawtrailer += sizeof(pak_trailer9);
		}
	
		if ((rawtrailerlen != PAK_V14_SIZE) && (rawtrailerlen != PAK_VLITE_SIZE)) {
			rawtrailer += sizeof(pak_trailer10);
		}
	
		if (rawtrailerlen != PAK_VLITE_SIZE) {
			memcpy(&p11, rawtrailer, sizeof(pak_trailer11));
			rawtrailer += sizeof(pak_trailer11);
	
			/* Is this a fancy image with PC-Dragon Info in it? */
			pcd1 = (pcd_info1 *) p11.disk_directory;
			if ((pcd1.magic1[0] == 0xAB) && (pcd1.magic1[1] == 0xBA)) {
				/* Yes it is! */
				/* Note! I should be checking the checksum */
				pcd2 = (pcd_info2 *) p11.disk_name;
			}
			else {
				/* No it is not */
				pcd1 = NULL;
			}
		}
	
		if (rawtrailerlen == PAK_V14_SIZE) {
			rawtrailer += sizeof(pak_trailer12);
		}
		else if (rawtrailerlen != PAK_VLITE_SIZE) {
			memcpy(&u1.s.p13, rawtrailer, sizeof(pak_trailer13));
			rawtrailer += sizeof(pak_trailer13);
		}
	
		trailer.reg_pc = LITTLE_ENDIANIZE_INT16(p3.reg_pc);
		trailer.reg_x = LITTLE_ENDIANIZE_INT16(p4.reg_x);
		trailer.reg_y = LITTLE_ENDIANIZE_INT16(p4.reg_y);
		trailer.reg_u = LITTLE_ENDIANIZE_INT16(p4.reg_u);
		trailer.reg_s = LITTLE_ENDIANIZE_INT16(p4.reg_s);
		trailer.reg_dp = p4.reg_dp;
		trailer.reg_b = p4.reg_b;
		trailer.reg_a = p4.reg_a;
		trailer.reg_cc = p6.reg_cc;
	
		trailer.pia[0] = 0x00;
		trailer.pia[1] = 0x34;
		trailer.pia[4] = 0x02;
		trailer.pia[5] = 0x34;
		trailer.pia[7] = 0x3c;
	
		switch(rawtrailerlen) {
		case PAK_V14_SIZE:
			trailer.pia[2] = u1.p8.io_ff02;
			trailer.pia[3] = u1.p8.io_ff03;
			trailer.pia[6] = u1.p8.io_ff22;
			trailer.sam = (u1.p8.rom_status == 0xdf) ? 0xE000 : 0x6000;
			trailer.sam |= calculate_sam_video(&p9);
			break;
	
		case PAK_VLITE_SIZE:
			/* This is a "lite" format that gives no trailer information except
			 * for the program counter... I have to set everything up as default
			 * including the stack pointer... arg this sucks...
			 */
			trailer.pia[2] = 0xff;
			trailer.pia[3] = 0x34;
			trailer.pia[6] = 0x00;
			trailer.sam = 0x6008;
			trailer.reg_s = 0x3d7;
			break;
	
		default:
			trailer.pia[2] = u1.s.p13.io_ff02;
			trailer.pia[3] = u1.s.p13.io_ff03;
			trailer.pia[6] = u1.s.p13.io_ff22;
			trailer.sam = (u1.s.p7.himem_readseg == 0) ? 0xE000 : 0x6000;
			trailer.sam |= calculate_sam_video(&p9);
			break;
		}
		return 0;
	}
	
}
