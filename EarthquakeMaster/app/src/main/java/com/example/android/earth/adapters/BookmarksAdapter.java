package com.example.android.earth.adapters;


import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.earth.database.BookmarkDatabase;
import com.example.android.earth.database.BookmarkEntry;
import com.example.android.earth.R;

import java.util.ArrayList;
import java.util.List;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<BookmarkEntry> mData;
    private Context context;

    public BookmarksAdapter(Context context, List<BookmarkEntry> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    public void setData(ArrayList<BookmarkEntry> mData){
        this.mData = mData;
        notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.bookmarked_earthquakes_list_item, parent, false));
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        BookmarkEntry bookmark = mData.get(position);
        holder.title.setText(bookmark.getTitle());
        holder.snippet.setText(bookmark.getSnippet());
    }

    private void remove(int position) {
        mData.remove(position);
        notifyItemChanged(position);
        notifyItemRangeRemoved(position, 1);
    }


    @Override
    public int getItemCount() { return mData == null ? 0 : mData.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView snippet;
        ImageButton removeBookmark;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookmarked_earthquakes_title_id);
            snippet = itemView.findViewById(R.id.bookmarked_earthquakes_snippet_id);
            removeBookmark = itemView.findViewById(R.id.remove_bookmark_id);

            removeBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final BookmarkEntry entryToBeRemoved = mData.get(getAdapterPosition());
                    remove(getAdapterPosition());
                    new AsyncTask<Void,Void,Void>(){
                        @Override
                        protected Void doInBackground(Void... voids) {
                            BookmarkDatabase.getInstance(context).bookmarkDAO().removeBookmark(entryToBeRemoved);
                            return null;
                        }
                    }.execute();
                }
            });
        }



    }
}