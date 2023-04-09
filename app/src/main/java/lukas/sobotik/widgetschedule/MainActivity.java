package lukas.sobotik.widgetschedule;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputLayout;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    TextInputLayout scheduleURL;
    Button saveButton;
    LinearLayout containsWeekDayLayout, removeEmptyItemsLayout, hideLastTableLayout;
    MaterialSwitch containsWeekDaySwitch, removeEmptyItemsSwitch, hideLastTableSwitch;

    SettingsDatabaseHelper settingsDatabaseHelper;
    ScheduleDatabaseHelper scheduleDatabaseHelper;
    List<SettingsEntry> settingsList;
    List<ScheduleEntry> scheduleList;
    List<String> HTMLTables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        loadDataFromDatabase();

        saveButton.setOnClickListener(v -> {
            String scheduleLink = Objects.requireNonNull(scheduleURL.getEditText()).getText().toString().toLowerCase().trim();

            Log.d("Custom Logging", String.valueOf(containsWeekDaySwitch.isChecked()));
            Log.d("Custom Logging", String.valueOf(removeEmptyItemsSwitch.isChecked()));
            Log.d("Custom Logging", String.valueOf(hideLastTableSwitch.isChecked()));

            settingsDatabaseHelper.addItem(new SettingsEntry(new Settings().ScheduleURL, scheduleLink));
            settingsDatabaseHelper.addItem(new SettingsEntry(new Settings().ContainsDayOfWeek, String.valueOf(containsWeekDaySwitch.isChecked())));
            settingsDatabaseHelper.addItem(new SettingsEntry(new Settings().RemoveEmptyItems, String.valueOf(removeEmptyItemsSwitch.isChecked())));
            settingsDatabaseHelper.addItem(new SettingsEntry(new Settings().HideLastTable, String.valueOf(hideLastTableSwitch.isChecked())));
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
            }
        }

        Cursor scheduleCursor = scheduleDatabaseHelper.readAllData();
        if (scheduleCursor.getCount() == 0) {
            return;
        }

        while (scheduleCursor.moveToNext()) {
            scheduleList.add(new ScheduleEntry(Integer.parseInt(scheduleCursor.getString(0)), scheduleCursor.getString(1), scheduleCursor.getString(2)));

            TextView textView = findViewById(R.id.schedule_html);
            textView.setText(scheduleCursor.getString(2));
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

    public void initialize() {
        settingsDatabaseHelper = new SettingsDatabaseHelper(this);
        scheduleDatabaseHelper = new ScheduleDatabaseHelper(this);
        settingsList = new ArrayList<>();
        scheduleList = new ArrayList<>();
        HTMLTables = new ArrayList<>();

        scheduleURL = findViewById(R.id.schedule_url_text_input);
        saveButton = findViewById(R.id.database_save_button);
        containsWeekDayLayout = findViewById(R.id.contains_day_of_week_layout);
        containsWeekDaySwitch = findViewById(R.id.contains_day_of_week_switch);
        removeEmptyItemsLayout = findViewById(R.id.remove_empty_items_layout);
        removeEmptyItemsSwitch = findViewById(R.id.remove_empty_items_switch);
        hideLastTableLayout = findViewById(R.id.hide_last_table_layout);
        hideLastTableSwitch = findViewById(R.id.hide_last_table_switch);
    }
}