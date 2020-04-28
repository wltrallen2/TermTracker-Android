package com.fortysomethingnerd.android.termtracker.utilities;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public enum CourseStatus {
    PLAN_TO_TAKE("Plan to Take"),
    IN_PROGRESS("In Progress"),
    DROPPED("Dropped"),
    COMPLETED("Completed");

    private String status;

    CourseStatus(String status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return status;
    }

    /*
     * Reverse Lookup Implementation
     */
    private static Map<String, CourseStatus> lookup = new HashMap<>();

    static {
        for(CourseStatus status : CourseStatus.values()) {
            lookup.put(status.toString(), status);
        }
    }

    public static CourseStatus get(String s) {
        return lookup.get(s);
    }

    public static String[] getCourseStatusStrings() {
        String[] statuses = new String[] {
                CourseStatus.PLAN_TO_TAKE.toString(),
                CourseStatus.IN_PROGRESS.toString(),
                CourseStatus.COMPLETED.toString(),
                CourseStatus.DROPPED.toString()
        };

        return statuses;
    }
}
