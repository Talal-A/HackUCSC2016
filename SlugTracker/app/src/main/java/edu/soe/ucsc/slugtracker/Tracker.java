package edu.soe.ucsc.slugtracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Tracker extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("HELP ME");
        System.out.println("app");
        System.out.println("Whatever");
        setContentView(R.layout.activity_tracker);
    }
}
