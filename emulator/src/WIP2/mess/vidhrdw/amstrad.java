/***************************************************************************

  amstrad.c.c

  Functions to emulate the video hardware of the amstrad CPC.

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package WIP2.mess.vidhrdw;
import static common.ptr.*;
import static WIP2.mess.vidhrdw.crtc6845.*;
import static WIP2.mess.vidhrdw.m6845H.*;
import static WIP2.mess.vidhrdw.m6845.*;
import static WIP.mame.osdependH.*;
import static WIP2.mess.machine.amstrad.*;
import static WIP2.mess.includes.amstradH.*;
import static WIP.arcadeflex.fucPtr.*;
import static WIP.arcadeflex.libc_v2.*;
import static WIP2.mess.systems.amstrad.*;
import static old.mame.cpuintrf.*;
import static old.mame.drawgfxH.*;
import static WIP.mame.drawgfx.*;
import static WIP.mame.common.*;
import static old.arcadeflex.video.*;

import static WIP2.mess.eventlstH.*;
import static WIP2.mess.eventlst.*;

import static WIP.mame.mame.*;

public class amstrad
{
	
	/* CRTC emulation code */
	
	static crtc6845_state amstrad_vidhrdw_6845_state;
	//#ifdef AMSTRAD_VIDEO_EVENT_LIST
	//static int amstrad_rendering;
        static int draw_function = 0;
	//#endif
	
	//#ifdef AMSTRAD_VIDEO_EVENT_LIST
	/* event list for storing colour changes, mode changes and CRTC writes */
	//#endif
	
	/***************************************************************************
	  Start the video hardware emulation.
	***************************************************************************/
	
	/* this contains the colours in Machine.pens form.*/
	/* this is updated from the eventlist and reflects the current state
	of the render colours - these may be different to the current colour palette values */
	/* colours can be changed at any time and will take effect immediatly */
	static int[] amstrad_render_colours=new int[17];
	
	//#ifndef AMSTRAD_VIDEO_EVENT_LIST
	static mame_bitmap	amstrad_bitmap;
	//#endif
	
	/* the mode is re-loaded at each HSYNC */
	/* current mode to render */
	static int amstrad_render_mode;
	
	static int amstrad_vsync;
	
	/* current programmed mode */
	static int amstrad_current_mode;
	
	static int[] Mode0Lookup=new int[256];
	static int[] Mode1Lookup=new int[256];
	static int[] Mode3Lookup=new int[256];
	
	static void amstrad_init_lookups()
	{
		int i;
	
		for (i=0; i<256; i++)
		{
			int pen;
	
			pen = (
				((i & (1<<7))>>(7-0)) |
				((i & (1<<3))>>(3-1)) |
				((i & (1<<5))>>(5-2)) |
	                        ((i & (1<<1))<<2)
				);
	
			Mode0Lookup[i] = pen;
	
			Mode3Lookup[i] = pen & 0x03;
	
			pen = (
				( ( (i & (1<<7)) >>7) <<0) |
			        ( ( (i & (1<<3)) >>3) <<1)
				);
	
			Mode1Lookup[i] = pen;
	
		}
	}
	
	//extern UBytePtr Amstrad_Memory;
	
	static int x_screen_offset=0;
	
	/* there are about 21 lines of monitor retrace */
	static int y_screen_offset=-21;
	
	static int amstrad_HSync=0;
	public static int amstrad_VSync=0;
	static int amstrad_Character_Row=0;
	static int amstrad_DE=0;
	
	
	//static UBytePtr amstrad_Video_RAM;
	static UBytePtr amstrad_display;
	//static struct osd_bitmap *amstrad_bitmap;
	
	static int x_screen_pos;
	static int y_screen_pos;
	
	//static void (*draw_function)(void);
	
	static void amstrad_draw_screen_enabled()
	{
		int sc1;
		int ma, ra;
		int addr;
		int byte1, byte2;
	
		sc1 = 0;
		ma = crtc6845_memory_address_r(0);
		ra = crtc6845_row_address_r(0);
	
		/* calc mem addr to fetch data from
		based on ma, and ra */
		addr = (((ma>>(4+8)) & 0x03)<<14) |
				((ra & 0x07)<<11) |
				((ma & 0x03ff)<<1);
	
		/* amstrad fetches two bytes per CRTC clock. */
		byte1 = Amstrad_Memory.read(addr);
		byte2 = Amstrad_Memory.read(addr+1);
	
	    /* depending on the mode! */
		switch (amstrad_render_mode)		
		{
	    
			/* mode 0 - low resolution - 16 colours */
			case 0:
			{
				int cpcpen,messpen;
				int data;
	
				data = byte1;
	
				{
					cpcpen = Mode0Lookup[data];
					messpen = amstrad_render_colours[cpcpen];
	
					amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
	
					data = data<<1;
	
					cpcpen = Mode0Lookup[data];
					messpen = amstrad_render_colours[cpcpen];
				
					amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
	                
				}
	
				data = byte2;
	
				{
					cpcpen = Mode0Lookup[data];
					messpen = amstrad_render_colours[cpcpen];
					amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
	                
	
					data = data<<1;
	
					cpcpen = Mode0Lookup[data];
					messpen = amstrad_render_colours[cpcpen];
					amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
	                
	                	}
			}
			break;
	
	        /* mode 1 - medium resolution - 4 colours */
			case 1:
			{
	                        int i;
	                        int cpcpen;
	                        int messpen;
	                        int data;
	
	                        data = byte1;
	
	                        for (i=0; i<4; i++)
	                        {
					cpcpen = Mode1Lookup[data & 0x0ff];
					messpen = amstrad_render_colours[cpcpen];
                                        amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
	                
					data = data<<1;
				}
	
	                        data = byte2;
	
	                        for (i=0; i<4; i++)
				{
					cpcpen = Mode1Lookup[data & 0x0ff];
					messpen = amstrad_render_colours[cpcpen];
					amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
	                
					data = data<<1;
				}
	
			}
			break;
	
			/* mode 2: high resolution - 2 colours */
			case 2:
			{
				int i;
				int Data = (byte1<<8) | byte2;
				int cpcpen,messpen;
	
				for (i=0; i<16; i++)
				{
					cpcpen = (Data>>15) & 0x01;
					messpen = amstrad_render_colours[cpcpen];
					amstrad_display.write(sc1, messpen);
					sc1++;
	                
					Data = Data<<1;
	
				}
	
			}
			break;
	
			/* undocumented mode. low resolution - 4 colours */
			case 3:
			{
				int cpcpen,messpen;
				int data;
	
				data = byte1;
	
				{
					cpcpen = Mode3Lookup[data];
					messpen = amstrad_render_colours[cpcpen];
	
					amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
	
					data = data<<1;
	
					cpcpen = Mode3Lookup[data];
					messpen = amstrad_render_colours[cpcpen];
				
					amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
	                
				}
	
				data = byte2;
	
				{
					cpcpen = Mode3Lookup[data];
					messpen = amstrad_render_colours[cpcpen];
					amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
	                
	
					data = data<<1;
	
					cpcpen = Mode3Lookup[data];
					messpen = amstrad_render_colours[cpcpen];
					amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
                                        amstrad_display.write(sc1, messpen);
					sc1++;
	                
	                	}
	
	
			}
			break;
	
			default:
				break;
		}
	
	
	}
	
	static void amstrad_draw_screen_disabled()
	{
		int sc1;
		int border_colour;
	
		border_colour = amstrad_render_colours[16];	
	
		/* if the display is not enable, draw border colour */
		for(sc1=0;sc1<16;sc1++)
		{
			amstrad_display.write(sc1, border_colour);
		}
	}
	
	/* Select the Function to draw the screen area */
	public static void amstrad_Set_VideoULA_DE()
	{
		if (amstrad_DE != 0)
		{
			//draw_function=*amstrad_draw_screen_enabled;
                        draw_function=1;
		} 
		else 
		{
			//draw_function=*amstrad_draw_screen_disabled;
                        draw_function=0;
		}
	}
	
	
	/************************************************************************
	 * amstrad 6845 Outputs to Video ULA
	 ************************************************************************/
	
	// called when the 6845 changes the character row
	public static void amstrad_Set_Character_Row(int offset, int data)
	{            
		amstrad_Character_Row=data;
		amstrad_Set_VideoULA_DE();
	}
	
	/* the horizontal screen position on the display is determined
	by the length of the HSYNC and the position of the hsync */
	public static void amstrad_Set_HSync(int offset, int data)
	{
	//	if (amstrad_rendering != 0)
	//	{
	
			/* hsync changed state? */
			if ((amstrad_HSync^data)!=0)
			{
	
					if (data!=0)
					{
									/* start of hsync */
	//#ifndef AMSTRAD_VIDEO_EVENT_LIST
	                amstrad_interrupt_timer_update();
						
	//#endif
						/* set new render mode */
						amstrad_render_mode = amstrad_current_mode;
					}
						else
						{
								/* end of hsync */
								y_screen_pos+=1;
	
								if (y_screen_pos>312)
								{
										y_screen_pos = 0;
								}
	
								if ((y_screen_pos>=0) && (y_screen_pos<AMSTRAD_SCREEN_HEIGHT))
								{
										x_screen_pos=x_screen_offset;
										amstrad_display=new UBytePtr(amstrad_bitmap.line[y_screen_pos], x_screen_pos);
								}
						}
			}
	//	}
	
	
		amstrad_HSync=data;
	}
	
	public static void amstrad_Set_VSync(int offset, int data)
	{
	
	        amstrad_vsync = data;
	
	//        logerror("%d\r\n",amstrad_vsync);
	
	    /* vsync changed state? */
	    if ((amstrad_VSync^data)!=0)
		{
	        if (data!=0)
	        {
		//		if (amstrad_rendering != 0)
		//		{
					y_screen_pos=y_screen_offset;
	
				   if ((y_screen_pos>=0) && (y_screen_pos<=AMSTRAD_SCREEN_HEIGHT))
				   {
						amstrad_display=new UBytePtr(amstrad_bitmap.line[y_screen_pos], x_screen_pos);
					}
		//		}
		//		else
		//		{
				   /* setup interrupt counter reset */
	//				 amstrad_interrupt_timer_trigger_reset_by_vsync();
		//		}
			}
	   }
	    
		
		amstrad_VSync=data;
	
	}
	
	// called when the 6845 changes the Display Enabled
	public static void amstrad_Set_DE(int offset, int data)
	{
		amstrad_DE=data;
		amstrad_Set_VideoULA_DE();
	}
	
	
	/* The cursor is not used on Amstrad. The CURSOR signal is available on the Expansion port
	for other hardware to use, but as far as I know it is not used by anything. */
	
	/* use this when rendering */
	
	public static crtc6845_interface amstrad6845= new crtc6845_interface(){
            @Override
            public void out_MA_func(int offset, int data) {
                // 0,// Memory Address register
            }

            @Override
            public void out_RA_func(int offset, int data) {
                //	amstrad_Set_Character_Row,// Row Address register
                amstrad_Set_Character_Row(offset, data);
            }

            @Override
            public void out_HS_func(int offset, int data) {
                //	amstrad_Set_HSync,// Horizontal status
                amstrad_Set_HSync(offset, data);
            }

            @Override
            public void out_VS_func(int offset, int data) {
                // amstrad_Set_VSync,// Vertical status
                amstrad_Set_VSync(offset, data);
            }

            @Override
            public void out_DE_func(int offset, int data) {
                //	amstrad_Set_DE,// Display Enabled status
                amstrad_Set_DE(offset, data);
            }

            @Override
            public void out_CR_func(int offset, int data) {
                //	null,// Cursor status
            }
    
        };
	
	/* update the amstrad colours */
	public static void amstrad_vh_update_colour(int PenIndex, int hw_colour_index)
	{
		amstrad_render_colours[PenIndex] = Machine.pens[hw_colour_index];
	}
	
	/* update mode */
	public static void amstrad_vh_update_mode(int Mode)
	{
            System.out.println("Mode: "+Mode);
		amstrad_current_mode = Mode;
	}
        
        static void draw_function(){
            //System.out.println("draw_function "+draw_function);
            if (draw_function == 0){
                amstrad_draw_screen_disabled();
            } else {
                amstrad_draw_screen_enabled();
            }
        }
	
	/* execute crtc_execute_cycles of crtc */
	public static void amstrad_vh_execute_crtc_cycles(int crtc_execute_cycles)
	{
	    while (crtc_execute_cycles>0)
		{
			/* check that we are on the emulated screen area. */
			if ((x_screen_pos>=0) && (x_screen_pos<AMSTRAD_SCREEN_WIDTH) && (y_screen_pos>=0) && (y_screen_pos<AMSTRAD_SCREEN_HEIGHT))
			{
				/* render the screen */
				draw_function();
			}
	
	        /* Move the CRT Beam on one 6845 character distance */
	        x_screen_pos=x_screen_pos+16; 
	        
	/*		if (x_screen_pos>800)
			{
				x_screen_pos = 0;
			}
	*/
			amstrad_display=new UBytePtr(amstrad_display, 16); 
	
	
			/* Clock the 6845 */
			crtc6845_clock();
			crtc_execute_cycles--;
		}
	}
	
	/************************************************************************
	 * amstrad_vh_screenrefresh
	 * resfresh the amstrad video screen
	 ************************************************************************/
	
	public static VhUpdatePtr amstrad_vh_screenrefresh = new VhUpdatePtr() { public void handler(mame_bitmap bitmap,int full_refresh) 
	{
	//#ifndef AMSTRAD_VIDEO_EVENT_LIST
		rectangle rect=new rectangle();
	
		rect.min_x = 0;
		rect.max_x = AMSTRAD_SCREEN_WIDTH;
		rect.min_y = 0;
		rect.max_y = AMSTRAD_SCREEN_HEIGHT;
	
                old.mame.drawgfx.copybitmap(bitmap, amstrad_bitmap, 0,0,0,0,rect, TRANSPARENCY_NONE,0);
		
		
	//#else
	/*	int c;
	
	
		int crtc_execute_cycles;
		int num_cycles_remaining;
		EVENT_LIST_ITEM pItem;
		int NumItemsRemaining;
		int previous_time;
	
		amstrad_rendering = 1;
		crtc6845_set_state(0, amstrad_vidhrdw_6845_state);
	
		previous_time = 0;
	        num_cycles_remaining = cpu_getcurrentcycles()>>2;	//get19968; //cpu_getfperiod();
	
		amstrad_bitmap=bitmap;
		amstrad_display = amstrad_bitmap.line[0];
		c=0;*/
	
		// video_refresh is set if any of the 6845 or Video ULA registers are changed
		// this then forces a full screen redraw
	
	
	        /*pItem = EventList_GetFirstItem();
	        NumItemsRemaining = EventList_NumEvents();
	
		do
		{
	                if (NumItemsRemaining==0)
			{
				crtc_execute_cycles = num_cycles_remaining;
				num_cycles_remaining = 0;
			}
			else
			{
				int time_delta;
	*/
				/* calculate time between last event and this event */
				/*time_delta = pItem.Event_Time - previous_time;
			
				crtc_execute_cycles = time_delta/4;
	
				num_cycles_remaining -= time_delta/4;
	
	        }
	
			amstrad_vh_execute_crtc_cycles(crtc_execute_cycles);
	
			if (NumItemsRemaining!=0)
			{
				switch ((pItem.Event_ID>>6) & 0x03)
				{
					case EVENT_LIST_CODE_GA_COLOUR:
					{
						int PenIndex = pItem.Event_ID & 0x03f;
						int Colour = pItem.Event_Data;
	
						amstrad_vh_update_colour(PenIndex, Colour);
					}
					break;
	
					case EVENT_LIST_CODE_GA_MODE:
					{
						amstrad_vh_update_mode(pItem.Event_Data);
					}
					break;
	
					case EVENT_LIST_CODE_CRTC_INDEX_WRITE:
					{*/
						/* register select */
					/*	crtc6845_address_w(0,pItem.Event_Data);
					}
					break;
	
					case EVENT_LIST_CODE_CRTC_WRITE:
					{
						crtc6845_register_w(0, pItem.Event_Data);
					}
					break;
	
					default:
						break;
				}*/
			
				/* store time for next calculation */
				/*previous_time = pItem.Event_Time;
				
                                pItem=EventList_GetNextItem();
				NumItemsRemaining--;		
			}
		}
		while (num_cycles_remaining>0);
	*/
	    /* Assume all other routines have processed their data from the list */
	/*    EventList_Reset();
	    EventList_SetOffsetStartTime ( cpu_getcurrentcycles() );
	
		crtc6845_get_state(0, amstrad_vidhrdw_6845_state);
		amstrad_rendering = 0;*/
	//#endif
	} };
	
	
	/************************************************************************
	 * amstrad_vh_start
	 * Initialize the amstrad video emulation
	 ************************************************************************/
	
	public static VhStartPtr amstrad_vh_start = new VhStartPtr() { public int handler() 
	{
	        int i;
	
		amstrad_init_lookups();
	
		crtc6845_start();
		crtc6845_config(amstrad6845);
		crtc6845_reset(0);
		crtc6845_get_state(0, amstrad_vidhrdw_6845_state);
		
		//draw_function=*amstrad_draw_screen_disabled;
                draw_function = 0;
	
	
		/* 64 us Per Line, 312 lines (PAL) = 19968 */
	    amstrad_render_mode = 0;
	    amstrad_current_mode = 0;
	    for (i=0; i<17; i++)
	    {
			amstrad_vh_update_colour(i, 0x014);
	    }
	
	//#ifdef AMSTRAD_VIDEO_EVENT_LIST
		//amstrad_rendering = 0;
		//EventList_Initialise(19968);
	//#else
		amstrad_bitmap = osd_alloc_bitmap(AMSTRAD_SCREEN_WIDTH, AMSTRAD_SCREEN_HEIGHT,8);
		amstrad_display = amstrad_bitmap.line[0];
	//#endif
	
		return 0;
	
	} };
	
	/************************************************************************
	 * amstrad_vh_stop
	 * Shutdown the amstrad video emulation
	 ************************************************************************/
	
	public static VhStopPtr amstrad_vh_stop = new VhStopPtr() { public void handler() 
	{
		crtc6845_stop();
	//#ifdef AMSTRAD_VIDEO_EVENT_LIST
		//EventList_Finish();
	//#else
		if (amstrad_bitmap != null)
		{
			osd_free_bitmap(amstrad_bitmap);
			amstrad_bitmap = null;
		}
	//#endif
	
	} };
}
