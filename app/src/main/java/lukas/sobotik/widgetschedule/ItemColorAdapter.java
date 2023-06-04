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

import java.util.List;

public class ItemColorAdapter extends BaseAdapter {
    private Context context;
    private List<String> data;
    private EditTextChangeListener listener;
    private final String TEMPORARY_EDITTEXT_TAG = "temporary_ignore_edittext_change";
    String itemColorString = "";
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
        itemColorString = getItemColorString(colorName);
        TextInputLayout itemEditText = convertView.findViewById(R.id.item_color_output);
        EditText editText = itemEditText.getEditText();

        TextView textView = convertView.findViewById(R.id.item_color_name);
        textView.setText("");

        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.item_color_view_bottom_sheet_layout, null);
        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.item_color_list_all_drawables);

        View itemColorView = convertView.findViewById(R.id.item_color_view);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        itemColorView.setOnClickListener(view -> {
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
            ItemColorDrawableAdapter itemColorDrawableAdapter = new ItemColorDrawableAdapter(context, DrawableParser.getAllDrawables());
            itemColorDrawableAdapter.setItemClickListener(clickPosition -> {
                int clickedDrawable = DrawableParser.getAllDrawables().get(clickPosition);

                String colorCriteria = getItemColorCriteria(data.get(position));
                String text = colorCriteria + "=" + DrawableParser.getDrawableName(clickedDrawable);
                data.set(position, text);
                if(editText != null) {
                    editText.setText(colorCriteria);
                }
                textView.setText(text);
                itemColorString = DrawableParser.getDrawableName(clickedDrawable);

                itemColorView.setBackgroundResource(clickedDrawable);

                bottomSheetDialog.dismiss();
            });
            recyclerView.setAdapter(itemColorDrawableAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
        });

        if (data.get(position).contains("=")) {
            String itemColor = getItemColorString(data.get(position));
            try {
                itemColorView.setBackgroundResource(DrawableParser.getDrawableId(itemColor));
            } catch (Exception e) {
                Log.e("Custom Logging", "Error while setting background resource", e);
            }
        }

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
            String colorCriteria = getItemColorCriteria(data.get(position));
            editText.setText(colorCriteria);
            editText.setTag(oldTag);
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.getTag() != TEMPORARY_EDITTEXT_TAG) {
                    int position = (int) editText.getTag();
                    String string = s.toString();
                    if (data.get(position).contains("=")) {
                        itemColorString = getItemColorString(data.get(position));
                        string = s + "=" + itemColorString;
                    }

                    data.set(position, string);
                    listener.onTextChanged(position, string);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return convertView;
    }

    public String getItemColorString(String s) {
        if (s.contains("=")) {
            String[] parts = s.split("=");
            return parts[parts.length - 1];
        } else {
            return null;
        }
    }
    public String getItemColorCriteria(String s) {
        String[] parts = s.split("=");

        StringBuilder resultBuilder = new StringBuilder();

        for (int i = 0; i < parts.length - 1; i++) {
            resultBuilder.append(parts[i]);
            if (i != parts.length - 2) {
                resultBuilder.append("=");
            }
        }

        if (parts.length == 1) {
            resultBuilder.append(parts[0]);
        }

        return resultBuilder.toString().trim();
    }
}
