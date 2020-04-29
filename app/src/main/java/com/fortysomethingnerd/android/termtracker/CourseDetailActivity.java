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

import java.text.ParseException;
import java.util.Date;
import java.util.stream.IntStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.COURSE_ID_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.LOG_TAG;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.STATUS_SPINNER_PROMPT;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TERM_ID_KEY;

public class CourseDetailActivity extends AppCompatActivity {
    private CourseDetailViewModel viewModel;
    private boolean isNewCourse = false;

    @BindView(R.id.course_detail_title_text_view)
    TextView titleTextView;

    @BindView(R.id.course_detail_status_spinner)
    Spinner statusSpinner;

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
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });
    }

    private void initViewModel() {
        viewModel = this.getDefaultViewModelProviderFactory()
                .create(CourseDetailViewModel.class);
        viewModel.getLiveCourse().observe(this, new Observer<CourseEntity>() {
            @Override
            public void onChanged(CourseEntity courseEntity) {
                if (courseEntity != null) {
                    titleTextView.setText(courseEntity.getTitle());
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

        Bundle extras = getIntent().getExtras();
        int courseId = extras.getInt(COURSE_ID_KEY);
        if (courseId == 0) {
            setTitle(getString(R.string.new_course));
            isNewCourse = true;
        } else {
            setTitle(getString(R.string.edit_course_details));
            viewModel.loadCourse(courseId);
        }
    }

    @OnClick(R.id.course_detail_end_text_view)
    public void showDatePickerDialog() {
        UtilityMethods.hideKeyboard(this);

        DatePickerDialogFragment dialog = new DatePickerDialogFragment();
        dialog.setTextViewId(R.id.course_detail_end_text_view);
        dialog.show(getSupportFragmentManager(), "date picker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isNewCourse) {
            getMenuInflater().inflate(R.menu.menu_course_detail, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveAndReturn();
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
        super.onBackPressed();
    }

    private void saveAndReturn() {
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

        try {
            Date end = DateConverter.parseStringToDate(endTextView.getText().toString());
            viewModel.saveCourse(termId, title, end, status, mentorName, mentorPhone, mentorEmail);
        } catch (ParseException e) {
            // TODO: Handle parse error.
            Log.i(LOG_TAG, "CourseDetailActivity.saveAndReturn: " + e);
            e.printStackTrace();
        }

        finish();
    }
}
