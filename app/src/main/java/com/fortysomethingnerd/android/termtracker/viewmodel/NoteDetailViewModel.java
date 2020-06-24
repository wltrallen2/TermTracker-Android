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
            // TODO: For a future version, handle all saveRecord cases so that...
            // the user is notified if a field is empty and given the chance to exit
            // without saving or to correct their error and attempt to save again.
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
