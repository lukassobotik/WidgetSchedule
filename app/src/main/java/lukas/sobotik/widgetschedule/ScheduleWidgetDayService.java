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

public class ScheduleWidgetDayService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("Custom Logging", "Day Service Creation Log");
        return new ScheduleWidgetDayFactory(this.getApplicationContext(), intent);
    }

    static class ScheduleWidgetDayFactory implements RemoteViewsFactory {

        private final Context context;
        private int appWidgetId;
        private List<CalendarDay> data;

        ScheduleWidgetDayFactory(Context context, Intent intent) {
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
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.day_item);
            Log.d("Custom Logging", "Day Service Log");
            remoteViews.setTextViewText(R.id.day_subject, data.get(position).getFormattedDay());
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 3;
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
