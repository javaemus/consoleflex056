/**********************************************************************

    speaker.h
    Sound driver to emulate a simple speaker,
    driven by one or more output bits

**********************************************************************/
#ifndef SPEAKER_H
#define SPEAKER_H
#define MAX_SPEAKER 2

#ifdef __cplusplus
extern "C" {
#endif

struct Speaker_interface
{
	int num;
	int mixing_level[MAX_SPEAKER];	/* mixing level in percent */
	int num_level[MAX_SPEAKER]; 	/* optional: number of levels (if not two) */
	INT16 *levels[MAX_SPEAKER]; 	/* optional: pointer to level lookup table */
};

void speaker_level_w (int which, int new_level);

#ifdef __cplusplus
}
#endif


#endif

