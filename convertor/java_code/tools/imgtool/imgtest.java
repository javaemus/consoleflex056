/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package tools.imgtool;

public class imgtest
{
	
	#ifdef MAME_DEBUG
	
	static const char *testimage = "test.img";
	/* static const char *testfile = "test.txt"; */
	
	/* ----------------------------------------------------------------------- */
	
	static void *create_buffer(size_t len)
	{
		char *buf;
		size_t testlen, i;
		static char *teststring = "All your base are belong to us";
	
		testlen = strlen(teststring);
		buf = malloc(len);
		if (!buf)
			return NULL;
	
		for (i = 0; i < len; i++) {
			buf[i] = teststring[i % testlen];
		}
		return buf;
	}
	
	static int get_freespace(const struct ImageModule *module, const char *imagename, int *freespace)
	{
		int err;
		IMAGE *img = NULL;
	
		err = img_open(module, imagename, OSD_FOPEN_RW, &img);
		if (err != 0)
			goto done;
	
		err = img_freespace(img, freespace);
		if (err != 0)
			goto done;
	
	done:
		if (img != 0)
			img_close(img);
		return err;
	}
	
	static int create_file_in_image(const struct ImageModule *module, const char *imagename, const char *fname, void *testbuf, size_t filesize)
	{
		int err;
		IMAGE *img = NULL;
		STREAM *s = NULL;
	
		/* Create a test stream */
		s = stream_open_mem(testbuf, filesize);
		if (!s) {
			err = IMGTOOLERR_OUTOFMEMORY;
			goto done;
		}
	
		err = img_open(module, imagename, OSD_FOPEN_RW, &img);
		if (err != 0)
			goto done;
	
		err = img_writefile(img, fname, s, NULL, NULL);
		if (err != 0)
			goto done;
	
	done:
		if (img != 0)
			img_close(img);
		return err;
	}
	
	static int delete_file_in_image(const struct ImageModule *module, const char *imagename, const char *fname)
	{
		int err;
		IMAGE *img = NULL;
	
		err = img_open(module, imagename, OSD_FOPEN_RW, &img);
		if (err != 0)
			goto done;
	
		err = img_deletefile(img, fname);
		if (err != 0)
			goto done;
	
	done:
		if (img != 0)
			img_close(img);
		return err;
	}
	
	static int count_files(const struct ImageModule *module, const char *imagename, int *totalfiles)
	{
		int err;
		IMAGE *img = NULL;
	
		err = img_open(module, imagename, OSD_FOPEN_RW, &img);
		if (err != 0)
			goto done;
	
		err = img_countfiles(img, totalfiles);
		if (err != 0)
			goto done;
	
	done:
		if (img != 0)
			img_close(img);
		return err;
	}
	
	static int assert_file_count(const struct ImageModule *module, const char *imagename, int filecount)
	{
		int err;
		int totalfiles;
	
		err = count_files(module, imagename, &totalfiles);
		if (err != 0)
			return err;
	
		assert(filecount == totalfiles);
		return 0;
	}
	
	static int assert_file_size(const struct ImageModule *module, const char *imagename, const char *fname, int assertedsize)
	{
		int filesize;
		int err;
		IMAGE *img = NULL;
	
		err = img_open(module, imagename, OSD_FOPEN_RW, &img);
		if (err != 0)
			goto done;
	
		err = img_filesize(img, fname, &filesize);
		if (err != 0)
			goto done;
	
		assert(filesize == assertedsize);
	
	done:
		if (img != 0)
			img_close(img);
		return err;
	}
	
	#define NUM_FILES	10
	
	static int calculate_filesize(int i)
	{
		return 5*(i+1)*(i+1)*(i+1)*(i+1);
	}
	
	static int internal_test(const struct ImageModule *module)
	{
		int err, i;
		const struct OptionTemplate *createopts;
		char *s;
		int freespace, freespace2, filesize;
		void *testbuf;
		struct NamedOption nopts[32];
		char buf[2048];
	
		/* Create the buffer */
		testbuf = create_buffer(calculate_filesize(NUM_FILES-1));
		if (!testbuf) {
			err = IMGTOOLERR_OUTOFMEMORY;
			goto done;
		}
	
		/* Create the image */
		memset(nopts, 0, sizeof(nopts));
		s = buf;
		createopts = module.createoptions_template;
		for (i = 0; createopts[i].name; i++) {
			assert(i < (sizeof(nopts) / sizeof(nopts[0]))-1);
			assert(createopts[i].flags == IMGOPTION_FLAG_TYPE_INTEGER);
	
			sprintf(s, "%i", createopts[i].min);
			nopts[i].name = createopts[i].name;
			nopts[i].value = s;
			s += strlen(s) + 1;
		}
		err = img_create(module, testimage, nopts);
		if (err != 0)
			goto done;
	
		/* Calculate free space */
		err = get_freespace(module, testimage, &freespace);
		if (err != 0)
			return err;
	
		/* Add files */
		for (i = 0; i < NUM_FILES; i++) {
			/* Must be n files */
			err = assert_file_count(module, testimage, i);
			if (err != 0)
				goto done;
	
			/* Put a file on the image */
			sprintf(buf, "test%i.txt", i);
			filesize = calculate_filesize(i);
			err = create_file_in_image(module, testimage, buf, testbuf, filesize);
			if (err != 0)
				goto done;
	
			/* Must be n+1 files */
			err = assert_file_count(module, testimage, i+1);
			if (err != 0)
				goto done;
	
			/* Verify the file size */
			err = assert_file_size(module, testimage, buf, filesize);
			if (err != 0)
				goto done;
		}
	
		/* Delete files */
		for (i = NUM_FILES-1; i >= 0; i--) {
			/* Must be n+1 files */
			err = assert_file_count(module, testimage, i+1);
			if (err != 0)
				goto done;
	
			/* Delete a file on the image */
			sprintf(buf, "test%i.txt", i);
			err = delete_file_in_image(module, testimage, buf);
			if (err != 0)
				goto done;
	
			/* Must be n files */
			err = assert_file_count(module, testimage, i);
			if (err != 0)
				goto done;
		}
	
		/* Verify that we freed all allocated blocks */
		err = get_freespace(module, testimage, &freespace2);
		if (err != 0)
			goto done;
		assert(freespace == freespace2);
	
	done:
		if (testbuf != 0)
			free(testbuf);
		return err;
	}
	
	/* ----------------------------------------------------------------------- */
	
	int imgtool_test(const struct ImageModule *module)
	{
		int err, i;
		size_t allmodules_count;
		const struct ImageModule **allmodules;
	
		if (module != 0) {
			err = internal_test(module);
			if (err != 0)
				return err;
		}
		else {
			allmodules = getmodules(&allmodules_count);
			for (i = 0; i < allmodules_count; i++) {
				err = internal_test(allmodules[i]);
				if (err != 0)
					return err;
			}
		}
		return 0;
	}
	
	int imgtool_test_byname(const char *modulename)
	{
		const struct ImageModule *module;
	
		if (modulename != 0) {
			module = findimagemodule(modulename);
			if (!module)
				return IMGTOOLERR_MODULENOTFOUND | IMGTOOLERR_SRC_MODULE;
		}
		else {
			module = NULL;
		}
		return imgtool_test(module);
	}
	
	
	#endif /* MAME_DEBUG */
}
