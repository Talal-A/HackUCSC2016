package edu.soe.ucsc.slugtracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.jsoup.*;

import org.w3c.dom.Document;

public class Tracker extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("HELP ME");
        System.out.println("Whatever");


       /* Document doc = Jsoup.connect("http://nutrition.sa.ucsc.edu/pickMenu.asp?location" +
                "Num=05&dtdate=02%2F02%2F2016&mealName=Breakfast").get();
        */



        setContentView(R.layout.activity_tracker);



    }
}


/*

    <tbody>
        <tr>...<tr> Contains all of the information for each food item

    It looks like "labelrecipe" is the tag i'll be using to find to scrape the website
    <div class="labelrecipe">Texas Beef Chili</div>
 */
