package com.fortysomethingnerd.android.termtracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.fortysomethingnerd.android.termtracker.database.CourseEntity;
import com.fortysomethingnerd.android.termtracker.ui.CoursesAdapter;
import com.fortysomethingnerd.android.termtracker.utilities.Constants;
import com.fortysomethingnerd.android.termtracker.utilities.SampleData;
import com.fortysomethingnerd.android.termtracker.viewmodel.CourseListViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.LOG_TAG;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TERM_ID_KEY;

public class CourseListActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private int termId;
    private List<CourseEntity> courseData = new ArrayList<>();
    private CoursesAdapter adapter;
    private CourseListViewModel viewModel;

    @OnClick(R.id.courseDetailFab)
    void courseDetailClickHandler() {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_list_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_course_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        setTermId();
        initRecyclerView();
        initViewModel();
    }

    private void initViewModel() {
        final Observer<List<CourseEntity>> observer = new Observer<List<CourseEntity>>() {
            @Override
            public void onChanged(List<CourseEntity> courseEntities) {
                courseData.clear();
                courseData.addAll(courseEntities);

                if (adapter == null) {
                    adapter = new CoursesAdapter(courseData, CourseListActivity.this);
                    mRecyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        };

        viewModel = this.getDefaultViewModelProviderFactory().create(CourseListViewModel.class);
        viewModel.courses.observe(this, observer);
    }

    private void setTermId() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(TERM_ID_KEY)) {
                termId = extras.getInt(TERM_ID_KEY);
            }
        }
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_add_sample_course_data) {
            addSampleData();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addSampleData() {
        viewModel.addSampleData(termId);
    }
}
