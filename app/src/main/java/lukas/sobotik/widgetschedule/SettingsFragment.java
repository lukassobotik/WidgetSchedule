package lukas.sobotik.widgetschedule;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputLayout;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TextInputLayout scheduleURL, itemColors;
    Button saveButton;
    LinearLayout containsWeekDayLayout, removeEmptyItemsLayout, hideLastTableLayout;
    MaterialSwitch containsWeekDaySwitch, removeEmptyItemsSwitch, hideLastTableSwitch;
    SettingsDatabaseHelper settingsDatabaseHelper;
    ScheduleDatabaseHelper scheduleDatabaseHelper;
    List<SettingsEntry> settingsList;
    List<ScheduleEntry> scheduleList;
    List<String> HTMLTables;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        View inflatedView = inflater.inflate(R.layout.fragment_settings, container, false);

        settingsDatabaseHelper = new SettingsDatabaseHelper(getContext());
        scheduleDatabaseHelper = new ScheduleDatabaseHelper(getContext());
        settingsList = new ArrayList<>();
        scheduleList = new ArrayList<>();
        HTMLTables = new ArrayList<>();

        scheduleURL = inflatedView.findViewById(R.id.schedule_url_text_input);
        saveButton = inflatedView.findViewById(R.id.database_save_button);
        containsWeekDayLayout = inflatedView.findViewById(R.id.contains_day_of_week_layout);
        containsWeekDaySwitch = inflatedView.findViewById(R.id.contains_day_of_week_switch);
        removeEmptyItemsLayout = inflatedView.findViewById(R.id.remove_empty_items_layout);
        removeEmptyItemsSwitch = inflatedView.findViewById(R.id.remove_empty_items_switch);
        hideLastTableLayout = inflatedView.findViewById(R.id.hide_last_table_layout);
        hideLastTableSwitch = inflatedView.findViewById(R.id.hide_last_table_switch);
        itemColors = inflatedView.findViewById(R.id.color_text_input);

        loadDataFromDatabase();

        saveButton.setOnClickListener(v -> {
            String scheduleLink = Objects.requireNonNull(scheduleURL.getEditText()).getText().toString().toLowerCase().trim();
            String itemColorsSource = Objects.requireNonNull(itemColors.getEditText()).getText().toString().toLowerCase().trim();

            settingsDatabaseHelper.addItem(new SettingsEntry(new Settings().ScheduleURL, scheduleLink));
            settingsDatabaseHelper.addItem(new SettingsEntry(new Settings().ContainsDayOfWeek, String.valueOf(containsWeekDaySwitch.isChecked())));
            settingsDatabaseHelper.addItem(new SettingsEntry(new Settings().RemoveEmptyItems, String.valueOf(removeEmptyItemsSwitch.isChecked())));
            settingsDatabaseHelper.addItem(new SettingsEntry(new Settings().HideLastTable, String.valueOf(hideLastTableSwitch.isChecked())));
            settingsDatabaseHelper.addItem(new SettingsEntry(new Settings().ItemColors, itemColorsSource));
            fetchDataFromURL(scheduleLink);
        });

        containsWeekDayLayout.setOnClickListener(view -> {
            containsWeekDaySwitch.setChecked(!containsWeekDaySwitch.isChecked());
            settingsDatabaseHelper.addItem(new SettingsEntry(new Settings().ContainsDayOfWeek, String.valueOf(containsWeekDaySwitch.isChecked())));
        });
        removeEmptyItemsLayout.setOnClickListener(view -> {
            removeEmptyItemsSwitch.setChecked(!removeEmptyItemsSwitch.isChecked());
            settingsDatabaseHelper.addItem(new SettingsEntry(new Settings().RemoveEmptyItems, String.valueOf(removeEmptyItemsSwitch.isChecked())));
        });
        hideLastTableLayout.setOnClickListener(view -> {
            hideLastTableSwitch.setChecked(!hideLastTableSwitch.isChecked());
            settingsDatabaseHelper.addItem(new SettingsEntry(new Settings().HideLastTable, String.valueOf(hideLastTableSwitch.isChecked())));
        });

        return inflatedView;
    }

    private void loadDataFromDatabase() {
        Cursor settingsCursor = settingsDatabaseHelper.readAllData();
        if (settingsCursor.getCount() == 0) {
            return;
        }

        while (settingsCursor.moveToNext()) {
            int settings = -1;
            if (Objects.equals(settingsCursor.getString(1), String.valueOf(new Settings().ScheduleURL))) {
                settings = new Settings().ScheduleURL;
            } else if (Objects.equals(settingsCursor.getString(1), String.valueOf(new Settings().ContainsDayOfWeek))) {
                settings = new Settings().ContainsDayOfWeek;
            } else if (Objects.equals(settingsCursor.getString(1), String.valueOf(new Settings().RemoveEmptyItems))) {
                settings = new Settings().RemoveEmptyItems;
            } else if (Objects.equals(settingsCursor.getString(1), String.valueOf(new Settings().HideLastTable))) {
                settings = new Settings().HideLastTable;
            } else if (Objects.equals(settingsCursor.getString(1), String.valueOf(new Settings().ItemColors))) {
                settings = new Settings().ItemColors;
            }

            settingsList.add(new SettingsEntry(settings, settingsCursor.getString(2), Integer.parseInt(settingsCursor.getString(0))));
        }

        for (SettingsEntry entry : settingsList) {
            if (String.valueOf(entry.getSettingName()).equals(String.valueOf(new Settings().ScheduleURL))) {
                Objects.requireNonNull(scheduleURL.getEditText()).setText(entry.getValue());
            } else if (String.valueOf(entry.getSettingName()).equals(String.valueOf(new Settings().ContainsDayOfWeek))) {
                boolean isChecked = entry.getValue().equals("true");
                containsWeekDaySwitch.setChecked(isChecked);
            } else if (String.valueOf(entry.getSettingName()).equals(String.valueOf(new Settings().RemoveEmptyItems))) {
                boolean isChecked = entry.getValue().equals("true");
                removeEmptyItemsSwitch.setChecked(isChecked);
            } else if (String.valueOf(entry.getSettingName()).equals(String.valueOf(new Settings().HideLastTable))) {
                boolean isChecked = entry.getValue().equals("true");
                hideLastTableSwitch.setChecked(isChecked);
            } else if (String.valueOf(entry.getSettingName()).equals(String.valueOf(new Settings().ItemColors))) {
                Objects.requireNonNull(itemColors.getEditText()).setText(entry.getValue());
            }
        }

        Cursor scheduleCursor = scheduleDatabaseHelper.readAllData();
        if (scheduleCursor.getCount() == 0) {
            return;
        }

        while (scheduleCursor.moveToNext()) {
            scheduleList.add(new ScheduleEntry(Integer.parseInt(scheduleCursor.getString(0)), scheduleCursor.getString(1), scheduleCursor.getString(2)));
        }
    }

    public void fetchDataFromURL(String url) {
        Thread thread = new Thread(() -> {
            try  {
                Log.d("Custom Logging", "Fetching Data...");
                try {
                    Document doc = Jsoup.connect(url).get();
                    scheduleDatabaseHelper.addItem(new ScheduleEntry(url, doc.html()));
                } catch (IOException e) {
                    Log.d("Custom Logging", "error " + e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }
}