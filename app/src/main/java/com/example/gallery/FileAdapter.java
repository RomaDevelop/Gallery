package com.example.gallery;

import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    private List<File> files;

    public FileAdapter(List<File> files) {
        this.files = files;
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        public TextView fileName;
        public ImageView filePreview;

        public FileViewHolder(View view) {
            super(view);
            fileName = view.findViewById(R.id.fileName);
            filePreview = view.findViewById(R.id.filePreview);
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

        File file = files.get(position);

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        if (bitmap != null) {
            holder.filePreview.setImageBitmap(bitmap);
        } else {
            holder.filePreview.setImageResource(R.drawable.ic_file);
        }

        holder.fileName.setText(file.getName());
    }

    @Override
    public int getItemCount() {
        return files.size();
    }
}