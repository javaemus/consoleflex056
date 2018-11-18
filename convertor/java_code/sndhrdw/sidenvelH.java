//
// /home/ms/source/sidplay/libsidplay/emu/RCS/envelope.h,v
//

#ifndef ENVELOPE_H
#define ENVELOPE_H


/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package sndhrdw;

public class sidenvelH
{
	
	extern void enveEmuInit(udword updateFreq, bool measuredValues);
	void enveEmuResetOperator(sidOperator* pVoice);
	
	
	extern ptr2sidUwordFunc enveModeTable[];   // . envelope.cpp
	extern const ubyte masterVolumeLevels[16];  // . envelope.cpp
	
	static const ubyte ENVE_STARTATTACK = 0;
	static const ubyte ENVE_STARTRELEASE = 2;
	
	static const ubyte ENVE_ATTACK = 4;
	static const ubyte ENVE_DECAY = 6;
	static const ubyte ENVE_SUSTAIN = 8;
	static const ubyte ENVE_RELEASE = 10;
	static const ubyte ENVE_SUSTAINDECAY = 12;
	static const ubyte ENVE_MUTE = 14;
	
	static const ubyte ENVE_STARTSHORTATTACK = 16;
	static const ubyte ENVE_SHORTATTACK = 16;
	
	static const ubyte ENVE_ALTER = 32;
	
	
	#endif
}
