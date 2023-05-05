package lukas.sobotik.widgetschedule;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ColorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ColorFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    SettingsDatabaseHelper settingsDatabaseHelper;
    ListView itemColorListView;
    TextInputLayout itemColors;
    Button saveButton;

    private String mParam1;
    private String mParam2;

    public ColorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ColorFragment.
     */
    public static ColorFragment newInstance(String param1, String param2) {
        ColorFragment fragment = new ColorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_color, container, false);

        settingsDatabaseHelper = new SettingsDatabaseHelper(getContext());

        itemColors = inflatedView.findViewById(R.id.color_text_input);
        itemColorListView = inflatedView.findViewById(R.id.color_list_view);
        saveButton = inflatedView.findViewById(R.id.color_database_save_button);

        loadDataFromDatabase();

        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("a");
        list.add("a");
        list.add("a");
        list.add("a");
        list.add("a");
        list.add("a");
        list.add("a");
        list.add("a");
        list.add("a");

        ItemColorAdapter colorAdapter = new ItemColorAdapter(getContext(), list);
        itemColorListView.setAdapter(colorAdapter);

        saveButton.setOnClickListener(view -> {
            String itemColorsSource = Objects.requireNonNull(itemColors.getEditText()).getText().toString().toLowerCase().trim();

            settingsDatabaseHelper.addItem(new SettingsEntry(new Settings().ItemColors, itemColorsSource));
        });

        return inflatedView;
    }

    private void loadDataFromDatabase() {
        Cursor settingsCursor = settingsDatabaseHelper.readAllData();
        if (settingsCursor.getCount() == 0) {
            return;
        }

        while (settingsCursor.moveToNext()) {
            if (Objects.equals(settingsCursor.getString(1), String.valueOf(new Settings().ItemColors))) {
                Objects.requireNonNull(itemColors.getEditText()).setText(settingsCursor.getString(2));
            }
        }
    }
}