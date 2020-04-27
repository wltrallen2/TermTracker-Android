package com.fortysomethingnerd.android.termtracker.database;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.DATE_FORMAT_CHAR_SEQUENCE;

public class DateConverter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    public static Date parseStringToDate(String s) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_CHAR_SEQUENCE);
        return sdf.parse(s);
    }

    public static String parseDateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_CHAR_SEQUENCE);
        return sdf.format(date);
    }
}
