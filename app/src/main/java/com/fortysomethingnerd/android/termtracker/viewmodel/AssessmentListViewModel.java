package com.fortysomethingnerd.android.termtracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.fortysomethingnerd.android.termtracker.database.AppRepository;
import com.fortysomethingnerd.android.termtracker.database.AssessmentEntity;
import com.fortysomethingnerd.android.termtracker.utilities.SampleData;

import java.util.List;

public class AssessmentListViewModel extends AndroidViewModel {
    private AppRepository appRepository;
    private LiveData<List<AssessmentEntity>> assessments;
    private LiveData<List<AssessmentEntity>> filteredAssessments;
    private MutableLiveData<Integer> filter = new MutableLiveData<>();

    public AssessmentListViewModel(@NonNull Application application) {
        super(application);
        appRepository = AppRepository.getInstance(getApplication());
        assessments = appRepository.mAssessments;
        filteredAssessments = Transformations
                .switchMap(filter, courseId -> appRepository.getAllAssessmentsForCourse(courseId));
    }

    public LiveData<List<AssessmentEntity>> getAssessments() {
        return assessments;
    }

    public void addSampleData(int courseId) {
        appRepository.addSampleDataForAssessments(courseId);
    }

    public void setFilter(int courseId) {
        filter.setValue(courseId);
    }

    public LiveData<List<AssessmentEntity>> getFilteredAssessments() {
        return filteredAssessments;
    }

    public void deleteAllAssessments(int courseId) {
        appRepository.deleteAllAssessmentsForCourse(courseId);
    }
}
