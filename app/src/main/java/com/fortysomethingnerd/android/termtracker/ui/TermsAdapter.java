package com.fortysomethingnerd.android.termtracker.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fortysomethingnerd.android.termtracker.R;
import com.fortysomethingnerd.android.termtracker.TermDetailActivity;
import com.fortysomethingnerd.android.termtracker.database.TermEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.TERM_ID_KEY;
import static com.fortysomethingnerd.android.termtracker.utilities.FormattedText.getHTMLText;

public class TermsAdapter extends RecyclerView.Adapter<TermsAdapter.ViewHolder> {

    private final List<TermEntity> mTerms;
    private final Context mContext;

    public TermsAdapter(List<TermEntity> mTerms, Context mContext) {
        this.mTerms = mTerms;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public TermsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.term_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TermsAdapter.ViewHolder holder, int position) {
        final TermEntity term = mTerms.get(position);
        holder.mTitleTextView.setText(term.getTitle());
        holder.mStartTextView.setText(getHTMLText("Start Date: ", term.getStartString()));
        holder.mEndTextView.setText(getHTMLText("End Date: ", term.getEndString()));

        holder.mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TermDetailActivity.class);
                intent.putExtra(TERM_ID_KEY, term.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTerms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.term_name_textView)
        TextView mTitleTextView;

        @BindView(R.id.term_start_textView)
        TextView mStartTextView;

        @BindView(R.id.term_end_textView)
        TextView mEndTextView;

        @BindView(R.id.termDetailFab)
        FloatingActionButton mFab;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
