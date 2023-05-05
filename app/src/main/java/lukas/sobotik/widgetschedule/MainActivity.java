package lukas.sobotik.widgetschedule;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        SettingsFragment settingsFragment = new SettingsFragment();
        ScheduleFragment scheduleFragment = new ScheduleFragment();
        ColorFragment colorFragment = new ColorFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.schedule_fragment_container, settingsFragment);
        transaction.add(R.id.schedule_fragment_container, scheduleFragment);
        transaction.add(R.id.schedule_fragment_container, colorFragment);
        transaction.hide(scheduleFragment);
        transaction.hide(colorFragment);
        transaction.commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (item.getItemId() == R.id.bottom_navigation_settings) {
                fragmentTransaction.show(settingsFragment);
                fragmentTransaction.hide(colorFragment);
                fragmentTransaction.hide(scheduleFragment);
                fragmentTransaction.commit();
                return true;
            } else if (item.getItemId() == R.id.bottom_navigation_schedule) {
                fragmentTransaction.hide(settingsFragment);
                fragmentTransaction.hide(colorFragment);
                fragmentTransaction.show(scheduleFragment);
                fragmentTransaction.commit();
                return true;
            } else if (item.getItemId() == R.id.bottom_navigation_colors) {
                fragmentTransaction.hide(settingsFragment);
                fragmentTransaction.show(colorFragment);
                fragmentTransaction.hide(scheduleFragment);
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