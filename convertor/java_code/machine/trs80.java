/***************************************************************************

  machine.c

  Functions to emulate general aspects of the machine (RAM, ROM, interrupts,
  I/O ports)

***************************************************************************/
/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package machine;

public class trs80
{
	
	#define VERBOSE 1
	
	#if VERBOSE
	#define LOG(x)	logerror x
	#else
	#define LOG(x)
	#endif
	
	
	UINT8 trs80_port_ff = 0;
	
	#define IRQ_TIMER		0x80
	#define IRQ_FDC 		0x40
	static	UINT8			irq_status = 0;
	
	#define MAX_LUMPS		192 	/* crude storage units - don't now much about it */
	#define MAX_GRANULES	8		/* lumps consisted of granules.. aha */
	#define MAX_SECTORS 	5		/* and granules of sectors */
	
	#if USE_TRACK
	static UINT8 track[4] = {0, };				/* current track per drive */
	#endif
	static UINT8 head;							 /* current head per drive */
	#if USE_SECTOR
	static UINT8 sector[4] = {0, }; 			/* current sector per drive */
	#endif
	static UINT8 irq_mask = 0;
	
	static UINT8 *cas_buff = NULL;
	static UINT32 cas_size = 0;
	
	static UINT8 *cmd_buff = NULL;
	static UINT32 cmd_size = 0;
	
	/* current tape file handles */
	static void *tape_put_file = NULL;
	static void *tape_get_file = NULL;
	
	/* tape buffer for the first eight bytes at write (to extract a filename) */
	static UINT8 tape_buffer[8];
	
	static int tape_count = 0;		/* file offset within tape file */
	static int put_bit_count = 0;	/* number of sync and data bits that were written */
	static int get_bit_count = 0;	/* number of sync and data bits to read */
	static int tape_bits = 0;		/* sync and data bits mask */
	static int tape_time = 0;		/* time in cycles for the next bit at read */
	static int in_sync = 0; 		/* flag if writing to tape detected the sync header A5 already */
	static int put_cycles = 0;		/* cycle count at last output port change */
	static int get_cycles = 0;		/* cycle count at last input port read */
	
	int trs80_videoram_r(int offset);
	void trs80_videoram_w(int offset, int data);
	
	static void tape_put_byte(UINT8 value);
	static static 
	#define FW TRS80_FONT_W
	#define FH TRS80_FONT_H
	
	
	public static InitDriverPtr init_trs80 = new InitDriverPtr() { public void handler() 
	{
		UINT8 *FNT = memory_region(REGION_GFX1);
		int i, y;
	
		for( i = 0x000; i < 0x080; i++ )
		{
			/* copy eight lines from the character generator */
			for (y = 0; y < 8; y++)
				FNT[i*FH+y] = FNT[0x0800+i*8+y] << 3;
			/* wipe out the lower lines (no descenders!) */
			for (y = 8; y < FH; y++)
				FNT[i*FH+y] = 0;
		}
		/* setup the 2x3 chunky block graphics (two times 64 characters) */
		for( i = 0x080; i < 0x100; i++ )
		{
			UINT8 b0, b1, b2, b3, b4, b5;
			b0 = (i & 0x01) ? 0xe0 : 0x00;
			b1 = (i & 0x02) ? 0x1c : 0x00;
			b2 = (i & 0x04) ? 0xe0 : 0x00;
			b3 = (i & 0x08) ? 0x1c : 0x00;
			b4 = (i & 0x10) ? 0xe0 : 0x00;
			b5 = (i & 0x20) ? 0x1c : 0x00;
	
			FNT[i*FH+ 0] = FNT[i*FH+ 1] = FNT[i*FH+ 2] = FNT[i*FH+ 3] = b0 | b1;
			FNT[i*FH+ 4] = FNT[i*FH+ 5] = FNT[i*FH+ 6] = FNT[i*FH+ 7] = b2 | b3;
			FNT[i*FH+ 8] = FNT[i*FH+ 9] = FNT[i*FH+10] = FNT[i*FH+11] = b4 | b5;
		}
	} };
	
	public static timer_callback cas_copy_callback = new timer_callback() { public void handler(int param) 
	{
		UINT16 entry = 0, block_ofs = 0, block_len = 0;
		unsigned offs = 0;
	
		while( cas_size > 3 )
		{
			UINT8 data = cas_buff[offs++];
	
			switch( data )
			{
			case 0x3c:		   /* CAS file header */
				block_len = cas_buff[offs++];
				/* on CMD files size=zero means size 256 */
				if( block_len == 0 )
					block_len += 256;
				block_ofs = cas_buff[offs++];
				block_ofs += 256 * cas_buff[offs++];
				cas_size -= 4;
				LOG(("cas_copy_callback block ($%02X) %d at $%04X\n", data, block_len, block_ofs));
				while( block_len && cas_size )
				{
					cpu_writemem16(block_ofs, cas_buff[offs]);
					offs++;
					block_ofs++;
					block_len--;
					cas_size--;
				}
				/* skip the CHKSUM byte */
				offs++;
				break;
			case 0x78:
				entry = cas_buff[offs++];
				entry += 256 * cas_buff[offs++];
				LOG(("cas_copy_callback entry ($%02X) at $%04X\n", data, entry));
				cas_size -= 3;
				break;
			default:
				cas_size--;
			}
		}
		cas_size = 0;
		cpu_set_reg(Z80_PC, entry);
	} };
	
	int trs80_cas_init(int id)
	{
		void *file = image_fopen(IO_CASSETTE,id,OSD_FILETYPE_IMAGE, OSD_FOPEN_READ);
		if (file != 0)
		{
			cas_size = osd_fsize(file);
			cas_buff = malloc(cas_size);
			if (cas_buff != 0)
			{
				osd_fread(file, cas_buff, cas_size);
				osd_fclose(file);
				if (cas_buff[1] == 0x55)
				{
					LOG(("trs80_cas_init: loading %s size %d\n", device_filename(IO_CASSETTE,id), cas_size));
				}
				else
				{
					free(cas_buff);
					cas_buff = NULL;
					cas_size = 0;
					logerror("trs80_cas_init: CAS file is not in SYSTEM format\n");
					return 1;
				}
			}
			else
			{
				cas_size = 0;
			}
		}
		return 0;
	}
	
	void trs80_cas_exit(int id)
	{
		if (cas_buff != 0)
			free(cas_buff);
		cas_buff = NULL;
		cas_size = 0;
	}
	
	public static timer_callback cmd_copy_callback = new timer_callback() { public void handler(int param) 
	{
		UINT16 entry = 0, block_ofs = 0, block_len = 0;
		unsigned offs = 0;
	
		while( cmd_size > 3 )
		{
			UINT8 data = cmd_buff[offs++];
	
			switch( data )
			{
			case 0x01:		   /* CMD file header */
			case 0x07:		   /* another type of CMD file header */
				block_len = cmd_buff[offs++];
				/* on CMD files size=zero means size 256 */
				if( block_len == 0 )
					block_len += 256;
				block_ofs = cmd_buff[offs++];
				block_ofs += 256 * cmd_buff[offs++];
				block_len -= 2;
				if( block_len == 0 )
					block_len = 256;
				cmd_size -= 4;
				LOG(("trs80_cmd_load block ($%02X) %d at $%04X\n", data, block_len, block_ofs));
				while( block_len && cmd_size )
				{
					cpu_writemem16(block_ofs, cmd_buff[offs]);
					offs++;
					block_ofs++;
					block_len--;
					cmd_size--;
				}
				break;
			case 0x02:
				block_len = cmd_buff[offs++];
				cmd_size -= 1;
				if (entry == 0)
				{
					entry = cmd_buff[offs++];
					entry += 256 * cmd_buff[offs++];
					LOG(("trs80_cmd_load entry ($%02X) at $%04X\n", data, entry));
				}
				else
				{
					UINT16 temp;
					temp = cmd_buff[offs++];
					temp += 256 * cmd_buff[offs++];
					LOG(("trs80_cmd_load 2nd entry ($%02X) at $%04X ignored\n", data, temp));
				}
				cmd_size -= 3;
				break;
			default:
				cmd_size--;
			}
		}
		cmd_size = 0;
		cpu_set_reg(Z80_PC, entry);
	} };
	
	int trs80_cmd_init(int id)
	{
		void *file = image_fopen(IO_QUICKLOAD,id,OSD_FILETYPE_IMAGE, OSD_FOPEN_READ);
		if (file != 0)
		{
			cmd_size = osd_fsize(file);
			cmd_buff = malloc(cmd_size);
			if (cmd_buff != 0)
			{
				LOG(("trs80_cmd_init: loading %s size %d\n", device_filename(IO_QUICKLOAD,id), cmd_size));
				osd_fread(file, cmd_buff, cmd_size);
				osd_fclose(file);
			}
			else
			{
				cmd_size = 0;
			}
		}
		return 0;
	}
	
	void trs80_cmd_exit(int id)
	{
		if (cmd_buff != 0)
			free(cmd_buff);
		cmd_buff = NULL;
		cmd_size = 0;
	}
	
	int trs80_floppy_init(int id)
	{
		static UINT8 pdrive[4*16];
		int i;
		int tracks; 	/* total tracks count per drive */
		int heads;		/* total heads count per drive */
		int spt;		/* sector per track count per drive */
		int dir_sector; /* first directory sector (aka DDSL) */
		int dir_length; /* length of directory in sectors (aka DDGA) */
	    void *file;
	
	    if (basicdsk_floppy_init(id) != INIT_PASS)
			return INIT_FAIL;
	
	    if (id == 0)        /* first floppy? */
		{
			file = image_fopen(IO_FLOPPY, id, OSD_FILETYPE_IMAGE, OSD_FOPEN_READ);
			if (file != 0)
			{
	
	            osd_fseek(file, 0, SEEK_SET);
				osd_fread(file, pdrive, 2);
	#if 0
				if (pdrive[0] != 0x00 || pdrive[1] != 0xfe)
				{
					basicdsk_read_sectormap(id, &tracks[id], &heads[id], &spt[id]);
				}
				else
	#endif
	
				osd_fseek(file, 2 * 256, SEEK_SET);
				osd_fread(file, pdrive, 4*16);
				osd_fclose(file);
			}
		}
	
		tracks = pdrive[id*16+3] + 1;
		heads = (pdrive[id*16+7] & 0x40) ? 2 : 1;
		spt = pdrive[id*16+4] / heads;
		dir_sector = 5 * pdrive[id*16+0] * pdrive[id*16+5];
		dir_length = 5 * pdrive[id*16+9];
	
	    /* set geometry so disk image can be read */
		basicdsk_set_geometry(id, tracks, heads, spt, 256, 0, 0);
	
		/* mark directory sectors with deleted data address mark */
		/* assumption dir_sector is a sector offset */
		for (i = 0; i < dir_length; i++)
		{
			int track, side, sector_id;
			int track_offset, sector_offset;
	
			/* calc sector offset */
			sector_offset = dir_sector + i;
	
			/* get track offset */
			track_offset = sector_offset / spt;
	
			/* calc track */
			track = track_offset / heads;
	
			/* calc side */
			side = track_offset % heads;
	
			/* calc sector id - first sector id is 0! */
			sector_id = sector_offset % spt;
	
			/* set deleted data address mark for sector specified */
			basicdsk_set_ddam(id, track, side, sector_id, 1);
		}
	    return INIT_PASS;
	}
	
	
	public static InitMachinePtr trs80_init_machine = new InitMachinePtr() { public void handler() 
	{
		wd179x_init(WD_TYPE_179X,trs80_fdc_callback);
	
		if (cas_size != 0)
		{
			LOG(("trs80_init_machine: schedule cas_copy_callback (%d)\n", cas_size));
			timer_set(0.5, 0, cas_copy_callback);
		}
	
		if (cmd_size != 0)
		{
			LOG(("trs80_init_machine: schedule cmd_copy_callback (%d)\n", cmd_size));
			timer_set(0.5, 0, cmd_copy_callback);
		}
	} };
	
	void trs80_shutdown_machine(void)
	{
		wd179x_exit();
		tape_put_close();
	}
	
	/*************************************
	 *
	 *				Tape emulation.
	 *
	 *************************************/
	
	static void tape_put_byte(UINT8 value)
	{
		if (tape_count < 8)
		{
			tape_buffer[tape_count++] = value;
			if (tape_count == 8)
			{
				/* BASIC tape ? */
				if (tape_buffer[1] == 0xd3)
				{
					char filename[12+1];
					UINT8 zeroes[256] = {0,};
	
					sprintf(filename, "basic%c.cas", tape_buffer[4]);
					tape_put_file = osd_fopen(Machine.gamedrv.name, filename, OSD_FILETYPE_IMAGE, OSD_FOPEN_RW);
					osd_fwrite(tape_put_file, zeroes, 256);
					osd_fwrite(tape_put_file, tape_buffer, 8);
				}
				else
				/* SYSTEM tape ? */
				if (tape_buffer[1] == 0x55)
				{
					char filename[12+1];
					UINT8 zeroes[256] = {0,};
	
					sprintf(filename, "%-6.6s.cas", tape_buffer+2);
					tape_put_file = osd_fopen(Machine.gamedrv.name, filename, OSD_FILETYPE_IMAGE, OSD_FOPEN_RW);
					osd_fwrite(tape_put_file, zeroes, 256);
					osd_fwrite(tape_put_file, tape_buffer, 8);
				}
			}
		}
		else
		{
			if (tape_put_file != 0)
				osd_fwrite(tape_put_file, &value, 1);
		}
	}
	
	static void tape_put_close(void)
	{
		/* file open ? */
		if (tape_put_file != 0)
		{
			if (put_bit_count != 0)
			{
				UINT8	value;
				while (put_bit_count < 16)
				{
						tape_bits <<= 1;
						put_bit_count++;
				}
				value = 0;
				if ((tape_bits & 0x8000) != 0) value |= 0x80;
				if ((tape_bits & 0x2000) != 0) value |= 0x40;
				if ((tape_bits & 0x0800) != 0) value |= 0x20;
				if ((tape_bits & 0x0200) != 0) value |= 0x10;
				if ((tape_bits & 0x0080) != 0) value |= 0x08;
				if ((tape_bits & 0x0020) != 0) value |= 0x04;
				if ((tape_bits & 0x0008) != 0) value |= 0x02;
				if ((tape_bits & 0x0002) != 0) value |= 0x01;
				tape_put_byte(value);
			}
			osd_fclose(tape_put_file);
		}
		tape_count = 0;
		tape_put_file = 0;
	}
	
	static void tape_get_byte(void)
	{
		int 	count;
		UINT8	value;
		if (tape_get_file != 0)
		{
			count = osd_fread(tape_get_file, &value, 1);
			if (count == 0)
			{
					value = 0;
					osd_fclose(tape_get_file);
					tape_get_file = 0;
			}
			tape_bits |= 0xaaaa;
			if ((value & 0x80) != 0) tape_bits ^= 0x4000;
			if ((value & 0x40) != 0) tape_bits ^= 0x1000;
			if ((value & 0x20) != 0) tape_bits ^= 0x0400;
			if ((value & 0x10) != 0) tape_bits ^= 0x0100;
			if ((value & 0x08) != 0) tape_bits ^= 0x0040;
			if ((value & 0x04) != 0) tape_bits ^= 0x0010;
			if ((value & 0x02) != 0) tape_bits ^= 0x0004;
			if ((value & 0x01) != 0) tape_bits ^= 0x0001;
			get_bit_count = 16;
			tape_count++;
		}
	}
	
	static void tape_get_open(void)
	{
		/* TODO: remove this */
		UBytePtr RAM = memory_region(REGION_CPU1);
	
		if (!tape_get_file)
		{
			char filename[12+1];
	
			sprintf(filename, "%-6.6s.cas", RAM + 0x41e8);
			logerror("filename %s\n", filename);
			tape_get_file = osd_fopen(Machine.gamedrv.name, filename, OSD_FILETYPE_IMAGE, OSD_FOPEN_READ);
			tape_count = 0;
		}
	}
	
	/*************************************
	 *
	 *				Port handlers.
	 *
	 *************************************/
	
	public static ReadHandlerPtr trs80_port_xx_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return 0;
	} };
	
	public static WriteHandlerPtr trs80_port_ff_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		int changes = trs80_port_ff ^ data;
	
		/* tape output changed ? */
		if ((changes & 0x03) != 0)
		{
			/* virtual tape ? */
			if( readinputport(0) & 0x20 )
			{
				int now_cycles = cpu_gettotalcycles();
				int diff = now_cycles - put_cycles;
				UINT8 value;
				/* overrun since last write ? */
				if (diff > 4000)
				{
					/* reset tape output */
					tape_put_close();
					put_bit_count = tape_bits = in_sync = 0;
				}
				else
				/* just wrote the interesting value ? */
				if( (data & 0x03) == 0x01 )
				{
					/* change within time for a 1 bit ? */
					if( diff < 2000 )
					{
						tape_bits = (tape_bits << 1) | 1;
						/* in sync already ? */
						if (in_sync != 0)
						{
							/* count 1 bit */
							put_bit_count += 1;
						}
						else
						{
							/* try to find sync header A5 */
							if( tape_bits == 0xcc33 )
							{
								in_sync = 1;
								put_bit_count = 16;
							}
						}
					}
					else	/* no change indicates a 0 bit */
					{
						/* shift twice */
						tape_bits <<= 2;
						/* in sync already ? */
						if (in_sync != 0)
							put_bit_count += 2;
					}
	
					/* collected 8 sync plus 8 data bits ? */
					if( put_bit_count >= 16 )
					{
						/* extract data bits to value */
						value = 0;
						if ((tape_bits & 0x8000) != 0) value |= 0x80;
						if ((tape_bits & 0x2000) != 0) value |= 0x40;
						if ((tape_bits & 0x0800) != 0) value |= 0x20;
						if ((tape_bits & 0x0200) != 0) value |= 0x10;
						if ((tape_bits & 0x0080) != 0) value |= 0x08;
						if ((tape_bits & 0x0020) != 0) value |= 0x04;
						if ((tape_bits & 0x0008) != 0) value |= 0x02;
						if ((tape_bits & 0x0002) != 0) value |= 0x01;
						put_bit_count -= 16;
						tape_bits = 0;
						tape_put_byte(value);
					}
				}
				/* remember the cycle count of this write */
				put_cycles = now_cycles;
			}
			else
			{
				switch( data & 0x03 )
				{
				case 0: /* 0.46 volts */
					speaker_level_w(0,1);
					break;
				case 1:
				case 3: /* 0.00 volts */
					speaker_level_w(0,0);
					break;
				case 2: /* 0.85 volts */
					speaker_level_w(0,2);
					break;
				}
			}
		}
	
		/* font width change ? (32<.64 characters per line) */
		if ((changes & 0x08) != 0)
			memset(dirtybuffer, 1, 64 * 16);
	
		trs80_port_ff = data;
	} };
	
	public static ReadHandlerPtr trs80_port_ff_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int now_cycles = cpu_gettotalcycles();
		/* virtual tape ? */
		if( readinputport(0) & 0x20 )
		{
			int diff = now_cycles - get_cycles;
			/* overrun since last read ? */
			if (diff >= 4000)
			{
				if (tape_get_file != 0)
				{
					osd_fclose(tape_get_file);
					tape_get_file = 0;
				}
				get_bit_count = tape_bits = tape_time = 0;
			}
			else /* check what he will get for input */
			{
				/* count down cycles */
				tape_time -= diff;
				/* time for the next sync or data bit ? */
				if (tape_time <= 0)
				{
					/* approx. time for a bit */
					tape_time += 1570;
					/* need to read get new data ? */
					if (--get_bit_count <= 0)
					{
						tape_get_open();
						tape_get_byte();
					}
					/* shift next sync or data bit to bit 16 */
					tape_bits <<= 1;
					/* if bit is set, set trs80_port_ff bit 4
					   which is then read as bit 7 */
					if ((tape_bits & 0x10000) != 0)
						trs80_port_ff |= 0x80;
				}
			}
			/* remember the cycle count of this read */
			get_cycles = now_cycles;
		}
		return (trs80_port_ff << 3) & 0xc0;
	} };
	
	/*************************************
	 *
	 *		Interrupt handlers.
	 *
	 *************************************/
	
	public static InterruptPtr trs80_timer_interrupt = new InterruptPtr() { public int handler() 
	{
		if( (irq_status & IRQ_TIMER) == 0 )
		{
			irq_status |= IRQ_TIMER;
			cpu_set_irq_line (0, 0, HOLD_LINE);
			return 0;
		}
		return ignore_interrupt ();
	} };
	
	public static InterruptPtr trs80_fdc_interrupt = new InterruptPtr() { public int handler() 
	{
		if ((irq_status & IRQ_FDC) == 0)
		{
			irq_status |= IRQ_FDC;
			cpu_set_irq_line (0, 0, HOLD_LINE);
			return 0;
		}
		return ignore_interrupt ();
	} };
	
	void trs80_fdc_callback(int event)
	{
		switch (event)
		{
			case WD179X_IRQ_CLR:
				irq_status &= ~IRQ_FDC;
				break;
			case WD179X_IRQ_SET:
				trs80_fdc_interrupt();
				break;
		}
	}
	
	public static InterruptPtr trs80_frame_interrupt = new InterruptPtr() { public int handler() 
	{
		return 0;
	} };
	
	void trs80_nmi_generate (int param)
	{
		cpu_cause_interrupt (0, Z80_NMI_INT);
	}
	
	/*************************************
	 *									 *
	 *		Memory handlers 			 *
	 *									 *
	 *************************************/
	
	READ_HANDLER ( trs80_printer_r )
	{
		/* nothing yet :( */
		return 0;
	}
	
	public static WriteHandlerPtr trs80_printer_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		/* nothing yet :( */
	} };
	
	public static ReadHandlerPtr trs80_irq_status_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int result = irq_status;
		irq_status &= ~(IRQ_TIMER | IRQ_FDC);
		return result;
	} };
	
	public static WriteHandlerPtr trs80_irq_mask_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		irq_mask = data;
	} };
	
	public static WriteHandlerPtr trs80_motor_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		UINT8 drive = 255;
	
		LOG(("trs80 motor_w $%02X\n", data));
	
		switch (data)
		{
		case 1:
			drive = 0;
			head = 0;
			break;
		case 2:
			drive = 1;
			head = 0;
			break;
		case 4:
			drive = 2;
			head = 0;
			break;
		case 8:
			drive = 3;
			head = 0;
			break;
		case 9:
			drive = 0;
			head = 1;
			break;
		case 10:
			drive = 1;
			head = 1;
			break;
		case 12:
			drive = 2;
			head = 1;
			break;
		}
	
		if (drive > 3)
			return;
	
	    wd179x_set_drive(drive);
		wd179x_set_side(head);
	
	} };
	
	/*************************************
	 *		Keyboard					 *
	 *************************************/
	public static ReadHandlerPtr trs80_keyboard_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int result = 0;
	
		if( setup_active() || onscrd_active() )
			return result;
	
		if ((offset & 1) != 0)
			result |= input_port_1_r.handler(0);
		if ((offset & 2) != 0)
			result |= input_port_2_r.handler(0);
		if ((offset & 4) != 0)
			result |= input_port_3_r.handler(0);
		if ((offset & 8) != 0)
			result |= input_port_4_r.handler(0);
		if ((offset & 16) != 0)
			result |= input_port_5_r.handler(0);
		if ((offset & 32) != 0)
			result |= input_port_6_r.handler(0);
		if ((offset & 64) != 0)
			result |= input_port_7_r.handler(0);
		if ((offset & 128) != 0)
			result |= input_port_8_r.handler(0);
	
		return result;
	} };
	
	
}
