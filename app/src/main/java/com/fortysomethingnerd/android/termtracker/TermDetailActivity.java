package com.fortysomethingnerd.android.termtracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import com.fortysomethingnerd.android.termtracker.database.DateConverter;
import com.fortysomethingnerd.android.termtracker.database.TermEntity;
import com.fortysomethingnerd.android.termtracker.fragments.DatePickerDialogFragment;
import com.fortysomethingnerd.android.termtracker.utilities.UtilityMethods;
import com.fortysomethingnerd.android.termtracker.viewmodel.TermDetailViewModel;

import java.text.ParseException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.EDITING_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.LOG_TAG;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TEMP_END_DATE;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TEMP_START_DATE;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TERM_ID_KEY;

public class TermDetailActivity extends AppCompatActivity {

    @BindView(R.id.term_detail_title_text_view)
    TextView termNameTextView;

    @BindView(R.id.term_detail_start_text_view)
    TextView termStartTextView;

    @BindView(R.id.term_detail_end_text_view)
    TextView termEndTextView;

    private TermDetailViewModel mViewModel;
    private boolean mNewTerm, mEditing;
//    private int termId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term_detail_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_term_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mEditing = savedInstanceState.getBoolean(EDITING_KEY);
            termStartTextView.setText(savedInstanceState.getString(TEMP_START_DATE));
            termEndTextView.setText(savedInstanceState.getString(TEMP_END_DATE));
        }

        initViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO: NEXT: This isn't working - Goal: Change title and add delete button when coming back to term from courses on new term creation.
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setTitle(getString(R.string.new_term));
            mNewTerm = true;
        } else {
            setTitle(getString(R.string.edit_term));
            int termId = extras.getInt(TERM_ID_KEY);
            mViewModel.loadData(termId);
        }
    }

    private void initViewModel() {
        mViewModel = this.getDefaultViewModelProviderFactory().create(TermDetailViewModel.class);
        mViewModel.mLiveTerm.observe(this, new Observer<TermEntity>() {
            @Override
            public void onChanged(TermEntity termEntity) {
                if(termEntity != null && !mEditing) {
                    termNameTextView.setText(termEntity.getTitle());
                    termStartTextView.setText(termEntity.getStartString());
                    termEndTextView.setText(termEntity.getEndString());
                }

//                termId = termEntity.getId();
            }
        });

//        Bundle extras = getIntent().getExtras();
//        if (extras == null) {
//            setTitle(getString(R.string.new_term));
//            mNewTerm = true;
//        } else {
//            setTitle(getString(R.string.edit_term));
//            int termId = extras.getInt(TERM_ID_KEY);
//            mViewModel.loadData(termId);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!mNewTerm) {
            getMenuInflater().inflate(R.menu.menu_term_detail, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveAndReturn();
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            mViewModel.deleteTerm();
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
        String termTitle = termNameTextView.getText().toString();
        String startString = termStartTextView.getText().toString();
        String endString = termEndTextView.getText().toString();

        int termId = -1;
        try {
            Date start = DateConverter.parseStringToDate(startString);
            Date end = DateConverter.parseStringToDate(endString);
            termId = (int) mViewModel.saveTerm(termTitle, start, end);
//            mViewModel.loadData(termId);
        } catch (ParseException e) {
            // TODO: Handle parse error.
            Log.i(LOG_TAG, "TermDetailActivity.saveAndReturn: " + e);
        }

        mViewModel.loadData(termId);
        mNewTerm = false;
        return termId;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (mViewModel.mLiveTerm != null) {
            outState.putInt(TERM_ID_KEY, mViewModel.mLiveTerm.getValue().getId());
        }

        outState.putBoolean(EDITING_KEY, true);
        outState.putString(TEMP_START_DATE, termStartTextView.getText().toString());
        outState.putString(TEMP_END_DATE, termEndTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void showDatePickerDialog(View view) {
        UtilityMethods.hideKeyboard(this);
        if (view instanceof TextView) {
            int textViewId = view.getId();
            DatePickerDialogFragment datePickerFragment = new DatePickerDialogFragment();
            datePickerFragment.setTextViewId(textViewId);
            datePickerFragment.show(getSupportFragmentManager(), "date picker");
        }
    }

    public void showCourseActivity(View view) {
        int termId = saveAndReturn();

        Intent intent = new Intent(this, CourseListActivity.class);
        intent.putExtra(TERM_ID_KEY, termId);

        startActivity(intent);
    }
}
