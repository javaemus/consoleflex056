/***************************************************************************

  commodore pet discrete video circuit

  PeT mess@utanet.at

***************************************************************************/

#include "driver.h"
#include "vidhrdw/generic.h"
#include "includes/state.h"

#include "includes/crtc6845.h"
#include "includes/pet.h"

void pet_vh_set_font(int font)
{
	pet_font=font;
}

void pet_vh_init (void)
{
	UINT8 *gfx = memory_region(REGION_GFX1);
	int i;

	/* inversion logic on board */
    for (i = 0; i < 0x400; i++) {
		gfx[0x800+i] = gfx[0x400+i];
		gfx[0x400+i] = gfx[i]^0xff;
		gfx[0xc00+i] = gfx[0x800+i]^0xff;
	}
}

void pet80_vh_init (void)
{
	UINT8 *gfx = memory_region(REGION_GFX1);
	int i;

	/* inversion logic on board */
    for (i = 0; i < 0x400; i++) {
		gfx[0x800+i] = gfx[0x400+i];
		gfx[0x400+i] = gfx[i]^0xff;
		gfx[0x0c00+i] = gfx[0x800+i]^0xff;
	}
	// I assume the hardware logic is not displaying line 8 and 9 of char
	// I draw it like lines would be 8-15 are black!
	for (i=511; i>=0; i--) {
		memcpy(gfx+i*16, gfx+i*8, 8);
		memset(gfx+i*16+8, 0, 8);
	}
}

void superpet_vh_init (void)
{
	UINT8 *gfx = memory_region(REGION_GFX1);
	int i;

	for (i=0; i<0x400; i++) {
		gfx[0x1000+i]=gfx[0x800+i];
		gfx[0x1800+i]=gfx[0xc00+i];
		gfx[0x1c00+i]=gfx[0x1800+i]^0xff;
		gfx[0x1400+i]=gfx[0x1000+i]^0xff;
		gfx[0x800+i]=gfx[0x400+i];
		gfx[0xc00+i]=gfx[0x800+i]^0xff;
		gfx[0x400+i]=gfx[i]^0xff;
	}
	// I assume the hardware logic is not displaying line 8 and 9 of char
	// I draw it like lines 8-15 are black!
	for (i=1023; i>=0; i--) {
		memcpy(gfx+i*16, gfx+i*8, 8);
		memset(gfx+i*16+8, 0, 8);
	}
}

//  commodore pet discrete video circuit
void pet_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh)
{
	int x, y, i;

	if (full_refresh) {
		memset(dirtybuffer, 1, videoram_size);
	}
	for (y=0, i=0; y<25;y++) {
		for (x=0;x<40;x++, i++) {
			if (dirtybuffer[i]) {
				drawgfx(bitmap,Machine->gfx[pet_font],
						videoram[i], 0, 0, 0, 8*x,8*y,
						&Machine->visible_area,TRANSPARENCY_NONE,0);
				dirtybuffer[i]=0;
			}
		}
	}

	state_display(bitmap);
}

//  commodore pet crtc video circuit for 40 columns display (standard crtc6845)
void pet40_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh)
{
	int x, y, i;
	int w=crtc6845_get_char_columns(crtc6845);
	int h=crtc6845_get_char_lines(crtc6845);
	int height=crtc6845_get_char_height(crtc6845);
	int start=crtc6845_get_start(crtc6845)&0x3ff;

	if (full_refresh||crtc6845_do_full_refresh(crtc6845)) {
		memset(dirtybuffer, 1, videoram_size);
	}
	for (y=0, i=start; y<h;y++) {
		for (x=0;x<w;x++, i=(i+1)&0x3ff) {
			if (dirtybuffer[i]) {
				drawgfx(bitmap,Machine->gfx[pet_font],
						videoram[i], 0, 0, 0, 8*x,height*y,
						&Machine->visible_area,TRANSPARENCY_NONE,0);
				dirtybuffer[i]=0;
			}
		}
	}

	state_display(bitmap);
}

// special hardware to allow crtc programmed for pet 40 column mode! to generate
// 80 column display!
void pet80_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh)
{
	int x, y, i;
	struct rectangle rect;
	int w=crtc6845_get_char_columns(crtc6845);
	int h=crtc6845_get_char_lines(crtc6845);
	int height=crtc6845_get_char_height(crtc6845);
	int start=crtc6845_get_start(crtc6845)&0x3ff;

	rect.min_x=Machine->visible_area.min_x;
	rect.max_x=Machine->visible_area.max_x;
	if (full_refresh||crtc6845_do_full_refresh(crtc6845)) {
		memset(dirtybuffer, 1, videoram_size);
	}
	for (y=0, rect.min_y=0, rect.max_y=height-1, i=start; y<h;
		 y++, rect.min_y+=height, rect.max_y+=height) {
		for (x=0; x<w; x++, i=(i+1)&0x3ff) {
			if (dirtybuffer[2*i]) {
				drawgfx(bitmap,Machine->gfx[pet_font],
						videoram[2*i], 0, 0, 0, 16*x,height*y,
						&rect,TRANSPARENCY_NONE,0);
				dirtybuffer[2*i]=0;
			}
			if (dirtybuffer[2*i+1]) {
				drawgfx(bitmap,Machine->gfx[pet_font],
						videoram[2*i+1], 0, 0, 0, 16*x+8,height*y,
						&rect,TRANSPARENCY_NONE,0);
				dirtybuffer[2*i+1]=0;
			}
		}
	}
	state_display(bitmap);
}

