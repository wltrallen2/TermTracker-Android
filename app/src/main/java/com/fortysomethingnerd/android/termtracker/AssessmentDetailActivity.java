package com.fortysomethingnerd.android.termtracker;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.room.PrimaryKey;

import com.fortysomethingnerd.android.termtracker.database.AssessmentEntity;
import com.fortysomethingnerd.android.termtracker.database.DateConverter;
import com.fortysomethingnerd.android.termtracker.utilities.UtilityMethods;
import com.fortysomethingnerd.android.termtracker.viewmodel.AssessmentDetailViewModel;
import com.fortysomethingnerd.android.termtracker.viewmodel.AssessmentListViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.DateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.ASSESSMENT_ID_KEY;

public class AssessmentDetailActivity extends AppCompatActivity {

    @BindView(R.id.assessment_detail_title_text_view)
    EditText titleTextView;

    @BindView(R.id.assessment_detail_goal_text_view)
    TextView goalTextView;

    @BindView(R.id.assessment_detail_due_date_text_view)
    TextView dueDateTextView;

    private AssessmentDetailViewModel viewModel;
    private boolean isNewAssessment, isEdited;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assessment_detail_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        initViewModel();
        setTitle();
    }

    private void setTitle() {
        int assessmentId = -1;
        try {
            assessmentId = viewModel.getAssessment().getValue().getId();
        } catch (Exception e) {
            // TODO: Handle this exception.
            e.printStackTrace();
        }

        Bundle extras = getIntent().getExtras();
        if (extras == null && assessmentId == -1) {
            setTitle(getString(R.string.new_assessment));
            isNewAssessment = true;
        } else {
            if (assessmentId == -1) {
                assessmentId = extras.getInt(ASSESSMENT_ID_KEY);
            }
            viewModel.loadAssessment(assessmentId);

            CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
            UtilityMethods.setTitleForCollapsingToolbarLayout(this, collapsingToolbarLayout, getString(R.string.edit_assessment));
            isNewAssessment = false;
        }
    }

    private void initViewModel() {
        Observer<AssessmentEntity> observer = new Observer<AssessmentEntity>() {
            @Override
            public void onChanged(AssessmentEntity assessmentEntity) {
                if (assessmentEntity != null) {
                    titleTextView.setText(assessmentEntity.getTitle());
                    goalTextView.setText(DateConverter.parseDateToString(assessmentEntity.getGoalDate()));
                    dueDateTextView.setText(DateConverter.parseDateToString(assessmentEntity.getDueDate()));
                }
            }
        };

        viewModel = this.getDefaultViewModelProviderFactory().create(AssessmentDetailViewModel.class);
        viewModel.getAssessment().observe(this, observer);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
