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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        //TODO: Integrate with the database and settings
        boolean containsDayOfWeek = true;
        boolean removeEmptyItems = true;
        boolean doNotShowLastTable = true;
        String timespanSplitter = "-";

        // HTML Data Parsing
        int columnNumber;
        int itemsIteration;
        String rowDate;
        LocalDate date;
        List<String> timespans;
        List<CalendarEvent> calendarEvents;
        int rowNumber;

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
            remoteViews.setTextViewText(R.id.schedule_date, data.get(position).getFormattedMonth());
            remoteViews.setTextViewText(R.id.schedule_item_title, data.get(position).getEventName());
            remoteViews.setTextViewText(R.id.schedule_item_timespan, data.get(position).getTimespan());

            //Group items on the same day
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
                Cursor scheduleCursor = new ScheduleDatabaseHelper(context).readAllData();
                if (scheduleCursor.getCount() == 0) {
                    Log.e("DATABASE ERROR", "Schedule Cursor has no Items");
                    return;
                }

                while (scheduleCursor.moveToNext()) {
                    ScheduleEntry entry = new ScheduleEntry(Integer.parseInt(scheduleCursor.getString(0)), scheduleCursor.getString(1), scheduleCursor.getString(2));
                    parseHTML(entry);
                }

            } catch (Exception e) {
                Log.e("DATABASE ERROR", e.getMessage());
            }
        }

        public void parseHTML(ScheduleEntry entry) {
            String html = entry.getScheduleHTML();
            Document document = Jsoup.parse(html);
            Elements tables = document.getElementsByTag("table");

            for (Element table : tables) {
                if (doNotShowLastTable && table.html().equals(Objects.requireNonNull(tables.last()).html())) return;

                Elements trs = table.getElementsByTag("tr");
                rowNumber = 0;
                timespans = new ArrayList<>();
                calendarEvents = new ArrayList<>();

                loopRows(trs);

                renderData();
                Log.d("Custom Logging", "-------------------------");
            }
        }

        private void loopRows(Elements trs) {
            for (Element tr : trs) {
                rowNumber += 1;
                Elements tds = tr.children();

                columnNumber = 0;
                itemsIteration = 0;
                rowDate = null;
                date = null;

                loopRowItems(tds, rowNumber);
            }
        }

        public void loopRowItems(Elements tds, int rowNumber) {
            for (Element td : tds) {
                if (td.html().startsWith("<input") || td.html().equals("&nbsp;")) continue;

                if (rowNumber == 1) {
                    timespans.add(td.html());
                }

                if (columnNumber == 0 && !td.ownText().isEmpty() && rowNumber > 1) {
                    rowDate = td.ownText();
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");
                        date = LocalDate.parse(rowDate, formatter);
                    } catch (Exception e) {
                        Log.e("ERROR", e.getMessage());
                    }
                }

                if (rowNumber > 1 && columnNumber != 0 && columnNumber != 1) {
                    CalendarEvent event = new CalendarEvent(date);
                    event.setEventName(td.html());
                    event.setTimespan(Objects.requireNonNull(timespans.get(itemsIteration)));
                    calendarEvents.add(event);
                    itemsIteration++;
                }

                columnNumber += 1;
            }
        }

        private void renderData() {
            CalendarEvent previousEvent = null;
            for (CalendarEvent event : calendarEvents) {
                if (removeEmptyItems && (event.getEventName().equals("-") || event.getEventName().equals(" "))) continue;

                if (previousEvent == null) {
                    event.setOnTheSameDayAsEventAbove(false);
                    data.add(event);
                } else if (event.getDate() != null){
                    event.setOnTheSameDayAsEventAbove(previousEvent.getDate().equals(event.getDate()));
                    data.add(event);
                }

                previousEvent = event;
            }
        }
    }
}
