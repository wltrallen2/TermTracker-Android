package com.fortysomethingnerd.android.termtracker;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.TaskStackBuilder;
import androidx.lifecycle.Observer;

import com.fortysomethingnerd.android.termtracker.components.MyReceiver;
import com.fortysomethingnerd.android.termtracker.database.AssessmentEntity;
import com.fortysomethingnerd.android.termtracker.database.DateConverter;
import com.fortysomethingnerd.android.termtracker.fragments.DatePickerDialogFragment;
import com.fortysomethingnerd.android.termtracker.utilities.AssessmentType;
import com.fortysomethingnerd.android.termtracker.utilities.UtilityMethods;
import com.fortysomethingnerd.android.termtracker.viewmodel.AssessmentDetailViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.*;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.ASSESSMENT_DUE_NOTIFICATION_ID_PREFIX;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.ASSESSMENT_GOAL_NOTIFICATION_ID_PREFIX;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.ASSESSMENT_ID_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.COURSE_ID_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.EDITING_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.LOG_TAG;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.NOTIFICATION_CHANNEL_ID_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.NOTIFICATION_HOUR;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.NOTIFICATION_ID;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.NOTIFICATION_MINUTE;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.NOTIFICATION_PENDING_INTENT_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.NOTIFICATION_SECOND;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.NOTIFICATION_TEXT_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.NOTIFICATION_TITLE_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TEMP_DUE_DATE;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TEMP_GOAL_ALARM_STATE;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TEMP_GOAL_DATE;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TERM_ID_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.UtilityMethods.hideKeyboard;

public class AssessmentDetailActivity extends AppCompatActivity {

    @BindView(R.id.assessment_detail_title_text_view)
    EditText titleTextView;

    @BindView(R.id.assessment_type_spinner)
    Spinner typeSpinner;

    @BindView(R.id.assessment_detail_goal_text_view)
    TextView goalTextView;

    @BindView(R.id.alarm_assessment_goal)
    ImageView goalAlarmIcon;

    @BindView(R.id.assessment_detail_due_date_text_view)
    TextView dueDateTextView;

    @BindView(R.id.alarm_assessment_due)
    ImageView dueAlarmIcon;

    private AssessmentDetailViewModel viewModel;
    private boolean isNewAssessment, isEdited;
    private boolean isGoalAlarmSet, isDueAlarmSet;

    /*******************************************************************************
     * LIFE CYCLE & OVERRIDDEN SUPERCLASS METHODS
     *******************************************************************************/
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

            setImageForView(goalAlarmIcon, savedInstanceState.getBoolean(TEMP_GOAL_ALARM_STATE));
            setImageForView(dueAlarmIcon, savedInstanceState.getBoolean(TEMP_DUE_ALARM_STATE));
        }

        initTypeSpinner();
        initViewModel();
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

    @Override
    public void onBackPressed() {
        saveAndReturn();
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(EDITING_KEY, true);
        outState.putString(TEMP_GOAL_DATE, goalTextView.getText().toString());
        outState.putString(TEMP_DUE_DATE, dueDateTextView.getText().toString());
        outState.putBoolean(TEMP_GOAL_ALARM_STATE, isGoalAlarmSet);
        outState.putBoolean(TEMP_DUE_ALARM_STATE, isDueAlarmSet);
        super.onSaveInstanceState(outState);
    }

    /*******************************************************************************
     * CLICK HANDLERS
     *******************************************************************************/
    @OnClick(R.id.assessment_detail_due_date_text_view)
    public void dueDateTextViewClickHandler() {
        showDatePickerDialog(R.id.assessment_detail_due_date_text_view);
    }

    @OnClick(R.id.assessment_detail_goal_text_view)
    public void goalTextViewClickHandler() {
        showDatePickerDialog(R.id.assessment_detail_goal_text_view);
    }

    public void showDatePickerDialog(int textViewId) {
        hideKeyboard(this);

        DatePickerDialogFragment dialog = new DatePickerDialogFragment();
        dialog.setTextViewId(textViewId);
        dialog.show(getSupportFragmentManager(), "date picker");
    }

    @OnClick({R.id.alarm_assessment_goal, R.id.alarm_assessment_due})
    public void toggleAlarmForView(ImageView view) {
        String alarmDateString = (view.equals(goalAlarmIcon))
                ? goalTextView.getText().toString()
                : dueDateTextView.getText().toString();

        try {
            Date date = DateConverter.parseStringToDate(alarmDateString);
            // Method will end here if dateString cannot be parsed.

            if(!isActiveForImageView(view) && canSetAlarm(date)) {
                toggleOnAlarmForView(view, date);
            } else {
                toggleOffAlarmForView(view, date);
            }
        } catch (ParseException e) {
            // Date string at top of try statement was unable to be parsed.
            Log.i(LOG_TAG, "Error in AssessmentDetailActivity.toggleAlarmForView: Error parsing date: " + e.toString());
            e.printStackTrace();
        }
    }

    /*******************************************************************************
     * PRIVATE HELPER METHODS - VIEW INITIALIZATION
     *******************************************************************************/
    private void initTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item);
        adapter.addAll(AssessmentType.getAssessmentTypeStrings());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(getApplicationContext(), v);
                return false;
            }
        });
    }

    private void initViewModel() {
        Observer<AssessmentEntity> observer = new Observer<AssessmentEntity>() {
            @Override
            public void onChanged(AssessmentEntity assessmentEntity) {
                if (assessmentEntity != null && !isEdited) {
                    // Set the contents of all text fields
                    titleTextView.setText(assessmentEntity.getTitle());
                    goalTextView.setText(DateConverter.parseDateToString(assessmentEntity.getGoalDate()));
                    dueDateTextView.setText(DateConverter.parseDateToString(assessmentEntity.getDueDate()));

                    // Set the alarm icons (either active or inactive)
                    // If the alarm cannot be set because the alarm time has passed,
                    // automatically set alarm icon to grey and disable the isAlarmActive in the assessmentEntity
                    if(assessmentEntity.isGoalAlarmActive() && canSetAlarm(assessmentEntity.getGoalDate())) {
                        setImageForView(goalAlarmIcon, true);
                    } else {
                        setImageForView(goalAlarmIcon, false);
                        assessmentEntity.setGoalAlarmActive(false);
                    }

                    if(assessmentEntity.isDueAlarmActive() && canSetAlarm(assessmentEntity.getDueDate())) {
                        setImageForView(dueAlarmIcon, true);
                    } else {
                        setImageForView(dueAlarmIcon, false);
                        assessmentEntity.setDueAlarmActive(false);
                    }

                    // Set the typeSpinner options and the selectedIndex
                    String type = assessmentEntity.getType().toString();
                    String[] types = AssessmentType.getAssessmentTypeStrings();
                    int index = 0;
                    while(index < types.length && !types[index].equals(type)) { index++; }
                    typeSpinner.setSelection(index);
                }
            }
        };

        viewModel = this.getDefaultViewModelProviderFactory().create(AssessmentDetailViewModel.class);
        viewModel.getLiveAssessment().observe(this, observer);
    }

    private void setTitle() {
        Bundle extras = getIntent().getExtras();
        if (!extras.containsKey(ASSESSMENT_ID_KEY)) {
            isNewAssessment = true;
            setTitle(getString(R.string.new_assessment));
        } else {
            isNewAssessment = false;
            int assessmentId = extras.getInt(ASSESSMENT_ID_KEY);
            viewModel.loadAssessment(assessmentId);

            CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
            UtilityMethods.setTitleForCollapsingToolbarLayout(this, collapsingToolbarLayout, getString(R.string.edit_assessment));
        }
    }

    /*******************************************************************************
     * PRIVATE HELPER METHODS - ALARMS
     *******************************************************************************/
    private boolean canSetAlarm(Date date) {
        return Calendar.getInstance().getTime().before(date);
    }

    private PendingIntent createPendingIntentForThisAssessment(int assessmentId) {
        int courseId = getIntent().getExtras().getInt(COURSE_ID_KEY);
        int termId = viewModel.getCourseForCourseId(courseId).getTermId();

        Intent resultIntent = new Intent(this, AssessmentDetailActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        stackBuilder.addNextIntent(mainIntent);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent termDetailIntent = new Intent(getApplicationContext(), TermDetailActivity.class);
        termDetailIntent.putExtra(TERM_ID_KEY, termId);
        stackBuilder.addNextIntent(termDetailIntent);

        Intent courseListIntent = new Intent(getApplicationContext(), CourseListActivity.class);
        courseListIntent.putExtra(TERM_ID_KEY, termId);
        stackBuilder.addNextIntent(courseListIntent);

        Intent courseDetailIntent = new Intent(getApplicationContext(), CourseDetailActivity.class);
        courseDetailIntent.setAction("course_intent_action_" + courseId);
        courseDetailIntent.putExtra(TERM_ID_KEY, termId);
        courseDetailIntent.putExtra(COURSE_ID_KEY, courseId);
        stackBuilder.addNextIntent(courseDetailIntent);

        Intent assessmentListIntent = new Intent(getApplicationContext(), AssessmentListActivity.class);
        assessmentListIntent.putExtra(COURSE_ID_KEY, courseId);
        stackBuilder.addNextIntent(assessmentListIntent);

        Intent assessmentDetailIntent = new Intent(getApplicationContext(), AssessmentDetailActivity.class);
        assessmentDetailIntent.putExtra(COURSE_ID_KEY, courseId);
        assessmentDetailIntent.putExtra(ASSESSMENT_ID_KEY, assessmentId);
        stackBuilder.addNextIntent(assessmentDetailIntent);

        // In older APIs, there is a bug that does not allow the top intent to receive its extras;
        // therefore, to bypass this bug, here is included an extra top intent that includes code
        // to automatically trigger the 'onBackPressed()' method if there are no extras in the Bundle.
        Intent dummyTopIntent = new Intent(getApplicationContext(), NoteListActivity.class);
        stackBuilder.addNextIntent(dummyTopIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(assessmentId, PendingIntent.FLAG_UPDATE_CURRENT);
        return resultPendingIntent;
    }

    private long getNotificationTime(Date date) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.HOUR_OF_DAY,  NOTIFICATION_HOUR);
        now.set(Calendar.MINUTE, NOTIFICATION_MINUTE);
        now.set(Calendar.SECOND, NOTIFICATION_SECOND);

        return now.getTimeInMillis();
    }

    private boolean isActiveForImageView(ImageView view) {
        if(view.equals(goalAlarmIcon))
            return isGoalAlarmSet;
        else return isDueAlarmSet;
    }

    private void setAssessmentNotification(int assessmentId, String title, Date date, String message, String notificationIdPrefix) {
        if(canSetAlarm(date)) {
            String notificationIdString = notificationIdPrefix + assessmentId;
            int notificationId = Integer.parseInt(notificationIdString);
            long notificationWhen = getNotificationTime(date);
            PendingIntent pendingIntentForContent = createPendingIntentForThisAssessment(assessmentId);

            Intent intent = new Intent(AssessmentDetailActivity.this, MyReceiver.class);
            intent.putExtra(NOTIFICATION_CHANNEL_ID_KEY, getString(R.string.assessment_tracker_notification_channel_id));
            intent.putExtra(NOTIFICATION_ID, notificationId);
            intent.putExtra(NOTIFICATION_TITLE_KEY, title);
            intent.putExtra(NOTIFICATION_TEXT_KEY, message);
            intent.putExtra(NOTIFICATION_PENDING_INTENT_KEY, pendingIntentForContent);

            PendingIntent pendingIntentForBroadcast = PendingIntent
                    .getBroadcast(AssessmentDetailActivity.this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationWhen, pendingIntentForBroadcast);
            showToast("Your notification for " + DateConverter.parseDateToString(date) + " is set.");
        } else {
            showToast("Alarms cannot be set for today or dates in the past.");
        }
    }

    private void setImageForView(ImageView view, boolean isAlarmSet) {
        if(isAlarmSet)
            view.setImageResource(R.drawable.ic_alarm_primary);
        else view.setImageResource(R.drawable.ic_alarm_grey);

        if(view.equals(goalAlarmIcon))
            isGoalAlarmSet = isAlarmSet;
        else isDueAlarmSet = isAlarmSet;
    }

    private void setNotificationForDue(int assessmentId, String courseName, String assessmentName, Date date) {
        String title = "Assessment Due Today";
        String message = "Your due date for your WGU assessment (" + assessmentName +
                ") for the course " + courseName + " is today!";
        setAssessmentNotification(assessmentId, title, date, message, ASSESSMENT_DUE_NOTIFICATION_ID_PREFIX);
    }

    private void setNotificationForGoal(int assessmentId, String courseName, String assessmentName, Date date) {
        String title = "Assessment Goal Today";
        String message = "Your goal date for your WGU assessment (" + assessmentName +
                ") for the course " + courseName + " is today!";
        setAssessmentNotification(assessmentId, title, date, message, ASSESSMENT_GOAL_NOTIFICATION_ID_PREFIX);
    }

    private void toggleOnAlarmForView(ImageView view, Date date) {
        // Toggle the alarm on if the course has already been saved to the database.
        AssessmentEntity assessment = viewModel.getLiveAssessment().getValue();
        if (assessment != null) {
            int assessmentId = assessment.getId();
            String courseName = viewModel.getCourseForAssessment().getTitle();
            String assessmentName = assessment.getTitle();

            if (view.equals(goalAlarmIcon))
                setNotificationForGoal(assessmentId, courseName, assessmentName, date);
            else setNotificationForDue(assessmentId, courseName, assessmentName, date);
        } else {
            // Else inform the user that the alarm will be set when the course is saved to the database.
            showToast("Your alarm for " + DateConverter.parseDateToString(date) +
                    " will be set when the course information is saved.");
        }

        setImageForView(view, true);
    }

    private void toggleOffAlarmForView(ImageView view, Date date) {
        // Clear the previously set alarm.
        AssessmentEntity assessment = viewModel.getLiveAssessment().getValue();
        if (assessment != null) {
            int assessmentId = assessment.getId();

            // Create a pendingIntent object that will match the pendingIntent for the
            // previously set alarm.
            Intent intent = new Intent(AssessmentDetailActivity.this, MyReceiver.class);
            String notificationPrefix = view.equals(goalAlarmIcon)
                    ? ASSESSMENT_GOAL_NOTIFICATION_ID_PREFIX
                    : ASSESSMENT_DUE_NOTIFICATION_ID_PREFIX;
            int notificationId = Integer.parseInt(notificationPrefix + assessmentId);
            PendingIntent pendingIntentForBroadcast = PendingIntent
                    .getBroadcast(AssessmentDetailActivity.this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Clear alarm
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntentForBroadcast);
        } // No need for an else clasue since if the assessment is new and not yet saved to the database,
        // the alarm would not have been set and therefore, no need to clear it.

        setImageForView(view, false);

        // Get toast message according to whether alarm was cleared by user or
        // because it was not a future alarm.
        String toastMessage = canSetAlarm(date)
                ? "Your alarm for " + DateConverter.parseDateToString(date) + " has been cleared."
                : "Alarms cannot be set for today or for dates in the past.";
        showToast(toastMessage);
    }

    /*******************************************************************************
     * PRIVATE HELPER METHODS - GENERAL
     *******************************************************************************/
    private int saveAndReturn() {
        // Hide keyboard.
        hideKeyboard(this);

        // Set assessmentId to -1, so that if the saveAssessment method fails, method can return -1 as the indicator.
        int assessmentId = -1;
        try {
            // Retrieve text by parsing the date strings in the date textViews.
            // This could trigger a ParseException (but shouldn't since the strings were parse from dates to begin with.
            // NOTE: If this does fail, the entire method will fail, so the rest of the method code
            // has been included in this try-catch block.
            Date goalDate = DateConverter.parseStringToDate(goalTextView.getText().toString());
            Date dueDate = DateConverter.parseStringToDate((dueDateTextView.getText().toString()));

            // Retrieve text from textView objects
            String title = titleTextView.getText().toString();

            // Retrieve text from typeSpinner
            int typeIndex = typeSpinner.getSelectedItemPosition();
            String typeString = AssessmentType.getAssessmentTypeStrings()[typeIndex];
            AssessmentType type = AssessmentType.get(typeString);

            // Retrieve the courseId from Bundle extras.
            Bundle extras = getIntent().getExtras();
            int courseId = extras.getInt(COURSE_ID_KEY);
            String courseName = viewModel.getCourseForCourseId(courseId).getTitle();

            // Save the assessment.
            assessmentId = (int) viewModel.saveAssessment(courseId, title, type, goalDate, isGoalAlarmSet, dueDate, isDueAlarmSet);

            // Set alarms for goal and due dates if applicable.
            if(isGoalAlarmSet && canSetAlarm(goalDate)) { setNotificationForGoal(assessmentId, courseName, title, goalDate); }
            if(isDueAlarmSet && canSetAlarm(dueDate)) { setNotificationForDue(assessmentId, courseName, title, dueDate); }

        } catch (ParseException e) {
            Log.i(LOG_TAG, "Error in AssessmentDetailActivity.saveAndReturn: " +
                    "one of the date strings failed to parse: " + e.toString());
            e.printStackTrace();
        }

        return assessmentId;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
