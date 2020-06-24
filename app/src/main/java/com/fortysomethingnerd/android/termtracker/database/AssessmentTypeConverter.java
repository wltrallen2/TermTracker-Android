package com.fortysomethingnerd.android.termtracker.database;

import androidx.room.TypeConverter;

import com.fortysomethingnerd.android.termtracker.utilities.AssessmentType;

public class AssessmentTypeConverter {

    @TypeConverter
    public String toString(AssessmentType type) {
        return type.toString();
    }

    @TypeConverter
    public AssessmentType toAssessmentType(String s) {
        return AssessmentType.get(s);
    }
}
