/* This is a basic disc image format.
Each driver which uses this must use basicdsk_set_geometry, so that
the data will be accessed correctly */


/* THIS DISK IMAGE CODE USED TO BE PART OF THE WD179X EMULATION, EXTRACTED INTO THIS FILE */
/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package machine;

public class basicdsk
{
	
	#define basicdsk_MAX_DRIVES 4
	#define VERBOSE 1
	static basicdsk     basicdsk_drives[basicdsk_MAX_DRIVES];
	
	static void basicdsk_seek_callback(int,int);
	static int basicdsk_get_sectors_per_track(int,int);
	static void basicdsk_get_id_callback(int, chrn_id *, int, int);
	static void basicdsk_read_sector_data_into_buffer(int drive, int side, int index1, char *ptr, int length);
	static void basicdsk_write_sector_data_from_buffer(int drive, int side, int index1, char *ptr, int length,int ddam);
	
	floppy_interface basicdsk_floppy_interface=
	{
		basicdsk_seek_callback,
		basicdsk_get_sectors_per_track,             /* done */
		basicdsk_get_id_callback,                   /* done */
		basicdsk_read_sector_data_into_buffer,      /* done */
		basicdsk_write_sector_data_from_buffer, /* done */\
		NULL,
		NULL
	};
	
	/* attempt to insert a disk into the drive specified with id */
	int basicdsk_floppy_init(int id)
	{
		const char *name = device_filename(IO_FLOPPY, id);
	
		if (id < basicdsk_MAX_DRIVES)
		{
			basicdsk *w = &basicdsk_drives[id];
	
			/* do we have an image name ? */
			if (!name || !name[0])
			{
				return INIT_PASS;
			}
			w.mode = 1;
			w.image_file = image_fopen(IO_FLOPPY, id, OSD_FILETYPE_IMAGE, OSD_FOPEN_RW);
			if( !w.image_file )
			{
				w.mode = 0;
				w.image_file = image_fopen(IO_FLOPPY, id, OSD_FILETYPE_IMAGE, OSD_FOPEN_READ);
				if( !w.image_file )
				{
					w.mode = 1;
					w.image_file = image_fopen(IO_FLOPPY, id, OSD_FILETYPE_IMAGE, OSD_FOPEN_RW_CREATE);
				}
			}
	
			/* this will be setup in the set_geometry function */
			w.ddam_map = NULL;
	
			/* the following line is unsafe, but floppy_drives_init assumes we start on track 0,
			so we need to reflect this */
			w.track = 0;
	
			floppy_drive_set_disk_image_interface(id,&basicdsk_floppy_interface);
	
			return  INIT_PASS;
		}
	
		return INIT_FAIL;
	}
	
	/* remove a disk from the drive specified by id */
	void basicdsk_floppy_exit(int id)
	{
		basicdsk *pDisk;
	
		/* sanity check */
		if ((id<0) || (id>=basicdsk_MAX_DRIVES))
			return;
	
		pDisk = &basicdsk_drives[id];
	
		/* if file was opened, close it */
		if (pDisk.image_file!=NULL)
		{
			osd_fclose(pDisk.image_file);
			pDisk.image_file = NULL;
		}
	
		/* free ddam map */
		if (pDisk.ddam_map!=NULL)
		{
			free(pDisk.ddam_map);
			pDisk.ddam_map = NULL;
		}
	}
	
	/* set data mark/deleted data mark for the sector specified. If ddam!=0, the sector will
	have a deleted data mark, if ddam==0, the sector will have a data mark */
	void	basicdsk_set_ddam(UINT8 id, UINT8 physical_track, UINT8 physical_side, UINT8 sector_id,UINT8 ddam)
	{
		unsigned long ddam_bit_offset, ddam_bit_index, ddam_byte_offset;
		basicdsk *pDisk;
	
		/* sanity check */
		if (id>=basicdsk_MAX_DRIVES)
			return;
	
		pDisk = &basicdsk_drives[id];
	
		if (!pDisk.ddam_map)
			return;
	
		logerror("basicdsk_set_ddam: #%d T:$%02x H:%d S:$%02x = %d\n",id, physical_track, physical_side, sector_id,ddam);
	
	    /* calculate bit-offset into map */
		ddam_bit_offset = (((physical_track * pDisk.heads) + physical_side)*pDisk.sec_per_track) +
						sector_id - pDisk.first_sector_id;
	
		/* if offset exceeds the number of bits that are stored in the ddam map return 0 */
		if (ddam_bit_offset>=(pDisk.ddam_map_size<<3))
			return;
	
		/* calculate byte offset */
		ddam_byte_offset = ddam_bit_offset>>3;
		/* calc bit index within byte */
		ddam_bit_index = ddam_bit_offset & 0x07;
	
		/* clear bit */
		pDisk.ddam_map[ddam_byte_offset] &= ~(1<<ddam_bit_index);
	
		/* deleted dam? */
		if (ddam != 0)
		{
			/* set deleted dam */
			pDisk.ddam_map[ddam_byte_offset] |= (1<<ddam_bit_index);
		}
	}
	
	/* get dam state for specified sector */
	static int basicdsk_get_ddam(UINT8 id, UINT8 physical_track, UINT8 physical_side, UINT8 sector_id)
	{
		unsigned long ddam_bit_offset, ddam_bit_index, ddam_byte_offset;
		basicdsk *pDisk;
	
		/* sanity check */
		if (id>=basicdsk_MAX_DRIVES)
			return 0;
	
		pDisk = &basicdsk_drives[id];
	
		if (!pDisk.ddam_map)
			return 0;
	
		/* calculate bit-offset into map */
		ddam_bit_offset = (((physical_track * pDisk.heads) + physical_side)*pDisk.sec_per_track) +
						sector_id - pDisk.first_sector_id;
	
		/* if offset exceeds the number of bits that are stored in the ddam map return 0 */
		if (ddam_bit_offset>=(pDisk.ddam_map_size<<3))
			return 0;
	
		/* calculate byte offset */
		ddam_byte_offset = ddam_bit_offset>>3;
		/* calc bit index within byte */
		ddam_bit_index = ddam_bit_offset & 0x07;
	
		/* clear bit */
		return ((pDisk.ddam_map[ddam_byte_offset] & (1<<ddam_bit_index))!=0);
	}
	
	
	/* dir_sector is a relative offset from the start of the disc,
	dir_length is a relative offset from the start of the disc */
	void basicdsk_set_geometry(UINT8 drive, UINT16 tracks, UINT8 heads, UINT8 sec_per_track, UINT16 sector_length, UINT8 first_sector_id, UINT16 offset_track_zero)
	{
		basicdsk *pDisk;
		unsigned long N;
		unsigned long ShiftCount;
	
	
		if (drive >= basicdsk_MAX_DRIVES)
		{
			logerror("basicdsk drive #%d not supported!\n", drive);
			return;
		}
	
		pDisk = &basicdsk_drives[drive];
	
	
	#if VERBOSE
		logerror("basicdsk geometry for drive #%d is %d tracks, %d heads, %d sec/track, %d bytes per sector, first sector id: %d, file offset to track 0: %d\n",
			drive, tracks, heads, sec_per_track, sector_length, first_sector_id, offset_track_zero);
	#endif
	
		pDisk.tracks = tracks;
		pDisk.heads = heads;
		pDisk.first_sector_id = first_sector_id;
		pDisk.sec_per_track = sec_per_track;
		pDisk.sector_length = sector_length;
		pDisk.offset = offset_track_zero;
		
		floppy_drive_set_geometry_absolute( drive, tracks, heads );
		
		pDisk.image_size = pDisk.tracks * pDisk.heads * pDisk.sec_per_track * pDisk.sector_length;
	
		/* if a ddam map was already set up clear it */
		if (pDisk.ddam_map!=NULL)
		{
			free(pDisk.ddam_map);
			pDisk.ddam_map = NULL;
		}
		/* setup a new ddam map */
		pDisk.ddam_map_size = ((pDisk.tracks * pDisk.heads * pDisk.sec_per_track)+7)>>3;
		pDisk.ddam_map = (UINT8 *)malloc(pDisk.ddam_map_size);
	
		if (pDisk.ddam_map!=NULL)
		{
			memset(pDisk.ddam_map, 0, pDisk.ddam_map_size);
		}
	
	
		/* from sector length calculate N value for sector id's */
		/* N = 0 for 128, N = 1 for 256, N = 2 for 512 ... */
		N = (pDisk.sector_length);
		ShiftCount = 0;
	
		if (N!=0)
		{
			while ((N & 0x080000000)==0)
			{
				N = N<<1;
				ShiftCount++;
			}
	
			/* get left-shift required to shift 1 to this
			power of 2 */
	
			pDisk.N = (31 - ShiftCount)-7;
		}
		else
		{
			pDisk.N = 0;
		}
	}
	
	
	/* seek to track/head/sector relative position in image file */
	static int basicdsk_seek(basicdsk * w, UINT8 t, UINT8 h, UINT8 s)
	{
	unsigned long offset;
		/* allow two additional tracks */
	    if (t >= w.tracks + 2)
		{
			logerror("basicdsk track %d >= %d\n", t, w.tracks + 2);
			return 0;
		}
	
	    if (h >= w.heads)
	    {
			logerror("basicdsk head %d >= %d\n", h, w.heads);
			return 0;
		}
	
	    if (s >= (w.first_sector_id + w.sec_per_track))
		{
			logerror("basicdsk sector %d\n", w.sec_per_track+w.first_sector_id);
			return 0;
		}
	
		offset = 0;
		offset += t;
		offset *= w.heads;
		offset += h;
		offset *= w.sec_per_track;
		offset += (s-w.first_sector_id);
		offset *= w.sector_length;
		offset += w.offset;
	
	
	#if VERBOSE
	    logerror("basicdsk seek track:%d head:%d sector:%d. offset #0x%08lX\n",
	             t, h, s, offset);
	#endif
	
		if (offset > w.image_size)
		{
			logerror("basicdsk seek offset %ld >= %ld\n", offset, w.image_size);
			return 0;
		}
	
		if (osd_fseek(w.image_file, offset, SEEK_SET) < 0)
		{
			logerror("basicdsk seek failed\n");
			return 0;
		}
	
		return 1;
	}
	
	
	
	
	#if 0
				w.status = seek(w, w.track, w.head, w.sector);
				if (w.status == 0)
					read_sector(w);
	
	
		/* if a track was just formatted */
		if (w.dam_cnt)
		{
			int i;
			for (i = 0; i < w.dam_cnt; i++)
			{
				if (w.track == w.dam_list[i][0] &&
					w.head == w.dam_list[i][1] &&
					w.sector == w.dam_list[i][2])
				{
	#if VERBOSE
	                                logerror("basicdsk reading formatted sector %d, track %d, head %d\n", w.sector, w.track, w.head);
	#endif
					w.data_offset = w.dam_data[i];
					return;
				}
			}
			/* sector not found, now the track buffer is invalid */
			w.dam_cnt = 0;
		}
	
	    /* if this is the real thing */
	    if (w.image_file == REAL_FDD)
	    {
		int tries = 3;
			do {
				w.status = osd_fdc_get_sector(w.track, w.head, w.head, w.sector, w.buffer);
				tries--;
			} while (tries && (w.status & (STA_2_REC_N_FND | STA_2_CRC_ERR | STA_2_LOST_DAT)));
			/* no error bits set ? */
			if ((w.status & (STA_2_REC_N_FND | STA_2_CRC_ERR | STA_2_LOST_DAT)) == 0)
			{
				/* start transferring data to the emulation now */
				w.status_drq = STA_2_DRQ;
				if (w.callback)
	                                (*w.callback) (basicdsk_DRQ_SET);
				w.status |= STA_2_DRQ | STA_2_BUSY;
	        }
	        return;
	    }
		else
		if (osd_fread(w.image_file, w.buffer, w.sector_length) != w.sector_length)
		{
			w.status = STA_2_LOST_DAT;
			return;
		}
	
	
	#endif
	
	
	
	void    basicdsk_step_callback(basicdsk *w, int drive, int direction)
	{
				w.track += direction;
	}
	
	#if 0
	/* write a sector */
	static void basicdsk_write_sector(basicdsk *w)
	{
	
		if (w.image_file == REAL_FDD)
		{
	                osd_fdc_put_sector(w.track, w.head, w.head, w.sector, w.buffer, w.write_cmd & FDC_DELETED_AM);
			return;
		}
	
	        seek(w, w.track, w.head, w.sector);
	        osd_fwrite(w.image_file, w.buffer, w.data_offset)
	}
	
	
	/* write an entire track by extracting the sectors */
	static void basicdsk_write_track(basicdsk *w)
	{
		if (floppy_drive_get_flag_state(drv,FLOPPY_DRIVE_DISK_WRITE_PROTECTED))
	    {
			w.status |= STA_1_WRITE_PRO;
			return;
		}
	#endif
	
	#if 0
	UINT8 *f;
	int cnt;
		w.dam_cnt = 0;
	    if (w.image_file != REAL_FDD && w.mode == 0)
	    {
	#if VERBOSE
	                logerror("basicdsk write_track write protected image\n");
	#endif
	        w.status = STA_2_WRITE_PRO;
	        return;
	    }
	
		memset(w.dam_list, 0xff, sizeof(w.dam_list));
		memset(w.dam_data, 0x00, sizeof(w.dam_data));
	
		f = w.buffer;
	#if VERBOSE
	        logerror("basicdsk write_track %s_LOW\n", (w.density) ? "MFM" : "FM" );
	#endif
	    cnt = (w.density) ? TRKSIZE_DD : TRKSIZE_SD;
	
		do
		{
			while ((--cnt > 0) && (*f != 0xfe))	/* start of DAM ?? */
				f++;
	
			if (cnt > 4)
			{
			int seclen;
				cnt -= 5;
				f++;			   /* skip FE */
				w.dam_list[w.dam_cnt][0] = *f++;	  /* copy track number */
				w.dam_list[w.dam_cnt][1] = *f++;	  /* copy head number */
				w.dam_list[w.dam_cnt][2] = *f++;	  /* copy sector number */
				w.dam_list[w.dam_cnt][3] = *f++;	  /* copy sector length */
				/* sector length in bytes */
				seclen = 128 << w.dam_list[w.dam_cnt][3];
	#if VERBOSE
	                        logerror("basicdsk write_track FE @%5d T:%02X H:%02X S:%02X L:%02X\n",
						(int)(f - w.buffer),
						w.dam_list[w.dam_cnt][0],w.dam_list[w.dam_cnt][1],
						w.dam_list[w.dam_cnt][2],w.dam_list[w.dam_cnt][3]);
	#endif
				/* search start of DATA */
				while ((--cnt > 0) && (*f != 0xf9) && (*f != 0xfa) && (*f != 0xfb))
					f++;
				if (cnt > seclen)
				{
					cnt--;
					/* skip data address mark */
	                f++;
	                /* set pointer to DATA to later write the sectors contents */
					w.dam_data[w.dam_cnt] = (int)(f - w.buffer);
					w.dam_cnt++;
	#if VERBOSE
	                                logerror("basicdsk write_track %02X @%5d data: %02X %02X %02X %02X ... %02X %02X %02X %02X\n",
							f[-1],
							(int)(f - w.buffer),
							f[0], f[1], f[2], f[3],
							f[seclen-4], f[seclen-3], f[seclen-2], f[seclen-1]);
	#endif
					f += seclen;
					cnt -= seclen;
				}
	        }
		} while (cnt > 0);
	
		if (w.image_file == REAL_FDD)
		{
			w.status = osd_fdc_format(w.track, w.head, w.dam_cnt, w.dam_list[0]);
	
	        if ((w.status & 0xfc) == 0)
			{
				/* now put all sectors contained in the format buffer */
				for (cnt = 0; cnt < w.dam_cnt; cnt++)
				{
					w.status = osd_fdc_put_sector(w.track, w.head, cnt, w.buffer[dam_data[cnt]], 0);
					/* bail out if an error occured */
					if (w.status & 0xfc)
						break;
				}
	        }
	    }
		else
		{
			/* now put all sectors contained in the format buffer */
			for (cnt = 0; cnt < w.dam_cnt; cnt++)
			{
				w.status = seek(w, w.track, w.head, w.dam_list[cnt][2]);
				if (w.status == 0)
				{
					if (osd_fwrite(w.image_file, &w.buffer[w.dam_data[cnt]], w.sector_length) != w.sector_length)
					{
						w.status = STA_2_LOST_DAT;
						return;
					}
				}
			}
		}
	}
	#endif
	#if 0
				if (w.image_file != REAL_FDD)
				{
					/* read normal or deleted data address mark ? */
					w.status |= deleted_dam(w);
				}
	#endif
	
	
	#if 0
	
		if ((data | 1) == 0xff)	   /* change single/double density ? */
		{
			/* only supports FM/LO and MFM/LO */
			w.density = (data & 1) ? DEN_MFM_LO : DEN_FM_LO;
	#if 0
			if (w.image_file == REAL_FDD)
				osd_fdc_density(w.unit, w.density, w.tracks, w.sec_per_track, w.sec_per_track, 1);
	#endif
			return;
		}
	#endif
	
	
	void    basicdsk_get_id_callback(int drive, chrn_id *id, int id_index, int side)
	{
		basicdsk *w = &basicdsk_drives[drive];
	
		/* construct a id value */
		id.C = w.track;
		id.H = side;
		id.R = w.first_sector_id + id_index;
	    id.N = w.N;
		id.data_id = w.first_sector_id + id_index;
		id.flags = 0;
	
		/* get dam */
		if (basicdsk_get_ddam(drive, w.track, side, id.R))
		{
			id.flags |= ID_FLAG_DELETED_DATA;
		}
	
	}
	
	int  basicdsk_get_sectors_per_track(int drive, int side)
	{
		basicdsk *w = &basicdsk_drives[drive];
	
		/* attempting to access an invalid side or track? */
		if ((side>=w.heads) || (w.track>=w.tracks))
		{
			/* no sectors */
			return 0;
		}
		/* return number of sectors per track */
		return w.sec_per_track;
	}
	
	void    basicdsk_seek_callback(int drive, int physical_track)
	{
		basicdsk *w = &basicdsk_drives[drive];
	
		w.track = physical_track;
	}
	
	void basicdsk_write_sector_data_from_buffer(int drive, int side, int index1, char *ptr, int length, int ddam)
	{
		basicdsk *w = &basicdsk_drives[drive];
	
		if (basicdsk_seek(w, w.track, side, index1)&&w.mode)
		{
			osd_fwrite(w.image_file, ptr, length);
		}
	
		basicdsk_set_ddam(drive, w.track, side, index1, ddam);
	}
	
	void basicdsk_read_sector_data_into_buffer(int drive, int side, int index1, char *ptr, int length)
	{
		basicdsk *w = &basicdsk_drives[drive];
	
		if (basicdsk_seek(w, w.track, side, index1))
		{
			osd_fread(w.image_file, ptr, length);
		}
	}
	
	
}
