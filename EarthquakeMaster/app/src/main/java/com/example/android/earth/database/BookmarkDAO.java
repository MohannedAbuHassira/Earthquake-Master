package com.example.android.earth.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BookmarkDAO {

    @Query("SELECT * FROM bookmarkentry")
    LiveData<List<BookmarkEntry>> getBookmarks();

    @Query("SELECT * FROM bookmarkentry where snippet = :snippet")
    LiveData<BookmarkEntry> getBookmarkBySnippet(String snippet);

    @Delete
    void removeBookmark(BookmarkEntry bookmarkEntry);

    @Insert
    void addBookmark(BookmarkEntry bookmarkEntry);
}
