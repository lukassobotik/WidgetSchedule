package lukas.sobotik.widgetschedule;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ScheduleWidgetCalendarService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("Custom Logging", "Calendar Service Creation Log");
        return new ScheduleWidgetCalendarFactory(this.getApplicationContext(), intent);
    }

    static class ScheduleWidgetCalendarFactory implements RemoteViewsFactory {

        private Context context;
        private int appWidgetId;
        private List<CalendarDay> data;

        ScheduleWidgetCalendarFactory(Context context, Intent intent) {
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            data = new ArrayList<>();
            data.add(new CalendarDay(LocalDate.now()));
            data.add(new CalendarDay(LocalDate.now()));
            data.add(new CalendarDay(LocalDate.now()));
            data.add(new CalendarDay(LocalDate.now()));
            data.add(new CalendarDay(LocalDate.now()));
            data.add(new CalendarDay(LocalDate.now()));
            data.add(new CalendarDay(LocalDate.now()));
            data.add(new CalendarDay(LocalDate.now()));
            data.add(new CalendarDay(LocalDate.now()));
            data.add(new CalendarDay(LocalDate.now()));
            data.add(new CalendarDay(LocalDate.now()));
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
            remoteViews.setTextViewText(R.id.calendar_day, data.get(position).getFormattedDay());
            remoteViews.setTextViewText(R.id.calendar_date, data.get(position).getFormattedMonth());
            Log.d("Custom Logging", "Calendar Service Log 1");
            remoteViews.setRemoteAdapter(R.id.day_listview, new Intent(context, ScheduleWidgetDayService.class));
            Log.d("Custom Logging", "Calendar Service Log 2");
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
