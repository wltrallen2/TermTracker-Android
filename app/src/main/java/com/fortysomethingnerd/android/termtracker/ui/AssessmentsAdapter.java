package com.fortysomethingnerd.android.termtracker.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fortysomethingnerd.android.termtracker.AssessmentDetailActivity;
import com.fortysomethingnerd.android.termtracker.R;
import com.fortysomethingnerd.android.termtracker.database.AssessmentEntity;
import com.fortysomethingnerd.android.termtracker.database.DateConverter;
import com.fortysomethingnerd.android.termtracker.utilities.FormattedText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssessmentsAdapter extends RecyclerView.Adapter<AssessmentsAdapter.ViewHolder> {

    private final List<AssessmentEntity> assessments;
    private final Context context;

    public AssessmentsAdapter(List<AssessmentEntity> assessments, Context context) {
        this.assessments = assessments;
        this.context = context;
    }

    @NonNull
    @Override
    public AssessmentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.assessment_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentsAdapter.ViewHolder holder, int position) {
        AssessmentEntity assessment = assessments.get(position);
        holder.titleTextView.setText(assessment.getTitle().toString());

        Spanned goalText = FormattedText.getHTMLText("Goal Date: ",
                DateConverter.parseDateToString(assessment.getGoalDate()));
        holder.goalTextView.setText(goalText);

        Spanned dueDateText = FormattedText.getHTMLText("Due Date: ",
                DateConverter.parseDateToString(assessment.getDueDate()));
        holder.dueDateTextView.setText(dueDateText);

        holder.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AssessmentDetailActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return assessments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.assessment_title_textView)
        TextView titleTextView;

        @BindView(R.id.assessment_goal_textView)
        TextView goalTextView;

        @BindView(R.id.assessment_due_date_textView)
        TextView dueDateTextView;

        @BindView(R.id.assessmentDetailFab)
        FloatingActionButton fab;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
