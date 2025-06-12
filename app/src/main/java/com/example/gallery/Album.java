package com.example.gallery;

import java.util.List;

public class Album {
    public String name;
    public String path;
    public List<Album> subAlbums;

    public Album(String name, String path, List<Album> subAlbums) {
        this.name = name;
        this.path = path;
        this.subAlbums = subAlbums;
    }
}
