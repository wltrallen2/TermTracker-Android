package com.fortysomethingnerd.android.termtracker.utilities;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public enum AssessmentType {
    OBJECTIVE("Objective"),
    PERFORMANCE("Performance");

    private String type;

    AssessmentType(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return type;
    }

    /*
     * Reverse Lookup Implementation
     */
    private static Map<String, AssessmentType> lookup = new HashMap<>();

    static {
        for(AssessmentType type : AssessmentType.values()) {
            lookup.put(type.toString(), type);
        }
    }

    public static AssessmentType get(String s) {
        return lookup.get(s);
    }

    public static String[] getAssessmentTypeStrings() {
        String[] types = new String[] {
                            AssessmentType.OBJECTIVE.toString(),
                            AssessmentType.PERFORMANCE.toString()
                            };

        return types;
    }
}
