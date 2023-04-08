package lukas.sobotik.widgetschedule;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
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
            settingsDatabaseHelper.addItem(new SettingsEntry(new Settings().ScheduleURL, scheduleLink));
            fetchDataFromURL(scheduleLink);
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
            }

            settingsList.add(new SettingsEntry(Integer.parseInt(settingsCursor.getString(0)), settings, settingsCursor.getString(2)));
        }

        for (SettingsEntry entry : settingsList) {
            Log.d("Custom Logging", String.valueOf(entry.getId()));
            Log.d("Custom Logging", String.valueOf(entry.getSettingName()));
            Log.d("Custom Logging", entry.getValue());

            if (String.valueOf(entry.getSettingName()).equals(String.valueOf(new Settings().ScheduleURL))) {
                Objects.requireNonNull(scheduleURL.getEditText()).setText(entry.getValue());
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
    }
}