package lukas.sobotik.widgetschedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

public class ItemColorAdapter  extends BaseAdapter {
    private Context context;
    private List<String> data;

    public ItemColorAdapter(Context context, List<String> data) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_color_list_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.item_color_name);
        textView.setText("");

        TextInputLayout editText = convertView.findViewById(R.id.item_color_output);
        Objects.requireNonNull(editText.getEditText()).setText(data.get(position));
        return convertView;
    }
}
