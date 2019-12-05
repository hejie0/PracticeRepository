package jdk.encryption.symmetry;

import jdk.encryption.BytesToHex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES是最优选择，也是最常用的
 * 算法: DES|DESede|AES
 * 工作模式: ECB|CBC|PCBC|CTR|CTS|CFB|CFB8-CFB128|OFB|OFB8-OFB128
 * 填充方式: NoPadding|PKCS5Padding|ISO10126Padding
 * 例子: Cipher.getInstance("DES/ECB/NoPadding");
 */
public class ESUtils {

    public enum ES{
        DES("DES"), ThreeDES("DESede"), AES("AES");

        private String name;
        private ES(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
    }

    /**
     * 生成密钥
     * @param es
     * @return
     * @throws Exception
     */
    public static byte[] initESKey(ES es) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(es.getName());
        //keyGenerator.init(keyLength); //DES默认56 56, 3DES默认168 112|168, AES默认128 128|192|256
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * 加密
     * @param key
     * @param data
     * @param es
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] key, byte[] data, ES es) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, es.getName());
        Cipher cipher = Cipher.getInstance(es.getName());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    /**
     * 解密
     * @param key
     * @param data
     * @param es
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] key, byte[] data, ES es) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, es.getName());
        Cipher cipher = Cipher.getInstance(es.getName());
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static void main(String[] args) throws Exception {
        String data = "hello";
        byte[] key = initESKey(ES.AES);
        byte[] encryptResult = encrypt(key, data.getBytes(), ES.AES);
        byte[] decryptResult = decrypt(key, encryptResult, ES.AES);

        System.out.println(BytesToHex.fromBytesToHex(key));
        System.out.println(BytesToHex.fromBytesToHex(encryptResult));
        System.out.println(new String(decryptResult));
    }
}
