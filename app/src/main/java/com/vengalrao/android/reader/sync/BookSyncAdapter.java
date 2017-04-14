package com.vengalrao.android.reader.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;

import com.vengalrao.android.reader.Utilities.Book;
import com.vengalrao.android.reader.Utilities.NetworkUtilities;
import com.vengalrao.android.reader.data.BookContrack;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URL;

/**
 * Created by vengalrao on 14-04-2017.
 */

public class BookSyncAdapter extends AbstractThreadedSyncAdapter {

    Book[] books;

    private static final int SYNC_INTERVAL = 60 * 120;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    public static final String ACTION_DATA_UPDATED = "UPDATE";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({BOOK_STATUS_OK, BOOK_SERVER_DOWN, BOOK_STATUS_SERVER_INVALID, BOOK_STATUS_INVALID,BOOK_STATUS_UNKNOWN})
    public @interface QuakeStatus {}

    public static final int BOOK_STATUS_OK = 0;
    public static final int BOOK_SERVER_DOWN = 1;
    public static final int BOOK_STATUS_SERVER_INVALID = 2;
    public static final int BOOK_STATUS_INVALID = 3;
    public static final int BOOK_STATUS_UNKNOWN = 4;


    private URL url;

    public BookSyncAdapter(Context context,boolean autoInitialize, boolean allowParallelSyncs){
        super(context, autoInitialize, allowParallelSyncs);
    }

    public BookSyncAdapter(Context context, boolean autoInitialize, URL url) {
        super(context, autoInitialize);
        this.url=url;
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED).setPackage(getContext().getPackageName());
        getContext().sendBroadcast(dataUpdatedIntent);
    }
}
