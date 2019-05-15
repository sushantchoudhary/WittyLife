package com.appdev.schoudhary.wittylife.ui.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.appdev.schoudhary.wittylife.BuildConfig;
import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.databinding.ActivityRankingBinding;
import com.appdev.schoudhary.wittylife.model.CostRanking;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.model.TrafficRanking;
import com.appdev.schoudhary.wittylife.network.ApiService;
import com.appdev.schoudhary.wittylife.network.RetroClient;
import com.appdev.schoudhary.wittylife.utils.AppExecutors;
import com.appdev.schoudhary.wittylife.viewmodel.MainViewModel;
import com.google.firebase.analytics.FirebaseAnalytics;

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
    private FirebaseAnalytics mFirebaseAnalytics;



    private CompositeDisposable disposables = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityRankingBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_ranking);
        binding.setLifecycleOwner(this);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        spinner = binding.rankSelector;

        mLoadingIndicator = binding.pbLoadingIndicator;
        ranking_header_index = binding.rankingIndex;
        ranking_header_value = binding.rankingValue;

        topCity = binding.firstCity;
        secondCity = binding.secondCity;
        thirdCity = binding.thirdCity;
        lastCity = binding.lastCity;

        topQol = binding.firstQol;
        secondQol = binding.secondQol;
        thirdQol = binding.thirdQol;
        lastQol = binding.lastQol;

        topPpi = binding.firstPpindex;
        secondPpi = binding.secondPpi;
        thirdPpi = binding.thirdPpiindex;
        lastPpi = binding.lastPpindex;

        mDB = AppDatabase.getsInstance(getApplicationContext());

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
            if (rankingOption != null && rankingOption.getRankingOption().equals(RankingOptions.QOL.getRankingOption())) {
                setupRankingFromViewModel();
            } else if (rankingOption != null && rankingOption.getRankingOption().equals(RankingOptions.COST.getRankingOption())) {
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
     *
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

        if (rankingData.size() == 0) {
            showErrorMessage();
            return;
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

        this.setTitle(R.string.qol_ranking_title);

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void populateCostUI(List<CostRanking> rankingData) {

        if (rankingData.size() == 0) {
            showErrorMessage();
            return;
        }

        ranking_header_index.setText(getString(R.string.groceries_index));
        ranking_header_value.setText(getString(R.string.rent_index));

        List<TextView> listOfCity = Arrays.asList(topCity, secondCity, thirdCity, lastCity);
        List<TextView> listOfQol = Arrays.asList(topQol, secondQol, thirdQol, lastQol);
        List<TextView> listOfPpi = Arrays.asList(topPpi, secondPpi, thirdPpi, lastPpi);

        for (int i = 0; i < 4; i++) {
            listOfCity.get(i).setText(rankingData.get(i).getCityName());
            listOfQol.get(i).setText(String.format("%.2f", rankingData.get(i).getGroceriesIndex()));
            listOfPpi.get(i).setText(String.format("%.2f", rankingData.get(i).getRentIndex()));
        }

        this.setTitle(R.string.cost_ranking_title);

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void populateTrafficUI(List<TrafficRanking> rankingData) {

        if (rankingData.size() == 0) {
            showErrorMessage();
            return;

        }

        ranking_header_index.setText(getString(R.string.co2_emission));
        ranking_header_value.setText(getString(R.string.inefficiency));

        List<TextView> listOfCity = Arrays.asList(topCity, secondCity, thirdCity, lastCity);
        List<TextView> listOfQol = Arrays.asList(topQol, secondQol, thirdQol, lastQol);
        List<TextView> listOfPpi = Arrays.asList(topPpi, secondPpi, thirdPpi, lastPpi);

        for (int i = 0; i < 4; i++) {
            listOfCity.get(i).setText(rankingData.get(i).getCityName());
            listOfQol.get(i).setText(String.format("%.2f", rankingData.get(i).getCo2EmissionIndex()));
            listOfPpi.get(i).setText(String.format("%.2f", rankingData.get(i).getInefficiencyIndex()));
        }

        this.setTitle(R.string.traffic_ranking_title);

    }


    private void setupRankingFromViewModel() {
        final MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getQOLRanking().observe(this, qolRankings -> {
            qolRankingData = qolRankings;
            populateQOLUI(qolRankingData);
        });
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

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.SOURCE, RankingOptions.QOL.getRankingOption());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        } else if (selectedItem.equals(RankingOptions.COST.getRankingOption())) {
            rankingOption = RankingOptions.COST;

            AppExecutors.getInstance().diskIO().execute(() -> {
                //FIXME Check DB first then network
                int count = mDB.costDao().getRowCount();

                runOnUiThread(() -> {
                    if (count > 0) {
                        setupCostRankingFromViewModel();
                    } else {
                        fetchAndUpdateCostUI();
                    }
                });
            });

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.SOURCE, RankingOptions.COST.getRankingOption());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        } else {
            rankingOption = RankingOptions.TRAFFIC;

            AppExecutors.getInstance().diskIO().execute(() -> {
                int trafficCount = mDB.trafficDao().getRowCount();

                runOnUiThread(() -> {
                    if (trafficCount > 0) {
                        setupTrafficRankingFromViewModel();
                    } else {
                        fetchAndUpdateTrafficUI();
                    }
                });

            });
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.SOURCE, RankingOptions.TRAFFIC.getRankingOption());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
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
                        AppExecutors.getInstance().diskIO().execute(() -> {
                            mDB.trafficDao().insertTrafficList(trafficRankings);
                        });
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
                        AppExecutors.getInstance().diskIO().execute(() -> {
                            mDB.costDao().insertCostList(costRankings);
                        });
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


    private void showErrorMessage() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.network_error)
                .setMessage(R.string.network_error_msg)
                .setNegativeButton(R.string.error_dismiss_button, (dialog, which) -> dialog.dismiss()).create().show();
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

    // To navigate back to parent activity when RankingActivity is launched from widget
    // Use taskAffinity in manifest
    @Override
    public void onPrepareSupportNavigateUpTaskStack(@NonNull TaskStackBuilder builder) {
        super.onPrepareSupportNavigateUpTaskStack(builder);

        Context context = this;
        Class destinationClass =  MainActivity.class;
        Intent intentToStartMainActivity = new Intent(context, destinationClass);
        intentToStartMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentToStartMainActivity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
