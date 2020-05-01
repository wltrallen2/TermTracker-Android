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

import com.fortysomethingnerd.android.termtracker.database.AssessmentEntity;
import com.fortysomethingnerd.android.termtracker.ui.AssessmentsAdapter;
import com.fortysomethingnerd.android.termtracker.utilities.SampleData;
import com.fortysomethingnerd.android.termtracker.viewmodel.AssessmentListViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.COURSE_ID_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.LOG_TAG;

public class AssessmentListActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private int courseId;
    private List<AssessmentEntity> assessmentData = new ArrayList<>();
    AssessmentsAdapter adapter;
    AssessmentListViewModel viewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assessment_list_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_assessment_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        setCourseId();
        initRecyclerView();
        initViewModel();
    }

    private void setCourseId() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            courseId = extras.getInt(COURSE_ID_KEY);
        }
    }

    private void initViewModel() {
        final Observer<List<AssessmentEntity>> observer = new Observer<List<AssessmentEntity>>() {
            @Override
            public void onChanged(List<AssessmentEntity> assessmentEntities) {
                assessmentData.clear();
                assessmentData.addAll(assessmentEntities);

                if (adapter == null) {
                    adapter = new AssessmentsAdapter(assessmentData, AssessmentListActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        };

        viewModel = this.getDefaultViewModelProviderFactory().create(AssessmentListViewModel.class);
        viewModel.setFilter(courseId);
        viewModel.getFilteredAssessments().observe(this, observer);
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_assessment_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_add_sample_assessment_data) {
            viewModel.addSampleData(courseId);
        } else if (item.getItemId() == R.id.action_delete_all_assessments) {
            viewModel.deleteAllAssessments(courseId);
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.assessmentDetailButton)
    void AssessmentDetailClickHandler() {
        Intent intent = new Intent(this, AssessmentDetailActivity.class);
        startActivity(intent);
    }
}
