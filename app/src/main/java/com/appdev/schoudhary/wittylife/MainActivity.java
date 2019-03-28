package com.appdev.schoudhary.wittylife;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;


import com.appdev.schoudhary.wittylife.ui.main.MaskTransformation;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private SearchView searchView;
    private ImageView qolranking;
    private ImageView costranking;
    private ImageView trafficranking;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private RecyclerView mDestinationLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        qolranking =  findViewById(R.id.qolranking);
        costranking = findViewById(R.id.costofliving);
        trafficranking = findViewById(R.id.traffic);



//        Picasso.with(this)
//                .load(R.drawable.larmrmah216854unsplash)
//                .fit()
//                .placeholder(R.drawable.baseline_search_24)
//                .error(R.drawable.baseline_search_24)
//                .transform(new MaskTransformation(this, R.drawable.rounded_convers_transformation))
//                .into(qolranking);
//
//        Picasso.with(this)
//                .load(R.drawable.mirzababic766502unsplash)
//                .fit()
//                .placeholder(R.drawable.baseline_search_24)
//                .error(R.drawable.baseline_search_24)
//                .transform(new MaskTransformation(this, R.drawable.rounded_convers_transformation))
//
//                .into(costranking);
//
//        Picasso.with(this)
//                .load(R.drawable.adamsherez258833unsplash)
//                .fit()
//                .placeholder(R.drawable.baseline_search_24)
//                .error(R.drawable.baseline_search_24)
//                .transform(new MaskTransformation(this, R.drawable.rounded_convers_transformation))
//
//                .into(trafficranking);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconifiedByDefault(false);
//        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
//        searchView.setLayoutParams(params);
        searchItem.expandActionView();

        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);

            //TODO
//        searchView.setSuggestionsAdapter();



        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(new ComponentName(this, SearchResultActivity.class )));

        return  true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


//    @Override
//    public void onBackPressed() {
//
//        if(searchView.isIconified() && searchView != null) {
//            searchView.setIconified(false);
//            searchView.clearFocus();
//        } else {
//            super.onBackPressed();
//
//        }
//    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }
        return super.dispatchTouchEvent(ev);
    }



}
