package edu.soe.ucsc.slugtracker;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.*;

import org.jsoup.nodes.Element;
import org.w3c.dom.Document;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by talal.abouhaiba on 1/29/16.
 *
 * This class is designed to allow the user to "add" meal items to their running calorie total.
 */

public class Tracker extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences savedInfo;
    private SharedPreferences.Editor settingsEditor;
    TextView count;
    private int calCount;
    private List<FoodObject> foodItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_tracker);

        savedInfo = getSharedPreferences("savedInfo", 0);

        // Create buttons
        settingsEditor = savedInfo.edit();
        count = (TextView) findViewById(R.id.textView);
        Button add1 = (Button) findViewById(R.id.add1);
        add1.setOnClickListener(this);
        Button add2 = (Button) findViewById(R.id.button);
        add2.setOnClickListener(this);

        calCount = 0;
        updateCount();

        // Alocate memory for list

        foodItems = new ArrayList<>();

        new Task().execute();
    }

    // When button clicked add calories.
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.add1:
                calCount += 270;
                for (FoodObject e: foodItems) {
                    System.out.println(e.getTag() + " " + e.getCal());
                }
                break;

            case R.id.button:
                calCount += 300;
                break;
        }

        updateCount();
    }

    private void updateCount() {
        count.setText("" + calCount);
    }


    private class Task extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {

            /*
            http://nutrition.sa.ucsc.edu/pickMenu.asp?locationNum=
            [LOCATION_NUMBER]&dtdate=
            [MONTH]%2F[DAY]%2F[YEAR]&mealName=[Breakfast/Lunch/Dinner]

            Locations:
            05: Cowell Stevenson
            20: Crown Merrill
            25: Porter Kresge
            30: Eight Oakes
            40: Nine Ten
            Month:
                01-12
            Day:
                01-28/31
            Year:
                2016
            */

                String locationNumber = "05";       // User chosen
                String currentMonth = "02";         // From current date
                String currentDay = "02";           // From current date
                String currentYear = "2016";        // From current date
                String currentMeal = "Breakfast";   // User chosen

                // Scrape info off site.
                org.jsoup.nodes.Document doc = Jsoup.connect("http://nutrition.sa.ucsc.edu/pickMenu.asp?locationNum=" +
                        locationNumber + "&dtdate=" + currentMonth + "%2F" + currentDay + "%2F" +
                        currentYear + "&mealName=" + currentMeal).get();


                // Search for line with food names.
//                String temp = doc.select("a[href]").id();


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
                    org.jsoup.nodes.Document doc2 = Jsoup.connect(nutritionSite).get();
                    for(Element el: doc2.select("font"))
                        el.wrap("<nutrition></nutrition>");
                    for(Element el: doc2.getElementsByTag("nutrition")) {
                        String cur = el.select("font").text();
                        int calCount;
                        if (cur.contains("Calories" + "\u00a0")) {
                            calCount = Integer.parseInt(cur.split("\u00a0")[1]);
                            foodItems.add(new FoodObject(foodName,calCount,0,0,0));

                        }
                        else
                            calCount = 0; // TODO: FIXME: Handle 0 cal count in constructor - ERROR

                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }


        // Print all the food names.
        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
        }

    }

    //public void

}
/*
    <tbody>
        <tr>...<tr> Contains all of the information for each food item

    It looks like "labelrecipe" is the tag i'll be using to find to scrape the website
    <div class="labelrecipe">Texas Beef Chili</div>
 */
