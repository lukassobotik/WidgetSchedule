package lukas.sobotik.widgetschedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemColorAdapter extends BaseAdapter {
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

        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.item_color_view_bottom_sheet_layout, null);
        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.item_color_list_all_drawables);

        List<Integer> data = new ArrayList<>();
        data.add(R.drawable.rounded_background_blue);
        data.add(R.drawable.rounded_background_coral);
        data.add(R.drawable.rounded_background_cyan);
        data.add(R.drawable.rounded_background_deep_pink);
        data.add(R.drawable.rounded_background_gold);
        data.add(R.drawable.rounded_background_green);
        data.add(R.drawable.rounded_background_indigo);
        data.add(R.drawable.rounded_background_light_blue);
        data.add(R.drawable.rounded_background_lime);
        data.add(R.drawable.rounded_background_orange);
        data.add(R.drawable.rounded_background_pink);
        data.add(R.drawable.rounded_background_purple);
        data.add(R.drawable.rounded_background_red);
        data.add(R.drawable.rounded_background_silver);
        data.add(R.drawable.rounded_background_teal);
        data.add(R.drawable.rounded_background_yellow);

        View itemColorView = convertView.findViewById(R.id.item_color_view);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        itemColorView.setOnClickListener(view -> {
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
            ItemColorDrawableAdapter itemColorDrawableAdapter = new ItemColorDrawableAdapter(context, data);
            recyclerView.setAdapter(itemColorDrawableAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        });

        TextInputLayout editText = convertView.findViewById(R.id.item_color_output);
        Objects.requireNonNull(editText.getEditText()).setText(data.get(position));
        return convertView;
    }
}
