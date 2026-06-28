package com.example.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    public interface OnAlbumClickListener {
        void onAlbumClick(Album album);
    }

    private List<Album> albums;
    private int indent;
    private OnAlbumClickListener listener;

    public AlbumAdapter(List<Album> albums, int indent, OnAlbumClickListener listener) {
        this.albums = albums;
        this.indent = indent;
        this.listener = listener;
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        public TextView albumName;
        public ImageView folderIcon;

        public AlbumViewHolder(View view) {
            super(view);
            albumName = view.findViewById(R.id.albumName);
            folderIcon = view.findViewById(R.id.folderIcon);
        }
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        Album album = albums.get(position);

        holder.itemView.setPadding(
                16 + indent * 40,
                holder.itemView.getPaddingTop(),
                holder.itemView.getPaddingRight(),
                holder.itemView.getPaddingBottom()
        );

        holder.albumName.setText(album.name);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAlbumClick(album);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }
}