package com.example.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    private List<String> files;

    public FileAdapter(List<String> files) {
        this.files = files;
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        public TextView fileName;

        public FileViewHolder(View view) {
            super(view);
            fileName = view.findViewById(R.id.fileName);
        }
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        holder.fileName.setText(files.get(position));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }
}
