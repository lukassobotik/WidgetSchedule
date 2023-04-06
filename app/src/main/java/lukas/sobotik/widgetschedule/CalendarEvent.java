package lukas.sobotik.widgetschedule;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CalendarEvent {
    public CalendarEvent(LocalDate date) {
        this.date = date;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public String getDay() {
        return date.getDayOfWeek().toString();
    }
    public String getFormattedDay() {
        return date.format(DateTimeFormatter.ofPattern("d"));
    }
    public String getFormattedMonth() {
        return date.format(DateTimeFormatter.ofPattern("MMM"));
    }
    public String getTimespan() {
        return timespan;
    }
    public void setTimespan(String timespan) {
        this.timespan = timespan;
    }
    public String getEventName() {
        return eventName;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public boolean isOnTheSameDayAsEventAbove() {
        return isOnTheSameDayAsEventAbove;
    }
    public void setOnTheSameDayAsEventAbove(boolean onTheSameDayAsEventAbove) {
        isOnTheSameDayAsEventAbove = onTheSameDayAsEventAbove;
    }

    private int id;
    private LocalDate date;
    private String timespan;
    private String eventName;
    private boolean isOnTheSameDayAsEventAbove;
}
