package com.appdev.schoudhary.wittylife.ui.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.appdev.schoudhary.wittylife.BuildConfig;
import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.CostRanking;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.model.TrafficRanking;
import com.appdev.schoudhary.wittylife.network.ApiService;
import com.appdev.schoudhary.wittylife.network.RetroClient;
import com.appdev.schoudhary.wittylife.viewmodel.MainViewModel;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RankingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = RankingActivity.class.getSimpleName();
    private TextView topCity;
    private TextView secondCity;
    private TextView thirdCity;
    private TextView lastCity;

    private TextView topQol;
    private TextView secondQol;
    private TextView thirdQol;
    private TextView lastQol;

    private TextView topPpi;
    private TextView secondPpi;
    private TextView thirdPpi;
    private TextView lastPpi;


    TextView ranking_header_index;
    TextView ranking_header_value;

    private Spinner spinner;

    private List<QOLRanking> qolRankingData;
    private List<CostRanking> costRankingData;
    private List<TrafficRanking> trafficRankingData;

    private RankingOptions rankingOption;

    private ProgressBar mLoadingIndicator;

    private static AppDatabase mDB;


    private CompositeDisposable disposables = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        spinner = findViewById(R.id.rank_selector);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mDB = AppDatabase.getsInstance(getApplicationContext());

        ranking_header_index = findViewById(R.id.ranking_index);
        ranking_header_value = findViewById(R.id.ranking_value);


        topCity = findViewById(R.id.first_city);
        secondCity = findViewById(R.id.second_city);
        thirdCity = findViewById(R.id.third_city);
        lastCity = findViewById(R.id.last_city);

        topQol = findViewById(R.id.first_qol);
        secondQol = findViewById(R.id.second_qol);
        thirdQol = findViewById(R.id.third_qol);
        lastQol = findViewById(R.id.last_qol);

        topPpi = findViewById(R.id.first_ppindex);
        secondPpi = findViewById(R.id.second_ppi);
        thirdPpi = findViewById(R.id.third_ppiindex);
        lastPpi = findViewById(R.id.last_ppindex);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rankingFilter, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Check for existing state after configuration change and restore the layout
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            rankingOption = (RankingOptions) savedInstanceState.getSerializable("rankingOption");
            /**
             * Updating UI from view model
             */
            if (rankingOption.getRankingOption().equals(RankingOptions.QOL.getRankingOption())) {
                setupRankingFromViewModel();
            } else if (rankingOption.getRankingOption().equals(RankingOptions.COST.getRankingOption())) {
                setupCostRankingFromViewModel();
            } else {
                setupTrafficRankingFromViewModel();
            }
        } else {
            Intent intentFromHome = getIntent();
            if (intentFromHome != null) {
                if (intentFromHome.hasExtra("RANKING_TYPE")) {
                    rankingOption = (RankingOptions) intentFromHome.getSerializableExtra("RANKING_TYPE");
                    /**
                     * Fetching destination ranking from API on destination details loading
                     */
//                    loadRankingDataInDB();
                    resolveRankingView(rankingOption);
                }
            }
        }
    }

    /**
     * Responsible for selecting correct spinner option based on ranking type from main activity
     * @param rankingOption
     */
    private void resolveRankingView(RankingOptions rankingOption) {
        if (rankingOption.getRankingOption().equals(RankingOptions.QOL.getRankingOption())) {
            spinner.setSelection(0, true);
        } else if (rankingOption.getRankingOption().equals(RankingOptions.COST.getRankingOption())) {
            spinner.setSelection(1, true);
        } else {
            spinner.setSelection(2, true);
        }

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void populateQOLUI(List<QOLRanking> rankingData) {

        if (rankingData == null) {
            showErrorMessage();
        }

        ranking_header_index.setText(R.string.ranking_qolindex);
        ranking_header_value.setText(R.string.PP_Index);

        List<TextView> listOfCity = Arrays.asList(topCity, secondCity, thirdCity, lastCity);
        List<TextView> listOfQol = Arrays.asList(topQol, secondQol, thirdQol, lastQol);
        List<TextView> listOfPpi = Arrays.asList(topPpi, secondPpi, thirdPpi, lastPpi);

        for (int i = 0; i < 4; i++) {
            listOfCity.get(i).setText(rankingData.get(i).getCityName());
            listOfQol.get(i).setText(String.format("%.2f", rankingData.get(i).getQualityOfLifeIndex()));
            listOfPpi.get(i).setText(String.format("%.2f", rankingData.get(i).getPurchasingPowerInclRentIndex()));
        }

        this.setTitle("Top QOL Ranking..");

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void populateCostUI(List<CostRanking> rankingData) {

        if (rankingData == null) {
            showErrorMessage();
        }

        ranking_header_index.setText("GroceriesIndex");
        ranking_header_value.setText("RentIndex");

        List<TextView> listOfCity = Arrays.asList(topCity, secondCity, thirdCity, lastCity);
        List<TextView> listOfQol = Arrays.asList(topQol, secondQol, thirdQol, lastQol);
        List<TextView> listOfPpi = Arrays.asList(topPpi, secondPpi, thirdPpi, lastPpi);

        for (int i = 0; i < 4; i++) {
            listOfCity.get(i).setText(rankingData.get(i).getCityName());
            listOfQol.get(i).setText(String.format("%.2f", rankingData.get(i).getGroceriesIndex()));
            listOfPpi.get(i).setText(String.format("%.2f", rankingData.get(i).getRentIndex()));
        }

        this.setTitle("Top Cost Ranking..");

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void populateTrafficUI(List<TrafficRanking> rankingData) {

        if (rankingData == null) {
            showErrorMessage();
        }

        ranking_header_index.setText("Co2EmissionIndex");
        ranking_header_value.setText("InefficiencyIndex");

        List<TextView> listOfCity = Arrays.asList(topCity, secondCity, thirdCity, lastCity);
        List<TextView> listOfQol = Arrays.asList(topQol, secondQol, thirdQol, lastQol);
        List<TextView> listOfPpi = Arrays.asList(topPpi, secondPpi, thirdPpi, lastPpi);

        for (int i = 0; i < 4; i++) {
            listOfCity.get(i).setText(rankingData.get(i).getCityName());
            listOfQol.get(i).setText(String.format("%.2f", rankingData.get(i).getCo2EmissionIndex()));
            listOfPpi.get(i).setText(String.format("%.2f", rankingData.get(i).getInefficiencyIndex()));
        }

        this.setTitle("Top Traffic Ranking..");

    }

    private void showErrorMessage() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.network_error)
                .setMessage(R.string.network_error_msg)
                .setNegativeButton(R.string.error_dismiss_button, (dialog, which) -> dialog.dismiss()).create().show();
    }

    private void setupRankingFromViewModel() {
        final MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        qolRankingData = viewModel.getQOLRanking();
        populateQOLUI(qolRankingData);
    }

    private void setupCostRankingFromViewModel() {
        final MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getCostRanking().observe(this, costRankings -> {
            costRankingData = costRankings;
            populateCostUI(costRankingData);
        });
    }


    private void setupTrafficRankingFromViewModel() {
        final MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTrafficRanking().observe(this, trafficRankings -> {
            trafficRankingData = trafficRankings;
            populateTrafficUI(trafficRankingData);
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("rankingOption", rankingOption);

        super.onSaveInstanceState(outState);
        Log.d(TAG, "Saving rankingData in bundle during orientation change");

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        rankingOption = (RankingOptions) savedInstanceState.getSerializable("rankingOption");

        Log.d(TAG, "Restoring rankingData from bundle during orientation change");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object selectedItem = parent.getSelectedItem();
        if (selectedItem.equals(RankingOptions.QOL.getRankingOption())) {
            rankingOption = RankingOptions.QOL;
            setupRankingFromViewModel();
        } else if (selectedItem.equals(RankingOptions.COST.getRankingOption())) {
            rankingOption = RankingOptions.COST;

            //FIXME Check DB first then network
            if(mDB.costDao().getRowCount() > 0) {
                setupCostRankingFromViewModel();
            } else {
                fetchAndUpdateCostUI();
            }
        } else {
            rankingOption = RankingOptions.TRAFFIC;
            if(mDB.trafficDao().getRowCount() > 0) {
                setupTrafficRankingFromViewModel();
            } else {
                fetchAndUpdateTrafficUI();
            }
        }
    }

    //FIXME Implement Repository and network bound resource
    private void fetchAndUpdateTrafficUI() {

        Observable<List<TrafficRanking>> callTrafficRanking;
        ApiService apiService = RetroClient.getApiService();
        callTrafficRanking = apiService.getTrafficRanking(BuildConfig.ApiKey);
        mLoadingIndicator.setVisibility(View.VISIBLE);

        /**
         * Fetch traffic ranking data from api
         */
        callTrafficRanking.take(4).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<TrafficRanking>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(List<TrafficRanking> trafficRankings) {
                        mDB.trafficDao().insertTrafficList(trafficRankings);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                        showErrorMessage();
                    }

                    @Override
                    public void onComplete() {
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                        setupTrafficRankingFromViewModel();
                    }
                });

    }

    //FIXME Implement Repository and network bound resource
    private void fetchAndUpdateCostUI() {
        ApiService apiService = RetroClient.getApiService();

        Observable<List<CostRanking>> callCostOfLivingRanking;
        callCostOfLivingRanking = apiService.getCostOfLivingRanking(BuildConfig.ApiKey);
        mLoadingIndicator.setVisibility(View.VISIBLE);

        /**
         * Fetch cost of living ranking data from api
         */
        callCostOfLivingRanking.take(4).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CostRanking>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(List<CostRanking> costRankings) {
                        mDB.costDao().insertCostList(costRankings);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                        showErrorMessage();
                    }

                    @Override
                    public void onComplete() {
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                        setupCostRankingFromViewModel();
                    }
                });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        parent.setSelection(0);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        resolveRankingView(rankingOption);

    }

    @Override
    protected void onPause() {
        disposables.dispose();
        super.onPause();
    }


}
