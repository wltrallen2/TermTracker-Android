package com.fortysomethingnerd.android.termtracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.fortysomethingnerd.android.termtracker.database.AppRepository;
import com.fortysomethingnerd.android.termtracker.database.NoteEntity;

import java.util.List;

public class NoteListViewModel extends AndroidViewModel {
    private AppRepository repository;
    private LiveData<List<NoteEntity>> notes;
    private LiveData<List<NoteEntity>> filteredNotes;
    private MutableLiveData<Integer> filter = new MutableLiveData<>();

    public NoteListViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getInstance(application.getApplicationContext());
        notes = repository.mNotes;
        filteredNotes = Transformations
                .switchMap(filter, courseId -> repository.getAllNotesForCourseId(courseId));
    }

    public LiveData<List<NoteEntity>> getNotes() {
        return notes;
    }

    public void setFilter(int courseId) {
        filter.setValue(courseId);
    }

    public LiveData<List<NoteEntity>> getFilteredNotes() {
        return filteredNotes;
    }

    public void addSampleNotes(int courseId) {
        repository.addSampleDataForNotes(courseId);
    }

    public void deleteAllNotesForCourseId(int courseId) {
        repository.deleteAllNotesForCourseId(courseId);
    }
}
