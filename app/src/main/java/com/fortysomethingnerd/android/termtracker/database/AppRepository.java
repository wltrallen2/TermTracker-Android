package com.fortysomethingnerd.android.termtracker.database;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.fortysomethingnerd.android.termtracker.utilities.SampleData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.LOG_TAG;

public class AppRepository {
    private static AppRepository ourInstance;

    public LiveData<List<TermEntity>> mTerms;
    public LiveData<List<CourseEntity>> mCourses;
    public LiveData<List<AssessmentEntity>> mAssessments;
    public LiveData<List<NoteEntity>> mNotes;

    private AppDatabase mDb;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

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
        mAssessments = getAllAssessments();
        mNotes = getAllNotes();
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

    public long insertTerm(TermEntity term) {
        Callable<Long> callable = new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return mDb.termDao().insertTerm(term);
            }
        };

        Future<Long> future = executor.submit(callable);
        long termId = -1;
        try {
            termId = future.get();
        } catch (ExecutionException e) {
            Log.i(LOG_TAG, "AppRepository.insertTerm: ExecutionException");
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.i(LOG_TAG, "AppRepository.insertTerm: InterruptionException");
            e.printStackTrace();
        }

        return termId;
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

    public long insertCourse(CourseEntity course) {
        Callable<Long> callable = new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return mDb.courseDao().insertCourse(course);
            }
        };

        Future<Long> future = executor.submit(callable);
        long courseId = -1;
        try {
            courseId = future.get();
        } catch (ExecutionException e) {
            // TODO: Handle this exception.
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO: Handle this exception.
            e.printStackTrace();
        }

        return courseId;
    }

    public void deleteCourse(CourseEntity course) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.courseDao().deleteCourse(course);
            }
        });
    }

    private LiveData<List<AssessmentEntity>> getAllAssessments() {
        return mDb.assessmentDao().getAll();
    }

    public void addSampleDataForAssessments(int courseId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.assessmentDao().insertAll(SampleData.getAssessments(courseId));
            }
        });
    }

    public LiveData<List<AssessmentEntity>> getAllAssessmentsForCourse(Integer courseId) {
        return mDb.assessmentDao().getAssessmentsForCourseId(courseId);
    }

    public void deleteAllAssessmentsForCourse(int courseId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.assessmentDao().deleteAssessmentsForCourseId(courseId);
            }
        });
    }

    public AssessmentEntity getAssessementById(int assessmentId) {
        return mDb.assessmentDao().getAssessmentById(assessmentId);
    }

    public void insertAssessment(AssessmentEntity assessment) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.assessmentDao().insertAssessment(assessment);
            }
        });
    }

    public void deleteAssessment(AssessmentEntity assessment) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.assessmentDao().deleteAssessment(assessment);
            }
        });
    }

    public void addSampleDataForNotes(int courseId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.notesDao().insertAll(SampleData.getNotes(courseId));
            }
        });
    }

    public LiveData<List<NoteEntity>> getAllNotesForCourseId(int courseId) {
        return mDb.notesDao().getAllNotesForCourseId(courseId);
    }

    public LiveData<List<NoteEntity>> getAllNotes() {
        return mDb.notesDao().getAllNotes();
    }

    public void deleteAllNotesForCourseId(int courseId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.notesDao().deleteAllNotesForCourseId(courseId);
            }
        });
    }

    public NoteEntity getNoteById(int noteId) {
        return mDb.notesDao().getNoteById(noteId);
    }

    public void saveNote(NoteEntity note) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.notesDao().insertNote(note);
            }
        });
    }

    public void deleteNote(NoteEntity note) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.notesDao().deleteNote(note);
            }
        });
    }

    public boolean isSafeToDeleteTerm(TermEntity term) {
        Callable<Boolean> callable = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                int count = mDb.courseDao().getCountOfCoursesForTermId(term.getId());
                return count == 0;
            }
        };

        Future<Boolean> future = executor.submit(callable);

        boolean safeToDelete = false;
        try {
            safeToDelete = future.get();
        } catch (ExecutionException e) {
            // TODO: Handle these exceptions.
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return safeToDelete;
    }
}