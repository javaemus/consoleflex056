/**
 * Ported to mess 0.56
 */
package mess.vidhrdw;

import static WIP.arcadeflex.fucPtr.*;
import static common.libc.expressions.*;
import WIP.mame.osdependH.mame_bitmap;
import static old.arcadeflex.osdepend.logerror;
import static common.libc.cstring.*;
import static common.ptr.*;
import static WIP.mame.common.bitmap_alloc;
import static WIP.mame.mame.Machine;
import static WIP2.mess.vidhrdw.tms9928aH.TMS99x8A;
import static WIP.arcadeflex.video.osd_skip_this_frame;
import static old.mame.drawgfx.*;
import static old.mame.drawgfxH.TRANSPARENCY_NONE;
import static WIP.mame.common.*;
import old.mame.drawgfxH.rectangle;
import static old.mame.palette.palette_set_color;

public class tms9928a {

    public static abstract interface INTCallbackPtr {

        public abstract void handler(int i);
    }
    static char TMS9928A_palette[]
            = {
                0, 0, 0,
                0, 0, 0,
                33, 200, 66,
                94, 220, 120,
                84, 85, 237,
                125, 118, 252,
                212, 82, 77,
                66, 235, 245,
                252, 85, 84,
                255, 121, 120,
                212, 193, 84,
                230, 206, 128,
                33, 176, 59,
                201, 91, 186,
                204, 204, 204,
                255, 255, 255
            };

    /*
    ** Defines for `dirty' optimization
     */
    public static int MAX_DIRTY_COLOUR = (256 * 3);
    public static int MAX_DIRTY_PATTERN = (256 * 3);
    public static int MAX_DIRTY_NAME = (40 * 24);

    public static int IMAGE_SIZE = (256 * 192);/* size of rendered image        */

    public static class TMS9928A {

        /* TMS9928A internal settings */
        int u8_ReadAhead, u8_StatusReg, u8_oldStatusReg, u8_FirstByte, u8_latch, u8_INT;
        int[] u8_Regs = new int[8];
        int Addr, BackColour, Change, mode;
        int colour, pattern, nametbl, spriteattribute, spritepattern;
        int colourmask, patternmask;
        INTCallbackPtr INTCallback;
        /* memory */
        UBytePtr vMem, dBackMem;
        mame_bitmap tmpbmp;
        int vramsize, model;
        /* emulation settings */
        int LimitSprites;/* max 4 sprites on a row, like original TMS9918A */
 /* dirty tables */
        char anyDirtyColour, anyDirtyName, anyDirtyPattern;
        char[] DirtyColour, DirtyName, DirtyPattern;
    };

    static TMS9928A tms = new TMS9928A();

    /**
     ** initialize the palette
     */
    public static VhConvertColorPromPtr tms9928A_init_palette = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            memcpy(palette, TMS9928A_palette, sizeof(TMS9928A_palette));
        }
    };

    /**
     ** The init, reset and shutdown functions
     */
    public static void TMS9928A_reset() {
        int i;

        for (i = 0; i < 8; i++) {
            tms.u8_Regs[i] = 0;
        }
        tms.u8_StatusReg = 0;
        tms.nametbl = tms.pattern = tms.colour = 0;
        tms.spritepattern = tms.spriteattribute = 0;
        tms.colourmask = tms.patternmask = 0;
        tms.Addr = tms.u8_ReadAhead = tms.u8_INT = 0;
        tms.mode = tms.BackColour = 0;
        tms.Change = 1;
        tms.u8_FirstByte = 0;
        tms.u8_latch = 0;
        _TMS9928A_set_dirty(1);
    }

    public static int TMS9928A_start(int model, /*unsigned*/ int vram) {
        /* 4 or 16 kB vram please */
        if (!((vram == 0x1000) || (vram == 0x4000) || (vram == 0x2000))) {
            return 1;
        }

        tms.model = model;

        /* Video RAM */
        tms.vramsize = vram;
        tms.vMem = new UBytePtr(vram);
        if (tms.vMem == null) {
            return (1);
        }
        memset(tms.vMem, 0, vram);

        /* Sprite back buffer */
        tms.dBackMem = new UBytePtr(IMAGE_SIZE);
        if (tms.dBackMem == null) {
            tms.vMem = null;
            return 1;
        }

        /* dirty buffers */
        tms.DirtyName = new char[MAX_DIRTY_NAME];
        if (tms.DirtyName == null) {
            tms.vMem = null;
            tms.dBackMem = null;
            return 1;
        }

        tms.DirtyPattern = new char[MAX_DIRTY_PATTERN];
        if (tms.DirtyPattern == null) {
            tms.vMem = null;
            tms.DirtyName = null;
            tms.dBackMem = null;
            return 1;
        }

        tms.DirtyColour = new char[MAX_DIRTY_COLOUR];
        if (tms.DirtyColour == null) {
            tms.vMem = null;
            tms.DirtyName = null;
            tms.DirtyPattern = null;
            tms.dBackMem = null;
            return 1;
        }

        /* back bitmap */
        tms.tmpbmp = bitmap_alloc(256, 192);
        if (tms.tmpbmp == null) {
            tms.vMem = null;
            tms.dBackMem = null;
            tms.DirtyName = null;
            tms.DirtyPattern = null;
            tms.DirtyColour = null;
            return 1;
        }

        TMS9928A_reset();
        tms.LimitSprites = 1;
        /*TODO*///	state_save_register_UINT8 ("tms9928a", 0, "R0", &tms.Regs[0], 1);
/*TODO*///	state_save_register_UINT8 ("tms9928a", 0, "R1", &tms.Regs[1], 1);
/*TODO*///	state_save_register_UINT8 ("tms9928a", 0, "R2", &tms.Regs[2], 1);
/*TODO*///	state_save_register_UINT8 ("tms9928a", 0, "R3", &tms.Regs[3], 1);
/*TODO*///	state_save_register_UINT8 ("tms9928a", 0, "R4", &tms.Regs[4], 1);
/*TODO*///	state_save_register_UINT8 ("tms9928a", 0, "R5", &tms.Regs[5], 1);
/*TODO*///	state_save_register_UINT8 ("tms9928a", 0, "R6", &tms.Regs[6], 1);
/*TODO*///	state_save_register_UINT8 ("tms9928a", 0, "R7", &tms.Regs[7], 1);
/*TODO*///	state_save_register_UINT8 ("tms9928a", 0, "S", &tms.StatusReg, 1);
/*TODO*///	state_save_register_UINT8 ("tms9928a", 0, "read_ahead", &tms.ReadAhead, 1);
/*TODO*///	state_save_register_UINT8 ("tms9928a", 0, "first_byte", &tms.FirstByte, 1);
/*TODO*///	state_save_register_UINT8 ("tms9928a", 0, "latch", &tms.latch, 1);
/*TODO*///	state_save_register_UINT16 ("tms9928a", 0, "vram_latch", (UINT16*)&tms.Addr, 1);
/*TODO*///	state_save_register_UINT8 ("tms9928a", 0, "interrupt_line", &tms.INT, 1);
/*TODO*///	state_save_register_UINT8 ("tms9928a", 0, "VRAM", tms.vMem, vram);
/*TODO*///
        return 0;
    }

    /*TODO*///
/*TODO*///void TMS9928A_post_load (void) {
/*TODO*///	int i;
/*TODO*///
/*TODO*///	/* mark the screen as dirty */
/*TODO*///	_TMS9928A_set_dirty (1);
/*TODO*///
/*TODO*///	/* all registers need to be re-written, so tables are recalculated */
/*TODO*///	for (i=0;i<8;i++)
/*TODO*///		_TMS9928A_change_register (i, tms.Regs[i]);
/*TODO*///
/*TODO*///	/* make sure the back ground colour is reset */
/*TODO*///	tms.BackColour = -1;
/*TODO*///
/*TODO*///	/* make sure the interrupt request is set properly */
/*TODO*///	if (tms.INTCallback) tms.INTCallback (tms.INT);
/*TODO*///}
    public static VhStopPtr TMS9928A_stop = new VhStopPtr() {
        public void handler() {
            tms.vMem = null;
            tms.dBackMem = null;
            tms.DirtyColour = null;
            tms.DirtyName = null;
            tms.DirtyPattern = null;
            bitmap_free(tms.tmpbmp);
            tms.tmpbmp = null;
        }
    };

    /**
     ** Set all dirty / clean
     */
    static void _TMS9928A_set_dirty(int dirty) {
        tms.anyDirtyColour = tms.anyDirtyName = tms.anyDirtyPattern = (char) dirty;
        memset(tms.DirtyName, dirty, MAX_DIRTY_NAME);
        memset(tms.DirtyColour, dirty, MAX_DIRTY_COLOUR);
        memset(tms.DirtyPattern, dirty, MAX_DIRTY_PATTERN);
    }

    /**
     ** The I/O functions.
     */
    public static ReadHandlerPtr TMS9928A_vram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            int/*UINT8*/ b;
            b = tms.u8_ReadAhead & 0xFF;
            tms.u8_ReadAhead = tms.vMem.read(tms.Addr);
            tms.Addr = (tms.Addr + 1) & (tms.vramsize - 1);
            tms.u8_latch = 0;
            return b & 0xFF;
        }
    };

    public static WriteHandlerPtr TMS9928A_vram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int i;

            if (tms.vMem.read(tms.Addr) != data) {
                tms.vMem.write(tms.Addr, data);
                tms.Change = 1;
                /* dirty optimization */
                if ((tms.Addr >= tms.nametbl)
                        && (tms.Addr < (tms.nametbl + MAX_DIRTY_NAME))) {
                    tms.DirtyName[tms.Addr - tms.nametbl] = 1;
                    tms.anyDirtyName = 1;
                }

                i = (tms.Addr - tms.colour) >> 3;
                if ((i >= 0) && (i < MAX_DIRTY_COLOUR)) {
                    tms.DirtyColour[i] = 1;
                    tms.anyDirtyColour = 1;
                }

                i = (tms.Addr - tms.pattern) >> 3;
                if ((i >= 0) && (i < MAX_DIRTY_PATTERN)) {
                    tms.DirtyPattern[i] = 1;
                    tms.anyDirtyPattern = 1;
                }
            }
            tms.Addr = (tms.Addr + 1) & (tms.vramsize - 1);
            tms.u8_ReadAhead = data & 0xFF;
            tms.u8_latch = 0;
        }
    };
    public static ReadHandlerPtr TMS9928A_register_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            int/*UINT8*/ b;
            b = tms.u8_StatusReg & 0xff;
            tms.u8_StatusReg = 0x1f;
            if (tms.u8_INT != 0) {
                tms.u8_INT = 0;
                if (tms.INTCallback != null) {
                    tms.INTCallback.handler(tms.u8_INT);
                }
            }
            tms.u8_latch = 0;
            return b;
        }
    };

    public static WriteHandlerPtr TMS9928A_register_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int reg;

            if (tms.u8_latch != 0) {
                if ((data & 0x80) != 0) {
                    /* register write */
                    reg = data & 7;
                    if (tms.u8_FirstByte != tms.u8_Regs[reg]) {
                        _TMS9928A_change_register(reg, tms.u8_FirstByte & 0xFF);
                    }
                } else {
                    /* set read/write address */
                    tms.Addr = ((char) data << 8 | tms.u8_FirstByte) & (tms.vramsize - 1);
                    if ((data & 0x40) == 0) {
                        /* read ahead */
                        TMS9928A_vram_r.handler(0);
                    }
                }
                tms.u8_latch = 0;
            } else {
                tms.u8_FirstByte = data & 0xFF;
                tms.u8_latch = 1;
            }
        }
    };

    static int Mask[]
            = {0x03, 0xfb, 0x0f, 0xff, 0x07, 0x7f, 0x07, 0xff};
    static String modes[] = {
        "Mode 0 (GRAPHIC 1)", "Mode 1 (TEXT 1)", "Mode 2 (GRAPHIC 2)",
        "Mode 1+2 (TEXT 1 variation)", "Mode 3 (MULTICOLOR)",
        "Mode 1+3 (BOGUS)", "Mode 2+3 (MULTICOLOR variation)",
        "Mode 1+2+3 (BOGUS)"};

    static void _TMS9928A_change_register(int reg, int/*UINT8*/ u8_val) {
        int/*UINT8*/ u8_b;
        int mode;

        u8_val = (u8_val & Mask[reg]) & 0xFF;
        tms.u8_Regs[reg] = u8_val;

        logerror("TMS9928A: Reg %d = %02xh\n", reg, (int) u8_val);
        tms.Change = 1;
        switch (reg) {
            case 0:
                if ((u8_val & 2) != 0) {
                    /* re-calculate masks and pattern generator & colour */
                    if ((u8_val & 2) != 0) {
                        tms.colour = ((tms.u8_Regs[3] & 0x80) * 64) & (tms.vramsize - 1);
                        tms.colourmask = (tms.u8_Regs[3] & 0x7f) * 8 | 7;
                        tms.pattern = ((tms.u8_Regs[4] & 4) * 2048) & (tms.vramsize - 1);
                        tms.patternmask = (tms.u8_Regs[4] & 3) * 256
                                | (tms.colourmask & 255);
                    } else {
                        tms.colour = (tms.u8_Regs[3] * 64) & (tms.vramsize - 1);
                        tms.pattern = (tms.u8_Regs[4] * 2048) & (tms.vramsize - 1);
                    }
                    tms.mode = ((tms.model == TMS99x8A ? (tms.u8_Regs[0] & 2) : 0) | ((tms.u8_Regs[1] & 0x10) >> 4) | ((tms.u8_Regs[1] & 8) >> 1));
                    logerror("TMS9928A: %s\n", modes[tms.mode]);
                    _TMS9928A_set_dirty(1);
                }
                break;
            case 1:
                /* check for changes in the INT line */
                u8_b = ((u8_val & 0x20) != 0 && (tms.u8_StatusReg & 0x80) != 0) ? 1 : 0;
                if (u8_b != tms.u8_INT) {
                    tms.u8_INT = u8_b & 0xFF;
                    if (tms.INTCallback != null) {
                        tms.INTCallback.handler(tms.u8_INT);
                    }
                }
                mode = ((tms.model == TMS99x8A ? (tms.u8_Regs[0] & 2) : 0) | ((tms.u8_Regs[1] & 0x10) >> 4) | ((tms.u8_Regs[1] & 8) >> 1));
                if (tms.mode != mode) {
                    tms.mode = mode;
                    _TMS9928A_set_dirty(1);
                    logerror("TMS9928A: %s\n", modes[tms.mode]);
                }
                break;
            case 2:
                tms.nametbl = (u8_val * 1024) & (tms.vramsize - 1);
                tms.anyDirtyName = 1;
                memset(tms.DirtyName, 1, MAX_DIRTY_NAME);
                break;
            case 3:
                if ((tms.u8_Regs[0] & 2) != 0) {
                    tms.colour = ((u8_val & 0x80) * 64) & (tms.vramsize - 1);
                    tms.colourmask = (u8_val & 0x7f) * 8 | 7;
                } else {
                    tms.colour = (u8_val * 64) & (tms.vramsize - 1);
                }
                tms.anyDirtyColour = 1;
                memset(tms.DirtyColour, 1, MAX_DIRTY_COLOUR);
                break;
            case 4:
                if ((tms.u8_Regs[0] & 2) != 0) {
                    tms.pattern = ((u8_val & 4) * 2048) & (tms.vramsize - 1);
                    tms.patternmask = (u8_val & 3) * 256 | 255;
                } else {
                    tms.pattern = (u8_val * 2048) & (tms.vramsize - 1);
                }
                tms.anyDirtyPattern = 1;
                memset(tms.DirtyPattern, 1, MAX_DIRTY_PATTERN);
                break;
            case 5:
                tms.spriteattribute = (u8_val * 128) & (tms.vramsize - 1);
                break;
            case 6:
                tms.spritepattern = (u8_val * 2048) & (tms.vramsize - 1);
                break;
            case 7:
                /* The backdrop is updated at TMS9928A_refresh() */
                tms.anyDirtyColour = 1;
                memset(tms.DirtyColour, 1, MAX_DIRTY_COLOUR);
                break;
        }
    }

    /**
     ** Interface functions
     */
    public static void TMS9928A_int_callback(INTCallbackPtr callback) {
        tms.INTCallback = callback;
    }

    public static void TMS9928A_set_spriteslimit(int limit) {
        tms.LimitSprites = limit;
    }

    /**
     ** Updates the screen (the dMem memory area).
     */
    public static VhUpdatePtr TMS9928A_refresh = new VhUpdatePtr() {
        public void handler(mame_bitmap bmp, int full_refresh) {
            int c;

            if (tms.Change != 0) {
                c = tms.u8_Regs[7] & 15;
                if (c == 0) {
                    c = 1;
                }
                if (tms.BackColour != c) {
                    tms.BackColour = c;
                    palette_set_color(0,
                            TMS9928A_palette[c * 3], TMS9928A_palette[c * 3 + 1],
                            TMS9928A_palette[c * 3 + 2]);
                }
            }

            if (full_refresh != 0) {
                _TMS9928A_set_dirty(1);
                tms.Change = 1;
            }

            if (tms.Change != 0 || full_refresh != 0) {
                if ((tms.u8_Regs[1] & 0x40) == 0) {
                    fillbitmap(bmp, Machine.pens[tms.BackColour],
                            Machine.visible_area);
                } else {
                    if (tms.Change != 0) {
                        ModeHandlers[tms.mode].handler(tms.tmpbmp);
                    }
                    copybitmap(bmp, tms.tmpbmp, 0, 0, 0, 0,
                            Machine.visible_area, TRANSPARENCY_NONE, 0);
                    if (((tms.u8_Regs[1] & 0x50) == 0x40)) {
                        _TMS9928A_sprites(bmp);
                    }
                }
            } else {
                tms.u8_StatusReg = tms.u8_oldStatusReg & 0xFF;
            }

            /* store Status register, so it can be restored at the next frame
       if there are no changes (sprite collision bit is lost) */
            tms.u8_oldStatusReg = tms.u8_StatusReg & 0xFF;
            tms.Change = 0;
        }
    };

    public static int TMS9928A_interrupt() {
        int b;

        /* when skipping frames, calculate sprite collision */
        if (osd_skip_this_frame() != 0) {
            if (tms.Change != 0) {
                if (((tms.u8_Regs[1] & 0x50) == 0x40)) {
                    _TMS9928A_sprites(null);
                }
            } else {
                tms.u8_StatusReg = tms.u8_oldStatusReg;
            }
        }
        tms.u8_StatusReg |= 0x80;
        b = (tms.u8_Regs[1] & 0x20) != 0 ? 1 : 0;
        if (b != tms.u8_INT) {
            tms.u8_INT = b & 0xFF;
            if (tms.INTCallback != null) {
                tms.INTCallback.handler(tms.u8_INT);
            }
        }

        return b;
    }

    public static ModeHandlersPtr _TMS9928A_mode1 = new ModeHandlersPtr() {
        public void handler(mame_bitmap bmp) {
            int pattern, x, y, yy, xx, name, charcode;
            int/*UINT8*/ u8_fg, u8_bg;
            UBytePtr patternptr;
            rectangle rt = new rectangle();

            if (!(tms.anyDirtyColour != 0 || tms.anyDirtyName != 0 || tms.anyDirtyPattern != 0)) {
                return;
            }

            u8_fg = Machine.pens[tms.u8_Regs[7] / 16] & 0xFF;
            u8_bg = Machine.pens[tms.u8_Regs[7] & 15] & 0xFF;

            if (tms.anyDirtyColour != 0) {
                /* colours at sides must be reset */
                rt.min_y = 0;
                rt.max_y = 191;
                rt.min_x = 0;
                rt.max_x = 7;
                fillbitmap(bmp, u8_bg, rt);
                rt.min_y = 0;
                rt.max_y = 191;
                rt.min_x = 248;
                rt.max_x = 255;
                fillbitmap(bmp, u8_bg, rt);
            }

            name = 0;
            for (y = 0; y < 24; y++) {
                for (x = 0; x < 40; x++) {
                    charcode = tms.vMem.read(tms.nametbl + name);
                    if (!(tms.DirtyName[name++] != 0 || tms.DirtyPattern[charcode] != 0)
                            && tms.anyDirtyColour == 0) {
                        continue;
                    }
                    patternptr = new UBytePtr(tms.vMem, tms.pattern + (charcode * 8));
                    for (yy = 0; yy < 8; yy++) {
                        pattern = patternptr.readinc();
                        for (xx = 0; xx < 6; xx++) {
                            plot_pixel.handler(bmp, 8 + x * 6 + xx, y * 8 + yy,
                                    (pattern & 0x80) != 0 ? u8_fg : u8_bg);
                            pattern *= 2;
                        }
                    }
                }
            }
            _TMS9928A_set_dirty(0);
        }
    };
    public static ModeHandlersPtr _TMS9928A_mode12 = new ModeHandlersPtr() {
        public void handler(mame_bitmap bmp) {
            int pattern, x, y, yy, xx, name, charcode;
            int/*UINT8*/ u8_fg, u8_bg;
            UBytePtr patternptr;
            rectangle rt = new rectangle();

            if (!(tms.anyDirtyColour != 0 || tms.anyDirtyName != 0 || tms.anyDirtyPattern != 0)) {
                return;
            }

            u8_fg = Machine.pens[tms.u8_Regs[7] / 16] & 0xFF;
            u8_bg = Machine.pens[tms.u8_Regs[7] & 15] & 0xFF;

            if ((tms.anyDirtyColour) != 0) {
                /* colours at sides must be reset */
                rt.min_y = 0;
                rt.max_y = 191;
                rt.min_x = 0;
                rt.max_x = 7;
                fillbitmap(bmp, u8_bg, rt);
                rt.min_y = 0;
                rt.max_y = 191;
                rt.min_x = 248;
                rt.max_x = 255;
                fillbitmap(bmp, u8_bg, rt);
            }

            name = 0;
            for (y = 0; y < 24; y++) {
                for (x = 0; x < 40; x++) {
                    charcode = (tms.vMem.read(tms.nametbl + name) + (y / 8) * 256) & tms.patternmask;
                    if (!(tms.DirtyName[name++] != 0 || tms.DirtyPattern[charcode] != 0)
                            && tms.anyDirtyColour == 0) {
                        continue;
                    }
                    patternptr = new UBytePtr(tms.vMem, tms.pattern + (charcode * 8));
                    for (yy = 0; yy < 8; yy++) {
                        pattern = patternptr.readinc();
                        for (xx = 0; xx < 6; xx++) {
                            plot_pixel.handler(bmp, 8 + x * 6 + xx, y * 8 + yy,
                                    (pattern & 0x80) != 0 ? u8_fg : u8_bg);
                            pattern *= 2;
                        }
                    }
                }
            }
            _TMS9928A_set_dirty(0);
        }
    };
    public static ModeHandlersPtr _TMS9928A_mode0 = new ModeHandlersPtr() {
        public void handler(mame_bitmap bmp) {
            int pattern, x, y, yy, xx, name, charcode, colour;
            int /*UINT8*/ u8_fg, u8_bg;
            UBytePtr patternptr;

            name = 0;
            for (y = 0; y < 24; y++) {
                for (x = 0; x < 32; x++) {
                    charcode = tms.vMem.read(tms.nametbl + name);
                    if (!(tms.DirtyName[name++] != 0 || tms.DirtyPattern[charcode] != 0
                            || tms.DirtyColour[charcode / 64] != 0)) {
                        continue;
                    }
                    patternptr = new UBytePtr(tms.vMem, tms.pattern + charcode * 8);
                    colour = tms.vMem.read(tms.colour + charcode / 8);
                    u8_fg = Machine.pens[colour / 16] & 0xFF;
                    u8_bg = Machine.pens[colour & 15] & 0xFF;
                    for (yy = 0; yy < 8; yy++) {
                        pattern = patternptr.readinc();
                        for (xx = 0; xx < 8; xx++) {
                            plot_pixel.handler(bmp, x * 8 + xx, y * 8 + yy,
                                    (pattern & 0x80) != 0 ? u8_fg : u8_bg);
                            pattern *= 2;
                        }
                    }
                }
            }
            _TMS9928A_set_dirty(0);
        }
    };

    public static ModeHandlersPtr _TMS9928A_mode2 = new ModeHandlersPtr() {
        public void handler(mame_bitmap bmp) {
            int colour, name, x, y, yy, pattern, xx, charcode;
            int/*UINT8*/ u8_fg, u8_bg;
            UBytePtr colourptr;
            UBytePtr patternptr;

            if (!(tms.anyDirtyColour != 0 || tms.anyDirtyName != 0 || tms.anyDirtyPattern != 0)) {
                return;
            }

            name = 0;
            for (y = 0; y < 24; y++) {
                for (x = 0; x < 32; x++) {
                    charcode = tms.vMem.read(tms.nametbl + name) + (y / 8) * 256;
                    colour = (charcode & tms.colourmask);
                    pattern = (charcode & tms.patternmask);
                    if (!(tms.DirtyName[name++] != 0 || tms.DirtyPattern[pattern] != 0
                            || tms.DirtyColour[colour] != 0)) {
                        continue;
                    }
                    patternptr = new UBytePtr(tms.vMem, tms.pattern + colour * 8);
                    colourptr = new UBytePtr(tms.vMem, tms.colour + pattern * 8);
                    for (yy = 0; yy < 8; yy++) {
                        pattern = patternptr.readinc();
                        colour = colourptr.readinc();
                        u8_fg = Machine.pens[colour / 16] & 0xFF;
                        u8_bg = Machine.pens[colour & 15] & 0xFF;
                        for (xx = 0; xx < 8; xx++) {
                            plot_pixel.handler(bmp, x * 8 + xx, y * 8 + yy,
                                    (pattern & 0x80) != 0 ? u8_fg : u8_bg);
                            pattern *= 2;
                        }
                    }
                }
            }
            _TMS9928A_set_dirty(0);
        }
    };
    public static ModeHandlersPtr _TMS9928A_mode3 = new ModeHandlersPtr() {
        public void handler(mame_bitmap bmp) {
            int x, y, yy, yyy, name, charcode;
            int /*UINT8*/ u8_fg, u8_bg;
            UBytePtr patternptr;

            if (!(tms.anyDirtyColour != 0 || tms.anyDirtyName != 0 || tms.anyDirtyPattern != 0)) {
                return;
            }

            name = 0;
            for (y = 0; y < 24; y++) {
                for (x = 0; x < 32; x++) {
                    charcode = tms.vMem.read(tms.nametbl + name);
                    if (!(tms.DirtyName[name++] != 0 || tms.DirtyPattern[charcode] != 0)
                            && tms.anyDirtyColour == 0) {
                        continue;
                    }
                    patternptr = new UBytePtr(tms.vMem, tms.pattern + charcode * 8 + (y & 3) * 2);
                    for (yy = 0; yy < 2; yy++) {
                        u8_fg = Machine.pens[(patternptr.read() / 16)] & 0xFF;
                        u8_bg = Machine.pens[((patternptr.readinc()) & 15)] & 0xFF;
                        for (yyy = 0; yyy < 4; yyy++) {
                            plot_pixel.handler(bmp, x * 8 + 0, y * 8 + yy * 4 + yyy, u8_fg);
                            plot_pixel.handler(bmp, x * 8 + 1, y * 8 + yy * 4 + yyy, u8_fg);
                            plot_pixel.handler(bmp, x * 8 + 2, y * 8 + yy * 4 + yyy, u8_fg);
                            plot_pixel.handler(bmp, x * 8 + 3, y * 8 + yy * 4 + yyy, u8_fg);
                            plot_pixel.handler(bmp, x * 8 + 4, y * 8 + yy * 4 + yyy, u8_bg);
                            plot_pixel.handler(bmp, x * 8 + 5, y * 8 + yy * 4 + yyy, u8_bg);
                            plot_pixel.handler(bmp, x * 8 + 6, y * 8 + yy * 4 + yyy, u8_bg);
                            plot_pixel.handler(bmp, x * 8 + 7, y * 8 + yy * 4 + yyy, u8_bg);
                        }
                    }
                }
            }
            _TMS9928A_set_dirty(0);
        }
    };
    public static ModeHandlersPtr _TMS9928A_mode23 = new ModeHandlersPtr() {
        public void handler(mame_bitmap bmp) {
            int x, y, yy, yyy, name, charcode;
            int /*UINT8*/ u8_fg, u8_bg;
            UBytePtr patternptr;

            if (!(tms.anyDirtyColour != 0 || tms.anyDirtyName != 0 || tms.anyDirtyPattern != 0)) {
                return;
            }

            name = 0;
            for (y = 0; y < 24; y++) {
                for (x = 0; x < 32; x++) {
                    charcode = tms.vMem.read(tms.nametbl + name);
                    if (!(tms.DirtyName[name++] != 0 || tms.DirtyPattern[charcode] != 0)
                            && tms.anyDirtyColour == 0) {
                        continue;
                    }
                    patternptr = new UBytePtr(tms.vMem, tms.pattern
                            + ((charcode + (y & 3) * 2 + (y / 8) * 256) & tms.patternmask) * 8);
                    for (yy = 0; yy < 2; yy++) {
                        u8_fg = Machine.pens[(patternptr.read() / 16)] & 0xFF;
                        u8_bg = Machine.pens[((patternptr.readinc()) & 15)] & 0xFF;
                        for (yyy = 0; yyy < 4; yyy++) {
                            plot_pixel.handler(bmp, x * 8 + 0, y * 8 + yy * 4 + yyy, u8_fg);
                            plot_pixel.handler(bmp, x * 8 + 1, y * 8 + yy * 4 + yyy, u8_fg);
                            plot_pixel.handler(bmp, x * 8 + 2, y * 8 + yy * 4 + yyy, u8_fg);
                            plot_pixel.handler(bmp, x * 8 + 3, y * 8 + yy * 4 + yyy, u8_fg);
                            plot_pixel.handler(bmp, x * 8 + 4, y * 8 + yy * 4 + yyy, u8_bg);
                            plot_pixel.handler(bmp, x * 8 + 5, y * 8 + yy * 4 + yyy, u8_bg);
                            plot_pixel.handler(bmp, x * 8 + 6, y * 8 + yy * 4 + yyy, u8_bg);
                            plot_pixel.handler(bmp, x * 8 + 7, y * 8 + yy * 4 + yyy, u8_bg);
                        }
                    }
                }
            }
            _TMS9928A_set_dirty(0);
        }
    };
    public static ModeHandlersPtr _TMS9928A_modebogus = new ModeHandlersPtr() {
        public void handler(mame_bitmap bmp) {
            int/*UINT8*/ u8_fg, u8_bg;
            int x, y, n, xx;

            if (!(tms.anyDirtyColour != 0 || tms.anyDirtyName != 0 || tms.anyDirtyPattern != 0)) {
                return;
            }

            u8_fg = Machine.pens[tms.u8_Regs[7] / 16] & 0xFF;
            u8_bg = Machine.pens[tms.u8_Regs[7] & 15] & 0xFF;

            for (y = 0; y < 192; y++) {
                xx = 0;
                n = 8;
                while (n-- != 0) {
                    plot_pixel.handler(bmp, xx++, y, u8_bg);
                }
                for (x = 0; x < 40; x++) {
                    n = 4;
                    while (n-- != 0) {
                        plot_pixel.handler(bmp, xx++, y, u8_fg);
                    }
                    n = 2;
                    while (n-- != 0) {
                        plot_pixel.handler(bmp, xx++, y, u8_bg);
                    }
                }
                n = 8;
                while (n-- != 0) {
                    plot_pixel.handler(bmp, xx++, y, u8_bg);
                }
            }

            _TMS9928A_set_dirty(0);
        }
    };

    /**
     ** This function renders the sprites. Sprite collision is calculated in *
     * in a back buffer (tms.dBackMem), because sprite collision detection * is
     * rather complicated (transparent sprites also cause the sprite * collision
     * bit to be set, and ``illegal'' sprites do not count * (they're not
     * displayed)). * * This code should be optimized. One day.
     */
    static void _TMS9928A_sprites(mame_bitmap bmp) {
        UBytePtr attributeptr;
        UBytePtr patternptr;
        int u8_c;
        int p, x, y, size, i, j, large, yy, xx, illegalsprite, illegalspriteline;
        int[] limit = new int[192];
        char line, line2;

        attributeptr = new UBytePtr(tms.vMem, tms.spriteattribute);
        size = (tms.u8_Regs[1] & 2) != 0 ? 16 : 8;
        large = (int) (tms.u8_Regs[1] & 1);

        for (x = 0; x < 192; x++) {
            limit[x] = 4;
        }
        tms.u8_StatusReg = 0x80;
        illegalspriteline = 255;
        illegalsprite = 0;

        memset(tms.dBackMem, 0, IMAGE_SIZE);
        for (p = 0; p < 32; p++) {
            y = attributeptr.readinc();
            if (y == 208) {
                break;
            }
            if (y > 208) {
                y = -(~y & 255);
            } else {
                y++;
            }
            x = attributeptr.readinc();
            patternptr = new UBytePtr(tms.vMem, tms.spritepattern
                    + ((size == 16) ? attributeptr.read() & 0xfc : attributeptr.read()) * 8);
            attributeptr.inc();
            u8_c = (attributeptr.read() & 0x0f);
            if ((attributeptr.read() & 0x80) != 0) {
                x -= 32;
            }
            attributeptr.inc();

            if (large == 0) {
                /* draw sprite (not enlarged) */
                for (yy = y; yy < (y + size); yy++) {
                    if ((yy < 0) || (yy > 191)) {
                        continue;
                    }
                    if (limit[yy] == 0) {
                        /* illegal sprite line */
                        if (yy < illegalspriteline) {
                            illegalspriteline = yy;
                            illegalsprite = p;
                        } else if (illegalspriteline == yy) {
                            if (illegalsprite > p) {
                                illegalsprite = p;
                            }
                        }
                        if (tms.LimitSprites != 0) {
                            continue;
                        }
                    } else {
                        limit[yy]--;
                    }
                    line = (char) (256 * patternptr.read(yy - y) + patternptr.read(yy - y + 16));
                    for (xx = x; xx < (x + size); xx++) {
                        if ((line & 0x8000) != 0) {
                            if ((xx >= 0) && (xx < 256)) {
                                if (tms.dBackMem.read(yy * 256 + xx) != 0) {
                                    tms.u8_StatusReg |= 0x20;
                                } else {
                                    tms.dBackMem.write(yy * 256 + xx, 0x01);
                                }
                                if (u8_c != 0 && (tms.dBackMem.read(yy * 256 + xx) & 0x02) == 0) {
                                    tms.dBackMem.or(yy * 256 + xx, 0x02);
                                    if (bmp != null) {
                                        plot_pixel.handler(bmp, xx, yy, Machine.pens[u8_c]);
                                    }
                                }
                            }
                        }
                        line *= 2;
                    }
                }
            } else {
                /* draw enlarged sprite */
                for (i = 0; i < size; i++) {
                    yy = y + i * 2;
                    line2 = (char) (256 * patternptr.read(i) + patternptr.read(i + 16));
                    for (j = 0; j < 2; j++) {
                        if ((yy >= 0) && (yy <= 191)) {
                            if (limit[yy] == 0) {
                                /* illegal sprite line */
                                if (yy < illegalspriteline) {
                                    illegalspriteline = yy;
                                    illegalsprite = p;
                                } else if (illegalspriteline == yy) {
                                    if (illegalsprite > p) {
                                        illegalsprite = p;
                                    }
                                }
                                if (tms.LimitSprites != 0) {
                                    continue;
                                }
                            } else {
                                limit[yy]--;
                            }
                            line = line2;
                            for (xx = x; xx < (x + size * 2); xx += 2) {
                                if ((line & 0x8000) != 0) {
                                    if ((xx >= 0) && (xx < 256)) {
                                        if (tms.dBackMem.read(yy * 256 + xx) != 0) {
                                            tms.u8_StatusReg |= 0x20;
                                        } else {
                                            tms.dBackMem.write(yy * 256 + xx, 0x01);
                                        }
                                        if (u8_c != 0 && (tms.dBackMem.read(yy * 256 + xx) & 0x02) == 0) {
                                            tms.dBackMem.or(yy * 256 + xx, 0x02);
                                            if (bmp != null) {
                                                plot_pixel.handler(bmp, xx, yy, Machine.pens[u8_c]);
                                            }
                                        }
                                    }
                                    if (((xx + 1) >= 0) && ((xx + 1) < 256)) {
                                        if (tms.dBackMem.read(yy * 256 + xx + 1) != 0) {
                                            tms.u8_StatusReg |= 0x20;
                                        } else {
                                            tms.dBackMem.write(yy * 256 + xx + 1, 0x01);
                                        }
                                        if (u8_c != 0 && (tms.dBackMem.read(yy * 256 + xx + 1) & 0x02) == 0) {
                                            tms.dBackMem.or(yy * 256 + xx + 1, 0x02);
                                            if (bmp != null) {
                                                plot_pixel.handler(bmp, xx + 1, yy, Machine.pens[u8_c]);
                                            }
                                        }
                                    }
                                }
                                line *= 2;
                            }
                        }
                        yy++;
                    }
                }
            }
        }
        if (illegalspriteline == 255) {
            tms.u8_StatusReg |= (p > 31) ? 31 : p;
        } else {
            tms.u8_StatusReg |= 0x40 + illegalsprite;
        }
    }
    static ModeHandlersPtr[] ModeHandlers = {
        _TMS9928A_mode0, _TMS9928A_mode1, _TMS9928A_mode2, _TMS9928A_mode12,
        _TMS9928A_mode3, _TMS9928A_modebogus, _TMS9928A_mode23,
        _TMS9928A_modebogus
    };

    public static abstract interface ModeHandlersPtr {

        public abstract void handler(mame_bitmap bmp);
    }
}
