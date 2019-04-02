package com.appdev.schoudhary.wittylife.ui.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.appdev.schoudhary.wittylife.BuildConfig;
import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.ClimateData;
import com.appdev.schoudhary.wittylife.model.CrimeData;
import com.appdev.schoudhary.wittylife.model.HealthCareData;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.network.ApiService;
import com.appdev.schoudhary.wittylife.network.RetroClient;
import com.appdev.schoudhary.wittylife.viewmodel.MainViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import at.grabner.circleprogress.CircleProgressView;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();
    private static final String DESTINATION_URL = "https://www.numbeo.com/quality-of-life/in/";
    private TextView mDetailHeader;
    private TextView mPPIValue;
    private TextView mSafetyValue;
    private TextView mHealthValue;
    private TextView mClimateValue;
    private TextView mMinContribValue;
    private TextView mMaxContribValue;


    private static AppDatabase mDB;


    private QOLRanking rankingData;
    private CircleProgressView mPPiCircleView;
    private CircleProgressView mSafetyCircleView;
    private CircleProgressView mHealthCircleView;
    private CircleProgressView mClimateCircleView;

    private CompositeDisposable disposables = new CompositeDisposable();
    private ShareActionProvider shareActionProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDetailHeader = findViewById(R.id.detail_header);
        mPPIValue = findViewById(R.id.ppi_value);
        mSafetyValue = findViewById(R.id.safety_value);
        mHealthValue = findViewById(R.id.healthcare_value);
        mClimateValue = findViewById(R.id.climate_value);
        mMinContribValue = findViewById(R.id.destination_min_contrib);
        mMaxContribValue = findViewById(R.id.destination_max_contrib);

        mPPiCircleView = findViewById(R.id.ppiCircleView);
        mSafetyCircleView = findViewById(R.id.safetyCircleView);
        mHealthCircleView = findViewById(R.id.healthCircleView);
        mClimateCircleView = findViewById(R.id.climateCircleView);

        mDB = AppDatabase.getsInstance(getApplicationContext());


        // Check for existing state after configuration change and restore the layout
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            rankingData = savedInstanceState.getParcelable("rankingData");
            /**
             * Updating UI from view model
             */
            setupRankingFromViewModel(rankingData);
        } else {
            Intent intentFromHome = getIntent();
            if (intentFromHome != null) {
                if (intentFromHome.hasExtra(Intent.EXTRA_TEXT)) {
                    rankingData = intentFromHome.getParcelableExtra(Intent.EXTRA_TEXT);
//                    setupMovieDetailFromViewModel(movieRecord);
                    /**
                     * Fetching destination ranking from API on destination details loading
                     */
//                    loadMovieReviewsInDB();
                    populateUI(rankingData);
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void populateUI(QOLRanking rankingData) {
        if (rankingData == null) {
            showErrorMessage();
        }

        Double QOLindex = Math.max(0, 100 + rankingData.getPurchasingPowerInclRentIndex() / 2.5 - (rankingData.getHousePriceToIncomeRatio() * 1.0) - rankingData.getCpiIndex() / 10 + rankingData.getSafetyIndex() / 2.0 + rankingData.getHealthcareIndex() / 2.5 - rankingData.getTrafficTimeIndex() / 2.0 - rankingData.getPollutionIndex() * 2.0 / 3.0 + rankingData.getClimateIndex() / 3.0);

        mPPiCircleView.setMaxValue(QOLindex.floatValue());
        mSafetyCircleView.setMaxValue(100);
        mHealthCircleView.setMaxValue(100);
        mClimateCircleView.setMaxValue(100);


        mPPIValue.setText(String.format("%.2f", Objects.requireNonNull(rankingData).getPurchasingPowerInclRentIndex()));
        mSafetyValue.setText(String.format("%.2f", Objects.requireNonNull(rankingData).getSafetyIndex()));
        mHealthValue.setText(String.format("%.2f", Objects.requireNonNull(rankingData).getHealthcareIndex()));
        mClimateValue.setText(String.format("%.2f", Objects.requireNonNull(rankingData).getClimateIndex()));

        mPPiCircleView.setValueAnimated(rankingData.getPurchasingPowerInclRentIndex().floatValue());
        mSafetyCircleView.setValueAnimated(rankingData.getSafetyIndex().floatValue());
        mHealthCircleView.setValueAnimated(rankingData.getHealthcareIndex().floatValue());
        mClimateCircleView.setValueAnimated(rankingData.getClimateIndex().floatValue());

        setContributorsData(rankingData.getCityName());

        this.setTitle(rankingData.getCityName());

    }

    private void setContributorsData(String cityName) {
        ApiService apiService = RetroClient.getApiService();

        Single<CrimeData> crimeDataCall = apiService.getDestinationCrimeData(BuildConfig.ApiKey, cityName);
        Single<HealthCareData> healthDataCall = apiService.getDestinationHealthData(BuildConfig.ApiKey, cityName);
        Single<ClimateData> climateDataCall = apiService.getDestinationClimateData(BuildConfig.ApiKey, cityName);

        @SuppressLint("SetTextI18n") Disposable disposable = Single.zip(crimeDataCall, healthDataCall, climateDataCall, (crimeData, healthCareData, climateData) -> {
            List<Integer> data = Arrays.asList(crimeData.getContributors(), healthCareData.getContributors(), climateData.getContributors());
            Integer maxValue = data.stream().mapToInt(v -> v).max().getAsInt();
            Integer minValue = data.stream().mapToInt(v -> v).min().getAsInt();

            return new Pair<>(maxValue, minValue);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contributors -> {
                    mMaxContribValue.setText(contributors.first.toString());
                    mMinContribValue.setText(contributors.second.toString());
                }, throwable -> showErrorMessage());

        disposables.add(disposable);

    }

    private void loadMovieReviewsInDB() {
    }


    private void setupRankingFromViewModel(QOLRanking rankingData) {
        final MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getRankingForCityId(rankingData.getCityId()).observe(this, new Observer<QOLRanking>() {
            @Override
            public void onChanged(@Nullable QOLRanking ranking) {
                viewModel.getRankingForCityId(rankingData.getCityId()
                ).removeObserver(this);
                Log.d(TAG, "Receiving database update from LiveData");
                populateUI(ranking);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("rankingData", rankingData);
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Saving rankingData in bundle during orientation change");

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        rankingData = savedInstanceState.getParcelable("rankingData");
        Log.d(TAG, "Restoring rankingData from bundle during orientation change");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.destination_detail, menu);
        // Fetch and store ShareActionProvider
//        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
//        setShareIntent(createShareRankingIntent());
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        /* Share menu item clicked */
        if (itemId == R.id.action_share) {
            Intent shareIntent = createShareRankingIntent();
            startActivity(shareIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Call to update the share intent
//        private void setShareIntent(Intent shareIntent) {
//            if (shareActionProvider != null) {
//                shareActionProvider.setShareIntent(shareIntent);
//            }
//        }
    private Intent createShareRankingIntent() {
        return Intent.createChooser(ShareCompat.IntentBuilder.from(this)
                .setChooserTitle("Share ranking data")
                .setType("text/plain")
                .setText(DESTINATION_URL + rankingData.getCityName())
                .getIntent(), getString(R.string.action_share));
    }

    private void showErrorMessage() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.network_error)
                .setMessage(R.string.network_error_msg)
                .setNegativeButton(R.string.error_dismiss_button, (dialog, which) -> dialog.dismiss()).create().show();
    }


    @Override
    protected void onPause() {
        disposables.dispose();
        super.onPause();
    }

}
