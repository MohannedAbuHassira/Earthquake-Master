package com.example.android.earth.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {BookmarkEntry.class}, version = 2, exportSchema = false)
public abstract class BookmarkDatabase extends RoomDatabase {

    private static final String LOG_TAG = BookmarkDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "bookmark_database";
    private static BookmarkDatabase sInstance;

    public static BookmarkDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        BookmarkDatabase.class, BookmarkDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract BookmarkDAO bookmarkDAO();

}