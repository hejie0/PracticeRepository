package encryption.asymmetric.rsa;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * RSA+Base64加密解密工具类 RSA 一般用在数据传输过程中的加密和解密 先用RSA加密后是字节数组 再用BASE64加密 成字符串进行传输 测试
 * RSA1024产生的公钥字节长度在160-170之间 私钥长度在630-640之间 经过base64加密后长度 公钥字节产度在210-220之间
 * 私钥长度在840-850之间 所以数据库设计时如果存公钥长度设计varchar(256) 私钥长度varchar(1024)
 *
 */
public abstract class RsaUtils {
    public static final String KEY_ALGORITHM = "RSA";
    private static final String PUBLIC_KEY = "rsa_public_key";
    private static final String PRIVATE_KEY = "rsa_private_key";
    private static final String ENCODING = "UTF-8";
    private static String priSalt = "a#b%s@Jdwa&"; //给私钥加盐

    static {
        try {
            String priSalt1 = Base64.encodeBase64String(priSalt.getBytes(ENCODING));
            if(priSalt1 != null) priSalt = priSalt1;
        } catch (Exception e) {}
    }

    /**
     *
     * 加密
     * 用公钥加密
     * @param content
     * @param base64PublicKeyStr
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String content, String base64PublicKeyStr) throws Exception {
        byte[] inputBytes = content.getBytes(ENCODING);
        byte[] outputBytes = encryptByPublicKey(inputBytes, base64PublicKeyStr);
        return Base64.encodeBase64String(outputBytes);
    }

    /**
     *
     * 加密
     *
     * 用私钥加密
     * @param content
     * @param base64PrivateKeyStr
     * @return
     * @throws Exception
     */
    public static String encryptByPrivateKey(String content, String base64PrivateKeyStr) throws Exception {
        byte[] inputBytes = content.getBytes(ENCODING);
        byte[] outputBytes = encryptByPrivateKey(inputBytes, base64PrivateKeyStr);
        return Base64.encodeBase64String(outputBytes);
    }

    /**
     *
     * 解密
     * 用公钥解密
     * @param content
     * @param base64PublicKeyStr
     * @return
     * @throws Exception
     */
    public static String decryptByPublicKey(String content, String base64PublicKeyStr) throws Exception {
        byte[] inputBytes = Base64.decodeBase64(content);
        byte[] outputBytes = decryptByPublicKey(inputBytes, base64PublicKeyStr);
        return new String(outputBytes, ENCODING);
    }

    /**
     *
     * 解密
     * 用私钥解密
     * @param content
     * @param base64PrivateKeyStr
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String content, String base64PrivateKeyStr) throws Exception {
        byte[] inputBytes = Base64.decodeBase64(content);
        byte[] outputBytes = decryptByPrivateKey(inputBytes, base64PrivateKeyStr);
        return new String(outputBytes, ENCODING);

    }

    /**
     *
     * 加密
     * 用公钥加密
     * @param content
     * @param base64PublicKeyStr
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] content, String base64PublicKeyStr) throws Exception {
        // 对公钥解密
        byte[] publicKeyBytes = Base64.decodeBase64(base64PublicKeyStr);

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(content);

    }

    /**
     *
     * 加密
     * 用私钥加密
     * @param content
     * @param base64PrivateKeyStr
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] content, String base64PrivateKeyStr) throws Exception {
        // 对密钥解密
        byte[] privateKeyBytes = Base64.decodeBase64(base64PrivateKeyStr);

        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return cipher.doFinal(content);

    }

    /**
     *
     * 解密
     * 用公钥解密
     * @param content
     * @param base64PublicKeyStr
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] content, String base64PublicKeyStr) throws Exception {
        // 对密钥解密
        byte[] publicKeyBytes = Base64.decodeBase64(base64PublicKeyStr);

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        return cipher.doFinal(content);

    }

    /**
     * 解密
     * 用私钥解密
     * @param content
     * @param base64PrivateKeyStr
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] content, String base64PrivateKeyStr) throws Exception {
        // 对密钥解密
        byte[] privateKeyBytes = Base64.decodeBase64(base64PrivateKeyStr);

        // 取得私钥  for PKCS#1
//        RSAPrivateKeyStructure asn1PrivKey = new RSAPrivateKeyStructure((ASN1Sequence) ASN1Sequence.fromByteArray(privateKeyBytes));
//        RSAPrivateKeySpec rsaPrivKeySpec = new RSAPrivateKeySpec(asn1PrivKey.getModulus(), asn1PrivKey.getPrivateExponent());
//        KeyFactory keyFactory= KeyFactory.getInstance("RSA");
//        PrivateKey priKey= keyFactory.generatePrivate(rsaPrivKeySpec);


        // 取得私钥  for PKCS#8
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key priKey = keyFactory.generatePrivate(pkcs8KeySpec);



        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return cipher.doFinal(content);
    }

    /**
     *
     * 取得私钥
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getBase64PrivateKeyStr(Map keyMap) throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 取得公钥
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getBase64PublicKeyStr(Map keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 初始化密钥
     * @return
     * @throws Exception
     */
    public static Map<String, Key> initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024); // 初始化RSA1024安全些
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic(); // 公钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate(); // 私钥
        Map keyMap = new HashMap(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    public static void sub(String content){
            int index = 0;
            String cont = null;
            StringBuilder builder = new StringBuilder();
            while(index + 40 < content.length()){
                cont = content.substring(index, index + 40);
                builder.append(cont + "\r\n");
                index += 40;
            }
            cont = content.substring(index, content.length());
            builder.append(cont);
            System.out.println(content);
            System.out.println(builder.toString());
            System.out.println(builder.toString().equals(content));
    }

    private static void test() throws Exception {
        Map<String, Key> keys = initKey();
        String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/81D0YhV4C+OamZCF2d3mdsBdLRpVTfMLYfLGh2ZaqUZWJaQ88NTEtavZJWRJD9TfGm4xABBFBmc2ptkW2b55X9GOZsUIjWvV5v4LFeFli+0D0rdgaRzdrAduxp8aPxvzfkduQ8fqLUl2k/u6Gj7BUwSqukyL5KdC0iMyyK0UWwIDAQAB";
        String priKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAL/zUPRiFXgL45qZkIXZ3eZ2wF0tGlVN8wth8saHZlqpRlYlpDzw1MS1q9klZEkP1N8abjEAEEUGZzam2RbZvnlf0Y5mxQiNa9Xm/gsV4WWL7QPSt2BpHN2sB27Gnxo/G/N+R25Dx+otSXaT+7oaPsFTBKq6TIvkp0LSIzLIrRRbAgMBAAECgYAb0A2y22Nw+YgJJqciPedBNYO5/IcMRTcqNw8OZSAXkt92LA4YJm0t84Tq+dgGHh0iFppQz0mMNtlNue9KuFI404DVJVbaJHw66DcerMDeNWaiW47189/PO3aBBPy7VB0c7MMs9ag3f4aD5DO8odrlAI+ZRrP+7wk8WO0sybeBgQJBAPKi9BjuafOXyFd3CJ7hZQlEJ1CMMNQ4A0aCaRrPg5QQn9utqN8/UQPv8oZ1VheBDLMdSl9Ty4r608H0+dDyOkMCQQDKhbdAylcfzZlpFWGvu8U/dK0AhABGBOd21Y/SQr9oLLo+T7/6gAxWC/+O79SnvbyC4UnBfQVIalkBsIx2NlgJAkEA1uRBog8NoWNNWUGgTe676EAHPcxbtqjBJeJ8KfWdyDYNDqe4R7ixDPanajRjgj+WOtB3PQRe4gESKNbJpifSNwJBALU7GbVSHMsp+onOB3/yQV46tAJNHc2K3/M4w549+vlnZTcTuNGbOu7Zh9VKi3ucUBCzOIR8s3iNKp9XdSroELkCQB2G+6kanJcxoVavtUTNYa+KTKPoMeG8FPhk/jVqxxkCuzfECLwFDXZ8sr36zbnfH+mbM4TpJCJwuwDDLkQekWA=";
        System.out.println("公钥: " + pubKey);
        System.out.println("私钥: " + priKey);

        String str = "123456";
        String content = encryptByPublicKey(str, pubKey);
        System.out.println("添加数据后的公钥: " + content);

        String output = decryptByPrivateKey(content, priKey);
        System.out.println(output);


        String priKeyBase64new = Base64.encodeBase64String((priKey+priSalt).getBytes(ENCODING));
        String priKeyBase64old = new String( Base64.decodeBase64(priKeyBase64new) );
        priKeyBase64old = priKeyBase64old.substring(0, priKeyBase64old.length() - priSalt.length());
        System.out.println("私钥的盐: " + priSalt);
        System.out.println("base64加密后的私钥: " + priKeyBase64new);
        System.out.println("base64解密后的私钥: " + priKeyBase64old);
        System.out.println("原私钥 == base64加密后的私钥: " + priKey.equals(priKeyBase64new));
        System.out.println("原私钥 == base64解密后的私钥: " + priKey.equals(priKeyBase64old));
    }

    // === Testing ===
    public static void main(String[] args) throws Exception {
//        String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/81D0YhV4C+OamZCF2d3mdsBdLRpVTfMLYfLGh2ZaqUZWJaQ88NTEtavZJWRJD9TfGm4xABBFBmc2ptkW2b55X9GOZsUIjWvV5v4LFeFli+0D0rdgaRzdrAduxp8aPxvzfkduQ8fqLUl2k/u6Gj7BUwSqukyL5KdC0iMyyK0UWwIDAQAB";
//        sub(pubKey);
        test();
    }

}
