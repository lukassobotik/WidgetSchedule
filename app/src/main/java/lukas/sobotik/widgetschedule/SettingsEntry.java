package lukas.sobotik.widgetschedule;

import androidx.annotation.NonNull;

public class SettingsEntry {
    private int id;
    private int settingName;
    private String value;

    public SettingsEntry(int id, @NonNull int settingName, @NonNull String value) {
        this.id = id;
        this.settingName = settingName;
        this.value = value;
    }

    public SettingsEntry(@NonNull int settingName, @NonNull String value) {
        this.settingName = settingName;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSettingName() {
        return settingName;
    }

    public void setSettingName(int settingName) {
        this.settingName = settingName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
