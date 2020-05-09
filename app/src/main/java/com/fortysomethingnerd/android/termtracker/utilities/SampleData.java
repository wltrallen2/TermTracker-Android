package com.fortysomethingnerd.android.termtracker.utilities;

import com.fortysomethingnerd.android.termtracker.database.AssessmentEntity;
import com.fortysomethingnerd.android.termtracker.database.CourseEntity;
import com.fortysomethingnerd.android.termtracker.database.NoteEntity;
import com.fortysomethingnerd.android.termtracker.database.TermEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class SampleData {

    private static final String SAMPLE_TEXT_1 = "A simple note";
    private static final String SAMPLE_TEXT_2 = "A note with a\nline feed";
    private static final String SAMPLE_TEXT_3 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n\n" +
            "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?";

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
        courses.add(new CourseEntity(termId, "Mobile Applications", getDate(0), false, getDate(60), false,
                CourseStatus.IN_PROGRESS, "Michelle Heslop",
                "855-222-3874", "michelle@wgu.edu"));
        courses.add(new CourseEntity(termId, "Business Basics", getDate(60), false, getDate(120), false,
                CourseStatus.IN_PROGRESS, "Michelle Heslop",
                "855-222-3874", "michelle@wgu.edu"));
        courses.add(new CourseEntity(termId, "Capstone Project", getDate(120), false, getDate(240), false,
                CourseStatus.PLAN_TO_TAKE, "Michelle Heslop",
                "855-222-3874", "michelle@wgu.edu"));
        return courses;
    }

    public static List<AssessmentEntity> getAssessments(int courseId) {
        List<AssessmentEntity> assessments = new ArrayList<>();
        assessments.add(new AssessmentEntity(courseId, "Test #1", getDate(60), false, getDate(120)));
        assessments.add(new AssessmentEntity(courseId, "Test #2", getDate(90), false, getDate(150)));
        assessments.add(new AssessmentEntity(courseId, "Project: Website", getDate(120), false, getDate(180)));
        return assessments;
    }

    public static List<NoteEntity> getNotes(int courseId) {
        List<NoteEntity> notes = new ArrayList<>();
        notes.add(new NoteEntity(courseId, "Sample Note 1", SAMPLE_TEXT_1));
        notes.add(new NoteEntity(courseId, "Sample Note 2", SAMPLE_TEXT_2));
        notes.add(new NoteEntity(courseId, "Sample Note 3", SAMPLE_TEXT_3));
        return notes;
    }
}
