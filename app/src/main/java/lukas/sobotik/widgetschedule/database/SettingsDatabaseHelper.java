package lukas.sobotik.widgetschedule.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import androidx.annotation.Nullable;
import lukas.sobotik.widgetschedule.entity.SettingsEntry;

public class SettingsDatabaseHelper extends SQLiteOpenHelper {
    Context context;
    public static final String DATABASE_NAME = "UserSettings.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "user_settings";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_VALUE = "value";


    public SettingsDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT NOT NULL UNIQUE," +
                COLUMN_VALUE + " TEXT NOT NULL);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addItem(SettingsEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, String.valueOf(entry.getSettingName()));
        cv.put(COLUMN_VALUE, entry.getValue());

        long result = db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        if (result == -1) {
            try {
                updateData(entry);
            } catch (Exception e) {
                Toast.makeText(context, "Failed to save to the Database", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery(query, null);
        return cursor;
    }

    public void updateData(SettingsEntry entry) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, String.valueOf(entry.getSettingName()));
        cv.put(COLUMN_VALUE, entry.getValue());

        String stringId = String.valueOf(entry.getId());

        long result = db.update(TABLE_NAME, cv, COLUMN_ID + "= ?", new String[]{stringId});
        if (result == -1) {
            Toast.makeText(context, "Failed to edit the Data", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully edited the Data", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteItem(SettingsEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_NAME,COLUMN_ID + "= ?", new String[]{String.valueOf(entry.getId())});
        if (result == -1) {
            Toast.makeText(context, "Failed to delete the Data", Toast.LENGTH_SHORT).show();
        }
    }
}
