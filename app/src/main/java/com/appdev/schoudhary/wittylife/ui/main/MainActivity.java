package com.appdev.schoudhary.wittylife.ui.main;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.appdev.schoudhary.wittylife.BuildConfig;
import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.City;
import com.appdev.schoudhary.wittylife.model.CityRecords;
import com.appdev.schoudhary.wittylife.model.DestinationImg;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.network.ApiService;
import com.appdev.schoudhary.wittylife.network.RetroClient;
import com.appdev.schoudhary.wittylife.network.UnsplashApiService;
import com.appdev.schoudhary.wittylife.utils.AppExecutors;
import com.appdev.schoudhary.wittylife.viewmodel.DestinationViewModel;
import com.appdev.schoudhary.wittylife.widget.RankingUpdateService;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


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
//        setTheme(R.style.AppTheme);

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
        //FIXME Move this view model to run only on network fetch
        clearDatabase();

        DestinationViewModel viewModel = ViewModelProviders.of(this).get(DestinationViewModel.class);

        viewModel.getIsLoading().observe(this, new android.arch.lifecycle.Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean loading) {
                if(loading) {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                } else {
                    mLoadingIndicator.setVisibility(View.GONE);
                }
            }
        });

        viewModel.getDestinationUrl().observe(this, destinationUrls -> {

            AppExecutors.getInstance().diskIO().execute(() -> {
                rankingList = mDB.qolDao().loadQOlRank();
//                mDB.photographerDao().loaduserById()
                rankingList.observe(this, new android.arch.lifecycle.Observer<List<QOLRanking>>() {
                    @Override
                    public void onChanged(@Nullable List<QOLRanking> qolRankings) {
                        if(qolRankings.size() == destinationUrls.size()) {
                            mainActivityAdapter = new MainActivityAdapter(qolRankings, destinationUrls, MainActivity.this);
                            mDestinationLayout.setAdapter(mainActivityAdapter);
                        }
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
            startActivity(intentToStartComparisonActivity);
        });

        /**
         * Set intent to launch RankingActivity on selecting Cost of living ranking image
         */
        findViewById(R.id.costofliving).setOnClickListener(v -> {
            Class destinationClass = RankingActivity.class;
            Intent intentToStartComparisonActivity = new Intent(MainActivity.this, destinationClass);
//                intentToStartComparisonActivity.putParcelableArrayListExtra(Intent.EXTRA_TEXT,new ArrayList<>(rankingList));
            intentToStartComparisonActivity.putExtra("RANKING_TYPE", RankingOptions.COST);
            startActivity(intentToStartComparisonActivity);
        });

        /**
         * Set intent to launch RankingActivity on selecting Traffic ranking image
         */
        findViewById(R.id.traffic).setOnClickListener(v -> {
            Class destinationClass = RankingActivity.class;
            Intent intentToStartComparisonActivity = new Intent(MainActivity.this, destinationClass);
//                intentToStartComparisonActivity.putParcelableArrayListExtra(Intent.EXTRA_TEXT,new ArrayList<>(rankingList));
            intentToStartComparisonActivity.putExtra("RANKING_TYPE", RankingOptions.TRAFFIC);
            startActivity(intentToStartComparisonActivity);
        });
    }

    private void clearDatabase() {
        /**
         * Clean database before fresh insertion
         */
        AppExecutors.getInstance().diskIO().execute(() -> {
            mDB.urlDao().deleteAllRows();
            mDB.photographerDao().deleteAllRows();
            mDB.qolDao().deleteAllRows();
            mDB.destinationDao().deleteAllRows();
        });
    }


    private void showErrorMessage() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.network_error)
                .setMessage(R.string.network_error_msg)
                .setNegativeButton(R.string.error_dismiss_button, (dialog, which) -> finish()).create().show();
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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }
        return super.dispatchTouchEvent(ev);
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
            qolranking.setTooltipText("Photo by " + "Larm Rmah" + "on Unsplash");
        }
        TooltipCompat.setTooltipText(qolranking, "Photo by " + "Larm Rmah" + " on Unsplash" );


        Picasso.with(this)
                .load(R.drawable.andrefrancoismckenzie557694unsplash)
                .fit()
                .placeholder(R.drawable.web_hi_res_512)
                .error(R.drawable.web_hi_res_512)
                .transform(new MaskTransformation(this, R.drawable.rounded_convers_transformation))

                .into(costranking);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            costranking.setTooltipText("Photo by " + "André François McKenzie" + "on Unsplash");
        }
        TooltipCompat.setTooltipText(costranking, "Photo by " + "André François McKenzie" + " on Unsplash" );


        Picasso.with(this)
                .load(R.drawable.laurenkay322313unsplash)
                .fit()
                .placeholder(R.drawable.web_hi_res_512)
                .error(R.drawable.web_hi_res_512)
                .transform(new MaskTransformation(this, R.drawable.rounded_convers_transformation))

                .into(trafficranking);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trafficranking.setTooltipText("Photo by " + "Lauren Kay" + "on Unsplash");
        }
        TooltipCompat.setTooltipText(trafficranking, "Photo by " + "Lauren Kay" + " on Unsplash" );
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

        startActivity(intentToStartDetailActivity);
    }

    /**
     * Load cities from API for city search validation
     */
    private void loadCitiesFromAPI() {
        Observable<CityRecords> callCityRecords;
        ApiService apiService = RetroClient.getApiService();
        callCityRecords = apiService.getCityRecords(BuildConfig.ApiKey);

        /**
         * Fetch city records data from api
         */
        //FIXME Long running task, must run as a background service
        Disposable disposable = callCityRecords.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(cityRecords -> {
                            List<City> cities = cityRecords.getCities();
                            AppExecutors.getInstance().diskIO().execute(() -> {
                                mDB.cityDao().insertCityList(cities);
                            });
                        }, throwable -> showErrorMessage()
                );

        disposables.add(disposable);
    }

    private void loadDestinationView() {
        Observable<List<QOLRanking>> callnumbeo;

        showRankingGridView();

        ApiService apiService = RetroClient.getApiService();
        UnsplashApiService unsplashApiService = RetroClient.getUnsplashApiService();

        callnumbeo = apiService.getQOLRanking(BuildConfig.ApiKey);

        clearDatabase();

        mLoadingIndicator.setVisibility(View.VISIBLE);

        callnumbeo.flatMapIterable(it -> it).take(6)
                .flatMap(qolRanking -> {

                    AppExecutors.getInstance().diskIO().execute(() -> mDB.runInTransaction(() -> {
                        long rowIds = mDB.qolDao().insertQOL(qolRanking);
                    }));

                    return unsplashApiService.getDestination(BuildConfig.UnsplashApiKey, qolRanking.getCityName());

                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DestinationImg>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(DestinationImg destinationImg) {
                        AppExecutors.getInstance().diskIO().execute(() -> {
                            mDB.destinationDao().insertDestinationList(destinationImg.getResults());
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                        mDestinationLayout.setVisibility(View.INVISIBLE);
                        showErrorMessage();
                    }

                    @Override
                    public void onComplete() {
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                        setupMainViewModel();
                    }
                });

    }

}
