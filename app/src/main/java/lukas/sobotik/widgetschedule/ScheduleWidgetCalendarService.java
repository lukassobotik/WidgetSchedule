package lukas.sobotik.widgetschedule;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScheduleWidgetCalendarService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ScheduleWidgetCalendarFactory(this.getApplicationContext(), intent);
    }

    static class ScheduleWidgetCalendarFactory implements RemoteViewsFactory {

        private Context context;
        private int appWidgetId;
        private List<CalendarEvent> data;

        // Database
        static boolean containsDayOfWeek = true;
        static boolean removeEmptyItems = true;
        static boolean doNotShowLastTable = true;
        static String itemColors = "";

        ScheduleWidgetCalendarFactory(Context context, Intent intent) {
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            data = new ArrayList<>();
            loadDataFromDatabase();
        }

        @Override
        public void onDataSetChanged() {
            Log.d("Custom Logging", "Updating...");
            data = new ArrayList<>();
            loadDataFromDatabase();
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.calendar_item);
            remoteViews.setTextViewText(R.id.schedule_day, data.get(position).getFormattedDay());
            remoteViews.setTextViewText(R.id.schedule_date, data.get(position).getFormattedWeekDay());
            remoteViews.setTextViewText(R.id.schedule_item_title, data.get(position).getEventName());
            remoteViews.setTextViewText(R.id.schedule_item_timespan, data.get(position).getTimespan());

            // Group items on the same day
            CalendarEvent event = data.get(position);
            int padding8dp = (int) (8 * Resources.getSystem().getDisplayMetrics().density);
            if (event.isOnTheSameDayAsEventAbove()) {
                remoteViews.setViewVisibility(R.id.schedule_date_layout, View.INVISIBLE);
                remoteViews.setViewPadding(R.id.calendar_item_parent_layout, padding8dp, 0, padding8dp, padding8dp);
            } else {
                remoteViews.setViewVisibility(R.id.schedule_date_layout, View.VISIBLE);
                remoteViews.setViewPadding(R.id.calendar_item_parent_layout, padding8dp, padding8dp, padding8dp, padding8dp);
            }

            return remoteViews;
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
            return data.get(position).getId();
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        public void loadDataFromDatabase() {
            try {
                Cursor settingsCursor = new SettingsDatabaseHelper(context).readAllData();
                if (settingsCursor.getCount() == 0) {
                    Log.e("DATABASE ERROR", "Settings Cursor has no Items");
                    return;
                }

                while (settingsCursor.moveToNext()) {
                    if (Objects.equals(settingsCursor.getString(1), String.valueOf(new Settings().ContainsDayOfWeek))) {
                        containsDayOfWeek = Boolean.parseBoolean(settingsCursor.getString(2));
                    } else if (Objects.equals(settingsCursor.getString(1), String.valueOf(new Settings().RemoveEmptyItems))) {
                        removeEmptyItems = Boolean.parseBoolean(settingsCursor.getString(2));
                    } else if (Objects.equals(settingsCursor.getString(1), String.valueOf(new Settings().HideLastTable))) {
                        doNotShowLastTable = Boolean.parseBoolean(settingsCursor.getString(2));
                    } else if (Objects.equals(settingsCursor.getString(1), String.valueOf(new Settings().ItemColors))) {
                        itemColors = settingsCursor.getString(2);
                    }
                }

                Cursor scheduleCursor = new ScheduleDatabaseHelper(context).readAllData();
                if (scheduleCursor.getCount() == 0) {
                    Log.e("DATABASE ERROR", "Schedule Cursor has no Items");
                    return;
                }

                while (scheduleCursor.moveToNext()) {
                    ScheduleEntry entry = new ScheduleEntry(Integer.parseInt(scheduleCursor.getString(0)), scheduleCursor.getString(1), scheduleCursor.getString(2));
                    data = HTMLParser.parseSchedule(entry, containsDayOfWeek, removeEmptyItems, doNotShowLastTable);
                }

            } catch (Exception e) {
                Log.e("DATABASE ERROR", e.getMessage());
            }
        }
    }
}
