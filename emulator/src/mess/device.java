/******************************************************************************

  MESS - device.c

  List of all available devices and Device handling interfaces.

******************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess;

import static mess.deviceH.*;
import static mess.deviceH.*;
import static mess.messH.*;
import static mess.mess.*;

import static WIP.mame.mame.*;
import static WIP2.mame.mameH.*;

import static old.arcadeflex.libc_old.*;

public class device
{
	
	/* The List of Devices, with Associated Names - Be careful to ensure that 	*/
	/* this list matches the ENUM from device.h, so searches can use IO_COUNT	*/
	public static Devices devices[] =
	{
            new Devices(IO_END,		"NONE",		"NONE"), /*  0 */
            new Devices(IO_CARTSLOT,	"cartridge",	"cart"), /*  1 */
            new Devices(IO_FLOPPY,	"floppydisk",	"flop"), /*  2 */
            new Devices(IO_HARDDISK,	"harddisk",	"hard"), /*  3 */
            new Devices(IO_CYLINDER,	"cylinder",	"cyln"), /*  4 */
            new Devices(IO_CASSETTE,	"cassette",	"cass"), /*  5 */
            new Devices(IO_PUNCHCARD,	"punchcard",	"pcrd"), /*  6 */
            new Devices(IO_PUNCHTAPE,	"punchtape",	"ptap"), /*  7 */
            new Devices(IO_PRINTER,	"printer",	"prin"), /*  8 */
            new Devices(IO_SERIAL,	"serial",	"serl"), /*  9 */
            new Devices(IO_PARALLEL,    "parallel",	"parl"), /* 10 */
            new Devices(IO_SNAPSHOT,	"snapshot",	"dump"), /* 11 */
            new Devices(IO_QUICKLOAD,	"quickload",	"quik"), /* 12 */
            new Devices(IO_COUNT,	null,		null  ), /* 13 Always at end of this array! */
	};
	
	
	/* register_device() - used to register the device in the options struct...	*/
	/* Call this from the CLI or UI to add a DEVICE (with its arg) to the 		*/
	/* options struct.  Return 0 for success, -1 for error 						*/
	public static int register_device (int type, char[] arg)
	{
		/*TODO*/////extern struct GameOptions options;
	
		/* Check the the device type is valid, otherwise this lookup will be bad*/
		if (type <= IO_END || type >= IO_COUNT || (type==0))
		{
			/*TODO*/////mess_printf("register_device() failed! - device type [%d] is not valid\n",type);
                        printf("register_device() failed! - device type [%d] is not valid\n",type);
			return -1;
		}
	
		/* Next, check that we havent loaded too many images					*/
		if (options.image_count >= MAX_IMAGES)
		{
			/*TODO*/////mess_printf("Too many image names specified!\n");
                        printf("Too many image names specified!\n");
			return -1;
		}
	
		/* All seems ok to add device and argument to options{} struct			*/
		/*TODO*/////#ifdef MAME_DEBUG
		/*TODO*/////mess_printf("Image [%s] Registered for Device [%s]\n", arg, device_typename(type));
                printf("Image [%s] Registered for Device [%s]\n", arg, device_typename(type));
		/*TODO*/////#endif
		/* the user specified a device type */
		options.image_files[options.image_count].type = type;
		/*TODO*/////options.image_files[options.image_count].name = strdup(arg);
		options.image_count++;
		return 0;
	
	}
	
	
	public static int device_open(int type, int id, int mode, Object args)
	{
            /*TODO*/////IODevice[] dev = Machine.gamedrv.dev;
            /*TODO*/////int dev_ptr = 0;
            /*TODO*/////while (dev != null && dev[dev_ptr].count != 0) {
            /*TODO*/////    if (type == dev[dev_ptr].type && dev[dev_ptr].open != null) {
            /*TODO*/////        return dev[dev_ptr].open.handler(id, mode, args);
            /*TODO*/////    }
            /*TODO*/////    dev_ptr++;
            /*TODO*/////}
            return 1;
	}
	
	public static void device_close(int type, int id)
	{
            /*TODO*/////IODevice[] dev = Machine.gamedrv.dev;
            /*TODO*/////int dev_ptr = 0;
            /*TODO*/////while( dev != null && dev[dev_ptr].count != 0 )
            /*TODO*/////{
            /*TODO*/////	if( type == dev.type && dev.close )
            /*TODO*/////	{
            /*TODO*/////		dev[dev_ptr].close.handler(id);
            /*TODO*/////		return;
            /*TODO*/////	}
            /*TODO*/////	dev++;
            /*TODO*/////}
	}
	
	public static int device_seek(int type, int id, int offset, int whence)
	{
            /*TODO*/////IODevice[] dev = Machine.gamedrv.dev;
            /*TODO*/////int dev_ptr = 0;
            /*TODO*/////while( dev != null && dev[dev_ptr].count != 0 )
            /*TODO*/////{
            /*TODO*/////	if( type == dev.type && dev.seek )
            /*TODO*/////		return (dev[dev_ptr].seek.handler(id,offset,whence));
            /*TODO*/////	dev++;
            /*TODO*/////}
            return 0;
	}
	
	public static int device_tell(int type, int id)
	{
            /*TODO*/////IODevice[] dev = Machine.gamedrv.dev;
            /*TODO*/////int dev_ptr = 0;
            /*TODO*/////while( dev != null && dev[dev_ptr].count != 0 )
            /*TODO*/////{
            /*TODO*/////	if( type == dev.type && dev.tell )
            /*TODO*/////		return (dev[dev_ptr].tell.handler(id));
            /*TODO*/////	dev++;
            /*TODO*/////}
            return 0;
	}
	
	public static int device_status(int type, int id, int newstatus)
	{
            /*TODO*/////IODevice[] dev = Machine.gamedrv.dev;
            /*TODO*/////int dev_ptr = 0;
            /*TODO*/////while( dev != null && dev[dev_ptr].count != 0 )
            /*TODO*/////{
            /*TODO*/////	if( type == dev.type && dev.status )
            /*TODO*/////		return (dev[dev_ptr].status.handler(id,newstatus));
            /*TODO*/////	dev++;
            /*TODO*/////}
            return 0;
	}
	
	public static int device_input(int type, int id)
	{
            /*TODO*/////IODevice[] dev = Machine.gamedrv.dev;
            /*TODO*/////int dev_ptr = 0;
            /*TODO*/////while( dev != null && dev[dev_ptr].count != 0 )
            /*TODO*/////{
            /*TODO*/////	if( type == dev.type && dev.input )
            /*TODO*/////		return (dev[dev_ptr].input.handler(id));
            /*TODO*/////	dev++;
            /*TODO*/////}
            return 0;
	}
	
	public static void device_output(int type, int id, int data)
	{
            /*TODO*/////IODevice[] dev = Machine.gamedrv.dev;
            /*TODO*/////int dev_ptr = 0;
            /*TODO*/////while( dev != null && dev[dev_ptr].count != 0 )
            /*TODO*/////{
            /*TODO*/////	if( type == dev.type && dev.output )
            /*TODO*/////	{
            /*TODO*/////		dev[dev_ptr].output.handler(id,data);
            /*TODO*/////		return;
            /*TODO*/////	}
            /*TODO*/////	dev++;
            /*TODO*/////}
	}
	
	public static int device_input_chunk(int type, int id, Object dst, int chunks)
	{
            /*TODO*/////IODevice[] dev = Machine.gamedrv.dev;
            /*TODO*/////int dev_ptr = 0;
            /*TODO*/////while( dev != null && dev[dev_ptr].count != 0 )
            /*TODO*/////{
            /*TODO*/////	if( type == dev.type && dev.input_chunk )
            /*TODO*/////		return (dev[dev_ptr].input_chunk.handler(id,dst,chunks));
            /*TODO*/////	dev++;
            /*TODO*/////}
            return 1;
	}
	
	public static void device_output_chunk(int type, int id, Object src, int chunks)
	{
            /*TODO*/////IODevice[] dev = Machine.gamedrv.dev;
            /*TODO*/////int dev_ptr = 0;
            /*TODO*/////while( dev != null && dev[dev_ptr].count != 0 )
            /*TODO*/////{
            /*TODO*/////	if( type == dev.type && dev.output )
            /*TODO*/////	{
            /*TODO*/////		dev[dev_ptr].output_chunk.handler(id,src,chunks);
            /*TODO*/////		return;
            /*TODO*/////	}
            /*TODO*/////	dev++;
            /*TODO*/////}
	}
}
