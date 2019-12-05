package jdk.encryption.signature;

import jdk.encryption.asymmetric.Asymmetric;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Map;

/**
 * DSA算法实现就是RSA数字签名算法实现的简装版,仅支持SHA系列算法
 * jdk8目前支持: SHA1withDSA、SHA224withDSA、SHA256withDSA
 */
public class DSASignatureUtils {

    public static String signature = "SHA1withDSA";
    public static final String ALGORITHM = "DSA";
    public static final String DSA_PUBLIC_KEY = "DSAPublicKey";
    public static final String DSA_PRIVATE_KEY = "DSAPrivateKey";
    public static int keySize = 1024;

    public static Map<String, Key> initDSAKey() throws Exception {
        Asymmetric.PUBLIC_KEY = DSA_PUBLIC_KEY;
        Asymmetric.PRIVATE_KEY = DSA_PRIVATE_KEY;
        return Asymmetric.initKey(ALGORITHM, keySize);
    }

    public static byte[] getPublicKey(Map<String, Key> keyMap){
        return keyMap.get(DSA_PUBLIC_KEY).getEncoded();
    }

    public static byte[] getPrivateKey(Map<String, Key> keyMap){
        return keyMap.get(DSA_PRIVATE_KEY).getEncoded();
    }

    public static byte[] generateSign(byte[] data, byte[] privateKey) throws Exception {
        PrivateKey priKey = Asymmetric.bytesToPrivateKey(ALGORITHM, privateKey);
        Signature sign = Signature.getInstance(signature); //实例化Signature
        sign.initSign(priKey); //初始化Signature
        sign.update(data); //更新
        return sign.sign(); //生成签名
    }

    public static boolean verifySign(byte[] data, byte[] signData, byte[] publicKey) throws Exception {
        PublicKey pubKey = Asymmetric.bytesToPublicKey(ALGORITHM, publicKey);
        Signature sign = Signature.getInstance(signature);
        sign.initVerify(pubKey);
        sign.update(data);
        return sign.verify(signData); //验证
    }

    public static void main(String[] args) throws Exception {
        byte[] data = "hello".getBytes();
        Map<String, Key> keyMap = initDSAKey();
        byte[] publicKey = getPublicKey(keyMap);
        byte[] privateKey = getPrivateKey(keyMap);

        byte[] sign = generateSign(data, privateKey);
        //data = "hello world".getBytes();
        boolean verify = verifySign(data, sign, publicKey);  //当 数据|签名|公钥 改变时 验证为false
        System.out.println(verify);
    }

}
