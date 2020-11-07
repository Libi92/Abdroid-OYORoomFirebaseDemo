package com.application.pglocator.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static String getDate(Long dateLong) {
        Date date = new Date(dateLong);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
        return df2.format(date);
    }
}
