#ifdef RUNTIME_LOADER
# ifdef __cplusplus
	extern "C" # else
	extern # endif
#endif


void studio2_video_dma(int cycles);

void studio2_vh_screenrefresh (struct mame_bitmap *bitmap, int full_refresh);
