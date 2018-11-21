/*
 * ported to v0.58
 * ported to v0.37b7
 * ported to v0.36
 *
 */
package WIP.sound;

import static WIP.sound.k007232.*;
import WIP.arcadeflex.libc_v2.*;

public class kdacApcm {

    public kdacApcm() {
        vol[0] = new int[2];
        vol[1] = new int[2];
    }
    public /*unsigned char*/ int[][] vol = new int[KDAC_A_PCM_MAX][];/* volume for the left and right channel */

    public /*unsigned int*/ long[] addr = new long[KDAC_A_PCM_MAX];
    public /*unsigned int*/ long[] start = new long[KDAC_A_PCM_MAX];
    public /*unsigned int*/ long[] step = new long[KDAC_A_PCM_MAX];
    public int[] play = new int[KDAC_A_PCM_MAX];
    public int[] loop = new int[KDAC_A_PCM_MAX];
    public /*unsigned char*/ int[] wreg = new int[0x10];/* write data */

    public UBytePtr[] pcmbuf = new UBytePtr[2];/* Channel A & B pointers */


}