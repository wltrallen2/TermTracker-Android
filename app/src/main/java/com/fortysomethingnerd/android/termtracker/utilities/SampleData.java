package com.fortysomethingnerd.android.termtracker.utilities;

import com.fortysomethingnerd.android.termtracker.database.AssessmentEntity;
import com.fortysomethingnerd.android.termtracker.database.CourseEntity;
import com.fortysomethingnerd.android.termtracker.database.TermEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class SampleData {

    private static Date getDate(int diff) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.DATE, diff);
        return cal.getTime();
    }

    public static List<TermEntity> getTerms() {
        List<TermEntity> terms = new ArrayList<>();
        terms.add(new TermEntity("Fall 2018", getDate(0), getDate(180)));
        terms.add(new TermEntity("Spring 2019", getDate(181), getDate(181 + 180)));
        terms.add(new TermEntity("Fall 2019", getDate(361), getDate(361 + 180)));
        return terms;
    }

    public static List<CourseEntity> getCourses(int termId) {
        List<CourseEntity> courses = new ArrayList<>();
        courses.add(new CourseEntity(termId, "Mobile Applications", getDate(0), getDate(60),
                CourseStatus.IN_PROGRESS, "Michelle Heslop",
                "855-222-3874", "michelle@wgu.edu"));
        courses.add(new CourseEntity(termId, "Business Basics", getDate(60), getDate(120),
                CourseStatus.IN_PROGRESS, "Michelle Heslop",
                "855-222-3874", "michelle@wgu.edu"));
        courses.add(new CourseEntity(termId, "Capstone Project", getDate(120), getDate(240),
                CourseStatus.PLAN_TO_TAKE, "Michelle Heslop",
                "855-222-3874", "michelle@wgu.edu"));
        return courses;
    }

    public static List<AssessmentEntity> getAssessments(int courseId) {
        List<AssessmentEntity> assessments = new ArrayList<>();
        assessments.add(new AssessmentEntity(courseId, "Test #1", getDate(60), getDate(120)));
        assessments.add(new AssessmentEntity(courseId, "Test #2", getDate(90), getDate(150)));
        assessments.add(new AssessmentEntity(courseId, "Project: Website", getDate(120), getDate(180)));
        return assessments;
    }
}
