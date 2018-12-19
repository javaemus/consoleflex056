/**
 * Ported to mess 0.56
 */
package mess.systems;

import static WIP.mame.memoryH.*;
import static WIP2.sound.sn76496.*;
import static WIP2.sound.sn76496H.*;
import static old.mame.inptportH.*;
import static WIP.arcadeflex.fucPtr.*;
import WIP.mame.sndintrfH.MachineSound;
import static WIP.mame.sndintrfH.SOUND_SN76496;
import static WIP2.mame.commonH.REGION_CPU1;
import old.mame.drawgfxH.rectangle;
import static old.mame.inputH.*;
import static old.mame.driverH.*;
import static mame.commonH.*;
import static mess.vidhrdw.tms9928aH.*;
import static mess.vidhrdw.tms9928a.*;
import static mess.messH.*;
import static mess.deviceH.*;
import static old.mame.cpuintrf.ignore_interrupt;
import static old.mame.cpuintrfH.PULSE_LINE;
import static old.mame.cpuintrf.*;
import static mess.machine.coleco.*;

public class coleco {

    static MemoryReadAddress coleco_readmem[]
            = {
                new MemoryReadAddress(0x0000, 0x1fff, MRA_ROM), /* COLECO.ROM */
                new MemoryReadAddress(0x6000, 0x63ff, MRA_RAM),
                new MemoryReadAddress(0x6400, 0x67ff, MRA_RAM),
                new MemoryReadAddress(0x6800, 0x6bff, MRA_RAM),
                new MemoryReadAddress(0x6c00, 0x6fff, MRA_RAM),
                new MemoryReadAddress(0x7000, 0x73ff, MRA_RAM),
                new MemoryReadAddress(0x7400, 0x77ff, MRA_RAM),
                new MemoryReadAddress(0x7800, 0x7bff, MRA_RAM),
                new MemoryReadAddress(0x7c00, 0x7fff, MRA_RAM),
                new MemoryReadAddress(0x8000, 0xffff, MRA_ROM), /* Cartridge */
                new MemoryReadAddress(-1) /* end of table */};

    static MemoryWriteAddress coleco_writemem[]
            = {
                new MemoryWriteAddress(0x0000, 0x1fff, MWA_ROM), /* COLECO.ROM */
                new MemoryWriteAddress(0x6000, 0x63ff, MWA_RAM),
                new MemoryWriteAddress(0x6400, 0x67ff, MWA_RAM),
                new MemoryWriteAddress(0x6800, 0x6bff, MWA_RAM),
                new MemoryWriteAddress(0x6c00, 0x6fff, MWA_RAM),
                new MemoryWriteAddress(0x7000, 0x73ff, MWA_RAM),
                new MemoryWriteAddress(0x7400, 0x77ff, MWA_RAM),
                new MemoryWriteAddress(0x7800, 0x7bff, MWA_RAM),
                new MemoryWriteAddress(0x7c00, 0x7fff, MWA_RAM),
                new MemoryWriteAddress(0x8000, 0xffff, MWA_ROM), /* Cartridge */
                new MemoryWriteAddress(-1) /* end of table */};

    static IOReadPort coleco_readport[]
            = {
                new IOReadPort(0xA0, 0xA0, TMS9928A_vram_r),
                new IOReadPort(0xA1, 0xA1, TMS9928A_register_r),
                new IOReadPort(0xBE, 0xBE, TMS9928A_vram_r),
                new IOReadPort(0xBF, 0xBF, TMS9928A_register_r),
                new IOReadPort(0xE0, 0xFF, coleco_paddle_r),
                new IOReadPort(-1) /* end of table */};

    static IOWritePort coleco_writeport[]
            = {
                new IOWritePort(0x80, 0x80, coleco_paddle_toggle_off),
                new IOWritePort(0x9F, 0x9F, coleco_paddle_toggle_off), /* Antarctic Adventure */
                new IOWritePort(0xA0, 0xA0, TMS9928A_vram_w),
                new IOWritePort(0xA1, 0xA1, TMS9928A_register_w),
                new IOWritePort(0xBE, 0xBE, TMS9928A_vram_w),
                new IOWritePort(0xBF, 0xBF, TMS9928A_register_w),
                new IOWritePort(0xC0, 0xC0, coleco_paddle_toggle_on),
                new IOWritePort(0xDF, 0xDF, coleco_paddle_toggle_on), /* Antarctic Adventure */
                new IOWritePort(0xE0, 0xFF, SN76496_0_w),
                new IOWritePort(-1) /* end of table */};

    static InputPortPtr input_ports_coleco = new InputPortPtr() {
        public void handler() {
            PORT_START();

            PORT_BITX(0x01, IP_ACTIVE_LOW, IPT_TILT, "0 (pad 1)", KEYCODE_0, IP_JOY_DEFAULT);
            PORT_BITX(0x02, IP_ACTIVE_LOW, IPT_TILT, "1 (pad 1)", KEYCODE_1, IP_JOY_DEFAULT);
            PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_TILT, "2 (pad 1)", KEYCODE_2, IP_JOY_DEFAULT);
            PORT_BITX(0x08, IP_ACTIVE_LOW, IPT_TILT, "3 (pad 1)", KEYCODE_3, IP_JOY_DEFAULT);
            PORT_BITX(0x10, IP_ACTIVE_LOW, IPT_TILT, "4 (pad 1)", KEYCODE_4, IP_JOY_DEFAULT);
            PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_TILT, "5 (pad 1)", KEYCODE_5, IP_JOY_DEFAULT);
            PORT_BITX(0x40, IP_ACTIVE_LOW, IPT_TILT, "6 (pad 1)", KEYCODE_6, IP_JOY_DEFAULT);
            PORT_BITX(0x80, IP_ACTIVE_LOW, IPT_TILT, "7 (pad 1)", KEYCODE_7, IP_JOY_DEFAULT);

            PORT_START();
            /* IN1 */
            PORT_BITX(0x01, IP_ACTIVE_LOW, IPT_TILT, "8 (pad 1)", KEYCODE_8, IP_JOY_DEFAULT);
            PORT_BITX(0x02, IP_ACTIVE_LOW, IPT_TILT, "9 (pad 1)", KEYCODE_9, IP_JOY_DEFAULT);
            PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_TILT, "# (pad 1)", KEYCODE_MINUS, IP_JOY_DEFAULT);
            PORT_BITX(0x08, IP_ACTIVE_LOW, IPT_TILT, ". (pad 1)", KEYCODE_EQUALS, IP_JOY_DEFAULT);
            PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_BUTTON2);
            PORT_BIT(0xB0, IP_ACTIVE_LOW, IPT_UNKNOWN);

            PORT_START();
            /* IN2 */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT);
            PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_BUTTON1);
            PORT_BIT(0xB0, IP_ACTIVE_LOW, IPT_UNKNOWN);

            PORT_START();
            /* IN3 */
            PORT_BITX(0x01, IP_ACTIVE_LOW, IPT_TILT, "0 (pad 2)", KEYCODE_0_PAD, IP_JOY_DEFAULT);
            PORT_BITX(0x02, IP_ACTIVE_LOW, IPT_TILT, "1 (pad 2)", KEYCODE_1_PAD, IP_JOY_DEFAULT);
            PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_TILT, "2 (pad 2)", KEYCODE_2_PAD, IP_JOY_DEFAULT);
            PORT_BITX(0x08, IP_ACTIVE_LOW, IPT_TILT, "3 (pad 2)", KEYCODE_3_PAD, IP_JOY_DEFAULT);
            PORT_BITX(0x10, IP_ACTIVE_LOW, IPT_TILT, "4 (pad 2)", KEYCODE_4_PAD, IP_JOY_DEFAULT);
            PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_TILT, "5 (pad 2)", KEYCODE_5_PAD, IP_JOY_DEFAULT);
            PORT_BITX(0x40, IP_ACTIVE_LOW, IPT_TILT, "6 (pad 2)", KEYCODE_6_PAD, IP_JOY_DEFAULT);
            PORT_BITX(0x80, IP_ACTIVE_LOW, IPT_TILT, "7 (pad 2)", KEYCODE_7_PAD, IP_JOY_DEFAULT);

            PORT_START();
            /* IN4 */
            PORT_BITX(0x01, IP_ACTIVE_LOW, IPT_TILT, "8 (pad 2)", KEYCODE_8_PAD, IP_JOY_DEFAULT);
            PORT_BITX(0x02, IP_ACTIVE_LOW, IPT_TILT, "9 (pad 2)", KEYCODE_9_PAD, IP_JOY_DEFAULT);
            PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_TILT, "# (pad 2)", KEYCODE_MINUS_PAD, IP_JOY_DEFAULT);
            PORT_BITX(0x08, IP_ACTIVE_LOW, IPT_TILT, ". (pad 2)", KEYCODE_PLUS_PAD, IP_JOY_DEFAULT);
            PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2);
            PORT_BIT(0xB0, IP_ACTIVE_LOW, IPT_UNKNOWN);

            PORT_START();
            /* IN5 */
            PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_PLAYER2);
            PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER2);
            PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_PLAYER2);
            PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_PLAYER2);
            PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2);
            PORT_BIT(0xB0, IP_ACTIVE_LOW, IPT_UNKNOWN);

            INPUT_PORTS_END();
        }
    };
    static SN76496interface sn76496_interface = new SN76496interface(
            1, /* 1 chip 		*/
            new int[]{3579545}, /* 3.579545 MHz */
            new int[]{100}
    );

    /**
     * *************************************************************************
     *
     * The interrupts come from the vdp. The vdp (tms9928a) interrupt can go up
     * and down; the Coleco only uses nmi interrupts (which is just a pulse).
     * They are edge-triggered: as soon as the vdp interrupt line goes up, an
     * interrupt is generated. Nothing happens when the line stays up or goes
     * down.
     *
     * To emulate this correctly, we set a callback in the tms9928a (they can
     * occur mid-frame). At every frame we call the TMS9928A_interrupt because
     * the vdp needs to know when the end-of-frame occurs, but we don't return
     * an interrupt.
     *
     **************************************************************************
     */
    public static InterruptPtr coleco_interrupt = new InterruptPtr() {
        public int handler() {
            TMS9928A_interrupt();
            return ignore_interrupt.handler();
        }
    };
    static int last_state = 0;
    public static INTCallbackPtr coleco_vdp_interrupt = new INTCallbackPtr() {
        public void handler(int state) {
            /* only if it goes up */
            if (state != 0 && last_state == 0) {
                cpu_set_nmi_line(0, PULSE_LINE);
            }
            last_state = state;
        }
    };

    public static VhStartPtr coleco_vh_start = new VhStartPtr() {
        public int handler() {
            if (TMS9928A_start(TMS99x8A, 0x4000) != 0) {
                return 1;
            }

            TMS9928A_int_callback(coleco_vdp_interrupt);
            return 0;
        }
    };

    static MachineDriver machine_driver_coleco = new MachineDriver(
            /* basic machine hardware */
            new MachineCPU[]{
                new MachineCPU(
                        CPU_Z80,
                        3579545, /* 3.579545 Mhz */
                        coleco_readmem, coleco_writemem, coleco_readport, coleco_writeport,
                        coleco_interrupt, 1
                )
            },
            60, DEFAULT_REAL_60HZ_VBLANK_DURATION, /* frames per second, vblank duration */
            1,
            null, /* init_machine */
            null, /* stop_machine */
            32 * 8, 24 * 8, new rectangle(0 * 8, 32 * 8 - 1, 0 * 8, 24 * 8 - 1),
            null, /* gfxdecodeinfo */
            TMS9928A_PALETTE_SIZE,
            TMS9928A_COLORTABLE_SIZE,
            tms9928A_init_palette,
            VIDEO_TYPE_RASTER,
            null,
            coleco_vh_start,
            TMS9928A_stop,
            TMS9928A_refresh,
            /* sound hardware */
            0, 0, 0, 0,
            new MachineSound[]{
                new MachineSound(
                        SOUND_SN76496,
                        sn76496_interface
                )
            }
    );

    /**
     * *************************************************************************
     *
     * Game driver(s)
     *
     **************************************************************************
     */
    static RomLoadPtr rom_coleco = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1, 0);
            ROM_LOAD("coleco.rom", 0x0000, 0x2000, 0x3aa93ef3);
            ROM_END();
        }
    };

    static RomLoadPtr rom_colecoa = new RomLoadPtr() {
        public void handler() {
            ROM_REGION(0x10000, REGION_CPU1, 0);
            ROM_LOAD("colecoa.rom", 0x0000, 0x2000, 0x39bb16fc);
            /* differences to 0x3aa93ef3 modified characters, added a pad 2 related fix */
            ROM_END();
        }
    };

    //ROM_START (colecofb_rom)
    //  ROM_REGIONX(0x10000,REGION_CPU1, 0)
    //  ROM_LOAD ("colecofb.rom", 0x0000, 0x2000, 0x640cf85b);/* no pause after title screen */
    //ROM_END(); 
    //ROM_START (coleconb_rom)
    //  ROM_REGIONX(0x10000,REGION_CPU1, 0)
    //  ROM_LOAD ("coleconb.rom", 0x0000, 0x2000, 0x66cda476);/* no title screen */
    //ROM_END(); 
    static IODevice io_coleco[] = {
        new IODevice(
        IO_CARTSLOT, /* type */
        1, /* count */
        "rom\0", /* file extensions */
        IO_RESET_CPU, /* reset if file changed */
        null,
        coleco_init_cart, /* init */
        null, /* exit */
        null, /* info */
        null, /* open */
        null, /* close */
        null, /* status */
        null, /* seek */
        null, /* tell */
        null, /* input */
        null, /* output */
        null, /* input_chunk */
        null /* output_chunk */
        ),
        new IODevice(IO_END)
    };

    static IODevice io_colecoa[] = {
        new IODevice(
        IO_CARTSLOT, /* type */
        1, /* count */
        "rom\0", /* file extensions */
        IO_RESET_CPU, /* reset if file changed */
        null,
        coleco_init_cart, /* init */
        null, /* exit */
        null, /* info */
        null, /* open */
        null, /* close */
        null, /* status */
        null, /* seek */
        null, /* tell */
        null, /* input */
        null, /* output */
        null, /* input_chunk */
        null /* output_chunk */
        ),
        new IODevice(IO_END)
    };


    /*    YEAR  NAME      PARENT    MACHINE   INPUT     INIT      COMPANY   FULLNAME */
    public static GameDriver driver_coleco = new GameDriver("1982", "coleco", "coleco.java", rom_coleco, null, machine_driver_coleco, input_ports_coleco, null, io_coleco, "Coleco", "Colecovision");
    public static GameDriver driver_colecoa = new GameDriver("1982", "colecoa", "coleco.java", rom_colecoa, null, machine_driver_coleco, input_ports_coleco, null, io_colecoa, "Coleco", "Colecovision (Thick Characters)");

    //#ifdef COLECO_HACKS
    //CONSX( 1982, colecofb, coleco,   coleco,   coleco,   0,        "Coleco", "Colecovision (Fast BIOS Hack)", GAME_COMPUTER_MODIFIED )
    //CONSX( 1982, coleconb, coleco,   coleco,   coleco,   0,        "Coleco", "Colecovision (NO BIOS Hack)", GAME_COMPUTER_MODIFIED )
    //#endif   
}
