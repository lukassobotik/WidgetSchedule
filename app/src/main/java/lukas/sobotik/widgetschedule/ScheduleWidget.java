package lukas.sobotik.widgetschedule;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class ScheduleWidget extends AppWidgetProvider {
    public static final String ACTION_REFRESH = "actionRefresh";
    public static int widgetId = 0;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.schedule_widget);

        views.setRemoteAdapter(R.id.calendar_listview, new Intent(context, ScheduleWidgetCalendarService.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.calendar_listview);

        Intent refreshIntent = new Intent(context, ScheduleWidget.class);
        refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { appWidgetId });

        refreshIntent.setAction(ACTION_REFRESH);

        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent);
        views.setPendingIntentTemplate(R.id.widget_refresh_button, refreshPendingIntent);

        widgetId = appWidgetId;

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.schedule_widget);
            appWidgetManager.updateAppWidget(appWidgetId, null);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_REFRESH.equals(intent.getAction())) {
            Log.d("Custom Logging", "CLICK");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

            if (appWidgetIds != null) {
                for (int appWidgetId : appWidgetIds) {
                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.schedule_widget);
                    appWidgetManager.updateAppWidget(appWidgetId, null);
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                    updateAppWidget(context, appWidgetManager, appWidgetId);
                }
            }

        }

    }
    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }
}