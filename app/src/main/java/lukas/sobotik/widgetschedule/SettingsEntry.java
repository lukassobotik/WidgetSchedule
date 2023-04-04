package lukas.sobotik.widgetschedule;

import androidx.annotation.NonNull;

public class SettingsEntry {
    private int id;
    private Settings settingName;
    private String value;

    public SettingsEntry(int id, @NonNull Settings settingName, @NonNull String value) {
        this.id = id;
        this.settingName = settingName;
        this.value = value;
    }

    public SettingsEntry(@NonNull Settings settingName, @NonNull String value) {
        this.settingName = settingName;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Settings getSettingName() {
        return settingName;
    }

    public void setSettingName(Settings settingName) {
        this.settingName = settingName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
