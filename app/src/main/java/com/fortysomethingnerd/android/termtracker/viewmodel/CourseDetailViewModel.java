package com.fortysomethingnerd.android.termtracker.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.fortysomethingnerd.android.termtracker.database.AppRepository;
import com.fortysomethingnerd.android.termtracker.database.CourseEntity;
import com.fortysomethingnerd.android.termtracker.utilities.CourseStatus;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CourseDetailViewModel extends AndroidViewModel {
    private MutableLiveData<CourseEntity> liveCourse = new MutableLiveData<>();
    private AppRepository repository;
    private Executor executor = Executors.newSingleThreadExecutor();

    public CourseDetailViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getInstance(application.getApplicationContext());
    }

    public MutableLiveData<CourseEntity> getLiveCourse() {
        return liveCourse;
    }

    public void loadCourse(int courseId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                CourseEntity newCourse = repository.getCourseById(courseId);
                liveCourse.postValue(newCourse);
            }
        });
    }

    public void saveCourse(int termId, String title, Date end, CourseStatus status, String mentorName, String mentorPhone, String mentorEmail) {
        CourseEntity course = liveCourse.getValue();
        if (course == null) {

            if (TextUtils.isEmpty(title) || end == null || status == null
                    || TextUtils.isEmpty(mentorName) || TextUtils.isEmpty(mentorPhone)
                    || TextUtils.isEmpty(mentorEmail)) { return; }

            course = new CourseEntity(termId, title.trim(), end, status,
                    mentorName.trim(), mentorPhone.trim(), mentorEmail.trim());
        } else {
            course.setTitle(title.trim());
            course.setEnd(end);
            course.setStatus(status);
            course.setMentorName(mentorName.trim());
            course.setMentorEmail(mentorEmail.trim());
            course.setMentorPhone(mentorPhone.trim());
        }

        repository.insertCourse(course);
    }

    public void deleteCourse() {
        repository.deleteCourse(liveCourse.getValue());
    }
}
