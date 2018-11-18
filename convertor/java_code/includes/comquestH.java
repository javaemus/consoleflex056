#ifdef RUNTIME_LOADER
# ifdef __cplusplus
	extern "C" # else
	extern # endif
#endif

void comquest_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh);
