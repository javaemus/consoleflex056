/*****************************************************************************
 *
 *	 tms7000.h (c header file)
 *	 Portable TMS7000 emulator (Texas Instruments 7000)
 *
 *	 Copyright (c) 2001 tim lindner, all rights reserved.
 *
 *	 - This source code is released as freeware for non-commercial purposes.
 *	 - You are free to use and redistribute this code in modified or
 *	   unmodified form, provided you list me in the credits.
 *	 - If you modify this source code, you must add a notice to each modified
 *	   source file that it has been changed.  If you're a nice person, you
 *	   will clearly mark each change too.  :)
 *	 - If you wish to use this for commercial purposes, please contact me at
 *	   tlindner@ix.netcom.com
 *   - This entire notice must remain in the source code.
 *
 *****************************************************************************/

#ifndef _TMS7000_H
#define _TMS7000_H

#include "memory.h"
#include "osd_cpu.h"

enum { TMS7000_PC=1, TMS7000_SP, TMS7000_ST };

enum { TMS7000_VCC, TMS7000_VSS };

extern int tms7000_icount;

/* PUBLIC FUNCTIONS */
extern unsigned tms7000_get_context(void *dst);
extern void tms7000_set_context(void *src);
extern unsigned tms7000_get_pc(void);
extern void tms7000_set_pc(unsigned val);
extern unsigned tms7000_get_sp(void);
extern void tms7000_set_sp(unsigned val);
extern void tms7000_set_reg(int regnum, unsigned val);
extern void tms7000_init(void);
extern void tms7000_reset(void *param);
extern void tms7000_exit(void);
extern const char *tms7000_info(void *context, int regnum);
extern unsigned tms7000_dasm(char *buffer, unsigned pc);
extern void tms7000_set_nmi_line(int state);
extern void tms7000_set_irq_line(int irqline, int state);
extern void tms7000_set_irq_callback(int (*callback)(int irqline));
extern WRITE_HANDLER( tms7000_internal_w );
extern READ_HANDLER( tms7000_internal_r );
extern int tms7000_execute(int cycles);
extern unsigned tms7000_get_reg(int regnum);
extern void tms7000_set_mc_line( int value );

#ifdef MAME_DEBUG
extern unsigned Dasm7000 (char *buffer, unsigned pc);
#endif

#endif /* _TMS7000_H */

