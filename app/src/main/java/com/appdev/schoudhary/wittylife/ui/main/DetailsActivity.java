package com.appdev.schoudhary.wittylife.ui.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.viewmodel.MainViewModel;

import java.util.Objects;

import at.grabner.circleprogress.CircleProgressView;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();
    private TextView mDetailHeader;
    private TextView mPPIValue;
    private TextView mSafetyValue;
    private TextView mHealthValue;
    private TextView mClimateValue;

    private static AppDatabase mDB;


    private QOLRanking rankingData;
    private CircleProgressView mPPiCircleView;
    private CircleProgressView mSafetyCircleView;
    private CircleProgressView mHealthCircleView;
    private CircleProgressView mClimateCircleView;


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

        mPPiCircleView = findViewById(R.id.ppiCircleView);
        mSafetyCircleView = findViewById(R.id.safetyCircleView);
        mHealthCircleView = findViewById(R.id.healthCircleView);
        mClimateCircleView = findViewById(R.id.climateCircleView);


//        mPPiCircleView.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
//            @Override
//            public void onProgressChanged(float value) {
//                Log.d(TAG, "Progress Changed: " + value);
//            }
//        });


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

    private void showErrorMessage() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.network_error)
                .setMessage(R.string.network_error_msg)
                .setNegativeButton(R.string.error_dismiss_button, (dialog, which) -> dialog.dismiss()).create().show();
    }
}
