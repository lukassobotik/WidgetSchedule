package lukas.sobotik.widgetschedule;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

        public static boolean swapData() {
            List<CalendarEvent> temp = allEvents;
            allEvents = currentEvents;
            currentEvents = temp;
            ignoreDatasetChanged = true;
            isShowingAllEvents = !isShowingAllEvents;
            Log.d("Custom Logging", "Data swapped");
            return isShowingAllEvents;
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
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                if (isShowingAllEvents) {
                    Toast.makeText(context, "Showing All Events.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "Showing Only Current Events.", Toast.LENGTH_SHORT).show();

                }
            });
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
                        event.setDrawableId(getDrawableId(colorMap.get(eventName)));
                    }
                }
            } catch (Exception e) {
                Log.e("DATABASE ERROR", e.getMessage());
            }
        }

        private int getDrawableId(String s) {
            int drawableId = -1;
            switch (s) {
                case "red":
                    drawableId = R.drawable.rounded_background_red;
                    break;
                case "pink":
                    drawableId = R.drawable.rounded_background_pink;
                    break;
                case "orange":
                    drawableId = R.drawable.rounded_background_orange;
                    break;
                case "lime":
                    drawableId = R.drawable.rounded_background_lime;
                    break;
                case "green":
                    drawableId = R.drawable.rounded_background_green;
                    break;
                case "teal":
                    drawableId = R.drawable.rounded_background_teal;
                    break;
                case "cyan":
                    drawableId = R.drawable.rounded_background_cyan;
                    break;
                case "light_blue":
                    drawableId = R.drawable.rounded_background_light_blue;
                    break;
                case "blue":
                    drawableId = R.drawable.rounded_background_blue;
                    break;
                case "purple":
                    drawableId = R.drawable.rounded_background_purple;
                    break;
                case "indigo":
                    drawableId = R.drawable.rounded_background_indigo;
                    break;
                case "deep_pink":
                    drawableId = R.drawable.rounded_background_deep_pink;
                    break;
                case "coral":
                    drawableId = R.drawable.rounded_background_coral;
                    break;
                case "gold":
                    drawableId = R.drawable.rounded_background_gold;
                    break;
                case "silver":
                    drawableId = R.drawable.rounded_background_silver;
                    break;
                case "yellow":
                    drawableId = R.drawable.rounded_background_yellow;
                    break;
            }
            return drawableId;
        }
    }
}
