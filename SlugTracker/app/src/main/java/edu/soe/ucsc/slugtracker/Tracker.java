package edu.soe.ucsc.slugtracker;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import org.jsoup.*;

import org.w3c.dom.Document;
import java.io.IOException;

/**
 * Created by talal.abouhaiba on 1/29/16.
 *
 * This class is designed to allow the user to "add" meal items to their running calorie total.
 */

public class Tracker extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences savedInfo;
    private SharedPreferences.Editor settingsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("HELP ME");
        System.out.println("Whatever");


        try{


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

            org.jsoup.nodes.Document doc = Jsoup.connect("http://nutrition.sa.ucsc.edu/pickMenu.asp?locationNum=" +
                    locationNumber + "&dtdate=" + currentMonth + "%2F" + currentDay + "%2F" +
                    currentYear + "&mealName=" + currentMeal).get();

            System.out.println(doc.title());
        }catch (IOException e) {
            e.printStackTrace();
        }



        setContentView(R.layout.activity_tracker);

        savedInfo = getSharedPreferences("savedInfo", 0);

        settingsEditor = savedInfo.edit();

        Button add1 = (Button) findViewById(R.id.add1);
        add1.setOnClickListener(this);


    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.add1:
                System.out.println("Test");
                break;
        }
    }


}


/*

    <tbody>
        <tr>...<tr> Contains all of the information for each food item

    It looks like "labelrecipe" is the tag i'll be using to find to scrape the website
    <div class="labelrecipe">Texas Beef Chili</div>
 */
