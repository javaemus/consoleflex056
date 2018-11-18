/*
 * converts fMSX's .cas files into samples for the MSX driver
 */

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package formats;

public class svi_casH
{
	
	int svi_cas_to_wav (UINT8 *casdata, int caslen, INT16 **wavdata, int *wavlen);
	
}
