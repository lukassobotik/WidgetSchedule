package lukas.sobotik.widgetschedule.entity;

import androidx.annotation.NonNull;

public class ScheduleEntry {
    private int id;
    private String scheduleName;
    private String scheduleHTML;

    public ScheduleEntry(int id, @NonNull String settingName, @NonNull String scheduleHTML) {
        this.id = id;
        this.scheduleName = settingName;
        this.scheduleHTML = scheduleHTML;
    }

    public ScheduleEntry(@NonNull String scheduleName, @NonNull String scheduleHTML) {
        this.scheduleName = scheduleName;
        this.scheduleHTML = scheduleHTML;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String settingName) {
        this.scheduleName = settingName;
    }

    public String getScheduleHTML() {
        return scheduleHTML;
    }

    public void setScheduleHTML(String value) {
        this.scheduleHTML = value;
    }
}
