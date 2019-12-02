package encryption.jdk8;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

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

        String expiryStr = "2019-11-28 10:51:10";
        String currentStr = "2019-11-28 10:52:10";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date expiryDate = sdf.parse(expiryStr);
        Date currentDate = new Date(); //sdf.parse(currentStr);
        long time = System.currentTimeMillis() / 1000 + 2 * 24 * 60 * 60;

        System.out.println("expiryDate: " + expiryDate.getTime());
        System.out.println("currentDate: " + currentDate.getTime());
        System.out.println("currentDate >= expiryDate: " + (currentDate.getTime() >= expiryDate.getTime()));
        System.out.println();
    }
}
