package com.fortysomethingnerd.android.termtracker.ui;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fortysomethingnerd.android.termtracker.R;
import com.fortysomethingnerd.android.termtracker.database.NoteEntity;
import com.fortysomethingnerd.android.termtracker.utilities.FormattedText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private final List<NoteEntity> notes;
    private final Context context;

    public NotesAdapter(List<NoteEntity> notes, Context context) {
        this.notes = notes;
        this.context = context;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.note_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        NoteEntity note = notes.get(position);
        Spanned titleText = FormattedText.getHTMLText(note.getTitle(), "");
        holder.titleTextView.setText(titleText);
        holder.textView.setText(note.getText());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.note_title_textView)
        TextView titleTextView;

        @BindView(R.id.note_textView)
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
