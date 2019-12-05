package jdk.encryption.asymmetric;

import jdk.encryption.BytesToHex;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

/**
 * RSA+Base64加密解密工具类 RSA 一般用在数据传输过程中的加密和解密 先用RSA加密后是字节数组 再用BASE64加密 成字符串进行传输 测试
 * RSA1024产生的公钥字节长度在160-170之间 私钥长度在630-640之间 经过base64加密后长度 公钥字节产度在210-220之间
 * 私钥长度在840-850之间 所以数据库设计时如果存公钥长度设计varchar(256) 私钥长度varchar(1024)
 *
 *
 * 1024 512-65536 & 64倍数
 * 工作模式：
 * ECB
 * 填充方式：
 * NoPadding、PKCS1Padding
 * OAEPWITHMD5AndMGF1Padding
 * OAEPWITHSHA1AndMGF1Padding
 * OAEPWITHSHA256AndMGF1Padding
 * OAEPWITHSHA384AndMGF1Padding
 * OAEPWITHSHA512AndMGF1Padding
 *
 * PublicKey转byte[]: PublicKey.getEncoded()
 * byte[]转PublicKey: KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(byte[]));
 *-------------------
 * PrivateKey转byte[]: PrivateKey.getEncoded()
 * byte[]转PrivateKey: KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(byte[]));
 */
public class RSAUtils1 {

    public static final String ALGORITHM = "RSA";
    public static final String RSA_PUBLIC_KEY = "RSAPublicKey";
    public static final String RSA_PRIVATE_KEY = "RSAPrivateKey";
    public static int keySize = 1024;

    public static Map<String, Key> initRSAKey() throws Exception {
        Asymmetric.PUBLIC_KEY = RSA_PUBLIC_KEY;
        Asymmetric.PRIVATE_KEY = RSA_PRIVATE_KEY;
        return Asymmetric.initKey(ALGORITHM, keySize); //1024 512-65536 & 64倍数
    }

    public static byte[] getPublicKey(Map<String, Key> keyMap){
        return keyMap.get(RSA_PUBLIC_KEY).getEncoded();
    }

    public static byte[] getPrivateKey(Map<String, Key> keyMap){
        return keyMap.get(RSA_PRIVATE_KEY).getEncoded();
    }

    public static byte[] encrypt(byte[] data, byte[] publicKey) throws Exception {
        PublicKey pubKey = bytesToPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] encryptedData, byte[] privateKey) throws Exception {
        PrivateKey priKey = bytesToPrivateKey(privateKey);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return cipher.doFinal(encryptedData);
    }

    public static PublicKey bytesToPublicKey(byte[] publicKey) throws Exception {
        return Asymmetric.bytesToPublicKey(ALGORITHM, publicKey);
    }

    public static PrivateKey bytesToPrivateKey(byte[] privateKey) throws Exception {
        return Asymmetric.bytesToPrivateKey(ALGORITHM, privateKey);
    }

    public static void main(String[] args) throws Exception {
        byte[] data = "hello".getBytes();
        Map<String, Key> keyMap = initRSAKey();
        byte[] publicKey = getPublicKey(keyMap);
        byte[] privateKey = getPrivateKey(keyMap);

        byte[] encryptedData = encrypt(data, publicKey);
        byte[] decryptedData = decrypt(encryptedData, privateKey);

        System.out.println("加密后的数据: " + BytesToHex.fromBytesToHex(encryptedData));
        System.out.println("解密后的数据: " + new String(decryptedData));
    }
}
