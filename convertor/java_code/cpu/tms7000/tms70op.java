/*****************************************************************************
 *
 *	 tms70op.c (Op code functions)
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

void illegal( void )
{
	/* This is a guess */
	tms7000_icount -= 4;
}

void adc_imp( void )
{
	UINT16	t;
	
	t = RDA + RDB + GET_C;
	WRA(t);
	
	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 5;
}

void adc_r2a( void )
{
	UINT16	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = RM(v) + RDA + GET_C;
	WRA(t);
	
	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 8;
}

void adc_r2b( void )
{
	UINT16	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = RM(v) + RDB + GET_C;
	WRB(t);
	
	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 8;
}

void adc_r2r( void )
{
	UINT16	t;
	UINT8	i,j;
	
	IMMBYTE(i);
	IMMBYTE(j);
	
	t = RM(i)+RM(j) + GET_C;
	WM(j,t);
	
	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 10;
}

void adc_i2a( void )
{
	UINT16	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = v + RDA + GET_C;
	WRA(t);
	
	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 7;
}

void adc_i2b( void )
{
	UINT16	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = v + RDB + GET_C;
	WRB(t);
	
	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 7;
}

void adc_i2r( void )
{
	UINT16	t;
	UINT8	i,j;
	
	IMMBYTE(i);
	IMMBYTE(j);
	
	t = i+RM(j) + GET_C;
	WM(j,t);
	
	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 9;
}

void add_imp( void )
{
	UINT16	t;
	
	t = RDA + RDB;
	WRA(t);
	
	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 5;
}

void add_r2a( void )
{
	UINT16	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = RM(v) + RDA;
	WRA(t);
	
	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 8;
}

void add_r2b( void )
{
	UINT16	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = RM(v) + RDB;
	WRB(t);
	
	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 8;
}

void add_r2r( void )
{
	UINT16	t;
	UINT8	i,j;
	
	IMMBYTE(i);
	IMMBYTE(j);
	
	t = RM(i)+RM(j);
	WM(j,t);
	
	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 10;
}

void add_i2a( void )
{
	UINT16	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = v + RDA;
	WRA(t);
	
	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 7;
}

void add_i2b( void )
{
	UINT16	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = v + RDB;
	WRB(t);
	
	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 7;
}

void add_i2r( void )
{
	UINT16	t;
	UINT8	i,j;
	
	IMMBYTE(i);
	IMMBYTE(j);
	
	t = i+RM(j);
	WM(j,t);
	
	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 9;
}

void and_imp( void )
{
	UINT8	t;
	
	t = RDA & RDB;
	WRA(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 5;
}

void and_r2a( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = RM(v) & RDA;
	WRA(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 8;
}

void and_r2b( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = RM(v) & RDB;
	WRB(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 8;
}

void and_r2r( void )
{
	UINT8	t;
	UINT8	i,j;
	
	IMMBYTE(i);
	IMMBYTE(j);
	
	t = RM(i) & RM(j);
	WM(j,t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 10;
}

void and_i2a( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = v & RDA;
	WRA(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 7;
}

void and_i2b( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = v & RDB;
	WRB(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 7;
}

void and_i2r( void )
{
	UINT8	t;
	UINT8	i,j;
	
	IMMBYTE(i);
	IMMBYTE(j);
	
	t = i & RM(j);
	WM(j,t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 9;
}

void andp_a2p( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	t = RDA & RM( 0x0100 + v);
	WM( 0x0100+v, t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 10;
}	

void andp_b2p( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	t = RDB & RM( 0x0100 + v);
	WM( 0x0100+v, t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 9;
}	

void andp_i2p( void )
{
	UINT8	t;
	UINT8	i,v;
	
	IMMBYTE(i);
	IMMBYTE(v);
	t = i & RM( 0x0100 + v);
	WM( 0x0100+v, t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 11;
}	

void br_dir( void )
{
	PAIR p;
	
	IMMWORD( p );
	pPC = p.d;
	tms7000_icount -= 10;
}

void br_ind( void )
{
	UINT8	v;
	
	IMMBYTE( v );
	PC = RRF16(v);
	
	tms7000_icount -= 9;
}

void br_inx( void )
{
	PAIR p;
	
	IMMWORD( p );
	pPC = p.d + RDB;
	tms7000_icount -= 12;
}

void btjo_imp( void )
{
	UINT8	t;
		
	t = RDB & RDA;

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE( j );
		pPC += j;
		tms7000_icount -= 9;
	}
	else
	{
		pPC++;
		tms7000_icount -= 7;
	}
}

void btjo_r2a( void )
{
	UINT8	t,r;
	
	IMMBYTE( r );
	t = RM( r ) & RDA;

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE( j );
		pPC += j;
		tms7000_icount -= 9;
	}
	else
	{
		pPC++;
		tms7000_icount -= 7;
	}
}

void btjo_r2b( void )
{
	UINT8	t,r;
	
	IMMBYTE(r);
	t = RM(r) & RDB;

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE(j);
		pPC += j;
		tms7000_icount -= 12;
	}
	else
	{
		pPC++;
		tms7000_icount -= 10;
	}
}

void btjo_r2r( void )
{
	UINT8	t,r,s;
	
	IMMBYTE(r);
	IMMBYTE(s);
	t = RM(r) & RM(s);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE(j);
		pPC += j;
		tms7000_icount -= 14;
	}
	else
	{
		pPC++;
		tms7000_icount -= 12;
	}
}

void btjo_i2a( void )
{
	UINT8	t,r;
	
	IMMBYTE(r);
	t = r & RDA;

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE(j);
		pPC += j;
		tms7000_icount -= 11;
	}
	else
	{
		pPC++;
		tms7000_icount -= 9;
	}
}

void btjo_i2b( void )
{
	UINT8	t,i;
	
	IMMBYTE(i);
	t = i & RDB;

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE(j);
		pPC += j;
		tms7000_icount -= 11;
	}
	else
	{
		pPC++;
		tms7000_icount -= 9;
	}
}

void btjo_i2r( void )
{
	UINT8	t,i,r;
	
	IMMBYTE(i);
	IMMBYTE(r);
	t = i & RM(r);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE(j);
		pPC += j;
		tms7000_icount -= 13;
	}
	else
	{
		pPC++;
		tms7000_icount -= 11;
	}
}

void btjop_a( void )
{
	UINT8	t,p;
	
	IMMBYTE(p);
	
	t = RM(0x100+p) & RDA;
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE(j);
		pPC += j;
		tms7000_icount -= 13;
	}
	else
	{
		pPC++;
		tms7000_icount -= 11;
	}
}
	
void btjop_b( void )
{
	UINT8	t,p;
	
	IMMBYTE(p);
	
	t = RM(0x100+p) & RDB;
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE(j);
		pPC += j;
		tms7000_icount -= 12;
	}
	else
	{
		pPC++;
		tms7000_icount -= 10;
	}
}
	
void btjop_im( void )
{
	UINT8	t,p,i;
	
	IMMBYTE(i);
	IMMBYTE(p);
	
	t = RM(0x100+p) & i;
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE(j);
		pPC += j;
		tms7000_icount -= 14;
	}
	else
	{
		pPC++;
		tms7000_icount -= 12;
	}
}
	
void btjz_imp( void )
{
	UINT8	t;

	t = RDB & ~RDA;

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE( j );
		pPC += j;
		tms7000_icount -= 9;
	}
	else
	{
		pPC++;
		tms7000_icount -= 7;
	}
}

void btjz_r2a( void )
{
	UINT8	t,r;
	
	IMMBYTE( r );
	t = RM( r ) & ~RDA;

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE( j );
		pPC += j;
		tms7000_icount -= 9;
	}
	else
	{
		pPC++;
		tms7000_icount -= 7;
	}
}

void btjz_r2b( void )
{
	UINT8	t,r;
	
	IMMBYTE(r);
	t = RM(r) & ~RDB;

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE(j);
		pPC += j;
		tms7000_icount -= 12;
	}
	else
	{
		pPC++;
		tms7000_icount -= 10;
	}
}

void btjz_r2r( void )
{
	UINT8	t,r,s;
	
	IMMBYTE(r);
	IMMBYTE(s);
	t = RM(r) & ~RM(s);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE(j);
		pPC += j;
		tms7000_icount -= 14;
	}
	else
	{
		pPC++;
		tms7000_icount -= 12;
	}
}

void btjz_i2a( void )
{
	UINT8	t,r;
	
	IMMBYTE(r);
	t = r & ~RDA;

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE(j);
		pPC += j;
		tms7000_icount -= 11;
	}
	else
	{
		pPC++;
		tms7000_icount -= 9;
	}
}

void btjz_i2b( void )
{
	UINT8	t,i;
	
	IMMBYTE(i);
	t = i & ~RDB;

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE(j);
		pPC += j;
		tms7000_icount -= 11;
	}
	else
	{
		pPC++;
		tms7000_icount -= 9;
	}
}

void btjz_i2r( void )
{
	UINT8	t,i,r;
	
	IMMBYTE(i);
	IMMBYTE(r);
	t = i & ~RM(r);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE(j);
		pPC += j;
		tms7000_icount -= 13;
	}
	else
	{
		pPC++;
		tms7000_icount -= 11;
	}
}

void btjzp_a( void )
{
	UINT8	t,p;
	
	IMMBYTE(p);
	
	t = RM(0x100+p) & ~RDA;
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE(j);
		pPC += j;
		tms7000_icount -= 13;
	}
	else
	{
		pPC++;
		tms7000_icount -= 11;
	}
}
	
void btjzp_b( void )
{
	UINT8	t,p;
	
	IMMBYTE(p);
	
	t = RM(0x100+p) & ~RDB;
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE(j);
		pPC += j;
		tms7000_icount -= 12;
	}
	else
	{
		pPC++;
		tms7000_icount -= 10;
	}
}
	
void btjzp_im( void )
{
	UINT8	t,p,i;
	
	IMMBYTE(i);
	IMMBYTE(p);
	
	t = RM(0x100+p) & ~i;
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if(t != 0)
	{
		INT8	j;
		
		SIMMBYTE(j);
		pPC += j;
		tms7000_icount -= 14;
	}
	else
	{
		pPC++;
		tms7000_icount -= 12;
	}
}
	
void call_dir( void )
{
	PAIR	tPC;
	
	IMMWORD( tPC );
	PUSHWORD( PC );
	pPC = tPC.d;
	
	tms7000_icount -= 14;
}

void call_ind( void )
{
	UINT8	v;
	
	IMMBYTE( v );
	PUSHWORD( PC );
	PC = RRF16(v);
	
	tms7000_icount -= 13;
}

void call_inx( void )
{
	PAIR	tPC;
	
	IMMWORD( tPC );
	PUSHWORD( PC );
	pPC = tPC.d + RDB;
	tms7000_icount -= 16;
}

void clr_a( void )
{
	WRA(0);
	tms7000_icount -= 5;
}

void clr_b( void )
{
	WRB(0);
	tms7000_icount -= 5;
}

void clr_r( void )
{
	UINT8	r;
	
	IMMBYTE(r);
	WM(r,0);
	tms7000_icount -= 7;
}

void clrc( void )
{
	UINT8	a;
	
	a = RDA;

	CLR_NZC;
	SET_N8(a);
	SET_Z8(a);
	
	tms7000_icount -= 6;
}

void cmp_imp( void )
{
	UINT16 t;
	
	t = RDA - RDB;
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	if( t==0 )
		SETC;
	else
		SET_C8( ~t );
	
	tms7000_icount -= 5;	
}	

void cmp_ra( void )
{
	UINT16	t;
	UINT8	r;
	
	IMMBYTE(r);
	t = RDA - RM(r);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	if( t==0 )
		SETC;
	else
		SET_C8( ~t );
	
	tms7000_icount -= 8;	
}	

void cmp_rb( void )
{
	UINT16	t;
	UINT8	r;
	
	IMMBYTE(r);
	t = RDB - RM(r);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	if( t==0 )
		SETC;
	else
		SET_C8( ~t );
	
	tms7000_icount -= 8;	
}	

void cmp_rr( void )
{
	UINT16	t;
	UINT8	r,s;
	
	IMMBYTE(r);
	IMMBYTE(s);
	t = RM(s) - RM(r);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	if( t==0 )
		SETC;
	else
		SET_C8( ~t );
	
	tms7000_icount -= 10;	
}	

void cmp_ia( void )
{
	UINT16	t;
	UINT8	i;
	
	IMMBYTE(i);
	t = RDA - i;
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	if( t==0 )
		SETC;
	else
		SET_C8( ~t );
	
	tms7000_icount -= 7;	
}	

void cmp_ib( void )
{
	UINT16	t;
	UINT8	i;
	
	IMMBYTE(i);
	t = RDB - i;
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	if( t==0 )
		SETC;
	else
		SET_C8( ~t );
	
	tms7000_icount -= 7;	
}	

void cmp_ir( void )
{
	UINT16	t;
	UINT8	i,r;
	
	IMMBYTE(i);
	IMMBYTE(r);
	t = RM(r) - i;
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	if( t==0 )
		SETC;
	else
		SET_C8( ~t );
	
	tms7000_icount -= 9;	
}	

void cmpa_dir( void )
{
	UINT16	t;
	PAIR	i;
	
	IMMWORD( i );
	t = RDA - RM(i.w.l);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	if( t==0 )
		SETC;
	else
		SET_C8( ~t );
	
	tms7000_icount -= 12;	
}	

void cmpa_ind( void )
{
	UINT16	t;
	PAIR	p;
	INT8	i;
	
	IMMBYTE(i);
	p = RRF16(i);
	t = RDA - RM(p.w.l);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	if( t==0 )
		SETC;
	else
		SET_C8( ~t );
	
	tms7000_icount -= 11;	
}	

void cmpa_inx( void )
{
	UINT16	t;
	PAIR	i;
	
	IMMWORD( i );
	t = RDA - RM(i.w.l + RDB);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	if( t==0 )
		SETC;
	else
		SET_C8( ~t );
	
	tms7000_icount -= 14;
}	

void dac_b2a( void )
{
	UINT16	t;
	
	t = bcd_add( RDA, RDB );
	
	if ((pSR & SR_C) != 0)
		t = bcd_add( t, 1 );

	WRA(t);

	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 7;
}
	
void dac_r2a( void )
{
	UINT8	r;
	UINT16	t;
	
	IMMBYTE(r);
	
	t = bcd_add( RDA, RM(r) );
	
	if ((pSR & SR_C) != 0)
		t = bcd_add( t, 1 );

	WRA(t);

	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 10;
}
	
void dac_r2b( void )
{
	UINT8	r;
	UINT16	t;
	
	IMMBYTE(r);
	
	t = bcd_add( RDB, RM(r) );
	
	if ((pSR & SR_C) != 0)
		t = bcd_add( t, 1 );

	WRB(t);

	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 10;
}
	
void dac_r2r( void )
{
	UINT8	r,s;
	UINT16	t;
	
	IMMBYTE(s);
	IMMBYTE(r);
	
	t = bcd_add( RM(s), RM(r) );
	
	if ((pSR & SR_C) != 0)
		t = bcd_add( t, 1 );

	WM(r,t);

	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 12;
}
	
void dac_i2a( void )
{
	UINT8	i;
	UINT16	t;
	
	IMMBYTE(i);
	
	t = bcd_add( i, RDA );
	
	if ((pSR & SR_C) != 0)
		t = bcd_add( t, 1 );

	WRA(t);

	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 9;
}
	
void dac_i2b( void )
{
	UINT8	i;
	UINT16	t;
	
	IMMBYTE(i);
	
	t = bcd_add( i, RDB );
	
	if ((pSR & SR_C) != 0)
		t = bcd_add( t, 1 );

	WRB(t);

	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 9;
}
	
void dac_i2r( void )
{
	UINT8	i,r;
	UINT16	t;
	
	IMMBYTE(i);
	IMMBYTE(r);
	
	t = bcd_add( i, RM(r) );
	
	if ((pSR & SR_C) != 0)
		t = bcd_add( t, 1 );

	WM(r,t);

	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 11;
}
	
void dec_a( void )
{
	UINT16 t;
	
	t = RDA - 1;
	
	WRA( t );
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	SET_C8(~t);

	tms7000_icount -= 5;
}

void dec_b( void )
{
	UINT16 t;
	
	t = RDB - 1;
	
	WRB( t );
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	SET_C8(~t);

	tms7000_icount -= 5;
}

void dec_r( void )
{
	UINT16	t;
	UINT8	r;
	
	IMMBYTE(r);
	
	t = RM(r) - 1;
	
	WM( r, t );
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	SET_C8(~t);

	tms7000_icount -= 7;
}

void decd_a( void )
{
	/* Not implemented */
	tms7000_icount -= 9;
}

void decd_b( void )
{
	/* Not implemented */
	tms7000_icount -= 9;
}

void decd_r( void )
{
	UINT8	r;
	PAIR	t;
	
	IMMBYTE(r);
	t = RRF16(r);
	t.d -= 1;
	WRF16(r,t);

	CLR_NZC;
	SET_N8(t.b.h);
	SET_Z8(t.b.h);

	SET_C16(~(t.d));

	tms7000_icount -= 11;
}

void dint( void )
{
	CLR_NZCI;
	tms7000_icount -= 5;
}

void djnz_a( void )
{
	UINT16 t;
	
	t = RDA - 1;
	
	WRA( t );
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if( t != 0 )
	{
		INT8	s;
		
		SIMMBYTE(s);
		pPC += s;
		tms7000_icount -= 7;
	}
	else
	{
		pPC++;
		tms7000_icount -= 2;
	}
}

void djnz_b( void )
{
	UINT16 t;
	
	t = RDB - 1;
	
	WRB( t );
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if( t != 0 )
	{
		INT8	s;
		
		SIMMBYTE(s);
		pPC += s;
		tms7000_icount -= 7;
	}
	else
	{
		pPC++;
		tms7000_icount -= 2;
	}
}

void djnz_r( void )
{
	UINT16	t;
	UINT8	r;
	
	IMMBYTE(r);
	
	t = RM(r) - 1;
	
	WM(r,t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	if( t != 0 )
	{
		INT8	s;
		
		SIMMBYTE(s);
		pPC += s;
		tms7000_icount -= 9;
	}
	else
	{
		pPC++;
		tms7000_icount -= 3;
	}
}

void dsb_b2a( void )
{
	UINT16	t;

	t = bcd_sub( RDA, RDB );
	
	if( !(pSR & SR_C) )
		t = bcd_sub( t, 1 );

	WRA(t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 7;
}
	
void dsb_r2a( void )
{
	UINT8	r;
	UINT16	t;
	
	IMMBYTE(r);

	t = bcd_sub( RDA, RM(r) );
	
	if( !(pSR & SR_C) )
		t = bcd_sub( t, 1 );

	WRA(t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 10;
}
	
void dsb_r2b( void )
{
	UINT8	r;
	UINT16	t;
	
	IMMBYTE(r);

	t = bcd_sub( RDB, RM(r) );
	
	if( !(pSR & SR_C) )
		t = bcd_sub( t, 1 );

	WRB(t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 10;
}
	
void dsb_r2r( void )
{
	UINT8	r,s;
	UINT16	t;

	IMMBYTE(s);
	IMMBYTE(r);

	t = bcd_sub( RM(s), RM(r) );
	
	if( !(pSR & SR_C) )
		t = bcd_sub( t, 1 );

	WM(r,t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 12;
}
	
void dsb_i2a( void )
{
	UINT8	i;
	UINT16	t;
	
	IMMBYTE(i);

	t = bcd_sub( RDA, i );
	
	if( !(pSR & SR_C) )
		t = bcd_sub( t, 1 );

	WRA(t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 9;
}
	
void dsb_i2b( void )
{
	UINT8	i;
	UINT16	t;
	
	IMMBYTE(i);

	t = bcd_sub( RDB, i );
	
	if( !(pSR & SR_C) )
		t = bcd_sub( t, 1 );

	WRB(t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 9;
}
	
void dsb_i2r( void )
{
	UINT8	r,i;
	UINT16	t;

	IMMBYTE(i);
	IMMBYTE(r);

	t = bcd_sub( RM(r), i );
	
	if( !(pSR & SR_C) )
		t = bcd_sub( t, 1 );

	WM(r,t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 11;
}
	
void eint( void )
{
	pSR |= (SR_N|SR_Z|SR_C|SR_I);
	tms7000_icount -= 5;
}

void idle( void )
{
	/* Not implemented */
	tms7000_icount -= 6;
}

void inc_a( void )
{
	UINT16	t;
	
	t = RDA + 1;
	
	WRA( t );

	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 5;
}
	
void inc_b( void )
{
	UINT16	t;
	
	t = RDB + 1;
	
	WRB( t );

	CLR_NZC;
	SET_C8(t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 5;
}

void inc_r( void )
{
	UINT16	t;
	UINT8	r;
	
	IMMBYTE(r);
	
	t = RM(r) + 1;
	
	WM( r, t );
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	SET_C8(t);

	tms7000_icount -= 7;
}

void inv_a( void )
{
	UINT16 t;
	
	t = ~(RDA);
	WRA(t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 5;
}

void inv_b( void )
{
	UINT16 t;
	
	t = ~(RDA);
	WRA(t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 5;
}

void inv_r( void )
{
	UINT16	t;
	UINT8	r;
	
	IMMBYTE(r);
	
	t = ~(RM(r));
	
	WM( r, t );
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 7;
}

void jc( void )
{
	if ((pSR & SR_C) != 0)
	{
		INT8 s;
		
		SIMMBYTE( s );
		pPC += s;
		tms7000_icount -= 7;
	}
	else
	{
		pPC++;
		tms7000_icount -= 5;
	}
}
		
void jeq( void )
{
	if ((pSR & SR_Z) != 0)
	{
		INT8 s;
		
		SIMMBYTE( s );
		pPC += s;
		tms7000_icount -= 7;
	}
	else
	{
		pPC++;
		tms7000_icount -= 5;
	}
}

void jl( void )
{
	if ((pSR & SR_C) != 0)
	{
		pPC++;
		tms7000_icount -= 5;
	}
	else
	{
		INT8 s;
		
		SIMMBYTE( s );
		pPC += s;
		tms7000_icount -= 7;
	}
}
		
void jmp( void )
{
	INT8 s;
	
	SIMMBYTE( s );
	pPC += s;
	tms7000_icount -= 7;
}

void jn( void )
{
	if ((pSR & SR_N) != 0)
	{
		pPC++;
		tms7000_icount -= 5;
	}
	else
	{
		INT8 s;
		
		SIMMBYTE( s );
		pPC += s;
		tms7000_icount -= 7;
	}
}

void jne( void )
{
	if ((pSR & SR_Z) != 0)
	{
		pPC++;
		tms7000_icount -= 5;
	}
	else
	{
		INT8 s;
		
		SIMMBYTE( s );
		pPC += s;
		tms7000_icount -= 7;
	}
}

void jp( void )
{
	if( pSR & (SR_Z|SR_N) )
	{
		pPC++;
		tms7000_icount -= 5;
	}
	else
	{
		INT8 s;
		
		SIMMBYTE( s );
		pPC += s;
		tms7000_icount -= 7;
	}
}

void jpz( void )
{
	if ((pSR & SR_N) != 0)
	{
		pPC++;
		tms7000_icount -= 5;
	}
	else
	{
		INT8 s;
		
		SIMMBYTE( s );
		pPC += s;
		tms7000_icount -= 7;
	}
}

void lda_dir( void )
{
	UINT16	t;
	PAIR	i;
	
	IMMWORD( i );
	t = RM(i.w.l);
	WRA(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 11;	
}	

void lda_ind( void )
{
	UINT16	t;
	PAIR	p;
	INT8	i;
	
	IMMBYTE(i);
	p=RRF16(i);
	t = RM(p.w.l);
	WRA(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 10;	
}	

void lda_inx( void )
{
	UINT16	t;
	PAIR	i;
	
	IMMWORD( i );
	t = RM(i.w.l + RDB);
	WRA(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 13;
}	

void ldsp( void )
{
	pSP = RDB;
	tms7000_icount -= 5;
}

void mov_a2b( void )
{
	UINT16	t;
	
	t = RDA;
	WRB(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 6;
}

void mov_b2a( void )
{
	UINT16	t;
	
	t = RDB;
	WRA(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 5;
}


void mov_a2r( void )
{
	UINT8	r;
	UINT16	t;
	
	IMMBYTE(r);
	
	t = RDA;
	WM(r,t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 8;
}

void mov_b2r( void )
{
	UINT8	r;
	UINT16	t;
	
	IMMBYTE(r);
	
	t = RDB;
	WM(r,t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 7;
}

void mov_r2a( void )
{
	UINT8	r;
	UINT16	t;
	
	IMMBYTE(r);
	t = RM(r);
	WRA(t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 8;
}

void mov_r2b( void )
{
	UINT8	r;
	UINT16	t;
	
	IMMBYTE(r);
	t = RM(r);
	WRB(t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 8;
}

void mov_r2r( void )
{
	UINT8	r,s;
	UINT16	t;
	
	IMMBYTE(r);
	IMMBYTE(s);
	t = RM(r);
	WM(s,t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 10;
}

void mov_i2a( void )
{
	UINT16	t;
	
	IMMBYTE(t);
	WRA(t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 7;
}

void mov_i2b( void )
{
	UINT16	t;
	
	IMMBYTE(t);
	WRB(t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 7;
}

void mov_i2r( void )
{
	UINT16	t;
	UINT8	r;
	
	IMMBYTE(t);
	IMMBYTE(r);
	WM(r,t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 9;
}

void movd_imm( void )
{
	PAIR	t;
	UINT8	r;
	
	IMMWORD(t);
	IMMBYTE(r);
	WRF16(r,t);
	
	CLR_NZC;
	SET_N8(t.b.h);
	SET_Z8(t.b.h);
	
	tms7000_icount -= 15;
	
}

void movd_r( void )
{
	PAIR	t;
	UINT8	r,s;
	
	IMMBYTE(r);
	IMMBYTE(s);
	t = RRF16(r);
	WRF16(s,t);
	
	CLR_NZC;
	SET_N8(t.b.h);
	SET_Z8(t.b.h);
	
	tms7000_icount -= 14;
	
}

void movd_ind( void )
{
	PAIR	t;
	UINT8	r;
	
	IMMWORD(t);
	t.w.l += RDB;
	IMMBYTE(r);
	WRF16(r,t);
	
	CLR_NZC;
	SET_N8(t.b.h);
	SET_Z8(t.b.h);
	
	tms7000_icount -= 17;
}

void movp_a2p( void )
{
	UINT8	p;
	UINT16	t;
	
	IMMBYTE(p);
	t=RDA;
	WM( 0x0100+p,t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 10;
}

void movp_b2p( void )
{
	UINT8	p;
	UINT16	t;
	
	IMMBYTE(p);
	t=RDB;
	WM( 0x0100+p,t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 10;
}

void movp_r2p( void )
{
	UINT8	p,r;
	UINT16	t;
	
	IMMBYTE(r);
	IMMBYTE(p);
	t=RM(r);
	WM( 0x0100+p,t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 11;
}

void movp_p2a( void )
{
	UINT8	p;
	UINT16	t;
	
	IMMBYTE(p);
	t=RM(0x0100+p);
	WRA(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 9;
}

void movp_p2b( void )
{
	UINT8	p;
	UINT16	t;
	
	IMMBYTE(p);
	t=RM(0x0100+p);
	WRB(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 8;
}

void mpy_ba( void )
{
	PAIR t;
	
	t.w.l = RDA * RDB;
	
	WRF16(0x01,t);

	CLR_NZC;
	SET_N8(t.b.h);
	SET_Z8(t.b.h);
	
	tms7000_icount -= 43;
	
}
	
void mpy_ra( void )
{
	PAIR	t;
	UINT8	r;
	
	IMMBYTE(r);
	
	t.w.l = RDA * RM(r);
	
	WRF16(0x01,t);

	CLR_NZC;
	SET_N8(t.b.h);
	SET_Z8(t.b.h);
	
	tms7000_icount -= 46;
	
}
	
void mpy_rb( void )
{
	PAIR	t;
	UINT8	r;
	
	IMMBYTE(r);
	
	t.w.l = RDB * RM(r);
	
	WRF16(0x01,t);

	CLR_NZC;
	SET_N8(t.b.h);
	SET_Z8(t.b.h);
	
	tms7000_icount -= 46;
	
}
	
void mpy_rr( void )
{
	PAIR	t;
	UINT8	r,s;
	
	IMMBYTE(r);
	IMMBYTE(s);
	
	t.w.l = RM(s) * RM(r);
	
	WRF16(0x01,t);

	CLR_NZC;
	SET_N8(t.b.h);
	SET_Z8(t.b.h);
	
	tms7000_icount -= 48;
	
}
	
void mpy_ia( void )
{
	PAIR	t;
	UINT8	i;
	
	IMMBYTE(i);
	
	t.w.l = RDA * i;
	
	WRF16(0x01,t);

	CLR_NZC;
	SET_N8(t.b.h);
	SET_Z8(t.b.h);
	
	tms7000_icount -= 45;
	
}
	
void mpy_ib( void )
{
	PAIR	t;
	UINT8	i;
	
	IMMBYTE(i);
	
	t.w.l = RDB * i;
	
	WRF16(0x01,t);

	CLR_NZC;
	SET_N8(t.b.h);
	SET_Z8(t.b.h);
	
	tms7000_icount -= 45;
	
}
	
void mpy_ir( void )
{
	PAIR	t;
	UINT8	i,r;
	
	IMMBYTE(i);
	IMMBYTE(r);
	
	t.w.l = RM(r) * i;
	
	WRF16(0x01,t);

	CLR_NZC;
	SET_N8(t.b.h);
	SET_Z8(t.b.h);
	
	tms7000_icount -= 47;
	
}
	
void nop( void )
{
	tms7000_icount -= 4;
}

void or_imp( void )
{
	UINT8	t;
	
	t = RDA | RDB;
	WRA(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 5;
}

void or_r2a( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = RM(v) | RDA;
	WRA(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 8;
}

void or_r2b( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = RM(v) | RDB;
	WRB(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 8;
}

void or_r2r( void )
{
	UINT8	t;
	UINT8	i,j;
	
	IMMBYTE(i);
	IMMBYTE(j);
	
	t = RM(i) | RM(j);
	WM(j,t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 10;
}

void or_i2a( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = v | RDA;
	WRA(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 7;
}

void or_i2b( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = v | RDB;
	WRB(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 7;
}

void or_i2r( void )
{
	UINT8	t;
	UINT8	i,j;
	
	IMMBYTE(i);
	IMMBYTE(j);
	
	t = i | RM(j);
	WM(j,t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 9;
}

void orp_a2p( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	t = RDA | RM( 0x0100 + v);
	WM( 0x0100+v, t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 10;
}	

void orp_b2p( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	t = RDB | RM( 0x0100 + v);
	WM( 0x0100+v, t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 9;
}	

void orp_i2p( void )
{
	UINT8	t;
	UINT8	i,v;
	
	IMMBYTE(i);
	IMMBYTE(v);
	t = i | RM( 0x0100 + v);
	WM( 0x0100+v, t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 11;
}	

void pop_a( void )
{
	UINT16	t;
	
	PULLBYTE(t);
	WRA(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 6;
}	

void pop_b( void )
{
	UINT16	t;
	
	PULLBYTE(t);
	WRB(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 6;
}	

void pop_r( void )
{
	UINT16	t;
	UINT8	r;
	
	IMMBYTE(r);
	PULLBYTE(t);
	WM(r,t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 8;
}	

void pop_st( void )
{
	UINT16	t;
	
	PULLBYTE(t);
	pSR = t;

	tms7000_icount -= 6;
}	

void push_a( void )
{
	UINT16	t;
	
	t = RDA;
	PUSHBYTE(t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 6;
}

void push_b( void )
{
	UINT16	t;
	
	t = RDB;
	PUSHBYTE(t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 6;
}

void push_r( void )
{
	UINT16	t;
	INT8	r;
	
	IMMBYTE(r);
	t = RM(r);
	PUSHBYTE(t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 8;
}

void push_st( void )
{
	UINT16	t;
	t = pSR;
	PUSHBYTE(t);

	tms7000_icount -= 6;
}

void reti( void )
{
	PULLWORD( PC );
	PULLBYTE( pSR );
	
	tms7000_icount -= 9;
}

void rets_imp( void )
{
	PULLWORD( PC );
	tms7000_icount -= 7;
}

void rl_a( void )
{
	UINT16	t;
	
	t = RDA << 1;
	
	CLR_NZC;
	SET_C8(t);
	
	if ((pSR & SR_C) != 0)
		t |= 0x01;

	SET_N8(t);
	SET_Z8(t);
	WRA(t);
	
	tms7000_icount -= 5;
}

void rl_b( void )
{
	UINT16	t;
	
	t = RDB << 1;
	
	CLR_NZC;
	SET_C8(t);
	
	if ((pSR & SR_C) != 0)
		t |= 0x01;

	SET_N8(t);
	SET_Z8(t);
	WRB(t);
	
	tms7000_icount -= 5;
}

void rl_r( void )
{
	UINT16	t;
	UINT8	r;
	
	IMMBYTE(r);
	t = RM(r) << 1;
	
	CLR_NZC;
	SET_C8(t);
	
	if ((pSR & SR_C) != 0)
		t |= 0x01;

	SET_N8(t);
	SET_Z8(t);
	WM(r,t);
	
	tms7000_icount -= 7;
}

void rlc_a( void )
{
	UINT16	t;
	int		old_carry;
	
	old_carry = (pSR & SR_C);
	
	t = RDA << 1;
	
	CLR_NZC;
	SET_C8(t);
	
	if (old_carry != 0)
		t |= 0x01;

	SET_N8(t);
	SET_Z8(t);
	WRA(t);
	
	tms7000_icount -= 5;
}

void rlc_b( void )
{
	UINT16	t;
	int		old_carry;
	
	old_carry = (pSR & SR_C);
	
	t = RDB << 1;
	
	CLR_NZC;
	SET_C8(t);
	
	if (old_carry != 0)
		t |= 0x01;

	SET_N8(t);
	SET_Z8(t);
	WRB(t);
	
	tms7000_icount -= 5;
}

void rlc_r( void )
{
	UINT16	t;
	UINT8	r;
	int		old_carry;
	
	old_carry = (pSR & SR_C);
	
	IMMBYTE(r);
	t = RM(r) << 1;
	
	CLR_NZC;
	SET_C8(t);
	
	if (old_carry != 0)
		t |= 0x01;

	SET_N8(t);
	SET_Z8(t);
	WM(r,t);
	
	tms7000_icount -= 7;
}

void rr_a( void )
{
	UINT16	t;
	int		old_bit0;
	
	t = RDA;
	
	old_bit0 = t & 0x0001;
	t = t >> 1;
	
	CLR_NZC;
	
	if (old_bit0 != 0)
	{
		SETC;
		t |= 0x80;
	}
	
	SET_N8(t);
	SET_Z8(t);
	
	WRA(t);
	
	tms7000_icount -= 5;
}

void rr_b( void )
{
	UINT16	t;
	int		old_bit0;
	
	t = RDB;
	
	old_bit0 = t & 0x0001;
	t = t >> 1;
	
	CLR_NZC;
	
	if (old_bit0 != 0)
	{
		SETC;
		t |= 0x80;
	}
	
	SET_N8(t);
	SET_Z8(t);
	
	WRB(t);
	
	tms7000_icount -= 5;
}

void rr_r( void )
{
	UINT16	t;
	UINT8	r;
	
	int		old_bit0;
	
	IMMBYTE(r);
	t = RM(r);
	
	old_bit0 = t & 0x0001;
	t = t >> 1;
	
	CLR_NZC;
	
	if (old_bit0 != 0)
	{
		SETC;
		t |= 0x80;
	}
	
	SET_N8(t);
	SET_Z8(t);
	
	WM(r,t);
	
	tms7000_icount -= 7;
}

void rrc_a( void )
{
	UINT16	t;
	int		old_bit0;
	
	t = RDA;
	
	old_bit0 = t & 0x0001;
	/* Place carry bit in 9th position */
	t |= ((pSR & SR_C) << 1);
	t = t >> 1;
	
	CLR_NZC;
	
	if (old_bit0 != 0)
		SETC;
	SET_N8(t);
	SET_Z8(t);
	
	WRA(t);
	
	tms7000_icount -= 5;
}

void rrc_b( void )
{
	UINT16	t;
	int		old_bit0;
	
	t = RDB;
	
	old_bit0 = t & 0x0001;
	/* Place carry bit in 9th position */
	t |= ((pSR & SR_C) << 1);
	t = t >> 1;
	
	CLR_NZC;
	
	if (old_bit0 != 0)
		SETC;
	SET_N8(t);
	SET_Z8(t);
	
	WRB(t);
	
	tms7000_icount -= 5;
}

void rrc_r( void )
{
	UINT16	t;
	UINT8	r;
	int		old_bit0;
	
	IMMBYTE(r);
	t = RM(r);
	
	old_bit0 = t & 0x0001;
	/* Place carry bit in 9th position */
	t |= ((pSR & SR_C) << 1);
	t = t >> 1;
	
	CLR_NZC;
	
	if (old_bit0 != 0)
		SETC;
	SET_N8(t);
	SET_Z8(t);
	
	WM(r,t);
	
	tms7000_icount -= 7;
}

void sbb_ba( void )
{
	UINT16	t;
	
	t = RDB - RDA - (pSR & SR_C ? 1 : 0);
	WRA(t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 5;
}

void sbb_ra( void )
{
	UINT16	t;
	UINT8	r;
	
	IMMBYTE(r);
	t = RM(r) - RDA - (pSR & SR_C ? 1 : 0);
	WRA(t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 8;
}

void sbb_rb( void )
{
	UINT16	t;
	UINT8	r;
	
	IMMBYTE(r);
	t = RM(r) - RDB - (pSR & SR_C ? 1 : 0);
	WRB(t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 8;
}

void sbb_rr( void )
{
	UINT16	t;
	UINT8	r,s;
	
	IMMBYTE(s);
	IMMBYTE(r);
	t = RM(s) - RM(r) - (pSR & SR_C ? 1 : 0);
	WM(r,t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 10;
}

void sbb_ia( void )
{
	UINT16	t;
	UINT8	i;
	
	IMMBYTE(i);
	t = i - RDA - (pSR & SR_C ? 1 : 0);
	WRA(t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 7;
}

void sbb_ib( void )
{
	UINT16	t;
	UINT8	i;
	
	IMMBYTE(i);
	t = i - RDB - (pSR & SR_C ? 1 : 0);
	WRB(t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 7;
}

void sbb_ir( void )
{
	UINT16	t;
	UINT8	r,i;
	
	IMMBYTE(i);
	IMMBYTE(r);
	t = i - RM(r) - (pSR & SR_C ? 1 : 0);
	WM(r,t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 9;
}

void setc( void )
{
	CLR_NZC;
	pSR |= (SR_C|SR_Z);
	
	tms7000_icount -= 5;
}
	
void sta_dir( void )
{
	UINT16	t;
	PAIR	i;
	
	t = RDA;
	IMMWORD( i );
	
	WM(i.w.l,t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 11;	
}	

void sta_ind( void )
{
	UINT16	t;
	PAIR	p;
	INT8	r;
	
	IMMBYTE(r);
	p = RRF16(r);
	t = RDA;
	WM(p.w.l,t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 10;	
}	

void sta_inx( void )
{
	UINT16	t;
	PAIR	i;
	
	IMMWORD( i );
	t = RDA;
	WM(i.w.l+RDB,t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);

	tms7000_icount -= 13;
}	

void stsp( void )
{
	WRB(pSP);

	tms7000_icount -= 6;
}	

void sub_ba( void )
{
	UINT16	t;
	
	t = RDB - RDA;
	WRA(t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 5;
}

void sub_ra( void )
{
	UINT16	t;
	UINT8	r;
	
	IMMBYTE(r);
	t = RM(r) - RDA;
	WRA(t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 8;
}

void sub_rb( void )
{
	UINT16	t;
	UINT8	r;
	
	IMMBYTE(r);
	t = RM(r) - RDB;
	WRB(t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 8;
}

void sub_rr( void )
{
	UINT16	t;
	UINT8	r,s;
	
	IMMBYTE(s);
	IMMBYTE(r);
	t = RM(s) - RM(r);
	WM(r,t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 10;
}

void sub_ia( void )
{
	UINT16	t;
	UINT8	i;
	
	IMMBYTE(i);
	t = i - RDA;
	WRA(t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 7;
}

void sub_ib( void )
{
	UINT16	t;
	UINT8	i;
	
	IMMBYTE(i);
	t = i - RDB;
	WRB(t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 7;
}

void sub_ir( void )
{
	UINT16	t;
	UINT8	r,i;
	
	IMMBYTE(i);
	IMMBYTE(r);
	t = i - RM(r);
	WM(r,t);

	CLR_NZC;
	SET_C8(~t);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 9;
}

void trap_0( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xfffe);
	tms7000_icount -= 14;
}
	
void trap_1( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xfffc);
	tms7000_icount -= 14;
}
	
void trap_2( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xfffa);
	tms7000_icount -= 14;
}
	
void trap_3( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xfff8);
	tms7000_icount -= 14;
}
	
void trap_4( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xfff6);
	tms7000_icount -= 14;
}
	
void trap_5( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xfff4);
	tms7000_icount -= 14;
}
	
void trap_6( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xfff2);
	tms7000_icount -= 14;
}
	
void trap_7( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xfff0);
	tms7000_icount -= 14;
}
	
void trap_8( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xffee);
	tms7000_icount -= 14;
}
	
void trap_9( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xffec);
	tms7000_icount -= 14;
}
	
void trap_10( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xffea);
	tms7000_icount -= 14;
}
	
void trap_11( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xffe8);
	tms7000_icount -= 14;
}
	
void trap_12( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xffe6);
	tms7000_icount -= 14;
}
	
void trap_13( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xffe4);
	tms7000_icount -= 14;
}
	
void trap_14( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xffe2);
	tms7000_icount -= 14;
}
	
void trap_15( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xffe0);
	tms7000_icount -= 14;
}
	
void trap_16( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xffde);
	tms7000_icount -= 14;
}
	
void trap_17( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xffdc);
	tms7000_icount -= 14;
}
	
void trap_18( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xffda);
	tms7000_icount -= 14;
}
	
void trap_19( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xffd8);
	tms7000_icount -= 14;
}
	
void trap_20( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xffd6);
	tms7000_icount -= 14;
}
	
void trap_21( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xffd4);
	tms7000_icount -= 14;
}
	
void trap_22( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xffd2);
	tms7000_icount -= 14;
}
	
void trap_23( void )
{
	PUSHWORD( PC );
	pPC = RM16(0xffd0);
	tms7000_icount -= 14;
}

void swap_a( void )
{
	UINT8	a,b;
	UINT16	t;
	
	a = b = RDA;
	
	a <<= 4;
	b >>= 4;
	t = a+b;
	
	WRA(t);
	
	CLR_NZC;
	
	pSR|=((t&0x0001)<<7);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -=8;
}

void swap_b( void )
{
	UINT8	a,b;
	UINT16	t;
	
	a = b = RDB;
	
	a <<= 4;
	b >>= 4;
	t = a+b;
	
	WRB(t);
	
	CLR_NZC;
	
	pSR|=((t&0x0001)<<7);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -=8;
}

void swap_r( void )
{
	UINT8	a,b,r;
	UINT16	t;
	
	IMMBYTE(r);
	a = b = RM(r);
	
	a <<= 4;
	b >>= 4;
	t = a+b;
	
	WM(r,t);
	
	CLR_NZC;
	
	pSR|=((t&0x0001)<<7);
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -=8;
}

void tstb( void )
{
	UINT16	t;
	
	t=RDB;

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 6;
}

void xchb_a( void )
{
	UINT16	t,u;
	
	t = RDB;
	u = RDA;
	
	WRA(t);
	WRB(u);	
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 6;
}	

void xchb_b( void )
{
	UINT16	t;
/*	UINT16	u;	*/
	
	t = RDB;
/*	u = RDB;	*/
	
/*	WRB(t);		*/
/*	WRB(u);		*/
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 6;
}	

void xchb_r( void )
{
	UINT16	t,u;
	UINT8	r;
	
	IMMBYTE(r);
	
	t = RDB;
	u = RM(r);
	
	WRA(t);
	WRB(u);	
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 8;
}	

void xor_imp( void )
{
	UINT8	t;
	
	t = RDA ^ RDB;
	WRA(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 5;
}

void xor_r2a( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = RM(v) ^ RDA;
	WRA(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 8;
}

void xor_r2b( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = RM(v) ^ RDB;
	WRB(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 8;
}

void xor_r2r( void )
{
	UINT8	t;
	UINT8	i,j;
	
	IMMBYTE(i);
	IMMBYTE(j);
	
	t = RM(i) ^ RM(j);
	WM(j,t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 10;
}

void xor_i2a( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = v ^ RDA;
	WRA(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 7;
}

void xor_i2b( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	
	t = v ^ RDB;
	WRB(t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 7;
}

void xor_i2r( void )
{
	UINT8	t;
	UINT8	i,j;
	
	IMMBYTE(i);
	IMMBYTE(j);
	
	t = i ^ RM(j);
	WM(j,t);
	
	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 9;
}

void xorp_a2p( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	t = RDA ^ RM( 0x0100 + v);
	WM( 0x0100+v, t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 10;
}	

void xorp_b2p( void )
{
	UINT8	t;
	UINT8	v;
	
	IMMBYTE(v);
	t = RDB ^ RM( 0x0100 + v);
	WM( 0x0100+v, t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 9;
}	

void xorp_i2p( void )
{
	UINT8	t;
	UINT8	i,v;
	
	IMMBYTE(i);
	IMMBYTE(v);
	t = i ^ RM( 0x0100 + v);
	WM( 0x0100+v, t);

	CLR_NZC;
	SET_N8(t);
	SET_Z8(t);
	
	tms7000_icount -= 11;
}	






