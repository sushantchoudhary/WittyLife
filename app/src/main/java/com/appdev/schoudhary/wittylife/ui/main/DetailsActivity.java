package com.appdev.schoudhary.wittylife.ui.main;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.SuggestionProvider;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.databinding.ActivityDetailsBinding;
import com.appdev.schoudhary.wittylife.model.CityIndices;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.viewmodel.CityIndicesViewModel;
import com.appdev.schoudhary.wittylife.viewmodel.ContribDataViewModel;
import com.appdev.schoudhary.wittylife.viewmodel.ContribDataViewModelFactory;
import com.appdev.schoudhary.wittylife.viewmodel.MainViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.Objects;

import javax.annotation.Nonnull;

import at.grabner.circleprogress.CircleProgressView;
import io.reactivex.disposables.CompositeDisposable;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();
    private static final String DESTINATION_URL = "https://www.numbeo.com/quality-of-life/in/";
    private TextView mDetailHeader, mPPIValue, mSafetyValue, mHealthValue, mClimateValue, mMinContribValue, mMaxContribValue ;
    private TextView mMinContribText;
    private TextView mMaxContribText;

    private ProgressBar mLoadingIndicator;

    private Pair<Integer, Integer> contribData = new Pair<>(0, 0);

    private FloatingActionButton floatingActionButton;
    private TableLayout tableLayout;
    private View headerDivider, footerDivider;

    private ConstraintLayout emptyStateView;

    private static AppDatabase mDB;


    private QOLRanking rankingData;
    private CircleProgressView mPPiCircleView, mSafetyCircleView, mHealthCircleView, mClimateCircleView ;

    private CompositeDisposable disposables = new CompositeDisposable();
    private ShareActionProvider shareActionProvider;

    private String searchResultCityName;
    private ShimmerFrameLayout mShimmerMinContainer, mShimmerMaxContainer;

    private LottieAnimationView lottieAnimationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        binding.setLifecycleOwner(this);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mShimmerMinContainer = findViewById(R.id.shimmer_min_container);
        mShimmerMaxContainer = findViewById(R.id.shimmer_max_container);

        final ConstraintLayout detailsLayout = binding.detailsLayout;

        mDetailHeader = binding.detailHeader;

        mPPIValue = binding.ppiValue;

        mSafetyValue = binding.safetyValue;

        mHealthValue = binding.healthcareValue;

        mClimateValue = binding.climateValue;

        mMinContribValue = binding.destinationMinContrib;

        mMaxContribValue = binding.destinationMaxContrib;

        mMinContribText = binding.minContribText;

        mMaxContribText = binding.maxContribText;

        headerDivider = binding.detailDividerMain;

        footerDivider = binding.detailDividerFooter;

        floatingActionButton = binding.compareFab;

        tableLayout = binding.tableLayout;

        mPPiCircleView = binding.ppiCircleView;

        mSafetyCircleView = binding.safetyCircleView;

        mHealthCircleView = binding.healthCircleView;

        mClimateCircleView = binding.climateCircleView;

        mLoadingIndicator = binding.pbLoadingIndicator;

        final MaterialButton searchAgain = findViewById(R.id.empty_state_header);
        emptyStateView = findViewById(R.id.empty_state_view);
        final TextView mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        mDB = AppDatabase.getsInstance(getApplicationContext());



        // Check for existing state after configuration change and restore the layout
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            rankingData = savedInstanceState.getParcelable("rankingData");
            searchResultCityName = savedInstanceState.getString("searchResultCityName");
            contribData = new Pair<>(savedInstanceState.getInt("maxContribData"),
                    savedInstanceState.getInt("minContribData"));

            /**
             * Updating UI from view model
             */
            if (searchResultCityName != null) {
                bindSearchUI(searchResultCityName);
            } else {
                setupRankingFromViewModel(rankingData);
            }

        } else {
            Intent intentFromHome = getIntent();
            if (intentFromHome != null) {
                if (intentFromHome.hasExtra(Intent.EXTRA_TEXT)) {
                    rankingData = intentFromHome.getParcelableExtra(Intent.EXTRA_TEXT);
//                    setupMovieDetailFromViewModel(movieRecord);
                    /**
                     * Fetching destination ranking from API on destination details loading
                     */
                    populateUI(rankingData);
                }
                // Handle ACTION_SEARCH intent
                handleIntent(intentFromHome);
            }
        }

        //Hide FAB so that context data is saved before user can navigate to comparison activity.
        floatingActionButton.hide();

        floatingActionButton.setOnClickListener(view -> {
            Class destinationClass = ComparisonActivity.class;
            Intent intentToStartComparisonActivity = new Intent(DetailsActivity.this, destinationClass);
            String name = (searchResultCityName != null) ? searchResultCityName : rankingData.getCityName();
            intentToStartComparisonActivity.putExtra(Intent.EXTRA_TEXT, name);
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(DetailsActivity.this).toBundle();

            startActivity(intentToStartComparisonActivity, bundle);
        });

        searchAgain.setOnClickListener(view -> finish());
    }

    @SuppressLint("DefaultLocale")
    private void populateUI(QOLRanking rankingData) {
        if (rankingData == null || rankingData.getPurchasingPowerInclRentIndex() == null) {
            startEmptyStateAnimation();
            return;
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

    @SuppressLint("DefaultLocale")
    private void populateUIFromSearch(@Nonnull Float purchasingPowerInclRentIndex,
                                      @Nonnull Float propertyPriceToIncomeRatio,
                                      @Nonnull Float cpiIndex,
                                      @Nonnull Float safetyIndex,
                                      @Nonnull Float healthcareIndex,
                                      @Nonnull Float trafficTimeIndex,
                                      @Nonnull Float pollutionIndex,
                                      @Nonnull Float climateIndex) {



        if(purchasingPowerInclRentIndex == null  ) {
            startEmptyStateAnimation();
            return;
        }

        Double QOLindex = Math.max(0, 100 + purchasingPowerInclRentIndex / 2.5
                - (propertyPriceToIncomeRatio * 1.0)
                - cpiIndex / 10 + safetyIndex / 2.0
                + healthcareIndex / 2.5
                - trafficTimeIndex / 2.0
                - pollutionIndex * 2.0 / 3.0
                + climateIndex / 3.0);


        mPPiCircleView.setMaxValue(QOLindex.floatValue());
        mSafetyCircleView.setMaxValue(100);
        mHealthCircleView.setMaxValue(100);
        mClimateCircleView.setMaxValue(100);


        mPPIValue.setText(String.format("%.2f", purchasingPowerInclRentIndex));
        mSafetyValue.setText(String.format("%.2f", safetyIndex));
        mHealthValue.setText(String.format("%.2f", healthcareIndex));
        mClimateValue.setText(String.format("%.2f", climateIndex));

        mPPiCircleView.setValueAnimated(purchasingPowerInclRentIndex);
        mSafetyCircleView.setValueAnimated(safetyIndex);
        mHealthCircleView.setValueAnimated(healthcareIndex);
        mClimateCircleView.setValueAnimated(climateIndex);

        setContributorsData(searchResultCityName);

        this.setTitle(searchResultCityName);

    }

    private void startEmptyStateAnimation() {
        mDetailHeader.setVisibility(View.INVISIBLE);
        tableLayout.setVisibility(View.INVISIBLE);
        mMinContribText.setVisibility(View.INVISIBLE);
        mMinContribValue.setVisibility(View.INVISIBLE);
        mMaxContribText.setVisibility(View.INVISIBLE);
        mMaxContribText.setVisibility(View.INVISIBLE);
        mShimmerMinContainer.setVisibility(View.INVISIBLE);
        mShimmerMaxContainer.setVisibility(View.INVISIBLE);

        headerDivider.setVisibility(View.INVISIBLE);
        footerDivider.setVisibility(View.INVISIBLE);

        emptyStateView.setVisibility(View.VISIBLE);

    }

    @SuppressLint("SetTextI18n")
    private void setContributorsData(String cityName) {

        if (contribData != null && contribData.first != 0) {
            setContribViewValue(Objects.requireNonNull(contribData.second.toString()), Objects.requireNonNull(contribData.first.toString()));
        } else {
            setContribDataFromViewModel(cityName);
        }
    }

    private void setContribDataFromViewModel(String cityName) {
        ContribDataViewModel contribDataViewModel = ViewModelProviders.of(this, new ContribDataViewModelFactory(cityName)).get(ContribDataViewModel.class);
        contribDataViewModel.getContribData().observe(this, new Observer<Pair<Integer, Integer>>() {
            @Override
            public void onChanged(@Nullable Pair<Integer, Integer> contribPair) {
                contribData =  contribPair;
            }
        });
        contribDataViewModel.getIsLoading().observe(this, loading -> {
            if(loading != null && loading) {
                mShimmerMinContainer.startShimmer();
                mShimmerMaxContainer.startShimmer();
            } else if (contribData != null){
                setContribViewValue(contribData.second.toString(), contribData.first.toString());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setContribViewValue(String minValue, String maxValue) {
        mMinContribText.setVisibility(View.VISIBLE);
        mMinContribValue.setVisibility(View.VISIBLE);

        mMinContribValue.setText(minValue);

        mShimmerMinContainer.stopShimmer();
        mShimmerMinContainer.setVisibility(View.GONE);

        mMaxContribValue.setVisibility(View.VISIBLE);
        mMaxContribText.setVisibility(View.VISIBLE);

        mMaxContribValue.setText(maxValue);

        mShimmerMaxContainer.stopShimmer();
        mShimmerMaxContainer.setVisibility(View.GONE);
        // Show FAB only after the contribution values have populated
        floatingActionButton.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchResultCityName = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions recentSuggestions = new SearchRecentSuggestions(
                    this, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            recentSuggestions.clearHistory();
            //TODO : Fix suggestion popup background and enable it again
//            recentSuggestions.saveRecentQuery(searchResultCityName, null);
            //use the query to search your data somehow

            bindSearchUI(searchResultCityName);
        }
    }

    private void bindSearchUI(String cityName) {

        mDB.cityIndicesDao().loadCityByName("%" + cityName + "%").observe(this, new Observer<CityIndices>() {
            @Override
            public void onChanged(@Nullable CityIndices cityIndices) {

                if (cityIndices != null) {
                    populateUIFromSearch(cityIndices.getPurchasingPowerInclRentIndex(),
                            cityIndices.getPropertyPriceToIncomeRatio(),
                            cityIndices.getCpiAndRentIndex(),
                            cityIndices.getSafetyIndex(),
                            cityIndices.getHealthCareIndex(),
                            cityIndices.getTrafficTimeIndex(),
                            cityIndices.getPollutionIndex(),
                            cityIndices.getClimateIndex());
                } else {
                    fetchAndUpdateIndicesFromViewModel(cityName);
                }
            }
        });
    }
    private void fetchAndUpdateIndicesFromViewModel(String cityName) {

        final CityIndicesViewModel viewModel = ViewModelProviders.of(this).get(CityIndicesViewModel.class);

        viewModel.getCityIndex(cityName).observe(this, new android.arch.lifecycle.Observer<CityIndices>() {
            @Override
            public void onChanged(@Nullable CityIndices cityIndices) {
                if (cityIndices != null) {
                    populateUIFromSearch(cityIndices.getPurchasingPowerInclRentIndex(),
                            cityIndices.getPropertyPriceToIncomeRatio(),
                            cityIndices.getCpiAndRentIndex(),
                            cityIndices.getSafetyIndex(),
                            cityIndices.getHealthCareIndex(),
                            cityIndices.getTrafficTimeIndex(),
                            cityIndices.getPollutionIndex(),
                            cityIndices.getClimateIndex());
                } else {
                    showErrorMessage();
                }
            }
        });
        viewModel.getIsLoading().observe(this, new android.arch.lifecycle.Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean loading) {
                if(loading != null && loading) {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                } else {
                    mLoadingIndicator.setVisibility(View.GONE);
                }
            }});
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
        outState.putString("searchResultCityName", searchResultCityName);
        outState.putInt("minContribData", contribData.second);
        outState.putInt("maxContribData", contribData.first);

        super.onSaveInstanceState(outState);
        Log.d(TAG, "Saving rankingData in bundle during orientation change");

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        rankingData = savedInstanceState.getParcelable("rankingData");
        searchResultCityName = savedInstanceState.getString("searchResultCityName");
        contribData = new Pair<>(
                savedInstanceState.getInt("maxContribData"),
                savedInstanceState.getInt("minContribData"));
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

       if (itemId == android.R.id.home) {
        supportFinishAfterTransition();
        return true;
    }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareRankingIntent() {
        String cityName = (searchResultCityName != null) ? searchResultCityName : rankingData.getCityName();

        return Intent.createChooser(ShareCompat.IntentBuilder.from(this)
                .setChooserTitle("Share ranking data")
                .setType("text/plain")
                .setText(DESTINATION_URL + cityName)
                .getIntent(), getString(R.string.action_share));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (rankingData != null) {
            setupRankingFromViewModel(rankingData);
        } else {
            bindSearchUI(searchResultCityName);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShimmerMinContainer.startShimmer();
        mShimmerMaxContainer.startShimmer();
    }

    @Override
    protected void onPause() {
        disposables.clear();
        mShimmerMinContainer.stopShimmer();
        mShimmerMaxContainer.stopShimmer();
        super.onPause();
    }

    private void showErrorMessage() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.network_error)
                .setMessage(R.string.network_error_msg)
                .setNegativeButton(R.string.error_dismiss_button, (dialog, which) -> dialog.dismiss()).create().show();
    }


    @Override
    public void onBackPressed() {
        overridePendingTransition(0, 0);
        super.onBackPressed();
    }
}
