package com.fortysomethingnerd.android.termtracker.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.fortysomethingnerd.android.termtracker.database.DateConverter;

import java.util.Calendar;
import java.util.Date;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.LOG_TAG;

public class DatePickerDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private int textViewId;
    private final Calendar calendar = Calendar.getInstance();

    public void setTextViewId(int id) {
        this.textViewId = id;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            textViewId = savedInstanceState.getInt("mTextViewId");
        } else {
            if (textViewId != 0) {
                TextView textView = (TextView) getActivity().findViewById(textViewId);
                String dateString = textView.getText().toString();
                if (!dateString.isEmpty()) {
                    try {
                        Date date = DateConverter.parseStringToDate(dateString);
                        calendar.setTime(date);
                    } catch (Exception ex) {
                        // Exception can be ignored because the datePickerDialogue will automatically
                        // set the date to the current date if the dateString is unable to be parsed.
                        Log.i(LOG_TAG, "onCreateDialog: " + ex);
                    }
                }
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(year, month, dayOfMonth);
        Date date = calendar.getTime();
        TextView tv = (TextView) getActivity().findViewById(textViewId);
        tv.setText(DateConverter.parseDateToString(date));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("mTextViewId", textViewId);
        super.onSaveInstanceState(outState);
    }
}
