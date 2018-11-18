
/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package sndhrdw;

public class _6581_H
{
	
	void sidEmuSetReplayingSpeed(int ntsc, uword callsPerSec);
	void sidEmuResetAutoPanning(int autoPanning);
	
	extern sbyte waveCalcNormal(sidOperator* pVoice);
	extern sbyte waveCalcRangeCheck(sidOperator* pVoice);
	
	void sidEmuConfigure(udword PCMfrequency, bool measuredEnveValues, 
						 bool isNewSID, bool emulateFilter, int clockSpeed);
	
	bool sidEmuReset(void);
	
	void sidEmuSetVoiceVolume(int voice, uword leftLevel, uword rightLevel, uword total);
	
	extern sidOperator optr1, optr2, optr3;
	extern uword voice4_gainLeft, voice4_gainRight;
	
	#if 0
	void sidEmuFillBuffer( emuEngine *thisEmu,
						   sidTune *thisTune, 
						   void* buffer, udword bufferLen );
	#else
	void sidEmuFillBuffer( void* buffer, udword bufferLen );
	#endif
	
	}
