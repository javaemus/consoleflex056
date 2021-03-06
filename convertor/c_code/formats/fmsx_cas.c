
#include "fmsx_cas.h"

#define CAS_PERIOD        (16)
#define CAS_HEADER_PERIODS (4000)
#define CAS_EMPTY_PERIODS (1000)
#define ALLOCATE_BLOCK    (1024*8)
static const UINT8 CasHeader[8] = { 0x1F,0xA6,0xDE,0xBA,0xCC,0x13,0x7D,0x74 };

int fmsx_cas_to_wav_size (UINT8 *casdata, int caslen)
	{
	int 	pos, size;

	if (caslen < 8) return -1;
	if (memcmp (casdata, CasHeader, sizeof (CasHeader) ) ) return -1;

	pos = size = 0;
	 
	while (pos < caslen)
		{
		if ( (pos + 8) < caslen)
			if (!memcmp (casdata + pos, CasHeader, 8) ) 
				{
				size += (CAS_EMPTY_PERIODS + CAS_HEADER_PERIODS) * CAS_PERIOD;
				pos += 8;
				continue;
				}

		size += CAS_PERIOD * 12;
		pos++;
		}

	return size;
	}

int fmsx_cas_to_wav (UINT8 *casdata, int caslen, INT16 **wavdata, int *wavlen)
{
	int cas_pos, samples_size, bit, state = 1, samples_pos, size, n, i, p;
	INT16 *samples, *nsamples;

	if (caslen < 8) return 1;
	if (memcmp (casdata, CasHeader, sizeof (CasHeader) ) ) return 1;

	cas_pos = 8;
	samples_size = ALLOCATE_BLOCK * 2;
	samples = (INT16*) malloc (samples_size);
	if (!samples)
		return 2;

	samples_pos = 0;

    while (cas_pos < caslen)
		{
		/* check memory for entire header (silence + header itself) */
		size = (CAS_EMPTY_PERIODS + CAS_HEADER_PERIODS) * CAS_PERIOD;
		if ( (samples_pos + size) >= samples_size)
			{
			samples_size += size;
			nsamples = (INT16*) realloc (samples, samples_size * 2);
			if (!nsamples)
				{
				free (samples);
				return 2;
				}
			else samples = nsamples;
			}

		/* write CAS_EMPTY_PERIODS of silence */
		memset (samples + samples_pos, 0, CAS_EMPTY_PERIODS * CAS_PERIOD * 2);
		samples_pos += CAS_EMPTY_PERIODS * CAS_PERIOD;

		/* write CAS_HEADER_PERIODS of header (high frequency) */
		for (i=0;i<CAS_HEADER_PERIODS*4;i++)
			{
			for (n=0;n<CAS_PERIOD / 4;n++)
				samples[samples_pos + n] = (state ? 32767 : -32767);

			samples_pos += CAS_PERIOD / 4 ;
			state = !state;
			}

		while (cas_pos < caslen)
			{
			/* check if we've hit a new header (or end of block) */
			if ( (cas_pos + 8) < caslen)
				{
				if (!memcmp (casdata + cas_pos, CasHeader, 8) )
					{
					cas_pos += 8;
					break; /* falls back to loop above; plays header again */
					}
				}

			/* check if we've got enough memory for the next byte */
			size = CAS_PERIOD * 11;
			if ( (samples_pos + size) >= samples_size)
				{
				samples_size += ALLOCATE_BLOCK;
				nsamples = (INT16*) realloc (samples, samples_size * 2);
				if (!nsamples)
					{
					free (samples);
					return 2;
					}
				else samples = nsamples;
				}

			for (i=0;i<=11;i++)
				{
				if (i == 0) bit = 0;
				else if (i < 9) bit = (casdata[cas_pos] & (1 << (i - 1) ) );
				else bit = 1;

				/* write this one bit */
				for (n=0;n<(bit ? 4 : 2);n++)
					{
					size = (bit ? CAS_PERIOD / 4 : CAS_PERIOD / 2);
					for (p=0;p<size;p++)
						{
						samples[samples_pos + p] = (state ? 32767 : -32767);
						}
					state = !state;
					samples_pos += size;
					}
				}
			cas_pos++;
			}
		}

	*wavdata = samples;
	*wavlen = samples_pos;

	return 0;
	}

