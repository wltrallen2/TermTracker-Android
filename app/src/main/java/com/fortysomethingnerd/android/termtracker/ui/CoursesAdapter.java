package com.fortysomethingnerd.android.termtracker.ui;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fortysomethingnerd.android.termtracker.R;
import com.fortysomethingnerd.android.termtracker.database.CourseEntity;
import com.fortysomethingnerd.android.termtracker.database.DateConverter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        holder.courseStatusTextView.setText(course.getStatus().toString());
        holder.courseEndTextView.setText(DateConverter.parseDateToString(course.getEnd()));
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

        @BindView(R.id.course_end_textView)
        TextView courseEndTextView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
