package com.example.android.earth.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class BookmarkEntry {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private String snippet;

    @Ignore
    public BookmarkEntry(){

    }

    public BookmarkEntry(long id,String title,String snippet){
        this.id = id;
        this.title = title;
        this.snippet = snippet;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }
}
