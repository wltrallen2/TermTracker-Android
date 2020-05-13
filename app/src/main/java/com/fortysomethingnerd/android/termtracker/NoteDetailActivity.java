package com.fortysomethingnerd.android.termtracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import com.fortysomethingnerd.android.termtracker.database.NoteEntity;
import com.fortysomethingnerd.android.termtracker.utilities.UtilityMethods;
import com.fortysomethingnerd.android.termtracker.viewmodel.NoteDetailViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.COURSE_ID_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.EDITING_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.NOTE_ID_KEY;

public class NoteDetailActivity extends AppCompatActivity {

    @BindView(R.id.note_detail_title_text_view)
    TextView titleTextView;

    @BindView(R.id.note_detail_text_view)
    TextView textView;

    @BindView(R.id.fab_share_note)
    FloatingActionButton fabShareNote;

    private NoteDetailViewModel viewModel;
    private boolean isNewNote, isEdited;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_detail_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            isEdited = savedInstanceState.getBoolean(EDITING_KEY);
        }

        initViewModel();
    }

    @Override
    protected void onResume() {
        setOptionsForNoteActivity();
        UtilityMethods.hideKeyboard(this);
        super.onResume();
    }

    private void initViewModel() {
        Observer<NoteEntity> observer = new Observer<NoteEntity>() {
            @Override
            public void onChanged(NoteEntity noteEntity) {
                if (noteEntity != null && !isEdited) {
                    titleTextView.setText(noteEntity.getTitle());
                    textView.setText(noteEntity.getText());
                }
            }
        };

        viewModel = this.getDefaultViewModelProviderFactory()
                .create(NoteDetailViewModel.class);
        viewModel.getLiveNote().observe(this, observer);
    }

    private void setOptionsForNoteActivity() {
        int noteId = -1;

        String title = "";
        Bundle extras = getIntent().getExtras();
        if (extras.containsKey(NOTE_ID_KEY)) {
            title = getString(R.string.edit_note);
            noteId = extras.getInt(NOTE_ID_KEY);

            viewModel.loadNote(noteId);
            isNewNote = false;
        } else {
            title = getString(R.string.new_note);
            isNewNote = true;
        }

        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        UtilityMethods.setTitleForCollapsingToolbarLayout(this, toolbarLayout, title);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(!isNewNote) {
            getMenuInflater().inflate(R.menu.menu_note_detail, menu);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_delete_note) {
            viewModel.deleteNote();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        saveAndReturn();
        finish();
        super.onBackPressed();
    }

    private void saveAndReturn() {
        UtilityMethods.hideKeyboard(this);
        Bundle extras = getIntent().getExtras();
        int courseId = extras.getInt(COURSE_ID_KEY);
        String title = titleTextView.getText().toString();
        String text = textView.getText().toString();

        viewModel.saveNote(courseId, title, text);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(EDITING_KEY, true);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.fab_share_note)
    public void shareNoteClickHandler() {
        Intent intentToSend = new Intent();
        intentToSend.setAction(Intent.ACTION_SEND);
        intentToSend.setType("message/rfc822");
        intentToSend.putExtra(Intent.EXTRA_TEXT, textView.getText());

        Intent intentToShare = Intent.createChooser(intentToSend, "Share Notes");
        startActivity(intentToShare);
    }
}
