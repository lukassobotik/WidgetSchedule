package lukas.sobotik.widgetschedule;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
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
            CalendarEvent exampleEvent = new CalendarEvent(LocalDate.now());
            exampleEvent.setEventName(context.getResources().getString(R.string.schedule_item_title_example));
            exampleEvent.setTimespan(context.getResources().getString(R.string.schedule_item_timespan_example));

            data = new ArrayList<>();
            data.add(exampleEvent);
            data.add(exampleEvent);
            data.add(exampleEvent);
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
    }
}
