package lukas.sobotik.widgetschedule.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import androidx.appcompat.app.AppCompatActivity;
import lukas.sobotik.widgetschedule.*;
import lukas.sobotik.widgetschedule.database.ScheduleDatabaseHelper;
import lukas.sobotik.widgetschedule.database.SettingsDatabaseHelper;
import lukas.sobotik.widgetschedule.entity.ScheduleEntry;
import lukas.sobotik.widgetschedule.entity.Settings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Objects;

public class ScheduleWidget extends AppWidgetProvider {
    public static final String ACTION_REFRESH = "lukas.sobotik.WidgetSchedule.REFRESH";
    public static final String ACTION_CURRENT_ITEMS = "lukas.sobotik.WidgetSchedule.CURRENT_ITEMS";
    public static int widgetId = 0;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.schedule_widget);

        // Set the ListWidgetService intent to act as the adapter for the ListView
        views.setRemoteAdapter(R.id.calendar_listview, new Intent(context, ScheduleWidgetCalendarService.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.calendar_listview);

        // Refresh Button Click Listening
        Intent refreshIntent = new Intent(context, ScheduleWidget.class);
        refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { appWidgetId });
        refreshIntent.setAction(ACTION_REFRESH);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent);
        views.setPendingIntentTemplate(R.id.widget_refresh_button, refreshPendingIntent);

        Intent currentItemsIntent = new Intent(context, ScheduleWidget.class);
        currentItemsIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        currentItemsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { appWidgetId });
        currentItemsIntent.setAction(ACTION_CURRENT_ITEMS);
        PendingIntent currentItemsPendingIntent = PendingIntent.getBroadcast(context, 0, currentItemsIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_current_items_button, currentItemsPendingIntent);
        views.setPendingIntentTemplate(R.id.widget_current_items_button, currentItemsPendingIntent);

        widgetId = appWidgetId;

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.schedule_widget);
            appWidgetManager.updateAppWidget(appWidgetId, null);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.d("Custom Logging", "Enabled");
    }

    @Override
    public void onDisabled(Context context) {
        Log.d("Custom Logging", "Disabled");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_REFRESH.equals(intent.getAction())) {
            Log.d("Custom Logging", "Refreshing...");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

            Intent refreshIntent = new Intent(context, RefreshActivity.class);
            refreshIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            refreshIntent.setAction(ACTION_REFRESH);
            context.startActivity(refreshIntent);

            if (appWidgetIds != null) {
                for (int appWidgetId : appWidgetIds) {
                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.schedule_widget);
                    appWidgetManager.updateAppWidget(appWidgetId, null);
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                    updateAppWidget(context, appWidgetManager, appWidgetId);
                }
            }
        } else if (ACTION_CURRENT_ITEMS.equals(intent.getAction())) {
            ScheduleWidgetCalendarService.ScheduleWidgetCalendarFactory factory =
                    new ScheduleWidgetCalendarService.ScheduleWidgetCalendarFactory(context.getApplicationContext());

            int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

            factory.swapData(appWidgetIds, context);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.calendar_listview);
        }
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }

    public static class RefreshActivity extends AppCompatActivity {
        String scheduleURL = "";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getIntent().getAction().equals(ACTION_REFRESH)) {
                loadDataFromDatabase();
                fetchDataFromURL(scheduleURL);
                finishAffinity();
            }
        }

        public void loadDataFromDatabase() {
            Cursor settingsCursor = new SettingsDatabaseHelper(this).readAllData();
            if (settingsCursor.getCount() == 0) {
                return;
            }

            while (settingsCursor.moveToNext()) {
                if (Objects.equals(settingsCursor.getString(1), String.valueOf(new Settings().ScheduleURL))) {
                    scheduleURL = settingsCursor.getString(2);
                }
            }
        }

        public void fetchDataFromURL(String url) {
            Thread thread = new Thread(() -> {
                try  {
                    Log.d("Custom Logging", "Fetching Data...");
                    try {
                        Document doc = Jsoup.connect(url).get();
                        new ScheduleDatabaseHelper(this).addItem(new ScheduleEntry(url, doc.html()));
                    } catch (IOException e) {
                        Log.d("Custom Logging", "error " + e.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            thread.start();
        }
    }
}