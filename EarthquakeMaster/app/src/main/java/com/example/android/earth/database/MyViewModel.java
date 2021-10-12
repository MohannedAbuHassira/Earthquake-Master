package com.example.android.earth.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

public class MyViewModel extends ViewModel {

    private LiveData<List<BookmarkEntry>> entries;

    private Context context;

    public MyViewModel(Context context) { this.context = context; }

    public LiveData<List<BookmarkEntry>> initViewModel() {
        BookmarkDatabase database = BookmarkDatabase.getInstance(context);
        if(entries==null)
            entries = database.bookmarkDAO().getBookmarks();
        return entries;
    }

    public LiveData<List<BookmarkEntry>> getEntries() {
        return entries;
    }

}
