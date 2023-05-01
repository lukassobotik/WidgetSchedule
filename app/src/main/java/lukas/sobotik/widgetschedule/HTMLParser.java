package lukas.sobotik.widgetschedule;

import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HTMLParser {
    static int columnNumber;
    static int itemsIteration;
    static String rowDate;
    static LocalDate date;
    static List<String> timespans;
    static List<CalendarEvent> calendarEvents;
    static int rowNumber;

    // Database
    static boolean containsDayOfWeek = true;
    static boolean removeEmptyItems = true;
    static boolean doNotShowLastTable = true;

    static List<CalendarEvent> data = new ArrayList<>();

    public static List<CalendarEvent> parseSchedule(ScheduleEntry entry, boolean containsDayOfWeek, boolean removeEmptyItems, boolean doNotShowLastTable) {
        data = new ArrayList<>();
        HTMLParser.containsDayOfWeek = containsDayOfWeek;
        HTMLParser.removeEmptyItems = removeEmptyItems;
        HTMLParser.doNotShowLastTable = doNotShowLastTable;

        String html = entry.getScheduleHTML();
        Document document = Jsoup.parse(html);
        Elements tables = document.getElementsByTag("table");

        for (Element table : tables) {
            if (doNotShowLastTable && table.html().equals(Objects.requireNonNull(tables.last()).html())) continue;

            Elements trs = table.getElementsByTag("tr");
            rowNumber = 0;
            timespans = new ArrayList<>();
            calendarEvents = new ArrayList<>();

            loopRows(trs);

            renderData();
            Log.d("Custom Logging", "-------------------------");
        }

        return data;
    }

    private static void loopRows(Elements trs) {
        for (Element tr : trs) {
            rowNumber += 1;
            Elements tds = tr.children();

            columnNumber = 0;
            itemsIteration = 0;
            rowDate = null;
            date = null;

            loopRowItems(tds, rowNumber);
        }
    }

    public static void loopRowItems(Elements tds, int rowNumber) {
        for (Element td : tds) {
            if (td.html().startsWith("<input") || td.html().equals("&nbsp;")) continue;

            if (rowNumber == 1) {
                timespans.add(td.html());
            }

            if (columnNumber == 0 && !td.ownText().isEmpty() && rowNumber > 1) {
                rowDate = td.ownText();
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");
                    date = LocalDate.parse(rowDate, formatter);
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage());
                }
            }

            if (rowNumber > 1 && columnNumber != 0 && (containsDayOfWeek && columnNumber != 1)) {
                CalendarEvent event = new CalendarEvent(date);
                event.setEventName(td.html());
                event.setTimespan(Objects.requireNonNull(timespans.get(itemsIteration)));
                calendarEvents.add(event);
                itemsIteration++;
            }

            columnNumber += 1;
        }
    }

    private static void renderData() {
        CalendarEvent previousEvent = null;
        for (CalendarEvent event : calendarEvents) {
            if (removeEmptyItems && (event.getEventName().equals("-") || event.getEventName().equals(" "))) continue;

            if (previousEvent == null) {
                event.setOnTheSameDayAsEventAbove(false);
                data.add(event);
            } else if (event.getDate() != null){
                event.setOnTheSameDayAsEventAbove(previousEvent.getDate().equals(event.getDate()));
                data.add(event);
            }

            previousEvent = event;
        }
    }
}
