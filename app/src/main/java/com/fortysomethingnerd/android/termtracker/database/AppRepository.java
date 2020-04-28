package com.fortysomethingnerd.android.termtracker.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.fortysomethingnerd.android.termtracker.utilities.SampleData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppRepository {
    private static AppRepository ourInstance;

    public LiveData<List<TermEntity>> mTerms;
    public LiveData<List<CourseEntity>> mCourses;

    private AppDatabase mDb;
    private Executor executor = Executors.newSingleThreadExecutor();

    public static AppRepository getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new AppRepository(context);
        }

        return ourInstance;
    }

    private AppRepository(Context context) {
        mDb = AppDatabase.getInstance(context);
        mTerms = getAllTerms();
        mCourses = getAllCourses();
    }

    public void addSampleDataForTerms() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.termDao().insertAll(SampleData.getTerms());
            }
        });
    }

    private LiveData<List<TermEntity>> getAllTerms() {
        // LiveData in conjunction with Room library does not require the use of an Executor
        // because Room does the background threading automatically.
        return mDb.termDao().getAll();
    }

    public void deleteAllTerms() {
        // If a query inside a dao returns a LiveData object, Room library will handle background
        // threading for you, but if it returns anything else (int, boolean, etc.), you have to
        // handle background threading explicitly.
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.termDao().deleteAll();
            }
        });
    }

    public TermEntity getTermById(int termId) {
        return mDb.termDao().getTermById(termId);
    }

    public void insertTerm(TermEntity term) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.termDao().insertTerm(term);
            }
        });
    }

    public void deleteTerm(TermEntity term) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.termDao().deleteTerm(term);
            }
        });
    }

    public void addSampleDataForCourses(int termId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.courseDao().insertAll(SampleData.getCourses(termId));
            }
        });
    }

    public LiveData<List<CourseEntity>> getAllCourses() {
        return mDb.courseDao().getAll();
    }

    public LiveData<List<CourseEntity>> getAllCoursesForTerm(int termId) {
        return mDb.courseDao().getAllCoursesForTerm(termId);
    }

    public void deleteAllCoursesForTerm(int termId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.courseDao().deleteAllCoursesInTerm(termId);
            }
        });
    }

    public CourseEntity getCourseById(int courseId) {
        return mDb.courseDao().getCourseById(courseId);
    }

    public void insertCourse(CourseEntity course) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.courseDao().insertCourse(course);
            }
        });
    }

    public void deleteCourse(CourseEntity course) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.courseDao().deleteCourse(course);
            }
        });
    }
}