package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private List<Note> noteList;
    private List<Note> fullList;   // used for search
    private Context context;

    public NoteAdapter(List<Note> noteList, Context context) {
        this.noteList = noteList;
        this.fullList = new ArrayList<>(noteList); // copy original list
        this.context = context;
    }

    // ✅ ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        Button btnOpen, btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            btnOpen = itemView.findViewById(R.id.btnOpen);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    // ✅ Create row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    // ✅ Bind data
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Note note = noteList.get(position);
        holder.txtName.setText(note.getName());

        // 🔘 OPEN BUTTON
        holder.btnOpen.setOnClickListener(v -> {

            String url = note.getLink();

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        });

        // 🗑 DELETE BUTTON
        holder.btnDelete.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle("Delete Note")
                    .setMessage("This action cannot be undone.\n\nDelete this note?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        int currentPosition = holder.getBindingAdapterPosition();

                        if (currentPosition != RecyclerView.NO_POSITION) {
                            noteList.remove(currentPosition);
                            fullList.remove(currentPosition); // important for search
                            notifyItemRemoved(currentPosition);
                            saveNotes();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    // ✅ SEARCH FILTER
    public void filter(String text) {

        noteList.clear();

        if (text.isEmpty()) {
            noteList.addAll(fullList);
        } else {
            text = text.toLowerCase();
            for (Note note : fullList) {
                if (note.getName().toLowerCase().contains(text)) {
                    noteList.add(note);
                }
            }
        }

        notifyDataSetChanged();
    }

    // ✅ Save after delete
    private void saveNotes() {
        SharedPreferences prefs =
                context.getSharedPreferences("notes", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = gson.toJson(fullList); // save full list

        prefs.edit()
                .putString("list", json)
                .apply();
    }
}