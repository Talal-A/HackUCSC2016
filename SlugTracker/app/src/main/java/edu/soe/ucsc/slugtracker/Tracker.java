package edu.soe.ucsc.slugtracker;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_tracker);

        savedInfo = getSharedPreferences("savedInfo", 0);

        // Create buttons
        settingsEditor = savedInfo.edit();
        count = (TextView) findViewById(R.id.textView);

        calCount = 0;
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
                String locationNumber = "05";       // User chosen
                String currentMonth = date.substring(5, 7);          // From current date
                String currentDay =  "29";//date.substring(8);           // From current date
                String currentYear = date.substring(0, 4);        // From current date
                String currentMeal = "Breakfast";   // User chosen

                // Scrape info off site.
                org.jsoup.nodes.Document doc = null;

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
                updateCount();
            }
        });
    }

    // When button clicked add calories.
    public void onClick(View v) {
        switch (v.getId()) {

        }

        updateCount();
    }

    private void updateCount() {
        count.setText("" + calCount);
    }

}
/*
    <tbody>
        <tr>...<tr> Contains all of the information for each food item

    It looks like "labelrecipe" is the tag i'll be using to find to scrape the website
    <div class="labelrecipe">Texas Beef Chili</div>
 */
