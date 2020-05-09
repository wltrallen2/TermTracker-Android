package com.fortysomethingnerd.android.termtracker.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.fortysomethingnerd.android.termtracker.database.AppRepository;
import com.fortysomethingnerd.android.termtracker.database.AssessmentEntity;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AssessmentDetailViewModel extends AndroidViewModel {
    private MutableLiveData<AssessmentEntity> liveAssessment = new MutableLiveData<>();
    private AppRepository appRepository;
    private Executor executor = Executors.newSingleThreadExecutor();

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

    public void saveAssessment(int courseId, String title, Date goalDate, boolean isGoalAlarmActive, Date dueDate) {
        AssessmentEntity assessment = liveAssessment.getValue();
        if (assessment == null) {

            if (TextUtils.isEmpty(title) || goalDate == null || dueDate == null
                || courseId < 1) { return; }

            assessment = new AssessmentEntity(courseId, title.trim(), goalDate, isGoalAlarmActive, dueDate);
        } else {
            assessment.setTitle(title);
            assessment.setGoalDate(goalDate);
            assessment.setDueDate(dueDate);
        }

        appRepository.insertAssessment(assessment);
    }

    public void deleteAssessment() {
        appRepository.deleteAssessment(liveAssessment.getValue());
    }
}
