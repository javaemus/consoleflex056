/**
 * changelog
 * 28/08/2017 - Added ShortPtr(short[] m) constructor
 */
package WIP.arcadeflex;

import static common.ptr.*;

/**
 * @author shadow
 */
public class libc_v2 {


    public static int argc;
    public static String[] argv;

    /*
     *  Convert command-line parameters
     */
    public static void ConvertArguments(String mainClass, String[] arguments) {
        argc = arguments.length + 1;
        argv = new String[argc];
        argv[0] = mainClass;
        for (int i = 1; i < argc; i++) {
            argv[i] = arguments[i - 1];
        }
    }


    /**
     * function equals to c memcmp function
     */
   


    /**
     * ***********************************
     * <p>
     * Unsigned Byte Pointer Emulation ***********************************
     */
    public static class UBytePtr {

        public int bsize = 1;
        public char[] memory;
        public int offset;

        public UBytePtr() {
        }

        public UBytePtr(int size) {
            memory = new char[size];
            offset = 0;
        }

        public UBytePtr(char[] m) {
            set(m, 0);
        }

        public UBytePtr(char[] m, int b) {
            set(m, b);
        }

        public UBytePtr(UBytePtr cp, int b) {
            set(cp.memory, cp.offset + b);
        }

        public UBytePtr(UBytePtr cp) {
            set(cp.memory, cp.offset);
        }

        public void set(char[] m, int b) {
            memory = m;
            offset = b;
        }

        public void set(char[] m) {
            memory = m;
            offset = 0;
        }

        public void set(UBytePtr cp, int b) {
            set(cp.memory, cp.offset + b);
        }

        public void inc() {
            offset += bsize;
        }

        public void dec() {
            offset -= bsize;
        }

        public void inc(int count) {
            offset += count * bsize;
        }

        public void dec(int count) {
            offset -= count * bsize;
        }

        public char read() {
            return (char) (memory[offset] & 0xFF);
        }

        public int READ_WORD(int index) {
            //return (char)(((memory[offset + 1 + index] << 8)& 0xFF) | (memory[offset + index]& 0xFF));
            /*return (char)((( memory[offset+index]&0xFF) << 0)
             | (( memory[offset+index + 1]&0xFF) << 8));*/
            return memory[offset + index + 1] << 8 | memory[offset + index];

        }

        public int READ_DWORD(int index)//unchecked!
        {
            /*return( ((memory[offset + 3 + index] << 24)& 0xFF)
             | ((memory[offset + 2 + index] << 16)& 0xFF)
             | ((memory[offset + 1 + index] << 8)& 0xFF)
             | ((memory[offset + index]& 0xFF)));*/
            int myNumber = ((memory[offset + index] & 0xFF) << 0)
                    | ((memory[offset + index + 1] & 0xFF) << 8)
                    | ((memory[offset + index + 2] & 0xFF) << 16)
                    | ((memory[offset + index + 3] & 0xFF) << 24);
            return myNumber;
        }

        public char read(int index) {
            //if(offset+index>memory.length-1)
            //    return 0;
            //System.out.println("libc_v2 offset: "+offset);
            //System.out.println("libc_v2 offset: "+index);
            //System.out.println("libc_v2 memory: "+memory.length);

            try {
                return (char) (memory[offset + index] & 0xFF); //return only the first 8bits
            } catch (Exception e) {
                System.out.println("libc_v2 offset: " + offset);
                System.out.println("libc_v2 index: " + index);
                System.out.println("libc_v2 memory: " + memory.length);
                e.printStackTrace(System.out);
            }
            return memory[0];
        }

        public char readinc() {
            return (char) ((memory[(this.offset++)]) & 0xFF);
        }

        public char readdec() {
            return (char) ((memory[(this.offset--)]) & 0xFF);
        }

        public void WRITE_WORD(int index, int value) {
            memory[offset + index + 1] = (char) (value >> 8 & 0xFF);
            memory[offset + index] = (char) (value & 0xFF);
            //memory[offset + index] = (char)(value & 0xFF);
            //memory[offset + index+ 1] = (char)((value >> 8)&0xFF);
        }

        public void write(int index, int value) {
            memory[offset + index] = (char) (value & 0xFF);//store 8 bits only             
        }

        public void write(int value) {
            memory[offset] = (char) (value & 0xFF);//store 8 bits only
        }

        public void writeinc(int value) {
            this.memory[(this.offset++)] = (char) (value & 0xFF);//store 8 bits only
        }

        public void writedec(int value) {
            this.memory[(this.offset--)] = (char) (value & 0xFF);//store 8 bits only
        }

        public void or(int value) {
            int tempbase = this.offset;
            char[] tempmemory = this.memory;
            tempmemory[tempbase] = (char) ((tempmemory[tempbase] | (char) value) & 0xFF);
        }

        public void or(int index, int value) {
            int tempbase = this.offset + index;
            char[] tempmemory = this.memory;
            tempmemory[tempbase] = (char) ((tempmemory[tempbase] | (char) value) & 0xFF);
        }
    }



    /**
     * Convert a char array to an unsigned integer
     *
     * @param b
     * @return
     */
    public static long charArrayToLong(char[] b) {
        int start = 0;
        int i = 0;
        int len = 4;
        int cnt = 0;
        char[] tmp = new char[len];
        for (i = start; i < (start + len); i++) {
            tmp[cnt] = b[i];
            cnt++;
        }
        long accum = 0;
        i = 0;
        for (int shiftBy = 0; shiftBy < 32; shiftBy += 8) {
            accum |= ((long) (tmp[i] & 0xff)) << shiftBy;
            i++;
        }
        return accum;
    }

    /**
     * Convert a char array to a unsigned short
     *
     * @param b
     * @return
     */
    public static int charArrayToInt(char[] b) {
        int start = 0;
        int low = b[start] & 0xff;
        int high = b[start + 1] & 0xff;
        return (int) (high << 8 | low);
    }
}
