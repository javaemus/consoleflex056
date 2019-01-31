/***************************************************************************

  dsk.c

 CPCEMU standard and extended disk image support.
 Used on Amstrad CPC and Spectrum +3 drivers.

 KT - 27/2/00 - Moved Disk Image handling code into this file
							- Fixed a few bugs
							- Cleaned code up a bit
***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess.machine;

import arcadeflex.libc.ptr.UBytePtr;
import static common.libc.cstring.memcpy;
import static mame.osdependH.OSD_FILETYPE_IMAGE_R;
import static mess.deviceH.IO_FLOPPY;
import mess.includes.flopdrvH;
import static mess.includes.flopdrvH.ID_FLAG_DELETED_DATA;
import mess.includes.flopdrvH.chrn_id;
import mess.includes.flopdrvH.floppy_interface;
import static mess.machine.flopdrv.floppy_drive_set_disk_image_interface;
import static mess.mess.image_fopen;
import static mess.messH.IMAGE_VERIFY_FAIL;
import static mess.messH.IMAGE_VERIFY_PASS;
import static mess.messH.OSD_FOPEN_READ;
import static mess.messH.OSD_FOPEN_RW;
import static mess.osdepend.fileio.osd_fclose;
import static mess.osdepend.fileio.osd_fread;
import static mess.osdepend.fileio.osd_fsize;
import static old.arcadeflex.fileio.osd_fwrite;
import static common.libc.cstring.*;
import consoleflex.funcPtr;
import consoleflex.funcPtr.io_exitPtr;
import consoleflex.funcPtr.io_initPtr;



public class dsk
{
	/* disk image and extended disk image support code */
	/* supports up to 84 tracks and 2 sides */
	
	public static final int dsk_MAX_TRACKS = 84;
	public static final int dsk_MAX_SIDES = 2;
	public static final int dsk_NUM_DRIVES = 4;
	public static final int dsk_SECTORS_PER_TRACK = 20;
	
	public static class dsk_drive
	{
		public char[] data; /* the whole image data */
		public int[] track_offsets = new int[dsk_MAX_TRACKS*dsk_MAX_SIDES]; /* offset within data for each track */
		public int[] sector_offsets = new int[dsk_SECTORS_PER_TRACK]; /* offset within current track for sector data */
		public int current_track;		/* current track */
		public int disk_image_type;  /* image type: standard or extended */
	};
	
	public static floppy_interface dsk_floppy_interface= new floppy_interface() {
            public void seek_callback(int drive, int physical_track) {
                dsk_seek_callback(drive, physical_track);
            }

            public int get_sectors_per_track(int drive, int physical_side) {
                return dsk_get_sectors_per_track(drive, physical_side);
            }

            public void get_id_callback(int drive, chrn_id chrn, int id_index, int physical_side) {
                dsk_get_id_callback(drive, chrn, id_index, physical_side);
            }

            public void read_sector_data_into_buffer(int drive, int side, int data_id, char[] data, int length) {
                dsk_read_sector_data_into_buffer(drive, side, data_id, data, length);
            }

            public void write_sector_data_from_buffer(int drive, int side, int data_id, char[] data, int length, int ddam) {
                dsk_write_sector_data_from_buffer(drive, side, data_id, data, length, ddam);
            }

            public void read_track_data_info_buffer(int drive, int side, char[] ptr, int length) {
                // nothing to do
            }

            public void format_sector(int drive, int side, int sector_index, int c, int h, int r, int n, int filler) {
                // nothing to do
            }
        };
	
	
	/*TODO*///static void dsk_disk_image_init(dsk_drive *);
	
	public static dsk_drive[] drives = new dsk_drive[dsk_NUM_DRIVES]; /* the drives */
        
        static {
            for ( int i=0 ; i<dsk_NUM_DRIVES ; i++ ){
               drives[i] = new dsk_drive();
            }
        }
	
	/* load image */
	public static int dsk_load(int type, int id)
	{
		System.out.println("dsk_load!!");
                Object file;
	
		file = image_fopen(type, id, OSD_FILETYPE_IMAGE_R, OSD_FOPEN_READ);
                System.out.println("File="+file);
	
		if (file != null)
		{
			int datasize;
			//UBytePtr data;
	
			/* get file size */
			datasize = osd_fsize(file);
	
			if (datasize!=0)
			{
				System.out.println("datasize!=0");
                                /* malloc memory for this data */
				//data = new UBytePtr(datasize);
                                drives[id].data = new char[datasize];
	
				if (drives[id].data!=null)
				{
					/* read whole file */
					osd_fread(file, drives[id].data, datasize);
                                        System.out.println("Asignamos! "+drives[id].data);
					//ptr = new UBytePtr(data);
	
					/* close file */
					osd_fclose(file);
                                        System.out.println("Asignamos2! "+drives[id].data);
					/* ok! */
					return 1;
				}
				osd_fclose(file);
	
			}
		}
	
		return 0;
	}
	
	public static int dsk_floppy_verify(char[] diskimage_data)
	{
		if ( (memcmp(diskimage_data, "MV - CPC".toCharArray(), 8)==0) || 	/* standard disk image? */
			 (memcmp(diskimage_data, "EXTENDED".toCharArray(), 8)==0))	/* extended disk image? */
		{
			return IMAGE_VERIFY_PASS;
		}
		return IMAGE_VERIFY_FAIL;
	}
	
	
	/* load floppy */
	public static io_initPtr dsk_floppy_load = new io_initPtr() {
            public int handler(int id) {
                //System.out.println("dsk_floppy_load!");
		//dsk_drive thedrive = drives[id];
                
                /*if (thedrive.data == null)
                    thedrive.data = new UBytePtr();*/
                
		/* load disk image */
                dsk_load(IO_FLOPPY,id);
                //System.out.println("dataX: "+drives[id].data);
                
		if ((dsk_load(IO_FLOPPY,id)) != 0)
		{
                    //System.out.println("data: "+drives[id].data);
                    
			if ((drives[id].data) != null)
			{
                            //thedrive.data = mem;
                            dsk_disk_image_init(id); /* initialise dsk */
                            floppy_drive_set_disk_image_interface(id,dsk_floppy_interface);
                            if(dsk_floppy_verify(drives[id].data) == IMAGE_VERIFY_PASS){
                                //System.out.println("PASA");
                                //return INIT_PASS;
                                return 0;
                            }else{
                                //System.out.println("NO PASA");
                                //return INIT_PASS;
                                return 0;
                            }
			}
		}
	
		//return INIT_PASS;
                return 0;
	}};
	
	public static int dsk_save(int type, int id, char[] ptr)
	{
		Object file;
	
		file = image_fopen(type, id, OSD_FILETYPE_IMAGE_R, OSD_FOPEN_RW);
	
		if (file != null)
		{
			int datasize;
			char[] data;
	
			/* get file size */
			datasize = osd_fsize(file);
	
			if (datasize!=0)
			{
				data = ptr;
				if (data!=null)
				{
					osd_fwrite(file, data, datasize);
	
					/* close file */
					osd_fclose(file);
	
					/* ok! */
					return 1;
				}
				osd_fclose(file);
	
			}
		};
	
		return 0;
	}
	
	public static io_exitPtr dsk_floppy_exit = new io_exitPtr() {
            	
            public int handler(int id) {
                dsk_drive thedrive = drives[id];
	
		if (thedrive.data!=null)
		{
			dsk_save(IO_FLOPPY,id,thedrive.data);
			//free(thedrive.data);
                        thedrive.data = null;
		}
		thedrive.data = null;
                
                return 0;
            }
	};
	
	
	public static void dsk_dsk_init_track_offsets(int drive)
	{
		int track_offset;
		int i;
		int track_size;
		int tracks, sides;
		int skip, length,offs;
		char[] file_loaded = drives[drive].data;
	
	
		/* get size of each track from main header. Size of each
		track includes a 0x0100 byte header, and the actual sector data for
		all sectors on the track */
		track_size = file_loaded[0x032] | (file_loaded[0x033]<<8);
	
		/* main header is 0x0100 in size */
		track_offset = 0x0100;
	
		sides = file_loaded[0x031];
		tracks = file_loaded[0x030];
	
	
		/* single sided? */
		if (sides==1)
		{
			skip = 2;
			length = tracks;
		}
		else
		{
			skip = 1;
			length = tracks*sides;
		}
	
		offs = 0;
		for (i=0; i<length; i++)
		{
			drives[drive].track_offsets[offs] = track_offset;
                        System.out.println("TRACK OFFSET["+i+"]="+track_offset);
			track_offset+=track_size;
			offs+=skip;
		}
	
	}
	
	public static void dsk_dsk_init_sector_offsets(int drive,int track,int side)
	{
            System.out.println("dsk_dsk_init_sector_offsets");
		int track_offset;
	
		side = side & 0x01;
	
		/* get offset to track header in image */
		track_offset = drives[drive].track_offsets[(track<<1) + side];
	
		if (track_offset!=0)
		{
			int spt;
			int sector_offset;
			int sector_size;
			int i;
	
			UBytePtr track_header;
	
			track_header= new UBytePtr(drives[drive].data, track_offset);
	
			/* sectors per track as specified in nec765 format command */
			/* sectors on this track */
			spt = track_header.read(0x015);
	
			sector_size = (1<<(track_header.read(0x014)+7));
	
			/* track header is 0x0100 bytes in size */
			sector_offset = 0x0100;
	
			for (i=0; i<spt; i++)
			{
                            //System.out.println("SECTOR OFFSET["+i+"]="+sector_offset);
				drives[drive].sector_offsets[i] = sector_offset;
				sector_offset+=sector_size;
			}
		}
	}
	
	public static void dsk_extended_dsk_init_track_offsets(int drive)
	{
            System.out.println("dsk_extended_dsk_init_track_offsets");
		int track_offset;
		int i;
		int track_size;
		int tracks, sides;
		int offs, skip, length;
		char[] file_loaded = drives[drive].data;
	
		sides = file_loaded[0x031];
		tracks = file_loaded[0x030];
	
		if (sides==1)
		{
			skip = 2;
			length = tracks;
		}
		else
		{
			skip = 1;
			length = tracks*sides;
		}
	
		/* main header is 0x0100 in size */
		track_offset = 0x0100;
		offs = 0;
		for (i=0; i<length; i++)
		{
			int track_size_high_byte;
	
			/* track size is specified as a byte, and is multiplied
			by 256 to get size in bytes. If 0, track doesn't exist and
			is unformatted, otherwise it exists. Track size includes 0x0100
			header */
			track_size_high_byte = file_loaded[0x034 + i];
	
			if (track_size_high_byte != 0)
			{
				/* formatted track */
				track_size = track_size_high_byte<<8;
	
				drives[drive].track_offsets[offs] = track_offset;
                                System.out.println("TRACK OFFSET["+i+"]="+track_offset);
				track_offset+=track_size;
			}
	
			offs+=skip;
		}
	}
	
	static int total_offset = 0;
        
	public static void dsk_extended_dsk_init_sector_offsets(int drive,int track,int side)
	{
            System.out.println("EXTENDED DSK!!!!");
		int track_offset;
	
		side = side & 0x01;
	
		/* get offset to track header in image */
		track_offset = drives[drive].track_offsets[(track<<1) + side];
                System.out.println("OFFSET_EXT: "+track_offset);
		if (track_offset!=0)
		{
			int spt;
			int sector_offset;
			int sector_size;
			int i;
			UBytePtr id_info;
			UBytePtr track_header;
	
			track_header= new UBytePtr(drives[drive].data, track_offset);
                        
                        total_offset += track_offset;
	
			/* sectors per track as specified in nec765 format command */
			/* sectors on this track */
			spt = track_header.read(0x015);
	
			id_info = new UBytePtr(track_header, 0x018);
                        
                        total_offset += 0x18;
	
			/* track header is 0x0100 bytes in size */
			sector_offset = 0x0100;
                        
                        total_offset += 0x100;
	
			for (i=0; i<spt; i++)
			{
	                        sector_size = id_info.read((i<<3) + 6) + (id_info.read((i<<3) + 7)<<8);
	
				drives[drive].sector_offsets[i] = sector_offset;
                                System.out.println("SECTOR OFFSET["+i+"]="+sector_offset);
				sector_offset+=sector_size;
			}
		}
	}
	
	
	
	public static void dsk_disk_image_init(int drive)
	{
            System.out.println("dsk_disk_image_init!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		/*-----------------27/02/00 11:26-------------------
		 clear offsets
		--------------------------------------------------*/
		//memset(thedrive.track_offsets[0], 0, dsk_MAX_TRACKS*dsk_MAX_SIDES);
                drives[drive].track_offsets = new int[dsk_MAX_TRACKS*dsk_MAX_SIDES];
		//memset(thedrive.sector_offsets[0], 0, 20);
                drives[drive].sector_offsets = new int[20];
	
		if (memcmp(drives[drive].data,"MV - CPC".toCharArray(),8)==0)
		{
                    System.out.println("dsk_disk_image_init----MV - CPC");
			drives[drive].disk_image_type = 0;
	
			/* standard disk image */
			dsk_dsk_init_track_offsets(drive);
	
		}
		else
		if (memcmp(drives[drive].data,"EXTENDED".toCharArray(),8)==0)
		{
                    System.out.println("dsk_disk_image_init----EXTENDED");
			drives[drive].disk_image_type = 1;
	
			/* extended disk image */
			dsk_extended_dsk_init_track_offsets(drive);
		}
	}
	
	
	public static void dsk_seek_callback(int drive, int physical_track)
	{
		drive = drive & 0x03;
		drives[drive].current_track = physical_track;
	}
	
	public static int get_track_offset(int drive, int side)
	{
            
		dsk_drive thedrive;
	
		drive = drive & 0x03;
		side = side & 0x01;
	
		thedrive = drives[drive];
                
                //System.out.println("--------get_track_offset - current track: "+thedrive.current_track);
	
		return thedrive.track_offsets[(thedrive.current_track<<1) + side];
	}
	
	public static char[] get_floppy_data(int drive)
	{
		drive = drive & 0x03;
		return drives[drive].data;
	}
	
	public static void dsk_get_id_callback(int drive, chrn_id id, int id_index, int side)
	{
            //System.out.println("dsk_get_id_callback");
		int id_offset;
		int track_offset;
		UBytePtr track_header;
		char[] data;
	
		drive = drive & 0x03;
		side = side & 0x01;
	
		/* get offset to track header in image */
		track_offset = get_track_offset(drive, side);
	
		/* track exists? */
		if (track_offset==0)
			return;
	
		/* yes */
		data = get_floppy_data(drive);
	
		if (data==null)
			return;
	
		track_header = new UBytePtr(data, track_offset);
	
		id_offset = 0x018 + (id_index<<3);
	
		id.C = track_header.read(id_offset + 0);
		id.H = track_header.read(id_offset + 1);
		id.R = track_header.read(id_offset + 2);
		id.N = track_header.read(id_offset + 3);
		id.flags = 0;
		id.data_id = id_index;
                
                System.out.println("dsk_get_id_callback C: "+id.C);
                System.out.println("dsk_get_id_callback H: "+id.H);
                System.out.println("dsk_get_id_callback R: "+id.R);
	
		if ((track_header.read(id_offset + 5) & 0x040) != 0)
		{
			id.flags |= ID_FLAG_DELETED_DATA;
		}
	
	
	
	
	//	id.ST0 = track_header[id_offset + 4];
	//	id.ST1 = track_header[id_offset + 5];
	
	}
	
	
	public static void dsk_set_ddam(int drive, int id_index, int side, int ddam)
	{
		int id_offset;
		int track_offset;
		UBytePtr track_header;
		char[] data;
	
		drive = drive & 0x03;
		side = side & 0x01;
	
		/* get offset to track header in image */
		track_offset = get_track_offset(drive, side);
	
		/* track exists? */
		if (track_offset==0)
			return;
	
		/* yes */
		data = get_floppy_data(drive);
	
		if (data==null)
			return;
	
		track_header = new UBytePtr(data, track_offset);
	
		id_offset = 0x018 + (id_index<<3);
	
		track_header.write( (id_offset + 5), track_header.read(id_offset + 5) & ~0x040);
	
		if (ddam != 0)
		{
			track_header.write( (id_offset + 5), track_header.read(id_offset + 5) | 0x040);
		}
	}
	
	
	public static UBytePtr dsk_get_sector_ptr_callback(int drive, int sector_index, int side)
	{
            System.out.println("dsk_get_sector_ptr_callback");
		int track_offset;
		int sector_offset;
		int track;
		dsk_drive thedrive;
		char[] data;
                
		drive = drive & 0x03;
		side = side & 0x01;
	
		thedrive = drives[drive];
	
		track = thedrive.current_track;
	
		/* offset to track header in image */
		track_offset = get_track_offset(drive, side);
                System.out.println("Track Offset: "+track_offset);
	
		/* track exists? */
		if (track_offset==0)
			return null;
                
                System.out.println("DISK IMAGE TYPE: "+thedrive.disk_image_type);
	
	
		/* setup sector offsets */
		switch (thedrive.disk_image_type)
		{
		case 0:
			dsk_dsk_init_sector_offsets(drive,track, side);
			break;
	
	
		case 1:
			dsk_extended_dsk_init_sector_offsets(drive, track, side);
			break;
	
		default:
			break;
		}
	
		sector_offset = thedrive.sector_offsets[sector_index];
                
                System.out.println("--->TRACK OFFSET: "+track_offset);
                System.out.println("--->SECTOR OFFSET: "+sector_offset);
	
		data = get_floppy_data(drive);
	
		if (data==null)
			return null;
                
                total_offset += track_offset + sector_offset;
	
		return (new UBytePtr(data, track_offset + sector_offset));
	}
	
	public static void dsk_write_sector_data_from_buffer(int drive, int side, int index1, char[] ptr, int length, int ddam)
	{
            System.out.println("dsk_write_sector_data_from_buffer!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		UBytePtr pSectorData;
	
		pSectorData = dsk_get_sector_ptr_callback(drive, index1, side);
	
		if (pSectorData!=null)
		{
			memcpy(pSectorData, ptr, length);
		}
	
		/* set ddam */
		dsk_set_ddam(drive, index1, side,ddam);
	}
	
	public static void dsk_read_sector_data_into_buffer(int drive, int side, int index1, char[] ptr, int length)
	{
            System.out.println("dsk_read_sector_data_into_buffer "+index1);
		UBytePtr pSectorData;
                
                total_offset = 0;
	
		pSectorData = dsk_get_sector_ptr_callback(drive, index1, side);
                //pSectorData.offset = 512-0x18;
                
                //System.out.println("TOTAL OFFSET="+pSectorData.offset);
                
                
                //total_offset += 512;
                
                //pSectorData.offset = drives[drive].track_offsets[1];
                //pSectorData.offset = 5376-256;
                
	
		if (pSectorData!=null)
		{
                    System.out.println("dsk_read_sector_data_into_buffer NOT NULL");
                    
                    System.out.println("FDC.C="+nec765.fdc.c);
                    System.out.println("FDC.H="+nec765.fdc.h);
                    System.out.println("FDC.R="+nec765.fdc.r);
                    
                    int id_index = flopdrv.drives[drive].id_index;
                    System.out.println("INDEX="+id_index);
                    
                    /*System.out.println("INDEX.C="+flopdrv.drives[drive].ids[id_index].C);
                    System.out.println("INDEX.H="+flopdrv.drives[drive].ids[id_index].H);
                    System.out.println("INDEX.R="+flopdrv.drives[drive].ids[id_index].R);*/
                    
                    System.out.println("Current Track="+drives[drive].current_track);
                    System.out.println("Current Sector="+index1);
                    //pSectorData.offset = 5120 + 256 + 100 -8 -24;
                    pSectorData.offset = 256 + 768;
                        System.out.println("Offset Antes: "+pSectorData.offset);
			memcpy(ptr, pSectorData, length);
                        System.out.println("MyPtr: "+ptr);
                        System.out.println("Length: "+length);
                        System.out.println("Offset: "+pSectorData.offset);
                        //drives[drive].data = ptr;
		}
	}
	
	public static int dsk_get_sectors_per_track(int drive, int side)
	{
            //System.out.println("dsk_get_sectors_per_track");
		int track_offset;
		UBytePtr track_header;
		char[] data;
	
		drive = drive & 0x03;
		side = side & 0x01;
	
		/* get offset to track header in image */
		track_offset = get_track_offset(drive, side);
	
		/* track exists? */
		if (track_offset==0)
			return 0;
	
		data = get_floppy_data(drive);
	
		if (data==null)
			return 0;
	
		/* yes, get sectors per track */
		track_header = new UBytePtr(data, track_offset);
                int _sal = (track_header.read(0x015));
                //System.out.println("SECTORS PER TRACK: "+_sal+" Track offset:"+track_offset);
                
		return _sal;
	}
	
}
