package com.fortysomethingnerd.android.termtracker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.room.OnConflictStrategy;

import com.fortysomethingnerd.android.termtracker.database.CourseEntity;
import com.fortysomethingnerd.android.termtracker.database.DateConverter;
import com.fortysomethingnerd.android.termtracker.fragments.DatePickerDialogFragment;
import com.fortysomethingnerd.android.termtracker.utilities.Constants;
import com.fortysomethingnerd.android.termtracker.utilities.CourseStatus;
import com.fortysomethingnerd.android.termtracker.utilities.UtilityMethods;
import com.fortysomethingnerd.android.termtracker.viewmodel.CourseDetailViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.ParseException;
import java.util.Date;
import java.util.stream.IntStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.COURSE_ID_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.EDITING_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.LOG_TAG;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.STATUS_SPINNER_PROMPT;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TEMP_END_DATE;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TEMP_START_DATE;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TERM_ID_KEY;

public class CourseDetailActivity extends AppCompatActivity {
    private CourseDetailViewModel viewModel;
    private boolean isNewCourse, isEdited = false;

    @BindView(R.id.course_detail_title_text_view)
    TextView titleTextView;

    @BindView(R.id.course_detail_status_spinner)
    Spinner statusSpinner;

    @BindView(R.id.course_detail_start_text_view)
    TextView startTextView;

    @BindView(R.id.course_detail_end_text_view)
    TextView endTextView;

    @BindView(R.id.course_detail_mentor_text_view)
    TextView mentorNameTextView;

    @BindView(R.id.course_detail_mphone_text_field)
    TextView mentorPhoneTextView;

    @BindView(R.id.course_detail_memail_text_view)
    TextView mentorEmailTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_detail_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            isEdited = savedInstanceState.getBoolean(EDITING_KEY);
            startTextView.setText(savedInstanceState.getString(TEMP_START_DATE));
            endTextView.setText(savedInstanceState.getString(TEMP_END_DATE));
        }

        initStatusSpinner();
        initViewModel();
    }

    private void initStatusSpinner() {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item);
        adapter.addAll(CourseStatus.getCourseStatusStrings());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
        statusSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //TODO: Can I move this out to a utility method?
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle();
    }

    private void setTitle() {
        int courseId = -1;
        try {
            courseId = viewModel.getLiveCourse().getValue().getId();
        } catch (Exception e) {
            // TODO: Handle this exception.
            e.printStackTrace();
        }

        Bundle extras = getIntent().getExtras();
        if (extras == null && courseId == -1) {
            setTitle(getString(R.string.new_course));
            isNewCourse = true;
        } else {
            if(courseId == -1) {
                courseId = extras.getInt(COURSE_ID_KEY);
            }
            viewModel.loadCourse(courseId);

            CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
            UtilityMethods.setTitleForCollapsingToolbarLayout(this, collapsingToolbarLayout, getString(R.string.edit_course_details));
            isNewCourse = false;
        }
    }

    private void initViewModel() {
        viewModel = this.getDefaultViewModelProviderFactory()
                .create(CourseDetailViewModel.class);
        viewModel.getLiveCourse().observe(this, new Observer<CourseEntity>() {
            @Override
            public void onChanged(CourseEntity courseEntity) {
                if (courseEntity != null && !isEdited) {
                    titleTextView.setText(courseEntity.getTitle());
                    startTextView.setText(DateConverter.parseDateToString(courseEntity.getStart()));
                    endTextView.setText(DateConverter.parseDateToString(courseEntity.getEnd()));
                    mentorNameTextView.setText(courseEntity.getMentorName());
                    mentorEmailTextView.setText(courseEntity.getMentorEmail());
                    mentorPhoneTextView.setText(courseEntity.getMentorPhone());

                    String status = courseEntity.getStatus().toString();
                    String[] statuses = CourseStatus.getCourseStatusStrings();
                    int index = 0;
                    while(index < statuses.length && !statuses[index].equals(status)) {
                        index++;
                    }
                    statusSpinner.setSelection(index);
                }
            }
        });
    }

    @OnClick(R.id.course_detail_start_text_view)
    public void startTextViewClickHandler() {
        showDatePickerDialog(R.id.course_detail_start_text_view);
    }

    @OnClick(R.id.course_detail_end_text_view)
    public void endTextViewClickHandler() {
        showDatePickerDialog(R.id.course_detail_end_text_view);
    }

    public void showDatePickerDialog(int textViewId) {
        UtilityMethods.hideKeyboard(this);

        DatePickerDialogFragment dialog = new DatePickerDialogFragment();
        dialog.setTextViewId(textViewId);
        dialog.show(getSupportFragmentManager(), "date picker");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isNewCourse) {
            getMenuInflater().inflate(R.menu.menu_course_detail, menu);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveAndReturn();
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_delete_course) {
            viewModel.deleteCourse();
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

    private int saveAndReturn() {
        UtilityMethods.hideKeyboard(this);

        String title = titleTextView.getText().toString();
        String mentorName = mentorNameTextView.getText().toString();
        String mentorPhone = mentorPhoneTextView.getText().toString();
        String mentorEmail = mentorEmailTextView.getText().toString();

        int statusIndex = statusSpinner.getSelectedItemPosition();
        String statusString = CourseStatus.getCourseStatusStrings()[statusIndex];
        CourseStatus status = CourseStatus.get(statusString);

        Bundle extras = getIntent().getExtras();
        int termId = extras.getInt(TERM_ID_KEY);

        int courseId = -1;
        try {
            Date start = DateConverter.parseStringToDate((startTextView.getText().toString()));
            Date end = DateConverter.parseStringToDate(endTextView.getText().toString());
            courseId = (int) viewModel.saveCourse(termId, title, start, end, status, mentorName, mentorPhone, mentorEmail);
        } catch (ParseException e) {
            // TODO: Handle parse error.
            Log.i(LOG_TAG, "CourseDetailActivity.saveAndReturn: " + e);
            e.printStackTrace();
        }
        // TODO: Will this cause an exception if courseId = -1? Move wihtin try/catch?
        viewModel.loadCourse(courseId);
        return courseId;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (viewModel.getLiveCourse().getValue() != null) {
            outState.putInt(COURSE_ID_KEY, viewModel.getLiveCourse().getValue().getId());
        }

        outState.putBoolean(Constants.EDITING_KEY, true);
        outState.putString(TEMP_START_DATE, startTextView.getText().toString());
        outState.putString(TEMP_END_DATE, endTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
