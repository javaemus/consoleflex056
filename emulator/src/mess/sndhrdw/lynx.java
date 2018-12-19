/******************************************************************************
 PeT mess@utanet.at 2000,2001
******************************************************************************/
/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess.sndhrdw;

import static WIP.arcadeflex.fucPtr.*;
import static WIP.mame.sndintrfH.*;

public class lynx
{
	
	
	/* accordingly to atari's reference manual
	   there were no stereo lynx produced (the manual knows only production until mid 1991)
	   the howard/developement board might have stereo support
	   the revised lynx 2 hardware might have stereo support at least at the stereo jacks
	
	   some games support stereo
	*/
	
	
	/*
	AUDIO_A	EQU $FD20
	AUDIO_B	EQU $FD28
	AUDIO_C	EQU $FD30
	AUDIO_D	EQU $FD38
	
	VOLUME_CNTRL	EQU 0
	FEEDBACK_ENABLE EQU 1	; enables 11/10/5..0
	OUTPUT_VALUE	EQU 2
	SHIFTER_L	EQU 3
	AUD_BAKUP	EQU 4
	AUD_CNTRL1	EQU 5
	AUD_COUNT	EQU 6
	AUD_CNTRL2	EQU 7
	
	; AUD_CNTRL1
	FEEDBACK_7	EQU %10000000
	AUD_RESETDONE	EQU %01000000
	AUD_INTEGRATE	EQU %00100000
	AUD_RELOAD	EQU %00010000
	AUD_CNTEN	EQU %00001000
	AUD_LINK	EQU %00000111	
	; link timers (0.2.4 / 1.3.5.7.Aud0.Aud1.Aud2.Aud3.1
	AUD_64us	EQU %00000110
	AUD_32us	EQU %00000101
	AUD_16us	EQU %00000100
	AUD_8us	EQU %00000011
	AUD_4us	EQU %00000010
	AUD_2us	EQU %00000001
	AUD_1us	EQU %00000000
	
	; AUD_CNTRL2 (read only)
	; B7..B4	; shifter bits 11..8
	; B3	; who knows
	; B2	; last clock state (0.1 causes count)
	; B1	; borrow in (1 causes count)
	; B0	; borrow out (count EQU 0 and borrow in)
	
	ATTEN_A	EQU $FD40
	ATTEN_B	EQU $FD41
	ATTEN_C	EQU $FD42
	ATTEN_D	EQU $FD43
	; B7..B4 attenuation left ear (0 silent ..15/16 volume)
	; B3..B0       "     right ear
	
	MPAN	EQU $FD44
	; B7..B4 left ear
	; B3..B0 right ear
	; B7/B3 EQU Audio D
	; a 1 enables attenuation for channel and side
	
	
	MSTEREO	EQU $FD50	; a 1 disables audio connection
	AUD_D_LEFT	EQU %10000000
	AUD_C_LEFT	EQU %01000000
	AUD_B_LEFT	EQU %00100000
	AUD_A_LEFT	EQU %00010000
	AUD_D_RIGHT	EQU %00001000
	AUD_C_RIGHT	EQU %00000100
	AUD_B_RIGHT	EQU %00000010
	AUD_A_RIGHT	EQU %00000001
	
	 */
	/*TODO*/////static int mixer_channel;
	/*TODO*/////static int usec_per_sample;
	static int[] shift_mask;
	static int[] shift_xor;
	
	/*TODO*/////typedef struct {
	/*TODO*/////    int nr;
	/*TODO*/////    union {
	/*TODO*/////	UINT8 data[8];
	/*TODO*/////	struct {
	/*TODO*/////	    UINT8 volume;
	/*TODO*/////	    UINT8 feedback;
	/*TODO*/////	    INT8 output;
	/*TODO*/////	    UINT8 shifter;
	/*TODO*/////	    UINT8 bakup;
	/*TODO*/////	    UINT8 control1;
	/*TODO*/////	    UINT8 counter;
	/*TODO*/////	    UINT8 control2;
	/*TODO*/////	} n;
	/*TODO*/////    } reg;
	/*TODO*/////    UINT8 attenuation;
	/*TODO*/////    int mask;
	/*TODO*/////    int shifter;
	/*TODO*/////    int ticks;
	/*TODO*/////    int count;
	/*TODO*/////} LYNX_AUDIO;
	/*TODO*/////static LYNX_AUDIO lynx_audio[4]= { 
	/*TODO*/////	{ 0 },
	/*TODO*/////	{ 1 },
	/*TODO*/////	{ 2 },
	/*TODO*/////	{ 3 } 
	/*TODO*/////};
	
	/*TODO*/////static void lynx_audio_reset_channel(LYNX_AUDIO *This)
	/*TODO*/////{
	/*TODO*/////    memset(This.reg.data, 0, (char*)(This+1)-(char*)(This.reg.data));
	/*TODO*/////}
	
	/*TODO*/////void lynx_audio_count_down(int nr)
	/*TODO*/////{
	/*TODO*/////    LYNX_AUDIO *This=lynx_audio+nr;
	/*TODO*/////    if (This.reg.n.control1&8 && (This.reg.n.control1&7)!=7) return;
	/*TODO*/////    if (nr==0) stream_update(mixer_channel,0);
	/*TODO*/////    This.count--;
	/*TODO*/////}
	
	/*TODO*/////void lynx_audio_debug(struct mame_bitmap *bitmap)
	/*TODO*/////{
	/*TODO*/////    char str[40];
	/*TODO*/////    sprintf(str,"%.2x %.2x %.2x %.2x %.2x %.2x %.2x %.2x",
	/*TODO*/////	    lynx_audio[0].reg.data[0],
	/*TODO*/////	    lynx_audio[0].reg.data[1],
	/*TODO*/////	    lynx_audio[0].reg.data[2],
	/*TODO*/////	    lynx_audio[0].reg.data[3],
	/*TODO*/////	    lynx_audio[0].reg.data[4],
	/*TODO*/////	    lynx_audio[0].reg.data[5],
	/*TODO*/////	    lynx_audio[0].reg.data[6],
	/*TODO*/////	    lynx_audio[0].reg.data[7]);
	
	//    ui_text(bitmap, str, 0,0);
	/*TODO*/////}
	
	/*TODO*/////static void lynx_audio_shift(LYNX_AUDIO *channel)
	/*TODO*/////{
	/*TODO*/////    channel.shifter=((channel.shifter<<1)&0x3ff)
	/*TODO*/////	|shift_xor[channel.shifter&channel.mask];
	    
	/*TODO*/////    if (channel.reg.n.control1&0x20) {
	/*TODO*/////	if (channel.shifter&1) {
	/*TODO*/////	    channel.reg.n.output+=channel.reg.n.volume;
	/*TODO*/////	} else {
	/*TODO*/////	    channel.reg.n.output-=channel.reg.n.volume;
	/*TODO*/////	}
	/*TODO*/////    }
	/*TODO*/////    switch (channel.nr) {
	/*TODO*/////    case 0: lynx_audio_count_down(1); break;
	/*TODO*/////    case 1: lynx_audio_count_down(2); break;
	/*TODO*/////    case 2: lynx_audio_count_down(3); break;
	/*TODO*/////    case 3: lynx_timer_count_down(1); break;
	/*TODO*/////    }
	/*TODO*/////}
	
	/*TODO*/////static void lynx_audio_execute(LYNX_AUDIO *channel)
	/*TODO*/////{
	/*TODO*/////    if (channel.reg.n.control1&8) { // count_enable
	/*TODO*/////	channel.ticks+=usec_per_sample;
	/*TODO*/////	if ((channel.reg.n.control1&7)==7) { // timer input
	/*TODO*/////	    if (channel.count<0) {
	/*TODO*/////		channel.count+=channel.reg.n.counter;
	/*TODO*/////		lynx_audio_shift(channel);
	/*TODO*/////	    }
	/*TODO*/////	} else {
	/*TODO*/////	    int t=1<<(channel.reg.n.control1&7);
	/*TODO*/////	    for (;;) {
	/*TODO*/////		for (;(channel.ticks>=t)&&channel.count>=0; channel.ticks-=t)
	/*TODO*/////		    channel.count--;
	/*TODO*/////		if (channel.ticks<t) break;
	/*TODO*/////		if (channel.count<0) {
	/*TODO*/////		    channel.count=channel.reg.n.counter;
	/*TODO*/////		    lynx_audio_shift(channel);
	/*TODO*/////		}
	/*TODO*/////	    }
	/*TODO*/////	}
	/*TODO*/////	if (!(channel.reg.n.control1&0x20)) {
	/*TODO*/////	    channel.reg.n.output=channel.shifter&1?0-channel.reg.n.volume:channel.reg.n.volume;
	/*TODO*/////	}
	/*TODO*/////    } else {
	/*TODO*/////	channel.ticks=0;
	/*TODO*/////	channel.count=0;
	/*TODO*/////    }
	/*TODO*/////}
	
	/*TODO*/////static UINT8 attenuation_enable;
	/*TODO*/////static UINT8 master_enable;
	
	/*TODO*/////UINT8 lynx_audio_read(int offset)
	/*TODO*/////{
	/*TODO*/////    UINT8 data=0;
	/*TODO*/////    stream_update(mixer_channel,0);
	/*TODO*/////    switch (offset) {
	/*TODO*/////    case 0x20: case 0x21: case 0x22: case 0x24: case 0x25:
	/*TODO*/////    case 0x28: case 0x29: case 0x2a: case 0x2c: case 0x2d: 
	/*TODO*/////    case 0x30: case 0x31: case 0x32: case 0x34: case 0x35: 
	/*TODO*/////    case 0x38: case 0x39: case 0x3a: case 0x3c: case 0x3d: 
	/*TODO*/////	data=lynx_audio[(offset>>3)&3].reg.data[offset&7];
	/*TODO*/////	break;
	/*TODO*/////    case 0x23: case 0x2b: case 0x33: case 0x3b: 
	/*TODO*/////	data=lynx_audio[(offset>>3)&3].shifter&0xff;
	/*TODO*/////	break;
	/*TODO*/////    case 0x26:case 0x2e:case 0x36:case 0x3e:
	/*TODO*/////	data=lynx_audio[(offset>>3)&3].count;
	/*TODO*/////	break;
	/*TODO*/////    case 0x27: case 0x2f: case 0x37: case 0x3f:
	/*TODO*/////	data=(lynx_audio[(offset>>3)&3].shifter>>4)&0xf0;
	/*TODO*/////	data|=lynx_audio[(offset>>3)&3].reg.data[offset&7]&0x0f;
	/*TODO*/////	break;
	/*TODO*/////    case 0x40: case 0x41: case 0x42: case 0x43: 
	/*TODO*/////	data=lynx_audio[offset&3].attenuation;
	/*TODO*/////	break;
	/*TODO*/////    case 0x44: 
	/*TODO*/////	data=attenuation_enable;
	/*TODO*/////	break;
	/*TODO*/////    case 0x50:
	/*TODO*/////	data=master_enable;
	/*TODO*/////	break;
	/*TODO*/////    }
	/*TODO*/////    return data;
	/*TODO*/////}
	
	/*TODO*/////void lynx_audio_write(int offset, UINT8 data)
	/*TODO*/////{
	//	logerror("%.6f audio write %.2x %.2x\n", timer_get_time(), offset, data);
	/*TODO*/////    LYNX_AUDIO *channel=lynx_audio+((offset>>3)&3);
	/*TODO*/////    stream_update(mixer_channel,0);
	/*TODO*/////    switch (offset) {
	/*TODO*/////    case 0x20: case 0x22: case 0x24: case 0x26:
	/*TODO*/////    case 0x28: case 0x2a: case 0x2c: case 0x2e:
	/*TODO*/////    case 0x30: case 0x32: case 0x34: case 0x36:
	/*TODO*/////    case 0x38: case 0x3a: case 0x3c: case 0x3e:
	/*TODO*/////	lynx_audio[(offset>>3)&3].reg.data[offset&7]=data;
	/*TODO*/////	break;
	/*TODO*/////    case 0x23: case 0x2b: case 0x33: case 0x3b: 
	/*TODO*/////	lynx_audio[(offset>>3)&3].reg.data[offset&7]=data;
	/*TODO*/////	lynx_audio[(offset>>3)&3].shifter&=~0xff;
	/*TODO*/////	lynx_audio[(offset>>3)&3].shifter|=data;
	/*TODO*/////	break;
	/*TODO*/////    case 0x27: case 0x2f: case 0x37: case 0x3f:
	/*TODO*/////	lynx_audio[(offset>>3)&3].reg.data[offset&7]=data;
	/*TODO*/////	lynx_audio[(offset>>3)&3].shifter&=~0xf00;
	/*TODO*/////	lynx_audio[(offset>>3)&3].shifter|=(data&0xf0)<<4;
	/*TODO*/////	break;
	/*TODO*/////    case 0x21: case 0x25:
	/*TODO*/////    case 0x29: case 0x2d:
	/*TODO*/////    case 0x31: case 0x35:
	/*TODO*/////    case 0x39: case 0x3d:
	/*TODO*/////	channel.reg.data[offset&7]=data;
	/*TODO*/////	channel.mask=channel.reg.n.feedback;
	/*TODO*/////	channel.mask|=(channel.reg.data[5]&0x80)<<1;
	/*TODO*/////	break;
	/*TODO*/////    case 0x40: case 0x41: case 0x42: case 0x43: // lynx2 only, howard extension board
	/*TODO*/////	lynx_audio[offset&3].attenuation=data;
	/*TODO*/////	break;
	/*TODO*/////    case 0x44: 
	/*TODO*/////	attenuation_enable=data; //lynx2 only, howard extension board
	/*TODO*/////	break;
	/*TODO*/////    case 0x50:
	/*TODO*/////	master_enable=data;//lynx2 only, howard write only
	/*TODO*/////	break;
	/*TODO*/////    }
	/*TODO*/////}
	
	/************************************/
	/* Sound handler update             */
	/************************************/
	/*TODO*/////void lynx_update (int param, INT16 *buffer, int length)
	/*TODO*/////{
	/*TODO*/////    int i, j;
	/*TODO*/////    LYNX_AUDIO *channel;
	/*TODO*/////    int v;
	    
	/*TODO*/////    for (i = 0; i < length; i++, buffer++)
	/*TODO*/////    {
	/*TODO*/////	*buffer = 0;
	/*TODO*/////	for (channel=lynx_audio, j=0; j<ARRAY_LENGTH(lynx_audio); j++, channel++) {
	/*TODO*/////	    lynx_audio_execute(channel);
	/*TODO*/////	    v=channel.reg.n.output;
	/*TODO*/////	    *buffer+=v*15;
	/*TODO*/////	}
	/*TODO*/////    }
	/*TODO*/////}
	
	/*TODO*/////void lynx2_update (int param, INT16 **buffer, int length)
	/*TODO*/////{
	/*TODO*/////    INT16 *left=buffer[0], *right=buffer[1];
	/*TODO*/////    int i, j;
	/*TODO*/////    LYNX_AUDIO *channel;
	/*TODO*/////    int v;
	    
	/*TODO*/////    for (i = 0; i < length; i++, left++, right++)
	/*TODO*/////    {
	/*TODO*/////	*left = 0;
	/*TODO*/////	*right= 0;
	/*TODO*/////	for (channel=lynx_audio, j=0; j<ARRAY_LENGTH(lynx_audio); j++, channel++) {
	/*TODO*/////	    lynx_audio_execute(channel);
	/*TODO*/////	    v=channel.reg.n.output;
	/*TODO*/////	    if (!(master_enable&(0x10<<j))) {		    
	/*TODO*/////		if (attenuation_enable&(0x10<<j)) {
	/*TODO*/////		    *left+=v*(channel.attenuation>>4);
	/*TODO*/////		} else {
	/*TODO*/////		    *left+=v*15;
	/*TODO*/////		}
	/*TODO*/////	    }
	/*TODO*/////	    if (!(master_enable&(1<<j))) {
	/*TODO*/////		if (attenuation_enable&(1<<j)) {
	/*TODO*/////		    *right+=v*(channel.attenuation&0xf);
	/*TODO*/////		} else {
	/*TODO*/////		    *right+=v*15;
	/*TODO*/////		}
	/*TODO*/////	    }
	/*TODO*/////	}
	/*TODO*/////    }
	/*TODO*/////}
	
	/*TODO*/////static void lynx_audio_init(void)
	/*TODO*/////{
	/*TODO*/////    int i;
	/*TODO*/////    shift_mask=(int*)malloc(512*sizeof(int));
	/*TODO*/////    assert(shift_mask!=0);
	
	/*TODO*/////    shift_xor=(int*)malloc(4096*sizeof(int));
	/*TODO*/////    assert(shift_xor!=0);
	
	/*TODO*/////    for (i=0; i<512; i++) {
	/*TODO*/////	shift_mask[i]=0;
	/*TODO*/////	if ((i & 1) != 0) shift_mask[i]|=1;
	/*TODO*/////	if ((i & 2) != 0) shift_mask[i]|=2;
	/*TODO*/////	if ((i & 4) != 0) shift_mask[i]|=4;
	/*TODO*/////	if ((i & 8) != 0) shift_mask[i]|=8;
	/*TODO*/////	if ((i & 0x10) != 0) shift_mask[i]|=0x10;
	/*TODO*/////	if ((i & 0x20) != 0) shift_mask[i]|=0x20;
	/*TODO*/////	if ((i & 0x40) != 0) shift_mask[i]|=0x400;
	/*TODO*/////	if ((i & 0x80) != 0) shift_mask[i]|=0x800;
	/*TODO*/////	if ((i & 0x100) != 0) shift_mask[i]|=0x80;
	/*TODO*/////    }
	/*TODO*/////    for (i=0; i<4096; i++) {
	/*TODO*/////	int j;
	/*TODO*/////	shift_xor[i]=1;
	/*TODO*/////	for (j=4096/2; j>0; j>>=1) {
	/*TODO*/////	    if ((i & j) != 0) shift_xor[i]^=1;
	/*TODO*/////	}
	/*TODO*/////    }
	/*TODO*/////}
	
	/*TODO*/////void lynx_audio_reset(void)
	/*TODO*/////{
	/*TODO*/////    int i;
	/*TODO*/////    for (i=0; i<ARRAY_LENGTH(lynx_audio); i++) {
	/*TODO*/////	lynx_audio_reset_channel(lynx_audio+i);
	/*TODO*/////    }
	/*TODO*/////}
	
	/************************************/
	/* Sound handler start              */
	/************************************/
	/*TODO*/////int lynx_custom_start (const struct MachineSound *driver)
        public static ShStartPtr lynx_custom_start = new ShStartPtr() {
            public int handler(MachineSound msound) {
                /*TODO*/////    if (!options.samplerate) return 0;
	
                /*TODO*/////    mixer_channel = stream_init("lynx", MIXER(50, MIXER_PAN_CENTER), 
                /*TODO*/////				options.samplerate, 0, lynx_update);
	
                /*TODO*/////    usec_per_sample=1000000/options.samplerate;
	    
                /*TODO*/////    lynx_audio_init();
                return 0;
            }
        };
	
	/*TODO*/////int lynx2_custom_start (const struct MachineSound *driver)
        public static ShStartPtr lynx2_custom_start = new ShStartPtr() {
            public int handler(MachineSound msound) {
	
                /*TODO*/////    const int vol[2]={ MIXER(50, MIXER_PAN_LEFT), MIXER(50, MIXER_PAN_RIGHT) };
                /*TODO*/////    const char *names[2]= { "lynx", "lynx" };

                /*TODO*/////    if (!options.samplerate) return 0;

                /*TODO*/////    mixer_channel = stream_init_multi(2, names, vol, options.samplerate, 0, lynx2_update);

                /*TODO*/////    usec_per_sample=1000000/options.samplerate;

                /*TODO*/////    lynx_audio_init();
                return 0;
            }
        };
	
	/************************************/
	/* Sound handler stop               */
	/************************************/
	public static ShStopPtr lynx_custom_stop = new ShStopPtr() {
            public void handler() {
                shift_xor=null;
                shift_mask=null;
            }
        }; 
	
	public static ShUpdatePtr lynx_custom_update = new ShUpdatePtr() {
            public void handler() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
	
}
