package com.appdev.schoudhary.wittylife.ui.main;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.appdev.schoudhary.wittylife.BuildConfig;
import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.City;
import com.appdev.schoudhary.wittylife.model.CityRecords;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.model.TrafficRanking;
import com.appdev.schoudhary.wittylife.network.ApiService;
import com.appdev.schoudhary.wittylife.network.RetroClient;
import com.appdev.schoudhary.wittylife.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ComparisonActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = ComparisonActivity.class.getSimpleName();
    private ActionBar actionBar;
    private ProgressBar mLoadingIndicator;
    private Spinner spinner;

    private MenuItem spinnerItem;

    private static AppDatabase mDB;
    private List<String> cityRecords ;

    private CompositeDisposable disposables = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);

        Toolbar comparetoolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(comparetoolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);


        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mDB = AppDatabase.getsInstance(getApplicationContext());

        cityRecords = new ArrayList<>();

        //FIXME Hack to force onCreateOptionMenu() to load spinner data
        invalidateOptionsMenu();


        // Check for existing state after configuration change and restore the layout
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            cityRecords = savedInstanceState.getStringArrayList("cityRecords");

            /**
             * Updating spinner UI from database
             */
//            populateSpinnerFromDB(savedInstanceState);
        } else {
            Intent intentFromHome = getIntent();
            if (intentFromHome != null) {
                if (intentFromHome.hasExtra(Intent.EXTRA_TEXT)) {
                    String sourceCity = intentFromHome.getParcelableExtra(Intent.EXTRA_TEXT);
//                    setupMovieDetailFromViewModel(movieRecord);
                    /**
                     * Fetching destination ranking from API on destination details loading
                     */
//                    loadMovieReviewsInDB();
                    fetchAndUpdateSpinner();
                }
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("cityRecords", (ArrayList<String>) cityRecords);
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Saving cityRecords in bundle during orientation change");

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cityRecords = savedInstanceState.getStringArrayList("cityRecords");
        Log.d(TAG, "Restoring rankingData from bundle during orientation change");
    }


    private void populateSpinnerFromDB(@Nullable Bundle savedInstanceState) {
        final MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getCityRecords().observe(this, new android.arch.lifecycle.Observer<List<City>>() {
            @Override
            public void onChanged(@Nullable List<City> cityList) {
               if(cityList != null){
                   cityList.forEach(city -> cityRecords.add(city.getCity()));
               }
               populateSpinnerData();
            }
        });
    }

    private void populateSpinnerData() {
        ArrayAdapter adapter = new ArrayAdapter<>(ComparisonActivity.this, android.R.layout.simple_spinner_item, cityRecords );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(ComparisonActivity.this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater  = getMenuInflater();
        inflater.inflate(R.menu.comparison_spinner, menu);

        spinnerItem =  menu.findItem(R.id.compare_menu);
        spinner = (Spinner) spinnerItem.getActionView();

        spinner.setFitsSystemWindows(true);

//        spinnerItem.expandActionView();
        spinnerItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });

        //FIXME Hack to avoid NPE on spinner object on config change
        if(!cityRecords.isEmpty() ) {
            populateSpinnerData();
        }

        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int itemId = item.getItemId();
//        /* Share menu item clicked */
//        if (itemId == R.id.compare_menu) {
//            //TODO Load cities from database and network
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //TODO Do something better
            parent.setSelection(0);
    }

    //FIXME Implement Repository and network bound resource
    private void fetchAndUpdateSpinner() {
        if(mDB.cityDao().getRowCount() > 0) {
            populateSpinnerFromDB(null);
        } else {
            fetchAndUpdateFromAPI();
        }
    }

    private void fetchAndUpdateFromAPI() {
        Observable<CityRecords> callCityRecords;
        ApiService apiService = RetroClient.getApiService();
        callCityRecords = apiService.getCityRecords(BuildConfig.ApiKey);
        mLoadingIndicator.setVisibility(View.VISIBLE);

        /**
         * Fetch city records data from api
         */
        //FIXME Long running tak, must run as a background service
        callCityRecords.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CityRecords>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(CityRecords cityRecords) {
                        List<City>  cities = cityRecords.getCities();
                        mDB.cityDao().insertCityList(cities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                        showErrorMessage();
                    }

                    @Override
                    public void onComplete() {
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                        populateSpinnerFromDB(null);
                    }
                });
    }

    private void showErrorMessage() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.network_error)
                .setMessage(R.string.network_error_msg)
                .setNegativeButton(R.string.error_dismiss_button, (dialog, which) -> dialog.dismiss()).create().show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        populateSpinnerFromDB(null);

    }

    @Override
    protected void onPause() {
        disposables.dispose();
        super.onPause();
    }
}
