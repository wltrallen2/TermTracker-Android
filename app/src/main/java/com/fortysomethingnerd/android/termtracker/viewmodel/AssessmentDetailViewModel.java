package com.fortysomethingnerd.android.termtracker.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.fortysomethingnerd.android.termtracker.database.AppRepository;
import com.fortysomethingnerd.android.termtracker.database.AssessmentEntity;
import com.fortysomethingnerd.android.termtracker.database.CourseEntity;
import com.fortysomethingnerd.android.termtracker.utilities.AssessmentType;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AssessmentDetailViewModel extends AndroidViewModel {
    private MutableLiveData<AssessmentEntity> liveAssessment = new MutableLiveData<>();
    private AppRepository appRepository;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public AssessmentDetailViewModel(@NonNull Application application) {
        super(application);
        appRepository = AppRepository.getInstance(application.getApplicationContext());
    }

    public MutableLiveData<AssessmentEntity> getLiveAssessment() {
        return liveAssessment;
    }

    public void loadAssessment(int assessmentId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AssessmentEntity assessmentEntity = appRepository.getAssessementById(assessmentId);
                liveAssessment.postValue(assessmentEntity);
            }
        });
    }

    public long saveAssessment(int courseId, String title, AssessmentType type, Date goalDate, boolean isGoalAlarmActive, Date dueDate, boolean isDueAlarmActive) {
        AssessmentEntity assessment = liveAssessment.getValue();
        if (assessment == null) {

            if (TextUtils.isEmpty(title) || goalDate == null || dueDate == null
                || courseId < 1) { return -1; }

            assessment = new AssessmentEntity(courseId, title.trim(), type, goalDate, isGoalAlarmActive, dueDate, isDueAlarmActive);
        } else {
            assessment.setTitle(title);
            assessment.setType(type);
            assessment.setGoalDate(goalDate);
            assessment.setGoalAlarmActive(isGoalAlarmActive);
            assessment.setDueDate(dueDate);
            assessment.setDueAlarmActive(isDueAlarmActive);
        }

        return appRepository.insertAssessment(assessment);
    }

    public void deleteAssessment() {
        appRepository.deleteAssessment(liveAssessment.getValue());
    }

    public CourseEntity getCourseForAssessment() {
        return appRepository.getCourseById(liveAssessment.getValue().getCourseId());
    }

    public CourseEntity getCourseForCourseId(int courseId) {
        return appRepository.getCourseById(courseId);
    }
}
