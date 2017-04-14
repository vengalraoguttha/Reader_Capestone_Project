package com.vengalrao.android.reader.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by vengalrao on 14-04-2017.
 */

public class WidgetIntentService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetFactory(getApplicationContext(),intent);
    }
}
