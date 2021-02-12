package util;

import me.xethh.utils.dateManipulation.BaseTimeZone;
import me.xethh.utils.dateManipulation.DateBuilder;
import me.xethh.utils.dateManipulation.DateFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    /**
     * 对象转数组
     * @param obj
     * @return
     */
    public static byte[] toByteArray (Object obj) {
        byte[] bytes = null;
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);) {

            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray ();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 数组转对象
     * @param bytes
     * @return
     */
    public static Object toObject (byte[] bytes) {
        Object obj = null;
        try(ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);) {

            obj = ois.readObject();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    public static void close(AutoCloseable... closeableList) {
        if (closeableList == null || closeableList.length == 0) {
            return;
        }

        for (AutoCloseable c : closeableList) {
            try {
                if (c != null) c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param dateTime 20200313130536
     * @return Calendar
     * @throws ParseException
     */
    public static Calendar parseDateTime (String dateTime) throws ParseException {
        System.out.println(dateTime);
        String stringFormat = "";
        if (dateTime.contains(".")) {
            stringFormat = "yyyyMMddHHmmss.SSS";
        } else {
            stringFormat = "yyyyMMddHHmm";
        }
        Date time = new SimpleDateFormat(stringFormat).parse(dateTime);
        DateBuilder builder = DateFactory.from(time);
        builder.timeZone(BaseTimeZone.GMT);
        return builder.asCalendar();
    }
}
