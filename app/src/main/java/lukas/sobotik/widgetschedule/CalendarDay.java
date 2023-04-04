package lukas.sobotik.widgetschedule;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class CalendarDay {

    private LocalDate date;
    private String day;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CalendarDay(LocalDate date) {
        this.date = date;
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

    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("MMMM d"));
    }

}
