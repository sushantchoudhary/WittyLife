package com.appdev.schoudhary.wittylife.ui.main;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.appdev.schoudhary.wittylife.BuildConfig;
import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.SearchResultActivity;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.DestinationImg;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.model.Urls;
import com.appdev.schoudhary.wittylife.network.ApiService;
import com.appdev.schoudhary.wittylife.network.RetroClient;
import com.appdev.schoudhary.wittylife.network.UnsplashApiService;
import com.appdev.schoudhary.wittylife.utils.AppExecutors;
import com.appdev.schoudhary.wittylife.viewmodel.DestinationUrlViewModel;
import com.appdev.schoudhary.wittylife.viewmodel.MainViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

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

    private RecyclerView mDestinationLayout;
    private MainActivityAdapter mainActivityAdapter;
    private static AppDatabase mDB;

    private HashMap<QOLRanking, Urls> destinationData;

    private List<QOLRanking> rankingList;

    private CompositeDisposable disposables = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        qolranking = findViewById(R.id.qolranking);
        costranking = findViewById(R.id.costofliving);
        trafficranking = findViewById(R.id.traffic);


        mDestinationLayout = findViewById(R.id.rv_populardestination);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT,
                GridLayoutManager.VERTICAL, false);
        mDestinationLayout.setLayoutManager(gridLayoutManager);

        mDestinationLayout.setHasFixedSize(true);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mDB = AppDatabase.getsInstance(getApplicationContext());

        setupRankingView();

        if (savedInstanceState != null) {
            setupMainViewModel();
        } else {
            loadDestinationView();
        }


    }

    private void loadDestinationView() {
        Observable<List<QOLRanking>> callnumbeo;

        showMoviewGridView();

        ApiService apiService = RetroClient.getApiService();
        UnsplashApiService unsplashApiService = RetroClient.getUnsplashApiService();


        callnumbeo = apiService.getQOLRanking(BuildConfig.ApiKey);

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
                        Urls urls = destinationImg.getResults().get(0).getUrls();
                        mDB.urlDao().insertURL(urls);
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


    private void showErrorMessage() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.network_error)
                .setMessage(R.string.network_error_msg)
                .setNegativeButton(R.string.error_dismiss_button, (dialog, which) -> finish()).create().show();
    }

    private void showMoviewGridView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mDestinationLayout.setVisibility(View.VISIBLE);
    }


    private void setupMainViewModel() {
        DestinationUrlViewModel viewModel = ViewModelProviders.of(this).get(DestinationUrlViewModel.class);
        viewModel.getDestinationUrl().observe(this, destinationUrls -> {

            rankingList = mDB.qolDao().loadQOlRank();

            Log.d(TAG, "Updating urls from LiveData in ViewModel");
            mainActivityAdapter = new  MainActivityAdapter(rankingList, destinationUrls, MainActivity.this);
            mDestinationLayout.setAdapter(mainActivityAdapter);
            //TODO : Fix this call {Skipping layout, no adapter found..}
//              layout.setQOLData(destinationUrls);
        });
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
                searchManager.getSearchableInfo(new ComponentName(this, SearchResultActivity.class)));

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disposables.clear();
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


    private void setupRankingView() {
        Picasso.with(this)
                .load(R.drawable.larmrmah216854unsplash)
                .fit()
                .placeholder(R.drawable.baseline_search_24)
                .error(R.drawable.baseline_search_24)
                .transform(new MaskTransformation(this, R.drawable.rounded_convers_transformation))
                .into(qolranking);

        Picasso.with(this)
                .load(R.drawable.andrefrancoismckenzie557694unsplash)
                .fit()
                .placeholder(R.drawable.baseline_search_24)
                .error(R.drawable.baseline_search_24)
                .transform(new MaskTransformation(this, R.drawable.rounded_convers_transformation))

                .into(costranking);

        Picasso.with(this)
                .load(R.drawable.laurenkay322313unsplash)
                .fit()
                .placeholder(R.drawable.baseline_search_24)
                .error(R.drawable.baseline_search_24)
                .transform(new MaskTransformation(this, R.drawable.rounded_convers_transformation))

                .into(trafficranking);
    }


    @Override
    public void onClick(QOLRanking rankingData) {
        Context context = this;
        Class destinationClass = DetailsActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, rankingData);
        startActivity(intentToStartDetailActivity);
    }
}
