package lukas.sobotik.widgetschedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import androidx.annotation.Nullable;

public class ScheduleDatabaseHelper extends SQLiteOpenHelper {
    Context context;
    public static final String DATABASE_NAME = "Schedule.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "schedule";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_SCHEDULE_NAME = "schedule_name";
    private static final String COLUMN_SCHEDULE_HTML = "schedule_html";


    public ScheduleDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_SCHEDULE_NAME + " TEXT NOT NULL," +
                COLUMN_SCHEDULE_HTML + " TEXT NOT NULL);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addItem(ScheduleEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, entry.getId());
        cv.put(COLUMN_SCHEDULE_NAME, entry.getScheduleName().toString());
        cv.put(COLUMN_SCHEDULE_HTML, entry.getScheduleHTML());

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            try {
                updateData(entry);
            } catch (Exception e) {
                Toast.makeText(context, "Failed to save to the Database", Toast.LENGTH_SHORT).show();
            }
        }
    }

    Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery(query, null);
        return cursor;
    }

    public void updateData(ScheduleEntry entry) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, entry.getId());
        cv.put(COLUMN_SCHEDULE_NAME, entry.getScheduleName().toString());
        cv.put(COLUMN_SCHEDULE_HTML, entry.getScheduleHTML());

        String stringId = String.valueOf(entry.getId());

        long result = db.update(TABLE_NAME, cv, COLUMN_ID + "= ?", new String[]{stringId});
        if (result == -1) {
            Toast.makeText(context, "Failed to edit the Data", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully edited the Data", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteItem(ScheduleEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        String stringId = String.valueOf(entry.getId());

        long result = db.delete(TABLE_NAME,COLUMN_ID + "= ?", new String[]{stringId});
        if (result == -1) {
            Toast.makeText(context, "Failed to delete the Data", Toast.LENGTH_SHORT).show();
        }
    }
}

