package com.appdev.schoudhary.wittylife.ui.main;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.TooltipCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.utils.AppExecutors;
import com.appdev.schoudhary.wittylife.viewmodel.DestinationViewModel;
import com.appdev.schoudhary.wittylife.widget.RankingUpdateService;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.disposables.CompositeDisposable;


public class MainActivity extends AppCompatActivity implements MainActivityAdapter.MainActivityAdapterOnClickHandler {

    private static final Integer SPAN_COUNT = 2;
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<QOLRanking> mQOLList;

    private SearchView searchView;
    private ImageView qolranking;
    private ImageView costranking;
    private ImageView trafficranking;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private ConstraintLayout parentLayout;
    private RecyclerView mDestinationLayout;
    private HorizontalScrollView mRankingsLayout;
    private MainActivityAdapter mainActivityAdapter;
    private static AppDatabase mDB;

    private FirebaseAnalytics mFirebaseAnalytics;

    private LiveData<List<QOLRanking>> rankingList;

    private CompositeDisposable disposables = new CompositeDisposable();

    final AtomicReference<Boolean> validCity = new AtomicReference<>(false);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        parentLayout = findViewById(R.id.mainactivity_layout);
        // Bring focus back from SearchView to activity layout
        parentLayout.requestFocus();


        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        qolranking = findViewById(R.id.qolranking);
        costranking = findViewById(R.id.costofliving);
        trafficranking = findViewById(R.id.traffic);

        mDestinationLayout = findViewById(R.id.rv_populardestination);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mRankingsLayout = findViewById(R.id.ranking_scrollview);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT,
                GridLayoutManager.VERTICAL, false);
        mDestinationLayout.setLayoutManager(gridLayoutManager);

        mDestinationLayout.setHasFixedSize(true);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mDB = AppDatabase.getsInstance(getApplicationContext());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        showRankingGridView();
        setupRankingView();

        if (savedInstanceState != null) {
            setupMainViewModel();
        } else {
            setupMainViewModel();
        }
    }

    private void setupMainViewModel() {
        setRankingsClickListener();

        DestinationViewModel viewModel = ViewModelProviders.of(this).get(DestinationViewModel.class);

        viewModel.getIsLoading().observe(this, loading -> {
            if(loading) {
                mLoadingIndicator.setVisibility(View.VISIBLE);
            } else {
                mLoadingIndicator.setVisibility(View.GONE);
            }
        });

        viewModel.getDestinationUrl().observe(this, destinationUrls -> {

            AppExecutors.getInstance().diskIO().execute(() -> {
                rankingList = mDB.qolDao().loadQOlRank();
                rankingList.observe(this, qolRankings -> {
                    if(qolRankings != null && qolRankings.size() == 10 &&
                            destinationUrls != null &&
                            destinationUrls.size() == 10) {
                        mainActivityAdapter = new MainActivityAdapter(qolRankings, destinationUrls, MainActivity.this);
                        mDestinationLayout.setAdapter(mainActivityAdapter);
                    }
                });

            });
            Log.d(TAG, "Updating urls from LiveData in ViewModel");

            //TODO : Fix this call {Skipping layout, no adapter found..}
//              layout.setQOLData(destinationUrls);
        });
    }

    private void setRankingsClickListener() {
        /**
         * Set intent to launch RankingActivity on selecting QOL ranking image
         */
        findViewById(R.id.qolranking).setOnClickListener(v -> {
            Class destinationClass = RankingActivity.class;
            Intent intentToStartComparisonActivity = new Intent(MainActivity.this, destinationClass);
//                intentToStartComparisonActivity.putParcelableArrayListExtra(Intent.EXTRA_TEXT,new ArrayList<>(rankingList));
            intentToStartComparisonActivity.putExtra("RANKING_TYPE", RankingOptions.QOL);
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();

            startActivity(intentToStartComparisonActivity, bundle);
        });

        /**
         * Set intent to launch RankingActivity on selecting Cost of living ranking image
         */
        findViewById(R.id.costofliving).setOnClickListener(v -> {
            Class destinationClass = RankingActivity.class;
            Intent intentToStartComparisonActivity = new Intent(MainActivity.this, destinationClass);
//                intentToStartComparisonActivity.putParcelableArrayListExtra(Intent.EXTRA_TEXT,new ArrayList<>(rankingList));
            intentToStartComparisonActivity.putExtra("RANKING_TYPE", RankingOptions.COST);
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();

            startActivity(intentToStartComparisonActivity, bundle);
        });

        /**
         * Set intent to launch RankingActivity on selecting Traffic ranking image
         */
        findViewById(R.id.traffic).setOnClickListener(v -> {
            Class destinationClass = RankingActivity.class;
            Intent intentToStartComparisonActivity = new Intent(MainActivity.this, destinationClass);
//                intentToStartComparisonActivity.putParcelableArrayListExtra(Intent.EXTRA_TEXT,new ArrayList<>(rankingList));
            intentToStartComparisonActivity.putExtra("RANKING_TYPE", RankingOptions.TRAFFIC);
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();

            startActivity(intentToStartComparisonActivity, bundle);
        });
    }


    private void showRankingGridView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRankingsLayout.setVisibility(View.VISIBLE);
        mDestinationLayout.setVisibility(View.VISIBLE);
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
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryRefinementEnabled(true);

        //To remove whiteline under the search view widget
        int searchPlateId = getApplicationContext().getResources().getIdentifier("android:id/search_plate", null, null);
        ViewGroup viewGroup = searchView.findViewById(searchPlateId);
        viewGroup.setBackgroundColor(Color.TRANSPARENT);

        //To remove whiteline under the search view submit button
        int searchSubmitId = getApplicationContext().getResources().getIdentifier("android:id/submit_area", null, null);
        ViewGroup submitGroup = searchView.findViewById(searchSubmitId);
        submitGroup.setBackgroundColor(Color.TRANSPARENT);

        int searchTextId = getApplicationContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView searchTextView = searchView.findViewById(searchTextId);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.cursor);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.getCause();
        }

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

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {

        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if (validCity.get()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, query);
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);

                    return false;
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.no_match_message), Toast.LENGTH_LONG);
                    toast.show();
                    return true;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Invalidate current selection on edit after activity resume
                validCity.set(false);
                searchView.setSubmitButtonEnabled(true);
                return isValidCity(newText);
            }
        });

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(new ComponentName(this, DetailsActivity.class)));

        return true;
    }

    private Boolean isValidCity(String searchText) {

        AppExecutors.getInstance().diskIO().execute(() -> {
            if (mDB.cityDao().loadCityByName(searchText) != null) {
                validCity.set(true);
            }
        });
        return validCity.get();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // To bring focus back from SearchView to activity layout
        parentLayout.requestFocus();

//        setupMainViewModel();
    }

    @Override
    protected void onRestart() {
//        loadDestinationView();
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disposables.clear();
    }


        @Override
    public void onBackPressed() {

        if(searchView.isIconified() && searchView != null) {
            searchView.setIconified(false);
            searchView.clearFocus();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }


    private void setupRankingView() {
        Picasso.with(this)
                .load(R.drawable.larmrmah216854unsplash)
                .fit()
                .placeholder(R.drawable.web_hi_res_512)
                .error(R.drawable.web_hi_res_512)
                .transform(new MaskTransformation(this, R.drawable.rounded_convers_transformation))
                .into(qolranking);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            qolranking.setTooltipText(getString(R.string.qol_image_tooltip));
        }
        TooltipCompat.setTooltipText(qolranking, getString(R.string.qol_image_tooltip) );


        Picasso.with(this)
                .load(R.drawable.andrefrancoismckenzie557694unsplash)
                .fit()
                .placeholder(R.drawable.web_hi_res_512)
                .error(R.drawable.web_hi_res_512)
                .transform(new MaskTransformation(this, R.drawable.rounded_convers_transformation))

                .into(costranking);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            costranking.setTooltipText(getString(R.string.cost_image_tooltip));
        }
        TooltipCompat.setTooltipText(costranking, getString(R.string.cost_image_tooltip) );


        Picasso.with(this)
                .load(R.drawable.laurenkay322313unsplash)
                .fit()
                .placeholder(R.drawable.web_hi_res_512)
                .error(R.drawable.web_hi_res_512)
                .transform(new MaskTransformation(this, R.drawable.rounded_convers_transformation))

                .into(trafficranking);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trafficranking.setTooltipText(getString(R.string.traffic_image_tooltip));
        }
        TooltipCompat.setTooltipText(trafficranking, getString(R.string.traffic_image_tooltip) );
    }


    @Override
    public void onClick(QOLRanking rankingData) {
        Context context = this;
        Class destinationClass = DetailsActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, rankingData);

        /**
         * Pass current top ranking to widget provider
         */
        RankingUpdateService.startActionUpdateRanking(getApplicationContext(), rankingData);

        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle();
        startActivity(intentToStartDetailActivity, bundle);
    }

}
