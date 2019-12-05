package jdk.encryption.signature;

import jdk.encryption.asymmetric.RSAUtils1;

import java.security.*;
import java.util.Map;

/**
 * RSA
 * jdk8目前支持: MD2withRSA、MD5withRSA
 * SHA1withRSA、SHA224withRSA、SHA256withRSA、SHA384withRSA、SHA512withRSA
 */
public class RSASignatureUtils {

    public static String signature = "MD5withRSA";

    public static byte[] generateSign(byte[] data, byte[] privateKey) throws Exception {
        PrivateKey priKey = RSAUtils1.bytesToPrivateKey(privateKey);
        Signature sign = Signature.getInstance(signature); //实例化Signature
        sign.initSign(priKey); //初始化Signature
        sign.update(data); //更新
        return sign.sign(); //生成签名
    }

    public static boolean verifySign(byte[] data, byte[] signData, byte[] publicKey) throws Exception {
        PublicKey pubKey = RSAUtils1.bytesToPublicKey(publicKey);
        Signature sign = Signature.getInstance(signature);
        sign.initVerify(pubKey);
        sign.update(data);
        return sign.verify(signData); //验证
    }

    public static void main(String[] args) throws Exception {
        byte[] data = "hello".getBytes();
        Map<String, Key> keyMap = RSAUtils1.initRSAKey();
        byte[] publicKey = RSAUtils1.getPublicKey(keyMap);
        byte[] privateKey = RSAUtils1.getPrivateKey(keyMap);

        byte[] sign = generateSign(data, privateKey);
        //data = "hello world".getBytes();
        boolean verify = verifySign(data, sign, publicKey);  //当 数据|签名|公钥 改变时 验证为false
        System.out.println(verify);
    }

}
