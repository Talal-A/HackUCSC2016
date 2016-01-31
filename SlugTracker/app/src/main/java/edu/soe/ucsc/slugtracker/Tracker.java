package edu.soe.ucsc.slugtracker;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.*;

import org.jsoup.nodes.Element;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Calendar;


/**
 * Created by talal.abouhaiba on 1/29/16.
 *
 * This class is designed to allow the user to "add" meal items to their running calorie total.
 */

public class Tracker extends ListActivity implements View.OnClickListener {

    SharedPreferences savedInfo;

    private int calCount;
    private int locationNum;

    private SharedPreferences.Editor settingsEditor;
    TextView count;
    private List<FoodObject> foodItems;
    ArrayAdapter arrayAdapter;
    ProgressDialog pd;
    private Handler handler;

    FoodDataBase foodData;

    private FoodObject[] stack = new FoodObject[500];
    int size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        foodData = new FoodDataBase(this);
        foodData.insertFood("", 0, 0, 0, 0);

        Button changeLocation = (Button) findViewById(R.id.changeLocation);
        changeLocation.setOnClickListener(this);

        savedInfo = getSharedPreferences("savedInfo", 0);

        // Create buttons
        settingsEditor = savedInfo.edit();

        count = (TextView) findViewById(R.id.textView);

        calCount = savedInfo.getInt("Calories", 0);
        locationNum = savedInfo.getInt("LocationNum", 5);
        updateCount();

        // Allocate memory for list

        foodItems = new ArrayList<>();

        ListView lv = getListView();
        lv.setAdapter(arrayAdapter);

        if(foodData.getNutrition(1).getTag() != null)
            System.out.println(foodData.getNutrition(1).getTag());

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tempTag = foodItems.get(position).getTag();
                int tempCal = foodItems.get(position).getCal();
                float tempCarb = foodItems.get(position).getCarbs();
                float tempFat = foodItems.get(position).getFat();
                float tempProtein = foodItems.get(position).getPro();


                calCount += tempCal;

                foodData.updateFood(1, tempTag, calCount, tempCarb, tempFat, tempProtein);
                push(foodItems.get(position));
                updateCount();
            }
        });

        updateList();

    } // end onCreate

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.changeLocation:
                presentLocations();
                break;

        } // end switch

    } // end onClick

    private void presentLocations() {

        final List<String> locations = new ArrayList<>();

        locations.add("Porter/Kresge");
        locations.add("Cowell/Stevenson");
        locations.add("Crown/Merrill");
        locations.add("Eight/Oakes");
        locations.add("Nine/Ten");

        AlertDialog.Builder locationList = new AlertDialog.Builder(this);
        locationList.setTitle("Select a Location");

        locationList.setItems(locations.toArray(new CharSequence[locations.size()]), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                setLocation(webLocation(locations.get(which)));
            }
        });

        locationList.show();

    } // end presentLocations

    public int getCurrentLocation() {
        return locationNum;
    }


    private void setLocation(int locationNumber) {

        locationNum = locationNumber;
        settingsEditor.putInt("LocationNum", locationNum);
        settingsEditor.apply();

        System.out.println("Setting location to: " + locationNum);
        updateList();

    }

    private void updateList() {

        pd = ProgressDialog.show(this, "", "Loading", true, false);
        handler = new Handler();

        foodItems = null;
        foodItems = new ArrayList<>();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                org.jsoup.nodes.Document doc = null;

                // checks what day of week it is to determine whether college is closed or not
                Calendar c = Calendar.getInstance();
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                int college = getCurrentLocation();
                if ((dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) &&
                        (college == 20 || college == 30)) {
                    System.out.println("This college is closed");
                } else if ((dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) &&
                        getMealTime().equals("Breakfast")) {
                    System.out.println("Breakfast is not served this day");
                }

                // FIXME: Should be in an else block, add else after we handle an "empty day"
                // Scrape info off site.
                System.out.println("http://nutrition.sa.ucsc.edu/pickMenu.asp?locationNum=" +
                        getCurrentLocation() + "&dtdate=" + getDateString() + "&mealName=" + getMealTime());
                try {
                    doc = Jsoup.connect("http://nutrition.sa.ucsc.edu/pickMenu.asp?locationNum=" +
                            getCurrentLocation() + "&dtdate=" + getDateString() + "&mealName=" + getMealTime()).get();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


                // Search for line with food names.
                // String temp = doc.select("a[href]").id();

                for (Element e : doc.select("a[href]"))
                    e.wrap("<foods></foods>");

                // Searching through tags for just the names.
                for (Element e : doc.getElementsByTag("foods")) {

                    String current = e.select("a[href]").text();
                    String foodName = e.select("a[href]").text();
                    if (current.equals("Top of Page")) {
                        break;
                    }

                    String nutritionSite = e.select("a[href]").attr("abs:href");


                    // parse needed
                    org.jsoup.nodes.Document doc2 = null;

                    try {
                        doc2 = Jsoup.connect(nutritionSite).get();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    // Wraps the current food item with a nutrition tag so that we can parse them.
                    // Without this for loop we end up getting all of the food tags on a single line
                    for (Element el : doc2.select("font"))
                        el.wrap("<nutrition></nutrition>");

                    // Grabs the food name that is surrounded by the nutrition tag

                    // Allergen Information
                    boolean milk = false, soy = false, treeOrPNut = false, gluten = false;
                    boolean fishShell = false, egg = false, wheat = false;

                    for (Element el : doc2.select("span")) {
                        String cur = el.select("span").text();
                        if (cur.contains("Egg"))
                            egg = true;
                        if (cur.contains("Soybean"))
                            soy = true;
                        if (cur.contains("Gluten"))
                            gluten = true;
                        if (cur.contains("Wheat"))
                            wheat = true;
                        if (cur.contains("Milk"))
                            milk = true;
                        if (cur.contains("Tree Nut") ||
                                cur.contains("Peanut"))
                            treeOrPNut = true;
                        if (cur.contains("Fish") || cur.contains("fish"))
                            fishShell = true;
                    }

                    boolean isProtein = false;
                    boolean isFat = false;
                    boolean isCarbs = false;

                    int currentCal = 0;
                    float currentProtein = 0;
                    float currentCarb = 0;
                    float currentFat = 0;

                    boolean obtainedProtein = false;
                    boolean obtainedFat = false;
                    boolean obtainedCarbs = false;
                    boolean obtainedCalories = false;


                    for (Element el : doc2.getElementsByTag("nutrition")) {


                        String cur = el.select("font").text();
                        //System.out.println("TEST: " + cur);

                        // Pushes FoodObject instances into a list of FoodObjects "foodItems"
                        if (obtainedCalories && obtainedCarbs && obtainedFat && obtainedProtein) {
                            FoodObject tempFoodO = new FoodObject(foodName, currentCal,
                                    currentProtein, currentFat, currentCarb);
                            tempFoodO.addAllergens(milk, soy, treeOrPNut, gluten,
                                    fishShell, egg, wheat);
                            foodItems.add(tempFoodO);
                            break;
                        }

                        if (isProtein) {

                            String temp = cur.split("g")[0];
                            currentProtein = Float.valueOf(temp);
                            isProtein = false;
                            obtainedProtein = true;

                        }

                        if (isFat) {

                            String temp = cur.split("g")[0];

                            currentFat = Float.valueOf(temp);

                            isFat = false;
                            obtainedFat = true;

                        }

                        if (isCarbs) {
                            String temp = cur.split("g")[0];

                            currentCarb = Float.valueOf(temp);

                            isCarbs = false;
                            obtainedCarbs = true;

                        }

                        if (cur.contains("Calories" + "\u00a0")) {

                            currentCal = Integer.parseInt(cur.split("\u00a0")[1]);
                            obtainedCalories = true;

                        } else if (cur.contains("Protein" + "\u00a0")) {

                            isProtein = true;

                        } else if (cur.contains("Total Fat" + "\u00a0")) {

                            isFat = true;

                        } else if (cur.contains("Tot. Carb." + "\u00a0")) {

                            isCarbs = true;

                        }
                    } // End of for loop with "nutrition" tag | Loop that scrapes nutritional info
                } // End of for loop with "foods" tag | Loop that grabs each food items name

                Looper.myLooper().quit();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<String> thelist = new ArrayList<>();

                        for (FoodObject e : foodItems)
                            thelist.add(e.getTag());

                        arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item,
                                R.id.tvName, thelist);

                        getListView().setAdapter(arrayAdapter);

                        pd.dismiss();
                    }
                });


            } // end run
        }; // end runnable
        Thread thread = new Thread(runnable);
        thread.start();


    } // end updateList


    private void updateCount() {
        count.setText(String.valueOf(calCount));
        settingsEditor.putInt("Calories", calCount);
        settingsEditor.apply();

    }

    //---------this is for the stack for removing mistakes--------------//

    public void push(FoodObject item){
        stack[size ++] = item;
    }

    public FoodObject pop(){
        if(size == 0){
            throw new NoSuchElementException("Cannot pop from empty stack");
        }
        FoodObject removed = stack[size-1];
        stack[-- size] = null;
        return removed;
    }

    // Get time of day and returns String for Breakfast, Lunch, Dinner, or Closed.
    public String getMealTime(){
        Calendar c = Calendar.getInstance();
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        String mealHour = "Closed";
        if(hourOfDay >= 7 && hourOfDay < 12){
            mealHour = "Breakfast";
        }else if(hourOfDay >= 12 && hourOfDay < 17){
            mealHour = "Lunch";
        }else if(hourOfDay >= 5 && hourOfDay < 23){
            mealHour = "Dinner";
        }

        return mealHour;
    }

    // Takes in dinning hall name(ex. Cowell/Stevenson), and returns it's web number.
    public int webLocation(String location){
        int locationNumber = 0;
        if(location == "Cowell/Stevenson"){
            locationNumber = 5;
        }else if(location == "Crown/Merrill"){
            locationNumber = 20;
        }else if(location == "Porter/Kresge") {
            locationNumber = 25;
        }else if(location == "Eight/Oakes"){
            locationNumber = 30;
        }else if(location == "Nine/Ten"){
            locationNumber = 40;
        }
        return locationNumber;
    }

    private String getDateString() {

        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        System.out.println(date);
        String currentMonth = date.substring(5, 7);          // From current date
        String currentDay = date.substring(8);           // From current date
        String currentYear = date.substring(0, 4);        // From current date

        return currentMonth + "%2F" + currentDay + "%2F" + currentYear;
    }

}
