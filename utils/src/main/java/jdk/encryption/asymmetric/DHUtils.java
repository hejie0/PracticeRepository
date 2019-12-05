package jdk.encryption.asymmetric;

import jdk.encryption.BytesToHex;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * keySize默认是1024 （512-1024 & 64的倍数）
 */
public class DHUtils {

    public static final String ALGORITHM = "DH";
    public static final String DH_PUBLIC_KEY = "DHPublicKey";
    public static final String DH_PRIVATE_KEY = "DHPrivateKey";
    public static int keySize = 1024;

    /**
     * 甲方初始化并返回密钥对
     * @return
     * @throws Exception
     */
    public static Map<String, Key> initDHKey() throws Exception {
        Asymmetric.PUBLIC_KEY = DH_PUBLIC_KEY;
        Asymmetric.PRIVATE_KEY = DH_PRIVATE_KEY;
        return Asymmetric.initKey(ALGORITHM, keySize); //1024 （512-1024 & 64的倍数）;
    }

    /**
     * 乙方根据甲方公钥初始化并返回密钥对
     * @param key
     * @return
     * @throws Exception
     */
    public static Map<String, Key> initKey(byte[] key) throws Exception {
        Map<String, Key> keyMap = new HashMap<>(2);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key); //将甲方公钥从字节数组转换为PublicKey
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM); //实例化密钥工厂
        DHPublicKey publicKey = (DHPublicKey)keyFactory.generatePublic(keySpec); //产生甲方公钥publicKey
        DHParameterSpec dhParameterSpec = publicKey.getParams(); //刨析甲方公钥，得到其参数
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM); //实例化密钥对生成器
        keyPairGenerator.initialize(dhParameterSpec); //用甲方公钥初始化密钥对生成器
        KeyPair keyPair = keyPairGenerator.generateKeyPair(); //产生密钥对
        keyMap.put(DH_PUBLIC_KEY, keyPair.getPublic()); //将乙方公钥保存到keyMap中
        keyMap.put(DH_PRIVATE_KEY, keyPair.getPrivate()); //将乙方私钥保存到keyMap中
        return keyMap;
    }

    /**
     * 根据对方的公钥和自己的私钥生成 本地密钥
     * @param publicKey
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] getSecretKey(byte[] publicKey, byte[] privateKey) throws Exception {
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKey);
        PKCS8EncodedKeySpec priKeySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM); //实例化密钥工厂
        PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);  //将公钥字节数组转换为PublicKey
        PrivateKey priKey = keyFactory.generatePrivate(priKeySpec); //将私钥字节数组转换为PrivateKey
        //准备根据以上公钥和私钥生成本地密钥SecretKey
        KeyAgreement keyAgreement = KeyAgreement.getInstance(ALGORITHM); //先实例化KeyAgreement
        keyAgreement.init(priKey); //用自己的私钥初始化KeyAgreement
        keyAgreement.doPhase(pubKey, true); //结合对方的公钥进行运算
        return keyAgreement.generateSecret(); //开始生成本地密钥SecretKey 密钥算法为对称密码算法（DES|3DES|AES）
    }

    /**
     * 从Map中取得公钥
     * @param keyMap
     * @return
     */
    public static byte[] getPublicKey(Map<String, Key> keyMap){
        return keyMap.get(DH_PUBLIC_KEY).getEncoded();
    }

    /**
     * 从Map中取得私钥
     * @param keyMap
     * @return
     */
    public static byte[] getPrivateKey(Map<String, Key> keyMap){
        return keyMap.get(DH_PRIVATE_KEY).getEncoded();
    }

    public static void main(String[] args) throws Exception {
        //甲方: 公钥、私钥、本地密钥
        byte[] publicKey1, privateKey1, secretKey1;
        //乙方: 公钥、私钥、本地密钥
        byte[] publicKey2, privateKey2, secretKey2;

        Map<String, Key> keyMap1 = initDHKey(); //初始化密钥 & 生成甲方密钥对
        publicKey1 = getPublicKey(keyMap1);
        privateKey1 = getPrivateKey(keyMap1);
        System.out.println("DH 甲方公钥: " + BytesToHex.fromBytesToHex(publicKey1));
        System.out.println("DH 甲方私钥: " + BytesToHex.fromBytesToHex(privateKey1));

        Map<String, Key> keyMap2 = initKey(publicKey1); //乙方根据甲方公钥生成密钥对
        publicKey2 = getPublicKey(keyMap2);
        privateKey2 = getPrivateKey(keyMap2);
        System.out.println("DH 乙方公钥: " + BytesToHex.fromBytesToHex(publicKey2));
        System.out.println("DH 乙方私钥: " + BytesToHex.fromBytesToHex(privateKey2));

        secretKey1 = getSecretKey(publicKey2, privateKey1); //甲方根据: 乙方的公钥 & 自己的私钥 生成本地密钥SecretKey1
        secretKey2 = getSecretKey(publicKey1, privateKey2); //乙方根据: 甲方的公钥 & 自己的私钥 生成本地密钥SecretKey2
        System.out.println("DH 甲方本地密钥: " + BytesToHex.fromBytesToHex(secretKey1));
        System.out.println("DH 乙方本地密钥: " + BytesToHex.fromBytesToHex(secretKey2));
    }
}
