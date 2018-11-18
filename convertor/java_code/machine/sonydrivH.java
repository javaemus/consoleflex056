/*
	sonydriv.h : interface file for sonydriv.c
*/

/* defines for the allowablesizes param below */
enum
{
	SONY_FLOPPY_ALLOW400K	= 1,
	SONY_FLOPPY_ALLOW800K	= 2,

	SONY_FLOPPY_EXT_SPEED_CONTROL = 0x8000	/* means the speed is controlled by computer */
};

int sony_floppy_init(int id, int allowablesizes);
void sony_floppy_exit(int id);

void sony_set_lines(int lines);
void sony_set_enable_lines(int enable_mask);
void sony_set_sel_line(int sel);
/*
void sony_set_speed(int speed);

void sony_write_data(int data);
