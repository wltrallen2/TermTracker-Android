package com.fortysomethingnerd.android.termtracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.fortysomethingnerd.android.termtracker.database.AppRepository;
import com.fortysomethingnerd.android.termtracker.database.AssessmentEntity;
import com.fortysomethingnerd.android.termtracker.utilities.SampleData;

import java.util.List;

public class AssessmentListViewModel extends AndroidViewModel {
    private List<AssessmentEntity> assessments;
    private AppRepository appRepository;

    public AssessmentListViewModel(@NonNull Application application) {
        super(application);
        appRepository = AppRepository.getInstance(getApplication());
        assessments = appRepository.mAssessments;
    }

    public List<AssessmentEntity> getAssessments() {
        return assessments;
    }

    public void addSampleData(int courseId) {
        appRepository.addSampleDataForAssessments(courseId);
    }
}
