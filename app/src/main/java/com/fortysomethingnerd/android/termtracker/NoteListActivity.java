package com.fortysomethingnerd.android.termtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fortysomethingnerd.android.termtracker.database.NoteEntity;
import com.fortysomethingnerd.android.termtracker.ui.NotesAdapter;
import com.fortysomethingnerd.android.termtracker.viewmodel.NoteListViewModel;

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
    private NoteListViewModel viewModel;

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
        initViewModel();
        initRecyclerView();
    }

    private void setCourseId() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            onBackPressed();
        } else {
            courseId = extras.getInt(COURSE_ID_KEY);
        }
    }

    private void initViewModel() {
        Observer<List<NoteEntity>> observer = new Observer<List<NoteEntity>>() {
            @Override
            public void onChanged(List<NoteEntity> noteEntities) {
                notesData.clear();
                notesData.addAll(noteEntities);

                if(adapter == null) {
                    adapter = new NotesAdapter(notesData, NoteListActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        };

        viewModel = this.getDefaultViewModelProviderFactory().create(NoteListViewModel.class);
        viewModel.setFilter(courseId);
        viewModel.getFilteredNotes().observe(this, observer);
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration divider =
                new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_add_sample_notes) {
            viewModel.addSampleNotes(courseId);
        } else if (item.getItemId() == R.id.action_delete_all_notes) {
            viewModel.deleteAllNotesForCourseId(courseId);
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
