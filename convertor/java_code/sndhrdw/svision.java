/***************************************************************************
 supervision sound hardware

 PeT mess@utanet.at
***************************************************************************/
/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package sndhrdw;

public class svision
{
	
	
	static int mixer_channel;
	
	SVISION_CHANNEL svision_channel[2];
	
	void svision_soundport_w (SVISION_CHANNEL *channel, int offset, int data)
	{
	    stream_update(mixer_channel,0);
	    logerror("%.6f channel 1 write %d %02x\n", timer_get_time(),offset&3, data);
	    channel.reg[offset]=data;
	    switch (offset) {
	    case 0:
	    case 1:
		if (channel.reg[0]) {
		    if (channel==svision_channel) 
			channel.size=(int)((options.samplerate*channel.reg[0]<<6)/4e6);
		    else
			channel.size=(int)((options.samplerate*channel.reg[0]<<6)/4e6);
		} else channel.size=0;
		channel.pos=0;
	    }
	    
	}
	
	/************************************/
	/* Sound handler update             */
	/************************************/
	void svision_update (int param, INT16 **buffer, int length)
	{
	    INT16 *left=buffer[0], *right=buffer[1];
	    int i, j;
	    SVISION_CHANNEL *channel;
	    
	    for (i = 0; i < length; i++, left++, right++)
	    {
		*left = 0;
		*right = 0;
		for (channel=svision_channel, j=0; j<ARRAY_LENGTH(svision_channel); j++, channel++) {
		    if (channel.pos<=channel.size/2) {
			if (channel.reg[2]&0x40) {
			    *left+=(channel.reg[2]&0xf)<<8;
			}
			if (channel.reg[2]&0x20) {
			    *right+=(channel.reg[2]&0xf)<<8;
			}
		    }
		    if (channel.reg[2]&0x60) {
			if (++channel.pos>=channel.size) channel.pos=0;
		    }
		}
	    }
	}
	
	/************************************/
	/* Sound handler start              */
	/************************************/
	int svision_custom_start (const struct MachineSound *driver)
	{
	    const int vol[2]={ MIXER(50, MIXER_PAN_LEFT), MIXER(50, MIXER_PAN_RIGHT) };
	    const char *names[2]= { "supervision", "supervision" };
		
	    if (!options.samplerate) return 0;
	
	    mixer_channel = stream_init_multi(2, names, vol, options.samplerate, 0, svision_update);
	    
	    return 0;
	}
	
	/************************************/
	/* Sound handler stop               */
	/************************************/
	void svision_custom_stop (void)
	{
	}
	
	void svision_custom_update (void)
	{
	}
}
