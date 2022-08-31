package com.kee0kai.thekey.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class TimeFormats {

    public static DateFormat simpleDateFormat() {
        DateFormat formatter = SimpleDateFormat.getDateInstance();
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter;
    }



}
