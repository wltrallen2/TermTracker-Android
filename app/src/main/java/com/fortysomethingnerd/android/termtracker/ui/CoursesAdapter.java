package com.fortysomethingnerd.android.termtracker.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fortysomethingnerd.android.termtracker.CourseDetailActivity;
import com.fortysomethingnerd.android.termtracker.R;
import com.fortysomethingnerd.android.termtracker.database.CourseEntity;
import com.fortysomethingnerd.android.termtracker.database.DateConverter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.COURSE_ID_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TERM_ID_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.FormattedText.getHTMLText;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder> {

    private final List<CourseEntity> courses;
    private final Context context;

    public CoursesAdapter(List<CourseEntity> courses, Context context) {
        this.courses = courses;
        this.context = context;
    }

    @NonNull
    @Override
    public CoursesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.course_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoursesAdapter.ViewHolder holder, int position) {
        final CourseEntity course = courses.get(position);

        holder.courseNameTextView.setText(course.getTitle());

        Spanned statusString = getHTMLText("Course Status: ", course.getStatus().toString());
        holder.courseStatusTextView.setText(statusString);

        Spanned startString = getHTMLText("Start Date: ", DateConverter.parseDateToString(course.getStart()));
        holder.courseStartTextView.setText(startString);

        Spanned endString = getHTMLText("Projected End Date: ", DateConverter.parseDateToString(course.getEnd()));
        holder.courseEndTextView.setText(endString);

        holder.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CourseDetailActivity.class);
                intent.putExtra(COURSE_ID_KEY, course.getId());
                intent.putExtra(TERM_ID_KEY, course.getTermId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.course_name_textView)
        TextView courseNameTextView;

        @BindView(R.id.course_status_textView)
        TextView courseStatusTextView;

        @BindView(R.id.course_start_textView)
        TextView courseStartTextView;

        @BindView(R.id.course_end_textView)
        TextView courseEndTextView;

        @BindView(R.id.courseDetailFab)
        FloatingActionButton fab;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
