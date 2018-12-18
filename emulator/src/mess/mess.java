/*
This file is a set of function calls and defs required for MESS.
*/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess;

import static mess.deviceH.*;
import static mess.device.*;

import static common.libc.cstdio.*;

public class mess
{
	
	/*TODO*///extern struct GameOptions options;
	/*TODO*///extern const struct Devices devices[];
	
	/* CRC database file for this driver, supplied by the OS specific code */
	/*TODO*///extern const char *crcfile;
	/*TODO*///extern const char *pcrcfile;
	
	
	/* Globals */
	int mess_keep_going;
	String renamed_image;
	
	public static class image_info {
		String name;
		int crc;
		int length;
		String longname;
		String manufacturer;
		String year;
		String playable;
		String extrainfo;
	};
	
	public static int MAX_INSTANCES = 5;
	static image_info[][] images = new image_info[IO_COUNT][MAX_INSTANCES];
        static int[] count = new int[IO_COUNT];
	
	/*TODO*///static char* dupe(const char *src)
	/*TODO*///{
	/*TODO*///	if (src != 0)
	/*TODO*///	{
	/*TODO*///		char *dst = malloc(strlen(src) + 1);
	/*TODO*///		if (dst != 0)
	/*TODO*///			strcpy(dst,src);
	/*TODO*///		return dst;
	/*TODO*///	}
	/*TODO*///	return NULL;
	/*TODO*///}
	
	
	/*TODO*///static char* stripspace(const char *src)
	/*TODO*///{
	/*TODO*///	static char buff[512];
	/*TODO*///	if (src != 0)
	/*TODO*///	{
	/*TODO*///		char *dst;
	/*TODO*///		while( *src && isspace(*src) )
	/*TODO*///			src++;
	/*TODO*///		strcpy(buff, src);
	/*TODO*///		dst = buff + strlen(buff);
	/*TODO*///		while( dst >= buff && isspace(*--dst) )
	/*TODO*///			*dst = '\0';
	/*TODO*///		return buff;
	/*TODO*///	}
	/*TODO*///	return NULL;
	/*TODO*///}
	
	/*TODO*///int DECL_SPEC mess_printf(char *fmt, ...)
	/*TODO*///{
	/*TODO*///	va_list arg;
	/*TODO*///	int length = 0;
	
	/*TODO*///	va_start(arg,fmt);
	
	/*TODO*///	if (options.mess_printf_output)
	/*TODO*///		length = options.mess_printf_output(fmt, arg);
	/*TODO*///	else if (!options.gui_host)
	/*TODO*///		length = vprintf(fmt, arg);
	
	/*TODO*///	va_end(arg);
	
	/*TODO*///	return length;
	/*TODO*///}
	
	/*TODO*///static int read_crc_config (const char *file, struct image_info *img, const char* sysname)
	/*TODO*///{
	/*TODO*///	int retval;
	/*TODO*///	void *config = config_open (file);
	
	/*TODO*///	retval = 1;
	/*TODO*///	if (config != 0)
	/*TODO*///	{
	/*TODO*///		char line[1024];
	/*TODO*///		char crc[9+1];
	
	/*TODO*///		sprintf(crc, "%08x", img.crc);
	/*TODO*///		config_load_string(config,sysname,0,crc,line,sizeof(line));
	/*TODO*///		if( line[0] )
	/*TODO*///		{
	/*TODO*///			logerror("found CRC %s= %s\n", crc, line);
	/*TODO*///			img.longname = dupe(stripspace(strtok(line, "|")));
	/*TODO*///			img.manufacturer = dupe(stripspace(strtok(NULL, "|")));
	/*TODO*///			img.year = dupe(stripspace(strtok(NULL, "|")));
	/*TODO*///			img.playable = dupe(stripspace(strtok(NULL, "|")));
	/*TODO*///			img.extrainfo = dupe(stripspace(strtok(NULL, "|")));
	/*TODO*///			retval = 0;
	/*TODO*///		}
	/*TODO*///		config_close(config);
	/*TODO*///	}
	/*TODO*///	return retval;
	/*TODO*///}
	
	
	/*TODO*/////	void *image_fopen(int type, int id, int filetype, int read_or_write)
/*TODO*/////	{
/*TODO*/////		struct image_info *img = &images[type][id];
/*TODO*/////		const char *sysname;
/*TODO*/////		void *file;
/*TODO*/////		int extnum;
/*TODO*/////		int original_len;
/*TODO*/////	
/*TODO*/////		if( type >= IO_COUNT )
/*TODO*/////		{
/*TODO*/////			logerror("image_fopen: type out of range (%d)\n", type);
/*TODO*/////			return NULL;
/*TODO*/////		}
/*TODO*/////	
/*TODO*/////		if( id >= count[type] )
/*TODO*/////		{
/*TODO*/////			logerror("image_fopen: id out of range (%d)\n", id);
/*TODO*/////			return NULL;
/*TODO*/////		}
/*TODO*/////	
/*TODO*/////		if( img.name == NULL )
/*TODO*/////			return NULL;
/*TODO*/////	
/*TODO*/////		/* try the supported extensions */
/*TODO*/////		extnum = 0;
/*TODO*/////	
/*TODO*/////		/* remember original file name */
/*TODO*/////		original_len = strlen(img.name);
/*TODO*/////	
/*TODO*/////		{
/*TODO*/////			extern struct GameDriver driver_0;
/*TODO*/////	
/*TODO*/////			sysname = Machine.gamedrv.name;
/*TODO*/////			logerror("image_fopen: trying %s for system %s\n", img.name, sysname);
/*TODO*/////			file = osd_fopen(sysname, img.name, filetype, read_or_write);
/*TODO*/////			/* file found, break out */
/*TODO*/////			if (!file)
/*TODO*/////			{
/*TODO*/////				if( Machine.gamedrv.clone_of &&
/*TODO*/////					Machine.gamedrv.clone_of != &driver_0 )
/*TODO*/////				{	/* R Nabet : Shouldn't this be moved to osd code ??? Mac osd code does such a retry
/*TODO*/////					whenever it makes sense, and I think this is the correct way. */
/*TODO*/////					sysname = Machine.gamedrv.clone_of.name;
/*TODO*/////					logerror("image_fopen: now trying %s for system %s\n", img.name, sysname);
/*TODO*/////					file = osd_fopen(sysname, img.name, filetype, read_or_write);
/*TODO*/////				}
/*TODO*/////			}
/*TODO*/////		}
/*TODO*/////	
/*TODO*/////		if (file != 0)
/*TODO*/////		{
/*TODO*/////			void *config;
/*TODO*/////			const struct IODevice *pc_dev = Machine.gamedrv.dev;
/*TODO*/////	
/*TODO*/////			/* did osd_fopen() rename the image? (yes, I know this is a hack) */
/*TODO*/////			if (renamed_image != 0)
/*TODO*/////			{
/*TODO*/////				free(img.name);
/*TODO*/////				img.name = renamed_image;
/*TODO*/////				renamed_image = NULL;
/*TODO*/////			}
/*TODO*/////	
/*TODO*/////			logerror("image_fopen: found image %s for system %s\n", img.name, sysname);
/*TODO*/////			img.length = osd_fsize(file);
/*TODO*/////	/* Cowering, partial crcs for NES/A7800/others */
/*TODO*/////			img.crc = 0;
/*TODO*/////			while( pc_dev && pc_dev.count && !img.crc)
/*TODO*/////			{
/*TODO*/////				logerror("partialcrc() . %08lx\n",pc_dev.partialcrc);
/*TODO*/////				if( type == pc_dev.type && pc_dev.partialcrc )
/*TODO*/////				{
/*TODO*/////					UBytePtr pc_buf = (UBytePtr )malloc(img.length);
/*TODO*/////					if (pc_buf != 0)
/*TODO*/////					{
/*TODO*/////						osd_fseek(file,0,SEEK_SET);
/*TODO*/////						osd_fread(file,pc_buf,img.length);
/*TODO*/////						osd_fseek(file,0,SEEK_SET);
/*TODO*/////						logerror("Calling partialcrc()\n");
/*TODO*/////						img.crc = (*pc_dev.partialcrc)(pc_buf,img.length);
/*TODO*/////						free(pc_buf);
/*TODO*/////					}
/*TODO*/////					else
/*TODO*/////					{
/*TODO*/////						logerror("failed to malloc(%d)\n", img.length);
/*TODO*/////					}
/*TODO*/////				}
/*TODO*/////				pc_dev++;
/*TODO*/////			}
/*TODO*/////	
/*TODO*/////			if (!img.crc) img.crc = osd_fcrc(file);
/*TODO*/////			if( img.crc == 0 && img.length < 0x100000 )
/*TODO*/////			{
/*TODO*/////				logerror("image_fopen: calling osd_fchecksum() for %d bytes\n", img.length);
/*TODO*/////				osd_fchecksum(sysname, img.name, &img.length, &img.crc);
/*TODO*/////				logerror("image_fopen: CRC is %08x\n", img.crc);
/*TODO*/////			}
/*TODO*/////	
/*TODO*/////			if (read_crc_config (crcfile, img, sysname) && Machine.gamedrv.clone_of.name)
/*TODO*/////				read_crc_config (pcrcfile, img, Machine.gamedrv.clone_of.name);
/*TODO*/////	
/*TODO*/////			config = config_open(crcfile);
/*TODO*/////		}
/*TODO*/////	
/*TODO*/////		return file;
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	
/*TODO*/////	/* common init for all IO_FLOPPY devices */
/*TODO*/////	static void	floppy_device_common_init(int id)
/*TODO*/////	{
/*TODO*/////		logerror("floppy device common init: id: %02x\n",id);
/*TODO*/////		/* disk inserted */
/*TODO*/////		floppy_drive_set_flag_state(id, FLOPPY_DRIVE_DISK_INSERTED, 1);
/*TODO*/////		/* drive connected */
/*TODO*/////		floppy_drive_set_flag_state(id, FLOPPY_DRIVE_CONNECTED, 1);
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	/* common exit for all IO_FLOPPY devices */
/*TODO*/////	static void floppy_device_common_exit(int id)
/*TODO*/////	{
/*TODO*/////		logerror("floppy device common exit: id: %02x\n",id);
/*TODO*/////		/* disk removed */
/*TODO*/////		floppy_drive_set_flag_state(id, FLOPPY_DRIVE_DISK_INSERTED, 0);
/*TODO*/////		/* drive disconnected */
/*TODO*/////		floppy_drive_set_flag_state(id, FLOPPY_DRIVE_CONNECTED, 0);
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	
/*TODO*/////	/*
/*TODO*/////	 * Does the system support cassette (for tapecontrol)
/*TODO*/////	 * TRUE, FALSE return
/*TODO*/////	 */
/*TODO*/////	int system_supports_cassette_device (void)
/*TODO*/////	{
/*TODO*/////		const struct IODevice *dev = Machine.gamedrv.dev;
/*TODO*/////	
/*TODO*/////		/* Cycle through all devices for this system */
/*TODO*/////		while(dev.type != IO_END)
/*TODO*/////		{
/*TODO*/////			if (dev.type == IO_CASSETTE)
/*TODO*/////				return TRUE;
/*TODO*/////			dev++;
/*TODO*/////		}
/*TODO*/////	
/*TODO*/////		return FALSE;
/*TODO*/////	}

	/*
	 * Return a name for the device type (to be used for UI functions)
	 */
	public static String device_typename(int type)
	{
		if (type < IO_COUNT)
			return devices[type].name;
		return "UNKNOWN";
	}

/*TODO*/////	const char *device_brieftypename(int type)
/*TODO*/////	{
/*TODO*/////		if (type < IO_COUNT)
/*TODO*/////			return devices[type].shortname;
/*TODO*/////		return "UNKNOWN";
/*TODO*/////	}
/*TODO*/////	
	/* Return a name for a device of type 'type' with id 'id' */
        static String[] typename_id_dev = new String[40];
        static int which_dev = 0;
    
	public static String device_typename_id(int type, int id)
	{
		char[][] typename_id=new char[40][31+1];
		int which = 0;
		if (type < IO_COUNT)
		{
                    which_dev = ++which_dev % 40;
                    /* for the average user counting starts at #1 ;-) */
                    typename_id_dev[which_dev] = sprintf("%s #%d", devices[type].name, id + 1);
                    return typename_id_dev[which_dev];
		}
		return "UNKNOWN";
	}
	
/*TODO*/////	/*
/*TODO*/////	 * Return the number of filenames for a device of type 'type'.
/*TODO*/////	 */
/*TODO*/////	int device_count(int type)
/*TODO*/////	{
/*TODO*/////		if (type >= IO_COUNT)
/*TODO*/////			return 0;
/*TODO*/////		return count[type];
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	/*
/*TODO*/////	 * Return the 'id'th filename for a device of type 'type',
/*TODO*/////	 * NULL if not enough image names of that type are available.
/*TODO*/////	 */
/*TODO*/////	const char *device_filename(int type, int id)
/*TODO*/////	{
/*TODO*/////		if (type >= IO_COUNT)
/*TODO*/////			return NULL;
/*TODO*/////		if (id < count[type])
/*TODO*/////			return images[type][id].name;
/*TODO*/////		return NULL;
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	/*
/*TODO*/////	 * Return the 'num'th file extension for a device of type 'type',
/*TODO*/////	 * NULL if no file extensions of that type are available.
/*TODO*/////	 */
/*TODO*/////	const char *device_file_extension(int type, int extnum)
/*TODO*/////	{
/*TODO*/////		const struct IODevice *dev = Machine.gamedrv.dev;
/*TODO*/////		const char *ext;
/*TODO*/////		if (type >= IO_COUNT)
/*TODO*/////			return NULL;
/*TODO*/////		while( dev.count )
/*TODO*/////		{
/*TODO*/////			if( type == dev.type )
/*TODO*/////			{
/*TODO*/////				ext = dev.file_extensions;
/*TODO*/////				while( ext && *ext && extnum-- > 0 )
/*TODO*/////					ext = ext + strlen(ext) + 1;
/*TODO*/////				if( ext && !*ext )
/*TODO*/////					ext = NULL;
/*TODO*/////				return ext;
/*TODO*/////			}
/*TODO*/////			dev++;
/*TODO*/////		}
/*TODO*/////		return NULL;
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	/*
/*TODO*/////	 * Return the 'id'th crc for a device of type 'type',
/*TODO*/////	 * NULL if not enough image names of that type are available.
/*TODO*/////	 */
/*TODO*/////	unsigned int device_crc(int type, int id)
/*TODO*/////	{
/*TODO*/////		if (type >= IO_COUNT)
/*TODO*/////			return 0;
/*TODO*/////		if (id < count[type])
/*TODO*/////			return images[type][id].crc;
/*TODO*/////		return 0;
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	/*
/*TODO*/////	 * Set the 'id'th crc for a device of type 'type',
/*TODO*/////	 * this is to be used if only a 'partial crc' shall be used.
/*TODO*/////	 */
/*TODO*/////	void device_set_crc(int type, int id, UINT32 new_crc)
/*TODO*/////	{
/*TODO*/////		if (type >= IO_COUNT)
/*TODO*/////		{
/*TODO*/////			logerror("device_set_crc: type out of bounds (%d)\n", type);
/*TODO*/////			return;
/*TODO*/////		}
/*TODO*/////		if (id < count[type])
/*TODO*/////		{
/*TODO*/////			images[type][id].crc = new_crc;
/*TODO*/////			logerror("device_set_crc: new_crc %08x\n", new_crc);
/*TODO*/////		}
/*TODO*/////		else
/*TODO*/////			logerror("device_set_crc: id out of bounds (%d)\n", id);
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	/*
/*TODO*/////	 * Return the 'id'th length for a device of type 'type',
/*TODO*/////	 * NULL if not enough image names of that type are available.
/*TODO*/////	 */
/*TODO*/////	unsigned int device_length(int type, int id)
/*TODO*/////	{
/*TODO*/////		if (type >= IO_COUNT)
/*TODO*/////			return 0;
/*TODO*/////		if (id < count[type])
/*TODO*/////			return images[type][id].length;
/*TODO*/////		return 0;
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	/*
/*TODO*/////	 * Return the 'id'th long name for a device of type 'type',
/*TODO*/////	 * NULL if not enough image names of that type are available.
/*TODO*/////	 */
/*TODO*/////	const char *device_longname(int type, int id)
/*TODO*/////	{
/*TODO*/////		if (type >= IO_COUNT)
/*TODO*/////			return NULL;
/*TODO*/////		if (id < count[type])
/*TODO*/////			return images[type][id].longname;
/*TODO*/////		return NULL;
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	/*
/*TODO*/////	 * Return the 'id'th manufacturer name for a device of type 'type',
/*TODO*/////	 * NULL if not enough image names of that type are available.
/*TODO*/////	 */
/*TODO*/////	const char *device_manufacturer(int type, int id)
/*TODO*/////	{
/*TODO*/////		if (type >= IO_COUNT)
/*TODO*/////			return NULL;
/*TODO*/////		if (id < count[type])
/*TODO*/////			return images[type][id].manufacturer;
/*TODO*/////		return NULL;
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	/*
/*TODO*/////	 * Return the 'id'th release year for a device of type 'type',
/*TODO*/////	 * NULL if not enough image names of that type are available.
/*TODO*/////	 */
/*TODO*/////	const char *device_year(int type, int id)
/*TODO*/////	{
/*TODO*/////		if (type >= IO_COUNT)
/*TODO*/////			return NULL;
/*TODO*/////		if (id < count[type])
/*TODO*/////			return images[type][id].year;
/*TODO*/////		return NULL;
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	/*
/*TODO*/////	 * Return the 'id'th playable info for a device of type 'type',
/*TODO*/////	 * NULL if not enough image names of that type are available.
/*TODO*/////	 */
/*TODO*/////	const char *device_playable(int type, int id)
/*TODO*/////	{
/*TODO*/////		if (type >= IO_COUNT)
/*TODO*/////			return NULL;
/*TODO*/////		if (id < count[type])
/*TODO*/////			return images[type][id].playable;
/*TODO*/////		return NULL;
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	
/*TODO*/////	/*
/*TODO*/////	 * Return the 'id'th extrainfo info for a device of type 'type',
/*TODO*/////	 * NULL if not enough image names of that type are available.
/*TODO*/////	 */
/*TODO*/////	const char *device_extrainfo(int type, int id)
/*TODO*/////	{
/*TODO*/////		if (type >= IO_COUNT)
/*TODO*/////			return NULL;
/*TODO*/////		if (id < count[type])
/*TODO*/////			return images[type][id].extrainfo;
/*TODO*/////		return NULL;
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	
/*TODO*/////	/*****************************************************************************
/*TODO*/////	 *  --Distribute images to their respective Devices--
/*TODO*/////	 *  Copy the Images specified at the CLI from options.image_files[] to the
/*TODO*/////	 *  array of filenames we keep here, depending on the Device type identifier
/*TODO*/////	 *  of each image.  Multiple instances of the same device are allowed
/*TODO*/////	 *  RETURNS 0 on success, 1 if failed
/*TODO*/////	 ****************************************************************************/
/*TODO*/////	static int distribute_images(void)
/*TODO*/////	{
/*TODO*/////		int i,j;
/*TODO*/////	
/*TODO*/////		logerror("Distributing Images to Devices...\n");
/*TODO*/////		/* Set names to NULL */
/*TODO*/////		for (i=0;i<IO_COUNT;i++)
/*TODO*/////			for (j=0;j<MAX_INSTANCES;j++)
/*TODO*/////				images[i][j].name = NULL;
/*TODO*/////	
/*TODO*/////		for( i = 0; i < options.image_count; i++ )
/*TODO*/////		{
/*TODO*/////			int type = options.image_files[i].type;
/*TODO*/////	
/*TODO*/////			if (type < IO_COUNT)
/*TODO*/////			{
/*TODO*/////				/* Add a filename to the arrays of names */
/*TODO*/////				if( options.image_files[i].name )
/*TODO*/////				{
/*TODO*/////					images[type][count[type]].name = dupe(options.image_files[i].name);
/*TODO*/////					if( !images[type][count[type]].name )
/*TODO*/////					{
/*TODO*/////						mess_printf(" ERROR - dupe() failed\n");
/*TODO*/////						return 1;
/*TODO*/////					}
/*TODO*/////				}
/*TODO*/////				count[type]++;
/*TODO*/////			}
/*TODO*/////			else
/*TODO*/////			{
/*TODO*/////				mess_printf(" Invalid Device type %d for %s\n", type, options.image_files[i].name);
/*TODO*/////				return 1;
/*TODO*/////			}
/*TODO*/////		}
/*TODO*/////	
/*TODO*/////		/* everything was fine */
/*TODO*/////		return 0;
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	
/*TODO*/////	
/*TODO*/////	/* Small check to see if system supports device */
/*TODO*/////	static int supported_device(const struct IODevice *dev, int type)
/*TODO*/////	{
/*TODO*/////		while(dev.type!=IO_END)
/*TODO*/////		{
/*TODO*/////			if(dev.type==type)
/*TODO*/////				return TRUE;	/* Return OK */
/*TODO*/////			dev++;
/*TODO*/////		}
/*TODO*/////		return FALSE;
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	/*****************************************************************************
/*TODO*/////	 *  --Initialise Devices--
/*TODO*/////	 *  Call the init() functions for all devices of a driver
/*TODO*/////	 *  ith all user specified image names.
/*TODO*/////	 ****************************************************************************/
/*TODO*/////	int init_devices(const void *game)
/*TODO*/////	{
/*TODO*/////		const struct GameDriver *gamedrv = game;
/*TODO*/////		const struct IODevice *dev = gamedrv.dev;
/*TODO*/////		int i,id;
/*TODO*/////	
/*TODO*/////		logerror("Initialising Devices...\n");
/*TODO*/////	
/*TODO*/////		/* Check that the driver supports all devices requested (options struct)*/
/*TODO*/////		for( i = 0; i < options.image_count; i++ )
/*TODO*/////		{
/*TODO*/////			if (supported_device(dev, options.image_files[i].type)==FALSE)
/*TODO*/////			{
/*TODO*/////				mess_printf(" ERROR: Device [%s] is not supported by this system\n",device_typename(options.image_files[i].type));
/*TODO*/////				return 1;
/*TODO*/////			}
/*TODO*/////		}
/*TODO*/////	
/*TODO*/////		/* Ok! All devices are supported.  Now distribute them to the appropriate device..... */
/*TODO*/////		if (distribute_images() == 1)
/*TODO*/////			return 1;
/*TODO*/////	
/*TODO*/////		/* Initialise all floppy drives here if the device is Setting can be overriden by the drivers and UI */
/*TODO*/////		floppy_drives_init();
/*TODO*/////	
/*TODO*/////		/* initialize --all-- devices */
/*TODO*/////		while( dev.count )
/*TODO*/////		{
/*TODO*/////			/* all instances */
/*TODO*/////			for( id = 0; id < dev.count; id++ )
/*TODO*/////			{
/*TODO*/////				mess_printf("Initialising %s device #%d\n",device_typename(dev.type), id + 1);
/*TODO*/////				/********************************************************************
/*TODO*/////				 * CALL INITIALISE DEVICE
/*TODO*/////				 ********************************************************************/
/*TODO*/////				/* if this device supports initialize (it should!) */
/*TODO*/////				if( dev.init )
/*TODO*/////				{
/*TODO*/////					int result;
/*TODO*/////	
/*TODO*/////					/* initialize */
/*TODO*/////					result = (*dev.init)(id);
/*TODO*/////	
/*TODO*/////					if( result != INIT_PASS)
/*TODO*/////					{
/*TODO*/////						mess_printf("Driver Reports Initialisation [for %s device] failed\n",device_typename(dev.type));
/*TODO*/////						mess_printf("Ensure image is valid and exists and (if needed) can be created\n");
/*TODO*/////						mess_printf("Also remember that some systems cannot boot without a valid image!\n");
/*TODO*/////						return 1;
/*TODO*/////					}
/*TODO*/////	
/*TODO*/////					/* init succeeded */
/*TODO*/////					/* if floppy, perform common init */
/*TODO*/////					if ((dev.type == IO_FLOPPY) && (device_filename(dev.type, id)))
/*TODO*/////					{
/*TODO*/////						floppy_device_common_init(id);
/*TODO*/////					}
/*TODO*/////				}
/*TODO*/////				else
/*TODO*/////				{
/*TODO*/////					mess_printf(" %s does not support init!\n", device_typename(dev.type));
/*TODO*/////				}
/*TODO*/////			}
/*TODO*/////			dev++;
/*TODO*/////		}
/*TODO*/////		mess_printf("Device Initialision Complete!\n");
/*TODO*/////		return 0;
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	/*
/*TODO*/////	 * Call the exit() functions for all devices of a
/*TODO*/////	 * driver for all images.
/*TODO*/////	 */
/*TODO*/////	void exit_devices(void)
/*TODO*/////	{
/*TODO*/////		const struct IODevice *dev = Machine.gamedrv.dev;
/*TODO*/////		int id;
/*TODO*/////		int type;
/*TODO*/////	
/*TODO*/////		/* shutdown all devices */
/*TODO*/////		while( dev.count )
/*TODO*/////		{
/*TODO*/////			/* all instances */
/*TODO*/////			if( dev.exit)
/*TODO*/////			{
/*TODO*/////				/* shutdown */
/*TODO*/////				for( id = 0; id < device_count(dev.type); id++ )
/*TODO*/////					(*dev.exit)(id);
/*TODO*/////	
/*TODO*/////			}
/*TODO*/////			else
/*TODO*/////			{
/*TODO*/////				logerror("%s does not support exit!\n", device_typename(dev.type));
/*TODO*/////			}
/*TODO*/////	
/*TODO*/////			/* The following is always executed for a IO_FLOPPY exit */
/*TODO*/////			/* KT: if a image is removed:
/*TODO*/////				1. Disconnect drive
/*TODO*/////				2. Remove disk from drive */
/*TODO*/////			/* This is done here, so if a device doesn't support exit, the status
/*TODO*/////			will still be correct */
/*TODO*/////			if (dev.type == IO_FLOPPY)
/*TODO*/////			{
/*TODO*/////				for (id = 0; id< device_count(dev.type); id++)
/*TODO*/////				{
/*TODO*/////					floppy_device_common_exit(id);
/*TODO*/////				}
/*TODO*/////			}
/*TODO*/////	
/*TODO*/////			dev++;
/*TODO*/////		}
/*TODO*/////	
/*TODO*/////		for( type = 0; type < IO_COUNT; type++ )
/*TODO*/////		{
/*TODO*/////			if( images[type] )
/*TODO*/////			{
/*TODO*/////				for( id = 0; id < device_count(dev.type); id++ )
/*TODO*/////				{
/*TODO*/////					if( images[type][id].name )
/*TODO*/////						free(images[type][id].name);
/*TODO*/////					if( images[type][id].longname )
/*TODO*/////						free(images[type][id].longname);
/*TODO*/////					if( images[type][id].manufacturer )
/*TODO*/////						free(images[type][id].manufacturer);
/*TODO*/////					if( images[type][id].year )
/*TODO*/////						free(images[type][id].year);
/*TODO*/////					if( images[type][id].playable )
/*TODO*/////						free(images[type][id].playable);
/*TODO*/////					if( images[type][id].extrainfo )
/*TODO*/////						free(images[type][id].extrainfo);
/*TODO*/////				}
/*TODO*/////			}
/*TODO*/////			count[type] = 0;
/*TODO*/////		}
/*TODO*/////	
/*TODO*/////		/* KT: clean up */
/*TODO*/////		floppy_drives_exit();
/*TODO*/////	
/*TODO*/////	#ifdef MAME_DEBUG
/*TODO*/////		for( type = 0; type < IO_COUNT; type++ )
/*TODO*/////		{
/*TODO*/////			if (count[type])
/*TODO*/////				mess_printf("OOPS!!!  Appears not all images free!\n");
/*TODO*/////	
/*TODO*/////		}
/*TODO*/////	#endif
/*TODO*/////	
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	/*
/*TODO*/////	 * Change the associated image filename for a device.
/*TODO*/////	 * Returns 0 if successful.
/*TODO*/////	 */
/*TODO*/////	int device_filename_change(int type, int id, const char *name)
/*TODO*/////	{
/*TODO*/////		const struct IODevice *dev = Machine.gamedrv.dev;
/*TODO*/////		struct image_info *img = &images[type][id];
/*TODO*/////	
/*TODO*/////		if( type >= IO_COUNT )
/*TODO*/////			return 1;
/*TODO*/////	
/*TODO*/////		while( dev.count && dev.type != type )
/*TODO*/////			dev++;
/*TODO*/////	
/*TODO*/////		if( id >= dev.count )
/*TODO*/////			return 1;
/*TODO*/////	
/*TODO*/////		if( dev.exit )
/*TODO*/////			dev.exit(id);
/*TODO*/////	
/*TODO*/////		/* if floppy, perform common exit */
/*TODO*/////		if (dev.type == IO_FLOPPY)
/*TODO*/////		{
/*TODO*/////			floppy_device_common_exit(id);
/*TODO*/////		}
/*TODO*/////	
/*TODO*/////		if( dev.init )
/*TODO*/////		{
/*TODO*/////			int result;
/*TODO*/////			/*
/*TODO*/////			 * set the new filename and reset all addition info, it will
/*TODO*/////			 * be inserted by osd_fopen() and the crc handling
/*TODO*/////			 */
/*TODO*/////			img.name = NULL;
/*TODO*/////			img.length = 0;
/*TODO*/////			img.crc = 0;
/*TODO*/////			if (name != 0)
/*TODO*/////			{
/*TODO*/////				img.name = dupe(name);
/*TODO*/////				/* Check the name */
/*TODO*/////				if( !img.name )
/*TODO*/////					return 1;
/*TODO*/////				/* check the count - if it was 0, add new! */
/*TODO*/////				if (!device_count(type))
/*TODO*/////					count[type]++;
/*TODO*/////			}
/*TODO*/////	
/*TODO*/////			if( dev.reset_depth == IO_RESET_CPU )
/*TODO*/////				machine_reset();
/*TODO*/////			else
/*TODO*/////			if( dev.reset_depth == IO_RESET_ALL )
/*TODO*/////			{
/*TODO*/////				mess_keep_going = 1;
/*TODO*/////	
/*TODO*/////			}
/*TODO*/////	
/*TODO*/////			result = (*dev.init)(id);
/*TODO*/////			if( result != INIT_PASS)
/*TODO*/////				return 1;
/*TODO*/////	
/*TODO*/////			/* init succeeded */
/*TODO*/////			/* if floppy, perform common init */
/*TODO*/////			if (dev.type == IO_FLOPPY)
/*TODO*/////			{
/*TODO*/////				floppy_device_common_init(id);
/*TODO*/////			}
/*TODO*/////	
/*TODO*/////		}
/*TODO*/////		return 0;
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	
/*TODO*/////	
/*TODO*/////	
/*TODO*/////	
/*TODO*/////	
/*TODO*/////	
/*TODO*/////	
/*TODO*/////	
/*TODO*/////	#ifdef MAME_DEBUG
/*TODO*/////	int messvaliditychecks(void)
/*TODO*/////	{
/*TODO*/////		int i;
/*TODO*/////		int error = 0;
/*TODO*/////	
/*TODO*/////		/* Check the device struct array */
/*TODO*/////		i=0;
/*TODO*/////		while (devices[i].id != IO_COUNT)
/*TODO*/////		{
/*TODO*/////			if (devices[i].id != i)
/*TODO*/////			{
/*TODO*/////				mess_printf("MESS Validity Error - Device struct array order mismatch\n");
/*TODO*/////				error = 1;
/*TODO*/////			}
/*TODO*/////			i++;
/*TODO*/////		}
/*TODO*/////		if (i < IO_COUNT)
/*TODO*/////		{
/*TODO*/////			mess_printf("MESS Validity Error - Device struct entry missing\n");
/*TODO*/////			error = 1;
/*TODO*/////		}
/*TODO*/////		return error;
/*TODO*/////	}
/*TODO*/////	#endif
/*TODO*/////	
/*TODO*/////	
/*TODO*/////	int displayimageinfo(struct mame_bitmap *bitmap, int selected)
/*TODO*/////	{
/*TODO*/////		char buf[2048], *dst = buf;
/*TODO*/////		int type, id, sel = selected - 1;
/*TODO*/////	
/*TODO*/////		dst += sprintf(dst,"%s\n\n",Machine.gamedrv.description);
/*TODO*/////	
/*TODO*/////		for (type = 0; type < IO_COUNT; type++)
/*TODO*/////		{
/*TODO*/////			for( id = 0; id < device_count(type); id++ )
/*TODO*/////			{
/*TODO*/////				const char *name = device_filename(type,id);
/*TODO*/////				if (name != 0)
/*TODO*/////				{
/*TODO*/////					const char *info;
/*TODO*/////					char *filename;
/*TODO*/////	
/*TODO*/////					filename = (char *) device_filename(type, id);
/*TODO*/////	
/*TODO*/////					dst += sprintf(dst,"%s: %s\n", device_typename_id(type,id), osd_basename(filename));
/*TODO*/////					info = device_longname(type,id);
/*TODO*/////					if (info != 0)
/*TODO*/////						dst += sprintf(dst,"%s\n", info);
/*TODO*/////					info = device_manufacturer(type,id);
/*TODO*/////					if (info != 0)
/*TODO*/////					{
/*TODO*/////						dst += sprintf(dst,"%s", info);
/*TODO*/////						info = stripspace(device_year(type,id));
/*TODO*/////						if( info && strlen(info))
/*TODO*/////							dst += sprintf(dst,", %s", info);
/*TODO*/////						dst += sprintf(dst,"\n");
/*TODO*/////					}
/*TODO*/////					info = device_playable(type,id);
/*TODO*/////					if (info != 0)
/*TODO*/////						dst += sprintf(dst,"%s\n", info);
/*TODO*/////	// why is extrainfo printed? only MSX and NES use it that i know of ... Cowering
/*TODO*/////	//				info = device_extrainfo(type,id);
/*TODO*/////	//				if (info != 0)
/*TODO*/////	//					dst += sprintf(dst,"%s\n", info);
/*TODO*/////				}
/*TODO*/////				else
/*TODO*/////				{
/*TODO*/////					dst += sprintf(dst,"%s: ---\n", device_typename_id(type,id));
/*TODO*/////				}
/*TODO*/////			}
/*TODO*/////		}
/*TODO*/////	
/*TODO*/////		if (sel == -1)
/*TODO*/////		{
/*TODO*/////			/* startup info, print MAME version and ask for any key */
/*TODO*/////	
/*TODO*/////			strcat(buf,"\n\tPress any key to Begin");
/*TODO*/////			ui_drawbox(bitmap,0,0,Machine.uiwidth,Machine.uiheight);
/*TODO*/////			ui_displaymessagewindow(bitmap, buf);
/*TODO*/////	
/*TODO*/////			sel = 0;
/*TODO*/////			if (code_read_async() != KEYCODE_NONE ||
/*TODO*/////				code_read_async() != JOYCODE_NONE)
/*TODO*/////				sel = -1;
/*TODO*/////		}
/*TODO*/////		else
/*TODO*/////		{
/*TODO*/////			/* menu system, use the normal menu keys */
/*TODO*/////			strcat(buf,"\n\t\x1a Return to Main Menu \x1b");
/*TODO*/////	
/*TODO*/////			ui_displaymessagewindow(bitmap,buf);
/*TODO*/////	
/*TODO*/////			if (input_ui_pressed(IPT_UI_SELECT))
/*TODO*/////				sel = -1;
/*TODO*/////	
/*TODO*/////			if (input_ui_pressed(IPT_UI_CANCEL))
/*TODO*/////				sel = -1;
/*TODO*/////	
/*TODO*/////			if (input_ui_pressed(IPT_UI_CONFIGURE))
/*TODO*/////				sel = -2;
/*TODO*/////		}
/*TODO*/////	
/*TODO*/////		if (sel == -1 || sel == -2)
/*TODO*/////		{
/*TODO*/////			/* tell updatescreen() to clean after us */
/*TODO*/////			schedule_full_refresh();
/*TODO*/////		}
/*TODO*/////	
/*TODO*/////		return sel + 1;
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	
/*TODO*/////	void showmessdisclaimer(void)
/*TODO*/////	{
/*TODO*/////		mess_printf(
/*TODO*/////			"MESS is an emulator: it reproduces, more or less faithfully, the behaviour of\n"
/*TODO*/////			"several computer and console systems. But hardware is useless without software\n"
/*TODO*/////			"so a file dump of the BIOS, cartridges, discs, and cassettes which run on that\n"
/*TODO*/////			"hardware is required. Such files, like any other commercial software, are\n"
/*TODO*/////			"copyrighted material and it is therefore illegal to use them if you don't own\n"
/*TODO*/////			"the original media from which the files are derived. Needless to say, these\n"
/*TODO*/////			"files are not distributed together with MESS. Distribution of MESS together\n"
/*TODO*/////			"with these files is a violation of copyright law and should be promptly\n"
/*TODO*/////			"reported to the authors so that appropriate legal action can be taken.\n\n");
/*TODO*/////	}
/*TODO*/////	
/*TODO*/////	void showmessinfo(void)
/*TODO*/////	{
/*TODO*/////		mess_printf(
/*TODO*/////			"M.E.S.S. v%s\n"
/*TODO*/////			"Multiple Emulation Super System - Copyright (C) 1997-2001 by the MESS Team\n"
/*TODO*/////			"M.E.S.S. is based on the ever excellent M.A.M.E. Source code\n"
/*TODO*/////			"Copyright (C) 1997-2001 by Nicola Salmoria and the MAME Team\n\n",
/*TODO*/////			build_version);
/*TODO*/////		showmessdisclaimer();
/*TODO*/////		mess_printf(
/*TODO*/////			"Usage:  MESS <system> <device> <software> <options>\n\n"
/*TODO*/////			"        MESS -list        for a brief list of supported systems\n"
/*TODO*/////			"        MESS -listdevices for a full list of supported devices\n"
/*TODO*/////			"        MESS -showusage   to see usage instructions\n"
/*TODO*/////			"See mess.txt for help, readme.txt for options.\n");
/*TODO*/////	
/*TODO*/////	}
	
}
