package com.fortysomethingnerd.android.termtracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.fortysomethingnerd.android.termtracker.database.AssessmentEntity;
import com.fortysomethingnerd.android.termtracker.utilities.SampleData;

import java.util.List;

public class AssessmentListViewModel extends AndroidViewModel {
    private List<AssessmentEntity> assessments = SampleData.getAssessments(1);

    public AssessmentListViewModel(@NonNull Application application) {
        super(application);
    }

    public List<AssessmentEntity> getAssessments() {
        return assessments;
    }
}
