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

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static lukas.sobotik.widgetschedule.DrawableParser.getDrawableId;

public class ScheduleWidgetCalendarService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ScheduleWidgetCalendarFactory(this.getApplicationContext(), intent);
    }

    static class ScheduleWidgetCalendarFactory implements RemoteViewsFactory {

        private final Context context;
        private int appWidgetId;
        private static List<CalendarEvent> allEvents;
        private static List<CalendarEvent> currentEvents;
        private List<String> colorList;

        // Database
        static boolean containsDayOfWeek = true;
        static boolean removeEmptyItems = true;
        static boolean doNotShowLastTable = true;
        static String itemColors = "";

        static boolean ignoreDatasetChanged = false;
        static boolean isShowingAllEvents = true;

        ScheduleWidgetCalendarFactory(Context context, Intent intent) {
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        public static void swapData(int[] appWidgetIds, Context context) {
            isShowingAllEvents = !isShowingAllEvents;
            List<CalendarEvent> temp = allEvents;
            allEvents = currentEvents;
            currentEvents = temp;
            ignoreDatasetChanged = true;

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.schedule_widget);
            if (isShowingAllEvents) {
                views.setTextViewText(R.id.widget_current_items_button, "All");
            } else {
                views.setTextViewText(R.id.widget_current_items_button, "Upcoming");
            }
            for (int appWidgetId : appWidgetIds) {
                AppWidgetManager.getInstance(context).partiallyUpdateAppWidget(appWidgetId, views);
            }
        }

        @Override
        public void onCreate() {
            allEvents = new ArrayList<>();
            currentEvents = new ArrayList<>();
            colorList = new ArrayList<>();
            loadDataFromDatabase();
        }

        @Override
        public void onDataSetChanged() {
            if (ignoreDatasetChanged) {
                ignoreDatasetChanged = false;
                return;
            }
            Log.d("Custom Logging", "Updating...");
            allEvents = new ArrayList<>();
            loadDataFromDatabase();

            LocalDate currentDate = LocalDate.now();
            currentEvents = allEvents.stream()
                        .filter(event -> event.getDate().isEqual(currentDate) || event.getDate().isAfter(currentDate))
                        .collect(Collectors.toList());
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return allEvents.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.calendar_item);
            remoteViews.setTextViewText(R.id.schedule_day, allEvents.get(position).getFormattedDay());
            remoteViews.setTextViewText(R.id.schedule_date, allEvents.get(position).getFormattedWeekDay());
            remoteViews.setTextViewText(R.id.schedule_item_title, allEvents.get(position).getEventName());
            remoteViews.setTextViewText(R.id.schedule_item_timespan, allEvents.get(position).getTimespan());

            // Group items on the same day
            CalendarEvent event = allEvents.get(position);
            int padding8dp = (int) (8 * Resources.getSystem().getDisplayMetrics().density);
            if (event.isOnTheSameDayAsEventAbove()) {
                remoteViews.setViewVisibility(R.id.schedule_date_layout, View.INVISIBLE);
                remoteViews.setViewPadding(R.id.calendar_item_parent_layout, padding8dp, 0, padding8dp, padding8dp);
            } else {
                remoteViews.setViewVisibility(R.id.schedule_date_layout, View.VISIBLE);
                remoteViews.setViewPadding(R.id.calendar_item_parent_layout, padding8dp, padding8dp, padding8dp, padding8dp);
            }


            // Set background color
            if (event.getDrawableId() > -1) {
                remoteViews.setInt(R.id.schedule_item, "setBackgroundResource", event.getDrawableId());
            } else {
                remoteViews.setInt(R.id.schedule_item, "setBackgroundResource", R.drawable.rounded_background_pink);
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
            return allEvents.get(position).getId();
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
                        String allItems = settingsCursor.getString(2);
                        String[] filteredArray = Arrays.stream(allItems.split(";"))
                                .filter(item -> !item.isEmpty())
                                .toArray(String[]::new);
                        Collections.addAll(colorList, filteredArray);
                    }
                }

                Cursor scheduleCursor = new ScheduleDatabaseHelper(context).readAllData();
                if (scheduleCursor.getCount() == 0) {
                    Log.e("DATABASE ERROR", "Schedule Cursor has no Items");
                    return;
                }

                while (scheduleCursor.moveToNext()) {
                    ScheduleEntry entry = new ScheduleEntry(Integer.parseInt(scheduleCursor.getString(0)), scheduleCursor.getString(1), scheduleCursor.getString(2));
                    allEvents = HTMLParser.parseSchedule(entry, containsDayOfWeek, removeEmptyItems, doNotShowLastTable);
                }

                Map<String, String> colorMap = new HashMap<>();
                for (String item : colorList) {
                    String[] parts = item.split("=");
                    colorMap.put(parts[0].toLowerCase(), parts[1]);
                }

                for (CalendarEvent event : allEvents) {
                    String eventName = event.getEventName().toLowerCase();
                    if (colorMap.containsKey(eventName)) {
                        event.setDrawableId(getDrawableId(Objects.requireNonNull(colorMap.get(eventName))));
                    }
                }
            } catch (Exception e) {
                Log.e("DATABASE ERROR", e.getMessage());
            }
        }
    }
}
