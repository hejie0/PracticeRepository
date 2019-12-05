package jdk.encryption.irreversible;

import jdk.encryption.BytesToHex;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class HmacUtils {

    public enum Hmac{
        HmacMD5("HmacMD5"
        ), HmacSHA1("HmacSHA1"
        ), HmacSHA256("HmacSHA256"
        ), HmacSHA384("HmacSHA384"
        ), HmacSHA512("HmacSHA512");

        private String name;
        private Hmac(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
    }

    public static byte[] initHmacKey(Hmac hmac) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(hmac.getName());
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }

    public static String encode(byte[] kye, byte[] data, Hmac hmac) throws Exception {
        SecretKey secretKey = new SecretKeySpec(kye, hmac.getName());
        Mac mac = Mac.getInstance(hmac.getName());
        mac.init(secretKey);
        byte[] resultBytes = mac.doFinal(data);
        return BytesToHex.fromBytesToHex(resultBytes);
    }

    public static void main(String[] args) throws Exception {
        byte[] key = initHmacKey(Hmac.HmacMD5);
        String result = encode(key, "hello".getBytes(), Hmac.HmacMD5);

        System.out.println(BytesToHex.fromBytesToHex(key));
        System.out.println(result);
    }
}
