/*
 * converts fMSX's .cas files into samples for the MSX driver
 */

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package formats;

public class fmsx_casH
{
	
	int fmsx_cas_to_wav (UINT8 *casdata, int caslen, INT16 **wavdata, int *wavlen);
	int fmsx_cas_to_wav_size (UINT8 *casdata, int caslen);
	
}
