/**
 * ported to v0.37b7
 *
 */
package mame;

import static mame.driverH.*;

import static mess.systems.coleco.*;
import static mess.systems.spectrum.*;

public class driver {

    public static GameDriver drivers[] = {
        
        driver_coleco,
        driver_colecoa,
        driver_spectrum,
        driver_spec128,
        driver_pentagon,
        null
    };
}
