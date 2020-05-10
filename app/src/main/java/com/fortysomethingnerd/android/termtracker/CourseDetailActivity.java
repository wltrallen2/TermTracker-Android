package com.fortysomethingnerd.android.termtracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.COURSE_END_NOTIFICATION_ID_PREFIX;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.COURSE_ID_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.COURSE_START_NOTIFICATION_ID_PREFIX;
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
import static com.fortysomethingnerd.android.termtracker.utilities.UtilityMethods.hideKeyboard;

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


    /*******************************************************************************
     * LIFE CYCLE & OVERRIDDEN SUPERCLASS METHODS
     *******************************************************************************/
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

    @Override
    protected void onResume() {
        super.onResume();
        setTitle();
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


    /*******************************************************************************
     * CLICK HANDLERS
     *******************************************************************************/
    @OnClick(R.id.course_detail_start_text_view)
    public void startTextViewClickHandler() {
        showDatePickerDialog(R.id.course_detail_start_text_view);
    }

    @OnClick(R.id.course_detail_end_text_view)
    public void endTextViewClickHandler() {
        showDatePickerDialog(R.id.course_detail_end_text_view);
    }

    public void showDatePickerDialog(int textViewId) {
        hideKeyboard(this);

        DatePickerDialogFragment dialog = new DatePickerDialogFragment();
        dialog.setTextViewId(textViewId);
        dialog.show(getSupportFragmentManager(), "date picker");
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
            showToast("Please complete all fields before adding assessments or notes.");
        }
    }

    @OnClick({R.id.alarm_course_start, R.id.alarm_course_end})
    public void toggleAlarmForView(View view) {
        ImageView alarmIcon = (ImageView) view;
        String alarmDateString = (alarmIcon.equals(startAlarmIcon))
                ? startTextView.getText().toString()
                : endTextView.getText().toString();

        try {
            Date date = DateConverter.parseStringToDate(alarmDateString);
            // Method will end here if dateString cannot be parsed.

            // If the user pushed the button when it was NOT active, they want to activate the alarm.
            if (!isAlarmActiveForImageView(alarmIcon) && canSetAlarm(date))
                toggleOnAlarmForView(alarmIcon, date);
            // Else alarm is to be cleared because user clicked on colored icon to change to grey
            // or because date is out of range.
            else toggleOffAlarmForView(alarmIcon, date);
        } catch (ParseException e) {
            // Date string at top of try statement was unable to be parsed.
            Log.i(LOG_TAG, "Error in CourseDetailActivity.toggleAlarmForView: Error parsing date: " + e.toString());
            e.printStackTrace();
        }
    }


    /*******************************************************************************
     * PRIVATE HELPER METHODS - VIEW INITIALIZATION
     *******************************************************************************/
    private void initStatusSpinner() {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item);
        adapter.addAll(CourseStatus.getCourseStatusStrings());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
        statusSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(getApplicationContext(), v);
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
                // If the courseEntity exists and has not been edited...
                if (courseEntity != null && !isEdited) {
                    //Set the contents of all text fields
                    titleTextView.setText(courseEntity.getTitle());
                    startTextView.setText(DateConverter.parseDateToString(courseEntity.getStart()));
                    endTextView.setText(DateConverter.parseDateToString(courseEntity.getEnd()));
                    mentorNameTextView.setText(courseEntity.getMentorName());
                    mentorEmailTextView.setText(courseEntity.getMentorEmail());
                    mentorPhoneTextView.setText(courseEntity.getMentorPhone());

                    // Set the alarm icons (either active or inactive)
                    // If the alarm cannot be set because the alarm time has past,
                    // automatically set alarm icon to grey and disable the isAlarmActive in the courseEntity
                    if(courseEntity.isStartAlarmActive() && canSetAlarm(courseEntity.getStart())) {
                        setImageForView(startAlarmIcon, true);
                    } else {
                        setImageForView(startAlarmIcon, false);
                        courseEntity.setStartAlarmActive(false);
                    }

                    if(courseEntity.isEndAlarmActive() && canSetAlarm(courseEntity.getEnd())) {
                        setImageForView(endAlarmIcon, true);
                    } else {
                        setImageForView(endAlarmIcon, false);
                        courseEntity.setEndAlarmActive(false);
                    }

                    // Set the statusSpinner options and the selectedIndex
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

    private void setTitle() {
        int courseId = getCourseId();
        if (courseId == -1) {
            isNewCourse = true;
            setTitle(getString(R.string.new_course));
        } else {
            isNewCourse = false;
            viewModel.loadCourse(courseId);

            CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
            UtilityMethods.setTitleForCollapsingToolbarLayout(this, collapsingToolbarLayout, getString(R.string.edit_course_details));
        }
    }


    /*******************************************************************************
     * PRIVATE HELPER METHODS - ALARMS
     *******************************************************************************/
    private boolean canSetAlarm(Date date) {
        return Calendar.getInstance().getTime().before(date);
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

    private long getNotificationTime(Date date) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.HOUR_OF_DAY, NOTIFICATION_HOUR);
        now.set(Calendar.MINUTE, NOTIFICATION_MINUTE);
        now.set(Calendar.SECOND, NOTIFICATION_SECOND);

        return now.getTimeInMillis();
    }

    private boolean isAlarmActiveForImageView(@NotNull ImageView view) {
        return view.getDrawable().getConstantState()
                == getResources().getDrawable(R.drawable.ic_alarm_primary).getConstantState();
    }

    private boolean isStartAlarmActive() {
        return isAlarmActiveForImageView(startAlarmIcon);
    }

    private boolean isEndAlarmActive() {
        return isAlarmActiveForImageView(endAlarmIcon);
    }

    private void setCourseNotification(int courseId, String title, Date when, String message, String notificationIdPrefix) {
        if(canSetAlarm(when)) {
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
            showToast("Alarms cannot be set for today or dates in the past.");
        }
    }

    private void setImageForView(ImageView imageView, boolean isSet) {
        if(isSet) {
            imageView.setImageResource(R.drawable.ic_alarm_primary);
        } else {
            imageView.setImageResource(R.drawable.ic_alarm_grey);
        }
    }

    private void setNotificationForStart(int courseId, String courseName, Date start) {
        String title = getString(R.string.course_starting_today);
        String message = "Your WGU course, " + courseName + ", is set to begin on " + DateConverter.parseDateToString(start);
        setCourseNotification(courseId, title, start, message, COURSE_START_NOTIFICATION_ID_PREFIX);
    }

    private void setNotificationForEnd(int courseId, String courseName, Date end) {
        String title = getString(R.string.course_ending_today);
        String message = "Your WGU course, " + courseName + ", is set to end on " + DateConverter.parseDateToString(end);
        setCourseNotification(courseId, title, end, message, COURSE_END_NOTIFICATION_ID_PREFIX);
    }

    private void toggleOffAlarmForView(ImageView alarmIcon, Date date) {
        // Clear the previously set alarm.
        CourseEntity course = viewModel.getLiveCourse().getValue();
        if (course != null) {
            int courseId = course.getId();

            // Create a pendingIntent object that will match the pendingIntent object of the
            // previously set alarm.
            Intent intent = new Intent(CourseDetailActivity.this, MyReceiver.class);
            String notificationPrefix = alarmIcon.equals(startAlarmIcon)
                    ? COURSE_START_NOTIFICATION_ID_PREFIX
                    : COURSE_END_NOTIFICATION_ID_PREFIX;
            int notificationId = Integer.parseInt(notificationPrefix + courseId);
            PendingIntent pendingIntentForBroadcast = PendingIntent
                    .getBroadcast(CourseDetailActivity.this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Clear alarm.
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntentForBroadcast);
        } // No need for an else clause since if the course is new and not yet saved in the database,
        // the alarm would not have been set and therefore, no need to clear.

        setImageForView(alarmIcon, false);

        // Get toast message according to whether alarm was cleared by user request or
        // because it was not a future alarm.
        String toastMessage = canSetAlarm(date)
                ? "Your alarm for  " + DateConverter.parseDateToString(date) + " has been cleared."
                : "Alarms cannot be set for today or for dates in the past.";
        showToast(toastMessage);
    }

    private void toggleOnAlarmForView(ImageView alarmIcon, Date date) {
        // Set the alarm if the course has already been saved in the database.
        CourseEntity course = viewModel.getLiveCourse().getValue();
        if (course != null) {
            int courseId = course.getId();
            String courseName = course.getTitle();

            if (alarmIcon.equals(startAlarmIcon))
                setNotificationForStart(courseId, courseName, date);
            else setNotificationForEnd(courseId, courseName, date);

            showToast("Your notification for " + DateConverter.parseDateToString(date) + " is set.");
        } else { // Inform the user that the alarm will be set when the course information is saved to the database.
            showToast("Your alarm for " + DateConverter.parseDateToString(date)
                    + " will be set when the course information is saved.");
        }

        setImageForView(alarmIcon, true);
    }


    /*******************************************************************************
     * PRIVATE HELPER METHODS - GENERAL
     *******************************************************************************/
    @Nullable
    private Class getClassForView(@NotNull View view) {
        switch(view.getId()) {
            case R.id.assessments_button :
                return AssessmentListActivity.class;
            case R.id.notes_button :
                return NoteListActivity.class;
            default :
                return null;
        }
    }

    /**
     *
     * @return Returns the courseId for the current courseEntity object or -1 if the courseEntity
     * object does not exist.
     */
    private int getCourseId() {
        int courseId = -1;

        // Attempt to retrieve courseId from liveCourse object in viewModel
        try {
            courseId = viewModel.getLiveCourse().getValue().getId();
        } catch (NullPointerException e) {
            // If liveCourse does not exist, attempt to retrieve courseId from intent extras.
            Bundle extras = getIntent().getExtras();
            if(extras != null && courseId == -1) {
                courseId = extras.getInt(COURSE_ID_KEY);
            }
        }

        // Return the courseId or return -1 if the courseId does not exist,
        // indicating that this is a new courseEntity object.
        return courseId;
    }

    /**
     * Saves the course (whether it is a new course or overwriting an old course). Also, it sets the
     * alarms for start and end dates if the user so chooses (and if they are not in the past or for
     * the current date). Finally, loads the new course into the liveCourse object in the viewModel.
     *
     * @return the courseId for the newly saved course, OR -1 if the save fails.
     */
    private int saveAndReturn() {
        // Hide keyboard
        hideKeyboard(this);

        // Set courseId to -1, so that if the saveCourse method fails, method can return -1 as the indicator.
        int courseId = -1;

        try {
            // Retrieve text by parsing the date strings in the date textViews.
            // This could trigger a ParseException (but shouldn't since the strings were parse from dates to begin with.
            // NOTE: If this does fail, the entire method will fail, so the rest of the method code
            // has been included in this try-catch block.
            Date start = DateConverter.parseStringToDate((startTextView.getText().toString()));
            Date end = DateConverter.parseStringToDate(endTextView.getText().toString());

            // Retrieve text from textView objects
            String title = titleTextView.getText().toString();
            String mentorName = mentorNameTextView.getText().toString();
            String mentorPhone = mentorPhoneTextView.getText().toString();
            String mentorEmail = mentorEmailTextView.getText().toString();

            // Retrieve text from statusSpinner
            int statusIndex = statusSpinner.getSelectedItemPosition();
            String statusString = CourseStatus.getCourseStatusStrings()[statusIndex];
            CourseStatus status = CourseStatus.get(statusString);

            // Retrieve booleans from alarm icons
            boolean isStartAlarmActive = isStartAlarmActive();
            boolean isEndAlarmActive = isEndAlarmActive();

            // Retrieve the term id from extras and the courseId, if one exists.
            Bundle extras = getIntent().getExtras();
            int termId = extras.getInt(TERM_ID_KEY);

            // Save the course and retrieve the new courseId from the future object.
            courseId = (int) viewModel.saveCourse(termId, title, start, isStartAlarmActive,
                    end, isEndAlarmActive, status, mentorName, mentorPhone, mentorEmail);

            // Set alarms for start and end times if applicable.
            if(isStartAlarmActive && canSetAlarm(start)) { setNotificationForStart(courseId, title, start); }
            if(isEndAlarmActive && canSetAlarm(end)) { setNotificationForEnd(courseId, title, end); }

            // Load the course by courseId into the viewModel.
            viewModel.loadCourse(courseId);

        } catch (ParseException e) {
            Log.i(LOG_TAG, "Error in CourseDetailActivity.saveAndReturn: " +
                    "one of the date strings failed to parse: " + e.toString());
            e.printStackTrace();
        }

        // Return the new courseId or return -1 if there was an error.
        return courseId;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
