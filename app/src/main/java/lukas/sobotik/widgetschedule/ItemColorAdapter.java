package lukas.sobotik.widgetschedule;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ItemColorAdapter extends BaseAdapter {
    private Context context;
    private List<String> data;
    private EditTextChangeListener listener;
    private final String TEMPORARY_EDITTEXT_TAG = "temporary_ignore_edittext_change";
    public ItemColorAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }

    public static class EditTextChangeListener {
        public void onTextChanged(int position, String newText) {
            // Handle the text change event
        }
    }

    public void setEditTextChangeListener(EditTextChangeListener listener) {
        this.listener = listener;
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

        EditText editText = itemEditText.getEditText();

        // Add text change listener
        if (editText == null) return convertView;

        // Remove the previous TextWatcher
        if (editText.getTag() instanceof TextWatcher) {
            editText.removeTextChangedListener((TextWatcher) editText.getTag());
        } else {
            editText.setTag(position);
        }

        if ((int) editText.getTag() == position) {
            // Set the text without triggering the TextWatcher
            int oldTag = (int) editText.getTag();
            editText.setTag(TEMPORARY_EDITTEXT_TAG);
            editText.setText(data.get(position));
            editText.setTag(oldTag);
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed, but required to implement
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.getTag() != TEMPORARY_EDITTEXT_TAG) {
                    int position = (int) editText.getTag();
                    Log.d("Custom Logging", "position: " + position + " s: " + s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText.getTag() != TEMPORARY_EDITTEXT_TAG) {
                    data.set(position, s.toString());
                    listener.onTextChanged(position, s.toString());
                }
            }
        });

        return convertView;
    }
}
