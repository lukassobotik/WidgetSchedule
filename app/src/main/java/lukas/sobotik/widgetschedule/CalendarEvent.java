package lukas.sobotik.widgetschedule;

import android.util.Log;

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
        try {
            return date.format(DateTimeFormatter.ofPattern("d"));
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
            return "";
        }
    }
    public String getFormattedMonth() {
        try {
            return date.format(DateTimeFormatter.ofPattern("MMM"));
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
            return "";
        }
    }
    public String getFormattedWeekDay() {
        try {
            return date.format(DateTimeFormatter.ofPattern("EEE"));
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
            return "";
        }
    }
    public String getShortFormattedWeekDay() {
        try {
            return date.format(DateTimeFormatter.ofPattern("EEE")).substring(0, 1);
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
            return "";
        }
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
    public int getDrawableId() {
        return drawableId;
    }
    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    private int id;
    private int drawableId = -1;
    private LocalDate date;
    private String timespan;
    private String eventName;
    private boolean isOnTheSameDayAsEventAbove;
}
