#ifdef UNDER_CE
/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package windows;

public class osdutilsH
{
	#else
	#endif
	
	#define strcmpi		stricmp
	#define strncmpi	strnicmp
	
	#ifndef UNDER_CE
	#define osd_mkdir(dir)	mkdir(dir)
	#endif
	
	#define PATH_SEPARATOR	'\\'
	
	#define EOLN "\r\n"
}
