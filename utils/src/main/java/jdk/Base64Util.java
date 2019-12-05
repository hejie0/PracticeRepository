package jdk;

import java.util.Base64;

public class Base64Util {

    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final Base64.Encoder encoder = Base64.getEncoder();

    /**
     * 编码
     * @param data
     * @return
     */
    public static byte[] encode(byte[] data){
        return encoder.encode(data);
    }

    public static String encodeToString(byte[] data){
        return encoder.encodeToString(data);
    }

    /**
     * 解码
     * @param data
     * @return
     */
    public static byte[] decode(byte[] data) {
        return decoder.decode(data);
    }

    public static byte[] decode(String data) {
        byte[] bytes = data.getBytes();
        return decoder.decode(bytes);
    }

    public static void main(String[] args) throws Exception {
        String text = "字串文字";
        byte[] textByte = text.getBytes("UTF-8");

        //编码
        final String encodedText = encodeToString(textByte);
        System.out.println(encodedText);
        //解码
        System.out.println(new String(decode(encodedText)));
    }
}
