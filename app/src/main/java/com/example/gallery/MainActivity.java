package com.example.gallery;

import java.util.Set;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.activity.OnBackPressedCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1001;
    RecyclerView recyclerView;
    AlbumAdapter adapter;

    RecyclerView fileRecyclerView;
    FileAdapter fileAdapter;

    private File albumsRoot;
    private File currentDir;
    private File forResolve;
    private File notImages;
    private boolean showingRowExternalAlbums = false;
    private boolean showingExternalAlbums = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        albumsRoot = new File(Environment.getExternalStorageDirectory(), "GalleryPro");
        forResolve = new File(albumsRoot, "For resolve");
        notImages = new File(albumsRoot, "Not images");
        currentDir = albumsRoot;

        recyclerView = new RecyclerView(this);
        fileRecyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(recyclerView);
        layout.addView(fileRecyclerView);

        setContentView(layout);

        checkPermissionsAndLoad();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(showingExternalAlbums)
                    loadAlbums(albumsRoot);
                else if(currentDir == null || currentDir.equals(albumsRoot))
                    finish();
                else {
                    File parent = currentDir.getParentFile();
                    if (parent != null && parent.exists()) {
                        if(parent.getAbsolutePath().startsWith(albumsRoot.getAbsolutePath())) {
                            loadAlbums(parent);
                        }
                        else {
                            loadExternalAlbums();
                        }
                    }
                    else {
                        loadAlbums(albumsRoot);
                    }
                }
            }
        });
    }

    private void checkPermissionsAndLoad() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                loadAlbums(currentDir);
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_PERMISSION_CODE);
            }
        } else {
            if (hasPermissions()) {
                loadAlbums(currentDir);
            } else {
                requestPermissions();
            }
        }
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSION_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                loadAlbums(currentDir);
            } else {
                Toast.makeText(this, "Permission required to access files", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean allGranted = true;
            for (int res : grantResults) {
                if (res != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                loadAlbums(currentDir);
            } else {
                Toast.makeText(this, "Permissions required to access files", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadAlbums(File dir) {
        // Убедимся, что корневая папка GalleryPro существует
        if (!albumsRoot.exists()) {
            boolean created = albumsRoot.mkdirs();
            if (!created) {
                Toast.makeText(this, "Could not create GalleryPro folder", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // Создаём папку forResolve, если её нет
        if (!forResolve.exists()) {
            boolean created = forResolve.mkdirs();
            if (!created) {
                Toast.makeText(this, "Could not create folder for resolving", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // Создаём папку notImages, если её нет
        if (!notImages.exists()) {
            boolean created = notImages.mkdirs();
            if (!created) {
                Toast.makeText(this, "Could not create folder for non-image files", Toast.LENGTH_LONG).show();
                return;
            }
        }

        showingRowExternalAlbums = dir.equals(albumsRoot);
        showingExternalAlbums = false;

        // Запоминаем текущую директорию
        currentDir = dir;
        Log.w("12345", currentDir.getAbsolutePath());

        // Загружаем список альбомов (подпапок)
        List<Album> albumList = listAlbumStructure(dir);

        if (showingRowExternalAlbums) {
            // Добавляем специальный "альбом" сверху
            Album externalAlbumsItem = new Album(
                    "Остальные альбомы на устройстве",
                    "EXTERNAL_ALBUMS_SPECIAL_PATH",
                    null
            );
            albumList.add(0, externalAlbumsItem);
        }

        adapter = new AlbumAdapter(albumList, 0, album -> {
            if ("EXTERNAL_ALBUMS_SPECIAL_PATH".equals(album.path)) {
                loadExternalAlbums();
            } else {
                File selectedDir = new File(album.path);
                loadAlbums(selectedDir);
            }
        });
        recyclerView.setAdapter(adapter);

        // Загружаем список файлов изображений
        List<String> files = listImageFiles(dir);
        fileAdapter = new FileAdapter(files);
        fileRecyclerView.setAdapter(fileAdapter);
    }

    private void loadExternalAlbums() {
        showingExternalAlbums = true;

        Set<String> mediaFoldersRelative = MediaStoreHelper.getImageFolders(this);
        List<Album> externalAlbums = new ArrayList<>();

        String albumsRootPath = albumsRoot.getAbsolutePath();

        for (String relativePath : mediaFoldersRelative) {
            // Формируем полный путь
            File folder = new File(Environment.getExternalStorageDirectory(), relativePath);
            if (!folder.exists()) continue;

            // Исключаем те, что внутри albumsRoot
            if (!folder.getAbsolutePath().startsWith(albumsRootPath)) {
                externalAlbums.add(new Album(folder.getName(), folder.getAbsolutePath(), null));
            }
        }

        adapter = new AlbumAdapter(externalAlbums, 0, album -> {
            // При клике в списке внешних альбомов — заходим внутрь
            File selectedDir = new File(album.path);
            loadAlbums(selectedDir);
        });

        recyclerView.setAdapter(adapter);

        // Внешние альбомы показываем без файлов внизу (по желанию)
        fileAdapter = new FileAdapter(new ArrayList<>());
        fileRecyclerView.setAdapter(fileAdapter);
    }

    private List<Album> listAlbumStructure(File dir) {
        List<Album> albums = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    List<Album> children = listAlbumStructure(file);
                    albums.add(new Album(file.getName(), file.getAbsolutePath(), children));
                }
            }
        }
        return albums;
    }

    private List<String> listImageFiles(File dir) {
        List<String> files = new ArrayList<>();
        File[] fileList = dir.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (file.isFile()) {
                    String name = file.getName().toLowerCase();
                    if (name.endsWith(".jpg") || name.endsWith(".jpeg")
                            || name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".webp")
                            || name.endsWith(".bmp") || name.endsWith(".tiff") || name.endsWith(".heif")
                            || name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".avi")
                            || name.endsWith(".mov") || name.endsWith(".wmv") || name.endsWith(".flv")
                            || name.endsWith(".3gp") || name.endsWith(".webm") || name.endsWith(".m4v")
                            || name.endsWith(".ts")  || name.endsWith(".mpeg") || name.endsWith(".mpg")) {
                        files.add(file.getName());
                    }
//                    else {
//                        // Если файл не является изображением, перемещаем его в папку notImages
//                        File notImageFile = new File(notImages, file.getName());
//                        if (!file.renameTo(notImageFile)) {
//                            // Если перемещение не удалось, выводим сообщение об ошибке
//                            Toast.makeText(this, "Failed to move non-image file: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//                        }
//                    }

                }
            }
        }
        return files;
    }
}
