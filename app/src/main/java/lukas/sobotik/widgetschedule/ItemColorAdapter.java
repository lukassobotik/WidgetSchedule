package lukas.sobotik.widgetschedule;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;

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

        String colorName = data.get(position);

        TextView textView = convertView.findViewById(R.id.item_color_name);
        textView.setText("");

        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.item_color_view_bottom_sheet_layout, null);
        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.item_color_list_all_drawables);

        TextInputLayout itemEditText = convertView.findViewById(R.id.item_color_output);

        View itemColorView = convertView.findViewById(R.id.item_color_view);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        itemColorView.setOnClickListener(view -> {
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
            ItemColorDrawableAdapter itemColorDrawableAdapter = new ItemColorDrawableAdapter(context, DrawableParser.getAllDrawables());
            itemColorDrawableAdapter.setItemClickListener(clickPosition -> {
                int clickedDrawable = DrawableParser.getAllDrawables().get(clickPosition);
                Log.d("Custom Logging", "getView: " + clickedDrawable);

                String colorCriteria = colorName.split("=")[0];
                String text = colorCriteria + "=" + DrawableParser.getDrawableName(clickedDrawable);
                data.set(position, text);
                Objects.requireNonNull(itemEditText.getEditText()).setText(data.get(position));

                itemColorView.setBackgroundResource(clickedDrawable);

                bottomSheetDialog.dismiss();
            });
            recyclerView.setAdapter(itemColorDrawableAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
        });

        if (colorName.contains("=")) {
            String itemColor = colorName.split("=")[1];
            itemColorView.setBackgroundResource(DrawableParser.getDrawableId(itemColor));
        }
        Objects.requireNonNull(itemEditText.getEditText()).setText(data.get(position));
        return convertView;
    }
}
