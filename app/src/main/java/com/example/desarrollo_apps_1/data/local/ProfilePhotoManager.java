package com.example.desarrollo_apps_1.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class ProfilePhotoManager {

    private static final String PREF_NAME = "xplorenow_profile_prefs";
    private static final String KEY_PHOTO_PATH = "profile_photo_path";
    private static final String PHOTO_FILE_NAME = "profile_image.jpg";

    private final Context context;
    private final SharedPreferences prefs;

    @Inject
    public ProfilePhotoManager(@ApplicationContext Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String savePhoto(Bitmap bitmap) {
        File file = new File(context.getFilesDir(), PHOTO_FILE_NAME);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
            String path = file.getAbsolutePath();
            prefs.edit().putString(KEY_PHOTO_PATH, path).apply();
            return path;
        } catch (IOException e) {
            return null;
        }
    }

    public String getPhotoPath() {
        return prefs.getString(KEY_PHOTO_PATH, null);
    }

    public void clearPhoto() {
        String path = getPhotoPath();
        if (path != null) {
            File file = new File(path);
            if (file.exists()) file.delete();
        }
        prefs.edit().remove(KEY_PHOTO_PATH).apply();
    }
}
