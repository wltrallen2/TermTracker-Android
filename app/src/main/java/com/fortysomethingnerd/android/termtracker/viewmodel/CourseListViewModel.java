package com.fortysomethingnerd.android.termtracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.fortysomethingnerd.android.termtracker.database.AppRepository;
import com.fortysomethingnerd.android.termtracker.database.CourseEntity;
import com.fortysomethingnerd.android.termtracker.utilities.SampleData;

import java.util.ArrayList;
import java.util.List;

public class CourseListViewModel extends AndroidViewModel {
    public LiveData<List<CourseEntity>> courses;
    private AppRepository repository;

    public CourseListViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getInstance(getApplication());
        courses = repository.mCourses; // TODO: Is this right?
    }

    public void addSampleData(int termId) {
        repository.addSampleDataForCourses(termId);
    }
}
