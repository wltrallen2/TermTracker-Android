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

import com.fortysomethingnerd.android.termtracker.database.AssessmentEntity;
import com.fortysomethingnerd.android.termtracker.database.DateConverter;
import com.fortysomethingnerd.android.termtracker.fragments.DatePickerDialogFragment;
import com.fortysomethingnerd.android.termtracker.utilities.UtilityMethods;
import com.fortysomethingnerd.android.termtracker.viewmodel.AssessmentDetailViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.ParseException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.ASSESSMENT_ID_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.COURSE_ID_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.EDITING_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TEMP_DUE_DATE;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TEMP_GOAL_DATE;

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

        if (savedInstanceState != null) {
            isEdited = savedInstanceState.getBoolean(EDITING_KEY);
            goalTextView.setText(savedInstanceState.getString(TEMP_GOAL_DATE));
            dueDateTextView.setText(savedInstanceState.getString(TEMP_DUE_DATE));
        }

        initViewModel();
    }

    private void setTitle() {
        int assessmentId = -1;
        // TODO: Delete this code after testing. Don't need this in this activity
        // because user will not be returning to this activity from a descendant activity.
        // try {
        //     assessmentId = viewModel.getLiveAssessment().getValue().getId();
        // } catch (Exception e) {
        //     // TODO: Handle this exception.
        //     e.printStackTrace();
        // }

        // TODO: Test this without the null check, then delete if not needed.
        // Null check is not needed because the extras Bundle will never be null.
        // It will always contain the courseId value.
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

    @Override
    protected void onResume() {
        super.onResume();
        setTitle();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(!isNewAssessment) {
            getMenuInflater().inflate(R.menu.menu_assessment_detail, menu);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void initViewModel() {
        Observer<AssessmentEntity> observer = new Observer<AssessmentEntity>() {
            @Override
            public void onChanged(AssessmentEntity assessmentEntity) {
                if (assessmentEntity != null && !isEdited) {
                    titleTextView.setText(assessmentEntity.getTitle());
                    goalTextView.setText(DateConverter.parseDateToString(assessmentEntity.getGoalDate()));
                    dueDateTextView.setText(DateConverter.parseDateToString(assessmentEntity.getDueDate()));
                }
            }
        };

        viewModel = this.getDefaultViewModelProviderFactory().create(AssessmentDetailViewModel.class);
        viewModel.getLiveAssessment().observe(this, observer);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_delete_assessment) {
            viewModel.deleteAssessment();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.assessment_detail_goal_text_view)
    public void goalTextViewClickHandler() {
        showDatePickerDialog(R.id.assessment_detail_goal_text_view);
    }

    @OnClick(R.id.assessment_detail_due_date_text_view)
    public void dueDateTextViewClickHandler() {
        showDatePickerDialog(R.id.assessment_detail_due_date_text_view);
    }

    public void showDatePickerDialog(int textViewId) {
        UtilityMethods.hideKeyboard(this);

        DatePickerDialogFragment dialog = new DatePickerDialogFragment();
        dialog.setTextViewId(textViewId);
        dialog.show(getSupportFragmentManager(), "date picker");
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

        try {
            String title = titleTextView.getText().toString();
            Date goalDate = DateConverter.parseStringToDate(goalTextView.getText().toString());
            Date dueDate = DateConverter.parseStringToDate((dueDateTextView.getText().toString()));
            // TODO: Replace false with isGoalAlarmActive
            viewModel.saveAssessment(courseId, title, goalDate, false, dueDate);
        } catch (ParseException e) {
            // TODO: Handle this exception.
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(EDITING_KEY, true);
        outState.putString(TEMP_GOAL_DATE, goalTextView.getText().toString());
        outState.putString(TEMP_DUE_DATE, dueDateTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
