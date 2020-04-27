package com.fortysomethingnerd.android.termtracker.database;

import androidx.room.TypeConverter;

import com.fortysomethingnerd.android.termtracker.utilities.CourseStatus;

public class CourseStatusConverter {

    @TypeConverter
    public String toString(CourseStatus status) {
        return status.toString();
    }

    @TypeConverter
    public CourseStatus toCourseStatus(String s) {
        return CourseStatus.get(s);
    }
}
