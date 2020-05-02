package com.fortysomethingnerd.android.termtracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.fortysomethingnerd.android.termtracker.database.AppRepository;
import com.fortysomethingnerd.android.termtracker.database.AssessmentEntity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AssessmentDetailViewModel extends AndroidViewModel {
    private MutableLiveData<AssessmentEntity> assessment = new MutableLiveData<>();
    private AppRepository appRepository;
    private Executor executor = Executors.newSingleThreadExecutor();

    public AssessmentDetailViewModel(@NonNull Application application) {
        super(application);
        appRepository = AppRepository.getInstance(application.getApplicationContext());
    }

    public MutableLiveData<AssessmentEntity> getAssessment() {
        return assessment;
    }

    public void loadAssessment(int assessmentId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AssessmentEntity assessmentEntity = appRepository.getAssessementById(assessmentId);
                assessment.postValue(assessmentEntity);
            }
        });
    }
}
