package com.fortysomethingnerd.android.termtracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fortysomethingnerd.android.termtracker.database.TermEntity;
import com.fortysomethingnerd.android.termtracker.ui.TermsAdapter;
import com.fortysomethingnerd.android.termtracker.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private List<TermEntity> termsData = new ArrayList<>();
    private TermsAdapter mAdapter;
    private MainViewModel mViewModel;

    @OnClick(R.id.termDetailFab)
    void termDetailClickHandler() {
        Intent intent = new Intent(this, TermDetailActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createNotificationChannels();

        ButterKnife.bind(this);
        initRecyclerView();
        initViewModel();
    }

    private void createNotificationChannels() {
        // Create notification channel for course reminders.
        String channel_id = getString(R.string.course_tracker_notification_channel_id);
        CharSequence name = getString(R.string.course_reminders_notification_channel_name);
        String description = getString(R.string.course_reminders_notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;

        createNotificationChannel(channel_id, name, description, importance);

        // Create notification channel for assessment reminders.
        channel_id = getString(R.string.assessment_tracker_notification_channel_id);
        name = getString(R.string.assessment_tracker_notification_channel_name);
        description = getString(R.string.assessment_tracker_notification_channel_description);

        createNotificationChannel(channel_id, name, description, importance);
    }

    private void createNotificationChannel(String channel_id, CharSequence name, String description, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initViewModel() {
        final Observer<List<TermEntity>> termsObserver = new Observer<List<TermEntity>>() {
            @Override
            public void onChanged(List<TermEntity> termEntities) {
                termsData.clear();
                termsData.addAll(termEntities);

                if (mAdapter == null) {
                    mAdapter = new TermsAdapter(termsData, MainActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }
        };

        mViewModel = this.getDefaultViewModelProviderFactory().create(MainViewModel.class);
        mViewModel.mTerms.observe(this, termsObserver);
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true); // Each item has the same height

        // Use a linear layout for recycler view rather than a tile layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mRecyclerView.getContext(), layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_sample_data) {
            addSampleData();
            return true;
        } else if (id == R.id.action_delete_all) {
            deleteAllNotes();
        return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAllNotes() {
        mViewModel.deleteAllNotes();
    }

    private void addSampleData() {
        mViewModel.addSampleData();
    }
}
