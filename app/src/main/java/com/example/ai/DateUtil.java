package com.example.ai;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

        public static String dateToString(Date date) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            return df.format(date);
        }
}
