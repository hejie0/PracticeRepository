package encryption.asymmetric.rsa;

import com.google.common.base.Strings;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.StringWriter;

/**
 * Transform PKCS format
 *     PKCS#1    -> PKCS#8
 *     PKCS#8    -> PKCS#1
 *
 */
public class RsaFormat {
    private static final String COMMENT_BEGIN_FLAG = "-----";
    private static final String RETURN_FLAG_R = "\r";
    private static final String RETURN_FLAG_N = "\n";



    //format PKCS#8 to PKCS#1
    public static String formatPkcs8ToPkcs1(String rawKey) throws Exception {
        String result = null;
        //extract valid key content
        String validKey = rawKey;//RsaPemUtil.extractFromPem(rawKey);
        if(!Strings.isNullOrEmpty(validKey)) {
            //将BASE64编码的私钥字符串进行解码
            byte[] encodeByte = Base64.decodeBase64(validKey);

            //==========
            //pkcs8Bytes contains PKCS#8 DER-encoded key as a byte[]
            PrivateKeyInfo pki = PrivateKeyInfo.getInstance(encodeByte);
            RSAPrivateKeyStructure pkcs1Key = RSAPrivateKeyStructure.getInstance(pki.getPrivateKey());
            byte[] pkcs1Bytes = pkcs1Key.getEncoded();//etc.
            //==========

            String type = "RSA PRIVATE KEY";
            result = format2PemString(type, pkcs1Bytes);
        }
        return result;
    }

    //format PKCS#1 to PKCS#8
    public static String formatPkcs1ToPkcs8(String rawKey) throws Exception {
        String result = null;
        //extract valid key content
        String validKey = rawKey;//RsaPemUtil.extractFromPem(rawKey);
        if(!Strings.isNullOrEmpty(validKey)) {
            //将BASE64编码的私钥字符串进行解码
            byte[] encodeByte = Base64.decodeBase64(validKey);

            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PKCSObjectIdentifiers.pkcs8ShroudedKeyBag);    //PKCSObjectIdentifiers.pkcs8ShroudedKeyBag
            ASN1Object asn1Object = ASN1Object.fromByteArray(encodeByte);
            PrivateKeyInfo privKeyInfo = new PrivateKeyInfo(algorithmIdentifier, asn1Object);
            byte[] pkcs8Bytes = privKeyInfo.getEncoded();

            String type = "PRIVATE KEY";
            result = format2PemString(type, pkcs8Bytes);

        }
        return result;
    }

    // Write to pem file
    private static String format2PemString(String type, byte[] privateKeyPKCS1) throws Exception {
        PemObject pemObject = new PemObject(type, privateKeyPKCS1);
        StringWriter stringWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(stringWriter);
        pemWriter.writeObject(pemObject);
        pemWriter.close();
        String pemString = stringWriter.toString();
        return pemString;
    }

    //=== Testing ===
    public static void main(String[] args) throws Exception {
        //String rawKey_pkcs1 = "MIICXQIBAAKBgQDgDxka1U8SWI1vBY7pA1UiCVnrdtSgE+PpyTqe2YSEWCgkYQ2YsohZwsaUao7nya7QnBgRiPKEHgS/Eey+L9iwo32Sn5fUwb0nJ1+JeXRA6JsDEKpONJojIbF2nfgHLWsNn4bzn5Webc6WZLx0GyLTQuZGadFVuVq2dQqEsrq7HwIDAQABAoGAYtaGLo4WWXNywJzlE+kCbwdNAU/kL9FWYtT/5P7zNCZnXtTpWIi5GU+QpfvzmlAfq6qP+3w77wgG8/qGQsd8gGu3mydi0ImmD9sJdhhsJuWZhCMM+qmvSmvG/gvIr+bdEmPhpCQpa3BLveUkFDA/OnwfTVL6ruwZayMzuToB6WECQQD63Gx9DZVhYoSxR7qSmiGf/TjfOJusTcrmc27Z5X5MS36a3Ux9Z+c9EaYFldZ3cPzP521ugNVvZdovKqFKIcQ5AkEA5KYeKBVlkrLaamSEu5WAX3DqJ6iDRqEjzMoVad5B1I7kJHO+NijUxNHaWaSqLOHuk37X+EAjSTozzwOkKwbqFwJBAM1NhhAWBNHtcdEwddWzBJ/N+jRdPLIX/Fz7zZXQRruj8VpGkGn1lf6ZqfjaNuoLcyunKB0OnR6NCbIePl/QIKkCQGgEQjfN9BVWlBJOhCuqCWphvcQo3v+kktq5HCC7YYtHLfZ/SQrubEzVgtXBGUGtzpD+5VUkKGlJtwP4Dhkc3iUCQQCwiFKuQe/OdlkYk1L4mb0H0fzy+/6mYxyUqpTXUw/6BVDOyowvzieh9oh2ZhnQS7YPBWz5ZXzwUH4YVwGqxiwA";
        //String rawKey_pkcs8 = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOAPGRrVTxJYjW8FjukDVSIJWet21KAT4+nJOp7ZhIRYKCRhDZiyiFnCxpRqjufJrtCcGBGI8oQeBL8R7L4v2LCjfZKfl9TBvScnX4l5dEDomwMQqk40miMhsXad+Actaw2fhvOflZ5tzpZkvHQbItNC5kZp0VW5WrZ1CoSyursfAgMBAAECgYBi1oYujhZZc3LAnOUT6QJvB00BT+Qv0VZi1P/k/vM0Jmde1OlYiLkZT5Cl+/OaUB+rqo/7fDvvCAbz+oZCx3yAa7ebJ2LQiaYP2wl2GGwm5ZmEIwz6qa9Ka8b+C8iv5t0SY+GkJClrcEu95SQUMD86fB9NUvqu7BlrIzO5OgHpYQJBAPrcbH0NlWFihLFHupKaIZ/9ON84m6xNyuZzbtnlfkxLfprdTH1n5z0RpgWV1ndw/M/nbW6A1W9l2i8qoUohxDkCQQDkph4oFWWSstpqZIS7lYBfcOonqINGoSPMyhVp3kHUjuQkc742KNTE0dpZpKos4e6Tftf4QCNJOjPPA6QrBuoXAkEAzU2GEBYE0e1x0TB11bMEn836NF08shf8XPvNldBGu6PxWkaQafWV/pmp+No26gtzK6coHQ6dHo0Jsh4+X9AgqQJAaARCN830FVaUEk6EK6oJamG9xCje/6SS2rkcILthi0ct9n9JCu5sTNWC1cEZQa3OkP7lVSQoaUm3A/gOGRzeJQJBALCIUq5B7852WRiTUviZvQfR/PL7/qZjHJSqlNdTD/oFUM7KjC/OJ6H2iHZmGdBLtg8FbPllfPBQfhhXAarGLAA=";
        //rawKey_pkcs1 = "MIICXAIBAAKBgQC0Y9rmhe4fIsFrrm0m/jrbfZsqxMYvg8qdvbSGHC9vnYm4K5p3bFBqqULAFlv2ZGjrWDFcBfa562E5hXtAoACXtsDH8WCkhfNiPkGQn3wNDGRpfYVup/F1LdceunSu0IYDP0MACzKY1S7KM2qJi8P8YlXZ91oyRfgb8lgqdQoXmQIDAQABAoGAIG90BM9AKckODlamucQswRqss9v95r1DyWk69IJM5Tzmbn8onyCStRsKLY/XqU4Ur3yEI4/O9U8lhDpEFzKt6ExkITkiVcVlRuK0lkAKS4Uu4W+YoPzNLwKoL3dS1my+wPOxgswJ+QPSrYfWoGDoDNiZctskcHprplkECYI9/aECQQDXvkZl9IHXRolUCQWfS5MFWVgPwaXNP3X3gXaqdfhoopIPsZphurd+hxDWforh+KNkdRejbbwIveGTxkXqxTe3AkEA1gzPz9IAzFPx8RjkOxgRag3g+Rp+35nuLw18Rt04EODinurCsg3t5k2rOx2Fic6GVozqM3B3p4ATr4GsyhALLwJAFi8vp/47d7p+FpES7e1kgdFNF9muxes3oyrB3Adjcsb8w/ZcTJ5Zjf4vgg7jExdxHbxYoOqAwllcH8jsmZaMAwJAXdJjzF3qR6WL6PfWIijciTXoJIf+kJfyFOG+VXllt9A6xRl1mYINguMMaO75t3u02n8NsNpdOCgleMpIKJF7tQJBALsDYb59npdnKL2V0zk5bZ7Nd40dy4KbRcXIG2Ll/Lo6CeY/6Jes868R0yyuyLGP5CcayMKVAhcVk3xEWI7dluU=";

        String rawKey_pkcs1 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTq1VS0G6B3qZDMgUwpOHDFjhMtOcOcGYi9bLpxLwfdjvIXOn/jT+TW6aT2Rzd3a5nyZaVJLY3XztG506pRrWEs54zndPp9+Tmt4HLIe2JIj2DLgS/ILCcX9ltN8X8rwOA7zsCoMMRk5pWWO7IyHHeVmlFMf9tYvTanWnmflrS5QIDAQAB";
        String rawKey_pkcs8 = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJOrVVLQboHepkMyBTCk4cMWOEy05w5wZiL1sunEvB92O8hc6f+NP5NbppPZHN3drmfJlpUktjdfO0bnTqlGtYSznjOd0+n35Oa3gcsh7YkiPYMuBL8gsJxf2W03xfyvA4DvOwKgwxGTmlZY7sjIcd5WaUUx/21i9NqdaeZ+WtLlAgMBAAECgYAzshRhtIQrROXRuk4k1GQQptO4zs/gtyqabIdg3TW6keql3xkwFKPAE6LCOYyogzXOMb1cowiDY/xdexaHnjBnI+w+VTbTd6cMxMdkJuu52KA68OwktdWTjnoxsNiRBNfMdV55UE3ktvF3VKT4A9Mw5Y+F6BZx3+jimpdq/PnCFQJBAPMPkJ7DHRvmBEbme0rRjVkMWwLHxPHVz+H4/JfBeEtOG74rMoGCsA9BvmEbb3bCcc3NnmKaPl+q9/DxCAku31cCQQCbh8WJZAJ3+AGmpL3BTsn2C/MTt0vcSBlb8qAxCVPqCBtYdjQAJs5u0fIoXCPDN2v9WbO8TMJOipuXZO8lU8YjAkBtKOthce1Lz0TSdsaednhNqs/g8skPd2ASc5k1qo0U/PJWuN3I7mGVqIgQ4ppvsqobrKHaVL14h7csvYNHZIanAkEAg+3funODRJCzldAYxmk3F+d7AjZxuyHNoRlmmTH4ZIUL7TYXVimU4pxkdafzDh1tFTOKKyhRip447WtJELjHUQJATFAA4c53Ucc9UwYP5RyZQlls8FsCzY4z2vKJMtbnuALQ9inLszo/AKMoBx91DSd/O/Pl/4hQ5HsOU8rwQ6K3aQ==";

        String formatKey1 = formatPkcs1ToPkcs8(rawKey_pkcs1);
        String formatKey2 = formatPkcs8ToPkcs1(rawKey_pkcs8);
        System.out.println(formatKey1);    //expect: abcdef
        System.out.println(formatKey2);
    }

}
