package lukas.sobotik.widgetschedule;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ScheduleWidgetCalendarService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ScheduleWidgetCalendarFactory(this.getApplicationContext(), intent);
    }

    static class ScheduleWidgetCalendarFactory implements RemoteViewsFactory {

        private Context context;
        private int appWidgetId;
        private List<CalendarEvent> data;

        ScheduleWidgetCalendarFactory(Context context, Intent intent) {
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            CalendarEvent exampleEvent1 = new CalendarEvent(LocalDate.now());
            exampleEvent1.setEventName(context.getResources().getString(R.string.schedule_item_title_example));
            exampleEvent1.setTimespan(context.getResources().getString(R.string.schedule_item_timespan_example));
            exampleEvent1.setOnTheSameDayAsEventAbove(false);

            CalendarEvent exampleEvent2 = new CalendarEvent(LocalDate.now());
            exampleEvent2.setEventName(context.getResources().getString(R.string.schedule_item_title_example));
            exampleEvent2.setTimespan(context.getResources().getString(R.string.schedule_item_timespan_example));
            exampleEvent2.setOnTheSameDayAsEventAbove(exampleEvent1.getDate().equals(exampleEvent2.getDate()));

            CalendarEvent exampleEvent3 = new CalendarEvent(LocalDate.now());
            exampleEvent3.setEventName("Different Meeting");
            exampleEvent3.setTimespan(context.getResources().getString(R.string.schedule_item_timespan_example));
            exampleEvent3.setOnTheSameDayAsEventAbove(exampleEvent2.getDate().equals(exampleEvent3.getDate()));

            CalendarEvent exampleEvent4 = new CalendarEvent(LocalDate.now().plusDays(1));
            exampleEvent4.setEventName("Different Meeting");
            exampleEvent4.setTimespan(context.getResources().getString(R.string.schedule_item_timespan_example));
            exampleEvent4.setOnTheSameDayAsEventAbove(exampleEvent3.getDate().equals(exampleEvent4.getDate()));

            data = new ArrayList<>();
            data.add(exampleEvent1);
            data.add(exampleEvent2);
            data.add(exampleEvent3);
            data.add(exampleEvent4);
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

            remoteViews = groupSimilarEvents(remoteViews, position);

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

        public RemoteViews groupSimilarEvents(RemoteViews remoteViews, int position) {
            CalendarEvent event = data.get(position);

            if (event.isOnTheSameDayAsEventAbove()) {
                remoteViews.setViewVisibility(R.id.schedule_date_layout, View.INVISIBLE);
                int padding = (int) (8 * Resources.getSystem().getDisplayMetrics().density);
                remoteViews.setViewPadding(R.id.calendar_item_parent_layout, padding, 0, padding, padding);
            }

            return remoteViews;
        }
    }
}
