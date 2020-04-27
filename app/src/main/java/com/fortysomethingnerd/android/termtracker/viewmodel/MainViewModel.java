package com.fortysomethingnerd.android.termtracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.fortysomethingnerd.android.termtracker.database.AppRepository;
import com.fortysomethingnerd.android.termtracker.database.TermEntity;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    public LiveData<List<TermEntity>> mTerms;
    private AppRepository mRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);

        mRepository = AppRepository.getInstance(application.getApplicationContext());
        mTerms = mRepository.mTerms;
    }

    public void addSampleData() {
        mRepository.addSampleDataForTerms();
    }

    public void deleteAllNotes() {
        mRepository.deleteAllTerms();
    }
}
