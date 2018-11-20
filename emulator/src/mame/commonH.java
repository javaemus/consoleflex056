/**
 *
 *  Ported to mame 0.56 same file for mess 0.56 as well
 */
package mame;

//mess specific file should use the proper import for arcadeflex
import java.util.ArrayList;
import static mess_spec.common.*;

public class commonH {

    /*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Type definitions
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///struct mame_bitmap
/*TODO*///{
/*TODO*///	int width,height;	/* width and height of the bitmap */
/*TODO*///	int depth;			/* bits per pixel */
/*TODO*///	void **line;		/* pointers to the start of each line - can be UINT8 **, UINT16 ** or UINT32 ** */
/*TODO*///
/*TODO*///	/* alternate way of accessing the pixels */
/*TODO*///	void *base;			/* pointer to pixel (0,0) (adjusted for padding) */
/*TODO*///	int rowpixels;		/* pixels per row (including padding) */
/*TODO*///	int rowbytes;		/* bytes per row (including padding) */
/*TODO*///};
/*TODO*///
/*TODO*///
    public static class RomModule {

        public RomModule(String _name, int _offset, int _length, int _crc) {
            this._name = _name;
            this._offset = _offset;
            this._length = _length;
            this._crc = _crc;
        }

        public String _name;/* name of the file to load */
        public int/*UINT32*/ _offset;/* offset to load it to */
        public int/*UINT32*/ _length;/* length of the file */
        public int/*UINT32*/ _crc;/* standard CRC-32 checksum */
    }

    public static class GameSample {

        public GameSample() {
            data = new byte[1];
        }

        public GameSample(int len) {
            data = new byte[len];
        }
        public int length;
        public int smpfreq;
        public int resolution;
        public byte data[];/* extendable */
    }

    public static class GameSamples {

        public GameSamples() {
            sample = new GameSample[1];
            sample[0] = new GameSample();
        }

        public GameSamples(int size) {
            sample = new GameSample[size];
            for (int i = 0; i < size; i++) {
                sample[i] = new GameSample(1);
            }
        }
        public int total;/* total number of samples */
        public GameSample sample[];/* extendable */
    }

    /**
     * *************************************************************************
     *
     * Constants and macros
     *
     **************************************************************************
     */
    public static final int REGION_INVALID = 0x80;
    public static final int REGION_CPU1 = 0x81;
    public static final int REGION_CPU2 = 0x82;
    public static final int REGION_CPU3 = 0x83;
    public static final int REGION_CPU4 = 0x84;
    public static final int REGION_CPU5 = 0x85;
    public static final int REGION_CPU6 = 0x86;
    public static final int REGION_CPU7 = 0x87;
    public static final int REGION_CPU8 = 0x88;
    public static final int REGION_GFX1 = 0x89;
    public static final int REGION_GFX2 = 0x8a;
    public static final int REGION_GFX3 = 0x8b;
    public static final int REGION_GFX4 = 0x8c;
    public static final int REGION_GFX5 = 0x8d;
    public static final int REGION_GFX6 = 0x8e;
    public static final int REGION_GFX7 = 0x8f;
    public static final int REGION_GFX8 = 0x90;
    public static final int REGION_PROMS = 0x91;
    public static final int REGION_SOUND1 = 0x92;
    public static final int REGION_SOUND2 = 0x93;
    public static final int REGION_SOUND3 = 0x94;
    public static final int REGION_SOUND4 = 0x95;
    public static final int REGION_SOUND5 = 0x96;
    public static final int REGION_SOUND6 = 0x97;
    public static final int REGION_SOUND7 = 0x98;
    public static final int REGION_SOUND8 = 0x99;
    public static final int REGION_USER1 = 0x9a;
    public static final int REGION_USER2 = 0x9b;
    public static final int REGION_USER3 = 0x9c;
    public static final int REGION_USER4 = 0x9d;
    public static final int REGION_USER5 = 0x9e;
    public static final int REGION_USER6 = 0x9f;
    public static final int REGION_USER7 = 0xa0;
    public static final int REGION_USER8 = 0xa1;
    public static final int REGION_MAX = 0xa2;

    public static int BADCRC(int crc) {
        return ~crc;
    }
    /*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Core macros for the ROM loading system
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
    /* ----- length compaction macros ----- */
    public static final int INVALID_LENGTH = 0x7ff;
    /*TODO*///#define COMPACT_LENGTH(x)									\
/*TODO*///	((((x) & 0xffffff00) == 0) ? (0x000 | ((x) >> 0)) :		\
/*TODO*///	 (((x) & 0xfffff00f) == 0) ? (0x100 | ((x) >> 4)) :		\
/*TODO*///	 (((x) & 0xffff00ff) == 0) ? (0x200 | ((x) >> 8)) :		\
/*TODO*///	 (((x) & 0xfff00fff) == 0) ? (0x300 | ((x) >> 12)) : 	\
/*TODO*///	 (((x) & 0xff00ffff) == 0) ? (0x400 | ((x) >> 16)) : 	\
/*TODO*///	 (((x) & 0xf00fffff) == 0) ? (0x500 | ((x) >> 20)) : 	\
/*TODO*///	 (((x) & 0x00ffffff) == 0) ? (0x600 | ((x) >> 24)) : 	\
/*TODO*///	 INVALID_LENGTH)
/*TODO*///#define UNCOMPACT_LENGTH(x)	(((x) == INVALID_LENGTH) ? 0 : (((x) & 0xff) << (((x) & 0x700) >> 6)))

    /* ----- per-entry constants ----- */
    public static final int ROMENTRYTYPE_REGION = 1;/* this entry marks the start of a region */
    public static final int ROMENTRYTYPE_END = 2;/* this entry marks the end of a region */
    public static final int ROMENTRYTYPE_RELOAD = 3;/* this entry reloads the previous ROM */
    public static final int ROMENTRYTYPE_CONTINUE = 4;/* this entry continues loading the previous ROM */
    public static final int ROMENTRYTYPE_FILL = 5;/* this entry fills an area with a constant value */
    public static final int ROMENTRYTYPE_COPY = 6;/* this entry copies data from another region/offset */
    public static final int ROMENTRYTYPE_COUNT = 7;

    public static final String ROMENTRY_REGION = "1";
    public static final String ROMENTRY_END = "2";
    public static final String ROMENTRY_RELOAD = "3";
    public static final String ROMENTRY_CONTINUE = "4";
    public static final String ROMENTRY_FILL = "5";
    public static final String ROMENTRY_COPY = "6";

    /*TODO*////* ----- per-entry macros ----- */
/*TODO*///#define ROMENTRY_GETTYPE(r)			((FPTR)(r)->_name)
/*TODO*///#define ROMENTRY_ISSPECIAL(r)		(ROMENTRY_GETTYPE(r) < ROMENTRYTYPE_COUNT)
/*TODO*///#define ROMENTRY_ISFILE(r)			(!ROMENTRY_ISSPECIAL(r))
/*TODO*///#define ROMENTRY_ISREGION(r)		((r)->_name == ROMENTRY_REGION)
/*TODO*///#define ROMENTRY_ISEND(r)			((r)->_name == ROMENTRY_END)
/*TODO*///#define ROMENTRY_ISRELOAD(r)		((r)->_name == ROMENTRY_RELOAD)
/*TODO*///#define ROMENTRY_ISCONTINUE(r)		((r)->_name == ROMENTRY_CONTINUE)
/*TODO*///#define ROMENTRY_ISFILL(r)			((r)->_name == ROMENTRY_FILL)
/*TODO*///#define ROMENTRY_ISCOPY(r)			((r)->_name == ROMENTRY_COPY)
/*TODO*///#define ROMENTRY_ISREGIONEND(r)		(ROMENTRY_ISREGION(r) || ROMENTRY_ISEND(r))
/*TODO*///

    /* ----- per-region constants ----- */
    public static final int ROMREGION_WIDTHMASK = 0x00000003;/* native width of region, as power of 2 */
    public static final int ROMREGION_8BIT = 0x00000000;/*    (non-CPU regions only) */
    public static final int ROMREGION_16BIT = 0x00000001;
    public static final int ROMREGION_32BIT = 0x00000002;
    public static final int ROMREGION_64BIT = 0x00000003;

    public static final int ROMREGION_ENDIANMASK = 0x00000004;/* endianness of the region */
    public static final int ROMREGION_LE = 0x00000000;/*    (non-CPU regions only) */
    public static final int ROMREGION_BE = 0x00000004;

    public static final int ROMREGION_INVERTMASK = 0x00000008;/* invert the bits of the region */
    public static final int ROMREGION_NOINVERT = 0x00000000;
    public static final int ROMREGION_INVERT = 0x00000008;

    public static final int ROMREGION_DISPOSEMASK = 0x00000010;/* dispose of the region after init */
    public static final int ROMREGION_NODISPOSE = 0x00000000;
    public static final int ROMREGION_DISPOSE = 0x00000010;

    public static final int ROMREGION_SOUNDONLYMASK = 0x00000020;/* load only if sound is enabled */
    public static final int ROMREGION_NONSOUND = 0x00000000;
    public static final int ROMREGION_SOUNDONLY = 0x00000020;

    public static final int ROMREGION_LOADUPPERMASK = 0x00000040;/* load into the upper part of CPU space */
    public static final int ROMREGION_LOADLOWER = 0x00000000;/*     (CPU regions only) */
    public static final int ROMREGION_LOADUPPER = 0x00000040;

    public static final int ROMREGION_ERASEMASK = 0x00000080;/* erase the region before loading */
    public static final int ROMREGION_NOERASE = 0x00000000;
    public static final int ROMREGION_ERASE = 0x00000080;

    public static final int ROMREGION_ERASEVALMASK = 0x0000ff00;/* value to erase the region to */
 /*TODO*///#define		ROMREGION_ERASEVAL(x)	((((x) & 0xff) << 8) | ROMREGION_ERASE)
/*TODO*///#define		ROMREGION_ERASE00		ROMREGION_ERASEVAL(0)
/*TODO*///#define		ROMREGION_ERASEFF		ROMREGION_ERASEVAL(0xff)
/*TODO*///
/*TODO*////* ----- per-region macros ----- */
/*TODO*///#define ROMREGION_GETTYPE(r)		((r)->_crc)
/*TODO*///#define ROMREGION_GETLENGTH(r)		((r)->_length)
/*TODO*///#define ROMREGION_GETFLAGS(r)		((r)->_offset)
/*TODO*///#define ROMREGION_GETWIDTH(r)		(8 << (ROMREGION_GETFLAGS(r) & ROMREGION_WIDTHMASK))
/*TODO*///#define ROMREGION_ISLITTLEENDIAN(r)	((ROMREGION_GETFLAGS(r) & ROMREGION_ENDIANMASK) == ROMREGION_LE)
/*TODO*///#define ROMREGION_ISBIGENDIAN(r)	((ROMREGION_GETFLAGS(r) & ROMREGION_ENDIANMASK) == ROMREGION_BE)
/*TODO*///#define ROMREGION_ISINVERTED(r)		((ROMREGION_GETFLAGS(r) & ROMREGION_INVERTMASK) == ROMREGION_INVERT)
/*TODO*///#define ROMREGION_ISDISPOSE(r)		((ROMREGION_GETFLAGS(r) & ROMREGION_DISPOSEMASK) == ROMREGION_DISPOSE)
/*TODO*///#define ROMREGION_ISSOUNDONLY(r)	((ROMREGION_GETFLAGS(r) & ROMREGION_SOUNDONLYMASK) == ROMREGION_SOUNDONLY)
/*TODO*///#define ROMREGION_ISLOADUPPER(r)	((ROMREGION_GETFLAGS(r) & ROMREGION_LOADUPPERMASK) == ROMREGION_LOADUPPER)
/*TODO*///#define ROMREGION_ISERASE(r)		((ROMREGION_GETFLAGS(r) & ROMREGION_ERASEMASK) == ROMREGION_ERASE)
/*TODO*///#define ROMREGION_GETERASEVAL(r)	((ROMREGION_GETFLAGS(r) & ROMREGION_ERASEVALMASK) >> 8)

    /* ----- per-ROM constants ----- */
    public static final int ROM_LENGTHMASK = 0x000007ff;/* the compacted length of the ROM */
    public static final int ROM_INVALIDLENGTH = INVALID_LENGTH;

    public static final int ROM_OPTIONALMASK = 0x00000800;/* optional - won't hurt if it's not there */
    public static final int ROM_REQUIRED = 0x00000000;
    public static final int ROM_OPTIONAL = 0x00000800;

    public static final int ROM_GROUPMASK = 0x0000f000;/* load data in groups of this size + 1 */
 /*TODO*///#define		ROM_GROUPSIZE(n)		((((n) - 1) & 15) << 12)
/*TODO*///#define		ROM_GROUPBYTE			ROM_GROUPSIZE(1)
/*TODO*///#define		ROM_GROUPWORD			ROM_GROUPSIZE(2)
/*TODO*///#define		ROM_GROUPDWORD			ROM_GROUPSIZE(4)

    public static final int ROM_SKIPMASK = 0x000f0000;/* skip this many bytes after each group */
 /*TODO*///#define		ROM_SKIP(n)				(((n) & 15) << 16)
/*TODO*///#define		ROM_NOSKIP				ROM_SKIP(0)

    public static final int ROM_REVERSEMASK = 0x00100000;/* reverse the byte order within a group */
    public static final int ROM_NOREVERSE = 0x00000000;
    public static final int ROM_REVERSE = 0x00100000;
    /*TODO*///
    public static final int ROM_BITWIDTHMASK = 0x00e00000;/* width of data in bits */
 /*TODO*///#define		ROM_BITWIDTH(n)			(((n) & 7) << 21)
/*TODO*///#define		ROM_NIBBLE				ROM_BITWIDTH(4)
/*TODO*///#define		ROM_FULLBYTE			ROM_BITWIDTH(8)

    public static final int ROM_BITSHIFTMASK = 0x07000000;/* left-shift count for the bits */
 /*TODO*///#define		ROM_BITSHIFT(n)			(((n) & 7) << 24)
/*TODO*///#define		ROM_NOSHIFT				ROM_BITSHIFT(0)
/*TODO*///#define		ROM_SHIFT_NIBBLE_LO		ROM_BITSHIFT(0)
/*TODO*///#define		ROM_SHIFT_NIBBLE_HI		ROM_BITSHIFT(4)

    public static final int ROM_INHERITFLAGSMASK = 0x08000000;/* inherit all flags from previous definition */
    public static final int ROM_INHERITFLAGS = 0x08000000;

    public static final int ROM_INHERITEDFLAGS = (ROM_GROUPMASK | ROM_SKIPMASK | ROM_REVERSEMASK | ROM_BITWIDTHMASK | ROM_BITSHIFTMASK);

    /*TODO*////* ----- per-ROM macros ----- */
/*TODO*///#define ROM_GETNAME(r)				((r)->_name)
/*TODO*///#define ROM_SAFEGETNAME(r)			(ROMENTRY_ISFILL(r) ? "fill" : ROMENTRY_ISCOPY(r) ? "copy" : ROM_GETNAME(r))
/*TODO*///#define ROM_GETOFFSET(r)			((r)->_offset)
/*TODO*///#define ROM_GETCRC(r)				((r)->_crc)
/*TODO*///#define ROM_GETLENGTH(r)			(UNCOMPACT_LENGTH((r)->_length & ROM_LENGTHMASK))
/*TODO*///#define ROM_GETFLAGS(r)				((r)->_length & ~ROM_LENGTHMASK)
/*TODO*///#define ROM_ISOPTIONAL(r)			((ROM_GETFLAGS(r) & ROM_OPTIONALMASK) == ROM_OPTIONAL)
/*TODO*///#define ROM_GETGROUPSIZE(r)			(((ROM_GETFLAGS(r) & ROM_GROUPMASK) >> 12) + 1)
/*TODO*///#define ROM_GETSKIPCOUNT(r)			((ROM_GETFLAGS(r) & ROM_SKIPMASK) >> 16)
/*TODO*///#define ROM_ISREVERSED(r)			((ROM_GETFLAGS(r) & ROM_REVERSEMASK) == ROM_REVERSE)
/*TODO*///#define ROM_GETBITWIDTH(r)			(((ROM_GETFLAGS(r) & ROM_BITWIDTHMASK) >> 21) + 8 * ((ROM_GETFLAGS(r) & ROM_BITWIDTHMASK) == 0))
/*TODO*///#define ROM_GETBITSHIFT(r)			((ROM_GETFLAGS(r) & ROM_BITSHIFTMASK) >> 24)
/*TODO*///#define ROM_INHERITSFLAGS(r)		((ROM_GETFLAGS(r) & ROM_INHERITFLAGSMASK) == ROM_INHERITFLAGS)
/*TODO*///#define ROM_NOGOODDUMP(r)			(ROM_GETCRC(r) == 0)
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Derived macros for the ROM loading system
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
    public static RomModule[] rommodule_macro = null;
    public static ArrayList<RomModule> arload = new ArrayList<>();

    /* ----- start/stop macros ----- */
    public static void ROM_END() {
        arload.add(new RomModule(ROMENTRY_END, 0, 0, 0));
        rommodule_macro = arload.toArray(new RomModule[arload.size()]);
        arload.clear();
    }

    /* ----- ROM region macros ----- */
    public static void ROM_REGION(int length, int type, int flags) {
        arload.add(new RomModule(ROMENTRY_REGION, flags, length, type));
    }

/*TODO*///#define ROM_REGION16_LE(length,type,flags)			ROM_REGION(length, type, (flags) | ROMREGION_16BIT | ROMREGION_LE)
/*TODO*///#define ROM_REGION16_BE(length,type,flags)			ROM_REGION(length, type, (flags) | ROMREGION_16BIT | ROMREGION_BE)
/*TODO*///#define ROM_REGION32_LE(length,type,flags)			ROM_REGION(length, type, (flags) | ROMREGION_32BIT | ROMREGION_LE)
/*TODO*///#define ROM_REGION32_BE(length,type,flags)			ROM_REGION(length, type, (flags) | ROMREGION_32BIT | ROMREGION_BE)
/*TODO*///
/*TODO*////* ----- core ROM loading macros ----- */
/*TODO*///#define ROMX_LOAD(name,offset,length,crc,flags)		{ name, offset, (flags) | COMPACT_LENGTH(length), crc },
/*TODO*///#define ROM_LOAD(name,offset,length,crc)			ROMX_LOAD(name, offset, length, crc, 0)
/*TODO*///#define ROM_LOAD_OPTIONAL(name,offset,length,crc)	ROMX_LOAD(name, offset, length, crc, ROM_OPTIONAL)
/*TODO*///#define ROM_CONTINUE(offset,length)					ROMX_LOAD(ROMENTRY_CONTINUE, offset, length, 0, ROM_INHERITFLAGS)
/*TODO*///#define ROM_RELOAD(offset,length)					ROMX_LOAD(ROMENTRY_RELOAD, offset, length, 0, ROM_INHERITFLAGS)
/*TODO*///#define ROM_FILL(offset,length,value)				ROM_LOAD(ROMENTRY_FILL, offset, length, value)
/*TODO*///#define ROM_COPY(rgn,srcoffset,offset,length)		ROMX_LOAD(ROMENTRY_COPY, offset, length, srcoffset, (rgn) << 24)
/*TODO*///
/*TODO*////* ----- nibble loading macros ----- */
/*TODO*///#define ROM_LOAD_NIB_HIGH(name,offset,length,crc)	ROMX_LOAD(name, offset, length, crc, ROM_NIBBLE | ROM_SHIFT_NIBBLE_HI)
/*TODO*///#define ROM_LOAD_NIB_LOW(name,offset,length,crc)	ROMX_LOAD(name, offset, length, crc, ROM_NIBBLE | ROM_SHIFT_NIBBLE_LO)
/*TODO*///
/*TODO*////* ----- new-style 16-bit loading macros ----- */
/*TODO*///#define ROM_LOAD16_BYTE(name,offset,length,crc)		ROMX_LOAD(name, offset, length, crc, ROM_SKIP(1))
/*TODO*///#define ROM_LOAD16_WORD(name,offset,length,crc)		ROM_LOAD(name, offset, length, crc)
/*TODO*///#define ROM_LOAD16_WORD_SWAP(name,offset,length,crc)ROMX_LOAD(name, offset, length, crc, ROM_GROUPWORD | ROM_REVERSE)
/*TODO*///
/*TODO*////* ----- new-style 32-bit loading macros ----- */
/*TODO*///#define ROM_LOAD32_BYTE(name,offset,length,crc)		ROMX_LOAD(name, offset, length, crc, ROM_SKIP(3))
/*TODO*///#define ROM_LOAD32_WORD(name,offset,length,crc)		ROMX_LOAD(name, offset, length, crc, ROM_GROUPWORD | ROM_SKIP(2))
/*TODO*///#define ROM_LOAD32_WORD_SWAP(name,offset,length,crc)ROMX_LOAD(name, offset, length, crc, ROM_GROUPWORD | ROM_REVERSE | ROM_SKIP(2))
/*TODO*///

    public static final int COIN_COUNTERS = 4;/* total # of coin counters */

    public static int flip_screen() {
        return flip_screen_x[0];
    }
    /*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Useful macros to deal with bit shuffling encryptions
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///#define BITSWAP8(val,B7,B6,B5,B4,B3,B2,B1,B0) \
/*TODO*///		(((((val) >> (B7)) & 1) << 7) | \
/*TODO*///		 ((((val) >> (B6)) & 1) << 6) | \
/*TODO*///		 ((((val) >> (B5)) & 1) << 5) | \
/*TODO*///		 ((((val) >> (B4)) & 1) << 4) | \
/*TODO*///		 ((((val) >> (B3)) & 1) << 3) | \
/*TODO*///		 ((((val) >> (B2)) & 1) << 2) | \
/*TODO*///		 ((((val) >> (B1)) & 1) << 1) | \
/*TODO*///		 ((((val) >> (B0)) & 1) << 0))
/*TODO*///
/*TODO*///#define BITSWAP16(val,B15,B14,B13,B12,B11,B10,B9,B8,B7,B6,B5,B4,B3,B2,B1,B0) \
/*TODO*///		(((((val) >> (B15)) & 1) << 15) | \
/*TODO*///		 ((((val) >> (B14)) & 1) << 14) | \
/*TODO*///		 ((((val) >> (B13)) & 1) << 13) | \
/*TODO*///		 ((((val) >> (B12)) & 1) << 12) | \
/*TODO*///		 ((((val) >> (B11)) & 1) << 11) | \
/*TODO*///		 ((((val) >> (B10)) & 1) << 10) | \
/*TODO*///		 ((((val) >> ( B9)) & 1) <<  9) | \
/*TODO*///		 ((((val) >> ( B8)) & 1) <<  8) | \
/*TODO*///		 ((((val) >> ( B7)) & 1) <<  7) | \
/*TODO*///		 ((((val) >> ( B6)) & 1) <<  6) | \
/*TODO*///		 ((((val) >> ( B5)) & 1) <<  5) | \
/*TODO*///		 ((((val) >> ( B4)) & 1) <<  4) | \
/*TODO*///		 ((((val) >> ( B3)) & 1) <<  3) | \
/*TODO*///		 ((((val) >> ( B2)) & 1) <<  2) | \
/*TODO*///		 ((((val) >> ( B1)) & 1) <<  1) | \
/*TODO*///		 ((((val) >> ( B0)) & 1) <<  0))
/*TODO*///
/*TODO*///#define BITSWAP24(val,B23,B22,B21,B20,B19,B18,B17,B16,B15,B14,B13,B12,B11,B10,B9,B8,B7,B6,B5,B4,B3,B2,B1,B0) \
/*TODO*///		(((((val) >> (B23)) & 1) << 23) | \
/*TODO*///		 ((((val) >> (B22)) & 1) << 22) | \
/*TODO*///		 ((((val) >> (B21)) & 1) << 21) | \
/*TODO*///		 ((((val) >> (B20)) & 1) << 20) | \
/*TODO*///		 ((((val) >> (B19)) & 1) << 19) | \
/*TODO*///		 ((((val) >> (B18)) & 1) << 18) | \
/*TODO*///		 ((((val) >> (B17)) & 1) << 17) | \
/*TODO*///		 ((((val) >> (B16)) & 1) << 16) | \
/*TODO*///		 ((((val) >> (B15)) & 1) << 15) | \
/*TODO*///		 ((((val) >> (B14)) & 1) << 14) | \
/*TODO*///		 ((((val) >> (B13)) & 1) << 13) | \
/*TODO*///		 ((((val) >> (B12)) & 1) << 12) | \
/*TODO*///		 ((((val) >> (B11)) & 1) << 11) | \
/*TODO*///		 ((((val) >> (B10)) & 1) << 10) | \
/*TODO*///		 ((((val) >> ( B9)) & 1) <<  9) | \
/*TODO*///		 ((((val) >> ( B8)) & 1) <<  8) | \
/*TODO*///		 ((((val) >> ( B7)) & 1) <<  7) | \
/*TODO*///		 ((((val) >> ( B6)) & 1) <<  6) | \
/*TODO*///		 ((((val) >> ( B5)) & 1) <<  5) | \
/*TODO*///		 ((((val) >> ( B4)) & 1) <<  4) | \
/*TODO*///		 ((((val) >> ( B3)) & 1) <<  3) | \
/*TODO*///		 ((((val) >> ( B2)) & 1) <<  2) | \
/*TODO*///		 ((((val) >> ( B1)) & 1) <<  1) | \
/*TODO*///		 ((((val) >> ( B0)) & 1) <<  0))
/*TODO*///
/*TODO*///
/*TODO*///#ifdef __cplusplus
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///#endif
/*TODO*///    
}
