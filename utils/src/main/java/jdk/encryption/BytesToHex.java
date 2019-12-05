package jdk.encryption;

public class BytesToHex {

    /**
     * byte数组转为16进制
     * @param bytes
     * @return
     */
    public static String fromBytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < bytes.length; i++){
            String str = Integer.toHexString(0xFF & bytes[i]);
            if(str.length() == 1){
                builder.append("0").append(str);
            }else {
                builder.append(str);
            }
        }
        return builder.toString();
    }

}
