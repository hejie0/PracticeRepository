package jdk.encryption.asymmetric;

import jdk.encryption.symmetry.Base64Util;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.StringWriter;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class Asymmetric {

    public static String PUBLIC_KEY;
    public static String PRIVATE_KEY;

    /**
     * 生成公私钥
     * @param algorithm
     * @param keySize
     * @return
     * @throws Exception
     */
    public static Map<String, Key> initKey(String algorithm, int keySize) throws Exception {
        Map<String, Key> keyMap = new HashMap<>(2); //存储公私钥
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm); //实例化密钥对生成器
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair(); //生成密钥对
        keyMap.put(PUBLIC_KEY, keyPair.getPublic());    //将公钥保存到keyMap中
        keyMap.put(PRIVATE_KEY, keyPair.getPrivate());  //将私钥保存到keyMap中
        return keyMap;
    }

    /**
     * 将byte数组转换为PublicKey
     * @param algorithm
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static PublicKey bytesToPublicKey(String algorithm, byte[] publicKey) throws Exception {
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory .generatePublic(pubKeySpec);
    }

    /**
     * 将byte数组转换为PrivateKey
     * @param algorithm
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static PrivateKey bytesToPrivateKey(String algorithm, byte[] privateKey) throws Exception {
        PKCS8EncodedKeySpec priKeySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePrivate(priKeySpec);
    }

    /**
     * 将公私钥格式化，除了BEGIN和END 内容与base64转换后一模一样
     * @param title
     * @param key
     * @return
     * @throws Exception
     */
    public static String formatKey(String title, byte[] key) throws Exception {
        PemObject pemObject = new PemObject(title, key);
        StringWriter stringWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(stringWriter);
        pemWriter.writeObject(pemObject);
        pemWriter.close();
        String pemString = stringWriter.toString();
        return pemString;
    }

    public static void main(String[] args) throws Exception {
        Map<String, Key> keyMap = RSAUtils1.initRSAKey();
        byte[] pubKey = RSAUtils1.getPublicKey(keyMap);
        byte[] priKey = RSAUtils1.getPrivateKey(keyMap);

        String publicKey = formatKey("PUBLIC KEY", pubKey); //内容与base64转换后一模一样
        System.out.println(Base64Util.encodeToString(pubKey));
        System.out.println(publicKey);
        System.out.println("=======================================================");
        String privateKey = formatKey("PRIVATE KEY", priKey); //内容与base64转换后一模一样
        System.out.println(Base64Util.encodeToString(priKey));
        System.out.println(privateKey);
    }


}
