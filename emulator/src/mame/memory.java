/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mame;
/**
 * Ported to 0.56
 */
/**
 *
 * @author jagsanchez
 */
public class memory {

/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///  memory.c
/*TODO*///
/*TODO*///  Functions which handle the CPU memory and I/O port access.
/*TODO*///
/*TODO*///  Caveats:
/*TODO*///
/*TODO*///  * The install_mem/port_*_handler functions are only intended to be
/*TODO*///	called at driver init time. Do not call them after this time.
/*TODO*///
/*TODO*///  * If your driver executes an opcode which crosses a bank-switched
/*TODO*///	boundary, it will pull the wrong data out of memory. Although not
/*TODO*///	a common case, you may need to revert to memcpy to work around this.
/*TODO*///	See machine/tnzs.c for an example.
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///#include "driver.h"
/*TODO*///#include "osd_cpu.h"
/*TODO*///#include "state.h"
/*TODO*///
/*TODO*///#include <stdarg.h>
/*TODO*///
/*TODO*///
/*TODO*/////#define MEM_DUMP
/*TODO*/////#define CHECK_MASKS
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Basic theory of memory handling:
/*TODO*///
/*TODO*///	An address with up to 32 bits is passed to a memory handler. First,
/*TODO*///	the non-significant bits are removed from the bottom; for example,
/*TODO*///	a 16-bit memory handler doesn't care about the low bit, so that is
/*TODO*///	removed.
/*TODO*///
/*TODO*///	Next, the address is broken into two halves, an upper half and a
/*TODO*///	lower half. The number of bits in each half varies based on the
/*TODO*///	total number of address bits. The upper half is then used as an
/*TODO*///	index into the base_lookup table.
/*TODO*///
/*TODO*///	If the value pulled from the table is within the range 192-255, then
/*TODO*///	the lower half of the address is needed to resolve the final handler.
/*TODO*///	The value from the table (192-255) is combined with the lower address
/*TODO*///	bits to form an index into a subtable.
/*TODO*///
/*TODO*///	Table values in the range 0-31 are reserved for internal handling
/*TODO*///	(such as RAM, ROM, NOP, and banking). Table values between 32 and 192
/*TODO*///	are assigned dynamically at startup.
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*////* macros for the profiler */
/*TODO*///#define MEMREADSTART			profiler_mark(PROFILER_MEMREAD);
/*TODO*///#define MEMREADEND(ret)			{ profiler_mark(PROFILER_END); return ret; }
/*TODO*///#define MEMWRITESTART			profiler_mark(PROFILER_MEMWRITE);
/*TODO*///#define MEMWRITEEND(ret)		{ (ret); profiler_mark(PROFILER_END); return; }
/*TODO*///
/*TODO*///#define DATABITS_TO_SHIFT(d)	(((d) == 32) ? 2 : ((d) == 16) ? 1 : 0)
/*TODO*///
/*TODO*////* helper macros */
/*TODO*///#define HANDLER_IS_RAM(h)		((FPTR)(h) == STATIC_RAM)
/*TODO*///#define HANDLER_IS_ROM(h)		((FPTR)(h) == STATIC_ROM)
/*TODO*///#define HANDLER_IS_RAMROM(h)	((FPTR)(h) == STATIC_RAMROM)
/*TODO*///#define HANDLER_IS_NOP(h)		((FPTR)(h) == STATIC_NOP)
/*TODO*///#define HANDLER_IS_BANK(h)		((FPTR)(h) >= STATIC_BANK1 && (FPTR)(h) <= STATIC_BANKMAX)
/*TODO*///#define HANDLER_IS_STATIC(h)	((FPTR)(h) < STATIC_COUNT)
/*TODO*///
/*TODO*///#define HANDLER_TO_BANK(h)		((FPTR)(h))
/*TODO*///#define BANK_TO_HANDLER(b)		((void *)(b))
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	TYPE DEFINITIONS
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///struct bank_data
/*TODO*///{
/*TODO*///	UINT8 				used;				/* is this bank used? */
/*TODO*///	UINT8 				cpunum;				/* the CPU it is used for */
/*TODO*///	offs_t 				base;				/* the base offset */
/*TODO*///	offs_t				readoffset;			/* original base offset for reads */
/*TODO*///	offs_t				writeoffset;		/* original base offset for writes */
/*TODO*///};
/*TODO*///
/*TODO*///struct handler_data
/*TODO*///{
/*TODO*///	void *				handler;			/* function pointer for handler */
/*TODO*///	offs_t				offset;				/* base offset for handler */
/*TODO*///};
/*TODO*///
/*TODO*///struct table_data
/*TODO*///{
/*TODO*///	UINT8 *				table;				/* pointer to base of table */
/*TODO*///	UINT8 				subtable_count;		/* number of subtables used */
/*TODO*///	UINT8 				subtable_alloc;		/* number of subtables allocated */
/*TODO*///	struct handler_data *handlers;			/* pointer to which set of handlers */
/*TODO*///};
/*TODO*///
/*TODO*///struct memport_data
/*TODO*///{
/*TODO*///	int					cpunum;				/* CPU index */
/*TODO*///	int					abits;				/* address bits */
/*TODO*///	int 				dbits;				/* data bits */
/*TODO*///	int					ebits;				/* effective address bits */
/*TODO*///	offs_t				mask;				/* address mask */
/*TODO*///	struct table_data	read;				/* memory read lookup table */
/*TODO*///	struct table_data	write;				/* memory write lookup table */
/*TODO*///};
/*TODO*///
/*TODO*///struct cpu_data
/*TODO*///{
/*TODO*///	void *				rombase;			/* ROM base pointer */
/*TODO*///	void *				rambase;			/* RAM base pointer */
/*TODO*///	opbase_handler 		opbase;				/* opcode base handler */
/*TODO*///
/*TODO*///	struct memport_data	mem;				/* memory tables */
/*TODO*///	struct memport_data	port;				/* port tables */
/*TODO*///};
/*TODO*///
/*TODO*///struct memory_address_table
/*TODO*///{
/*TODO*///	int 				bits;				/* address bits */
/*TODO*///	read8_handler		handler;			/* handler associated with that */
/*TODO*///};
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	GLOBAL VARIABLES
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///UINT8 *						OP_ROM;							/* opcode ROM base */
/*TODO*///UINT8 *						OP_RAM;							/* opcode RAM base */
/*TODO*///UINT8		 				opcode_entry;					/* opcode readmem entry */
/*TODO*///
/*TODO*///UINT8 *						readmem_lookup;					/* memory read lookup table */
/*TODO*///static UINT8 *				writemem_lookup;				/* memory write lookup table */
/*TODO*///static UINT8 *				readport_lookup;				/* port read lookup table */
/*TODO*///static UINT8 *				writeport_lookup;				/* port write lookup table */
/*TODO*///
/*TODO*///offs_t						mem_amask;						/* memory address mask */
/*TODO*///static offs_t				port_amask;						/* port address mask */
/*TODO*///
/*TODO*///UINT8 *						cpu_bankbase[STATIC_COUNT];		/* array of bank bases */
/*TODO*///struct ExtMemory			ext_memory[MAX_EXT_MEMORY];		/* externally-allocated memory */
/*TODO*///
/*TODO*///static opbase_handler		opbasefunc;						/* opcode base override */
/*TODO*///
/*TODO*///static struct handler_data 	rmemhandler8[ENTRY_COUNT];		/* 8-bit memory read handlers */
/*TODO*///static struct handler_data 	rmemhandler16[ENTRY_COUNT];		/* 16-bit memory read handlers */
/*TODO*///static struct handler_data 	rmemhandler32[ENTRY_COUNT];		/* 32-bit memory read handlers */
/*TODO*///static struct handler_data 	wmemhandler8[ENTRY_COUNT];		/* 8-bit memory write handlers */
/*TODO*///static struct handler_data 	wmemhandler16[ENTRY_COUNT];		/* 16-bit memory write handlers */
/*TODO*///static struct handler_data 	wmemhandler32[ENTRY_COUNT];		/* 32-bit memory write handlers */
/*TODO*///
/*TODO*///static struct handler_data 	rporthandler8[ENTRY_COUNT];		/* 8-bit port read handlers */
/*TODO*///static struct handler_data 	rporthandler16[ENTRY_COUNT];	/* 16-bit port read handlers */
/*TODO*///static struct handler_data 	rporthandler32[ENTRY_COUNT];	/* 32-bit port read handlers */
/*TODO*///static struct handler_data 	wporthandler8[ENTRY_COUNT];		/* 8-bit port write handlers */
/*TODO*///static struct handler_data 	wporthandler16[ENTRY_COUNT];	/* 16-bit port write handlers */
/*TODO*///static struct handler_data 	wporthandler32[ENTRY_COUNT];	/* 32-bit port write handlers */
/*TODO*///
/*TODO*///static read8_handler 		rmemhandler8s[STATIC_COUNT];	/* copy of 8-bit static read memory handlers */
/*TODO*///static write8_handler 		wmemhandler8s[STATIC_COUNT];	/* copy of 8-bit static write memory handlers */
/*TODO*///
/*TODO*///static struct cpu_data 		cpudata[MAX_CPU];				/* data gathered for each CPU */
/*TODO*///static struct bank_data 	bankdata[MAX_BANKS];			/* data gathered for each bank */
/*TODO*///
/*TODO*///offs_t encrypted_opcode_start[MAX_CPU],encrypted_opcode_end[MAX_CPU];
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	PROTOTYPES
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int CLIB_DECL fatalerror(const char *string, ...);
/*TODO*///static UINT8 get_handler_index(struct handler_data *table, void *handler, offs_t start);
/*TODO*///static UINT8 alloc_new_subtable(const struct memport_data *memport, struct table_data *tabledata, UINT8 previous_value);
/*TODO*///static void populate_table(struct memport_data *memport, int iswrite, offs_t start, offs_t stop, UINT8 handler);
/*TODO*///static void *assign_dynamic_bank(int cpunum, offs_t start);
/*TODO*///static void install_mem_handler(struct memport_data *memport, int iswrite, offs_t start, offs_t end, void *handler);
/*TODO*///static void install_port_handler(struct memport_data *memport, int iswrite, offs_t start, offs_t end, void *handler);
/*TODO*///static void set_static_handler(int idx,
/*TODO*///		read8_handler r8handler, read16_handler r16handler, read32_handler r32handler,
/*TODO*///		write8_handler w8handler, write16_handler w16handler, write32_handler w32handler);
/*TODO*///static int init_cpudata(void);
/*TODO*///static int init_memport(int cpunum, struct memport_data *data, int abits, int dbits, int ismemory);
/*TODO*///static int verify_memory(void);
/*TODO*///static int verify_ports(void);
/*TODO*///static int allocate_memory(void);
/*TODO*///static int populate_memory(void);
/*TODO*///static int populate_ports(void);
/*TODO*///static void register_banks(void);
/*TODO*///static int mem_address_bits_of_cpu(int cpunum);
/*TODO*///static int port_address_bits_of_cpu(int cpunum);
/*TODO*///static int init_static(void);
/*TODO*///
/*TODO*///#ifdef MEM_DUMP
/*TODO*///static void mem_dump(void);
/*TODO*///#endif
/*TODO*///#ifdef CHECK_MASKS
/*TODO*///static void verify_masks(void);
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	memory_init - initialize the memory system
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///int memory_init(void)
/*TODO*///{
/*TODO*///#ifdef CHECK_MASKS
/*TODO*///	verify_masks();
/*TODO*///#endif
/*TODO*///
/*TODO*///	/* init the static handlers */
/*TODO*///	if (!init_static())
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	/* init the CPUs */
/*TODO*///	if (!init_cpudata())
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	/* verify the memory handlers and check banks */
/*TODO*///	if (!verify_memory())
/*TODO*///		return 0;
/*TODO*///	if (!verify_ports())
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	/* allocate memory for sparse address spaces */
/*TODO*///	if (!allocate_memory())
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	/* then fill in the tables */
/*TODO*///	if (!populate_memory())
/*TODO*///		return 0;
/*TODO*///	if (!populate_ports())
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	register_banks();
/*TODO*///
/*TODO*///#ifdef MEM_DUMP
/*TODO*///	/* dump the final memory configuration */
/*TODO*///	mem_dump();
/*TODO*///#endif
/*TODO*///
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	memory_shutdown - free memory
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void memory_shutdown(void)
/*TODO*///{
/*TODO*///	struct ExtMemory *ext;
/*TODO*///	int cpunum;
/*TODO*///
/*TODO*///	/* free all the tables */
/*TODO*///	for (cpunum = 0; cpunum < MAX_CPU; cpunum++ )
/*TODO*///	{
/*TODO*///		if (cpudata[cpunum].mem.read.table)
/*TODO*///			free(cpudata[cpunum].mem.read.table);
/*TODO*///		if (cpudata[cpunum].mem.write.table)
/*TODO*///			free(cpudata[cpunum].mem.write.table);
/*TODO*///		if (cpudata[cpunum].port.read.table)
/*TODO*///			free(cpudata[cpunum].port.read.table);
/*TODO*///		if (cpudata[cpunum].port.write.table)
/*TODO*///			free(cpudata[cpunum].port.write.table);
/*TODO*///	}
/*TODO*///	memset(&cpudata, 0, sizeof(cpudata));
/*TODO*///
/*TODO*///	/* free all the external memory */
/*TODO*///	for (ext = ext_memory; ext->data; ext++)
/*TODO*///		free(ext->data);
/*TODO*///	memset(ext_memory, 0, sizeof(ext_memory));
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	memory_set_opcode_base - set the base of
/*TODO*///	ROM
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void memory_set_opcode_base(int cpunum, void *base)
/*TODO*///{
/*TODO*///	cpudata[cpunum].rombase = base;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///void memory_set_encrypted_opcode_range(int cpunum,offs_t min_address,offs_t max_address)
/*TODO*///{
/*TODO*///	encrypted_opcode_start[cpunum] = min_address;
/*TODO*///	encrypted_opcode_end[cpunum] = max_address;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	memory_set_context - set the memory context
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void memory_set_context(int activecpu)
/*TODO*///{
/*TODO*///	OP_RAM = cpu_bankbase[STATIC_RAM] = cpudata[activecpu].rambase;
/*TODO*///	OP_ROM = cpudata[activecpu].rombase;
/*TODO*///	opcode_entry = STATIC_ROM;
/*TODO*///
/*TODO*///	readmem_lookup = cpudata[activecpu].mem.read.table;
/*TODO*///	writemem_lookup = cpudata[activecpu].mem.write.table;
/*TODO*///	readport_lookup = cpudata[activecpu].port.read.table;
/*TODO*///	writeport_lookup = cpudata[activecpu].port.write.table;
/*TODO*///
/*TODO*///	mem_amask = cpudata[activecpu].mem.mask;
/*TODO*///	port_amask = cpudata[activecpu].port.mask;
/*TODO*///
/*TODO*///	opbasefunc = cpudata[activecpu].opbase;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	memory_set_bankhandler_r - set readmemory
/*TODO*///	handler for bank memory (8-bit only!)
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void memory_set_bankhandler_r(int bank, offs_t offset, mem_read_handler handler)
/*TODO*///{
/*TODO*///	/* determine the new offset */
/*TODO*///	if (HANDLER_IS_RAM(handler) || HANDLER_IS_ROM(handler))
/*TODO*///		rmemhandler8[bank].offset = 0 - offset, handler = (mem_read_handler)STATIC_RAM;
/*TODO*///	else if (HANDLER_IS_BANK(handler))
/*TODO*///		rmemhandler8[bank].offset = bankdata[HANDLER_TO_BANK(handler)].readoffset - offset;
/*TODO*///	else
/*TODO*///		rmemhandler8[bank].offset = bankdata[bank].readoffset - offset;
/*TODO*///
/*TODO*///	/* set the new handler */
/*TODO*///	if (HANDLER_IS_STATIC(handler))
/*TODO*///		handler = rmemhandler8s[(FPTR)handler];
/*TODO*///	rmemhandler8[bank].handler = (void *)handler;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	memory_set_bankhandler_w - set writememory
/*TODO*///	handler for bank memory (8-bit only!)
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void memory_set_bankhandler_w(int bank, offs_t offset, mem_write_handler handler)
/*TODO*///{
/*TODO*///	/* determine the new offset */
/*TODO*///	if (HANDLER_IS_RAM(handler) || HANDLER_IS_ROM(handler) || HANDLER_IS_RAMROM(handler))
/*TODO*///		wmemhandler8[bank].offset = 0 - offset;
/*TODO*///	else if (HANDLER_IS_BANK(handler))
/*TODO*///		wmemhandler8[bank].offset = bankdata[HANDLER_TO_BANK(handler)].writeoffset - offset;
/*TODO*///	else
/*TODO*///		wmemhandler8[bank].offset = bankdata[bank].writeoffset - offset;
/*TODO*///
/*TODO*///	/* set the new handler */
/*TODO*///	if (HANDLER_IS_STATIC(handler))
/*TODO*///		handler = wmemhandler8s[(FPTR)handler];
/*TODO*///	wmemhandler8[bank].handler = (void *)handler;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	memory_set_opbase_handler - change op-code
/*TODO*///	memory base
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///opbase_handler memory_set_opbase_handler(int cpunum, opbase_handler function)
/*TODO*///{
/*TODO*///	opbase_handler old = cpudata[cpunum].opbase;
/*TODO*///	cpudata[cpunum].opbase = function;
/*TODO*///	if (cpunum == cpu_getactivecpu())
/*TODO*///		opbasefunc = function;
/*TODO*///	return old;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	install_mem_read_handler - install dynamic
/*TODO*///	read handler for 8-bit case
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///data8_t *install_mem_read_handler(int cpunum, offs_t start, offs_t end, mem_read_handler handler)
/*TODO*///{
/*TODO*///	/* sanity check */
/*TODO*///	if (cpudata[cpunum].mem.dbits != 8)
/*TODO*///	{
/*TODO*///		printf("fatal: install_mem_read_handler called on %d-bit cpu\n",cpudata[cpunum].mem.dbits);
/*TODO*///		exit(1);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* install the handler */
/*TODO*///	install_mem_handler(&cpudata[cpunum].mem, 0, start, end, (void *)handler);
/*TODO*///#ifdef MEM_DUMP
/*TODO*///	/* dump the new memory configuration */
/*TODO*///	mem_dump();
/*TODO*///#endif
/*TODO*///	return memory_find_base(cpunum, start);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	install_mem_read16_handler - install dynamic
/*TODO*///	read handler for 16-bit case
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///data16_t *install_mem_read16_handler(int cpunum, offs_t start, offs_t end, mem_read16_handler handler)
/*TODO*///{
/*TODO*///	/* sanity check */
/*TODO*///	if (cpudata[cpunum].mem.dbits != 16)
/*TODO*///	{
/*TODO*///		printf("fatal: install_mem_read16_handler called on %d-bit cpu\n",cpudata[cpunum].mem.dbits);
/*TODO*///		exit(1);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* install the handler */
/*TODO*///	install_mem_handler(&cpudata[cpunum].mem, 0, start, end, (void *)handler);
/*TODO*///#ifdef MEM_DUMP
/*TODO*///	/* dump the new memory configuration */
/*TODO*///	mem_dump();
/*TODO*///#endif
/*TODO*///	return memory_find_base(cpunum, start);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	install_mem_read32_handler - install dynamic
/*TODO*///	read handler for 32-bit case
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///data32_t *install_mem_read32_handler(int cpunum, offs_t start, offs_t end, mem_read32_handler handler)
/*TODO*///{
/*TODO*///	/* sanity check */
/*TODO*///	if (cpudata[cpunum].mem.dbits != 32)
/*TODO*///	{
/*TODO*///		printf("fatal: install_mem_read32_handler called on %d-bit cpu\n",cpudata[cpunum].mem.dbits);
/*TODO*///		exit(1);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* install the handler */
/*TODO*///	install_mem_handler(&cpudata[cpunum].mem, 0, start, end, (void *)handler);
/*TODO*///#ifdef MEM_DUMP
/*TODO*///	/* dump the new memory configuration */
/*TODO*///	mem_dump();
/*TODO*///#endif
/*TODO*///	return memory_find_base(cpunum, start);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	install_mem_write_handler - install dynamic
/*TODO*///	read handler for 8-bit case
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///data8_t *install_mem_write_handler(int cpunum, offs_t start, offs_t end, mem_write_handler handler)
/*TODO*///{
/*TODO*///	/* sanity check */
/*TODO*///	if (cpudata[cpunum].mem.dbits != 8)
/*TODO*///	{
/*TODO*///		printf("fatal: install_mem_write_handler called on %d-bit cpu\n",cpudata[cpunum].mem.dbits);
/*TODO*///		exit(1);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* install the handler */
/*TODO*///	install_mem_handler(&cpudata[cpunum].mem, 1, start, end, (void *)handler);
/*TODO*///#ifdef MEM_DUMP
/*TODO*///	/* dump the new memory configuration */
/*TODO*///	mem_dump();
/*TODO*///#endif
/*TODO*///	return memory_find_base(cpunum, start);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	install_mem_write16_handler - install dynamic
/*TODO*///	read handler for 16-bit case
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///data16_t *install_mem_write16_handler(int cpunum, offs_t start, offs_t end, mem_write16_handler handler)
/*TODO*///{
/*TODO*///	/* sanity check */
/*TODO*///	if (cpudata[cpunum].mem.dbits != 16)
/*TODO*///	{
/*TODO*///		printf("fatal: install_mem_write16_handler called on %d-bit cpu\n",cpudata[cpunum].mem.dbits);
/*TODO*///		exit(1);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* install the handler */
/*TODO*///	install_mem_handler(&cpudata[cpunum].mem, 1, start, end, (void *)handler);
/*TODO*///#ifdef MEM_DUMP
/*TODO*///	/* dump the new memory configuration */
/*TODO*///	mem_dump();
/*TODO*///#endif
/*TODO*///	return memory_find_base(cpunum, start);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	install_mem_write32_handler - install dynamic
/*TODO*///	read handler for 32-bit case
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///data32_t *install_mem_write32_handler(int cpunum, offs_t start, offs_t end, mem_write32_handler handler)
/*TODO*///{
/*TODO*///	/* sanity check */
/*TODO*///	if (cpudata[cpunum].mem.dbits != 32)
/*TODO*///	{
/*TODO*///		printf("fatal: install_mem_write32_handler called on %d-bit cpu\n",cpudata[cpunum].mem.dbits);
/*TODO*///		exit(1);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* install the handler */
/*TODO*///	install_mem_handler(&cpudata[cpunum].mem, 1, start, end, (void *)handler);
/*TODO*///#ifdef MEM_DUMP
/*TODO*///	/* dump the new memory configuration */
/*TODO*///	mem_dump();
/*TODO*///#endif
/*TODO*///	return memory_find_base(cpunum, start);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	install_port_read_handler - install dynamic
/*TODO*///	read handler for 8-bit case
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void install_port_read_handler(int cpunum, offs_t start, offs_t end, port_read_handler handler)
/*TODO*///{
/*TODO*///	/* sanity check */
/*TODO*///	if (cpudata[cpunum].port.dbits != 8)
/*TODO*///	{
/*TODO*///		printf("fatal: install_port_read_handler called on %d-bit cpu\n",cpudata[cpunum].port.dbits);
/*TODO*///		exit(1);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* install the handler */
/*TODO*///	install_port_handler(&cpudata[cpunum].port, 0, start, end, (void *)handler);
/*TODO*///#ifdef MEM_DUMP
/*TODO*///	/* dump the new memory configuration */
/*TODO*///	mem_dump();
/*TODO*///#endif
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	install_port_read16_handler - install dynamic
/*TODO*///	read handler for 16-bit case
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void install_port_read16_handler(int cpunum, offs_t start, offs_t end, port_read16_handler handler)
/*TODO*///{
/*TODO*///	/* sanity check */
/*TODO*///	if (cpudata[cpunum].port.dbits != 16)
/*TODO*///	{
/*TODO*///		printf("fatal: install_port_read16_handler called on %d-bit cpu\n",cpudata[cpunum].port.dbits);
/*TODO*///		exit(1);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* install the handler */
/*TODO*///	install_port_handler(&cpudata[cpunum].port, 0, start, end, (void *)handler);
/*TODO*///#ifdef MEM_DUMP
/*TODO*///	/* dump the new memory configuration */
/*TODO*///	mem_dump();
/*TODO*///#endif
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	install_port_read32_handler - install dynamic
/*TODO*///	read handler for 32-bit case
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void install_port_read32_handler(int cpunum, offs_t start, offs_t end, port_read32_handler handler)
/*TODO*///{
/*TODO*///	/* sanity check */
/*TODO*///	if (cpudata[cpunum].port.dbits != 32)
/*TODO*///	{
/*TODO*///		printf("fatal: install_port_read32_handler called on %d-bit cpu\n",cpudata[cpunum].port.dbits);
/*TODO*///		exit(1);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* install the handler */
/*TODO*///	install_port_handler(&cpudata[cpunum].port, 0, start, end, (void *)handler);
/*TODO*///#ifdef MEM_DUMP
/*TODO*///	/* dump the new memory configuration */
/*TODO*///	mem_dump();
/*TODO*///#endif
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	install_port_write_handler - install dynamic
/*TODO*///	read handler for 8-bit case
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void install_port_write_handler(int cpunum, offs_t start, offs_t end, port_write_handler handler)
/*TODO*///{
/*TODO*///	/* sanity check */
/*TODO*///	if (cpudata[cpunum].port.dbits != 8)
/*TODO*///	{
/*TODO*///		printf("fatal: install_port_write_handler called on %d-bit cpu\n",cpudata[cpunum].port.dbits);
/*TODO*///		exit(1);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* install the handler */
/*TODO*///	install_port_handler(&cpudata[cpunum].port, 1, start, end, (void *)handler);
/*TODO*///#ifdef MEM_DUMP
/*TODO*///	/* dump the new memory configuration */
/*TODO*///	mem_dump();
/*TODO*///#endif
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	install_port_write16_handler - install dynamic
/*TODO*///	read handler for 16-bit case
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void install_port_write16_handler(int cpunum, offs_t start, offs_t end, port_write16_handler handler)
/*TODO*///{
/*TODO*///	/* sanity check */
/*TODO*///	if (cpudata[cpunum].port.dbits != 16)
/*TODO*///	{
/*TODO*///		printf("fatal: install_port_write16_handler called on %d-bit cpu\n",cpudata[cpunum].port.dbits);
/*TODO*///		exit(1);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* install the handler */
/*TODO*///	install_port_handler(&cpudata[cpunum].port, 1, start, end, (void *)handler);
/*TODO*///#ifdef MEM_DUMP
/*TODO*///	/* dump the new memory configuration */
/*TODO*///	mem_dump();
/*TODO*///#endif
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	install_port_write32_handler - install dynamic
/*TODO*///	read handler for 32-bit case
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void install_port_write32_handler(int cpunum, offs_t start, offs_t end, port_write32_handler handler)
/*TODO*///{
/*TODO*///	/* sanity check */
/*TODO*///	if (cpudata[cpunum].port.dbits != 32)
/*TODO*///	{
/*TODO*///		printf("fatal: install_port_write32_handler called on %d-bit cpu\n",cpudata[cpunum].port.dbits);
/*TODO*///		exit(1);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* install the handler */
/*TODO*///	install_port_handler(&cpudata[cpunum].port, 1, start, end, (void *)handler);
/*TODO*///#ifdef MEM_DUMP
/*TODO*///	/* dump the new memory configuration */
/*TODO*///	mem_dump();
/*TODO*///#endif
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	fatalerror - display an error message and
/*TODO*///	exit immediately
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///int CLIB_DECL fatalerror(const char *string, ...)
/*TODO*///{
/*TODO*///	va_list arg;
/*TODO*///	va_start(arg, string);
/*TODO*///	vprintf(string, arg);
/*TODO*///	va_end(arg);
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	memory_find_base - return a pointer to the
/*TODO*///	base of RAM associated with the given CPU
/*TODO*///	and offset
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void *memory_find_base(int cpunum, offs_t offset)
/*TODO*///{
/*TODO*///	int region = REGION_CPU1 + cpunum;
/*TODO*///	struct ExtMemory *ext;
/*TODO*///
/*TODO*///	/* look in external memory first */
/*TODO*///	for (ext = ext_memory; ext->data; ext++)
/*TODO*///		if (ext->region == region && ext->start <= offset && ext->end >= offset)
/*TODO*///			return (void *)((UINT8 *)ext->data + (offset - ext->start));
/*TODO*///
/*TODO*///	return (UINT8 *)cpudata[cpunum].rambase + offset;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	get_handler_index - finds the index of a
/*TODO*///	handler, or allocates a new one as necessary
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///UINT8 get_handler_index(struct handler_data *table, void *handler, offs_t start)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///	/* all static handlers are hardcoded */
/*TODO*///	if (HANDLER_IS_STATIC(handler))
/*TODO*///		return (FPTR)handler;
/*TODO*///
/*TODO*///	/* otherwise, we have to search */
/*TODO*///	for (i = STATIC_COUNT; i < SUBTABLE_BASE; i++)
/*TODO*///	{
/*TODO*///		if (table[i].handler == NULL)
/*TODO*///		{
/*TODO*///			table[i].handler = handler;
/*TODO*///			table[i].offset = start;
/*TODO*///		}
/*TODO*///		if (table[i].handler == handler && table[i].offset == start)
/*TODO*///			return i;
/*TODO*///	}
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	alloc_new_subtable - allocates more space
/*TODO*///	for a new subtable
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///UINT8 alloc_new_subtable(const struct memport_data *memport, struct table_data *tabledata, UINT8 previous_value)
/*TODO*///{
/*TODO*///	int l1bits = LEVEL1_BITS(memport->ebits);
/*TODO*///	int l2bits = LEVEL2_BITS(memport->ebits);
/*TODO*///
/*TODO*///	/* make sure we don't run out */
/*TODO*///	if (tabledata->subtable_count + 1 == SUBTABLE_COUNT)
/*TODO*///		fatalerror("error: ran out of memory subtables\n");
/*TODO*///
/*TODO*///	/* allocate more memory if we need to */
/*TODO*///	if (tabledata->subtable_count <= tabledata->subtable_alloc)
/*TODO*///	{
/*TODO*///		tabledata->subtable_alloc += SUBTABLE_ALLOC;
/*TODO*///		tabledata->table = realloc(tabledata->table, (1 << l1bits) + (tabledata->subtable_alloc << l2bits));
/*TODO*///		if (!tabledata->table)
/*TODO*///			fatalerror("error: ran out of memory allocating memory subtable\n");
/*TODO*///	}
/*TODO*///
/*TODO*///	/* initialize the table entries */
/*TODO*///	memset(&tabledata->table[(1 << l1bits) + (tabledata->subtable_count << l2bits)], previous_value, 1 << l2bits);
/*TODO*///
/*TODO*///	/* return the new index */
/*TODO*///	return SUBTABLE_BASE + tabledata->subtable_count++;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	populate_table - assign a memory handler to
/*TODO*///	a range of addresses
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void populate_table(struct memport_data *memport, int iswrite, offs_t start, offs_t stop, UINT8 handler)
/*TODO*///{
/*TODO*///	struct table_data *tabledata = iswrite ? &memport->write : &memport->read;
/*TODO*///	int minbits = DATABITS_TO_SHIFT(memport->dbits);
/*TODO*///	int l1bits = LEVEL1_BITS(memport->ebits);
/*TODO*///	int l2bits = LEVEL2_BITS(memport->ebits);
/*TODO*///	offs_t l2mask = LEVEL2_MASK(memport->ebits);
/*TODO*///	offs_t l1start = start >> (l2bits + minbits);
/*TODO*///	offs_t l2start = (start >> minbits) & l2mask;
/*TODO*///	offs_t l1stop = stop >> (l2bits + minbits);
/*TODO*///	offs_t l2stop = (stop >> minbits) & l2mask;
/*TODO*///	UINT8 subindex;
/*TODO*///
/*TODO*///	/* sanity check */
/*TODO*///	if (start > stop)
/*TODO*///		return;
/*TODO*///
/*TODO*///	/* set the base for non RAM/ROM cases */
/*TODO*///	if (handler != STATIC_RAM && handler != STATIC_ROM && handler != STATIC_RAMROM)
/*TODO*///		tabledata->handlers[handler].offset = start;
/*TODO*///
/*TODO*///	/* remember the base for banks */
/*TODO*///	if (handler >= STATIC_BANK1 && handler <= STATIC_BANKMAX)
/*TODO*///	{
/*TODO*///		if (iswrite)
/*TODO*///			bankdata[handler].writeoffset = start;
/*TODO*///		else
/*TODO*///			bankdata[handler].readoffset = start;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* handle the starting edge if it's not on a block boundary */
/*TODO*///	if (l2start != 0)
/*TODO*///	{
/*TODO*///		/* get the subtable index */
/*TODO*///		subindex = tabledata->table[l1start];
/*TODO*///		if (subindex < SUBTABLE_BASE)
/*TODO*///			subindex = tabledata->table[l1start] = alloc_new_subtable(memport, tabledata, subindex);
/*TODO*///		subindex &= SUBTABLE_MASK;
/*TODO*///
/*TODO*///		/* if the start and stop end within the same block, handle that */
/*TODO*///		if (l1start == l1stop)
/*TODO*///		{
/*TODO*///			memset(&tabledata->table[(1 << l1bits) + (subindex << l2bits) + l2start], handler, l2stop - l2start + 1);
/*TODO*///			return;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* otherwise, fill until the end */
/*TODO*///		memset(&tabledata->table[(1 << l1bits) + (subindex << l2bits) + l2start], handler, (1 << l2bits) - l2start);
/*TODO*///		if (l1start != (offs_t)~0) l1start++;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* handle the trailing edge if it's not on a block boundary */
/*TODO*///	if (l2stop != l2mask)
/*TODO*///	{
/*TODO*///		/* get the subtable index */
/*TODO*///		subindex = tabledata->table[l1stop];
/*TODO*///		if (subindex < SUBTABLE_BASE)
/*TODO*///			subindex = tabledata->table[l1stop] = alloc_new_subtable(memport, tabledata, subindex);
/*TODO*///		subindex &= SUBTABLE_MASK;
/*TODO*///
/*TODO*///		/* fill from the beginning */
/*TODO*///		memset(&tabledata->table[(1 << l1bits) + (subindex << l2bits)], handler, l2stop + 1);
/*TODO*///
/*TODO*///		/* if the start and stop end within the same block, handle that */
/*TODO*///		if (l1start == l1stop)
/*TODO*///			return;
/*TODO*///		if (l1stop != 0) l1stop--;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* now fill in the middle tables */
/*TODO*///	if (l1start <= l1stop)
/*TODO*///		memset(&tabledata->table[l1start], handler, l1stop - l1start + 1);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	assign_dynamic_bank - finds a free or exact
/*TODO*///	matching bank
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void *assign_dynamic_bank(int cpunum, offs_t start)
/*TODO*///{
/*TODO*///	int bank;
/*TODO*///
/*TODO*///	/* special case: never assign a dynamic bank to an offset that */
/*TODO*///	/* intersects the CPU's region; always use RAM for that */
/*TODO*///	if (start < memory_region_length(REGION_CPU1 + cpunum))
/*TODO*///		return (void *)STATIC_RAM;
/*TODO*///
/*TODO*///	/* loop over banks, searching for an exact match or an empty */
/*TODO*///	for (bank = 1; bank <= MAX_BANKS; bank++)
/*TODO*///		if (!bankdata[bank].used || (bankdata[bank].cpunum == cpunum && bankdata[bank].base == start))
/*TODO*///		{
/*TODO*///			bankdata[bank].used = 1;
/*TODO*///			bankdata[bank].cpunum = cpunum;
/*TODO*///			bankdata[bank].base = start;
/*TODO*///			return BANK_TO_HANDLER(bank);
/*TODO*///		}
/*TODO*///
/*TODO*///	/* if we got here, we failed */
/*TODO*///	fatalerror("cpu #%d: ran out of banks for sparse memory regions!\n", cpunum);
/*TODO*///	return NULL;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	install_mem_handler - installs a handler for
/*TODO*///	memory operatinos
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void install_mem_handler(struct memport_data *memport, int iswrite, offs_t start, offs_t end, void *handler)
/*TODO*///{
/*TODO*///	struct table_data *tabledata = iswrite ? &memport->write : &memport->read;
/*TODO*///	UINT8 idx;
/*TODO*///
/*TODO*///	/* translate ROM and RAMROM to RAM here for read cases */
/*TODO*///	if (!iswrite)
/*TODO*///		if (HANDLER_IS_ROM(handler) || HANDLER_IS_RAMROM(handler))
/*TODO*///			handler = (void *)MRA_RAM;
/*TODO*///
/*TODO*///	/* assign banks for sparse memory spaces */
/*TODO*///	if (IS_SPARSE(memport->abits) && HANDLER_IS_RAM(handler))
/*TODO*///		handler = (void *)assign_dynamic_bank(memport->cpunum, start);
/*TODO*///
/*TODO*///	/* set the handler */
/*TODO*///	idx = get_handler_index(tabledata->handlers, handler, start);
/*TODO*///	populate_table(memport, iswrite, start, end, idx);
/*TODO*///
/*TODO*///	/* if this is a bank, set the bankbase as well */
/*TODO*///	if (HANDLER_IS_BANK(handler))
/*TODO*///		cpu_bankbase[HANDLER_TO_BANK(handler)] = memory_find_base(memport->cpunum, start);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	install_port_handler - installs a handler for
/*TODO*///	port operatinos
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void install_port_handler(struct memport_data *memport, int iswrite, offs_t start, offs_t end, void *handler)
/*TODO*///{
/*TODO*///	struct table_data *tabledata = iswrite ? &memport->write : &memport->read;
/*TODO*///	UINT8 idx = get_handler_index(tabledata->handlers, handler, start);
/*TODO*///	populate_table(memport, iswrite, start, end, idx);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	set_static_handler - handy shortcut for
/*TODO*///	setting all 6 handlers for a given index
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void set_static_handler(int idx,
/*TODO*///		read8_handler r8handler, read16_handler r16handler, read32_handler r32handler,
/*TODO*///		write8_handler w8handler, write16_handler w16handler, write32_handler w32handler)
/*TODO*///{
/*TODO*///	rmemhandler8s[idx] = r8handler;
/*TODO*///	wmemhandler8s[idx] = w8handler;
/*TODO*///
/*TODO*///	rmemhandler8[idx].handler = (void *)r8handler;
/*TODO*///	rmemhandler16[idx].handler = (void *)r16handler;
/*TODO*///	rmemhandler32[idx].handler = (void *)r32handler;
/*TODO*///	wmemhandler8[idx].handler = (void *)w8handler;
/*TODO*///	wmemhandler16[idx].handler = (void *)w16handler;
/*TODO*///	wmemhandler32[idx].handler = (void *)w32handler;
/*TODO*///
/*TODO*///	rporthandler8[idx].handler = (void *)r8handler;
/*TODO*///	rporthandler16[idx].handler = (void *)r16handler;
/*TODO*///	rporthandler32[idx].handler = (void *)r32handler;
/*TODO*///	wporthandler8[idx].handler = (void *)w8handler;
/*TODO*///	wporthandler16[idx].handler = (void *)w16handler;
/*TODO*///	wporthandler32[idx].handler = (void *)w32handler;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	init_cpudata - initialize the cpudata
/*TODO*///	structure for each CPU
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int init_cpudata(void)
/*TODO*///{
/*TODO*///	int cpunum;
/*TODO*///
/*TODO*///	/* zap the cpudata structure */
/*TODO*///	memset(&cpudata, 0, sizeof(cpudata));
/*TODO*///
/*TODO*///	/* loop over CPUs */
/*TODO*///	for (cpunum = 0; cpunum < cpu_gettotalcpu(); cpunum++)
/*TODO*///	{
/*TODO*///		int cputype = Machine->drv->cpu[cpunum].cpu_type & ~CPU_FLAGS_MASK;
/*TODO*///
/*TODO*///		/* set the RAM/ROM base */
/*TODO*///		cpudata[cpunum].rambase = cpudata[cpunum].rombase = memory_region(REGION_CPU1 + cpunum);
/*TODO*///		cpudata[cpunum].opbase = NULL;
/*TODO*///		encrypted_opcode_start[cpunum] = 0;
/*TODO*///		encrypted_opcode_end[cpunum] = 0;
/*TODO*///
/*TODO*///		/* initialize the readmem and writemem tables */
/*TODO*///		if (!init_memport(cpunum, &cpudata[cpunum].mem, mem_address_bits_of_cpu(cputype), cpunum_databus_width(cpunum), 1))
/*TODO*///			return 0;
/*TODO*///
/*TODO*///		/* initialize the readport and writeport tables */
/*TODO*///		if (!init_memport(cpunum, &cpudata[cpunum].port, port_address_bits_of_cpu(cputype), cpunum_databus_width(cpunum), 0))
/*TODO*///			return 0;
/*TODO*///
/*TODO*///#if HAS_Z80
/*TODO*///		/* Z80 port mask kludge */
/*TODO*///		if (cputype == CPU_Z80)
/*TODO*///			if (!(Machine->drv->cpu[cpunum].cpu_type & CPU_16BIT_PORT))
/*TODO*///				cpudata[cpunum].port.mask = 0xff;
/*TODO*///#endif
/*TODO*///	}
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	init_memport - initialize the mem/port data
/*TODO*///	structure
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int init_memport(int cpunum, struct memport_data *data, int abits, int dbits, int ismemory)
/*TODO*///{
/*TODO*///	/* determine the address and data bits */
/*TODO*///	data->cpunum = cpunum;
/*TODO*///	data->abits = abits;
/*TODO*///	data->dbits = dbits;
/*TODO*///	data->ebits = abits - DATABITS_TO_SHIFT(dbits);
/*TODO*///	data->mask = 0xffffffffUL >> (32 - abits);
/*TODO*///
/*TODO*///	/* allocate memory */
/*TODO*///	data->read.table = malloc(1 << LEVEL1_BITS(data->ebits));
/*TODO*///	data->write.table = malloc(1 << LEVEL1_BITS(data->ebits));
/*TODO*///	if (!data->read.table)
/*TODO*///		return fatalerror("cpu #%d couldn't allocate read table\n", cpunum);
/*TODO*///	if (!data->write.table)
/*TODO*///		return fatalerror("cpu #%d couldn't allocate write table\n", cpunum);
/*TODO*///
/*TODO*///	/* initialize everything to unmapped */
/*TODO*///	memset(data->read.table, STATIC_UNMAP, 1 << LEVEL1_BITS(data->ebits));
/*TODO*///	memset(data->write.table, STATIC_UNMAP, 1 << LEVEL1_BITS(data->ebits));
/*TODO*///
/*TODO*///	/* initialize the pointers to the handlers */
/*TODO*///	if (ismemory)
/*TODO*///	{
/*TODO*///		data->read.handlers = (dbits == 32) ? rmemhandler32 : (dbits == 16) ? rmemhandler16 : rmemhandler8;
/*TODO*///		data->write.handlers = (dbits == 32) ? wmemhandler32 : (dbits == 16) ? wmemhandler16 : wmemhandler8;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		data->read.handlers = (dbits == 32) ? rporthandler32 : (dbits == 16) ? rporthandler16 : rporthandler8;
/*TODO*///		data->write.handlers = (dbits == 32) ? wporthandler32 : (dbits == 16) ? wporthandler16 : wporthandler8;
/*TODO*///	}
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	verify_memory - verify the memory structs
/*TODO*///	and track which banks are referenced
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int verify_memory(void)
/*TODO*///{
/*TODO*///	int cpunum;
/*TODO*///
/*TODO*///	/* zap the bank data */
/*TODO*///	memset(&bankdata, 0, sizeof(bankdata));
/*TODO*///
/*TODO*///	/* loop over CPUs */
/*TODO*///	for (cpunum = 0; cpunum < cpu_gettotalcpu(); cpunum++)
/*TODO*///	{
/*TODO*///		const struct Memory_ReadAddress *mra = Machine->drv->cpu[cpunum].memory_read;
/*TODO*///		const struct Memory_WriteAddress *mwa = Machine->drv->cpu[cpunum].memory_write;
/*TODO*///		UINT32 width;
/*TODO*///		int bank;
/*TODO*///
/*TODO*///		/* determine the desired width */
/*TODO*///		switch (cpunum_databus_width(cpunum))
/*TODO*///		{
/*TODO*///			case 8:		width = MEMPORT_WIDTH_8;	break;
/*TODO*///			case 16:	width = MEMPORT_WIDTH_16;	break;
/*TODO*///			case 32:	width = MEMPORT_WIDTH_32;	break;
/*TODO*///			default:	return fatalerror("cpu #%d has invalid memory width!\n", cpunum);
/*TODO*///		}
/*TODO*///
/*TODO*///		/* verify the read handlers */
/*TODO*///		if (mra)
/*TODO*///		{
/*TODO*///			/* verify the MEMPORT_READ_START header */
/*TODO*///			if (mra->start == MEMPORT_MARKER && mra->end != 0)
/*TODO*///			{
/*TODO*///				if ((mra->end & MEMPORT_TYPE_MASK) != MEMPORT_TYPE_MEM)
/*TODO*///					return fatalerror("cpu #%d has port handlers in place of memory read handlers!\n", cpunum);
/*TODO*///				if ((mra->end & MEMPORT_DIRECTION_MASK) != MEMPORT_DIRECTION_READ)
/*TODO*///					return fatalerror("cpu #%d has memory write handlers in place of memory read handlers!\n", cpunum);
/*TODO*///				if ((mra->end & MEMPORT_WIDTH_MASK) != width)
/*TODO*///					return fatalerror("cpu #%d uses wrong data width memory handlers! (width = %d, memory = %08x)\n", cpunum,cpunum_databus_width(cpunum),mra->end);
/*TODO*///				mra++;
/*TODO*///			}
/*TODO*///
/*TODO*///			/* track banks used */
/*TODO*///			for ( ; !IS_MEMPORT_END(mra); mra++)
/*TODO*///				if (!IS_MEMPORT_MARKER(mra) && HANDLER_IS_BANK(mra->handler))
/*TODO*///				{
/*TODO*///					bank = HANDLER_TO_BANK(mra->handler);
/*TODO*///					bankdata[bank].used = 1;
/*TODO*///					bankdata[bank].cpunum = -1;
/*TODO*///				}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* verify the write handlers */
/*TODO*///		if (mwa)
/*TODO*///		{
/*TODO*///			/* verify the MEMPORT_WRITE_START header */
/*TODO*///			if (mwa->start == MEMPORT_MARKER && mwa->end != 0)
/*TODO*///			{
/*TODO*///				if ((mwa->end & MEMPORT_TYPE_MASK) != MEMPORT_TYPE_MEM)
/*TODO*///					return fatalerror("cpu #%d has port handlers in place of memory write handlers!\n", cpunum);
/*TODO*///				if ((mwa->end & MEMPORT_DIRECTION_MASK) != MEMPORT_DIRECTION_WRITE)
/*TODO*///					return fatalerror("cpu #%d has memory read handlers in place of memory write handlers!\n", cpunum);
/*TODO*///				if ((mwa->end & MEMPORT_WIDTH_MASK) != width)
/*TODO*///					return fatalerror("cpu #%d uses wrong data width memory handlers! (width = %d, memory = %08x)\n", cpunum,cpunum_databus_width(cpunum),mwa->end);
/*TODO*///				mwa++;
/*TODO*///			}
/*TODO*///
/*TODO*///			/* track banks used */
/*TODO*///			for (; !IS_MEMPORT_END(mwa); mwa++)
/*TODO*///				if (!IS_MEMPORT_MARKER(mwa) && HANDLER_IS_BANK(mwa->handler))
/*TODO*///				{
/*TODO*///					bank = HANDLER_TO_BANK(mwa->handler);
/*TODO*///					bankdata[bank].used = 1;
/*TODO*///					bankdata[bank].cpunum = -1;
/*TODO*///				}
/*TODO*///				mwa++;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	verify_ports - verify the port structs
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int verify_ports(void)
/*TODO*///{
/*TODO*///	int cpunum;
/*TODO*///
/*TODO*///	/* loop over CPUs */
/*TODO*///	for (cpunum = 0; cpunum < cpu_gettotalcpu(); cpunum++)
/*TODO*///	{
/*TODO*///		const struct IO_ReadPort *mra = Machine->drv->cpu[cpunum].port_read;
/*TODO*///		const struct IO_WritePort *mwa = Machine->drv->cpu[cpunum].port_write;
/*TODO*///		UINT32 width;
/*TODO*///
/*TODO*///		/* determine the desired width */
/*TODO*///		switch (cpunum_databus_width(cpunum))
/*TODO*///		{
/*TODO*///			case 8:		width = MEMPORT_WIDTH_8;	break;
/*TODO*///			case 16:	width = MEMPORT_WIDTH_16;	break;
/*TODO*///			case 32:	width = MEMPORT_WIDTH_32;	break;
/*TODO*///			default:	return fatalerror("cpu #%d has invalid memory width!\n", cpunum);
/*TODO*///		}
/*TODO*///
/*TODO*///		/* verify the read handlers */
/*TODO*///		if (mra)
/*TODO*///		{
/*TODO*///			/* verify the PORT_READ_START header */
/*TODO*///			if (mra->start == MEMPORT_MARKER && mra->end != 0)
/*TODO*///			{
/*TODO*///				if ((mra->end & MEMPORT_TYPE_MASK) != MEMPORT_TYPE_IO)
/*TODO*///					return fatalerror("cpu #%d has memory handlers in place of I/O read handlers!\n", cpunum);
/*TODO*///				if ((mra->end & MEMPORT_DIRECTION_MASK) != MEMPORT_DIRECTION_READ)
/*TODO*///					return fatalerror("cpu #%d has port write handlers in place of port read handlers!\n", cpunum);
/*TODO*///				if ((mra->end & MEMPORT_WIDTH_MASK) != width)
/*TODO*///					return fatalerror("cpu #%d uses wrong data width port handlers! (width = %d, memory = %08x)\n", cpunum,cpunum_databus_width(cpunum),mra->end);
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* verify the write handlers */
/*TODO*///		if (mwa)
/*TODO*///		{
/*TODO*///			/* verify the PORT_WRITE_START header */
/*TODO*///			if (mwa->start == MEMPORT_MARKER && mwa->end != 0)
/*TODO*///			{
/*TODO*///				if ((mwa->end & MEMPORT_TYPE_MASK) != MEMPORT_TYPE_IO)
/*TODO*///					return fatalerror("cpu #%d has memory handlers in place of I/O write handlers!\n", cpunum);
/*TODO*///				if ((mwa->end & MEMPORT_DIRECTION_MASK) != MEMPORT_DIRECTION_WRITE)
/*TODO*///					return fatalerror("cpu #%d has port read handlers in place of port write handlers!\n", cpunum);
/*TODO*///				if ((mwa->end & MEMPORT_WIDTH_MASK) != width)
/*TODO*///					return fatalerror("cpu #%d uses wrong data width port handlers! (width = %d, memory = %08x)\n", cpunum,cpunum_databus_width(cpunum),mwa->end);
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	needs_ram - returns true if a given type
/*TODO*///	of memory needs RAM backing it
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int needs_ram(int cpunum, void *handler)
/*TODO*///{
/*TODO*///	/* RAM, ROM, and banks always need RAM */
/*TODO*///	if (HANDLER_IS_RAM(handler) || HANDLER_IS_ROM(handler) || HANDLER_IS_RAMROM(handler) || HANDLER_IS_BANK(handler))
/*TODO*///		return 1;
/*TODO*///
/*TODO*///	/* NOPs never need RAM */
/*TODO*///	else if (HANDLER_IS_NOP(handler))
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	/* otherwise, we only need RAM for sparse memory spaces */
/*TODO*///	else
/*TODO*///		return IS_SPARSE(cpudata[cpunum].mem.abits);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	allocate_memory - allocate memory for
/*TODO*///	sparse CPU address spaces
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int allocate_memory(void)
/*TODO*///{
/*TODO*///	struct ExtMemory *ext = ext_memory;
/*TODO*///	int cpunum;
/*TODO*///
/*TODO*///	/* don't do it for drivers that don't have ROM (MESS needs this) */
/*TODO*///	if (Machine->gamedrv->rom == 0)
/*TODO*///		return 1;
/*TODO*///
/*TODO*///	/* loop over all CPUs */
/*TODO*///	for (cpunum = 0; cpunum < cpu_gettotalcpu(); cpunum++)
/*TODO*///	{
/*TODO*///		int region = REGION_CPU1 + cpunum;
/*TODO*///		int region_length = memory_region(region) ? memory_region_length(region) : 0;
/*TODO*///		int size = region_length;
/*TODO*///
/*TODO*///		/* keep going until we break out */
/*TODO*///		while (1)
/*TODO*///		{
/*TODO*///			const struct Memory_ReadAddress *mra = Machine->drv->cpu[cpunum].memory_read;
/*TODO*///			const struct Memory_WriteAddress *mwa = Machine->drv->cpu[cpunum].memory_write;
/*TODO*///			offs_t lowest = ~0, end, lastend;
/*TODO*///
/*TODO*///			/* find the base of the lowest memory region that extends past the end */
/*TODO*///			for (mra = Machine->drv->cpu[cpunum].memory_read; !IS_MEMPORT_END(mra); mra++)
/*TODO*///				if (!IS_MEMPORT_MARKER(mra))
/*TODO*///					if (mra->end >= size && mra->start < lowest && needs_ram(cpunum, (void *)mra->handler))
/*TODO*///						lowest = mra->start;
/*TODO*///
/*TODO*///			for (mwa = Machine->drv->cpu[cpunum].memory_write; !IS_MEMPORT_END(mwa); mwa++)
/*TODO*///				if (!IS_MEMPORT_MARKER(mwa))
/*TODO*///					if (mwa->end >= size && mwa->start < lowest && (mwa->base || needs_ram(cpunum, (void *)mwa->handler)))
/*TODO*///						lowest = mwa->start;
/*TODO*///
/*TODO*///			/* done if nothing found */
/*TODO*///			if (lowest == ~0)
/*TODO*///				break;
/*TODO*///
/*TODO*///			/* now loop until we find the end of this contiguous block of memory */
/*TODO*///			lastend = ~0;
/*TODO*///			end = lowest;
/*TODO*///			while (end != lastend)
/*TODO*///			{
/*TODO*///				lastend = end;
/*TODO*///
/*TODO*///				/* find the end of the contiguous block of memory */
/*TODO*///				for (mra = Machine->drv->cpu[cpunum].memory_read; !IS_MEMPORT_END(mra); mra++)
/*TODO*///					if (!IS_MEMPORT_MARKER(mra))
/*TODO*///						if (mra->start <= end+1 && mra->end > end && needs_ram(cpunum, (void *)mra->handler))
/*TODO*///							end = mra->end;
/*TODO*///
/*TODO*///				for (mwa = Machine->drv->cpu[cpunum].memory_write; !IS_MEMPORT_END(mwa); mwa++)
/*TODO*///					if (!IS_MEMPORT_MARKER(mwa))
/*TODO*///						if (mwa->start <= end+1 && mwa->end > end && (mwa->base || needs_ram(cpunum, (void *)mwa->handler)))
/*TODO*///							end = mwa->end;
/*TODO*///			}
/*TODO*///
/*TODO*///			/* fill in the data structure */
/*TODO*///			ext->start = lowest;
/*TODO*///			ext->end = end;
/*TODO*///			ext->region = region;
/*TODO*///
/*TODO*///			/* allocate memory */
/*TODO*///			ext->data = malloc(end+1 - lowest);
/*TODO*///			if (!ext->data)
/*TODO*///				fatalerror("malloc(%d) failed (lowest: %x - end: %x)\n", end + 1 - lowest, lowest, end);
/*TODO*///
/*TODO*///			/* reset the memory */
/*TODO*///			memset(ext->data, 0, end+1 - lowest);
/*TODO*///
/*TODO*///			/* prepare for the next loop */
/*TODO*///			size = ext->end + 1;
/*TODO*///			ext++;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	populate_memory - populate the memory mapping
/*TODO*///	tables with entries
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int populate_memory(void)
/*TODO*///{
/*TODO*///	int cpunum;
/*TODO*///
/*TODO*///	/* loop over CPUs */
/*TODO*///	for (cpunum = 0; cpunum < cpu_gettotalcpu(); cpunum++)
/*TODO*///	{
/*TODO*///		const struct Memory_ReadAddress *mra, *mra_start = Machine->drv->cpu[cpunum].memory_read;
/*TODO*///		const struct Memory_WriteAddress *mwa, *mwa_start = Machine->drv->cpu[cpunum].memory_write;
/*TODO*///
/*TODO*///		/* install the read handlers */
/*TODO*///		if (mra_start)
/*TODO*///		{
/*TODO*///			/* first find the end and check for address bits */
/*TODO*///			for (mra = mra_start; !IS_MEMPORT_END(mra); mra++)
/*TODO*///				if (IS_MEMPORT_MARKER(mra) && (mra->end & MEMPORT_ABITS_MASK))
/*TODO*///					cpudata[cpunum].mem.mask = 0xffffffffUL >> (32 - (mra->end & MEMPORT_ABITS_VAL_MASK));
/*TODO*///
/*TODO*///			/* then work backwards */
/*TODO*///			for (mra--; mra >= mra_start; mra--)
/*TODO*///				if (!IS_MEMPORT_MARKER(mra))
/*TODO*///					install_mem_handler(&cpudata[cpunum].mem, 0, mra->start, mra->end, (void *)mra->handler);
/*TODO*///		}
/*TODO*///
/*TODO*///		/* install the write handlers */
/*TODO*///		if (mwa_start)
/*TODO*///		{
/*TODO*///			/* first find the end and check for address bits */
/*TODO*///			for (mwa = mwa_start; !IS_MEMPORT_END(mwa); mwa++)
/*TODO*///				if (IS_MEMPORT_MARKER(mwa) && (mwa->end & MEMPORT_ABITS_MASK))
/*TODO*///					cpudata[cpunum].mem.mask = 0xffffffffUL >> (32 - (mwa->end & MEMPORT_ABITS_VAL_MASK));
/*TODO*///
/*TODO*///			/* then work backwards */
/*TODO*///			for (mwa--; mwa >= mwa_start; mwa--)
/*TODO*///				if (!IS_MEMPORT_MARKER(mwa))
/*TODO*///				{
/*TODO*///					install_mem_handler(&cpudata[cpunum].mem, 1, mwa->start, mwa->end, (void *)mwa->handler);
/*TODO*///					if (mwa->base) *mwa->base = memory_find_base(cpunum, mwa->start);
/*TODO*///					if (mwa->size) *mwa->size = mwa->end - mwa->start + 1;
/*TODO*///				}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	populate_ports - populate the port mapping
/*TODO*///	tables with entries
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int populate_ports(void)
/*TODO*///{
/*TODO*///	int cpunum;
/*TODO*///
/*TODO*///	/* loop over CPUs */
/*TODO*///	for (cpunum = 0; cpunum < cpu_gettotalcpu(); cpunum++)
/*TODO*///	{
/*TODO*///		const struct IO_ReadPort *mra, *mra_start = Machine->drv->cpu[cpunum].port_read;
/*TODO*///		const struct IO_WritePort *mwa, *mwa_start = Machine->drv->cpu[cpunum].port_write;
/*TODO*///
/*TODO*///		/* install the read handlers */
/*TODO*///		if (mra_start)
/*TODO*///		{
/*TODO*///			/* first find the end and check for address bits */
/*TODO*///			for (mra = mra_start; !IS_MEMPORT_END(mra); mra++)
/*TODO*///				if (IS_MEMPORT_MARKER(mra) && (mra->end & MEMPORT_ABITS_MASK))
/*TODO*///					cpudata[cpunum].port.mask = 0xffffffffUL >> (32 - (mra->end & MEMPORT_ABITS_VAL_MASK));
/*TODO*///
/*TODO*///			/* then work backwards */
/*TODO*///			for (mra--; mra != mra_start; mra--)
/*TODO*///				if (!IS_MEMPORT_MARKER(mra))
/*TODO*///					install_port_handler(&cpudata[cpunum].port, 0, mra->start, mra->end, (void *)mra->handler);
/*TODO*///		}
/*TODO*///
/*TODO*///		/* install the write handlers */
/*TODO*///		if (mwa_start)
/*TODO*///		{
/*TODO*///			/* first find the end and check for address bits */
/*TODO*///			for (mwa = mwa_start; !IS_MEMPORT_END(mwa); mwa++)
/*TODO*///				if (IS_MEMPORT_MARKER(mwa) && (mwa->end & MEMPORT_ABITS_MASK))
/*TODO*///					cpudata[cpunum].port.mask = 0xffffffffUL >> (32 - (mwa->end & MEMPORT_ABITS_VAL_MASK));
/*TODO*///
/*TODO*///			/* then work backwards */
/*TODO*///			for (mwa--; mwa != mwa_start; mwa--)
/*TODO*///				if (!IS_MEMPORT_MARKER(mwa))
/*TODO*///					install_port_handler(&cpudata[cpunum].port, 1, mwa->start, mwa->end, (void *)mwa->handler);
/*TODO*///		}
/*TODO*///	}
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	register_banks - Registers all memory banks
/*TODO*///    into the state save system
/*TODO*///-------------------------------------------------*/
/*TODO*///typedef struct rg_map_entry {
/*TODO*///	struct rg_map_entry *next;
/*TODO*///	UINT32 start;
/*TODO*///	UINT32 end;
/*TODO*///	int flags;
/*TODO*///} rg_map_entry;
/*TODO*///
/*TODO*///static rg_map_entry *rg_map = 0;
/*TODO*///
/*TODO*///enum {
/*TODO*///	RG_SAVE_READ  = 0x0001,
/*TODO*///	RG_DROP_READ  = 0x0002,
/*TODO*///	RG_READ_MASK  = 0x00ff,
/*TODO*///
/*TODO*///	RG_SAVE_WRITE = 0x0100,
/*TODO*///	RG_DROP_WRITE = 0x0200,
/*TODO*///	RG_WRITE_MASK = 0xff00
/*TODO*///};
/*TODO*///
/*TODO*///static void rg_add_entry(UINT32 start, UINT32 end, int mode)
/*TODO*///{
/*TODO*///	rg_map_entry **cur;
/*TODO*///	cur = &rg_map;
/*TODO*///	while(*cur && ((*cur)->end < start))
/*TODO*///		cur = &(*cur)->next;
/*TODO*///
/*TODO*///	while(start <= end)
/*TODO*///	{
/*TODO*///		int mask;
/*TODO*///		if(!*cur || ((*cur)->start > start))
/*TODO*///		{
/*TODO*///			rg_map_entry *e = malloc(sizeof(rg_map_entry));
/*TODO*///			e->start = start;
/*TODO*///			e->end = *cur && (*cur)->start <= end ? (*cur)->start - 1 : end;
/*TODO*///			e->flags = mode;
/*TODO*///			e->next = *cur;
/*TODO*///			*cur = e;
/*TODO*///			cur = &(*cur)->next;
/*TODO*///			start = e->end + 1;
/*TODO*///			if(start > end)
/*TODO*///				return;
/*TODO*///		}
/*TODO*///
/*TODO*///		if((*cur)->start < start)
/*TODO*///		{
/*TODO*///			rg_map_entry *e = malloc(sizeof(rg_map_entry));
/*TODO*///			e->start = (*cur)->start;
/*TODO*///			e->end = start - 1;
/*TODO*///			e->flags = (*cur)->flags;
/*TODO*///			e->next = *cur;
/*TODO*///			(*cur)->start = start;
/*TODO*///			*cur = e;
/*TODO*///			cur = &(*cur)->next;
/*TODO*///		}
/*TODO*///
/*TODO*///		if((*cur)->end > end)
/*TODO*///		{
/*TODO*///			rg_map_entry *e = malloc(sizeof(rg_map_entry));
/*TODO*///			e->start = start;
/*TODO*///			e->end = end;
/*TODO*///			e->flags = (*cur)->flags;
/*TODO*///			e->next = *cur;
/*TODO*///			(*cur)->start = end+1;
/*TODO*///			*cur = e;
/*TODO*///		}
/*TODO*///
/*TODO*///		mask = 0;
/*TODO*///
/*TODO*///		if (mode & RG_READ_MASK)
/*TODO*///			mask |= RG_READ_MASK;
/*TODO*///		if (mode & RG_WRITE_MASK)
/*TODO*///			mask |= RG_WRITE_MASK;
/*TODO*///
/*TODO*///		(*cur)->flags = ((*cur)->flags & ~mask) | mode;
/*TODO*///		start = (*cur)->end + 1;
/*TODO*///		cur = &(*cur)->next;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///static void rg_map_clear(void)
/*TODO*///{
/*TODO*///	rg_map_entry *e = rg_map;
/*TODO*///	while(e)
/*TODO*///	{
/*TODO*///		rg_map_entry *n = e->next;
/*TODO*///		free(e);
/*TODO*///		e = n;
/*TODO*///	}
/*TODO*///	rg_map = 0;
/*TODO*///}
/*TODO*///
/*TODO*///static void register_zone(int cpunum, UINT32 start, UINT32 end)
/*TODO*///{
/*TODO*///	char name[256];
/*TODO*///	sprintf (name, "%08x-%08x", start, end);
/*TODO*///	switch (cpunum_databus_width(cpunum))
/*TODO*///	{
/*TODO*///	case 8:
/*TODO*///		state_save_register_UINT8 ("memory", cpunum, name, memory_find_base(cpunum, start), end-start+1);
/*TODO*///		break;
/*TODO*///	case 16:
/*TODO*///		state_save_register_UINT16("memory", cpunum, name, memory_find_base(cpunum, start), (end-start+1)/2);
/*TODO*///		break;
/*TODO*///	case 32:
/*TODO*///		state_save_register_UINT32("memory", cpunum, name, memory_find_base(cpunum, start), (end-start+1)/4);
/*TODO*///		break;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///void register_banks(void)
/*TODO*///{
/*TODO*///	int cpunum, i;
/*TODO*///	int banksize[MAX_BANKS];
/*TODO*///	int bankcpu[MAX_BANKS];
/*TODO*///
/*TODO*///	for (i=0; i<MAX_BANKS; i++)
/*TODO*///	{
/*TODO*///		banksize[i] = 0;
/*TODO*///		bankcpu[i] = -1;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* loop over CPUs */
/*TODO*///	for (cpunum = 0; cpunum < cpu_gettotalcpu(); cpunum++)
/*TODO*///	{
/*TODO*///		const struct Memory_ReadAddress *mra, *mra_start = Machine->drv->cpu[cpunum].memory_read;
/*TODO*///		const struct Memory_WriteAddress *mwa, *mwa_start = Machine->drv->cpu[cpunum].memory_write;
/*TODO*///		int bits = cpudata[cpunum].mem.abits;
/*TODO*/////		int width = cpunum_databus_width(cpunum);
/*TODO*///
/*TODO*///		if (!IS_SPARSE(bits))
/*TODO*///		{
/*TODO*///			UINT32 size = memory_region_length(REGION_CPU1 + cpunum);
/*TODO*///			if (size > (1<<bits))
/*TODO*///				size = 1 << bits;
/*TODO*///			rg_add_entry(0, size-1, RG_SAVE_READ|RG_SAVE_WRITE);
/*TODO*///		}
/*TODO*///
/*TODO*///
/*TODO*///		if (mra_start)
/*TODO*///		{
/*TODO*///			for (mra = mra_start; !IS_MEMPORT_END(mra); mra++);
/*TODO*///			mra--;
/*TODO*///			for (;mra != mra_start; mra--)
/*TODO*///			{
/*TODO*///				if (!IS_MEMPORT_MARKER (mra))
/*TODO*///				{
/*TODO*///					int mode;
/*TODO*///					mem_read_handler h = mra->handler;
/*TODO*///					if (!HANDLER_IS_STATIC (h))
/*TODO*///						mode = RG_DROP_READ;
/*TODO*///					else if (HANDLER_IS_RAM(h))
/*TODO*///						mode = RG_SAVE_READ;
/*TODO*///					else if (HANDLER_IS_ROM(h))
/*TODO*///						mode = RG_DROP_READ;
/*TODO*///					else if (HANDLER_IS_RAMROM(h))
/*TODO*///						mode = RG_SAVE_READ;
/*TODO*///					else if (HANDLER_IS_NOP(h))
/*TODO*///						mode = RG_DROP_READ;
/*TODO*///					else if (HANDLER_IS_BANK(h))
/*TODO*///					{
/*TODO*///						int size = mra->end-mra->start+1;
/*TODO*///						if (banksize[HANDLER_TO_BANK(h)] < size)
/*TODO*///							banksize[HANDLER_TO_BANK(h)] = size;
/*TODO*///						bankcpu[HANDLER_TO_BANK(h)] = cpunum;
/*TODO*///						mode = RG_DROP_READ;
/*TODO*///					}
/*TODO*///					else
/*TODO*///						abort();
/*TODO*///					rg_add_entry(mra->start, mra->end, mode);
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///		if (mwa_start)
/*TODO*///		{
/*TODO*///			for (mwa = mwa_start; !IS_MEMPORT_END(mwa); mwa++);
/*TODO*///			mwa--;
/*TODO*///			for (;mwa != mwa_start; mwa--)
/*TODO*///			{
/*TODO*///				if (!IS_MEMPORT_MARKER (mwa))
/*TODO*///				{
/*TODO*///					int mode;
/*TODO*///					mem_write_handler h = mwa->handler;
/*TODO*///					if (!HANDLER_IS_STATIC (h))
/*TODO*///						mode = mwa->base ? RG_SAVE_WRITE : RG_DROP_WRITE;
/*TODO*///					else if (HANDLER_IS_RAM(h))
/*TODO*///						mode = RG_SAVE_WRITE;
/*TODO*///					else if (HANDLER_IS_ROM(h))
/*TODO*///						mode = RG_DROP_WRITE;
/*TODO*///					else if (HANDLER_IS_RAMROM(h))
/*TODO*///						mode = RG_SAVE_WRITE;
/*TODO*///					else if (HANDLER_IS_NOP(h))
/*TODO*///						mode = RG_DROP_WRITE;
/*TODO*///					else if (HANDLER_IS_BANK(h))
/*TODO*///					{
/*TODO*///						int size = mwa->end-mwa->start+1;
/*TODO*///						if (banksize[HANDLER_TO_BANK(h)] < size)
/*TODO*///							banksize[HANDLER_TO_BANK(h)] = size;
/*TODO*///						bankcpu[HANDLER_TO_BANK(h)] = cpunum;
/*TODO*///						mode = RG_DROP_WRITE;;
/*TODO*///					}
/*TODO*///					else
/*TODO*///						abort();
/*TODO*///					rg_add_entry(mwa->start, mwa->end, mode);
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		{
/*TODO*///			rg_map_entry *e = rg_map;
/*TODO*///			UINT32 start = 0, end = 0;
/*TODO*///			int active = 0;
/*TODO*///			while (e)
/*TODO*///			{
/*TODO*///				if(e && (e->flags & (RG_SAVE_READ|RG_SAVE_WRITE)))
/*TODO*///				{
/*TODO*///					if (!active)
/*TODO*///					{
/*TODO*///						active = 1;
/*TODO*///						start = e->start;
/*TODO*///					}
/*TODO*///					end = e->end;
/*TODO*///				}
/*TODO*///				else if (active)
/*TODO*///				{
/*TODO*///					register_zone (cpunum, start, end);
/*TODO*///					active = 0;
/*TODO*///				}
/*TODO*///
/*TODO*///				if (active && (!e->next || (e->end+1 != e->next->start)))
/*TODO*///				{
/*TODO*///					register_zone (cpunum, start, end);
/*TODO*///					active = 0;
/*TODO*///				}
/*TODO*///				e = e->next;
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		rg_map_clear();
/*TODO*///	}
/*TODO*///
/*TODO*///	for (i=0; i<MAX_BANKS; i++)
/*TODO*///		if (banksize[i])
/*TODO*///			switch (cpunum_databus_width(bankcpu[i]))
/*TODO*///			{
/*TODO*///			case 8:
/*TODO*///				state_save_register_UINT8 ("bank", i, "ram",           cpu_bankbase[i], banksize[i]);
/*TODO*///				break;
/*TODO*///			case 16:
/*TODO*///				state_save_register_UINT16("bank", i, "ram", (UINT16 *)cpu_bankbase[i], banksize[i]/2);
/*TODO*///				break;
/*TODO*///			case 32:
/*TODO*///				state_save_register_UINT32("bank", i, "ram", (UINT32 *)cpu_bankbase[i], banksize[i]/4);
/*TODO*///				break;
/*TODO*///			}
/*TODO*///
/*TODO*///}
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	READBYTE - generic byte-sized read handler
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///#define READBYTE8(name,abits,lookup,handlist,mask)										\
/*TODO*///data8_t name(offs_t address)															\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMREADSTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,0)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,0)];							\
/*TODO*///																						\
/*TODO*///	/* for compatibility with setbankhandler, 8-bit systems */							\
/*TODO*///	/* must call handlers for banks */													\
/*TODO*///	if (entry == STATIC_RAM)															\
/*TODO*///		MEMREADEND(cpu_bankbase[STATIC_RAM][address])									\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		read8_handler handler = (read8_handler)handlist[entry].handler;					\
/*TODO*///		MEMREADEND((*handler)(address - handlist[entry].offset))						\
/*TODO*///	}																					\
/*TODO*///	return 0;																			\
/*TODO*///}																						\
/*TODO*///
/*TODO*///#define READBYTE16BE(name,abits,lookup,handlist,mask)									\
/*TODO*///data8_t name(offs_t address)															\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMREADSTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,1)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,1)];							\
/*TODO*///																						\
/*TODO*///	/* handle banks inline */															\
/*TODO*///	address -= handlist[entry].offset;													\
/*TODO*///	if (entry <= STATIC_RAM)															\
/*TODO*///		MEMREADEND(cpu_bankbase[entry][BYTE_XOR_BE(address)])							\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		int shift = 8 * (~address & 1);													\
/*TODO*///		read16_handler handler = (read16_handler)handlist[entry].handler;				\
/*TODO*///		MEMREADEND((*handler)(address >> 1, ~(0xff << shift)) >> shift)					\
/*TODO*///	}																					\
/*TODO*///	return 0;																			\
/*TODO*///}																						\
/*TODO*///
/*TODO*///#define READBYTE16LE(name,abits,lookup,handlist,mask)									\
/*TODO*///data8_t name(offs_t address)															\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMREADSTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,1)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,1)];							\
/*TODO*///																						\
/*TODO*///	/* handle banks inline */															\
/*TODO*///	address -= handlist[entry].offset;													\
/*TODO*///	if (entry <= STATIC_RAM)															\
/*TODO*///		MEMREADEND(cpu_bankbase[entry][BYTE_XOR_LE(address)])							\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		int shift = 8 * (address & 1);													\
/*TODO*///		read16_handler handler = (read16_handler)handlist[entry].handler;				\
/*TODO*///		MEMREADEND((*handler)(address >> 1, ~(0xff << shift)) >> shift)					\
/*TODO*///	}																					\
/*TODO*///	return 0;																			\
/*TODO*///}																						\
/*TODO*///
/*TODO*///#define READBYTE32BE(name,abits,lookup,handlist,mask)									\
/*TODO*///data8_t name(offs_t address)															\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMREADSTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,2)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,2)];							\
/*TODO*///																						\
/*TODO*///	/* handle banks inline */															\
/*TODO*///	address -= handlist[entry].offset;													\
/*TODO*///	if (entry <= STATIC_RAM)															\
/*TODO*///		MEMREADEND(cpu_bankbase[entry][BYTE4_XOR_BE(address)])							\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		int shift = 8 * (~address & 3);													\
/*TODO*///		read32_handler handler = (read32_handler)handlist[entry].handler;				\
/*TODO*///		MEMREADEND((*handler)(address >> 2, ~(0xff << shift)) >> shift) 				\
/*TODO*///	}																					\
/*TODO*///	return 0;																			\
/*TODO*///}																						\
/*TODO*///
/*TODO*///#define READBYTE32LE(name,abits,lookup,handlist,mask)									\
/*TODO*///data8_t name(offs_t address)															\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMREADSTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,2)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,2)];							\
/*TODO*///																						\
/*TODO*///	/* handle banks inline */															\
/*TODO*///	address -= handlist[entry].offset;													\
/*TODO*///	if (entry <= STATIC_RAM)															\
/*TODO*///		MEMREADEND(cpu_bankbase[entry][BYTE4_XOR_LE(address)])							\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		int shift = 8 * (address & 3);													\
/*TODO*///		read32_handler handler = (read32_handler)handlist[entry].handler;				\
/*TODO*///		MEMREADEND((*handler)(address >> 2, ~(0xff << shift)) >> shift) 				\
/*TODO*///	}																					\
/*TODO*///	return 0;																			\
/*TODO*///}																						\
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	READWORD - generic word-sized read handler
/*TODO*///	(16-bit and 32-bit aligned only!)
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///#define READWORD16(name,abits,lookup,handlist,mask)										\
/*TODO*///data16_t name(offs_t address)															\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMREADSTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,1)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,1)];							\
/*TODO*///																						\
/*TODO*///	/* handle banks inline */															\
/*TODO*///	address -= handlist[entry].offset;													\
/*TODO*///	if (entry <= STATIC_RAM)															\
/*TODO*///		MEMREADEND(*(data16_t *)&cpu_bankbase[entry][address])							\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		read16_handler handler = (read16_handler)handlist[entry].handler;				\
/*TODO*///		MEMREADEND((*handler)(address >> 1,0))										 	\
/*TODO*///	}																					\
/*TODO*///	return 0;																			\
/*TODO*///}																						\
/*TODO*///
/*TODO*///#define READWORD32BE(name,abits,lookup,handlist,mask)									\
/*TODO*///data16_t name(offs_t address)															\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMREADSTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,2)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,2)];							\
/*TODO*///																						\
/*TODO*///	/* handle banks inline */															\
/*TODO*///	address -= handlist[entry].offset;													\
/*TODO*///	if (entry <= STATIC_RAM)															\
/*TODO*///		MEMREADEND(*(data16_t *)&cpu_bankbase[entry][WORD_XOR_BE(address)])				\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		int shift = 8 * (~address & 2);													\
/*TODO*///		read32_handler handler = (read32_handler)handlist[entry].handler;				\
/*TODO*///		MEMREADEND((*handler)(address >> 2, ~(0xffff << shift)) >> shift)				\
/*TODO*///	}																					\
/*TODO*///	return 0;																			\
/*TODO*///}																						\
/*TODO*///
/*TODO*///#define READWORD32LE(name,abits,lookup,handlist,mask)									\
/*TODO*///data16_t name(offs_t address)															\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMREADSTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,2)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,2)];							\
/*TODO*///																						\
/*TODO*///	/* handle banks inline */															\
/*TODO*///	address -= handlist[entry].offset;													\
/*TODO*///	if (entry <= STATIC_RAM)															\
/*TODO*///		MEMREADEND(*(data16_t *)&cpu_bankbase[entry][WORD_XOR_LE(address)])				\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		int shift = 8 * (address & 2);													\
/*TODO*///		read32_handler handler = (read32_handler)handlist[entry].handler;				\
/*TODO*///		MEMREADEND((*handler)(address >> 2, ~(0xffff << shift)) >> shift)				\
/*TODO*///	}																					\
/*TODO*///	return 0;																			\
/*TODO*///}																						\
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	READLONG - generic dword-sized read handler
/*TODO*///	(32-bit aligned only!)
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///#define READLONG32(name,abits,lookup,handlist,mask)										\
/*TODO*///data32_t name(offs_t address)															\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMREADSTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,2)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,2)];							\
/*TODO*///																						\
/*TODO*///	/* handle banks inline */															\
/*TODO*///	address -= handlist[entry].offset;													\
/*TODO*///	if (entry <= STATIC_RAM)															\
/*TODO*///		MEMREADEND(*(data32_t *)&cpu_bankbase[entry][address])							\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		read32_handler handler = (read32_handler)handlist[entry].handler;				\
/*TODO*///		MEMREADEND((*handler)(address >> 2,0))										 	\
/*TODO*///	}																					\
/*TODO*///	return 0;																			\
/*TODO*///}																						\
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	WRITEBYTE - generic byte-sized write handler
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///#define WRITEBYTE8(name,abits,lookup,handlist,mask)										\
/*TODO*///void name(offs_t address, data8_t data)													\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMWRITESTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,0)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,0)];							\
/*TODO*///																						\
/*TODO*///	/* for compatibility with setbankhandler, 8-bit systems */							\
/*TODO*///	/* must call handlers for banks */													\
/*TODO*///	if (entry == (FPTR)MRA_RAM)															\
/*TODO*///		MEMWRITEEND(cpu_bankbase[STATIC_RAM][address] = data)							\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		write8_handler handler = (write8_handler)handlist[entry].handler;				\
/*TODO*///		MEMWRITEEND((*handler)(address - handlist[entry].offset, data))					\
/*TODO*///	}																					\
/*TODO*///}																						\
/*TODO*///
/*TODO*///#define WRITEBYTE16BE(name,abits,lookup,handlist,mask)									\
/*TODO*///void name(offs_t address, data8_t data)													\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMWRITESTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,1)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,1)];							\
/*TODO*///																						\
/*TODO*///	/* handle banks inline */															\
/*TODO*///	address -= handlist[entry].offset;													\
/*TODO*///	if (entry <= STATIC_RAM)															\
/*TODO*///		MEMWRITEEND(cpu_bankbase[entry][BYTE_XOR_BE(address)] = data)					\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		int shift = 8 * (~address & 1);													\
/*TODO*///		write16_handler handler = (write16_handler)handlist[entry].handler;				\
/*TODO*///		MEMWRITEEND((*handler)(address >> 1, data << shift, ~(0xff << shift))) 			\
/*TODO*///	}																					\
/*TODO*///}																						\
/*TODO*///
/*TODO*///#define WRITEBYTE16LE(name,abits,lookup,handlist,mask)									\
/*TODO*///void name(offs_t address, data8_t data)													\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMWRITESTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,1)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,1)];							\
/*TODO*///																						\
/*TODO*///	/* handle banks inline */															\
/*TODO*///	address -= handlist[entry].offset;													\
/*TODO*///	if (entry <= STATIC_RAM)															\
/*TODO*///		MEMWRITEEND(cpu_bankbase[entry][BYTE_XOR_LE(address)] = data)					\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		int shift = 8 * (address & 1);													\
/*TODO*///		write16_handler handler = (write16_handler)handlist[entry].handler;				\
/*TODO*///		MEMWRITEEND((*handler)(address >> 1, data << shift, ~(0xff << shift)))			\
/*TODO*///	}																					\
/*TODO*///}																						\
/*TODO*///
/*TODO*///#define WRITEBYTE32BE(name,abits,lookup,handlist,mask)									\
/*TODO*///void name(offs_t address, data8_t data)													\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMWRITESTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,2)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,2)];							\
/*TODO*///																						\
/*TODO*///	/* handle banks inline */															\
/*TODO*///	address -= handlist[entry].offset;													\
/*TODO*///	if (entry <= STATIC_RAM)															\
/*TODO*///		MEMWRITEEND(cpu_bankbase[entry][BYTE4_XOR_BE(address)] = data)					\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		int shift = 8 * (~address & 3);													\
/*TODO*///		write32_handler handler = (write32_handler)handlist[entry].handler;				\
/*TODO*///		MEMWRITEEND((*handler)(address >> 2, data << shift, ~(0xff << shift))) 			\
/*TODO*///	}																					\
/*TODO*///}																						\
/*TODO*///
/*TODO*///#define WRITEBYTE32LE(name,abits,lookup,handlist,mask)									\
/*TODO*///void name(offs_t address, data8_t data)													\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMWRITESTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,2)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,2)];							\
/*TODO*///																						\
/*TODO*///	/* handle banks inline */															\
/*TODO*///	address -= handlist[entry].offset;													\
/*TODO*///	if (entry <= STATIC_RAM)															\
/*TODO*///		MEMWRITEEND(cpu_bankbase[entry][BYTE4_XOR_LE(address)] = data)					\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		int shift = 8 * (address & 3);													\
/*TODO*///		write32_handler handler = (write32_handler)handlist[entry].handler;				\
/*TODO*///		MEMWRITEEND((*handler)(address >> 2, data << shift, ~(0xff << shift))) 			\
/*TODO*///	}																					\
/*TODO*///}																						\
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	WRITEWORD - generic word-sized write handler
/*TODO*///	(16-bit and 32-bit aligned only!)
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///#define WRITEWORD16(name,abits,lookup,handlist,mask)									\
/*TODO*///void name(offs_t address, data16_t data)												\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMWRITESTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,1)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,1)];							\
/*TODO*///																						\
/*TODO*///	/* handle banks inline */															\
/*TODO*///	address -= handlist[entry].offset;													\
/*TODO*///	if (entry <= STATIC_RAM)															\
/*TODO*///		MEMWRITEEND(*(data16_t *)&cpu_bankbase[entry][address] = data)					\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		write16_handler handler = (write16_handler)handlist[entry].handler;				\
/*TODO*///		MEMWRITEEND((*handler)(address >> 1, data, 0))								 	\
/*TODO*///	}																					\
/*TODO*///}																						\
/*TODO*///
/*TODO*///#define WRITEWORD32BE(name,abits,lookup,handlist,mask)									\
/*TODO*///void name(offs_t address, data16_t data)												\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMWRITESTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,2)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,2)];							\
/*TODO*///																						\
/*TODO*///	/* handle banks inline */															\
/*TODO*///	address -= handlist[entry].offset;													\
/*TODO*///	if (entry <= STATIC_RAM)															\
/*TODO*///		MEMWRITEEND(*(data16_t *)&cpu_bankbase[entry][WORD_XOR_BE(address)] = data)		\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		int shift = 8 * (~address & 2);													\
/*TODO*///		write32_handler handler = (write32_handler)handlist[entry].handler;				\
/*TODO*///		MEMWRITEEND((*handler)(address >> 2, data << shift, ~(0xffff << shift))) 		\
/*TODO*///	}																					\
/*TODO*///}																						\
/*TODO*///
/*TODO*///#define WRITEWORD32LE(name,abits,lookup,handlist,mask)									\
/*TODO*///void name(offs_t address, data16_t data)												\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMWRITESTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,2)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,2)];							\
/*TODO*///																						\
/*TODO*///	/* handle banks inline */															\
/*TODO*///	address -= handlist[entry].offset;													\
/*TODO*///	if (entry <= STATIC_RAM)															\
/*TODO*///		MEMWRITEEND(*(data16_t *)&cpu_bankbase[entry][WORD_XOR_LE(address)] = data)		\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		int shift = 8 * (address & 2);													\
/*TODO*///		write32_handler handler = (write32_handler)handlist[entry].handler;				\
/*TODO*///		MEMWRITEEND((*handler)(address >> 2, data << shift, ~(0xffff << shift))) 		\
/*TODO*///	}																					\
/*TODO*///}																						\
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	WRITELONG - dword-sized write handler
/*TODO*///	(32-bit aligned only!)
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///#define WRITELONG32(name,abits,lookup,handlist,mask)									\
/*TODO*///void name(offs_t address, data32_t data)												\
/*TODO*///{																						\
/*TODO*///	UINT8 entry;																		\
/*TODO*///	MEMWRITESTART																		\
/*TODO*///																						\
/*TODO*///	/* perform lookup */																\
/*TODO*///	address &= mask;																	\
/*TODO*///	entry = lookup[LEVEL1_INDEX(address,abits,2)];										\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = lookup[LEVEL2_INDEX(entry,address,abits,2)];							\
/*TODO*///																						\
/*TODO*///	/* handle banks inline */															\
/*TODO*///	address -= handlist[entry].offset;													\
/*TODO*///	if (entry <= STATIC_RAM)															\
/*TODO*///		MEMWRITEEND(*(data32_t *)&cpu_bankbase[entry][address] = data)					\
/*TODO*///																						\
/*TODO*///	/* fall back to the handler */														\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		write32_handler handler = (write32_handler)handlist[entry].handler;				\
/*TODO*///		MEMWRITEEND((*handler)(address >> 2, data, 0))								 	\
/*TODO*///	}																					\
/*TODO*///}																						\
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	SETOPBASE - generic opcode base changer
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///#define SETOPBASE(name,abits,minbits,table)												\
/*TODO*///void name(offs_t pc)																	\
/*TODO*///{																						\
/*TODO*///	UINT8 *base;																		\
/*TODO*///	UINT8 entry;																		\
/*TODO*///																						\
/*TODO*///	/* allow overrides */																\
/*TODO*///	if (opbasefunc) 																	\
/*TODO*///	{																					\
/*TODO*///		pc = (*opbasefunc)(pc);															\
/*TODO*///		if (pc == ~0)																	\
/*TODO*///			return; 																	\
/*TODO*///	}																					\
/*TODO*///																						\
/*TODO*///	/* perform the lookup */															\
/*TODO*///	pc &= mem_amask;																	\
/*TODO*///	entry = readmem_lookup[LEVEL1_INDEX(pc,abits,minbits)];								\
/*TODO*///	if (entry >= SUBTABLE_BASE)															\
/*TODO*///		entry = readmem_lookup[LEVEL2_INDEX(entry,pc,abits,minbits)];					\
/*TODO*///	opcode_entry = entry;																\
/*TODO*///																						\
/*TODO*///	/* RAM/ROM/RAMROM */																\
/*TODO*///	if (entry >= STATIC_RAM && entry <= STATIC_RAMROM)									\
/*TODO*///		base = cpu_bankbase[STATIC_RAM];												\
/*TODO*///																						\
/*TODO*///	/* banked memory */																	\
/*TODO*///	else if (entry >= STATIC_BANK1 && entry <= STATIC_RAM)								\
/*TODO*///		base = cpu_bankbase[entry];														\
/*TODO*///																						\
/*TODO*///	/* other memory -- could be very slow! */											\
/*TODO*///	else																				\
/*TODO*///	{																					\
/*TODO*///		logerror("cpu #%d (PC=%08X): warning - op-code execute on mapped I/O\n",		\
/*TODO*///					cpu_getactivecpu(), activecpu_get_pc());							\
/*TODO*///		/*base = memory_find_base(cpu_getactivecpu(), pc);*/							\
/*TODO*///		return;																			\
/*TODO*///	}																					\
/*TODO*///																						\
/*TODO*///	/* compute the adjusted base */														\
/*TODO*///	OP_ROM = base - table[entry].offset + (OP_ROM - OP_RAM);							\
/*TODO*///	OP_RAM = base - table[entry].offset;												\
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	GENERATE_HANDLERS - macros to spew out all
/*TODO*///	the handlers needed for a given memory type
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///#define GENERATE_HANDLERS_8BIT(type, abits) \
/*TODO*///	    READBYTE8(cpu_read##type##abits,             abits, read##type##_lookup,  r##type##handler8,  type##_amask) \
/*TODO*///	   WRITEBYTE8(cpu_write##type##abits,            abits, write##type##_lookup, w##type##handler8,  type##_amask)
/*TODO*///
/*TODO*///#define GENERATE_HANDLERS_16BIT_BE(type, abits) \
/*TODO*///	 READBYTE16BE(cpu_read##type##abits##bew,        abits, read##type##_lookup,  r##type##handler16, type##_amask) \
/*TODO*///	   READWORD16(cpu_read##type##abits##bew_word,   abits, read##type##_lookup,  r##type##handler16, type##_amask) \
/*TODO*///	WRITEBYTE16BE(cpu_write##type##abits##bew,       abits, write##type##_lookup, w##type##handler16, type##_amask) \
/*TODO*///	  WRITEWORD16(cpu_write##type##abits##bew_word,  abits, write##type##_lookup, w##type##handler16, type##_amask)
/*TODO*///
/*TODO*///#define GENERATE_HANDLERS_16BIT_LE(type, abits) \
/*TODO*///	 READBYTE16LE(cpu_read##type##abits##lew,        abits, read##type##_lookup,  r##type##handler16, type##_amask) \
/*TODO*///	   READWORD16(cpu_read##type##abits##lew_word,   abits, read##type##_lookup,  r##type##handler16, type##_amask) \
/*TODO*///	WRITEBYTE16LE(cpu_write##type##abits##lew,       abits, write##type##_lookup, w##type##handler16, type##_amask) \
/*TODO*///	  WRITEWORD16(cpu_write##type##abits##lew_word,  abits, write##type##_lookup, w##type##handler16, type##_amask)
/*TODO*///
/*TODO*///#define GENERATE_HANDLERS_32BIT_BE(type, abits) \
/*TODO*///	 READBYTE32BE(cpu_read##type##abits##bedw,       abits, read##type##_lookup,  r##type##handler32, type##_amask) \
/*TODO*///	 READWORD32BE(cpu_read##type##abits##bedw_word,  abits, read##type##_lookup,  r##type##handler32, type##_amask) \
/*TODO*///	   READLONG32(cpu_read##type##abits##bedw_dword, abits, read##type##_lookup,  r##type##handler32, type##_amask) \
/*TODO*///	WRITEBYTE32BE(cpu_write##type##abits##bedw,      abits, write##type##_lookup, w##type##handler32, type##_amask) \
/*TODO*///	WRITEWORD32BE(cpu_write##type##abits##bedw_word, abits, write##type##_lookup, w##type##handler32, type##_amask) \
/*TODO*///	  WRITELONG32(cpu_write##type##abits##bedw_dword,abits, write##type##_lookup, w##type##handler32, type##_amask)
/*TODO*///
/*TODO*///#define GENERATE_HANDLERS_32BIT_LE(type, abits) \
/*TODO*///	 READBYTE32LE(cpu_read##type##abits##ledw,       abits, read##type##_lookup,  r##type##handler32, type##_amask) \
/*TODO*///	 READWORD32LE(cpu_read##type##abits##ledw_word,  abits, read##type##_lookup,  r##type##handler32, type##_amask) \
/*TODO*///	   READLONG32(cpu_read##type##abits##ledw_dword, abits, read##type##_lookup,  r##type##handler32, type##_amask) \
/*TODO*///	WRITEBYTE32LE(cpu_write##type##abits##ledw,      abits, write##type##_lookup, w##type##handler32, type##_amask) \
/*TODO*///	WRITEWORD32LE(cpu_write##type##abits##ledw_word, abits, write##type##_lookup, w##type##handler32, type##_amask) \
/*TODO*///	  WRITELONG32(cpu_write##type##abits##ledw_dword,abits, write##type##_lookup, w##type##handler32, type##_amask)
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	GENERATE_MEM_HANDLERS - memory handler
/*TODO*///	variants of the GENERATE_HANDLERS
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///#define GENERATE_MEM_HANDLERS_8BIT(abits) \
/*TODO*///GENERATE_HANDLERS_8BIT(mem, abits) \
/*TODO*///SETOPBASE(cpu_setopbase##abits,           abits, 0, rmemhandler8)
/*TODO*///
/*TODO*///#define GENERATE_MEM_HANDLERS_16BIT_BE(abits) \
/*TODO*///GENERATE_HANDLERS_16BIT_BE(mem, abits) \
/*TODO*///SETOPBASE(cpu_setopbase##abits##bew,      abits, 1, rmemhandler16)
/*TODO*///
/*TODO*///#define GENERATE_MEM_HANDLERS_16BIT_LE(abits) \
/*TODO*///GENERATE_HANDLERS_16BIT_LE(mem, abits) \
/*TODO*///SETOPBASE(cpu_setopbase##abits##lew,      abits, 1, rmemhandler16)
/*TODO*///
/*TODO*///#define GENERATE_MEM_HANDLERS_32BIT_BE(abits) \
/*TODO*///GENERATE_HANDLERS_32BIT_BE(mem, abits) \
/*TODO*///SETOPBASE(cpu_setopbase##abits##bedw,     abits, 2, rmemhandler32)
/*TODO*///
/*TODO*///#define GENERATE_MEM_HANDLERS_32BIT_LE(abits) \
/*TODO*///GENERATE_HANDLERS_32BIT_LE(mem, abits) \
/*TODO*///SETOPBASE(cpu_setopbase##abits##ledw,     abits, 2, rmemhandler32)
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	GENERATE_PORT_HANDLERS - port handler
/*TODO*///	variants of the GENERATE_HANDLERS
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///#define GENERATE_PORT_HANDLERS_8BIT(abits) \
/*TODO*///GENERATE_HANDLERS_8BIT(port, abits)
/*TODO*///
/*TODO*///#define GENERATE_PORT_HANDLERS_16BIT_BE(abits) \
/*TODO*///GENERATE_HANDLERS_16BIT_BE(port, abits)
/*TODO*///
/*TODO*///#define GENERATE_PORT_HANDLERS_16BIT_LE(abits) \
/*TODO*///GENERATE_HANDLERS_16BIT_LE(port, abits)
/*TODO*///
/*TODO*///#define GENERATE_PORT_HANDLERS_32BIT_BE(abits) \
/*TODO*///GENERATE_HANDLERS_32BIT_BE(port, abits)
/*TODO*///
/*TODO*///#define GENERATE_PORT_HANDLERS_32BIT_LE(abits) \
/*TODO*///GENERATE_HANDLERS_32BIT_LE(port, abits)
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	the memory handlers we need to generate
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///GENERATE_MEM_HANDLERS_8BIT(16)
/*TODO*///GENERATE_MEM_HANDLERS_8BIT(20)
/*TODO*///GENERATE_MEM_HANDLERS_8BIT(21)
/*TODO*///GENERATE_MEM_HANDLERS_8BIT(24)
/*TODO*///
/*TODO*///GENERATE_MEM_HANDLERS_16BIT_BE(16)
/*TODO*///GENERATE_MEM_HANDLERS_16BIT_BE(24)
/*TODO*///GENERATE_MEM_HANDLERS_16BIT_BE(32)
/*TODO*///
/*TODO*///GENERATE_MEM_HANDLERS_16BIT_LE(16)
/*TODO*///GENERATE_MEM_HANDLERS_16BIT_LE(17)
/*TODO*///GENERATE_MEM_HANDLERS_16BIT_LE(24)
/*TODO*///GENERATE_MEM_HANDLERS_16BIT_LE(29)
/*TODO*///GENERATE_MEM_HANDLERS_16BIT_LE(32)
/*TODO*///
/*TODO*///GENERATE_MEM_HANDLERS_32BIT_BE(24)
/*TODO*///GENERATE_MEM_HANDLERS_32BIT_BE(29)
/*TODO*///GENERATE_MEM_HANDLERS_32BIT_BE(32)
/*TODO*///
/*TODO*///GENERATE_MEM_HANDLERS_32BIT_LE(26)
/*TODO*///GENERATE_MEM_HANDLERS_32BIT_LE(29)
/*TODO*///GENERATE_MEM_HANDLERS_32BIT_LE(32)
/*TODO*///
/*TODO*///GENERATE_MEM_HANDLERS_32BIT_BE(18)	/* HACK -- used for pdp-1 */
/*TODO*///
/*TODO*////* make sure you add an entry to this list whenever you add a set of handlers */
/*TODO*///static const struct memory_address_table readmem_to_bits[] =
/*TODO*///{
/*TODO*///	{ 16, cpu_readmem16 },
/*TODO*///	{ 20, cpu_readmem20 },
/*TODO*///	{ 21, cpu_readmem21 },
/*TODO*///	{ 24, cpu_readmem24 },
/*TODO*///
/*TODO*///	{ 16, cpu_readmem16bew },
/*TODO*///	{ 24, cpu_readmem24bew },
/*TODO*///	{ 32, cpu_readmem32bew },
/*TODO*///
/*TODO*///	{ 16, cpu_readmem16lew },
/*TODO*///	{ 17, cpu_readmem17lew },
/*TODO*///	{ 24, cpu_readmem24lew },
/*TODO*///	{ 29, cpu_readmem29lew },
/*TODO*///	{ 32, cpu_readmem32lew },
/*TODO*///
/*TODO*///	{ 24, cpu_readmem24bedw },
/*TODO*///	{ 29, cpu_readmem29bedw },
/*TODO*///	{ 32, cpu_readmem32bedw },
/*TODO*///
/*TODO*///	{ 26, cpu_readmem26ledw },
/*TODO*///	{ 29, cpu_readmem29ledw },
/*TODO*///	{ 32, cpu_readmem32ledw },
/*TODO*///
/*TODO*///	{ 18, cpu_readmem18bedw }
/*TODO*///};
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	the port handlers we need to generate
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///GENERATE_PORT_HANDLERS_8BIT(16)
/*TODO*///
/*TODO*///GENERATE_PORT_HANDLERS_16BIT_BE(16)
/*TODO*///
/*TODO*///GENERATE_PORT_HANDLERS_16BIT_LE(16)
/*TODO*///GENERATE_PORT_HANDLERS_16BIT_LE(24)
/*TODO*///
/*TODO*///GENERATE_PORT_HANDLERS_32BIT_BE(16)
/*TODO*///
/*TODO*///GENERATE_PORT_HANDLERS_32BIT_LE(16)
/*TODO*///GENERATE_PORT_HANDLERS_32BIT_LE(24)
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	get address bits from a read handler
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///int mem_address_bits_of_cpu(int cputype)
/*TODO*///{
/*TODO*///	read8_handler handler = cputype_get_interface(cputype)->memory_read;
/*TODO*///	int	idx;
/*TODO*///
/*TODO*///	/* scan the table */
/*TODO*///	for (idx = 0; idx < sizeof(readmem_to_bits) / sizeof(readmem_to_bits[0]); idx++)
/*TODO*///		if (readmem_to_bits[idx].handler == handler)
/*TODO*///			return readmem_to_bits[idx].bits;
/*TODO*///
/*TODO*///	/* this is a fatal error */
/*TODO*///	fatalerror("CPU #%d memory handlers don't have a table entry in readmem_to_bits!\n");
/*TODO*///	exit(1);
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	get address bits from a read handler
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///int port_address_bits_of_cpu(int cputype)
/*TODO*///{
/*TODO*///#if (HAS_V60)
/*TODO*///	return cputype == CPU_V60 ? 24 : 16;
/*TODO*///#else
/*TODO*///	return 16;
/*TODO*///#endif
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	basic static handlers
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static READ_HANDLER( mrh8_bad )
/*TODO*///{
/*TODO*///	logerror("cpu #%d (PC=%08X): unmapped memory byte read from %08X\n", cpu_getactivecpu(), activecpu_get_pc(), offset);
/*TODO*///	if (activecpu_address_bits() <= SPARSE_THRESH) return cpu_bankbase[STATIC_RAM][offset];
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///static READ16_HANDLER( mrh16_bad )
/*TODO*///{
/*TODO*///	logerror("cpu #%d (PC=%08X): unmapped memory word read from %08X & %04X\n", cpu_getactivecpu(), activecpu_get_pc(), offset*2, mem_mask ^ 0xffff);
/*TODO*///	if (activecpu_address_bits() <= SPARSE_THRESH) return ((data16_t *)cpu_bankbase[STATIC_RAM])[offset];
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///static READ32_HANDLER( mrh32_bad )
/*TODO*///{
/*TODO*///	logerror("cpu #%d (PC=%08X): unmapped memory dword read from %08X & %08X\n", cpu_getactivecpu(), activecpu_get_pc(), offset*4, mem_mask ^ 0xffffffff);
/*TODO*///	if (activecpu_address_bits() <= SPARSE_THRESH) return ((data32_t *)cpu_bankbase[STATIC_RAM])[offset];
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///static WRITE_HANDLER( mwh8_bad )
/*TODO*///{
/*TODO*///	logerror("cpu #%d (PC=%08X): unmapped memory byte write to %08X = %02X\n", cpu_getactivecpu(), activecpu_get_pc(), offset, data);
/*TODO*///	if (activecpu_address_bits() <= SPARSE_THRESH) cpu_bankbase[STATIC_RAM][offset] = data;
/*TODO*///}
/*TODO*///static WRITE16_HANDLER( mwh16_bad )
/*TODO*///{
/*TODO*///	logerror("cpu #%d (PC=%08X): unmapped memory word write to %08X = %04X & %04X\n", cpu_getactivecpu(), activecpu_get_pc(), offset*2, data, mem_mask ^ 0xffff);
/*TODO*///	if (activecpu_address_bits() <= SPARSE_THRESH) COMBINE_DATA(&((data16_t *)cpu_bankbase[STATIC_RAM])[offset]);
/*TODO*///}
/*TODO*///static WRITE32_HANDLER( mwh32_bad )
/*TODO*///{
/*TODO*///	logerror("cpu #%d (PC=%08X): unmapped memory dword write to %08X = %08X & %08X\n", cpu_getactivecpu(), activecpu_get_pc(), offset*4, data, mem_mask ^ 0xffffffff);
/*TODO*///	if (activecpu_address_bits() <= SPARSE_THRESH) COMBINE_DATA(&((data32_t *)cpu_bankbase[STATIC_RAM])[offset]);
/*TODO*///}
/*TODO*///
/*TODO*///static READ_HANDLER( prh8_bad )
/*TODO*///{
/*TODO*///	logerror("cpu #%d (PC=%08X): unmapped port byte read from %08X\n", cpu_getactivecpu(), activecpu_get_pc(), offset);
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///static READ16_HANDLER( prh16_bad )
/*TODO*///{
/*TODO*///	logerror("cpu #%d (PC=%08X): unmapped port word read from %08X & %04X\n", cpu_getactivecpu(), activecpu_get_pc(), offset*2, mem_mask ^ 0xffff);
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///static READ32_HANDLER( prh32_bad )
/*TODO*///{
/*TODO*///	logerror("cpu #%d (PC=%08X): unmapped port dword read from %08X & %08X\n", cpu_getactivecpu(), activecpu_get_pc(), offset*4, mem_mask ^ 0xffffffff);
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///static WRITE_HANDLER( pwh8_bad )
/*TODO*///{
/*TODO*///	logerror("cpu #%d (PC=%08X): unmapped port byte write to %08X = %02X\n", cpu_getactivecpu(), activecpu_get_pc(), offset, data);
/*TODO*///}
/*TODO*///static WRITE16_HANDLER( pwh16_bad )
/*TODO*///{
/*TODO*///	logerror("cpu #%d (PC=%08X): unmapped port word write to %08X = %04X & %04X\n", cpu_getactivecpu(), activecpu_get_pc(), offset*2, data, mem_mask ^ 0xffff);
/*TODO*///}
/*TODO*///static WRITE32_HANDLER( pwh32_bad )
/*TODO*///{
/*TODO*///	logerror("cpu #%d (PC=%08X): unmapped port dword write to %08X = %08X & %08X\n", cpu_getactivecpu(), activecpu_get_pc(), offset*4, data, mem_mask ^ 0xffffffff);
/*TODO*///}
/*TODO*///
/*TODO*///static WRITE_HANDLER( mwh8_rom )       { logerror("cpu #%d (PC=%08X): byte write to ROM %08X = %02X\n", cpu_getactivecpu(), activecpu_get_pc(), offset, data); }
/*TODO*///static WRITE16_HANDLER( mwh16_rom )    { logerror("cpu #%d (PC=%08X): word write to %08X = %04X & %04X\n", cpu_getactivecpu(), activecpu_get_pc(), offset*2, data, mem_mask ^ 0xffff); }
/*TODO*///static WRITE32_HANDLER( mwh32_rom )    { logerror("cpu #%d (PC=%08X): dword write to %08X = %08X & %08X\n", cpu_getactivecpu(), activecpu_get_pc(), offset*4, data, mem_mask ^ 0xffffffff); }
/*TODO*///
/*TODO*///static READ_HANDLER( mrh8_nop )        { return 0; }
/*TODO*///static READ16_HANDLER( mrh16_nop )     { return 0; }
/*TODO*///static READ32_HANDLER( mrh32_nop )     { return 0; }
/*TODO*///
/*TODO*///static WRITE_HANDLER( mwh8_nop )       {  }
/*TODO*///static WRITE16_HANDLER( mwh16_nop )    {  }
/*TODO*///static WRITE32_HANDLER( mwh32_nop )    {  }
/*TODO*///
/*TODO*///static READ_HANDLER( mrh8_ram )        { return cpu_bankbase[STATIC_RAM][offset]; }
/*TODO*///static WRITE_HANDLER( mwh8_ram )       { cpu_bankbase[STATIC_RAM][offset] = data; }
/*TODO*///
/*TODO*///static WRITE_HANDLER( mwh8_ramrom )    { cpu_bankbase[STATIC_RAM][offset] = cpu_bankbase[STATIC_RAM][offset + (OP_ROM - OP_RAM)] = data; }
/*TODO*///static WRITE16_HANDLER( mwh16_ramrom ) { COMBINE_DATA(&cpu_bankbase[STATIC_RAM][offset*2]); COMBINE_DATA(&cpu_bankbase[0][offset*2 + (OP_ROM - OP_RAM)]); }
/*TODO*///static WRITE32_HANDLER( mwh32_ramrom ) { COMBINE_DATA(&cpu_bankbase[STATIC_RAM][offset*4]); COMBINE_DATA(&cpu_bankbase[0][offset*4 + (OP_ROM - OP_RAM)]); }
/*TODO*///
/*TODO*///static READ_HANDLER( mrh8_bank1 )      { return cpu_bankbase[1][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank2 )      { return cpu_bankbase[2][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank3 )      { return cpu_bankbase[3][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank4 )      { return cpu_bankbase[4][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank5 )      { return cpu_bankbase[5][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank6 )      { return cpu_bankbase[6][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank7 )      { return cpu_bankbase[7][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank8 )      { return cpu_bankbase[8][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank9 )      { return cpu_bankbase[9][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank10 )     { return cpu_bankbase[10][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank11 )     { return cpu_bankbase[11][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank12 )     { return cpu_bankbase[12][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank13 )     { return cpu_bankbase[13][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank14 )     { return cpu_bankbase[14][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank15 )     { return cpu_bankbase[15][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank16 )     { return cpu_bankbase[16][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank17 )     { return cpu_bankbase[17][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank18 )     { return cpu_bankbase[18][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank19 )     { return cpu_bankbase[19][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank20 )     { return cpu_bankbase[20][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank21 )     { return cpu_bankbase[21][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank22 )     { return cpu_bankbase[22][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank23 )     { return cpu_bankbase[23][offset]; }
/*TODO*///static READ_HANDLER( mrh8_bank24 )     { return cpu_bankbase[24][offset]; }
/*TODO*///
/*TODO*///static WRITE_HANDLER( mwh8_bank1 )     { cpu_bankbase[1][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank2 )     { cpu_bankbase[2][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank3 )     { cpu_bankbase[3][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank4 )     { cpu_bankbase[4][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank5 )     { cpu_bankbase[5][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank6 )     { cpu_bankbase[6][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank7 )     { cpu_bankbase[7][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank8 )     { cpu_bankbase[8][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank9 )     { cpu_bankbase[9][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank10 )    { cpu_bankbase[10][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank11 )    { cpu_bankbase[11][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank12 )    { cpu_bankbase[12][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank13 )    { cpu_bankbase[13][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank14 )    { cpu_bankbase[14][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank15 )    { cpu_bankbase[15][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank16 )    { cpu_bankbase[16][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank17 )    { cpu_bankbase[17][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank18 )    { cpu_bankbase[18][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank19 )    { cpu_bankbase[19][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank20 )    { cpu_bankbase[20][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank21 )    { cpu_bankbase[21][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank22 )    { cpu_bankbase[22][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank23 )    { cpu_bankbase[23][offset] = data; }
/*TODO*///static WRITE_HANDLER( mwh8_bank24 )    { cpu_bankbase[24][offset] = data; }
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	init_static - sets up the static memory
/*TODO*///	handlers
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int init_static(void)
/*TODO*///{
/*TODO*///	memset(rmemhandler8,  0, sizeof(rmemhandler8));
/*TODO*///	memset(rmemhandler8s, 0, sizeof(rmemhandler8s));
/*TODO*///	memset(rmemhandler16, 0, sizeof(rmemhandler16));
/*TODO*///	memset(rmemhandler32, 0, sizeof(rmemhandler32));
/*TODO*///	memset(wmemhandler8,  0, sizeof(wmemhandler8));
/*TODO*///	memset(wmemhandler8s, 0, sizeof(wmemhandler8s));
/*TODO*///	memset(wmemhandler16, 0, sizeof(wmemhandler16));
/*TODO*///	memset(wmemhandler32, 0, sizeof(wmemhandler32));
/*TODO*///
/*TODO*///	memset(rporthandler8,  0, sizeof(rporthandler8));
/*TODO*///	memset(rporthandler16, 0, sizeof(rporthandler16));
/*TODO*///	memset(rporthandler32, 0, sizeof(rporthandler32));
/*TODO*///	memset(wporthandler8,  0, sizeof(wporthandler8));
/*TODO*///	memset(wporthandler16, 0, sizeof(wporthandler16));
/*TODO*///	memset(wporthandler32, 0, sizeof(wporthandler32));
/*TODO*///
/*TODO*///	set_static_handler(STATIC_BANK1,  mrh8_bank1,  NULL,         NULL,         mwh8_bank1,  NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK2,  mrh8_bank2,  NULL,         NULL,         mwh8_bank2,  NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK3,  mrh8_bank3,  NULL,         NULL,         mwh8_bank3,  NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK4,  mrh8_bank4,  NULL,         NULL,         mwh8_bank4,  NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK5,  mrh8_bank5,  NULL,         NULL,         mwh8_bank5,  NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK6,  mrh8_bank6,  NULL,         NULL,         mwh8_bank6,  NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK7,  mrh8_bank7,  NULL,         NULL,         mwh8_bank7,  NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK8,  mrh8_bank8,  NULL,         NULL,         mwh8_bank8,  NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK9,  mrh8_bank9,  NULL,         NULL,         mwh8_bank9,  NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK10, mrh8_bank10, NULL,         NULL,         mwh8_bank10, NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK11, mrh8_bank11, NULL,         NULL,         mwh8_bank11, NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK12, mrh8_bank12, NULL,         NULL,         mwh8_bank12, NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK13, mrh8_bank13, NULL,         NULL,         mwh8_bank13, NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK14, mrh8_bank14, NULL,         NULL,         mwh8_bank14, NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK15, mrh8_bank15, NULL,         NULL,         mwh8_bank15, NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK16, mrh8_bank16, NULL,         NULL,         mwh8_bank16, NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK17, mrh8_bank17, NULL,         NULL,         mwh8_bank17, NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK18, mrh8_bank18, NULL,         NULL,         mwh8_bank18, NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK19, mrh8_bank19, NULL,         NULL,         mwh8_bank19, NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK20, mrh8_bank20, NULL,         NULL,         mwh8_bank20, NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK21, mrh8_bank21, NULL,         NULL,         mwh8_bank21, NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK22, mrh8_bank22, NULL,         NULL,         mwh8_bank22, NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK23, mrh8_bank23, NULL,         NULL,         mwh8_bank23, NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_BANK24, mrh8_bank24, NULL,         NULL,         mwh8_bank24, NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_UNMAP,  mrh8_bad,    mrh16_bad,    mrh32_bad,    mwh8_bad,    mwh16_bad,    mwh32_bad);
/*TODO*///	set_static_handler(STATIC_NOP,    mrh8_nop,    mrh16_nop,    mrh32_nop,    mwh8_nop,    mwh16_nop,    mwh32_nop);
/*TODO*///	set_static_handler(STATIC_RAM,    mrh8_ram,    NULL,         NULL,         mwh8_ram,    NULL,         NULL);
/*TODO*///	set_static_handler(STATIC_ROM,    NULL,        NULL,         NULL,         mwh8_rom,    mwh16_rom,    mwh32_rom);
/*TODO*///	set_static_handler(STATIC_RAMROM, NULL,        NULL,         NULL,         mwh8_ramrom, mwh16_ramrom, mwh32_ramrom);
/*TODO*///
/*TODO*///	/* override port unmapped handlers */
/*TODO*///	rporthandler8 [STATIC_UNMAP].handler = (void *)prh8_bad;
/*TODO*///	rporthandler16[STATIC_UNMAP].handler = (void *)prh16_bad;
/*TODO*///	rporthandler32[STATIC_UNMAP].handler = (void *)prh32_bad;
/*TODO*///	wporthandler8 [STATIC_UNMAP].handler = (void *)pwh8_bad;
/*TODO*///	wporthandler16[STATIC_UNMAP].handler = (void *)pwh16_bad;
/*TODO*///	wporthandler32[STATIC_UNMAP].handler = (void *)pwh32_bad;
/*TODO*///
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	debugging
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///#ifdef MEM_DUMP
/*TODO*///
/*TODO*///static void dump_map(FILE *file, const struct memport_data *memport, const struct table_data *table)
/*TODO*///{
/*TODO*///	static const char *strings[] =
/*TODO*///	{
/*TODO*///		"invalid",		"bank 1",		"bank 2",		"bank 3",
/*TODO*///		"bank 4",		"bank 5",		"bank 6",		"bank 7",
/*TODO*///		"bank 8",		"bank 9",		"bank 10",		"bank 11",
/*TODO*///		"bank 12",		"bank 13",		"bank 14",		"bank 15",
/*TODO*///		"bank 16",		"bank 17",		"bank 18",		"bank 19",
/*TODO*///		"bank 20",		"bank 21",		"bank 22",		"bank 23",
/*TODO*///		"bank 24",		"RAM",			"ROM",			"RAMROM",
/*TODO*///		"nop",			"unused 1",		"unused 2",		"unmapped"
/*TODO*///	};
/*TODO*///
/*TODO*///	int minbits = DATABITS_TO_SHIFT(memport->dbits);
/*TODO*///	int l1bits = LEVEL1_BITS(memport->ebits);
/*TODO*///	int l2bits = LEVEL2_BITS(memport->ebits);
/*TODO*///	int l1count = 1 << l1bits;
/*TODO*///	int l2count = 1 << l2bits;
/*TODO*///	int i, j;
/*TODO*///
/*TODO*///	fprintf(file, "  Address bits = %d\n", memport->abits);
/*TODO*///	fprintf(file, "     Data bits = %d\n", memport->dbits);
/*TODO*///	fprintf(file, "Effective bits = %d\n", memport->ebits);
/*TODO*///	fprintf(file, "       L1 bits = %d\n", l1bits);
/*TODO*///	fprintf(file, "       L2 bits = %d\n", l2bits);
/*TODO*///	fprintf(file, "  Address mask = %X\n", memport->mask);
/*TODO*///	fprintf(file, "\n");
/*TODO*///
/*TODO*///	for (i = 0; i < l1count; i++)
/*TODO*///	{
/*TODO*///		UINT8 entry = table->table[i];
/*TODO*///		if (entry != STATIC_UNMAP)
/*TODO*///		{
/*TODO*///			fprintf(file, "%05X  %08X-%08X    = %02X: ", i,
/*TODO*///					i << (l2bits + minbits),
/*TODO*///					((i+1) << (l2bits + minbits)) - 1, entry);
/*TODO*///			if (entry < STATIC_COUNT)
/*TODO*///				fprintf(file, "%s [offset=%08X]\n", strings[entry], table->handlers[entry].offset);
/*TODO*///			else if (entry < SUBTABLE_BASE)
/*TODO*///				fprintf(file, "handler(%08X) [offset=%08X]\n", (UINT32)table->handlers[entry].handler, table->handlers[entry].offset);
/*TODO*///			else
/*TODO*///			{
/*TODO*///				fprintf(file, "subtable %d\n", entry & SUBTABLE_MASK);
/*TODO*///				entry &= SUBTABLE_MASK;
/*TODO*///
/*TODO*///				for (j = 0; j < l2count; j++)
/*TODO*///				{
/*TODO*///					UINT8 entry2 = table->table[(1 << l1bits) + (entry << l2bits) + j];
/*TODO*///					if (entry2 != STATIC_UNMAP)
/*TODO*///					{
/*TODO*///						fprintf(file, "   %05X  %08X-%08X = %02X: ", j,
/*TODO*///								(i << (l2bits + minbits)) | (j << minbits),
/*TODO*///								((i << (l2bits + minbits)) | ((j+1) << minbits)) - 1, entry2);
/*TODO*///						if (entry2 < STATIC_COUNT)
/*TODO*///							fprintf(file, "%s [offset=%08X]\n", strings[entry2], table->handlers[entry2].offset);
/*TODO*///						else if (entry2 < SUBTABLE_BASE)
/*TODO*///							fprintf(file, "handler(%08X) [offset=%08X]\n", (UINT32)table->handlers[entry2].handler, table->handlers[entry2].offset);
/*TODO*///						else
/*TODO*///							fprintf(file, "subtable %d???????????\n", entry2 & SUBTABLE_MASK);
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///static void mem_dump(void)
/*TODO*///{
/*TODO*///	FILE *file = fopen("memdump.log", "w");
/*TODO*///	int cpunum;
/*TODO*///
/*TODO*///	/* skip if we can't open the file */
/*TODO*///	if (!file)
/*TODO*///		return;
/*TODO*///
/*TODO*///	/* loop over CPUs */
/*TODO*///	for (cpunum = 0; cpunum < cpu_gettotalcpu(); cpunum++)
/*TODO*///	{
/*TODO*///		/* memory handlers */
/*TODO*///		if (cpudata[cpunum].mem.abits)
/*TODO*///		{
/*TODO*///			fprintf(file, "\n\n"
/*TODO*///			              "===============================\n"
/*TODO*///			              "CPU %d read memory handler dump\n"
/*TODO*///			              "===============================\n", cpunum);
/*TODO*///			dump_map(file, &cpudata[cpunum].mem, &cpudata[cpunum].mem.read);
/*TODO*///
/*TODO*///			fprintf(file, "\n\n"
/*TODO*///			              "================================\n"
/*TODO*///			              "CPU %d write memory handler dump\n"
/*TODO*///			              "================================\n", cpunum);
/*TODO*///			dump_map(file, &cpudata[cpunum].mem, &cpudata[cpunum].mem.write);
/*TODO*///		}
/*TODO*///
/*TODO*///		/* port handlers */
/*TODO*///		if (cpudata[cpunum].port.abits)
/*TODO*///		{
/*TODO*///			fprintf(file, "\n\n"
/*TODO*///			              "=============================\n"
/*TODO*///			              "CPU %d read port handler dump\n"
/*TODO*///			              "=============================\n", cpunum);
/*TODO*///			dump_map(file, &cpudata[cpunum].port, &cpudata[cpunum].port.read);
/*TODO*///
/*TODO*///			fprintf(file, "\n\n"
/*TODO*///			              "==============================\n"
/*TODO*///			              "CPU %d write port handler dump\n"
/*TODO*///			              "==============================\n", cpunum);
/*TODO*///			dump_map(file, &cpudata[cpunum].port, &cpudata[cpunum].port.write);
/*TODO*///		}
/*TODO*///	}
/*TODO*///	fclose(file);
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///#ifdef CHECK_MASKS
/*TODO*///static void *track_buffer[65536];
/*TODO*///static int track_count;
/*TODO*///static int static_count;
/*TODO*///static int track_entry(void *entry, void *handler)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	for (i = 0; i < track_count; i++)
/*TODO*///		if (track_buffer[i] == entry)
/*TODO*///			return 1;
/*TODO*///	track_buffer[track_count++] = entry;
/*TODO*///	if (HANDLER_IS_STATIC(handler))
/*TODO*///		static_count++;
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///static void verify_masks(void)
/*TODO*///{
/*TODO*///	int i, cpunum;
/*TODO*///
/*TODO*///	for (i = 0;drivers[i];i++)
/*TODO*///	{
/*TODO*///		const struct RomModule *romp = drivers[i]->rom;
/*TODO*///		if (romp)
/*TODO*///		{
/*TODO*///			for (cpunum = 0;cpunum < MAX_CPU;cpunum++)
/*TODO*///			{
/*TODO*///				if (drivers[i]->drv->cpu[cpunum].cpu_type)
/*TODO*///				{
/*TODO*///					const struct Memory_ReadAddress *mra = drivers[i]->drv->cpu[cpunum].memory_read;
/*TODO*///					const struct Memory_WriteAddress *mwa = drivers[i]->drv->cpu[cpunum].memory_write;
/*TODO*///					const struct IO_ReadPort *iora = drivers[i]->drv->cpu[cpunum].port_read;
/*TODO*///					const struct IO_WritePort *iowa = drivers[i]->drv->cpu[cpunum].port_write;
/*TODO*///
/*TODO*///					if (mra)
/*TODO*///						for ( ; !IS_MEMPORT_END(mra); mra++)
/*TODO*///							if (!IS_MEMPORT_MARKER(mra))
/*TODO*///							{
/*TODO*///								size_t size = mra->end - mra->start + 1;
/*TODO*///								if (size)
/*TODO*///								{
/*TODO*///									while (!(size & 1)) size >>= 1;
/*TODO*///									if (size != 1)
/*TODO*///									{
/*TODO*///										if (!track_entry((void *)mra, (void *)mra->handler))
/*TODO*///											printf("%s: %s cpu %d readmem inval size  { %08X, %08X }\n", drivers[i]->source_file, drivers[i]->name, cpunum, mra->start, mra->end);
/*TODO*///									}
/*TODO*///								}
/*TODO*///								size--;
/*TODO*///								if ((mra->start & size) != 0)
/*TODO*///								{
/*TODO*///									if (!track_entry((void *)mra, (void *)mra->handler))
/*TODO*///										printf("%s: %s cpu %d readmem inval start { %08X, %08X }\n", drivers[i]->source_file, drivers[i]->name, cpunum, mra->start, mra->end);
/*TODO*///								}
/*TODO*///								if ((mra->end & size) != size)
/*TODO*///								{
/*TODO*///									if (!track_entry((void *)mra, (void *)mra->handler))
/*TODO*///										printf("%s: %s cpu %d readmem inval end  { %08X, %08X }\n", drivers[i]->source_file, drivers[i]->name, cpunum, mra->start, mra->end);
/*TODO*///								}
/*TODO*///							}
/*TODO*///
/*TODO*///					if (mwa)
/*TODO*///						for ( ; !IS_MEMPORT_END(mwa); mwa++)
/*TODO*///							if (!IS_MEMPORT_MARKER(mwa))
/*TODO*///							{
/*TODO*///								size_t size = mwa->end - mwa->start + 1;
/*TODO*///								if (size)
/*TODO*///								{
/*TODO*///									while (!(size & 1)) size >>= 1;
/*TODO*///									if (size != 1)
/*TODO*///									{
/*TODO*///										if (!track_entry((void *)mwa, (void *)mwa->handler))
/*TODO*///											printf("%s: %s cpu %d writemem inval size  { %08X, %08X }\n", drivers[i]->source_file, drivers[i]->name, cpunum, mwa->start, mwa->end);
/*TODO*///									}
/*TODO*///								}
/*TODO*///								size--;
/*TODO*///								if ((mwa->start & size) != 0)
/*TODO*///								{
/*TODO*///									if (!track_entry((void *)mwa, (void *)mwa->handler))
/*TODO*///										printf("%s: %s cpu %d writemem inval start { %08X, %08X }\n", drivers[i]->source_file, drivers[i]->name, cpunum, mwa->start, mwa->end);
/*TODO*///								}
/*TODO*///								if ((mwa->end & size) != size)
/*TODO*///								{
/*TODO*///									if (!track_entry((void *)mwa, (void *)mwa->handler))
/*TODO*///										printf("%s: %s cpu %d writemem inval end  { %08X, %08X }\n", drivers[i]->source_file, drivers[i]->name, cpunum, mwa->start, mwa->end);
/*TODO*///								}
/*TODO*///							}
/*TODO*///
/*TODO*///					if (iora)
/*TODO*///						for ( ; !IS_MEMPORT_END(iora); iora++)
/*TODO*///							if (!IS_MEMPORT_MARKER(iora))
/*TODO*///							{
/*TODO*///								size_t size = iora->end - iora->start + 1;
/*TODO*///								if (size)
/*TODO*///								{
/*TODO*///									while (!(size & 1)) size >>= 1;
/*TODO*///									if (size != 1)
/*TODO*///									{
/*TODO*///										if (!track_entry((void *)iora, (void *)iora->handler))
/*TODO*///											printf("%s: %s cpu %d readmem inval size  { %08X, %08X }\n", drivers[i]->source_file, drivers[i]->name, cpunum, iora->start, iora->end);
/*TODO*///									}
/*TODO*///								}
/*TODO*///								size--;
/*TODO*///								if ((iora->start & size) != 0)
/*TODO*///								{
/*TODO*///									if (!track_entry((void *)iora, (void *)iora->handler))
/*TODO*///										printf("%s: %s cpu %d readmem inval start { %08X, %08X }\n", drivers[i]->source_file, drivers[i]->name, cpunum, iora->start, iora->end);
/*TODO*///								}
/*TODO*///								if ((iora->end & size) != size)
/*TODO*///								{
/*TODO*///									if (!track_entry((void *)iora, (void *)iora->handler))
/*TODO*///										printf("%s: %s cpu %d readmem inval end  { %08X, %08X }\n", drivers[i]->source_file, drivers[i]->name, cpunum, iora->start, iora->end);
/*TODO*///								}
/*TODO*///							}
/*TODO*///
/*TODO*///					if (iowa)
/*TODO*///						for ( ; !IS_MEMPORT_END(iowa); iowa++)
/*TODO*///							if (!IS_MEMPORT_MARKER(iowa))
/*TODO*///							{
/*TODO*///								size_t size = iowa->end - iowa->start + 1;
/*TODO*///								if (size)
/*TODO*///								{
/*TODO*///									while (!(size & 1)) size >>= 1;
/*TODO*///									if (size != 1)
/*TODO*///									{
/*TODO*///										if (!track_entry((void *)iowa, (void *)iowa->handler))
/*TODO*///											printf("%s: %s cpu %d writemem inval size  { %08X, %08X }\n", drivers[i]->source_file, drivers[i]->name, cpunum, iowa->start, iowa->end);
/*TODO*///									}
/*TODO*///								}
/*TODO*///								size--;
/*TODO*///								if ((iowa->start & size) != 0)
/*TODO*///								{
/*TODO*///									if (!track_entry((void *)iowa, (void *)iowa->handler))
/*TODO*///										printf("%s: %s cpu %d writemem inval start { %08X, %08X }\n", drivers[i]->source_file, drivers[i]->name, cpunum, iowa->start, iowa->end);
/*TODO*///								}
/*TODO*///								if ((iowa->end & size) != size)
/*TODO*///								{
/*TODO*///									if (!track_entry((void *)iowa, (void *)iowa->handler))
/*TODO*///										printf("%s: %s cpu %d writemem inval end  { %08X, %08X }\n", drivers[i]->source_file, drivers[i]->name, cpunum, iowa->start, iowa->end);
/*TODO*///								}
/*TODO*///							}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	printf("Total busted entries = %d\n", track_count);
/*TODO*///	printf("Busted entries that are static = %d\n", static_count);
/*TODO*///}
/*TODO*///#endif
    
}

