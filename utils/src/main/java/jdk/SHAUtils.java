package jdk;

import jdk.encryption.BytesToHex;

import java.security.MessageDigest;

public class SHAUtils {

    public enum SHA{
        SHA("SHA-1"), SHA256("SHA-256"), SHA384("SHA-384"), SHA512("SHA-512");
        private String name;
        private SHA(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
    }

    /**
     * 编码
     * @param data 编码内容
     * @param sha
     * @return
     * @throws Exception
     */
    public static String encode(String data, SHA sha) throws Exception {
        MessageDigest arithmetic = MessageDigest.getInstance(sha.getName());
        arithmetic.update(data.getBytes());
        byte[] bytes = arithmetic.digest();
        return BytesToHex.fromBytesToHex(bytes);
    }

    public static String encode(String data) throws Exception {
        return encode(data, SHA.SHA);
    }

    public static void main(String[] args) throws Exception {
        String shaResult = encode("hello", SHA.SHA256);
        System.out.println(shaResult);
    }

}
