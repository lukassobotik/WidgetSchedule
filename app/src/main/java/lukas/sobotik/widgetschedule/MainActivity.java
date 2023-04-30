package lukas.sobotik.widgetschedule;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputLayout;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        SettingsFragment settingsFragment = new SettingsFragment();
        ScheduleFragment scheduleFragment = new ScheduleFragment();

        // Add the fragment to the container view with a FragmentTransaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.schedule_fragment_container, settingsFragment);
        transaction.commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (item.getItemId() == R.id.bottom_navigation_settings) {
                Log.d("Custom Logging", "Settings");
                fragmentTransaction.replace(R.id.schedule_fragment_container, settingsFragment);
                fragmentTransaction.commit();
                return true;
            } else if (item.getItemId() == R.id.bottom_navigation_schedule) {
                Log.d("Custom Logging", "Schedule");
                fragmentTransaction.replace(R.id.schedule_fragment_container, scheduleFragment);
                fragmentTransaction.commit();
                return true;
            }
            return false;
        });
    }

    public void initialize() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }
}