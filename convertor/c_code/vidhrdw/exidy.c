/***************************************************************************

  exidy.c

  Functions to emulate the video hardware of the Exidy Sorcerer

***************************************************************************/

#include "driver.h"
#include "vidhrdw/generic.h"
#include "includes/exidy.h"

/***************************************************************************
  Start the video hardware emulation.
***************************************************************************/

int exidy_vh_start(void)
{

	return 0;
}

void    exidy_vh_stop(void)
{
}

/* two colours */
static unsigned short exidy_colour_table[EXIDY_NUM_COLOURS] =
{
	0, 1
};

/* black/white */
static unsigned char exidy_palette[EXIDY_NUM_COLOURS * 3] =
{
    0x000, 0x000, 0x000,
    0x0ff, 0x0ff, 0x0ff
};


/* Initialise the palette */
void exidy_init_palette(unsigned char *sys_palette, unsigned short *sys_colortable, const unsigned char *color_prom)
{
        memcpy(sys_palette, exidy_palette, sizeof (exidy_palette));
        memcpy(sys_colortable, exidy_colour_table, sizeof (exidy_colour_table));
}

/***************************************************************************
  Draw the game screen in the given mame_bitmap.
  Do NOT call osd_update_display() from this function,
  it will be called by the main emulation engine.
***************************************************************************/
void exidy_vh_screenrefresh(struct mame_bitmap *bitmap, int full_refresh)
{
	int x,y;
	int pens[2];

	pens[0] = Machine->pens[0];
	pens[1] = Machine->pens[1];

	for (y=0; y<EXIDY_SCREEN_HEIGHT>>3; y++)
	{
		for (x=0; x<EXIDY_SCREEN_WIDTH>>3; x++)
		{
			int cheight, cwidth;
			int char_addr;
			int ch;

			/* get char from z80 address space */
			ch = cpu_readmem16(0x0f080 + (y<<6) + x) & 0x0ff;

			/* prom at 0x0f800, user chars from 0x0fc00 */
			char_addr = 0x0f800 + (ch<<3);
		
			for (cheight=0; cheight<8; cheight++)
			{
				int byte;
				int px,py;

				/* read byte of graphics data from z80 memory */
				/* either prom or ram */
				byte = cpu_readmem16(char_addr+cheight);

				px = (x<<3);
				py = (y<<3)+cheight;
				for (cwidth=0; cwidth<8; cwidth++)
				{
					int pen;

					pen = (byte>>7) & 0x001;
					pen = pens[pen];

					plot_pixel(bitmap,px, py,pen);
					px++;
					byte = byte<<1;
				}
			}
		}
	}
}

