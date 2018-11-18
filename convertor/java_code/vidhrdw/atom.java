/******************************************************************************

atom.c

******************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class atom
{
	
	static void atom_charproc(UINT8 c)
	{
		m6847_inv_w(0,		(c & 0x80));
		m6847_as_w(0,		(c & 0x40));
		m6847_intext_w(0,	(c & 0x40));
	}
	
	public static VhStartPtr atom_vh_start = new VhStartPtr() { public int handler() 
	{
		struct m6847_init_params p;
	
		m6847_vh_normalparams(&p);
		p.version = M6847_VERSION_ORIGINAL_PAL;
		p.ram = memory_region(REGION_CPU1) + 0x8000;
		p.ramsize = 0x10000;
		p.charproc = atom_charproc;
	
		if (m6847_vh_start(&p))
			return 1;
	
		return (0);
	} };
	
}
