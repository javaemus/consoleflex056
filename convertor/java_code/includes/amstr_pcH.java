



#define AMSTRAD_HELPER(bit, text, key1, key2) \
	PORT_BITX( bit, 0x0000, IPT_KEYBOARD, text, key1, key2 );

#define AMSTRAD_KEYBOARD \
    PORT_START();   /* IN4 */\
	PORT_BIT ( 0x0001, 0x0000, IPT_UNUSED );	/* unused scancode 0 */\
	AMSTRAD_HELPER( 0x0002, "Esc",          KEYCODE_ESC,        IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0004, "1 !",          KEYCODE_1,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0008, "2 @",          KEYCODE_2,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0010, "3 #",          KEYCODE_3,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0020, "4 $",          KEYCODE_4,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0040, "5 %",          KEYCODE_5,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0080, "6 ^",          KEYCODE_6,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0100, "7 &",          KEYCODE_7,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0200, "8 *",          KEYCODE_8,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0400, "9 (",          KEYCODE_9,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0800, "0 )",          KEYCODE_0,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x1000, "- _",          KEYCODE_MINUS,      IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x2000, "= +",          KEYCODE_EQUALS,     IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x4000, "<--",          KEYCODE_BACKSPACE,  IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x8000, "Tab",          KEYCODE_TAB,        IP_JOY_NONE ) \
		\
	PORT_START(); 	/* IN5 */\
	AMSTRAD_HELPER( 0x0001, "Q",            KEYCODE_Q,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0002, "W",            KEYCODE_W,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0004, "E",            KEYCODE_E,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0008, "R",            KEYCODE_R,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0010, "T",            KEYCODE_T,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0020, "Y",            KEYCODE_Y,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0040, "U",            KEYCODE_U,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0080, "I",            KEYCODE_I,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0100, "O",            KEYCODE_O,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0200, "P",            KEYCODE_P,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0400, "[ {",          KEYCODE_OPENBRACE,  IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0800, "] }",          KEYCODE_CLOSEBRACE, IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x1000, "Enter",        KEYCODE_ENTER,      IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x2000, "Ctrl",			KEYCODE_LCONTROL,   IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x4000, "A",            KEYCODE_A,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x8000, "S",            KEYCODE_S,          IP_JOY_NONE ) \
		\
	PORT_START(); 	/* IN6 */\
	AMSTRAD_HELPER( 0x0001, "D",            KEYCODE_D,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0002, "F",            KEYCODE_F,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0004, "G",            KEYCODE_G,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0008, "H",            KEYCODE_H,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0010, "J",            KEYCODE_J,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0020, "K",            KEYCODE_K,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0040, "L",            KEYCODE_L,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0080, "; :",          KEYCODE_COLON,      IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0100, "' \"",         KEYCODE_QUOTE,      IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0200, "` ~",          KEYCODE_TILDE,      IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0400, "L-Shift",      KEYCODE_LSHIFT,     IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0800, "\\ |",         KEYCODE_BACKSLASH,  IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x1000, "Z",            KEYCODE_Z,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x2000, "X",            KEYCODE_X,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x4000, "C",            KEYCODE_C,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x8000, "V",            KEYCODE_V,          IP_JOY_NONE ) \
		\
	PORT_START(); 	/* IN7 */\
	AMSTRAD_HELPER( 0x0001, "B",            KEYCODE_B,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0002, "N",            KEYCODE_N,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0004, "M",            KEYCODE_M,          IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0008, ", <",          KEYCODE_COMMA,      IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0010, ". >",          KEYCODE_STOP,       IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0020, "/ ?",          KEYCODE_SLASH,      IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0040, "R-Shift",      KEYCODE_RSHIFT,     IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0080, "KP * (PrtScr)",KEYCODE_ASTERISK,   IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0100, "Alt",          KEYCODE_LALT,       IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0200, "Space",        KEYCODE_SPACE,      IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0400, "Caps",         KEYCODE_CAPSLOCK,   IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0800, "F1",           KEYCODE_F1,         IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x1000, "F2",           KEYCODE_F2,         IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x2000, "F3",           KEYCODE_F3,         IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x4000, "F4",           KEYCODE_F4,         IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x8000, "F5",           KEYCODE_F5,         IP_JOY_NONE ) \
		\
	PORT_START(); 	/* IN8 */\
	AMSTRAD_HELPER( 0x0001, "F6",           KEYCODE_F6,         IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0002, "F7",           KEYCODE_F7,         IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0004, "F8",           KEYCODE_F8,         IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0008, "F9",           KEYCODE_F9,         IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0010, "F10",          KEYCODE_F10,        IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0020, "NumLock",      KEYCODE_NUMLOCK,    IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0040, "ScrLock",      KEYCODE_SCRLOCK,    IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0080, "KP 7 (Home)",  KEYCODE_7_PAD,      KEYCODE_HOME )\
	AMSTRAD_HELPER( 0x0100, "KP 8 (Up)",    KEYCODE_8_PAD,      CODE_NONE )  \
	AMSTRAD_HELPER( 0x0200, "KP 9 (PgUp)",  KEYCODE_9_PAD,      KEYCODE_PGUP) \
	AMSTRAD_HELPER( 0x0400, "KP -",         KEYCODE_MINUS_PAD,  IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0800, "KP 4 (Left)",  KEYCODE_4_PAD,      CODE_NONE )\
	AMSTRAD_HELPER( 0x1000, "KP 5",         KEYCODE_5_PAD,      IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x2000, "KP 6 (Right)", KEYCODE_6_PAD,      CODE_NONE )\
	AMSTRAD_HELPER( 0x4000, "KP +",         KEYCODE_PLUS_PAD,   IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x8000, "KP 1 (End)",   KEYCODE_1_PAD,      KEYCODE_END ) \
		\
	PORT_START(); 	/* IN9 key codes 0x50 */\
	AMSTRAD_HELPER( 0x0001, "KP 2 (Down)",  KEYCODE_2_PAD,      CODE_NONE ) \
	AMSTRAD_HELPER( 0x0002, "KP 3 (PgDn)",  KEYCODE_3_PAD,      KEYCODE_PGDN ) \
	AMSTRAD_HELPER( 0x0004, "KP 0 (Ins)",   KEYCODE_0_PAD,      KEYCODE_INSERT ) \
	AMSTRAD_HELPER( 0x0008, "KP . (Del)",   KEYCODE_DEL_PAD,    KEYCODE_DEL ) \
	PORT_BIT ( 0x0030, 0x0000, IPT_UNUSED );
	AMSTRAD_HELPER( 0x0040, "?(84/102)\\",	KEYCODE_BACKSLASH2,	IP_JOY_NONE ) \
		\
	PORT_BIT ( 0xff80, 0x0000, IPT_UNUSED );
		\
	PORT_START(); 	/* IN10 */\
	PORT_BIT ( 0xffff, 0x0000, IPT_UNUSED );
		\
	PORT_START(); 	/* IN11 key codes 0x70 */\
	PORT_BIT ( 0x806e, 0x0000, IPT_UNUSED );
	AMSTRAD_HELPER( 0x0001, "-.",			CODE_DEFAULT,    IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0010,	"?Enter",		CODE_DEFAULT,    IP_JOY_NONE ) \
	AMSTRAD_HELPER( 0x0100,	"Amstrad Joystick Button 1",KEYCODE_RALT, CODE_NONE ) \
	AMSTRAD_HELPER( 0x0080,	"Amstrad Joystick Button 2",KEYCODE_RCONTROL, CODE_NONE ) \
	AMSTRAD_HELPER( 0x0200,	"Amstrad Joystick Right",	KEYCODE_RIGHT, CODE_NONE ) \
	AMSTRAD_HELPER( 0x0400,	"Amstrad Joystick Left",	KEYCODE_LEFT, CODE_NONE ) \
	AMSTRAD_HELPER( 0x0800,	"Amstrad Joystick Down",	KEYCODE_DOWN, CODE_NONE ) \
	AMSTRAD_HELPER( 0x1000,	"Amstrad Joystick Up",		KEYCODE_UP, CODE_NONE ) \
	AMSTRAD_HELPER( 0x4000,	"Amstrad Mouse Button left",	KEYCODE_F11, CODE_NONE ) \
	AMSTRAD_HELPER( 0x2000,	"Amstrad Mouse Button right",	KEYCODE_F12, CODE_NONE ) \
	PORT_START(); 	/* IN12 */\
	PORT_BIT ( 0xffff, 0x0000, IPT_UNUSED );
	PORT_START();  /* IN13 Mouse - X AXIS */ \
	PORT_ANALOGX( 0xff, 0x00, IPT_TRACKBALL_X | IPF_REVERSE|IPF_PLAYER1, 100, 0, 0, 0, \
				  CODE_DEFAULT, CODE_DEFAULT, IP_JOY_NONE, IP_JOY_NONE );\
	PORT_START();  /* IN14 Mouse - Y AXIS */ \
	PORT_ANALOGX( 0xff, 0x00, IPT_TRACKBALL_Y | IPF_PLAYER1, 100, 0, 0, 0, \
				  CODE_DEFAULT, CODE_DEFAULT, IP_JOY_NONE, IP_JOY_NONE );
