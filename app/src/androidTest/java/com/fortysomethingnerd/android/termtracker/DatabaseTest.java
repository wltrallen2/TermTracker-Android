package com.fortysomethingnerd.android.termtracker;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.fortysomethingnerd.android.termtracker.database.AppDatabase;
import com.fortysomethingnerd.android.termtracker.database.CourseDao;
import com.fortysomethingnerd.android.termtracker.database.CourseEntity;
import com.fortysomethingnerd.android.termtracker.database.TermDao;
import com.fortysomethingnerd.android.termtracker.database.TermEntity;
import com.fortysomethingnerd.android.termtracker.utilities.SampleData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    public static final String TAG = "Junit";
    private AppDatabase mDb;
    private TermDao termDao;
    private CourseDao courseDao;

    private int termId;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        termDao = mDb.termDao();
        courseDao = mDb.courseDao();
        int termId = 1;
        Log.i(TAG, "createDb");
    }

    private void loadSampleCourses() {
        courseDao.insertAll(SampleData.getCourses(termId));
    }

    @After
    public void closeDb() {
        mDb.close();
        Log.i(TAG, "closeDb");
    }

    @Test
    public void createAndCountItems() {
        loadSampleCourses();
        int count = courseDao.getCount();
        Log.i(TAG, "createAndCountItems: count = " + count);
        assertEquals(SampleData.getCourses(termId).size(), count);
    }

    @Test
    public void compareString() {
        loadSampleCourses();
        CourseEntity original = SampleData.getCourses(termId).get(0);
        CourseEntity fromDb = courseDao.getCourseById(1);
        assertEquals(original.getTitle(), fromDb.getTitle());
        assertEquals(1, fromDb.getId());
    }

    @Test
    public void compareTermId() {
        loadSampleCourses();
        CourseEntity original = SampleData.getCourses(termId).get(0);
        CourseEntity fromDb = courseDao.getCourseById(1);
        assertEquals(original.getTermId(), fromDb.getTermId());
    }

}
