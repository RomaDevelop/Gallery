package com.example.gallery;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.HashSet;
import java.util.Set;

public class MediaStoreHelper {

    public static Set<String> getImageFolders(Context context) {
        Set<String> imageFolders = new HashSet<>();

        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Images.Media.RELATIVE_PATH
        };

        ContentResolver resolver = context.getContentResolver();

        try (Cursor cursor = resolver.query(collection, projection, null, null, null)) {
            if (cursor != null) {
                int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH);

                while (cursor.moveToNext()) {
                    String relativePath = cursor.getString(pathColumn);
                    if (relativePath != null) {
                        imageFolders.add(relativePath);
                    }
                }
            }
        }

        return imageFolders;
    }
}
