package lukas.sobotik.widgetschedule.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import lukas.sobotik.widgetschedule.R;

import java.util.List;

public class ScheduleAdapter extends BaseAdapter {
    private Context context;
    private List<String> data;

    public ScheduleAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.schedule_list_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.schedule_list_item);
        textView.setText("");

        WebView webView = convertView.findViewById(R.id.schedule_item_web_view);
        webView.loadDataWithBaseURL(null, data.get(position), "text/html", "UTF-8", null);
        return convertView;
    }
}
