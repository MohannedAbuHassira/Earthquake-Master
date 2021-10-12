package com.example.android.earth.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.earth.adapters.BookmarksAdapter;
import com.example.android.earth.database.BookmarkDatabase;
import com.example.android.earth.database.BookmarkEntry;
import com.example.android.earth.R;
import com.example.android.earth.database.MyViewModel;
import com.example.android.earth.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class BookmarksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookmarksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        setUpRecycleView();
        loadBookmarks();

        getSupportActionBar().setTitle(R.string.earthquake_bookmarks);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void loadBookmarks(){
        MyViewModel myViewModel = new MyViewModel(this);
        myViewModel.initViewModel();
        myViewModel.getEntries().observe(this, new Observer<List<BookmarkEntry>>() {
            @Override
            public void onChanged(@Nullable List<BookmarkEntry> bookmarkEntries) {
                adapter.setData((ArrayList<BookmarkEntry>) bookmarkEntries);
            }});
    }

    private void setUpRecycleView() {
        recyclerView = findViewById(R.id.bookmarks_recycleview_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookmarksAdapter(this, new ArrayList<BookmarkEntry>());
        recyclerView.setAdapter(adapter);
    }
}
