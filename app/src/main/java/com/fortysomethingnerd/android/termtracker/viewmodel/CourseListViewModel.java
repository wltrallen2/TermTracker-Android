package com.fortysomethingnerd.android.termtracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.fortysomethingnerd.android.termtracker.database.AppRepository;
import com.fortysomethingnerd.android.termtracker.database.CourseEntity;

import java.util.List;

public class CourseListViewModel extends AndroidViewModel {
    private LiveData<List<CourseEntity>> courses;
    private LiveData<List<CourseEntity>> filteredCourses;
    private MutableLiveData<Integer> filter = new MutableLiveData<>();

    private AppRepository repository;

    public CourseListViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getInstance(application.getApplicationContext());
        courses = repository.mCourses;
        filteredCourses = Transformations
                .switchMap(filter, termId -> repository.getAllCoursesForTerm(termId));
    }

    public LiveData<List<CourseEntity>> getFilteredCourses() {
        return filteredCourses;
    }

    public void setFilter(Integer termId) {
        filter.setValue(termId);
    }

    public LiveData<List<CourseEntity>> getAllCourses() {
        return courses;
    }

    public void addSampleData(int termId) {
        repository.addSampleDataForCourses(termId);
    }

    public void deleteAllCoursesForTerm(int termId) {
        repository.deleteAllCoursesForTerm(termId);
    }
}
