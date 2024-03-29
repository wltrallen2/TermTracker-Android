package com.fortysomethingnerd.android.termtracker;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.fortysomethingnerd.android.termtracker.database.AppDatabase;
import com.fortysomethingnerd.android.termtracker.database.AssessmentDao;
import com.fortysomethingnerd.android.termtracker.database.AssessmentEntity;
import com.fortysomethingnerd.android.termtracker.database.CourseDao;
import com.fortysomethingnerd.android.termtracker.database.NoteDao;
import com.fortysomethingnerd.android.termtracker.database.NoteEntity;
import com.fortysomethingnerd.android.termtracker.database.TermDao;
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
    private AssessmentDao assessmentDao;
    private NoteDao noteDao;

    private int termId;
    private int courseId;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        termDao = mDb.termDao();
        courseDao = mDb.courseDao();
        assessmentDao = mDb.assessmentDao();
        noteDao = mDb.notesDao();
        termId = 1;
        courseId = 1;
        Log.i(TAG, "createDb");
    }

    private void loadSampleNotes() {
        noteDao.insertAll(SampleData.getNotes(courseId));
    }

    @After
    public void closeDb() {
        mDb.close();
        Log.i(TAG, "closeDb");
    }

    @Test
    public void createAndCountItems() {
        loadSampleNotes();
        int count =  noteDao.getCount();
        Log.i(TAG, "createAndCountItems: count = " + count);
        assertEquals(SampleData.getNotes(courseId).size(), count);
    }

    @Test
    public void compareString() {
        loadSampleNotes();
        NoteEntity original = SampleData.getNotes(courseId).get(0);
        NoteEntity fromDb = noteDao.getNoteById(1);
        assertEquals(original.getTitle(), fromDb.getTitle());
        assertEquals(1, fromDb.getId());
    }

    @Test
    public void compareCourseId() {
        loadSampleNotes();
        NoteEntity original = SampleData.getNotes(courseId).get(0);
        NoteEntity fromDb = noteDao.getNoteById(1);
        assertEquals(original.getCourseId(), fromDb.getCourseId());
    }

}
