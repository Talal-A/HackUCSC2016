package edu.soe.ucsc.slugtracker;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import org.jsoup.*;

import org.w3c.dom.Document;

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


       /* Document doc = Jsoup.connect("http://nutrition.sa.ucsc.edu/pickMenu.asp?location" +
                "Num=05&dtdate=02%2F02%2F2016&mealName=Breakfast").get();
        */



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
