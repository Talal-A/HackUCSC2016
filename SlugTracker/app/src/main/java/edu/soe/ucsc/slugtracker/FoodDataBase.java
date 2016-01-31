package edu.soe.ucsc.slugtracker;

/**
 * Created by Wesly on 1/30/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class FoodDataBase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Nutrition";
    private static final int DATABASE_VERSION = 1;
    public static final String FOOD_TABLE_NAME = "DailyNutrition";
    public static final String FOOD_COLUMN_ID = "_id";
    public static final String FOOD_COLUMN_TAG = "tag";
    public static final String FOOD_COLUMN_CALORIES = "calories";
    public static final String FOOD_COLUMN_FAT = "fat";
    public static final String FOOD_COLUMN_CARBS = "carbs";
    public static final String FOOD_COLUMN_PROTEIN = "protein";

    public FoodDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create food table
        String CREATE_FOOD_TABLE = "CREATE TABLE " + FOOD_TABLE_NAME + "(" +
                FOOD_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                FOOD_COLUMN_TAG + " TEXT, " +
                FOOD_COLUMN_CALORIES + " INTEGER, " +
                FOOD_COLUMN_CARBS + " REAL, " +
                FOOD_COLUMN_FAT + " REAL, " +
                FOOD_COLUMN_PROTEIN  + " REAL )";

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

    // inserts new values to row
    public boolean insertFood(String tag, int cal, float carbs, float fat, float protein) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FOOD_COLUMN_TAG, tag);
        contentValues.put(FOOD_COLUMN_CALORIES, cal);
        contentValues.put(FOOD_COLUMN_CARBS, carbs);
        contentValues.put(FOOD_COLUMN_FAT, fat);
        contentValues.put(FOOD_COLUMN_PROTEIN, protein);
        db.insert(FOOD_TABLE_NAME, null, contentValues);
        return true;
    }

    // Updates info of food without adding anything to table
    public boolean updateFood(Integer id, String tag, int cal, float carbs, float fat, float protein) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FOOD_COLUMN_TAG, tag);
        contentValues.put(FOOD_COLUMN_CALORIES, cal);
        contentValues.put(FOOD_COLUMN_CARBS, carbs);
        contentValues.put(FOOD_COLUMN_FAT, fat);
        contentValues.put(FOOD_COLUMN_PROTEIN, protein);
        db.update(FOOD_TABLE_NAME, contentValues, FOOD_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
        return true;
    }

    // deletes single contact by ID
    public void deleteContact(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FOOD_TABLE_NAME, FOOD_COLUMN_ID + " = ?",
                new String[]{Integer.toString(id)});
        db.close();
    }

    // Gets a single row via ID from database
    FoodObject getNutrition(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(FOOD_TABLE_NAME, new String[]{FOOD_COLUMN_TAG, FOOD_COLUMN_CALORIES,
                        FOOD_COLUMN_CARBS, FOOD_COLUMN_FAT, FOOD_COLUMN_PROTEIN}, FOOD_COLUMN_ID + "=?",
                        new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        String tempTag = cursor.getString(0);
        int tempCal = Integer.parseInt(cursor.getString(1));
        float tempCarbs = Float.parseFloat(cursor.getString(2));
        float tempFat = Float.parseFloat(cursor.getString(3));
        float tempProtein = Float.parseFloat(cursor.getString(4));

        FoodObject food = new FoodObject(tempTag, tempCal, tempProtein, tempFat, tempCarbs);
        return food;
    }

    // Gets everything in database
    public List<FoodObject> getAllFood() {
        List<FoodObject> foodList = new ArrayList<FoodObject>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + FOOD_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String tempTag = cursor.getString(0);
                int tempCal = Integer.parseInt(cursor.getString(1));
                float tempCarbs = Float.parseFloat(cursor.getString(2));
                float tempFat = Float.parseFloat(cursor.getString(3));
                float tempProtein = Float.parseFloat(cursor.getString(4));

                FoodObject food = new FoodObject(tempTag, tempCal, tempProtein, tempFat, tempCarbs);
                // Adding contact to list
                foodList.add(food);
            } while (cursor.moveToNext());
        }

        // return contact list
        return foodList;
    }

}