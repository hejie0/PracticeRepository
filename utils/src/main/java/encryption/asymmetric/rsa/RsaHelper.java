package encryption.asymmetric.rsa;

import encryption.jdk8.Base64Util;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RsaHelper {
    private static final Logger logger = Logger.getLogger(RsaHelper.class);
    private RSAPublicKey publicKey;
    private RSAPrivateCrtKey privateKey;

    static {
        Security.addProvider(new BouncyCastleProvider()); //使用bouncycastle作为加密算法实现
    }

    public RsaHelper(String publicKey, String privateKey) {
        this(Base64Util.decode(publicKey), Base64Util.decode(privateKey));
    }

    public RsaHelper(byte[] publicKey, byte[] privateKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            if (publicKey != null && publicKey.length > 0) {
                this.publicKey = (RSAPublicKey)keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
            }
            if (privateKey != null && privateKey.length > 0) {
                this.privateKey = (RSAPrivateCrtKey)keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public RsaHelper(String publicKey) {
        this(Base64Util.decode(publicKey));
    }

    public RsaHelper(byte[] publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            if (publicKey != null && publicKey.length > 0) {
                this.publicKey = (RSAPublicKey)keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] encrypt(byte[] content) {
        if (publicKey == null) {
            throw new RuntimeException("public key is null.");
        }

        if (content == null) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            int size = publicKey.getModulus().bitLength() / 8 - 11;
            ByteArrayOutputStream baos = new ByteArrayOutputStream((content.length + size - 1) / size * (size + 11));
            int left = 0;
            for (int i = 0; i < content.length; ) {
                left = content.length - i;
                if (left > size) {
                    cipher.update(content, i, size);
                    i += size;
                } else {
                    cipher.update(content, i, left);
                    i += left;
                }
                baos.write(cipher.doFinal());
            }
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] decrypt(byte[] secret) {
        if (privateKey == null) {
            throw new RuntimeException("private key is null.");
        }

        if (secret == null) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            int size = privateKey.getModulus().bitLength() / 8;
            ByteArrayOutputStream baos = new ByteArrayOutputStream((secret.length + size - 12) / (size - 11) * size);
            int left = 0;
            for (int i = 0; i < secret.length; ) {
                left = secret.length - i;
                if (left > size) {
                    cipher.update(secret, i, size);
                    i += size;
                } else {
                    cipher.update(secret, i, left);
                    i += left;
                }
                baos.write(cipher.doFinal());
            }
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("rsa decrypt failed.", e);
        }
        return null;
    }

    public byte[] sign(byte[] content) {
        if (privateKey == null) {
            throw new RuntimeException("private key is null.");
        }
        if (content == null) {
            return null;
        }
        try {
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initSign(privateKey);
            signature.update(content);
            return signature.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verify(byte[] sign, byte[] content) {
        if (publicKey == null) {
            throw new RuntimeException("public key is null.");
        }
        if (sign == null || content == null) {
            return false;
        }
        try {
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(publicKey);
            signature.update(content);
            return signature.verify(sign);
        } catch (Exception e) {
            logger.error("rsa verify failed", e);
        }
        return false;
    }


    public static void main(String[] args) throws Exception {
        RsaHelper helper = new RsaHelper("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTq1VS0G6B3qZDMgUwpOHDFjhMtOcOcGYi9bLpxLwfdjvIXOn/jT+TW6aT2Rzd3a5nyZaVJLY3XztG506pRrWEs54zndPp9+Tmt4HLIe2JIj2DLgS/ILCcX9ltN8X8rwOA7zsCoMMRk5pWWO7IyHHeVmlFMf9tYvTanWnmflrS5QIDAQAB",
                "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJOrVVLQboHepkMyBTCk4cMWOEy05w5wZiL1sunEvB92O8hc6f+NP5NbppPZHN3drmfJlpUktjdfO0bnTqlGtYSznjOd0+n35Oa3gcsh7YkiPYMuBL8gsJxf2W03xfyvA4DvOwKgwxGTmlZY7sjIcd5WaUUx/21i9NqdaeZ+WtLlAgMBAAECgYAzshRhtIQrROXRuk4k1GQQptO4zs/gtyqabIdg3TW6keql3xkwFKPAE6LCOYyogzXOMb1cowiDY/xdexaHnjBnI+w+VTbTd6cMxMdkJuu52KA68OwktdWTjnoxsNiRBNfMdV55UE3ktvF3VKT4A9Mw5Y+F6BZx3+jimpdq/PnCFQJBAPMPkJ7DHRvmBEbme0rRjVkMWwLHxPHVz+H4/JfBeEtOG74rMoGCsA9BvmEbb3bCcc3NnmKaPl+q9/DxCAku31cCQQCbh8WJZAJ3+AGmpL3BTsn2C/MTt0vcSBlb8qAxCVPqCBtYdjQAJs5u0fIoXCPDN2v9WbO8TMJOipuXZO8lU8YjAkBtKOthce1Lz0TSdsaednhNqs/g8skPd2ASc5k1qo0U/PJWuN3I7mGVqIgQ4ppvsqobrKHaVL14h7csvYNHZIanAkEAg+3funODRJCzldAYxmk3F+d7AjZxuyHNoRlmmTH4ZIUL7TYXVimU4pxkdafzDh1tFTOKKyhRip447WtJELjHUQJATFAA4c53Ucc9UwYP5RyZQlls8FsCzY4z2vKJMtbnuALQ9inLszo/AKMoBx91DSd/O/Pl/4hQ5HsOU8rwQ6K3aQ==");
        String message = "hello";
        byte[] encrypted = helper.encrypt(message.getBytes("UTF-8"));
        byte[] decrypted = helper.decrypt(encrypted);
        System.out.println("message: " + new String(decrypted, "UTF-8"));

    }
}
