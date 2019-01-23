package consoleflex;


import old.arcadeflex.osdepend;
import static old2.arcadeflex.libc_v2.*;


/**
 *
 * @author shadow
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ConvertArguments("consoleflex", args);
        args = null;
        System.exit(osdepend.main(argc, argv));
    }

}
