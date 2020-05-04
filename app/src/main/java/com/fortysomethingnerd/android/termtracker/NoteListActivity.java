package com.fortysomethingnerd.android.termtracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fortysomethingnerd.android.termtracker.R;
import com.fortysomethingnerd.android.termtracker.database.NoteEntity;
import com.fortysomethingnerd.android.termtracker.ui.NotesAdapter;
import com.fortysomethingnerd.android.termtracker.utilities.Constants;
import com.fortysomethingnerd.android.termtracker.utilities.SampleData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.COURSE_ID_KEY;

public class NoteListActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private int courseId;
    private List<NoteEntity> notesData = new ArrayList<>();
    private NotesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_list_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_note_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        setCourseId();
        initRecyclerView();

        notesData.addAll(SampleData.getNotes(courseId));
        for(NoteEntity note : notesData) {
            Log.i(Constants.LOG_TAG, "NoteListActivity.onCreate: " + note);
        }
    }

    private void setCourseId() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            courseId = extras.getInt(COURSE_ID_KEY);
        }
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration divider =
                new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);

        adapter = new NotesAdapter(notesData, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.noteListFab)
    public void newNoteClickHandler() {
        Intent intent = new Intent(this, NoteDetailActivity.class);
        intent.putExtra(COURSE_ID_KEY, courseId);
        startActivity(intent);
    }
}
