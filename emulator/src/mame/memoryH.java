/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mame;

import static WIP.arcadeflex.fucPtr.*;
import static common.ptr.*;
import static old.mame.cpuintrf.*;
import static old.mame.cpuintrfH.*;

/**
 *
 * @author jagsanchez
 */
public class memoryH {
    /***************************************************************************

	memory.h

	Functions which handle the CPU memory and I/O port access.

    ***************************************************************************/

    /*
     * ported to v0.56
     * using automatic conversion tool v0.01
     */ 

            /* obsolete, to be removed */
            /*TODO*/////public static final int READ_WORD(a)			(*(UINT16 *)(a))
            /*TODO*/////public static final int WRITE_WORD(a,d)			(*(UINT16 *)(a) = (d))


            /***************************************************************************

                    Basic type definitions

                    These types are used for memory handlers.

            ***************************************************************************/

            /* ----- typedefs for data and offset types ----- */
            /*TODO*/////typedef UINT8			data8_t;
            /*TODO*/////typedef UINT16			data16_t;
            /*TODO*/////typedef UINT32			data32_t;
            /*TODO*/////typedef UINT32			offs_t;

            /* ----- typedefs for the various common memory/port handlers ----- */
            //typedef data8_t			(*read8_handler)  (UNUSEDARG offs_t offset);
            public static abstract interface read8_handler {
                public abstract int handler(int offset);
            }
            //typedef void			(*write8_handler) (UNUSEDARG offs_t offset, UNUSEDARG data8_t data);
            public static abstract interface write8_handler {
                public abstract void handler(int offset, int data);
            }
            //typedef data16_t		(*read16_handler) (UNUSEDARG offs_t offset, UNUSEDARG data16_t mem_mask);
            public static abstract interface read16_handler {
                public abstract int handler(int offset, int mem_mask);
            }
            //typedef void			(*write16_handler)(UNUSEDARG offs_t offset, UNUSEDARG data16_t data, UNUSEDARG data16_t mem_mask);
            public static abstract interface write16_handler {
                public abstract void handler(int offset, int data, int mem_mask);
            }
            //typedef data32_t		(*read32_handler) (UNUSEDARG offs_t offset, UNUSEDARG data32_t mem_mask);
            public static abstract interface read32_handler {
                public abstract int handler(int offset, int mem_mask);
            }
            //typedef void			(*write32_handler)(UNUSEDARG offs_t offset, UNUSEDARG data32_t data, UNUSEDARG data32_t mem_mask);
            public static abstract interface write32_handler {
                public abstract void handler(int offset, int data, int mem_mask);
            }
            //typedef offs_t			(*opbase_handler) (UNUSEDARG offs_t address);
            public static abstract interface opbase_handler {
                public abstract int handler(int address);
            }

            /* ----- typedefs for the various common memory handlers ----- */
            public static abstract interface mem_read_handler extends read8_handler{};
            public static abstract interface mem_write_handler extends write8_handler{};
            public static abstract interface mem_read16_handler extends read16_handler{};
            public static abstract interface mem_write16_handler extends write16_handler{};
            public static abstract interface mem_read32_handler extends read32_handler{};
            public static abstract interface mem_write32_handler extends write32_handler{};

            /* ----- typedefs for the various common port handlers ----- */
            public static  abstract interface port_read_handler extends read8_handler{};
            public static abstract interface port_write_handler extends write8_handler{};
            public static abstract interface port_read16_handler extends read16_handler{};
            public static abstract interface port_write16_handler extends write16_handler{};
            public static abstract interface port_read32_handler extends read32_handler{};
            public static abstract interface port_write32_handler extends write32_handler{};

            /* ----- typedefs for externally allocated memory ----- */
            public static class ExtMemory
            {
                    int			start, end;
                    int			region;
                    UBytePtr		data;
            };



            /***************************************************************************

                    Basic macros

            ***************************************************************************/

            /* ----- macros for declaring the various common memory/port handlers ----- */
            /*TODO*/////public static final int READ_HANDLER(name) 		data8_t  name(UNUSEDARG offs_t offset)
            /*TODO*/////public static final int WRITE_HANDLER(name) 	void     name(UNUSEDARG offs_t offset, UNUSEDARG data8_t data)
            /*TODO*/////public static final int READ16_HANDLER(name)	data16_t name(UNUSEDARG offs_t offset, UNUSEDARG data16_t mem_mask)
            /*TODO*/////public static final int WRITE16_HANDLER(name)	void     name(UNUSEDARG offs_t offset, UNUSEDARG data16_t data, UNUSEDARG data16_t mem_mask)
            /*TODO*/////public static final int READ32_HANDLER(name)	data32_t name(UNUSEDARG offs_t offset, UNUSEDARG data32_t mem_mask)
            /*TODO*/////public static final int WRITE32_HANDLER(name)	void     name(UNUSEDARG offs_t offset, UNUSEDARG data32_t data, UNUSEDARG data32_t mem_mask)
            /*TODO*/////public static final int OPBASE_HANDLER(name)	offs_t   name(UNUSEDARG offs_t address)

            /* ----- macros for accessing bytes and words within larger chunks ----- */
            /*TODO*/////#ifdef LSB_FIRST
            /*TODO*/////        public static final int BYTE_XOR_BE(a)  	((a) ^ 1)				/* read/write a byte to a 16-bit space */
            /*TODO*/////        public static final int BYTE_XOR_LE(a)  	(a)
            /*TODO*/////        public static final int BYTE4_XOR_BE(a) 	((a) ^ 3)				/* read/write a byte to a 32-bit space */
            /*TODO*/////        public static final int BYTE4_XOR_LE(a) 	(a)
            /*TODO*/////        public static final int WORD_XOR_BE(a)  	((a) ^ 2)				/* read/write a word to a 32-bit space */
            /*TODO*/////        public static final int WORD_XOR_LE(a)  	(a)
            /*TODO*/////#else
            /*TODO*/////        public static final int BYTE_XOR_BE(a)  	(a)
            /*TODO*/////        public static final int BYTE_XOR_LE(a)  	((a) ^ 1)				/* read/write a byte to a 16-bit space */
            /*TODO*/////        public static final int BYTE4_XOR_BE(a) 	(a)
            /*TODO*/////        public static final int BYTE4_XOR_LE(a) 	((a) ^ 3)				/* read/write a byte to a 32-bit space */
            /*TODO*/////        public static final int WORD_XOR_BE(a)  	(a)
            /*TODO*/////        public static final int WORD_XOR_LE(a)  	((a) ^ 2)				/* read/write a word to a 32-bit space */
            /*TODO*/////#endif



            /***************************************************************************

                    Memory/port array constants

                    These apply to values in the array of read/write handlers that is
                    declared within each driver.

            ***************************************************************************/

            /* ----- memory/port width constants ----- */
            public static final int MEMPORT_WIDTH_MASK	=	0x00000003;				/* mask to get at the width bits */
            public static final int MEMPORT_WIDTH_8     =	0x00000001;				/* this memory/port array is for an 8-bit databus */
            public static final int MEMPORT_WIDTH_16 	=	0x00000002;				/* this memory/port array is for a 16-bit databus */
            public static final int MEMPORT_WIDTH_32 	=	0x00000003;				/* this memory/port array is for a 32-bit databus */

            /* ----- memory/port type constants ----- */
            public static final int MEMPORT_TYPE_MASK	=	0x30000000;				/* mask to get at the type bits */
            public static final int MEMPORT_TYPE_MEM 	=	0x10000000;				/* this memory/port array is for memory */
            public static final int MEMPORT_TYPE_IO	=	0x20000000;				/* this memory/port array is for ports */

            /* ----- memory/port direction constants ----- */
            public static final int MEMPORT_DIRECTION_MASK =	0xc0000000;				/* mask to get at the direction bits */
            public static final int MEMPORT_DIRECTION_READ =	0x40000000;				/* this memory/port array is for reads */
            public static final int MEMPORT_DIRECTION_WRITE=	0x80000000;				/* this memory/port array is for writes */

            /* ----- memory/port address bits constants ----- */
            public static final int MEMPORT_ABITS_MASK =	0x08000000;				/* set this bit to indicate the entry has address bits */
            public static final int MEMPORT_ABITS_VAL_MASK =	0x000000ff;				/* number of address bits */

            /* ----- memory/port struct marker constants ----- */
            //public static final int MEMPORT_MARKER  =		((offs_t)~0);			/* used in the end field to indicate end of array */
            public static final int MEMPORT_MARKER  =		(~0);			/* used in the end field to indicate end of array */

            /* ----- static memory/port handler constants ----- */
            public static final int STATIC_INVALID  =			0;						/* invalid - should never be used */
            public static final int STATIC_BANK1    =			1;						/* banked memory #1 */
            public static final int STATIC_BANK2    =			2;						/* banked memory #2 */
            public static final int STATIC_BANK3    =			3;						/* banked memory #3 */
            public static final int STATIC_BANK4    =			4;						/* banked memory #4 */
            public static final int STATIC_BANK5    =			5;						/* banked memory #5 */
            public static final int STATIC_BANK6    =			6;						/* banked memory #6 */
            public static final int STATIC_BANK7    =			7;						/* banked memory #7 */
            public static final int STATIC_BANK8    =			8;						/* banked memory #8 */
            public static final int STATIC_BANK9    =			9;						/* banked memory #9 */
            public static final int STATIC_BANK10   =			10;						/* banked memory #10 */
            public static final int STATIC_BANK11   =			11;						/* banked memory #11 */
            public static final int STATIC_BANK12   =			12;						/* banked memory #12 */
            public static final int STATIC_BANK13   =			13;						/* banked memory #13 */
            public static final int STATIC_BANK14   =			14;						/* banked memory #14 */
            public static final int STATIC_BANK15   =			15;						/* banked memory #15 */
            public static final int STATIC_BANK16   =			16;						/* banked memory #16 */
            public static final int STATIC_BANK17   =			17;						/* banked memory #17 */
            public static final int STATIC_BANK18   =			18;						/* banked memory #18 */
            public static final int STATIC_BANK19   =			19;						/* banked memory #19 */
            public static final int STATIC_BANK20   =			20;						/* banked memory #20 */
            public static final int STATIC_BANK21   =			21;						/* banked memory #21 */
            public static final int STATIC_BANK22   =			22;						/* banked memory #22 */
            public static final int STATIC_BANK23   =			23;						/* banked memory #23 */
            public static final int STATIC_BANK24   =			24;						/* banked memory #24 */
            public static final int STATIC_RAM      =			25;						/* RAM - standard reads/writes */
            public static final int STATIC_ROM      =			26;						/* ROM - just like RAM, but writes to the bit-bucket */
            public static final int STATIC_RAMROM   =			27;						/* RAMROM - use for access in encrypted 8-bit systems */
            public static final int STATIC_NOP      =			28;						/* NOP - reads are 0, writes to the bit-bucket */
            public static final int STATIC_UNUSED1  =			29;						/* unused - reserved for future use */
            public static final int STATIC_UNUSED2  =			30;						/* unused - reserved for future use */
            public static final int STATIC_UNMAP    =			31;						/* unmapped - all unmapped memory goes here */
            public static final int STATIC_COUNT    =			32;						/* total number of static handlers */

            /* ----- banking constants ----- */
            public static final int MAX_BANKS       =			24;						/* maximum number of banks */
            public static final int STATIC_BANKMAX  =			(STATIC_RAM - 1);		/* handler constant of last bank */



            /***************************************************************************

                    Constants for static entries in memory read/write arrays

                    The first 32 entries in the memory lookup table are reserved for
                    "static" handlers. These are internal handlers for RAM, ROM, banks,
                    and unmapped memory areas. The following definitions are the
                    properly-casted versions of the STATIC_ constants above.

            ***************************************************************************/

            /* 8-bit reads */
            //public static final int MRA_BANK1				((mem_read_handler)STATIC_BANK1)
            public static final int MRA_BANK1 = STATIC_BANK1;
            public static final int MRA_BANK2 = STATIC_BANK2;
            public static final int MRA_BANK3 = STATIC_BANK3;
            public static final int MRA_BANK4 = STATIC_BANK4;
            public static final int MRA_BANK5 = STATIC_BANK5;
            public static final int MRA_BANK6 = STATIC_BANK6;
            public static final int MRA_BANK7 = STATIC_BANK7;
            public static final int MRA_BANK8 = STATIC_BANK8;
            public static final int MRA_BANK9 = STATIC_BANK9;
            public static final int MRA_BANK10 = STATIC_BANK10;
            public static final int MRA_BANK11 = STATIC_BANK11;
            public static final int MRA_BANK12 = STATIC_BANK12;
            public static final int MRA_BANK13 = STATIC_BANK13;
            public static final int MRA_BANK14 = STATIC_BANK14;
            public static final int MRA_BANK15 = STATIC_BANK15;
            public static final int MRA_BANK16 = STATIC_BANK16;
            public static final int MRA_BANK17 = STATIC_BANK17;
            public static final int MRA_BANK18 = STATIC_BANK18;
            public static final int MRA_BANK19 = STATIC_BANK19;
            public static final int MRA_BANK20 = STATIC_BANK20;
            public static final int MRA_BANK21 = STATIC_BANK21;
            public static final int MRA_BANK22 = STATIC_BANK22;
            public static final int MRA_BANK23 = STATIC_BANK23;
            public static final int MRA_BANK24 = STATIC_BANK24;
            public static final int MRA_NOP = STATIC_NOP;
            public static final int MRA_RAM = STATIC_RAM;
            public static final int MRA_ROM = STATIC_ROM;
            public static final int MRA_RAMROM = STATIC_RAMROM;

            /* 8-bit writes */
            public static final int MWA_BANK1 = STATIC_BANK1;
            public static final int MWA_BANK2 = STATIC_BANK2;
            public static final int MWA_BANK3 = STATIC_BANK3;
            public static final int MWA_BANK4 = STATIC_BANK4;
            public static final int MWA_BANK5 = STATIC_BANK5;
            public static final int MWA_BANK6 = STATIC_BANK6;
            public static final int MWA_BANK7 = STATIC_BANK7;
            public static final int MWA_BANK8 = STATIC_BANK8;
            public static final int MWA_BANK9 = STATIC_BANK9;
            public static final int MWA_BANK10 = STATIC_BANK10;
            public static final int MWA_BANK11 = STATIC_BANK11;
            public static final int MWA_BANK12 = STATIC_BANK12;
            public static final int MWA_BANK13 = STATIC_BANK13;
            public static final int MWA_BANK14 = STATIC_BANK14;
            public static final int MWA_BANK15 = STATIC_BANK15;
            public static final int MWA_BANK16 = STATIC_BANK16;
            public static final int MWA_BANK17 = STATIC_BANK17;
            public static final int MWA_BANK18 = STATIC_BANK18;
            public static final int MWA_BANK19 = STATIC_BANK19;
            public static final int MWA_BANK20 = STATIC_BANK20;
            public static final int MWA_BANK21 = STATIC_BANK21;
            public static final int MWA_BANK22 = STATIC_BANK22;
            public static final int MWA_BANK23 = STATIC_BANK23;
            public static final int MWA_BANK24 = STATIC_BANK24;
            public static final int MWA_NOP = STATIC_NOP;
            public static final int MWA_RAM = STATIC_RAM;
            public static final int MWA_ROM = STATIC_ROM;
            public static final int MWA_RAMROM = STATIC_RAMROM;

            /* 16-bit reads */
            public static final int MRA16_BANK1 = STATIC_BANK1;
            public static final int MRA16_BANK2 = STATIC_BANK2;
            public static final int MRA16_BANK3 = STATIC_BANK3;
            public static final int MRA16_BANK4 = STATIC_BANK4;
            public static final int MRA16_BANK5 = STATIC_BANK5;
            public static final int MRA16_BANK6 = STATIC_BANK6;
            public static final int MRA16_BANK7 = STATIC_BANK7;
            public static final int MRA16_BANK8 = STATIC_BANK8;
            public static final int MRA16_BANK9 = STATIC_BANK9;
            public static final int MRA16_BANK10 = STATIC_BANK10;
            public static final int MRA16_BANK11 = STATIC_BANK11;
            public static final int MRA16_BANK12 = STATIC_BANK12;
            public static final int MRA16_BANK13 = STATIC_BANK13;
            public static final int MRA16_BANK14 = STATIC_BANK14;
            public static final int MRA16_BANK15 = STATIC_BANK15;
            public static final int MRA16_BANK16 = STATIC_BANK16;
            public static final int MRA16_BANK17 = STATIC_BANK17;
            public static final int MRA16_BANK18 = STATIC_BANK18;
            public static final int MRA16_BANK19 = STATIC_BANK19;
            public static final int MRA16_BANK20 = STATIC_BANK20;
            public static final int MRA16_BANK21 = STATIC_BANK21;
            public static final int MRA16_BANK22 = STATIC_BANK22;
            public static final int MRA16_BANK23 = STATIC_BANK23;
            public static final int MRA16_BANK24 = STATIC_BANK24;
            public static final int MRA16_NOP = STATIC_NOP;
            public static final int MRA16_RAM = STATIC_RAM;
            public static final int MRA16_ROM = STATIC_ROM;

            /* 16-bit writes */
            public static final int MWA16_BANK1 = STATIC_BANK1;
            public static final int MWA16_BANK2 = STATIC_BANK2;
            public static final int MWA16_BANK3 = STATIC_BANK3;
            public static final int MWA16_BANK4 = STATIC_BANK4;
            public static final int MWA16_BANK5 = STATIC_BANK5;
            public static final int MWA16_BANK6 = STATIC_BANK6;
            public static final int MWA16_BANK7 = STATIC_BANK7;
            public static final int MWA16_BANK8 = STATIC_BANK8;
            public static final int MWA16_BANK9 = STATIC_BANK9;
            public static final int MWA16_BANK10 = STATIC_BANK10;
            public static final int MWA16_BANK11 = STATIC_BANK11;
            public static final int MWA16_BANK12 = STATIC_BANK12;
            public static final int MWA16_BANK13 = STATIC_BANK13;
            public static final int MWA16_BANK14 = STATIC_BANK14;
            public static final int MWA16_BANK15 = STATIC_BANK15;
            public static final int MWA16_BANK16 = STATIC_BANK16;
            public static final int MWA16_BANK17 = STATIC_BANK17;
            public static final int MWA16_BANK18 = STATIC_BANK18;
            public static final int MWA16_BANK19 = STATIC_BANK19;
            public static final int MWA16_BANK20 = STATIC_BANK20;
            public static final int MWA16_BANK21 = STATIC_BANK21;
            public static final int MWA16_BANK22 = STATIC_BANK22;
            public static final int MWA16_BANK23 = STATIC_BANK23;
            public static final int MWA16_BANK24 = STATIC_BANK24;
            public static final int MWA16_NOP = STATIC_NOP;
            public static final int MWA16_RAM = STATIC_RAM;
            public static final int MWA16_ROM = STATIC_ROM;

            /* 32-bit reads */
            public static final int MRA32_BANK1 = STATIC_BANK1;
            public static final int MRA32_BANK2 = STATIC_BANK2;
            public static final int MRA32_BANK3 = STATIC_BANK3;
            public static final int MRA32_BANK4 = STATIC_BANK4;
            public static final int MRA32_BANK5 = STATIC_BANK5;
            public static final int MRA32_BANK6 = STATIC_BANK6;
            public static final int MRA32_BANK7 = STATIC_BANK7;
            public static final int MRA32_BANK8 = STATIC_BANK8;
            public static final int MRA32_BANK9 = STATIC_BANK9;
            public static final int MRA32_BANK10 = STATIC_BANK10;
            public static final int MRA32_BANK11 = STATIC_BANK11;
            public static final int MRA32_BANK12 = STATIC_BANK12;
            public static final int MRA32_BANK13 = STATIC_BANK13;
            public static final int MRA32_BANK14 = STATIC_BANK14;
            public static final int MRA32_BANK15 = STATIC_BANK15;
            public static final int MRA32_BANK16 = STATIC_BANK16;
            public static final int MRA32_BANK17 = STATIC_BANK17;
            public static final int MRA32_BANK18 = STATIC_BANK18;
            public static final int MRA32_BANK19 = STATIC_BANK19;
            public static final int MRA32_BANK20 = STATIC_BANK20;
            public static final int MRA32_BANK21 = STATIC_BANK21;
            public static final int MRA32_BANK22 = STATIC_BANK22;
            public static final int MRA32_BANK23 = STATIC_BANK23;
            public static final int MRA32_BANK24 = STATIC_BANK24;
            public static final int MRA32_NOP = STATIC_NOP;
            public static final int MRA32_RAM = STATIC_RAM;
            public static final int MRA32_ROM = STATIC_ROM;

            /* 32-bit writes */
            public static final int MWA32_BANK1 = STATIC_BANK1;
            public static final int MWA32_BANK2 = STATIC_BANK2;
            public static final int MWA32_BANK3 = STATIC_BANK3;
            public static final int MWA32_BANK4 = STATIC_BANK4;
            public static final int MWA32_BANK5 = STATIC_BANK5;
            public static final int MWA32_BANK6 = STATIC_BANK6;
            public static final int MWA32_BANK7 = STATIC_BANK7;
            public static final int MWA32_BANK8 = STATIC_BANK8;
            public static final int MWA32_BANK9 = STATIC_BANK9;
            public static final int MWA32_BANK10 = STATIC_BANK10;
            public static final int MWA32_BANK11 = STATIC_BANK11;
            public static final int MWA32_BANK12 = STATIC_BANK12;
            public static final int MWA32_BANK13 = STATIC_BANK13;
            public static final int MWA32_BANK14 = STATIC_BANK14;
            public static final int MWA32_BANK15 = STATIC_BANK15;
            public static final int MWA32_BANK16 = STATIC_BANK16;
            public static final int MWA32_BANK17 = STATIC_BANK17;
            public static final int MWA32_BANK18 = STATIC_BANK18;
            public static final int MWA32_BANK19 = STATIC_BANK19;
            public static final int MWA32_BANK20 = STATIC_BANK20;
            public static final int MWA32_BANK21 = STATIC_BANK21;
            public static final int MWA32_BANK22 = STATIC_BANK22;
            public static final int MWA32_BANK23 = STATIC_BANK23;
            public static final int MWA32_BANK24 = STATIC_BANK24;
            public static final int MWA32_NOP = STATIC_NOP;
            public static final int MWA32_RAM = STATIC_RAM;
            public static final int MWA32_ROM = STATIC_ROM;



            /***************************************************************************

                    Constants for static entries in port read/write arrays

            ***************************************************************************/

            /* 8-bit port reads */
            public static final int IORP_NOP = STATIC_NOP;

            /* 8-bit port writes */
            public static final int IOWP_NOP = STATIC_NOP;

            /* 16-bit port reads */
            public static final int IORP16_NOP = STATIC_NOP;

            /* 16-bit port writes */
            public static final int IOWP16_NOP = STATIC_NOP;

            /* 32-bit port reads */
            public static final int IORP32_NOP = STATIC_NOP;

            /* 32-bit port writes */
            public static final int IOWP32_NOP = STATIC_NOP;



            /***************************************************************************

                    Memory/port array type definitions

                    Note that the memory hooks are not passed the actual memory address
                    where the operation takes place, but the offset from the beginning
                    of the block they are assigned to. This makes handling of mirror
                    addresses easier, and makes the handlers a bit more "object oriented".
                    If you handler needs to read/write the main memory area, provide a
                    "base" pointer: it will be initialized by the main engine to point to
                    the beginning of the memory block assigned to the handler. You may
                    also provided a pointer to "size": it will be set to the length of
                    the memory area processed by the handler.

            ***************************************************************************/

            /* ----- structs for memory read arrays ----- */
            public static class Memory_ReadAddress {
                public int start, end;              /* start, end addresses, inclusive */
                public int handler;                 /* handler callback */
                /*TODO*/////public mem_read_handler _handler;   /* handler callback */
                public ReadHandlerPtr _handler;   /* handler callback */
                public UBytePtr base;
                
                public Memory_ReadAddress(int start, int end, int handler, ReadHandlerPtr _handler) {
                    this.start = start;
                    this.end = end;
                    this.handler = handler;
                    this._handler = _handler;
                }
                
                public Memory_ReadAddress(int start, int end, int handler) {
                    this(start, end, handler, null);
                }
                
                public Memory_ReadAddress(int start, int end, ReadHandlerPtr _handler, UBytePtr base) {
                    this.start = start;
                    this.end = end;
                    this.handler = -15000;//random number for not matching something else
                    this._handler = _handler;
                    this.base = base;
                }
                
                public Memory_ReadAddress(int start, int end, ReadHandlerPtr _handler) {
                    this(start, end, _handler, null);
                }

                public Memory_ReadAddress(int start) {
                    this(start, -1, null);
                }
            };
            
            public static class Memory_ReadAddress16 {
                public int start, end;              /* start, end addresses, inclusive */
                public int handler;                 /* handler callback */
                public mem_read16_handler _handler; /* handler callback */
                
                
            };
            
            public static class Memory_ReadAddress32 {
                public int start, end;              /* start, end addresses, inclusive */
                public int handler;                 /* handler callback */
                public mem_read32_handler _handler; /* handler callback */
                
                
            };
            
            /* ----- structs for memory write arrays ----- */
            public static class Memory_WriteAddress {
                public int start, end;              /* start, end addresses, inclusive */
                public int handler;                 /* handler callback */
                //public mem_write_handler	_handler;		/* handler callback */
                public WriteHandlerPtr	_handler;		/* handler callback */
                /* see special values below */
                public UBytePtr base;
                /* optional (see explanation above) */
                public int[] size;
                /* optional (see explanation above) */

                public Memory_WriteAddress(int start, int end, int handler, WriteHandlerPtr _handler, UBytePtr base, int[] size) {
                    this.start=start;
                    this.end=end;
                    this.handler=handler;
                    this._handler=_handler;
                    this.base=base;
                    this.size=size;
                }
                
                public Memory_WriteAddress(int start, int end, int handler, UBytePtr base, int[] size) {
                    this.start = start;
                    this.end = end;
                    this.handler = handler;
                    this.base = base;
                    this.size = size;
                }

                public Memory_WriteAddress(int start, int end, WriteHandlerPtr _handler, UBytePtr base, int[] size) {
                    this.start = start;
                    this.end = end;
                    this.handler = -15000;//random number for not matching something else
                    this._handler = _handler;
                    this.base = base;
                    this.size = size;
                }

                public Memory_WriteAddress(int start, int end, int handler, UBytePtr base) {
                    this.start = start;
                    this.end = end;
                    this.handler = handler;
                    this.base = base;
                }

                public Memory_WriteAddress(int start, int end, int handler) {
                    this(start, end, handler, null);
                }

                public Memory_WriteAddress(int start, int end, WriteHandlerPtr _handler, UBytePtr base) {
                    this.start = start;
                    this.end = end;
                    this.handler = -15000;//random number for not matching something else
                    this._handler = _handler;
                    this.base = base;
                }

                public Memory_WriteAddress(int start, int end, WriteHandlerPtr _handler) {
                    this.start = start;
                    this.end = end;
                    this.handler = -15000;//random number for not matching something else
                    this._handler = _handler;
                    this.base = null;
                }

                public Memory_WriteAddress(int start) {
                    this(start, -1, null);
                }
            };
            
            public static class Memory_WriteAddress16 {
                public int start, end;              /* start, end addresses, inclusive */
                public int handler;                 /* handler callback */
                public mem_write16_handler	_handler;		/* handler callback */
                /* see special values below */
                public UBytePtr base;
                /* optional (see explanation above) */
                public int[] size;
                /* optional (see explanation above) */
                
            };
            
            public static class Memory_WriteAddress32 {
                public int start, end;              /* start, end addresses, inclusive */
                public int handler;                 /* handler callback */
                public mem_write32_handler	_handler;		/* handler callback */
                /* see special values below */
                public UBytePtr base;
                /* optional (see explanation above) */
                public int[] size;
                /* optional (see explanation above) */
                
            };
            
            /* ----- structs for port read arrays ----- */
            public static class IO_ReadPort {
                public int start, end;          /* start, end addresses, inclusive */
                public int handler;             /* handler callback */
                //port_read_handler _handler;     /* handler callback */
                public ReadHandlerPtr _handler;
                
                public IO_ReadPort() {
                }

                public IO_ReadPort(int start, int end, int handler) {
                    this.start = start;
                    this.end = end;
                    this.handler = handler;
                }

                public IO_ReadPort(int start, int end, ReadHandlerPtr _handler) {
                    this.start = start;
                    this.end = end;
                    this.handler = -15000;//random number for not matching something else
                    this._handler = _handler;
                }

                public IO_ReadPort(int start) {
                    this(start, 0, null);
                }
            };
            
            public static class IO_ReadPort16 {
                public int start, end;          /* start, end addresses, inclusive */
                public int handler;             /* handler callback */
                port_read16_handler _handler;     /* handler callback */
                
            };
            
            public static class IO_ReadPort32 {
                public int start, end;          /* start, end addresses, inclusive */
                public int handler;             /* handler callback */
                port_read32_handler _handler;     /* handler callback */
                
            };
            
            /* ----- structs for port write arrays ----- */
            public static class IO_WritePort {
                public int start, end;          /* start, end addresses, inclusive */
                public int handler;             /* handler callback */
                //port_write_handler _handler;     /* handler callback */
                public WriteHandlerPtr _handler;
                
                public IO_WritePort() {
                }

                public IO_WritePort(int start, int end, int handler) {
                    this.start = start;
                    this.end = end;
                    this.handler = handler;
                }

                public IO_WritePort(int start, int end, WriteHandlerPtr _handler) {
                    this.start = start;
                    this.end = end;
                    this.handler = -15000;//random number for not matching something else
                    this._handler = _handler;
                }

                public IO_WritePort(int start) {
                    this(start, 0, null);
                }
            };
            
            public static class IO_WritePort16 {
                public int start, end;          /* start, end addresses, inclusive */
                public int handler;             /* handler callback */
                port_write16_handler _handler;     /* handler callback */
                
            };
            
            public static class IO_WritePort32 {
                public int start, end;          /* start, end addresses, inclusive */
                public int handler;             /* handler callback */
                port_write32_handler _handler;     /* handler callback */
                
            };


            /***************************************************************************

                    Memory/port array macros

            ***************************************************************************/

            /* ----- macros for identifying memory/port struct markers ----- */
            /*TODO*/////public static final int IS_MEMPORT_MARKER(ma)		((ma).start == MEMPORT_MARKER && (ma).end < MEMPORT_MARKER)
            /*TODO*/////public static final int IS_MEMPORT_END(ma)			((ma).start == MEMPORT_MARKER && (ma).end == 0)

            /* ----- macros for defining the start/stop points ----- */
            /*TODO*/////public static final int MEMPORT_ARRAY_START(t,n,f)	const struct t n[] = { { MEMPORT_MARKER, (f) },
            /*TODO*/////public static final int MEMPORT_ARRAY_END			{ MEMPORT_MARKER, 0 } };

            /* ----- macros for setting the number of address bits ----- */
            /*TODO*/////public static final int MEMPORT_SET_BITS(b)			{ MEMPORT_MARKER, MEMPORT_ABITS_MASK | (b) },

            /* ----- macros for declaring the start of a memory struct array ----- */
            /*TODO*/////public static final int MEMORY_READ_START(name)		MEMPORT_ARRAY_START(Memory_ReadAddress,    name, MEMPORT_DIRECTION_READ  | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8)
            /*TODO*/////public static final int MEMORY_WRITE_START(name)	MEMPORT_ARRAY_START(Memory_WriteAddress,   name, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8)
            /*TODO*/////public static final int MEMORY_READ16_START(name)	MEMPORT_ARRAY_START(Memory_ReadAddress16,  name, MEMPORT_DIRECTION_READ  | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_16)
            /*TODO*/////public static final int MEMORY_WRITE16_START(name)	MEMPORT_ARRAY_START(Memory_WriteAddress16, name, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_16)
            /*TODO*/////public static final int MEMORY_READ32_START(name)	MEMPORT_ARRAY_START(Memory_ReadAddress32,  name, MEMPORT_DIRECTION_READ  | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_32)
            /*TODO*/////public static final int MEMORY_WRITE32_START(name)	MEMPORT_ARRAY_START(Memory_WriteAddress32, name, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_32)

            /*TODO*/////public static final int MEMORY_ADDRESS_BITS(bits)	MEMPORT_SET_BITS(bits)
            /*TODO*/////public static final int MEMORY_END					MEMPORT_ARRAY_END

            /* ----- macros for declaring the start of a port struct array ----- */
            /*TODO*/////public static final int PORT_READ_START(name)		MEMPORT_ARRAY_START(IO_ReadPort,    name, MEMPORT_DIRECTION_READ  | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8)
            /*TODO*/////public static final int PORT_WRITE_START(name)		MEMPORT_ARRAY_START(IO_WritePort,   name, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8)
            /*TODO*/////public static final int PORT_READ16_START(name)		MEMPORT_ARRAY_START(IO_ReadPort16,  name, MEMPORT_DIRECTION_READ  | MEMPORT_TYPE_IO | MEMPORT_WIDTH_16)
            /*TODO*/////public static final int PORT_WRITE16_START(name)	MEMPORT_ARRAY_START(IO_WritePort16, name, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_16)
            /*TODO*/////public static final int PORT_READ32_START(name)		MEMPORT_ARRAY_START(IO_ReadPort32,  name, MEMPORT_DIRECTION_READ  | MEMPORT_TYPE_IO | MEMPORT_WIDTH_32)
            /*TODO*/////public static final int PORT_WRITE32_START(name)	MEMPORT_ARRAY_START(IO_WritePort32, name, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_32)

            /*TODO*/////public static final int PORT_ADDRESS_BITS(bits)		MEMPORT_SET_BITS(bits)
            /*TODO*/////public static final int PORT_END					MEMPORT_ARRAY_END



            /***************************************************************************

                    Memory/port lookup constants

                    These apply to values in the internal lookup table.

            ***************************************************************************/

            /* ----- memory/port lookup table definitions ----- */
            public static final int SUBTABLE_COUNT  =   64;                     /* number of slots reserved for subtables */
            public static final int SUBTABLE_MASK   =   (SUBTABLE_COUNT-1);	/* mask to get at the subtable index */
            public static final int SUBTABLE_BASE   =   (256-SUBTABLE_COUNT);	/* first index of a subtable */
            public static final int ENTRY_COUNT     =   (SUBTABLE_BASE);          /* number of legitimate (non-subtable) entries */
            public static final int SUBTABLE_ALLOC  =   8;      /* number of subtables to allocate at a time */

            /* ----- bit counts ----- */
            public static final int LEVEL1_BITS_PREF    =   12; /* preferred number of bits in the 1st level lookup */
            public static final int LEVEL1_BITS_BIAS    =   4;  /* number of bits used to bias the L1 bits computation */
            public static final int SPARSE_THRESH       =   20; /* number of address bits above which we use sparse memory */

            /* ----- external memory constants ----- */
            public static final int MAX_EXT_MEMORY      =   64; /* maximum external memory areas we can allocate */



            /***************************************************************************

                    Memory/port lookup macros

                    These are used for accessing the internal lookup table.

            ***************************************************************************/

            /* ----- macros for determining the number of bits to use ----- */
            /*TODO*/////public static final int LEVEL1_BITS(x)			(((x) < (2*LEVEL1_BITS_PREF - LEVEL1_BITS_BIAS)) ? LEVEL1_BITS_PREF : ((x) + LEVEL1_BITS_BIAS) / 2)
            /*TODO*/////public static final int LEVEL2_BITS(x)			((x) - LEVEL1_BITS(x))
            /*TODO*/////public static final int LEVEL1_MASK(x)			((1 << LEVEL1_BITS(x)) - 1)
            /*TODO*/////public static final int LEVEL2_MASK(x)			((1 << LEVEL2_BITS(x)) - 1)

            /* ----- table lookup helpers ----- */
            /*TODO*/////public static final int LEVEL1_INDEX(a,b,m)		((a) >> (LEVEL2_BITS((b)-(m)) + (m)))
            /*TODO*/////public static final int LEVEL2_INDEX(e,a,b,m)	((1 << LEVEL1_BITS((b)-(m))) + (((e) & SUBTABLE_MASK) << LEVEL2_BITS((b)-(m))) + (((a) >> (m)) & LEVEL2_MASK((b)-(m))))

            /* ----- sparse memory space detection ----- */
            /*TODO*/////public static final int IS_SPARSE(a)			((a) > SPARSE_THRESH)



            /***************************************************************************

                    Macros to help declare handlers for core readmem/writemem routines

            ***************************************************************************/

            /* ----- for declaring 8-bit handlers ----- */
            /*TODO*/////public static final int DECLARE_HANDLERS_8BIT(type, abits) \
            /*TODO*/////data8_t  cpu_read##type##abits             (offs_t offset);					\
            /*TODO*/////void     cpu_write##type##abits            (offs_t offset, data8_t data);

            /* ----- for declaring 16-bit bigendian handlers ----- */
            /*TODO*/////public static final int DECLARE_HANDLERS_16BIT_BE(type, abits) \
            /*TODO*/////data8_t  cpu_read##type##abits##bew        (offs_t offset);					\
            /*TODO*/////data16_t cpu_read##type##abits##bew_word   (offs_t offset);					\
            /*TODO*/////void     cpu_write##type##abits##bew       (offs_t offset, data8_t data);	\
            /*TODO*/////void     cpu_write##type##abits##bew_word  (offs_t offset, data16_t data);

            /* ----- for declaring 16-bit littleendian handlers ----- */
            /*TODO*/////public static final int DECLARE_HANDLERS_16BIT_LE(type, abits) \
            /*TODO*/////data8_t  cpu_read##type##abits##lew        (offs_t offset);					\
            /*TODO*/////data16_t cpu_read##type##abits##lew_word   (offs_t offset);					\
            /*TODO*/////void     cpu_write##type##abits##lew       (offs_t offset, data8_t data);	\
            /*TODO*/////void     cpu_write##type##abits##lew_word  (offs_t offset, data16_t data);

            /* ----- for declaring 32-bit bigendian handlers ----- */
            /*TODO*/////public static final int DECLARE_HANDLERS_32BIT_BE(type, abits) \
            /*TODO*/////data8_t  cpu_read##type##abits##bedw       (offs_t offset);					\
            /*TODO*/////data16_t cpu_read##type##abits##bedw_word  (offs_t offset);					\
            /*TODO*/////data32_t cpu_read##type##abits##bedw_dword (offs_t offset);					\
            /*TODO*/////void     cpu_write##type##abits##bedw      (offs_t offset, data8_t data);	\
            /*TODO*/////void     cpu_write##type##abits##bedw_word (offs_t offset, data16_t data);	\
            /*TODO*/////void     cpu_write##type##abits##bedw_dword(offs_t offset, data32_t data);

            /* ----- for declaring 32-bit littleendian handlers ----- */
            /*TODO*/////public static final int DECLARE_HANDLERS_32BIT_LE(type, abits) \
            /*TODO*/////data8_t  cpu_read##type##abits##ledw       (offs_t offset);					\
            /*TODO*/////data16_t cpu_read##type##abits##ledw_word  (offs_t offset);					\
            /*TODO*/////data32_t cpu_read##type##abits##ledw_dword (offs_t offset);					\
            /*TODO*/////void     cpu_write##type##abits##ledw      (offs_t offset, data8_t data);	\
            /*TODO*/////void     cpu_write##type##abits##ledw_word (offs_t offset, data16_t data);	\
            /*TODO*/////void     cpu_write##type##abits##ledw_dword(offs_t offset, data32_t data);

            /* ----- for declaring memory handlers ----- */
            /*TODO*/////public static final int DECLARE_MEM_HANDLERS_8BIT(abits) \
            /*TODO*/////DECLARE_HANDLERS_8BIT(mem, abits) \
            /*TODO*/////void     cpu_setopbase##abits              (offs_t pc);

            /*TODO*/////public static final int DECLARE_MEM_HANDLERS_16BIT_BE(abits) \
            /*TODO*/////DECLARE_HANDLERS_16BIT_BE(mem, abits) \
            /*TODO*/////void     cpu_setopbase##abits##bew         (offs_t pc);

            /*TODO*/////public static final int DECLARE_MEM_HANDLERS_16BIT_LE(abits) \
            /*TODO*/////DECLARE_HANDLERS_16BIT_LE(mem, abits) \
            /*TODO*/////void     cpu_setopbase##abits##lew         (offs_t pc);

            /*TODO*/////public static final int DECLARE_MEM_HANDLERS_32BIT_BE(abits) \
            /*TODO*/////DECLARE_HANDLERS_32BIT_BE(mem, abits) \
            /*TODO*/////void     cpu_setopbase##abits##bedw        (offs_t pc);

            /*TODO*/////public static final int DECLARE_MEM_HANDLERS_32BIT_LE(abits) \
            /*TODO*/////DECLARE_HANDLERS_32BIT_LE(mem, abits) \
            /*TODO*/////void     cpu_setopbase##abits##ledw        (offs_t pc);

            /* ----- for declaring port handlers ----- */
            /*TODO*/////public static final int DECLARE_PORT_HANDLERS_8BIT(abits) \
            /*TODO*/////DECLARE_HANDLERS_8BIT(port, abits)

            /*TODO*/////public static final int DECLARE_PORT_HANDLERS_16BIT_BE(abits) \
            /*TODO*/////DECLARE_HANDLERS_16BIT_BE(port, abits)

            /*TODO*/////public static final int DECLARE_PORT_HANDLERS_16BIT_LE(abits) \
            /*TODO*/////DECLARE_HANDLERS_16BIT_LE(port, abits)

            /*TODO*/////public static final int DECLARE_PORT_HANDLERS_32BIT_BE(abits) \
            /*TODO*/////DECLARE_HANDLERS_32BIT_BE(port, abits)

            /*TODO*/////public static final int DECLARE_PORT_HANDLERS_32BIT_LE(abits) \
            /*TODO*/////DECLARE_HANDLERS_32BIT_LE(port, abits)



            /***************************************************************************

                    Function prototypes for core readmem/writemem routines

            ***************************************************************************/

            /* ----- declare 8-bit handlers ----- */
            /*TODO*/////DECLARE_MEM_HANDLERS_8BIT(16)
            /*TODO*/////DECLARE_MEM_HANDLERS_8BIT(17)
            /*TODO*/////DECLARE_MEM_HANDLERS_8BIT(20)
            /*TODO*/////DECLARE_MEM_HANDLERS_8BIT(21)
            /*TODO*/////DECLARE_MEM_HANDLERS_8BIT(24)
            /*TODO*/////public static final int change_pc16(pc)			change_pc_generic(pc, 16, 0, cpu_setopbase16)
            /*TODO*/////public static final int change_pc17(pc) 		change_pc_generic(pc, 17, 0, cpu_setopbase17)
            /*TODO*/////public static final int change_pc20(pc)			change_pc_generic(pc, 20, 0, cpu_setopbase20)
            /*TODO*/////public static final int change_pc21(pc)			change_pc_generic(pc, 21, 0, cpu_setopbase21)
            /*TODO*/////public static final int change_pc24(pc)			change_pc_generic(pc, 24, 0, cpu_setopbase24)

            /* ----- declare 16-bit bigendian handlers ----- */
            /*TODO*/////DECLARE_MEM_HANDLERS_16BIT_BE(16)
            /*TODO*/////DECLARE_MEM_HANDLERS_16BIT_BE(24)
            /*TODO*/////DECLARE_MEM_HANDLERS_16BIT_BE(32)
            /*TODO*/////public static final int change_pc16bew(pc)		change_pc_generic(pc, 16, 1, cpu_setopbase16bew)
            /*TODO*/////public static final int change_pc24bew(pc)		change_pc_generic(pc, 24, 1, cpu_setopbase24bew)
            /*TODO*/////public static final int change_pc32bew(pc)		change_pc_generic(pc, 32, 1, cpu_setopbase32bew)

            /* ----- declare 16-bit littleendian handlers ----- */
            /*TODO*/////DECLARE_MEM_HANDLERS_16BIT_LE(16)
            /*TODO*/////DECLARE_MEM_HANDLERS_16BIT_LE(17)
            /*TODO*/////DECLARE_MEM_HANDLERS_16BIT_LE(24)
            /*TODO*/////DECLARE_MEM_HANDLERS_16BIT_LE(29)
            /*TODO*/////DECLARE_MEM_HANDLERS_16BIT_LE(32)
            /*TODO*/////public static final int change_pc16lew(pc)		change_pc_generic(pc, 16, 1, cpu_setopbase16lew)
            /*TODO*/////public static final int change_pc17lew(pc)		change_pc_generic(pc, 17, 1, cpu_setopbase17lew)
            /*TODO*/////public static final int change_pc24lew(pc)		change_pc_generic(pc, 24, 1, cpu_setopbase24lew)
            /*TODO*/////public static final int change_pc29lew(pc)		change_pc_generic(pc, 29, 1, cpu_setopbase29lew)
            /*TODO*/////public static final int change_pc32lew(pc)		change_pc_generic(pc, 32, 1, cpu_setopbase32lew)

            /* ----- declare 32-bit bigendian handlers ----- */
            /*TODO*/////DECLARE_MEM_HANDLERS_32BIT_BE(24)
            /*TODO*/////DECLARE_MEM_HANDLERS_32BIT_BE(29)
            /*TODO*/////DECLARE_MEM_HANDLERS_32BIT_BE(32)
            /*TODO*/////public static final int change_pc24bedw(pc)		change_pc_generic(pc, 24, 2, cpu_setopbase24bedw)
            /*TODO*/////public static final int change_pc29bedw(pc)		change_pc_generic(pc, 29, 2, cpu_setopbase29bedw)
            /*TODO*/////public static final int change_pc32bedw(pc)		change_pc_generic(pc, 32, 2, cpu_setopbase32bedw)

            /* ----- declare 32-bit littleendian handlers ----- */
            /*TODO*/////DECLARE_MEM_HANDLERS_32BIT_LE(26)
            /*TODO*/////DECLARE_MEM_HANDLERS_32BIT_LE(29)
            /*TODO*/////DECLARE_MEM_HANDLERS_32BIT_LE(32)
            /*TODO*/////public static final int change_pc26ledw(pc)		change_pc_generic(pc, 26, 2, cpu_setopbase26ledw)
            /*TODO*/////public static final int change_pc29ledw(pc)		change_pc_generic(pc, 29, 2, cpu_setopbase29ledw)
            /*TODO*/////public static final int change_pc32ledw(pc)		change_pc_generic(pc, 32, 2, cpu_setopbase32ledw)

            /* ----- declare pdp1 handler ----- */
            /*TODO*/////DECLARE_MEM_HANDLERS_32BIT_BE(18)
            /*TODO*/////public static final int change_pc28bedw(pc)		change_pc_generic(pc, 18, 2, cpu_setopbase18bedw)


            /***************************************************************************

                    Function prototypes for core readport/writeport routines

            ***************************************************************************/

            /* ----- declare 8-bit handlers ----- */
            /*TODO*/////DECLARE_PORT_HANDLERS_8BIT(16)

            /* ----- declare 16-bit bigendian handlers ----- */
            /*TODO*/////DECLARE_PORT_HANDLERS_16BIT_BE(16)

            /* ----- declare 16-bit littleendian handlers ----- */
            /*TODO*/////DECLARE_PORT_HANDLERS_16BIT_LE(16)
            /*TODO*/////DECLARE_PORT_HANDLERS_16BIT_LE(24)

            /* ----- declare 32-bit bigendian handlers ----- */
            /*TODO*/////DECLARE_PORT_HANDLERS_32BIT_BE(16)

            /* ----- declare 32-bit littleendian handlers ----- */
            /*TODO*/////DECLARE_PORT_HANDLERS_32BIT_LE(16)
            /*TODO*/////DECLARE_PORT_HANDLERS_32BIT_LE(24)


            /***************************************************************************

                    Function prototypes for core memory functions

            ***************************************************************************/

            /* ----- memory setup function ----- */
            /*TODO*/////void		memory_set_context(int activecpu);

            /* ----- dynamic bank handlers ----- */
            /*TODO*/////void		memory_set_bankhandler_r(int bank, offs_t offset, mem_read_handler handler);
            /*TODO*/////void		memory_set_bankhandler_w(int bank, offs_t offset, mem_write_handler handler);

            /* ----- opcode base control ---- */
            /*TODO*/////opbase_handler memory_set_opbase_handler(int cpunum, opbase_handler function);

            /* ----- separate opcode/data encryption helpers ---- */
            /*TODO*/////void		memory_set_opcode_base(int cpunum, void *base);
            /*TODO*/////void		memory_set_encrypted_opcode_range(int cpunum, offs_t min_address,offs_t max_address);
            /*TODO*/////extern offs_t encrypted_opcode_start[],encrypted_opcode_end[];

            /* ----- return a base pointer to memory ---- */
            /*TODO*/////void *		memory_find_base(int cpunum, offs_t offset);

            /* ----- dynamic memory mapping ----- */
            /*TODO*/////data8_t *	install_mem_read_handler    (int cpunum, offs_t start, offs_t end, mem_read_handler handler);
            /*TODO*/////data16_t *	install_mem_read16_handler  (int cpunum, offs_t start, offs_t end, mem_read16_handler handler);
            /*TODO*/////data32_t *	install_mem_read32_handler  (int cpunum, offs_t start, offs_t end, mem_read32_handler handler);
            /*TODO*/////data8_t *	install_mem_write_handler   (int cpunum, offs_t start, offs_t end, mem_write_handler handler);
            /*TODO*/////data16_t *	install_mem_write16_handler (int cpunum, offs_t start, offs_t end, mem_write16_handler handler);
            /*TODO*/////data32_t *	install_mem_write32_handler (int cpunum, offs_t start, offs_t end, mem_write32_handler handler);

            /* ----- dynamic port mapping ----- */
            /*TODO*/////void		install_port_read_handler   (int cpunum, offs_t start, offs_t end, port_read_handler handler);
            /*TODO*/////void		install_port_read16_handler (int cpunum, offs_t start, offs_t end, port_read16_handler handler);
            /*TODO*/////void		install_port_read32_handler (int cpunum, offs_t start, offs_t end, port_read32_handler handler);
            /*TODO*/////void		install_port_write_handler  (int cpunum, offs_t start, offs_t end, port_write_handler handler);
            /*TODO*/////void		install_port_write16_handler(int cpunum, offs_t start, offs_t end, port_write16_handler handler);
            /*TODO*/////void		install_port_write32_handler(int cpunum, offs_t start, offs_t end, port_write32_handler handler);



            /***************************************************************************

                    Global variables

            ***************************************************************************/

            public static int opcode_entry;		/* current entry for opcode fetching */
            public static UBytePtr OP_ROM;				/* opcode ROM base */
            public static UBytePtr OP_RAM;				/* opcode RAM base */
            public static UBytePtr[] cpu_bankbase;		/* array of bank bases */
            public static int readmem_lookup;		/* pointer to the readmem lookup table */
            public static int mem_amask=0xFFFFFFFF;			/* memory address mask */
            public ExtMemory[]	ext_memory;		/* externally-allocated memory */



            /***************************************************************************

                    Helper macros

            ***************************************************************************/

            /* ----- 16/32-bit memory accessing ----- */
            /*TODO*/////public static final int COMBINE_DATA(int varptr){
            /*TODO*/////    varptr = ((varptr & mem_mask) | (data & ~mem_mask));
            /*TODO*/////    return varptr;
            /*TODO*/////}

            /* ----- 16-bit memory accessing ----- */
            /*TODO*/////public static final int ACCESSING_LSB16				((mem_mask & 0x00ff) == 0)
            /*TODO*/////public static final int ACCESSING_MSB16				((mem_mask & 0xff00) == 0)
            /*TODO*/////public static final int ACCESSING_LSB				ACCESSING_LSB16
            /*TODO*/////public static final int ACCESSING_MSB				ACCESSING_MSB16

            /* ----- 32-bit memory accessing ----- */
            /*TODO*/////public static final int ACCESSING_LSW32				((mem_mask & 0x0000ffff) == 0)
            /*TODO*/////public static final int ACCESSING_MSW32				((mem_mask & 0xffff0000) == 0)
            /*TODO*/////public static final int ACCESSING_LSB32				((mem_mask & 0x000000ff) == 0)
            /*TODO*/////public static final int ACCESSING_MSB32				((mem_mask & 0xff000000) == 0)

            /* ----- opcode reading ----- */
            public static final int cpu_readop(int A){
                return ((OP_ROM.read((A) & mem_amask)) & 0xFF);
            }
            public static final int cpu_readop16(int A){
                return ((OP_ROM.read((A) & mem_amask)) & 0xFFFF);
            }
            public static final int cpu_readop32(int A){
                return ((OP_ROM.read((A) & mem_amask)) & 0xFFFFFF);
            }

            /* ----- opcode argument reading ----- */
            public static final int cpu_readop_arg(int A){
                return ((OP_RAM.read((A) & mem_amask)) & 0xFF);
            }
            public static final int cpu_readop_arg16(int A){
                return ((OP_RAM.read((A) & mem_amask)) & 0xFFFF);
            }
            public static final int cpu_readop_arg32(int A){
                return ((OP_RAM.read((A) & mem_amask)) & 0xFFFFFF);
            }

            /* ----- bank switching for CPU cores ----- */
            /*TODO*/////public static final int change_pc_generic(pc,abits,minbits,setop)										\
            /*TODO*/////do {																					\
            /*TODO*/////        if (readmem_lookup[LEVEL1_INDEX((pc) & mem_amask,abits,minbits)] != opcode_entry)	\
            /*TODO*/////                setop(pc);																		\
            /*TODO*/////} while (0)																				\


            /* ----- forces the next branch to generate a call to the opbase handler ----- */
            /*TODO*/////public static final int catch_nextBranch()			(opcode_entry = 0xff)

            /* ----- bank switching macro ----- */
            public static void cpu_setbank(int bank, UBytePtr _base) {														
            
                    if (bank >= STATIC_BANK1 && bank <= STATIC_BANKMAX)
                    {
                            cpu_bankbase[bank] = _base;
                            if (opcode_entry == bank && cpu_getactivecpu() >= 0)
                            {
                                    opcode_entry = 0xff;
                                    //cpu_set_op_base(cpu_get_pc_byte());
                                    cpu_set_op_base(cpu_get_pc());
                            }
                    }
            }




    


}
