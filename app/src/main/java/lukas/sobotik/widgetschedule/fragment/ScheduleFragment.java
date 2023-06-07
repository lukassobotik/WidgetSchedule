package lukas.sobotik.widgetschedule.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import lukas.sobotik.widgetschedule.R;
import lukas.sobotik.widgetschedule.adapter.ScheduleAdapter;
import lukas.sobotik.widgetschedule.database.ScheduleDatabaseHelper;
import lukas.sobotik.widgetschedule.entity.ScheduleEntry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {
    List<ScheduleEntry> scheduleList;
    ScheduleDatabaseHelper scheduleDatabaseHelper;
    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_schedule, container, false);

        ListView listView = inflatedView.findViewById(R.id.schedule_list_view);

        scheduleList = new ArrayList<>();
        scheduleDatabaseHelper = new ScheduleDatabaseHelper(getContext());
        loadDataFromDatabase();

        List<String> tableHTMLs = new ArrayList<>();
        for (ScheduleEntry entry : scheduleList) {
            Document document = Jsoup.parse(entry.getScheduleHTML());
            Elements tables = document.getElementsByTag("table");

            for (Element element : tables) {
                tableHTMLs.add(element.outerHtml());
            }
        }

        ScheduleAdapter adapter = new ScheduleAdapter(getActivity(), tableHTMLs);

        listView.setAdapter(adapter);

        return inflatedView;
    }

    private void loadDataFromDatabase() {
        Cursor scheduleCursor = scheduleDatabaseHelper.readAllData();
        if (scheduleCursor.getCount() == 0) {
            return;
        }

        while (scheduleCursor.moveToNext()) {
            scheduleList.add(new ScheduleEntry(Integer.parseInt(scheduleCursor.getString(0)), scheduleCursor.getString(1), scheduleCursor.getString(2)));
        }
    }
}