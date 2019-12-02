package encryption.jdk8;

import java.util.Base64;

public class Base64Util {

    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final Base64.Encoder encoder = Base64.getEncoder();

    /**
     * 编码
     * @param key
     * @return
     */
    public static String encode(byte[] key){
            return encoder.encodeToString(key);
    }

    /**
     * 解码
     * @param key
     * @return
     */
    public static byte[] decode(String key) {
        return decoder.decode(key);
    }

    public static void main(String[] args) throws Exception {
        String text = "字串文字";
        byte[] textByte = text.getBytes("UTF-8");

        //编码
        final String encodedText = encode(textByte);
        System.out.println(encodedText);
        //解码
        System.out.println(new String(decode(encodedText), "UTF-8"));
    }
}
