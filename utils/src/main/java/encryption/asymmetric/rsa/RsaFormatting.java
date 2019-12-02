package encryption.asymmetric.rsa;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RsaFormatting {

    public static Map<Integer, Key> initKey() throws Exception {
        Map<Integer, Key> keys = new HashMap<>(2);
        // 生成密钥对
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey)keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)keyPair.getPrivate();
        keys.put(0, rsaPrivateKey);
        keys.put(1, rsaPublicKey);
        return keys;
    }

    public static void rsaPriFormat(byte[] rsaPrivateKey) throws Exception {
        // 格式化私钥
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");  // 确定算法
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);  // 确定加密密钥
        byte[] result = cipher.doFinal("原文".getBytes());  // 加密
        System.out.println(Base64.encodeBase64String(result));
    }

    public static void rsaPubFormat(byte[] rsaPublicKey) throws Exception {
        // 格式化公钥
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA"); // 确定算法
        cipher.init(Cipher.DECRYPT_MODE, publicKey);  // 确定公钥
        byte[] result = cipher.doFinal("原文".getBytes());  // 加密
        System.out.println(Base64.encodeBase64String(result));
    }

    public static void main(String[] args) throws Exception {
        String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/81D0YhV4C+OamZCF2d3mdsBdLRpVTfMLYfLGh2ZaqUZWJaQ88NTEtavZJWRJD9TfGm4xABBFBmc2ptkW2b55X9GOZsUIjWvV5v4LFeFli+0D0rdgaRzdrAduxp8aPxvzfkduQ8fqLUl2k/u6Gj7BUwSqukyL5KdC0iMyyK0UWwIDAQAB";
        String priKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAL/zUPRiFXgL45qZkIXZ3eZ2wF0tGlVN8wth8saHZlqpRlYlpDzw1MS1q9klZEkP1N8abjEAEEUGZzam2RbZvnlf0Y5mxQiNa9Xm/gsV4WWL7QPSt2BpHN2sB27Gnxo/G/N+R25Dx+otSXaT+7oaPsFTBKq6TIvkp0LSIzLIrRRbAgMBAAECgYAb0A2y22Nw+YgJJqciPedBNYO5/IcMRTcqNw8OZSAXkt92LA4YJm0t84Tq+dgGHh0iFppQz0mMNtlNue9KuFI404DVJVbaJHw66DcerMDeNWaiW47189/PO3aBBPy7VB0c7MMs9ag3f4aD5DO8odrlAI+ZRrP+7wk8WO0sybeBgQJBAPKi9BjuafOXyFd3CJ7hZQlEJ1CMMNQ4A0aCaRrPg5QQn9utqN8/UQPv8oZ1VheBDLMdSl9Ty4r608H0+dDyOkMCQQDKhbdAylcfzZlpFWGvu8U/dK0AhABGBOd21Y/SQr9oLLo+T7/6gAxWC/+O79SnvbyC4UnBfQVIalkBsIx2NlgJAkEA1uRBog8NoWNNWUGgTe676EAHPcxbtqjBJeJ8KfWdyDYNDqe4R7ixDPanajRjgj+WOtB3PQRe4gESKNbJpifSNwJBALU7GbVSHMsp+onOB3/yQV46tAJNHc2K3/M4w549+vlnZTcTuNGbOu7Zh9VKi3ucUBCzOIR8s3iNKp9XdSroELkCQB2G+6kanJcxoVavtUTNYa+KTKPoMeG8FPhk/jVqxxkCuzfECLwFDXZ8sr36zbnfH+mbM4TpJCJwuwDDLkQekWA=";
        rsaPriFormat(priKey.getBytes());
        rsaPubFormat(pubKey.getBytes());

    }

}
