package jdk;

import jdk.encryption.BytesToHex;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    /**
     * 编码
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String encode(byte[] data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data);
        byte[] bytes = md5.digest();
        return BytesToHex.fromBytesToHex(bytes);
    }

    /**
     * 对文件编码
     * @param filePath
     * @return
     * @throws Exception
     */
    public static String encode(String filePath) throws Exception {
        FileInputStream fis = new FileInputStream(filePath);
        return encode(fis);
    }

    public static String encode(InputStream in) throws Exception {
        try(DigestInputStream dis = new DigestInputStream(in, MessageDigest.getInstance("MD5"));){
            byte[] buffer = new byte[1024];
            int read = dis.read(buffer);
            while (read != -1){
                read = dis.read(buffer);
            }
            MessageDigest md5 = dis.getMessageDigest();
            byte[] bytes = md5.digest();
            return BytesToHex.fromBytesToHex(bytes);
        }
    }

    public static void main(String[] args) throws Exception {
        String md5Result1 = encode("hello".getBytes());
        System.out.println(md5Result1);

        InputStream in = MD5Utils.class.getResourceAsStream("/hello.txt");
        String md5Result2 = encode(in);
        System.out.println(md5Result2);
    }

}
