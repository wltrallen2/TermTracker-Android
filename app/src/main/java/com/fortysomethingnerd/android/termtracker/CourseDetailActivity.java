package com.fortysomethingnerd.android.termtracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.lifecycle.Observer;

import com.fortysomethingnerd.android.termtracker.components.MyReceiver;
import com.fortysomethingnerd.android.termtracker.database.CourseEntity;
import com.fortysomethingnerd.android.termtracker.database.DateConverter;
import com.fortysomethingnerd.android.termtracker.fragments.DatePickerDialogFragment;
import com.fortysomethingnerd.android.termtracker.utilities.Constants;
import com.fortysomethingnerd.android.termtracker.utilities.CourseStatus;
import com.fortysomethingnerd.android.termtracker.utilities.UtilityMethods;
import com.fortysomethingnerd.android.termtracker.viewmodel.CourseDetailViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.COURSE_END_NOTIFICATION_ID_PREFIX;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.COURSE_ID_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.COURSE_START_NOTIFICATION_ID_PREFIX;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.DATE_FORMAT_CHAR_SEQUENCE;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.EDITING_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.LOG_TAG;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.NOTIFICATION_CHANNEL_ID_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.NOTIFICATION_HOUR;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.NOTIFICATION_ID;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.NOTIFICATION_MINUTE;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.NOTIFICATION_SECOND;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.NOTIFICATION_TEXT_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.NOTIFICATION_TITLE_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TEMP_END_ALARM_STATE;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TEMP_END_DATE;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TEMP_START_ALARM_STATE;
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

    @BindView(R.id.alarm_course_start)
    ImageView startAlarmIcon;

    @BindView(R.id.course_detail_end_text_view)
    TextView endTextView;

    @BindView(R.id.alarm_course_end)
    ImageView endAlarmIcon;

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

            setImageForView(startAlarmIcon, savedInstanceState.getBoolean(TEMP_START_ALARM_STATE));
            setImageForView(endAlarmIcon, savedInstanceState.getBoolean(TEMP_END_ALARM_STATE));
        }

        initStatusSpinner();
        initViewModel();
    }

    private void setImageForView(ImageView imageView, boolean isSet) {
        if(isSet) {
            imageView.setImageResource(R.drawable.ic_alarm_primary);
        } else {
            imageView.setImageResource(R.drawable.ic_alarm_grey);
        }
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

                    // TODO: NEXT -> Test this code. Also test device rotation for icons
                    if(courseEntity.isStartAlarmActive() && canSetAlarm(courseEntity.getStart())) {
                        startAlarmIcon.setImageResource(R.drawable.ic_alarm_primary);
                    } else {
                        startAlarmIcon.setImageResource(R.drawable.ic_alarm_grey);
                        courseEntity.setStartAlarmActive(false);
                    }

                    if(courseEntity.isEndAlarmActive() && canSetAlarm(courseEntity.getEnd())) {
                        endAlarmIcon.setImageResource(R.drawable.ic_alarm_primary);
                    } else {
                        endAlarmIcon.setImageResource(R.drawable.ic_alarm_grey);
                        courseEntity.setEndAlarmActive(false);
                    }

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
            onBackPressed();
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

    // TODO: NEXT --> Why is there a double saved course when adding a new course, navigating forward to
    // assessments, and then clicking back to the course list?
    private int saveAndReturn() {
        UtilityMethods.hideKeyboard(this);

        String title = titleTextView.getText().toString();
        String mentorName = mentorNameTextView.getText().toString();
        String mentorPhone = mentorPhoneTextView.getText().toString();
        String mentorEmail = mentorEmailTextView.getText().toString();

        int statusIndex = statusSpinner.getSelectedItemPosition();
        String statusString = CourseStatus.getCourseStatusStrings()[statusIndex];
        CourseStatus status = CourseStatus.get(statusString);

        boolean isStartAlarmActive = isStartAlarmActive();
        boolean isEndAlarmActive = isEndAlarmActive();

        Bundle extras = getIntent().getExtras();
        int termId = extras.getInt(TERM_ID_KEY);

        int courseId = -1;
        try {
            Date start = DateConverter.parseStringToDate((startTextView.getText().toString()));
            Date end = DateConverter.parseStringToDate(endTextView.getText().toString());

            courseId = (int) viewModel.saveCourse(termId, title, start, isStartAlarmActive,
                    end, isEndAlarmActive, status, mentorName, mentorPhone, mentorEmail);
            Toast.makeText(this, "Created/Inserted course Id " + courseId, Toast.LENGTH_LONG).show();

            if(isStartAlarmActive && canSetAlarm(start)) { setNotificationForStart(courseId, title, start); }
            if(isEndAlarmActive && canSetAlarm(end)) { setNotificationForEnd(courseId, title, end); }

            viewModel.loadCourse(courseId);

        } catch (ParseException e) {
            // TODO: Handle parse error.
            Log.i(LOG_TAG, "CourseDetailActivity.saveAndReturn: " + e);
            e.printStackTrace();
        }

        return courseId;
    }

    private boolean canSetAlarm(Date date) {
        return Calendar.getInstance().getTime().before(date);
    }

    @OnClick({R.id.alarm_course_start, R.id.alarm_course_end})
    public void toggleAlarmForView(View view) {
        ImageView alarmIcon = (ImageView) view;
        CourseEntity course = viewModel.getLiveCourse().getValue();

        String alarmDateString = "";
        if(alarmIcon.equals(startAlarmIcon)) {
            alarmDateString = startTextView.getText().toString();
        } else {
            alarmDateString = endTextView.getText().toString();
        }

        Date date = null;
        try {
            date = DateConverter.parseStringToDate(alarmDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (isAlarmActiveForImageView(alarmIcon) && canSetAlarm(date)) {

            if (course != null) {
                int courseId = course.getId();
                String courseName = course.getTitle();

                if(alarmIcon.equals(startAlarmIcon)) {
                    setNotificationForStart(courseId, courseName, date);
                 } else {
                    setNotificationForEnd(courseId, courseName, date);
                }

                Toast.makeText(this,
                        "Your notification for " + alarmDateString + " is set.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,
                        "Your alarm for " + alarmDateString
                                + " will be set when the course information is saved.",
                        Toast.LENGTH_LONG).show();
            }

            alarmIcon.setImageResource(R.drawable.ic_alarm_primary);
        } else { // Alarm is to be cleared because user clicked on colored icon to change to grey
            if (course != null) {
                int courseId = course.getId();

                Intent intent = new Intent(CourseDetailActivity.this, MyReceiver.class);
                String notificationPrefix = view.equals(startAlarmIcon)
                        ? COURSE_START_NOTIFICATION_ID_PREFIX
                        : COURSE_END_NOTIFICATION_ID_PREFIX;
                int notificationId = Integer.parseInt(notificationPrefix + courseId);
                PendingIntent pendingIntentForBroadcast = PendingIntent
                        .getBroadcast(CourseDetailActivity.this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntentForBroadcast);
            }

            alarmIcon.setImageResource(R.drawable.ic_alarm_grey);

            String toastMessage = canSetAlarm(date) ? "Your alarm for  " + alarmDateString + "has been cleared."
                    : "Alarms cannot be set for today or for dates in the past.";
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void setNotificationForStart(int courseId, String courseName, Date start) {
        Toast.makeText(this, "Setting alarm for start of course.", Toast.LENGTH_LONG).show();

        String title = getString(R.string.course_starting_today);
        String message = "Your WGU course, " + courseName + ", is set to begin on " + DateConverter.parseDateToString(start);
        setCourseNotification(courseId, title, start, message, COURSE_START_NOTIFICATION_ID_PREFIX);
    }

    private void setNotificationForEnd(int courseId, String courseName, Date end) {
        Toast.makeText(this, "Setting alarm for end of course.", Toast.LENGTH_LONG).show();

        String title = getString(R.string.course_ending_today);
        String message = "Your WGU course, " + courseName + ", is set to end on " + DateConverter.parseDateToString(end);
        setCourseNotification(courseId, title, end, message, COURSE_END_NOTIFICATION_ID_PREFIX);
    }

    private void setCourseNotification(int courseId, String title, Date when, String message, String notificationIdPrefix) {
        if(Calendar.getInstance().getTime().before(when)) {
            String notificationIdString = notificationIdPrefix + courseId;
            int notificationId = Integer.parseInt(notificationIdString);
            long notificationWhen = getNotificationTime(when);
            PendingIntent pendingIntentForContent = createPendingIntentForThisCourse(courseId);

            Intent intent = new Intent(CourseDetailActivity.this, MyReceiver.class);
            intent.putExtra(NOTIFICATION_CHANNEL_ID_KEY, getString(R.string.course_tracker_notification_channel_id));
            intent.putExtra(NOTIFICATION_ID, notificationId);
            intent.putExtra(NOTIFICATION_TITLE_KEY, title);
            intent.putExtra(NOTIFICATION_TEXT_KEY, message);
            intent.putExtra(Constants.NOTIFICATION_PENDING_INTENT_KEY, pendingIntentForContent);

            PendingIntent pendingIntentForBroadcast = PendingIntent.getBroadcast(CourseDetailActivity.this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationWhen, pendingIntentForBroadcast);
        } else {
            Toast.makeText(this, "Alarms cannot be set for today or dates in the past.", Toast.LENGTH_LONG).show();
        }
    }

    private long getNotificationTime(Date date) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.HOUR_OF_DAY, NOTIFICATION_HOUR);
        now.set(Calendar.MINUTE, NOTIFICATION_MINUTE);
        now.set(Calendar.SECOND, NOTIFICATION_SECOND);

        return now.getTimeInMillis();
    }

    private PendingIntent createPendingIntentForThisCourse(int courseId) {
        int termId = getIntent().getExtras().getInt(TERM_ID_KEY);

        Intent resultIntent = new Intent(this, CourseDetailActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        stackBuilder.editIntentAt(1).putExtra(TERM_ID_KEY, termId);
        stackBuilder.editIntentAt(2).putExtra(TERM_ID_KEY, termId);
        stackBuilder.editIntentAt(3).putExtra(TERM_ID_KEY, termId);
        stackBuilder.editIntentAt(3).putExtra(COURSE_ID_KEY, courseId);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        return resultPendingIntent;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (viewModel.getLiveCourse().getValue() != null) {
            outState.putInt(COURSE_ID_KEY, viewModel.getLiveCourse().getValue().getId());
        }

        outState.putBoolean(Constants.EDITING_KEY, true);
        outState.putString(TEMP_START_DATE, startTextView.getText().toString());
        outState.putString(TEMP_END_DATE, endTextView.getText().toString());
        outState.putBoolean(TEMP_START_ALARM_STATE, isStartAlarmActive());
        outState.putBoolean((TEMP_END_ALARM_STATE), isEndAlarmActive());
        super.onSaveInstanceState(outState);
    }

    private boolean isAlarmActiveForImageView(ImageView view) {
        return view.getDrawable().getConstantState()
                == getResources().getDrawable(R.drawable.ic_alarm_primary).getConstantState();
    }

    private boolean isStartAlarmActive() {
        return isAlarmActiveForImageView(startAlarmIcon);
    }

    private boolean isEndAlarmActive() {
        return isAlarmActiveForImageView(endAlarmIcon);
    }

    @OnClick({R.id.assessments_button, R.id.notes_button})
    public void showNextActivity(View view) {
        int courseId = saveAndReturn();
        if(courseId > 0) {
            Class activityClass = getClassForView(view);
            if (activityClass != null) {
                Intent intent = new Intent(this, activityClass);
                intent.putExtra(COURSE_ID_KEY, courseId);
                startActivity(intent);
            }
        } else {
            // TODO: Handle the case that the user hasn't completed the course info.
        }
    }

    private Class getClassForView(View view) {
        switch(view.getId()) {
            case R.id.assessments_button :
                return AssessmentListActivity.class;
            case R.id.notes_button :
                return NoteListActivity.class;
            default :
                return null;
        }
    }
}
