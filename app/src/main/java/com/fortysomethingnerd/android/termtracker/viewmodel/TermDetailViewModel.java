package com.fortysomethingnerd.android.termtracker.viewmodel;

import android.app.Application;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.fortysomethingnerd.android.termtracker.database.AppRepository;
import com.fortysomethingnerd.android.termtracker.database.TermEntity;
import com.fortysomethingnerd.android.termtracker.utilities.Constants;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.LOG_TAG;

public class TermDetailViewModel extends AndroidViewModel {

    public MutableLiveData<TermEntity> mLiveTerm = new MutableLiveData<>();
    private AppRepository mRepository;
    private Executor executor = Executors.newSingleThreadExecutor();

    public TermDetailViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application.getApplicationContext());
    }

    public void loadData(int termId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                TermEntity term = mRepository.getTermById(termId);
                mLiveTerm.postValue(term);
            }
        });
    }

    public long saveTerm(String termTitle, Date start, Date end) {
        TermEntity term = mLiveTerm.getValue();
        if (term == null) {
            Log.i(LOG_TAG, "TermDetailViewModel.saveTerm: term is null");
            if(TextUtils.isEmpty(termTitle.trim())
                || TextUtils.isEmpty(start.toString())
                || TextUtils.isEmpty(end.toString())) {
                return 0;
            }

            term = new TermEntity(termTitle.trim(), start, end);
        } else {
            Log.i(LOG_TAG, "TermDetailViewModel.saveTerm: term " + term.toString());

            term.setTitle(termTitle.trim());
            term.setStart(start);
            term.setEnd(end);
        }

        return mRepository.insertTerm(term);
    }

    public void deleteTerm() {
        mRepository.deleteTerm(mLiveTerm.getValue());
    }
}
