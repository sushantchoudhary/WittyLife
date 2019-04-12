package com.appdev.schoudhary.wittylife.ui.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.appdev.schoudhary.wittylife.BuildConfig;
import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.City;
import com.appdev.schoudhary.wittylife.model.CityIndices;
import com.appdev.schoudhary.wittylife.model.CityRecords;
import com.appdev.schoudhary.wittylife.network.ApiService;
import com.appdev.schoudhary.wittylife.network.RetroClient;
import com.appdev.schoudhary.wittylife.viewmodel.CityIndicesViewModel;
import com.appdev.schoudhary.wittylife.viewmodel.CityIndicesViewModelFactory;
import com.appdev.schoudhary.wittylife.viewmodel.MainViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.StackedValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ComparisonActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener, OnChartValueSelectedListener {
    private static final String TAG = ComparisonActivity.class.getSimpleName();
    private ActionBar actionBar;
    private ProgressBar mLoadingIndicator;
    private Spinner spinner;

    private MenuItem spinnerItem;

    private String sourceCity;

    private static AppDatabase mDB;
    private List<String> cityRecords;

    private PieChart piechart;
    private BarChart barChart;

    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY;

    private CompositeDisposable disposables = new CompositeDisposable();

    private Typeface tfLight;
    private Typeface tfRegular;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);

        Toolbar comparetoolbar = findViewById(R.id.toolbar);
        setSupportActionBar(comparetoolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        tfLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "OpenSans-Light.ttf");
        tfRegular = Typeface.createFromAsset(getApplicationContext().getAssets(), "Roboto-Regular.ttf");


        piechart = findViewById(R.id.piechart);

        barChart = findViewById(R.id.stackbar);
        barChart.setOnChartValueSelectedListener(this);

        tvX = findViewById(R.id.tvXMax);
        tvY = findViewById(R.id.tvYMax);

        seekBarX = findViewById(R.id.seekBar1);
        seekBarY = findViewById(R.id.seekBar2);

        seekBarX.setOnSeekBarChangeListener(this);
        seekBarY.setOnSeekBarChangeListener(this);



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
                    sourceCity = intentFromHome.getStringExtra(Intent.EXTRA_TEXT);
//                    setupMovieDetailFromViewModel(movieRecord);
                    /**
                     * Fetching destination ranking from API on destination details loading
                     */
//                    loadMovieReviewsInDB();
                    fetchAndUpdateIndicesFromAPI(sourceCity);
                    fetchAndUpdateSpinner();
                }
            }
        }
        /**
         * Setup pie piechart comparison
         */
        bindViewPieChart();


        /**
         *  Setup stack bar piechart
         */
        bindViewStackBarChart();

    }

    private void bindViewStackBarChart() {

        barChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the piechart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(40);

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);

        barChart.setDrawValueAboveBar(false);
        barChart.setHighlightFullBarEnabled(false);

        // change the position of the y-labels
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setEnabled(false);
//        leftAxis.setValueFormatter(new MyValueFormatter("K"));
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        barChart.getAxisRight().setEnabled(false);

        XAxis xLabels = barChart.getXAxis();
        xLabels.setEnabled(false);
//        xLabels.setPosition(XAxis.XAxisPosition.TOP);

        // barChart.setDrawXLabels(false);
        // barChart.setDrawYLabels(false);

        // setting data
        seekBarX.setProgress(12);
        seekBarY.setProgress(100);

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);
    }

    private void bindViewPieChart() {

        piechart.setUsePercentValues(true);
        piechart.getDescription().setEnabled(false);
        piechart.setExtraOffsets(5, 10, 5, 5);

        piechart.setDragDecelerationFrictionCoef(0.95f);


        piechart.setCenterTextTypeface(tfLight);
//        piechart.setCenterText(generateCenterSpannableText());

        piechart.setDrawHoleEnabled(false);
        piechart.setHoleColor(Color.GRAY);

        piechart.setTransparentCircleColor(Color.WHITE);
        piechart.setTransparentCircleAlpha(110);

        piechart.setHoleRadius(0f);
        piechart.setTransparentCircleRadius(0f);

        piechart.setDrawCenterText(false);

        piechart.setRotationAngle(0);
        // enable rotation of the piechart by touch
        piechart.setRotationEnabled(true);
        piechart.setHighlightPerTapEnabled(true);

        // piechart.setUnit(" €");
        // piechart.setDrawUnitsInChart(true);

        // add a selection listener
        piechart.setOnChartValueSelectedListener(this);


        seekBarX.setProgress(4);
        seekBarY.setProgress(10);

        piechart.animateXY(1400, 1400);
        // piechart.spin(2000, 0, 360);

        Legend l = piechart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(10f);
        l.setYEntrySpace(1f);
        l.setYOffset(0f);

        // entry label styling
        piechart.setEntryLabelColor(Color.BLACK);
        piechart.setEntryLabelTypeface(tfRegular);
        piechart.setEntryLabelTextSize(12f);
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
                if (cityList != null) {
                    cityList.forEach(city -> cityRecords.add(city.getCity()));
                }
                populateSpinnerData();
            }
        });
    }

    private void populateSpinnerData() {
        ArrayAdapter adapter = new ArrayAdapter<>(ComparisonActivity.this, android.R.layout.simple_spinner_item, cityRecords);

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

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.comparison_spinner, menu);

        spinnerItem = menu.findItem(R.id.compare_menu);
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
        if (!cityRecords.isEmpty()) {
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
        String selectedCity = (String) parent.getSelectedItem();

        //FIXME Should live in ViewModel
        fetchAndUpdateIndicesFromAPI(selectedCity);

        CityIndicesViewModelFactory selectedFactory = new CityIndicesViewModelFactory(mDB, selectedCity);
        final CityIndicesViewModel viewModel = ViewModelProviders.of(this, selectedFactory).get(CityIndicesViewModel.class);

        viewModel.getCityIndices().observe(this, new android.arch.lifecycle.Observer<CityIndices>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onChanged(@Nullable CityIndices selectedCityIndices) {
                if (sourceCity != null && selectedCityIndices != null) {

                    CityIndices sourceCityIndices = mDB.cityIndicesDao().loadCityByNameRaw(sourceCity);
                    bindChartingView(String.format("%.2f", sourceCityIndices.getQualityOfLifeIndex()),
                            String.format("%.2f", selectedCityIndices.getQualityOfLifeIndex()));

                }
            }
        });


//        CustomLiveData liveData = new CustomLiveData(mDB.cityIndicesDao().loadCityByName(sourceCity),
//                mDB.cityIndicesDao().loadCityByName(selectedCity));
//
//        LiveData<CityIndices> indices =  Transformations.switchMap(liveData ,
//                cityRecords -> bindChartingView(cityRecords.first, cityRecords.second));


    }

    private void bindChartingView(String sourceCity, String selectedCity) {
        System.out.printf("*************" + sourceCity + "**********" + selectedCity);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //TODO Do something better
        parent.setSelection(0);
    }

    //FIXME Implement Repository and network bound resource
    private void fetchAndUpdateSpinner() {
        if (mDB.cityDao().getRowCount() > 0) {
            populateSpinnerFromDB(null);
        } else {
            fetchAndUpdateSpinnerFromAPI();
        }
    }

    private void fetchAndUpdateSpinnerFromAPI() {
        Observable<CityRecords> callCityRecords;
        ApiService apiService = RetroClient.getApiService();
        callCityRecords = apiService.getCityRecords(BuildConfig.ApiKey);
//        mLoadingIndicator.setVisibility(View.VISIBLE);

        /**
         * Fetch city records data from api
         */
        //FIXME Long running task, must run as a background service
        callCityRecords.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CityRecords>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(CityRecords cityRecords) {
                        List<City> cities = cityRecords.getCities();
                        mDB.cityDao().insertCityList(cities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                        showErrorMessage();
                    }

                    @Override
                    public void onComplete() {
//                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                        populateSpinnerFromDB(null);
                    }
                });
    }


    /**
     * Fetch and update DB with city indices data
     *
     * @param cityName
     */
    private void fetchAndUpdateIndicesFromAPI(String cityName) {
        Single<CityIndices> callCityIndices;
        ApiService apiService = RetroClient.getApiService();
        callCityIndices = apiService.getCityIndices(BuildConfig.ApiKey, cityName);
        mLoadingIndicator.setVisibility(View.VISIBLE);

        Disposable disposable = callCityIndices.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cityIndices -> {
                            mLoadingIndicator.setVisibility(View.INVISIBLE);
                            mDB.cityIndicesDao().insertIndices(cityIndices);
                        }, throwable -> {
                            mLoadingIndicator.setVisibility(View.INVISIBLE);
                            showErrorMessage();
                        }
                );


        disposables.add(disposable);
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        tvX.setText(String.valueOf(seekBarX.getProgress()));
//        tvY.setText(String.valueOf(seekBarY.getProgress()));
        tvX.setText(String.valueOf(seekBarX.getProgress()));
        tvY.setText(String.valueOf(seekBarY.getProgress()));

        // Setup piechart data
        setData(seekBarX.getProgress(), seekBarY.getProgress());

        // Setup stackbarchart data
        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < seekBarX.getProgress(); i++) {
            float mul = (seekBarY.getProgress() + 1);
            float val1 = (float) (Math.random() * mul) + mul / 3;
            float val2 = (float) (Math.random() * mul) + mul / 3;
            float val3 = (float) (Math.random() * mul) + mul / 3;

            values.add(new BarEntry(
                    i,
                    new float[]{val1, val2, val3}));
        }

        BarDataSet set1;

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Statistics Vienna 2014");
            set1.setDrawIcons(false);
            set1.setColors(getColors());
            set1.setStackLabels(new String[]{"Births", "Divorces", "Marriages"});

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueFormatter(new StackedValueFormatter(true, "", 1));
            data.setValueTextColor(Color.WHITE);

            barChart.setData(data);
        }

        barChart.setFitBars(true);
        barChart.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
    }

    private void setData(int count, float range) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the piechart.
//        for (int i = 0; i < count ; i++) {
//            entries.add(new PieEntry((float) ((Math.random() * range) + range / 5),
//                    parties[i % parties.length],
//                    getResources().getDrawable(R.drawable.star)));
//        }

        entries.add(new PieEntry(18.5f, "Green"));
        entries.add(new PieEntry(26.7f, "Yellow"));
        entries.add(new PieEntry(24.0f, "Red"));
        entries.add(new PieEntry(30.8f, "Blue"));

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
//        data.setValueFormatter(new PercentFormatter(piechart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(tfLight);
        piechart.setData(data);

        // undo all highlights
        piechart.highlightValues(null);

        piechart.invalidate();
    }


    private int[] getColors() {

        // have as many colors as stack-values per entry
        int[] colors = new int[3];

        System.arraycopy(ColorTemplate.MATERIAL_COLORS, 0, colors, 0, 3);

        return colors;
    }

}


