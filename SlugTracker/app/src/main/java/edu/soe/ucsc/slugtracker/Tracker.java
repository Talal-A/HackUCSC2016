package edu.soe.ucsc.slugtracker;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.*;

import org.jsoup.nodes.Element;
import org.w3c.dom.Document;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Calendar;

import java.util.Calendar;

/**
 * Created by talal.abouhaiba on 1/29/16.
 *
 * This class is designed to allow the user to "add" meal items to their running calorie total.
 */

public class Tracker extends ListActivity implements View.OnClickListener {

    SharedPreferences savedInfo;
    private SharedPreferences.Editor settingsEditor;
    TextView count;
    private int calCount;
    private List<FoodObject> foodItems;
    ArrayAdapter arrayAdapter;
    ProgressDialog pd;
    private Handler handler;

    private FoodObject[] stack = new FoodObject[500];
    int size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_tracker);


        // DEBUG
        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(this);

        savedInfo = getSharedPreferences("savedInfo", 0);

        // Create buttons
        settingsEditor = savedInfo.edit();

        count = (TextView) findViewById(R.id.textView);

        calCount = savedInfo.getInt("Calories", 0);
        updateCount();

        // Alocate memory for list

        foodItems = new ArrayList<>();
        System.out.println("begin");
        //new Task().execute();

        pd = ProgressDialog.show(this,"", "Loading", true, false);
        handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("in run");
                Looper.prepare();

                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                System.out.println(date);
                System.out.println(mealTime());
                String locationNumber = "05";       // User chosen
                String currentMonth = date.substring(5, 7);          // From current date
                String currentDay =  "29";//date.substring(8);           // From current date
                String currentYear = date.substring(0, 4);        // From current date
                String currentMeal = "Breakfast";   // User chosen

                org.jsoup.nodes.Document doc = null;

                // checks what day of week it is to determine whether college is closed or not
                Calendar c = Calendar.getInstance();
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                int college = Integer.parseInt(locationNumber);
                if((dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) &&
                        (college == 20 || college == 30)) {
                    System.out.println("This college is closed");
                } else if((dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) &&
                        currentMeal.equals("Breakfast")){
                    System.out.println("Breakfast is not served this day");
                }

                // FIXME: Should be in an else block, add else after we handle an "empty day"
                    // Scrape info off site.
                    try {
                        doc = Jsoup.connect("http://nutrition.sa.ucsc.edu/pickMenu.asp?locationNum=" +
                                locationNumber + "&dtdate=" + currentMonth + "%2F" + currentDay + "%2F" +
                                currentYear + "&mealName=" + currentMeal).get();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }


                // Search for line with food names.
                // String temp = doc.select("a[href]").id();

                for(Element e: doc.select("a[href]"))
                    e.wrap("<foods></foods>");

                // Searching through tags for just the names.
                for(Element e: doc.getElementsByTag("foods")) {

                    String current = e.select("a[href]").text();
                    String foodName = e.select("a[href]").text();
                    if (current.equals("Top of Page")) {
                        break;
                    }

                    String nutritionSite = e.select("a[href]").attr("abs:href");

                    System.out.println(nutritionSite);
                    // parse needed
                    org.jsoup.nodes.Document doc2 = null;

                    try {
                        doc2 = Jsoup.connect(nutritionSite).get();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    // Wraps the current food item with a nutrition tag so that we can parse them.
                    // Without this for loop we end up getting all of the food tags on a single line
                    for(Element el: doc2.select("font"))
                        el.wrap("<nutrition></nutrition>");

                    // Grabs the food name that is surrounded by the nutrition tag

                    // Allergen Information
                    String[] tempAllergens;
                    boolean milk, soy, treeOrPNut, gluten, fishShell,  egg,  wheat, allergenBoolean;
                    for(Element el: doc2.select("span")){
                        String cur = el.select("span").text();
                        System.out.println(cur);
                        if (allergenBoolean){
                            tempAllergens = cur.split(",");
                            for(int i = 0; i < tempAllergens.length; i++){
                                System.out.println("Allergen info: " + tempAllergens[i]);
                            }
                            break;
                        }
                        if(cur.contains("Allergens:" + "\u00a0")){
                            allergenBoolean = true;
                        }

                    }
                    for(int i = 0; i < tempAllergens.length; i++){
                        if(tempAllergens[i].contains("Egg")){
                            egg = true;
                            System.out.println("Contains Eggs");
                        }
                        if(tempAllergens[i].contains("Soybean")){
                            soy = true;
                            System.out.println("Contains Soybean");
                        }
                        if(tempAllergens[i].contains("Gluten")){
                            gluten = true;
                            System.out.println("Contains Gluten");
                        }
                        if(tempAllergens[i].contains("Wheat")){
                            wheat = true;
                            System.out.println("Contains Wheat");
                        }
                        if(tempAllergens[i].contains("Milk")){
                            milk = true;
                            System.out.println("Contains Milk");
                        }
                        if(tempAllergens[i].contains("Tree Nut") ||
                                tempAllergens[i].contains("Peanut"){
                            treeOrPNut = true;
                            System.out.println("Contains Tree Nut / Peanut");
                        }
                        if(tempAllergens[i].contains("Fish") || tempAllergens[i].contains("fish")){
                            fishShell = true;
                            System.out.println("Contains Fish");
                        }
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


                    for(Element el: doc2.getElementsByTag("nutrition")) {


                        String cur = el.select("font").text();
                        //System.out.println("TEST: " + cur);

                        if (obtainedCalories && obtainedCarbs && obtainedFat && obtainedProtein) {
                            FoodObject tempFoodO = new FoodObject(foodName,currentCal,
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

                        } if (isFat) {
                            String temp = cur.split("g")[0];

                            currentFat = Float.valueOf(temp);

                            isFat = false;
                            obtainedFat = true;

                        }  if (isCarbs) {
                            String temp = cur.split("g")[0];

                            currentCarb = Float.valueOf(temp);

                            isCarbs = false;
                            obtainedCarbs = true;


                        }


                        if (cur.contains("Calories" + "\u00a0")) {

                            currentCal = Integer.parseInt(cur.split("\u00a0")[1]);
                            obtainedCalories = true;

                        } else if (cur.contains("Protein"+ "\u00a0")) {

                            isProtein = true;

                        } else if (cur.contains("Total Fat" + "\u00a0")) {

                            isFat = true;

                        } else if (cur.contains("Tot. Carb." + "\u00a0")) {

                            isCarbs = true;

                        }



                    }
                }

                Looper.myLooper().quit();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<String> thelist = new ArrayList<>();

                        for (FoodObject e: foodItems)
                            thelist.add(e.getTag());

                        arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item, R.id.tvName,thelist);

                        getListView().setAdapter(arrayAdapter);

                        pd.dismiss();
                    }
                });



            } // end run
        }; // end runnable
        Thread thread = new Thread(runnable);
        thread.start();
        System.out.println("end");

        ListView lv = getListView();
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                calCount += foodItems.get(position).getCal();
                push(foodItems.get(position));
                updateCount();
            }
        });
    }

    // When button clicked add calories.
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button:
                presentLocations();
                break;

        }

        updateCount();
    }

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
                System.out.println("worked");
            }
        });

        locationList.show();

    }


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
    public String mealTime(){
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
}
/*
    <tbody>
        <tr>...<tr> Contains all of the information for each food item

    It looks like "labelrecipe" is the tag i'll be using to find to scrape the website
    <div class="labelrecipe">Texas Beef Chili</div>
 */
