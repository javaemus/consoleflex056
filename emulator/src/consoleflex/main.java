package consoleflex;


import old.arcadeflex.osdepend;
import static old2.arcadeflex.libc_v2.*;

//import static mame.ui_textH.MESS;
/**
 *
 * @author shadow
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // selects MESS (menu, options and messages)
        //MESS = true;
        
        ConvertArguments("consoleflex", args);
        args = null;
        System.exit(osdepend.main(argc, argv));
    }

}
