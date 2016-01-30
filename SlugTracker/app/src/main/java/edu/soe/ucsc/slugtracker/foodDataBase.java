package edu.soe.ucsc.slugtracker;

/**
 * Created by Wesly on 1/30/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class foodDataBase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Nutrition";
    private static final int DATABASE_VERSION = 1;
    public static final String FOOD_TABLE_NAME = "DailyNutrition";
    public static final String FOOD_COLUMN_ID = "_id";
    public static final String FOOD_COLUMN_TAG = "tag";
    public static final String FOOD_COLUMN_CALORIES = "calories";
    public static final String FOOD_COLUMN_FAT = "fat";
    public static final String FOOD_COLUMN_CARBS = "carbs";
    public static final String FOOD_COLUMN_PROTEIN = "protein";

    public foodDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create food table
        String CREATE_FOOD_TABLE = "CREATE TABLE" + FOOD_TABLE_NAME + "(" +
                FOOD_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                FOOD_COLUMN_TAG + " TEXT, " +
                FOOD_COLUMN_CALORIES + " INTEGER, " +
                FOOD_COLUMN_CARBS + " FLOAT, " +
                FOOD_COLUMN_FAT + " FLOAT, " +
                FOOD_COLUMN_PROTEIN  + " FLOAT )";

        // create food table
        db.execSQL(CREATE_FOOD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older food table if existed
        db.execSQL("DROP TABLE IF EXISTS" + FOOD_TABLE_NAME);

        // create fresh food table
        this.onCreate(db);
    }

    public boolean insertFood(String tag, int cal, float carbs, float fat, float protein) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FOOD_COLUMN_TAG, tag);
        contentValues.put(FOOD_COLUMN_CALORIES, cal);
        contentValues.put(FOOD_COLUMN_CARBS, carbs);
        contentValues.put(FOOD_COLUMN_FAT, fat);
        contentValues.put(FOOD_COLUMN_PROTEIN, protein);
        db.insert(FOOD_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updatePerson(Integer id, String tag, int cal, float carbs, float fat, float protein) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FOOD_COLUMN_TAG, tag);
        contentValues.put(FOOD_COLUMN_CALORIES, cal);
        contentValues.put(FOOD_COLUMN_CARBS, carbs);
        contentValues.put(FOOD_COLUMN_FAT, fat);
        contentValues.put(FOOD_COLUMN_PROTEIN, protein);
        db.update(FOOD_TABLE_NAME, contentValues, FOOD_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

}