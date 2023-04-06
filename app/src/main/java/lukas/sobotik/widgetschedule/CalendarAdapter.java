package lukas.sobotik.widgetschedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CalendarAdapter extends BaseAdapter {
    private List<CalendarEvent> data;
    private LayoutInflater inflater;

    public CalendarAdapter(Context context, List<CalendarEvent> data) {
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    // Override other methods of BaseAdapter here
    // getItem(), getItemId(), getView() and getCount()

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.calendar_item, null);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.schedule_date);
            holder.description = convertView.findViewById(R.id.schedule_day);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CalendarEvent item = data.get(position);
        holder.title.setText(item.getDate().getDayOfMonth());
        holder.description.setText(item.getDay());

        return convertView;
    }

    static class ViewHolder {
        TextView title;
        TextView description;
    }
}
