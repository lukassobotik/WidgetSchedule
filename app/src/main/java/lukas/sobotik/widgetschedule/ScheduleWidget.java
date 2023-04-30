package lukas.sobotik.widgetschedule;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Objects;

/**
 * Implementation of App Widget functionality.
 */
public class ScheduleWidget extends AppWidgetProvider {
    public static final String ACTION_REFRESH = "lukas.sobotik.WidgetSchedule.REFRESH";
    private static final String ACTION_SHOW_BOTTOM_SHEET = "lukas.sobotik.WidgetSchedule.SHOW_BOTTOM_SHEET";
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
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent);
        views.setPendingIntentTemplate(R.id.widget_refresh_button, refreshPendingIntent);

        widgetId = appWidgetId;

        // Widget Header Click Listening
        Intent popupIntent = new Intent(context, ScheduleWidget.class);
        popupIntent.setAction(ACTION_SHOW_BOTTOM_SHEET);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, popupIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_header, pendingIntent);

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
        } else if (ACTION_SHOW_BOTTOM_SHEET.equals(intent.getAction())) {
            Intent bottomSheetIntent = new Intent(context, BottomSheetActivity.class);
            bottomSheetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            bottomSheetIntent.setAction(ACTION_SHOW_BOTTOM_SHEET);
            context.startActivity(bottomSheetIntent);
        }
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }

    public static class BottomSheetActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Log.d("Custom Logging", "BottomSheetActivity Created.");

            if (getIntent().getAction().equals(ACTION_SHOW_BOTTOM_SHEET)) {
                showBottomSheet();
                Log.d("Custom Logging", "Showing Bottom Sheet...");
            }
        }

        private void showBottomSheet() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

            ListView listView = bottomSheetView.findViewById(R.id.bottom_sheet_list_view);
            listView.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, new String[]{"Item 1", "Item 2", "Item 3"}));
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Log.d("Custom Logging", position + " ");
                finishAffinity();
            });

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        }
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