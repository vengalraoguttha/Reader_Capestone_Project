package com.vengalrao.android.reader.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.vengalrao.android.reader.MainActivity;
import com.vengalrao.android.reader.R;
import com.vengalrao.android.reader.sync.BookSyncAdapter;

/**
 * Created by vengalrao on 14-04-2017.
 */

public class WidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int appWidgetId:appWidgetIds){
            RemoteViews view=new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            //launch main Activity
            Intent intent2 = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);
            view.setOnClickPendingIntent(R.id.widget, pendingIntent);


            Intent intent=new Intent(context, WidgetIntentService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
            view.setRemoteAdapter(R.id.book_widget_list,intent);

            Intent clickIntentTemplate = new Intent(context, MainActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            view.setPendingIntentTemplate(R.id.book_widget_list, clickPendingIntentTemplate);

            view.setEmptyView(R.id.book_widget_list, R.id.widget_empty);
            appWidgetManager.updateAppWidget(appWidgetId,view);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,R.id.book_widget_list);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.v("receiver","cameo "+BookSyncAdapter.ACTION_DATA_UPDATED+" "+intent.getAction());
        if (BookSyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            Log.v("receiver","camei");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.book_widget_list);
        }
    }
}
