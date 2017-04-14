package com.vengalrao.android.reader.Widget;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.vengalrao.android.reader.R;
import com.vengalrao.android.reader.data.BookContrack;

/**
 * Created by vengalrao on 14-04-2017.
 */

public class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private Cursor mCursor;
    private Context mContext;
    private int widgetId;

    public WidgetFactory(Context context, Intent intent){
        mContext=context;
        widgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if(mCursor!=null){
            mCursor.close();
        }
        mCursor=mContext.getContentResolver().query(BookContrack.BookFavorite.CONTENT_FAV_URI,
                new String[]{BookContrack.BookFavorite.BOOK_ID, BookContrack.BookFavorite.BOOK_TITLE, BookContrack.BookFavorite.BOOK_AUTHOR},
                null,
                null,
                BookContrack.BookFavorite.BOOK_TITLE
        );
    }

    @Override
    public void onDestroy() {
        if(mCursor!=null){
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        if(mCursor!=null)
            return mCursor.getCount();
        else
            return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                mCursor == null || !mCursor.moveToPosition(position)) {
            if(mCursor==null){
                RemoteViews views=new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
                views.setViewVisibility(R.id.book_widget_list, View.GONE);
                views.setViewVisibility(R.id.widget_empty,View.VISIBLE);
            }
            return null;
        }
        RemoteViews views=new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        views.setViewVisibility(R.id.book_widget_list, View.VISIBLE);
        views.setViewVisibility(R.id.widget_empty,View.GONE);

        if(mCursor.moveToPosition(position)){
            views.setTextViewText(R.id.book_name_widget,mCursor.getString(mCursor.getColumnIndex(BookContrack.BookFavorite.BOOK_TITLE)));
            views.setTextViewText(R.id.book_author_widget,mCursor.getString(mCursor.getColumnIndex(BookContrack.BookFavorite.BOOK_AUTHOR)));
        }

        final Intent fillInIntent = new Intent();
        views.setOnClickFillInIntent(R.id.book_widget_list,fillInIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
