package com.fortysomethingnerd.android.termtracker.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.fortysomethingnerd.android.termtracker.database.AppRepository;
import com.fortysomethingnerd.android.termtracker.database.NoteEntity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NoteDetailViewModel extends AndroidViewModel {

    MutableLiveData<NoteEntity> liveNote = new MutableLiveData<>();
    AppRepository repository;
    Executor executor = Executors.newSingleThreadExecutor();

    public NoteDetailViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getInstance(application.getApplicationContext());
    }

    public void loadNote(int noteId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                NoteEntity note = repository.getNoteById(noteId);
                liveNote.postValue(note);
            }
        });
    }

    public MutableLiveData<NoteEntity> getLiveNote() {
        return liveNote;
    }

    public void saveNote(int courseId, String title, String text) {
        NoteEntity note = liveNote.getValue();
        if (note == null) {
            // TODO: Handle these cases to signal user of error.
            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(text)) { return; }

            note = new NoteEntity(courseId, title.trim(), text.trim());
        } else {
            note.setTitle(title.trim());
            note.setText(text.trim());
        }

        repository.saveNote(note);
    }

    public void deleteNote() {
        repository.deleteNote(liveNote.getValue());
    }
}
