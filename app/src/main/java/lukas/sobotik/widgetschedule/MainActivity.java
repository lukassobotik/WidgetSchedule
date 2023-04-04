package lukas.sobotik.widgetschedule;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextInputLayout scheduleURL;
    Button saveButton;
    DatabaseHelper databaseHelper;
    List<SettingsEntry> settingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        loadDataFromDatabase();

        saveButton.setOnClickListener(v -> {
            databaseHelper.addItem(new SettingsEntry(Settings.ScheduleURL, Objects.requireNonNull(scheduleURL.getEditText()).getText().toString().toLowerCase().trim()));
        });
    }

    private void loadDataFromDatabase() {
        Cursor cursor = databaseHelper.readAllData();
        if (cursor.getCount() == 0) {
            return;
        }

        while (cursor.moveToNext()) {
            Settings settings = null;
            if (Objects.equals(cursor.getString(1), Settings.ScheduleURL.toString())) {
                settings = Settings.ScheduleURL;
            }

            settingsList.add(new SettingsEntry(Integer.parseInt(cursor.getString(0)), Objects.requireNonNull(settings), cursor.getString(2)));
        }

        for (SettingsEntry entry : settingsList) {
            Log.d("Custom Logging", String.valueOf(entry.getId()));
            Log.d("Custom Logging", entry.getSettingName().toString());
            Log.d("Custom Logging", entry.getValue());

            if (entry.getSettingName().equals(Settings.ScheduleURL)) {
                Objects.requireNonNull(scheduleURL.getEditText()).setText(entry.getValue());
            }
        }
    }

    public void initialize() {
        databaseHelper = new DatabaseHelper(this);
        settingsList = new ArrayList<>();

        scheduleURL = findViewById(R.id.schedule_url_text_input);
        saveButton = findViewById(R.id.database_save_button);
    }
}