package netty.demo.filesync.utils;

public class Utils {

    public static int MAX_BUFFER_SIZE = 8192;
    public static int MAX_FILE_BLOCK_SIZE = 8100;
    public static int FILE_SEGMENT_SIZE = 12 * MAX_BUFFER_SIZE; //96KB
    public static int PULL_REQUEST_SIZE = FILE_SEGMENT_SIZE + 4 * 1024;

}
