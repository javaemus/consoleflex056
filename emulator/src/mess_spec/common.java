/**
 *  ported to mess 0.56 (similar with mame 0.56 but with some mess stuff in it)
 * 
 */
package mess_spec;

public class common {
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Constants
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///// VERY IMPORTANT: osd_alloc_bitmap must allocate also a "safety area" 16 pixels wide all
/*TODO*///// around the bitmap. This is required because, for performance reasons, some graphic
/*TODO*///// routines don't clip at boundaries of the bitmap.
/*TODO*///#define BITMAP_SAFETY			16
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Type definitions
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///struct rom_load_data
/*TODO*///{
/*TODO*///	int 		warnings;				/* warning count during processing */
/*TODO*///	int 		errors;					/* error count during processing */
/*TODO*///
/*TODO*///	int 		romsloaded;				/* current ROMs loaded count */
/*TODO*///	int			romstotal;				/* total number of ROMs to read */
/*TODO*///
/*TODO*///	void *		file;					/* current file */
/*TODO*///
/*TODO*///	UINT8 *		regionbase;				/* base of current region */
/*TODO*///	UINT32		regionlength;			/* length of current region */
/*TODO*///
/*TODO*///	char		errorbuf[4096];			/* accumulated errors */
/*TODO*///	UINT8		tempbuf[65536];			/* temporary buffer */
/*TODO*///};
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Global variables
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*////* These globals are only kept on a machine basis - LBO 042898 */
/*TODO*///unsigned int dispensed_tickets;
/*TODO*///unsigned int coins[COIN_COUNTERS];
/*TODO*///unsigned int lastcoin[COIN_COUNTERS];
/*TODO*///unsigned int coinlockedout[COIN_COUNTERS];
/*TODO*///
    public static int[] flip_screen_x = new int[1];
    public static int[] flip_screen_y = new int[1];
/*TODO*///
/*TODO*///int snapno;
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Prototypes
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///static int rom_load_new(const struct RomModule *romp);
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Functions
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///void showdisclaimer(void)   /* MAURY_BEGIN: dichiarazione */
/*TODO*///{
/*TODO*///	printf("MAME is an emulator: it reproduces, more or less faithfully, the behaviour of\n"
/*TODO*///		 "several arcade machines. But hardware is useless without software, so an image\n"
/*TODO*///		 "of the ROMs which run on that hardware is required. Such ROMs, like any other\n"
/*TODO*///		 "commercial software, are copyrighted material and it is therefore illegal to\n"
/*TODO*///		 "use them if you don't own the original arcade machine. Needless to say, ROMs\n"
/*TODO*///		 "are not distributed together with MAME. Distribution of MAME together with ROM\n"
/*TODO*///		 "images is a violation of copyright law and should be promptly reported to the\n"
/*TODO*///		 "authors so that appropriate legal action can be taken.\n\n");
/*TODO*///}                           /* MAURY_END: dichiarazione */
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Sample handling code
/*TODO*///
/*TODO*///	This function is different from readroms() because it doesn't fail if
/*TODO*///	it doesn't find a file: it will load as many samples as it can find.
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///#ifdef LSB_FIRST
/*TODO*///#define intelLong(x) (x)
/*TODO*///#else
/*TODO*///#define intelLong(x) (((x << 24) | (((unsigned long) x) >> 24) | (( x & 0x0000ff00) << 8) | (( x & 0x00ff0000) >> 8)))
/*TODO*///#endif
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	read_wav_sample - read a WAV file as a sample
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static struct GameSample *read_wav_sample(void *f)
/*TODO*///{
/*TODO*///	unsigned long offset = 0;
/*TODO*///	UINT32 length, rate, filesize, temp32;
/*TODO*///	UINT16 bits, temp16;
/*TODO*///	char buf[32];
/*TODO*///	struct GameSample *result;
/*TODO*///
/*TODO*///	/* read the core header and make sure it's a WAVE file */
/*TODO*///	offset += osd_fread(f, buf, 4);
/*TODO*///	if (offset < 4)
/*TODO*///		return NULL;
/*TODO*///	if (memcmp(&buf[0], "RIFF", 4) != 0)
/*TODO*///		return NULL;
/*TODO*///
/*TODO*///	/* get the total size */
/*TODO*///	offset += osd_fread(f, &filesize, 4);
/*TODO*///	if (offset < 8)
/*TODO*///		return NULL;
/*TODO*///	filesize = intelLong(filesize);
/*TODO*///
/*TODO*///	/* read the RIFF file type and make sure it's a WAVE file */
/*TODO*///	offset += osd_fread(f, buf, 4);
/*TODO*///	if (offset < 12)
/*TODO*///		return NULL;
/*TODO*///	if (memcmp(&buf[0], "WAVE", 4) != 0)
/*TODO*///		return NULL;
/*TODO*///
/*TODO*///	/* seek until we find a format tag */
/*TODO*///	while (1)
/*TODO*///	{
/*TODO*///		offset += osd_fread(f, buf, 4);
/*TODO*///		offset += osd_fread(f, &length, 4);
/*TODO*///		length = intelLong(length);
/*TODO*///		if (memcmp(&buf[0], "fmt ", 4) == 0)
/*TODO*///			break;
/*TODO*///
/*TODO*///		/* seek to the next block */
/*TODO*///		osd_fseek(f, length, SEEK_CUR);
/*TODO*///		offset += length;
/*TODO*///		if (offset >= filesize)
/*TODO*///			return NULL;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* read the format -- make sure it is PCM */
/*TODO*///	offset += osd_fread_lsbfirst(f, &temp16, 2);
/*TODO*///	if (temp16 != 1)
/*TODO*///		return NULL;
/*TODO*///
/*TODO*///	/* number of channels -- only mono is supported */
/*TODO*///	offset += osd_fread_lsbfirst(f, &temp16, 2);
/*TODO*///	if (temp16 != 1)
/*TODO*///		return NULL;
/*TODO*///
/*TODO*///	/* sample rate */
/*TODO*///	offset += osd_fread(f, &rate, 4);
/*TODO*///	rate = intelLong(rate);
/*TODO*///
/*TODO*///	/* bytes/second and block alignment are ignored */
/*TODO*///	offset += osd_fread(f, buf, 6);
/*TODO*///
/*TODO*///	/* bits/sample */
/*TODO*///	offset += osd_fread_lsbfirst(f, &bits, 2);
/*TODO*///	if (bits != 8 && bits != 16)
/*TODO*///		return NULL;
/*TODO*///
/*TODO*///	/* seek past any extra data */
/*TODO*///	osd_fseek(f, length - 16, SEEK_CUR);
/*TODO*///	offset += length - 16;
/*TODO*///
/*TODO*///	/* seek until we find a data tag */
/*TODO*///	while (1)
/*TODO*///	{
/*TODO*///		offset += osd_fread(f, buf, 4);
/*TODO*///		offset += osd_fread(f, &length, 4);
/*TODO*///		length = intelLong(length);
/*TODO*///		if (memcmp(&buf[0], "data", 4) == 0)
/*TODO*///			break;
/*TODO*///
/*TODO*///		/* seek to the next block */
/*TODO*///		osd_fseek(f, length, SEEK_CUR);
/*TODO*///		offset += length;
/*TODO*///		if (offset >= filesize)
/*TODO*///			return NULL;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* allocate the game sample */
/*TODO*///	result = malloc(sizeof(struct GameSample) + length);
/*TODO*///	if (result == NULL)
/*TODO*///		return NULL;
/*TODO*///
/*TODO*///	/* fill in the sample data */
/*TODO*///	result->length = length;
/*TODO*///	result->smpfreq = rate;
/*TODO*///	result->resolution = bits;
/*TODO*///
/*TODO*///	/* read the data in */
/*TODO*///	if (bits == 8)
/*TODO*///	{
/*TODO*///		osd_fread(f, result->data, length);
/*TODO*///
/*TODO*///		/* convert 8-bit data to signed samples */
/*TODO*///		for (temp32 = 0; temp32 < length; temp32++)
/*TODO*///			result->data[temp32] ^= 0x80;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		/* 16-bit data is fine as-is */
/*TODO*///		osd_fread_lsbfirst(f, result->data, length);
/*TODO*///	}
/*TODO*///
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	readsamples - load all samples
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///struct GameSamples *readsamples(const char **samplenames,const char *basename)
/*TODO*////* V.V - avoids samples duplication */
/*TODO*////* if first samplename is *dir, looks for samples into "basename" first, then "dir" */
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	struct GameSamples *samples;
/*TODO*///	int skipfirst = 0;
/*TODO*///
/*TODO*///	/* if the user doesn't want to use samples, bail */
/*TODO*///	if (!options.use_samples) return 0;
/*TODO*///
/*TODO*///	if (samplenames == 0 || samplenames[0] == 0) return 0;
/*TODO*///
/*TODO*///	if (samplenames[0][0] == '*')
/*TODO*///		skipfirst = 1;
/*TODO*///
/*TODO*///	i = 0;
/*TODO*///	while (samplenames[i+skipfirst] != 0) i++;
/*TODO*///
/*TODO*///	if (!i) return 0;
/*TODO*///
/*TODO*///	if ((samples = malloc(sizeof(struct GameSamples) + (i-1)*sizeof(struct GameSample))) == 0)
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	samples->total = i;
/*TODO*///	for (i = 0;i < samples->total;i++)
/*TODO*///		samples->sample[i] = 0;
/*TODO*///
/*TODO*///	for (i = 0;i < samples->total;i++)
/*TODO*///	{
/*TODO*///		void *f;
/*TODO*///
/*TODO*///		if (samplenames[i+skipfirst][0])
/*TODO*///		{
/*TODO*///			if ((f = osd_fopen(basename,samplenames[i+skipfirst],OSD_FILETYPE_SAMPLE,0)) == 0)
/*TODO*///				if (skipfirst)
/*TODO*///					f = osd_fopen(samplenames[0]+1,samplenames[i+skipfirst],OSD_FILETYPE_SAMPLE,0);
/*TODO*///			if (f != 0)
/*TODO*///			{
/*TODO*///				samples->sample[i] = read_wav_sample(f);
/*TODO*///				osd_fclose(f);
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	return samples;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	freesamples - free allocated samples
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void freesamples(struct GameSamples *samples)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///
/*TODO*///	if (samples == 0) return;
/*TODO*///
/*TODO*///	for (i = 0;i < samples->total;i++)
/*TODO*///		free(samples->sample[i]);
/*TODO*///
/*TODO*///	free(samples);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Memory region code
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	memory_region - returns pointer to a memory
/*TODO*///	region
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///unsigned char *memory_region(int num)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///	if (num < MAX_MEMORY_REGIONS)
/*TODO*///		return Machine->memory_region[num].base;
/*TODO*///	else
/*TODO*///	{
/*TODO*///		for (i = 0;i < MAX_MEMORY_REGIONS;i++)
/*TODO*///		{
/*TODO*///			if (Machine->memory_region[i].type == num)
/*TODO*///				return Machine->memory_region[i].base;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	memory_region_length - returns length of a
/*TODO*///	memory region
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///size_t memory_region_length(int num)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///	if (num < MAX_MEMORY_REGIONS)
/*TODO*///		return Machine->memory_region[num].length;
/*TODO*///	else
/*TODO*///	{
/*TODO*///		for (i = 0;i < MAX_MEMORY_REGIONS;i++)
/*TODO*///		{
/*TODO*///			if (Machine->memory_region[i].type == num)
/*TODO*///				return Machine->memory_region[i].length;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	new_memory_region - allocates memory for a
/*TODO*///	region
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///int new_memory_region(int num, size_t length, UINT32 flags)
/*TODO*///{
/*TODO*///    int i;
/*TODO*///
/*TODO*///    if (num < MAX_MEMORY_REGIONS)
/*TODO*///    {
/*TODO*///        Machine->memory_region[num].length = length;
/*TODO*///        Machine->memory_region[num].base = malloc(length);
/*TODO*///        return (Machine->memory_region[num].base == NULL) ? 1 : 0;
/*TODO*///    }
/*TODO*///    else
/*TODO*///    {
/*TODO*///        for (i = 0;i < MAX_MEMORY_REGIONS;i++)
/*TODO*///        {
/*TODO*///            if (Machine->memory_region[i].base == NULL)
/*TODO*///            {
/*TODO*///                Machine->memory_region[i].length = length;
/*TODO*///                Machine->memory_region[i].type = num;
/*TODO*///                Machine->memory_region[i].flags = flags;
/*TODO*///                Machine->memory_region[i].base = malloc(length);
/*TODO*///                return (Machine->memory_region[i].base == NULL) ? 1 : 0;
/*TODO*///            }
/*TODO*///        }
/*TODO*///    }
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	free_memory_region - releases memory for a
/*TODO*///	region
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void free_memory_region(int num)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///	if (num < MAX_MEMORY_REGIONS)
/*TODO*///	{
/*TODO*///		free(Machine->memory_region[num].base);
/*TODO*///		memset(&Machine->memory_region[num], 0, sizeof(Machine->memory_region[num]));
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		for (i = 0;i < MAX_MEMORY_REGIONS;i++)
/*TODO*///		{
/*TODO*///			if (Machine->memory_region[i].type == num)
/*TODO*///			{
/*TODO*///				free(Machine->memory_region[i].base);
/*TODO*///				memset(&Machine->memory_region[i], 0, sizeof(Machine->memory_region[i]));
/*TODO*///				return;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Coin counter code
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	coin_counter_w - sets input for coin counter
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void coin_counter_w(int num,int on)
/*TODO*///{
/*TODO*///	if (num >= COIN_COUNTERS) return;
/*TODO*///	/* Count it only if the data has changed from 0 to non-zero */
/*TODO*///	if (on && (lastcoin[num] == 0))
/*TODO*///	{
/*TODO*///		coins[num]++;
/*TODO*///	}
/*TODO*///	lastcoin[num] = on;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	coin_lockout_w - locks out one coin input
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void coin_lockout_w(int num,int on)
/*TODO*///{
/*TODO*///	if (num >= COIN_COUNTERS) return;
/*TODO*///
/*TODO*///	coinlockedout[num] = on;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	coin_lockout_global_w - locks out all the coin
/*TODO*///	inputs
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void coin_lockout_global_w(int on)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///	for (i = 0; i < COIN_COUNTERS; i++)
/*TODO*///	{
/*TODO*///		coin_lockout_w(i,on);
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Global video attribute handling code
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	updateflip - handle global flipping
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void updateflip(void)
/*TODO*///{
/*TODO*///	int min_x,max_x,min_y,max_y;
/*TODO*///
/*TODO*///	tilemap_set_flip(ALL_TILEMAPS,(TILEMAP_FLIPX & flip_screen_x) | (TILEMAP_FLIPY & flip_screen_y));
/*TODO*///
/*TODO*///	min_x = Machine->drv->default_visible_area.min_x;
/*TODO*///	max_x = Machine->drv->default_visible_area.max_x;
/*TODO*///	min_y = Machine->drv->default_visible_area.min_y;
/*TODO*///	max_y = Machine->drv->default_visible_area.max_y;
/*TODO*///
/*TODO*///	if (flip_screen_x)
/*TODO*///	{
/*TODO*///		int temp;
/*TODO*///
/*TODO*///		temp = Machine->drv->screen_width - min_x - 1;
/*TODO*///		min_x = Machine->drv->screen_width - max_x - 1;
/*TODO*///		max_x = temp;
/*TODO*///	}
/*TODO*///	if (flip_screen_y)
/*TODO*///	{
/*TODO*///		int temp;
/*TODO*///
/*TODO*///		temp = Machine->drv->screen_height - min_y - 1;
/*TODO*///		min_y = Machine->drv->screen_height - max_y - 1;
/*TODO*///		max_y = temp;
/*TODO*///	}
/*TODO*///
/*TODO*///	set_visible_area(min_x,max_x,min_y,max_y);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	flip_screen_set - set global flip
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void flip_screen_set(int on)
/*TODO*///{
/*TODO*///	flip_screen_x_set(on);
/*TODO*///	flip_screen_y_set(on);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	flip_screen_x_set - set global horizontal flip
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void flip_screen_x_set(int on)
/*TODO*///{
/*TODO*///	if (on) on = ~0;
/*TODO*///	if (flip_screen_x != on)
/*TODO*///	{
/*TODO*///		set_vh_global_attribute(&flip_screen_x,on);
/*TODO*///		updateflip();
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	flip_screen_y_set - set global vertical flip
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void flip_screen_y_set(int on)
/*TODO*///{
/*TODO*///	if (on) on = ~0;
/*TODO*///	if (flip_screen_y != on)
/*TODO*///	{
/*TODO*///		set_vh_global_attribute(&flip_screen_y,on);
/*TODO*///		updateflip();
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	set_vh_global_attribute - set an arbitrary
/*TODO*///	global video attribute
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void set_vh_global_attribute( int *addr, int data )
/*TODO*///{
/*TODO*///	if (*addr != data)
/*TODO*///	{
/*TODO*///		schedule_full_refresh();
/*TODO*///		*addr = data;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	set_visible_area - adjusts the visible portion
/*TODO*///	of the bitmap area dynamically
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void set_visible_area(int min_x,int max_x,int min_y,int max_y)
/*TODO*///{
/*TODO*///	Machine->visible_area.min_x = min_x;
/*TODO*///	Machine->visible_area.max_x = max_x;
/*TODO*///	Machine->visible_area.min_y = min_y;
/*TODO*///	Machine->visible_area.max_y = max_y;
/*TODO*///
/*TODO*///	/* vector games always use the whole bitmap */
/*TODO*///	if (Machine->drv->video_attributes & VIDEO_TYPE_VECTOR)
/*TODO*///	{
/*TODO*///		min_x = 0;
/*TODO*///		max_x = Machine->scrbitmap->width - 1;
/*TODO*///		min_y = 0;
/*TODO*///		max_y = Machine->scrbitmap->height - 1;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		int temp;
/*TODO*///
/*TODO*///		if (Machine->orientation & ORIENTATION_SWAP_XY)
/*TODO*///		{
/*TODO*///			temp = min_x; min_x = min_y; min_y = temp;
/*TODO*///			temp = max_x; max_x = max_y; max_y = temp;
/*TODO*///		}
/*TODO*///		if (Machine->orientation & ORIENTATION_FLIP_X)
/*TODO*///		{
/*TODO*///			temp = Machine->scrbitmap->width - min_x - 1;
/*TODO*///			min_x = Machine->scrbitmap->width - max_x - 1;
/*TODO*///			max_x = temp;
/*TODO*///		}
/*TODO*///		if (Machine->orientation & ORIENTATION_FLIP_Y)
/*TODO*///		{
/*TODO*///			temp = Machine->scrbitmap->height - min_y - 1;
/*TODO*///			min_y = Machine->scrbitmap->height - max_y - 1;
/*TODO*///			max_y = temp;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	osd_set_visible_area(min_x,max_x,min_y,max_y);
/*TODO*///
/*TODO*///	Machine->absolute_visible_area.min_x = min_x;
/*TODO*///	Machine->absolute_visible_area.max_x = max_x;
/*TODO*///	Machine->absolute_visible_area.min_y = min_y;
/*TODO*///	Machine->absolute_visible_area.max_y = max_y;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Bitmap allocation/freeing code
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	bitmap_alloc - allocate a bitmap at the
/*TODO*///	current screen depth
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///struct mame_bitmap *bitmap_alloc(int width,int height)
/*TODO*///{
/*TODO*///	return bitmap_alloc_depth(width,height,Machine->scrbitmap->depth);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	bitmap_alloc_depth - allocate a bitmap for a
/*TODO*///	specific depth
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///struct mame_bitmap *bitmap_alloc_depth(int width,int height,int depth)
/*TODO*///{
/*TODO*///	struct mame_bitmap *bitmap;
/*TODO*///
/*TODO*///	/* cheesy kludge: pass in negative depth to prevent orientation swapping */
/*TODO*///	if (depth < 0)
/*TODO*///	{
/*TODO*///		depth = -depth;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* adjust for orientation */
/*TODO*///	else if (Machine->orientation & ORIENTATION_SWAP_XY)
/*TODO*///	{
/*TODO*///		int temp = width; width = height; height = temp;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* verify it's a depth we can handle */
/*TODO*///	if (depth != 8 && depth != 15 && depth != 16 && depth != 32)
/*TODO*///	{
/*TODO*///		logerror("osd_alloc_bitmap() unknown depth %d\n",depth);
/*TODO*///		return NULL;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* allocate memory for the bitmap struct */
/*TODO*///	bitmap = malloc(sizeof(struct mame_bitmap));
/*TODO*///	if (bitmap != NULL)
/*TODO*///	{
/*TODO*///		int i, rowlen, rdwidth, bitmapsize, linearraysize, pixelsize;
/*TODO*///		unsigned char *bm;
/*TODO*///
/*TODO*///		/* initialize the basic parameters */
/*TODO*///		bitmap->depth = depth;
/*TODO*///		bitmap->width = width;
/*TODO*///		bitmap->height = height;
/*TODO*///
/*TODO*///		/* determine pixel size in bytes */
/*TODO*///		pixelsize = 1;
/*TODO*///		if (depth == 15 || depth == 16)
/*TODO*///			pixelsize = 2;
/*TODO*///		else if (depth == 32)
/*TODO*///			pixelsize = 4;
/*TODO*///
/*TODO*///		/* round the width to a multiple of 8 */
/*TODO*///		rdwidth = (width + 7) & ~7;
/*TODO*///		rowlen = rdwidth + 2 * BITMAP_SAFETY;
/*TODO*///		bitmap->rowpixels = rowlen;
/*TODO*///
/*TODO*///		/* now convert from pixels to bytes */
/*TODO*///		rowlen *= pixelsize;
/*TODO*///		bitmap->rowbytes = rowlen;
/*TODO*///
/*TODO*///		/* determine total memory for bitmap and line arrays */
/*TODO*///		bitmapsize = (height + 2 * BITMAP_SAFETY) * rowlen;
/*TODO*///		linearraysize = (height + 2 * BITMAP_SAFETY) * sizeof(unsigned char *);
/*TODO*///
/*TODO*///		/* allocate the bitmap data plus an array of line pointers */
/*TODO*///		bitmap->line = malloc(linearraysize + bitmapsize);
/*TODO*///		if (bitmap->line == NULL)
/*TODO*///		{
/*TODO*///			free(bitmap);
/*TODO*///			return NULL;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* clear ALL bitmap, including safety area, to avoid garbage on right */
/*TODO*///		bm = (unsigned char *)bitmap->line + linearraysize;
/*TODO*///		memset(bm, 0, (height + 2 * BITMAP_SAFETY) * rowlen);
/*TODO*///
/*TODO*///		/* initialize the line pointers */
/*TODO*///		for (i = 0; i < height + 2 * BITMAP_SAFETY; i++)
/*TODO*///			bitmap->line[i] = &bm[i * rowlen + BITMAP_SAFETY * pixelsize];
/*TODO*///
/*TODO*///		/* adjust for the safety rows */
/*TODO*///		bitmap->line += BITMAP_SAFETY;
/*TODO*///		bitmap->base = bitmap->line[0];
/*TODO*///	}
/*TODO*///
/*TODO*///	/* return the result */
/*TODO*///	return bitmap;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	bitmap_free - free a bitmap
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void bitmap_free(struct mame_bitmap *bitmap)
/*TODO*///{
/*TODO*///	/* skip if NULL */
/*TODO*///	if (!bitmap)
/*TODO*///		return;
/*TODO*///
/*TODO*///	/* unadjust for the safety rows */
/*TODO*///	bitmap->line -= BITMAP_SAFETY;
/*TODO*///
/*TODO*///	/* free the memory */
/*TODO*///	free(bitmap->line);
/*TODO*///	free(bitmap);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Screen snapshot code
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	save_screen_snapshot_as - save a snapshot to
/*TODO*///	the given filename
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void save_screen_snapshot_as(void *fp,struct mame_bitmap *bitmap)
/*TODO*///{
/*TODO*///	if (Machine->drv->video_attributes & VIDEO_TYPE_VECTOR)
/*TODO*///		png_write_bitmap(fp,bitmap);
/*TODO*///	else
/*TODO*///	{
/*TODO*///		struct mame_bitmap *copy;
/*TODO*///		int sizex, sizey, scalex, scaley;
/*TODO*///
/*TODO*///		sizex = Machine->visible_area.max_x - Machine->visible_area.min_x + 1;
/*TODO*///		sizey = Machine->visible_area.max_y - Machine->visible_area.min_y + 1;
/*TODO*///
/*TODO*///		scalex = (Machine->drv->video_attributes & VIDEO_PIXEL_ASPECT_RATIO_2_1) ? 2 : 1;
/*TODO*///		scaley = (Machine->drv->video_attributes & VIDEO_PIXEL_ASPECT_RATIO_1_2) ? 2 : 1;
/*TODO*///
/*TODO*///		copy = bitmap_alloc_depth(sizex * scalex,sizey * scaley,bitmap->depth);
/*TODO*///
/*TODO*///		if (copy)
/*TODO*///		{
/*TODO*///			int x,y,sx,sy;
/*TODO*///
/*TODO*///			sx = Machine->absolute_visible_area.min_x;
/*TODO*///			sy = Machine->absolute_visible_area.min_y;
/*TODO*///			if (Machine->orientation & ORIENTATION_SWAP_XY)
/*TODO*///			{
/*TODO*///				int t;
/*TODO*///
/*TODO*///				t = scalex; scalex = scaley; scaley = t;
/*TODO*///			}
/*TODO*///
/*TODO*///			switch (bitmap->depth)
/*TODO*///			{
/*TODO*///			case 8:
/*TODO*///				for (y = 0;y < copy->height;y++)
/*TODO*///				{
/*TODO*///					for (x = 0;x < copy->width;x++)
/*TODO*///					{
/*TODO*///						((UINT8 *)copy->line[y])[x] = ((UINT8 *)bitmap->line[sy+(y/scaley)])[sx +(x/scalex)];
/*TODO*///					}
/*TODO*///				}
/*TODO*///				break;
/*TODO*///			case 15:
/*TODO*///			case 16:
/*TODO*///				for (y = 0;y < copy->height;y++)
/*TODO*///				{
/*TODO*///					for (x = 0;x < copy->width;x++)
/*TODO*///					{
/*TODO*///						((UINT16 *)copy->line[y])[x] = ((UINT16 *)bitmap->line[sy+(y/scaley)])[sx +(x/scalex)];
/*TODO*///					}
/*TODO*///				}
/*TODO*///				break;
/*TODO*///			case 32:
/*TODO*///				for (y = 0;y < copy->height;y++)
/*TODO*///				{
/*TODO*///					for (x = 0;x < copy->width;x++)
/*TODO*///					{
/*TODO*///						((UINT32 *)copy->line[y])[x] = ((UINT32 *)bitmap->line[sy+(y/scaley)])[sx +(x/scalex)];
/*TODO*///					}
/*TODO*///				}
/*TODO*///				break;
/*TODO*///			default:
/*TODO*///				logerror("Unknown color depth\n");
/*TODO*///				break;
/*TODO*///			}
/*TODO*///			png_write_bitmap(fp,copy);
/*TODO*///			bitmap_free(copy);
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	save_screen_snapshot - save a screen snapshot
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void save_screen_snapshot(struct mame_bitmap *bitmap)
/*TODO*///{
/*TODO*///	char name[20];
/*TODO*///	void *fp;
/*TODO*///
/*TODO*///	/* avoid overwriting existing files */
/*TODO*///	/* first of all try with "gamename.png" */
/*TODO*///	sprintf(name,"%.8s", Machine->gamedrv->name);
/*TODO*///	if (osd_faccess(name,OSD_FILETYPE_SCREENSHOT))
/*TODO*///	{
/*TODO*///		do
/*TODO*///		{
/*TODO*///			/* otherwise use "nameNNNN.png" */
/*TODO*///			sprintf(name,"%.4s%04d",Machine->gamedrv->name,snapno++);
/*TODO*///		} while (osd_faccess(name, OSD_FILETYPE_SCREENSHOT));
/*TODO*///	}
/*TODO*///
/*TODO*///	if ((fp = osd_fopen(Machine->gamedrv->name, name, OSD_FILETYPE_SCREENSHOT, 1)) != NULL)
/*TODO*///	{
/*TODO*///		save_screen_snapshot_as(fp,bitmap);
/*TODO*///		osd_fclose(fp);
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	ROM loading code
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	rom_first_region - return pointer to first ROM
/*TODO*///	region
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///const struct RomModule *rom_first_region(const struct GameDriver *drv)
/*TODO*///{
/*TODO*///	return drv->rom;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	rom_next_region - return pointer to next ROM
/*TODO*///	region
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///const struct RomModule *rom_next_region(const struct RomModule *romp)
/*TODO*///{
/*TODO*///	romp++;
/*TODO*///	while (!ROMENTRY_ISREGIONEND(romp))
/*TODO*///		romp++;
/*TODO*///	return ROMENTRY_ISEND(romp) ? NULL : romp;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	rom_first_file - return pointer to first ROM
/*TODO*///	file
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///const struct RomModule *rom_first_file(const struct RomModule *romp)
/*TODO*///{
/*TODO*///	romp++;
/*TODO*///	while (!ROMENTRY_ISFILE(romp) && !ROMENTRY_ISREGIONEND(romp))
/*TODO*///		romp++;
/*TODO*///	return ROMENTRY_ISREGIONEND(romp) ? NULL : romp;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	rom_next_file - return pointer to next ROM
/*TODO*///	file
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///const struct RomModule *rom_next_file(const struct RomModule *romp)
/*TODO*///{
/*TODO*///	romp++;
/*TODO*///	while (!ROMENTRY_ISFILE(romp) && !ROMENTRY_ISREGIONEND(romp))
/*TODO*///		romp++;
/*TODO*///	return ROMENTRY_ISREGIONEND(romp) ? NULL : romp;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	rom_first_chunk - return pointer to first ROM
/*TODO*///	chunk
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///const struct RomModule *rom_first_chunk(const struct RomModule *romp)
/*TODO*///{
/*TODO*///	return (ROMENTRY_ISFILE(romp)) ? romp : NULL;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	rom_next_chunk - return pointer to next ROM
/*TODO*///	chunk
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///const struct RomModule *rom_next_chunk(const struct RomModule *romp)
/*TODO*///{
/*TODO*///	romp++;
/*TODO*///	return (ROMENTRY_ISCONTINUE(romp)) ? romp : NULL;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	debugload - log data to a file
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void CLIB_DECL debugload(const char *string, ...)
/*TODO*///{
/*TODO*///#ifdef LOG_LOAD
/*TODO*///	static int opened;
/*TODO*///	va_list arg;
/*TODO*///	FILE *f;
/*TODO*///
/*TODO*///	f = fopen("romload.log", opened++ ? "a" : "w");
/*TODO*///	if (f)
/*TODO*///	{
/*TODO*///		va_start(arg, string);
/*TODO*///		vfprintf(f, string, arg);
/*TODO*///		va_end(arg);
/*TODO*///		fclose(f);
/*TODO*///	}
/*TODO*///#endif
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	count_roms - counts the total number of ROMs
/*TODO*///	that will need to be loaded
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int count_roms(const struct RomModule *romp)
/*TODO*///{
/*TODO*///	const struct RomModule *region, *rom;
/*TODO*///	int count = 0;
/*TODO*///
/*TODO*///	/* loop over regions, then over files */
/*TODO*///	for (region = romp; region; region = rom_next_region(region))
/*TODO*///		for (rom = rom_first_file(region); rom; rom = rom_next_file(rom))
/*TODO*///			count++;
/*TODO*///
/*TODO*///	/* return the total count */
/*TODO*///	return count;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	fill_random - fills an area of memory with
/*TODO*///	random data
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void fill_random(UINT8 *base, UINT32 length)
/*TODO*///{
/*TODO*///	while (length--)
/*TODO*///		*base++ = rand();
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	handle_missing_file - handles error generation
/*TODO*///	for missing files
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void handle_missing_file(struct rom_load_data *romdata, const struct RomModule *romp)
/*TODO*///{
/*TODO*///	/* optional files are okay */
/*TODO*///	if (ROM_ISOPTIONAL(romp))
/*TODO*///	{
/*TODO*///		sprintf(&romdata->errorbuf[strlen(romdata->errorbuf)], "OPTIONAL %-12s NOT FOUND\n", ROM_GETNAME(romp));
/*TODO*///		romdata->warnings++;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* no good dumps are okay */
/*TODO*///	else if (ROM_NOGOODDUMP(romp))
/*TODO*///	{
/*TODO*///		sprintf(&romdata->errorbuf[strlen(romdata->errorbuf)], "%-12s NOT FOUND (NO GOOD DUMP KNOWN)\n", ROM_GETNAME(romp));
/*TODO*///		romdata->warnings++;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* anything else is bad */
/*TODO*///	else
/*TODO*///	{
/*TODO*///		sprintf(&romdata->errorbuf[strlen(romdata->errorbuf)], "%-12s NOT FOUND\n", ROM_GETNAME(romp));
/*TODO*///		romdata->errors++;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	verify_length_and_crc - verify the length
/*TODO*///	and CRC of a file
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void verify_length_and_crc(struct rom_load_data *romdata, const char *name, UINT32 explength, UINT32 expcrc)
/*TODO*///{
/*TODO*///	UINT32 actlength, actcrc;
/*TODO*///
/*TODO*///	/* we've already complained if there is no file */
/*TODO*///	if (!romdata->file)
/*TODO*///		return;
/*TODO*///
/*TODO*///	/* get the length and CRC from the file */
/*TODO*///	actlength = osd_fsize(romdata->file);
/*TODO*///	actcrc = osd_fcrc(romdata->file);
/*TODO*///
/*TODO*///	/* verify length */
/*TODO*///	if (explength != actlength)
/*TODO*///	{
/*TODO*///		sprintf(&romdata->errorbuf[strlen(romdata->errorbuf)], "%-12s WRONG LENGTH (expected: %08x found: %08x)\n", name, explength, actlength);
/*TODO*///		romdata->warnings++;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* verify CRC */
/*TODO*///	if (expcrc != actcrc)
/*TODO*///	{
/*TODO*///		/* expected CRC == 0 means no good dump known */
/*TODO*///		if (expcrc == 0)
/*TODO*///			sprintf(&romdata->errorbuf[strlen(romdata->errorbuf)], "%-12s NO GOOD DUMP KNOWN\n", name);
/*TODO*///
/*TODO*///		/* inverted CRC means needs redump */
/*TODO*///		else if (expcrc == BADCRC(actcrc))
/*TODO*///			sprintf(&romdata->errorbuf[strlen(romdata->errorbuf)], "%-12s ROM NEEDS REDUMP\n",name);
/*TODO*///
/*TODO*///		/* otherwise, it's just bad */
/*TODO*///		else
/*TODO*///			sprintf(&romdata->errorbuf[strlen(romdata->errorbuf)], "%-12s WRONG CRC (expected: %08x found: %08x)\n", name, expcrc, actcrc);
/*TODO*///		romdata->warnings++;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	display_rom_load_results - display the final
/*TODO*///	results of ROM loading
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int display_rom_load_results(struct rom_load_data *romdata)
/*TODO*///{
/*TODO*///	int region;
/*TODO*///
/*TODO*///	/* final status display */
/*TODO*///	osd_display_loading_rom_message(NULL, romdata->romsloaded, romdata->romstotal);
/*TODO*///
/*TODO*///	/* only display if we have warnings or errors */
/*TODO*///	if (romdata->warnings || romdata->errors)
/*TODO*///	{
/*TODO*///		extern int bailing;
/*TODO*///
/*TODO*///		/* display either an error message or a warning message */
/*TODO*///		if (romdata->errors)
/*TODO*///		{
/*TODO*///			#ifndef MESS
/*TODO*///			strcat(romdata->errorbuf, "ERROR: required files are missing, the game cannot be run.\n");
/*TODO*///			#else
/*TODO*///			strcat(romdata->errorbuf, "ERROR: required files are missing, the system cannot be started.\n");
/*TODO*///			#endif
/*TODO*///			bailing = 1;
/*TODO*///		}
/*TODO*///		else
/*TODO*///			#ifndef MESS
/*TODO*///			strcat(romdata->errorbuf, "WARNING: the game might not run correctly.\n");
/*TODO*///			#else
/*TODO*///			strcat(romdata->errorbuf, "WARNING: the system might not operate correctly.\n");
/*TODO*///			#endif
/*TODO*///
/*TODO*///		/* display the result */
/*TODO*///		printf("%s", romdata->errorbuf);
/*TODO*///
/*TODO*///		/* if we're not getting out of here, wait for a keypress */
/*TODO*///		if (!options.gui_host && !bailing)
/*TODO*///		{
/*TODO*///			int k;
/*TODO*///
/*TODO*///			/* loop until we get one */
/*TODO*///			printf ("Press any key to continue\n");
/*TODO*///			do
/*TODO*///			{
/*TODO*///				k = code_read_async();
/*TODO*///			}
/*TODO*///			while (k == CODE_NONE || k == KEYCODE_LCONTROL);
/*TODO*///
/*TODO*///			/* bail on a control + C */
/*TODO*///			if (keyboard_pressed(KEYCODE_LCONTROL) && keyboard_pressed(KEYCODE_C))
/*TODO*///				return 1;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* clean up any regions */
/*TODO*///	if (romdata->errors)
/*TODO*///		for (region = 0; region < MAX_MEMORY_REGIONS; region++)
/*TODO*///			free_memory_region(region);
/*TODO*///
/*TODO*///	/* return true if we had any errors */
/*TODO*///	return (romdata->errors != 0);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	region_post_process - post-process a region,
/*TODO*///	byte swapping and inverting data as necessary
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void region_post_process(struct rom_load_data *romdata, const struct RomModule *regiondata)
/*TODO*///{
/*TODO*///	int type = ROMREGION_GETTYPE(regiondata);
/*TODO*///	int datawidth = ROMREGION_GETWIDTH(regiondata) / 8;
/*TODO*///	int littleendian = ROMREGION_ISLITTLEENDIAN(regiondata);
/*TODO*///	UINT8 *base;
/*TODO*///	int i, j;
/*TODO*///
/*TODO*///	debugload("+ datawidth=%d little=%d\n", datawidth, littleendian);
/*TODO*///
/*TODO*///	/* if this is a CPU region, override with the CPU width and endianness */
/*TODO*///	if (type >= REGION_CPU1 && type < REGION_CPU1 + MAX_CPU)
/*TODO*///	{
/*TODO*///		int cputype = Machine->drv->cpu[type - REGION_CPU1].cpu_type & ~CPU_FLAGS_MASK;
/*TODO*///		if (cputype != 0)
/*TODO*///		{
/*TODO*///			datawidth = cputype_databus_width(cputype) / 8;
/*TODO*///			littleendian = (cputype_endianess(cputype) == CPU_IS_LE);
/*TODO*///			debugload("+ CPU region #%d: datawidth=%d little=%d\n", type - REGION_CPU1, datawidth, littleendian);
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* if the region is inverted, do that now */
/*TODO*///	if (ROMREGION_ISINVERTED(regiondata))
/*TODO*///	{
/*TODO*///		debugload("+ Inverting region\n");
/*TODO*///		for (i = 0, base = romdata->regionbase; i < romdata->regionlength; i++)
/*TODO*///			*base++ ^= 0xff;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* swap the endianness if we need to */
/*TODO*///#ifdef LSB_FIRST
/*TODO*///	if (datawidth > 1 && !littleendian)
/*TODO*///#else
/*TODO*///	if (datawidth > 1 && littleendian)
/*TODO*///#endif
/*TODO*///	{
/*TODO*///		debugload("+ Byte swapping region\n");
/*TODO*///		for (i = 0, base = romdata->regionbase; i < romdata->regionlength; i += datawidth)
/*TODO*///		{
/*TODO*///			UINT8 temp[8];
/*TODO*///			memcpy(temp, base, datawidth);
/*TODO*///			for (j = datawidth - 1; j >= 0; j--)
/*TODO*///				*base++ = temp[j];
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	open_rom_file - open a ROM file, searching
/*TODO*///	up the parent and loading via CRC
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int open_rom_file(struct rom_load_data *romdata, const struct RomModule *romp)
/*TODO*///{
/*TODO*///	const struct GameDriver *drv;
/*TODO*///	char crc[9];
/*TODO*///
/*TODO*///	/* update status display */
/*TODO*///	if (osd_display_loading_rom_message(ROM_GETNAME(romp), ++romdata->romsloaded, romdata->romstotal) != 0)
/*TODO*///       return 0;
/*TODO*///
/*TODO*///	/* first attempt reading up the chain through the parents */
/*TODO*///	romdata->file = NULL;
/*TODO*///	for (drv = Machine->gamedrv; !romdata->file && drv; drv = drv->clone_of)
/*TODO*///		if (drv->name && *drv->name)
/*TODO*///			romdata->file = osd_fopen(drv->name, ROM_GETNAME(romp), OSD_FILETYPE_ROM, 0);
/*TODO*///
/*TODO*///	/* if that failed, attempt to open via CRC */
/*TODO*///	sprintf(crc, "%08x", ROM_GETCRC(romp));
/*TODO*///	for (drv = Machine->gamedrv; !romdata->file && drv; drv = drv->clone_of)
/*TODO*///		if (drv->name && *drv->name)
/*TODO*///			romdata->file = osd_fopen(drv->name, crc, OSD_FILETYPE_ROM, 0);
/*TODO*///
/*TODO*///	/* return the result */
/*TODO*///	return (romdata->file != NULL);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	rom_fread - cheesy fread that fills with
/*TODO*///	random data for a NULL file
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int rom_fread(struct rom_load_data *romdata, UINT8 *buffer, int length)
/*TODO*///{
/*TODO*///	/* files just pass through */
/*TODO*///	if (romdata->file)
/*TODO*///		return osd_fread(romdata->file, buffer, length);
/*TODO*///
/*TODO*///	/* otherwise, fill with randomness */
/*TODO*///	else
/*TODO*///		fill_random(buffer, length);
/*TODO*///
/*TODO*///	return length;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	read_rom_data - read ROM data for a single
/*TODO*///	entry
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int read_rom_data(struct rom_load_data *romdata, const struct RomModule *romp)
/*TODO*///{
/*TODO*///	int datashift = ROM_GETBITSHIFT(romp);
/*TODO*///	int datamask = ((1 << ROM_GETBITWIDTH(romp)) - 1) << datashift;
/*TODO*///	int numbytes = ROM_GETLENGTH(romp);
/*TODO*///	int groupsize = ROM_GETGROUPSIZE(romp);
/*TODO*///	int skip = ROM_GETSKIPCOUNT(romp);
/*TODO*///	int reversed = ROM_ISREVERSED(romp);
/*TODO*///	int numgroups = (numbytes + groupsize - 1) / groupsize;
/*TODO*///	UINT8 *base = romdata->regionbase + ROM_GETOFFSET(romp);
/*TODO*///	int i;
/*TODO*///
/*TODO*///	debugload("Loading ROM data: offs=%X len=%X mask=%02X group=%d skip=%d reverse=%d\n", ROM_GETOFFSET(romp), numbytes, datamask, groupsize, skip, reversed);
/*TODO*///
/*TODO*///	/* make sure the length was an even multiple of the group size */
/*TODO*///	if (numbytes % groupsize != 0)
/*TODO*///	{
/*TODO*///		printf("Error in RomModule definition: %s length not an even multiple of group size\n", ROM_GETNAME(romp));
/*TODO*///		return -1;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* make sure we only fill within the region space */
/*TODO*///	if (ROM_GETOFFSET(romp) + numgroups * groupsize + (numgroups - 1) * skip > romdata->regionlength)
/*TODO*///	{
/*TODO*///		printf("Error in RomModule definition: %s out of memory region space\n", ROM_GETNAME(romp));
/*TODO*///		return -1;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* make sure the length was valid */
/*TODO*///	if (numbytes == 0)
/*TODO*///	{
/*TODO*///		printf("Error in RomModule definition: %s has an invalid length\n", ROM_GETNAME(romp));
/*TODO*///		return -1;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* special case for simple loads */
/*TODO*///	if (datamask == 0xff && (groupsize == 1 || !reversed) && skip == 0)
/*TODO*///		return rom_fread(romdata, base, numbytes);
/*TODO*///
/*TODO*///	/* chunky reads for complex loads */
/*TODO*///	skip += groupsize;
/*TODO*///	while (numbytes)
/*TODO*///	{
/*TODO*///		int evengroupcount = (sizeof(romdata->tempbuf) / groupsize) * groupsize;
/*TODO*///		int bytesleft = (numbytes > evengroupcount) ? evengroupcount : numbytes;
/*TODO*///		UINT8 *bufptr = romdata->tempbuf;
/*TODO*///
/*TODO*///		/* read as much as we can */
/*TODO*///		debugload("  Reading %X bytes into buffer\n", bytesleft);
/*TODO*///		if (rom_fread(romdata, romdata->tempbuf, bytesleft) != bytesleft)
/*TODO*///			return 0;
/*TODO*///		numbytes -= bytesleft;
/*TODO*///
/*TODO*///		debugload("  Copying to %08X\n", (int)base);
/*TODO*///
/*TODO*///		/* unmasked cases */
/*TODO*///		if (datamask == 0xff)
/*TODO*///		{
/*TODO*///			/* non-grouped data */
/*TODO*///			if (groupsize == 1)
/*TODO*///				for (i = 0; i < bytesleft; i++, base += skip)
/*TODO*///					*base = *bufptr++;
/*TODO*///
/*TODO*///			/* grouped data -- non-reversed case */
/*TODO*///			else if (!reversed)
/*TODO*///				while (bytesleft)
/*TODO*///				{
/*TODO*///					for (i = 0; i < groupsize && bytesleft; i++, bytesleft--)
/*TODO*///						base[i] = *bufptr++;
/*TODO*///					base += skip;
/*TODO*///				}
/*TODO*///
/*TODO*///			/* grouped data -- reversed case */
/*TODO*///			else
/*TODO*///				while (bytesleft)
/*TODO*///				{
/*TODO*///					for (i = groupsize - 1; i >= 0 && bytesleft; i--, bytesleft--)
/*TODO*///						base[i] = *bufptr++;
/*TODO*///					base += skip;
/*TODO*///				}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* masked cases */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			/* non-grouped data */
/*TODO*///			if (groupsize == 1)
/*TODO*///				for (i = 0; i < bytesleft; i++, base += skip)
/*TODO*///					*base = (*base & ~datamask) | ((*bufptr++ << datashift) & datamask);
/*TODO*///
/*TODO*///			/* grouped data -- non-reversed case */
/*TODO*///			else if (!reversed)
/*TODO*///				while (bytesleft)
/*TODO*///				{
/*TODO*///					for (i = 0; i < groupsize && bytesleft; i++, bytesleft--)
/*TODO*///						base[i] = (base[i] & ~datamask) | ((*bufptr++ << datashift) & datamask);
/*TODO*///					base += skip;
/*TODO*///				}
/*TODO*///
/*TODO*///			/* grouped data -- reversed case */
/*TODO*///			else
/*TODO*///				while (bytesleft)
/*TODO*///				{
/*TODO*///					for (i = groupsize - 1; i >= 0 && bytesleft; i--, bytesleft--)
/*TODO*///						base[i] = (base[i] & ~datamask) | ((*bufptr++ << datashift) & datamask);
/*TODO*///					base += skip;
/*TODO*///				}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	debugload("  All done\n");
/*TODO*///	return ROM_GETLENGTH(romp);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	fill_rom_data - fill a region of ROM space
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int fill_rom_data(struct rom_load_data *romdata, const struct RomModule *romp)
/*TODO*///{
/*TODO*///	UINT32 numbytes = ROM_GETLENGTH(romp);
/*TODO*///	UINT8 *base = romdata->regionbase + ROM_GETOFFSET(romp);
/*TODO*///
/*TODO*///	/* make sure we fill within the region space */
/*TODO*///	if (ROM_GETOFFSET(romp) + numbytes > romdata->regionlength)
/*TODO*///	{
/*TODO*///		printf("Error in RomModule definition: FILL out of memory region space\n");
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* make sure the length was valid */
/*TODO*///	if (numbytes == 0)
/*TODO*///	{
/*TODO*///		printf("Error in RomModule definition: FILL has an invalid length\n");
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* fill the data */
/*TODO*///	memset(base, ROM_GETCRC(romp) & 0xff, numbytes);
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	copy_rom_data - copy a region of ROM space
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int copy_rom_data(struct rom_load_data *romdata, const struct RomModule *romp)
/*TODO*///{
/*TODO*///	UINT8 *base = romdata->regionbase + ROM_GETOFFSET(romp);
/*TODO*///	int srcregion = ROM_GETFLAGS(romp) >> 24;
/*TODO*///	UINT32 numbytes = ROM_GETLENGTH(romp);
/*TODO*///	UINT32 srcoffs = ROM_GETCRC(romp);
/*TODO*///	UINT8 *srcbase;
/*TODO*///
/*TODO*///	/* make sure we copy within the region space */
/*TODO*///	if (ROM_GETOFFSET(romp) + numbytes > romdata->regionlength)
/*TODO*///	{
/*TODO*///		printf("Error in RomModule definition: COPY out of target memory region space\n");
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* make sure the length was valid */
/*TODO*///	if (numbytes == 0)
/*TODO*///	{
/*TODO*///		printf("Error in RomModule definition: COPY has an invalid length\n");
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* make sure the source was valid */
/*TODO*///	srcbase = memory_region(srcregion);
/*TODO*///	if (!srcbase)
/*TODO*///	{
/*TODO*///		printf("Error in RomModule definition: COPY from an invalid region\n");
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* make sure we find within the region space */
/*TODO*///	if (srcoffs + numbytes > memory_region_length(srcregion))
/*TODO*///	{
/*TODO*///		printf("Error in RomModule definition: COPY out of source memory region space\n");
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* fill the data */
/*TODO*///	memcpy(base, srcbase + srcoffs, numbytes);
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	process_rom_entries - process all ROM entries
/*TODO*///	for a region
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int process_rom_entries(struct rom_load_data *romdata, const struct RomModule *romp)
/*TODO*///{
/*TODO*///	UINT32 lastflags = 0;
/*TODO*///
/*TODO*///	/* loop until we hit the end of this region */
/*TODO*///	while (!ROMENTRY_ISREGIONEND(romp))
/*TODO*///	{
/*TODO*///		/* if this is a continue entry, it's invalid */
/*TODO*///		if (ROMENTRY_ISCONTINUE(romp))
/*TODO*///		{
/*TODO*///			printf("Error in RomModule definition: ROM_CONTINUE not preceded by ROM_LOAD\n");
/*TODO*///			goto fatalerror;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* if this is a reload entry, it's invalid */
/*TODO*///		if (ROMENTRY_ISRELOAD(romp))
/*TODO*///		{
/*TODO*///			printf("Error in RomModule definition: ROM_RELOAD not preceded by ROM_LOAD\n");
/*TODO*///			goto fatalerror;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* handle fills */
/*TODO*///		if (ROMENTRY_ISFILL(romp))
/*TODO*///		{
/*TODO*///			if (!fill_rom_data(romdata, romp++))
/*TODO*///				goto fatalerror;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* handle copies */
/*TODO*///		else if (ROMENTRY_ISCOPY(romp))
/*TODO*///		{
/*TODO*///			if (!copy_rom_data(romdata, romp++))
/*TODO*///				goto fatalerror;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* handle files */
/*TODO*///		else if (ROMENTRY_ISFILE(romp))
/*TODO*///		{
/*TODO*///			const struct RomModule *baserom = romp;
/*TODO*///			int explength = 0;
/*TODO*///
/*TODO*///			/* open the file */
/*TODO*///			debugload("Opening ROM file: %s\n", ROM_GETNAME(romp));
/*TODO*///			if (!open_rom_file(romdata, romp))
/*TODO*///				handle_missing_file(romdata, romp);
/*TODO*///
/*TODO*///			/* loop until we run out of reloads */
/*TODO*///			do
/*TODO*///			{
/*TODO*///				/* loop until we run out of continues */
/*TODO*///				do
/*TODO*///				{
/*TODO*///					struct RomModule modified_romp = *romp++;
/*TODO*///					int readresult;
/*TODO*///
/*TODO*///					/* handle flag inheritance */
/*TODO*///					if (!ROM_INHERITSFLAGS(&modified_romp))
/*TODO*///						lastflags = modified_romp._length & ROM_INHERITEDFLAGS;
/*TODO*///					else
/*TODO*///						modified_romp._length = (modified_romp._length & ~ROM_INHERITEDFLAGS) | lastflags;
/*TODO*///
/*TODO*///					explength += UNCOMPACT_LENGTH(modified_romp._length);
/*TODO*///
/*TODO*///                    /* attempt to read using the modified entry */
/*TODO*///					readresult = read_rom_data(romdata, &modified_romp);
/*TODO*///					if (readresult == -1)
/*TODO*///						goto fatalerror;
/*TODO*///				}
/*TODO*///				while (ROMENTRY_ISCONTINUE(romp));
/*TODO*///
/*TODO*///				/* if this was the first use of this file, verify the length and CRC */
/*TODO*///				if (baserom)
/*TODO*///				{
/*TODO*///					debugload("Verifying length (%X) and CRC (%08X)\n", explength, ROM_GETCRC(baserom));
/*TODO*///					verify_length_and_crc(romdata, ROM_GETNAME(baserom), explength, ROM_GETCRC(baserom));
/*TODO*///					debugload("Verify succeeded\n");
/*TODO*///				}
/*TODO*///
/*TODO*///				/* reseek to the start and clear the baserom so we don't reverify */
/*TODO*///				if (romdata->file)
/*TODO*///					osd_fseek(romdata->file, 0, SEEK_SET);
/*TODO*///				baserom = NULL;
/*TODO*///				explength = 0;
/*TODO*///			}
/*TODO*///			while (ROMENTRY_ISRELOAD(romp));
/*TODO*///
/*TODO*///			/* close the file */
/*TODO*///			if (romdata->file)
/*TODO*///			{
/*TODO*///				debugload("Closing ROM file\n");
/*TODO*///				osd_fclose(romdata->file);
/*TODO*///				romdata->file = NULL;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	return 1;
/*TODO*///
/*TODO*///	/* error case */
/*TODO*///fatalerror:
/*TODO*///	if (romdata->file)
/*TODO*///		osd_fclose(romdata->file);
/*TODO*///	romdata->file = NULL;
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	readroms - load all the ROMs for this machine
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///int readroms(void)
/*TODO*///{
/*TODO*///	return rom_load_new(Machine->gamedrv->rom);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	rom_load_new - new, more flexible ROM
/*TODO*///	loading system
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///int rom_load_new(const struct RomModule *romp)
/*TODO*///{
/*TODO*///	const struct RomModule *regionlist[REGION_MAX];
/*TODO*///	const struct RomModule *region;
/*TODO*///	static struct rom_load_data romdata;
/*TODO*///	int regnum;
/*TODO*///
/*TODO*///	/* reset the region list */
/*TODO*///	for (regnum = 0;regnum < REGION_MAX;regnum++)
/*TODO*///		regionlist[regnum] = NULL;
/*TODO*///
/*TODO*///	/* reset the romdata struct */
/*TODO*///	memset(&romdata, 0, sizeof(romdata));
/*TODO*///	romdata.romstotal = count_roms(romp);
/*TODO*///
/*TODO*///	/* loop until we hit the end */
/*TODO*///	for (region = romp, regnum = 0; region; region = rom_next_region(region), regnum++)
/*TODO*///	{
/*TODO*///		int regiontype = ROMREGION_GETTYPE(region);
/*TODO*///
/*TODO*///		debugload("Processing region %02X (length=%X)\n", regiontype, ROMREGION_GETLENGTH(region));
/*TODO*///
/*TODO*///		/* the first entry must be a region */
/*TODO*///		if (!ROMENTRY_ISREGION(region))
/*TODO*///		{
/*TODO*///			printf("Error: missing ROM_REGION header\n");
/*TODO*///			return 1;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* if sound is disabled and it's a sound-only region, skip it */
/*TODO*///		if (Machine->sample_rate == 0 && ROMREGION_ISSOUNDONLY(region))
/*TODO*///			continue;
/*TODO*///
/*TODO*///		/* allocate memory for the region */
/*TODO*///		if (new_memory_region(regiontype, ROMREGION_GETLENGTH(region), ROMREGION_GETFLAGS(region)))
/*TODO*///		{
/*TODO*///			printf("Error: unable to allocate memory for region %d\n", regiontype);
/*TODO*///			return 1;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* remember the base and length */
/*TODO*///		romdata.regionlength = memory_region_length(regiontype);
/*TODO*///		romdata.regionbase = memory_region(regiontype);
/*TODO*///		debugload("Allocated %X bytes @ %08X\n", romdata.regionlength, (int)romdata.regionbase);
/*TODO*///
/*TODO*///		/* clear the region if it's requested */
/*TODO*///		if (ROMREGION_ISERASE(region))
/*TODO*///			memset(romdata.regionbase, ROMREGION_GETERASEVAL(region), romdata.regionlength);
/*TODO*///
/*TODO*///		/* or if it's sufficiently small (<= 4MB) */
/*TODO*///		else if (romdata.regionlength <= 0x400000)
/*TODO*///			memset(romdata.regionbase, 0, romdata.regionlength);
/*TODO*///
/*TODO*///#ifdef MAME_DEBUG
/*TODO*///		/* if we're debugging, fill region with random data to catch errors */
/*TODO*///		else
/*TODO*///			fill_random(romdata.regionbase, romdata.regionlength);
/*TODO*///#endif
/*TODO*///
/*TODO*///		/* now process the entries in the region */
/*TODO*///		if (!process_rom_entries(&romdata, region + 1))
/*TODO*///			return 1;
/*TODO*///
/*TODO*///		/* add this region to the list */
/*TODO*///		if (regiontype < REGION_MAX)
/*TODO*///			regionlist[regiontype] = region;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* post-process the regions */
/*TODO*///	for (regnum = 0; regnum < REGION_MAX; regnum++)
/*TODO*///		if (regionlist[regnum])
/*TODO*///		{
/*TODO*///			debugload("Post-processing region %02X\n", regnum);
/*TODO*///			romdata.regionlength = memory_region_length(regnum);
/*TODO*///			romdata.regionbase = memory_region(regnum);
/*TODO*///			region_post_process(&romdata, regionlist[regnum]);
/*TODO*///		}
/*TODO*///
/*TODO*///	/* display the results and exit */
/*TODO*///	return display_rom_load_results(&romdata);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	printromlist - print list of ROMs
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void printromlist(const struct RomModule *romp,const char *basename)
/*TODO*///{
/*TODO*///	const struct RomModule *region, *rom, *chunk;
/*TODO*///
/*TODO*///	if (!romp) return;
/*TODO*///
/*TODO*///#ifdef MESS
/*TODO*///	if (!strcmp(basename,"nes")) return;
/*TODO*///#endif
/*TODO*///
/*TODO*///	printf("This is the list of the ROMs required for driver \"%s\".\n"
/*TODO*///			"Name              Size       Checksum\n",basename);
/*TODO*///
/*TODO*///	for (region = romp; region; region = rom_next_region(region))
/*TODO*///	{
/*TODO*///		for (rom = rom_first_file(region); rom; rom = rom_next_file(rom))
/*TODO*///		{
/*TODO*///			const char *name = ROM_GETNAME(rom);
/*TODO*///			int expchecksum = ROM_GETCRC(rom);
/*TODO*///			int length = 0;
/*TODO*///
/*TODO*///			for (chunk = rom_first_chunk(rom); chunk; chunk = rom_next_chunk(chunk))
/*TODO*///				length += ROM_GETLENGTH(chunk);
/*TODO*///
/*TODO*///			if (expchecksum)
/*TODO*///				printf("%-12s  %7d bytes  %08x\n",name,length,expchecksum);
/*TODO*///			else
/*TODO*///				printf("%-12s  %7d bytes  NO GOOD DUMP KNOWN\n",name,length);
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
}