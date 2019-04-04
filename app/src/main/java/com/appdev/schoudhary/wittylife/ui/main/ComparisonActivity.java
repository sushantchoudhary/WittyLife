package com.appdev.schoudhary.wittylife.ui.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.appdev.schoudhary.wittylife.R;

public class ComparisonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
