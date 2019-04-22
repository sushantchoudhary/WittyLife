package com.appdev.schoudhary.wittylife.ui.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.appdev.schoudhary.wittylife.BuildConfig;
import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.City;
import com.appdev.schoudhary.wittylife.model.CityIndices;
import com.appdev.schoudhary.wittylife.model.CityRecords;
import com.appdev.schoudhary.wittylife.network.ApiService;
import com.appdev.schoudhary.wittylife.network.RetroClient;
import com.appdev.schoudhary.wittylife.utils.AppExecutors;
import com.appdev.schoudhary.wittylife.viewmodel.CityIndicesViewModel;
import com.appdev.schoudhary.wittylife.viewmodel.MainViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ComparisonActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnChartValueSelectedListener {
    private static final String TAG = ComparisonActivity.class.getSimpleName();
    private ActionBar actionBar;
    private ProgressBar mLoadingIndicator;
    private Spinner spinner;

    private MenuItem spinnerItem;

    private String sourceCity;
    private String selectedCity;

    private static AppDatabase mDB;
    private List<String> cityRecords;

    private PieChart piechart;
    private BarChart barChart;

    private CompositeDisposable disposables = new CompositeDisposable();

    private Typeface tfLight;
    private Typeface tfRegular;

    private Boolean spinnerTouched = false;


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

        mLoadingIndicator = findViewById(R.id.compare_loading_indicator);

        mDB = AppDatabase.getsInstance(getApplicationContext());

        cityRecords = new ArrayList<>();

        //FIXME Hack to force onCreateOptionMenu() to load spinner data
        invalidateOptionsMenu();


        // Check for existing state after configuration change and restore the layout
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            cityRecords = savedInstanceState.getStringArrayList("cityRecords");
//            savedInstanceState.getInt("spinnerPosition");
            selectedCity = savedInstanceState.getString("selectedCity");
            sourceCity = savedInstanceState.getString("sourceCity");
            /**
             * Updating Chart UI from View Model
             */
            setupSelectedCityFromViewModel(selectedCity);
        } else {
            Intent intentFromHome = getIntent();
            if (intentFromHome != null) {
                if (intentFromHome.hasExtra(Intent.EXTRA_TEXT)) {
                    sourceCity = intentFromHome.getStringExtra(Intent.EXTRA_TEXT);
//                    setupMovieDetailFromViewModel();
                    /**
                     * Fetching destination ranking from API on destination details loading
                     */
//                    loadReviewsInDB();
                    fetchAndUpdateIndicesFromAPI(intentFromHome.getStringExtra(Intent.EXTRA_TEXT));
                    fetchAndUpdateSpinner();
                }
            }
        }
        /**
         * Setup pie chart comparison
         */
        bindViewPieChart();


        /**
         *  Setup stack bar  comparison
         */
        bindViewStackBarChart();

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList("cityRecords", (ArrayList<String>) cityRecords);

        outState.putString("sourceCity", sourceCity);
        outState.putString("selectedCity", selectedCity);

        Log.d(TAG, "Saving cityRecords in bundle during orientation change");

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cityRecords = savedInstanceState.getStringArrayList("cityRecords");

        //FIXME Use viewmodel to save UI state
        sourceCity = savedInstanceState.getString("sourceCity");
        selectedCity = savedInstanceState.getString("selectedCity");


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

        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("Real value selected in spinner.");
                spinnerTouched = true;
                return false;
            }
        });

        spinner.setOnItemSelectedListener(ComparisonActivity.this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //FIXME Update menu icon color to white
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
        String selectedItem = (String) parent.getSelectedItem();

        //FIXME Should live in ViewModel, check in Db first then API
        if (spinnerTouched) {
            fetchAndUpdateIndicesFromAPI(selectedItem);
        }
        spinnerTouched = false;


//        CustomLiveData liveData = new CustomLiveData(mDB.cityIndicesDao().loadCityByName(sourceCity),
//                mDB.cityIndicesDao().loadCityByName(selectedCity));
//
//        LiveData<CityIndices> indices =  Transformations.switchMap(liveData ,
//                cityRecords -> bindChartingView(cityRecords.first, cityRecords.second));
    }

    private void setupSelectedCityFromViewModel(String selectedCity) {
        final CityIndicesViewModel viewModel = ViewModelProviders.of(this).get(CityIndicesViewModel.class);

        viewModel.loadCity(selectedCity);


        viewModel.getCityIndices().observe(this, new android.arch.lifecycle.Observer<CityIndices>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onChanged(@Nullable CityIndices selectedCityIndices) {
                if (selectedCityIndices != null && sourceCity != null) {

                    AppExecutors.getInstance().diskIO().execute(() -> {
                        CityIndices sourceCityIndices = mDB.cityIndicesDao().loadCityByNameRaw(sourceCity);

                        if (selectedCityIndices.getSafetyIndex() != null
                                && selectedCityIndices.getClimateIndex() != null
                                && sourceCityIndices != null
                                && !selectedCity.equals(sourceCity)) {

                            bindChartingView(sourceCityIndices, selectedCityIndices);

                        }
                    });
                } else {
                    clearChartData();
                }
            }
        });
    }


    private void bindChartingView(@NonNull CityIndices sourceCityQOLIndex, @NonNull CityIndices selectedCityQOLIndex) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        if (sourceCityQOLIndex.getSafetyIndex() != null && selectedCityQOLIndex.getSafetyIndex() != null) {
            Float sourceCity = Float.valueOf(decimalFormat.format(sourceCityQOLIndex.getSafetyIndex()));
            Float selectedCity = Float.valueOf(decimalFormat.format(selectedCityQOLIndex.getSafetyIndex()));
            Map<String, Float> qolData = new HashMap<>();
            qolData.put(sourceCityQOLIndex.getName(), sourceCity);
            qolData.put(selectedCityQOLIndex.getName(), selectedCity);

            setPieData(qolData);
        } else {
            piechart.clear();
            piechart.invalidate();
        }

        if (sourceCityQOLIndex.getClimateIndex() != null && selectedCityQOLIndex.getClimateIndex() != null) {

            Float sourceCityClimate = Float.valueOf(decimalFormat.format(sourceCityQOLIndex.getClimateIndex()));
            Float selectedCityClimate = Float.valueOf(decimalFormat.format(selectedCityQOLIndex.getClimateIndex()));
            Map<String, Float> climateData = new HashMap<>();
            climateData.put(sourceCityQOLIndex.getName(), sourceCityClimate);
            climateData.put(selectedCityQOLIndex.getName(), selectedCityClimate);

            setStackData(climateData);
        } else {
            barChart.clear();
            barChart.invalidate();
        }
    }


    private void bindViewStackBarChart() {

        barChart.getDescription().setEnabled(false);
        barChart.getDescription().setText("Climate Index");
        barChart.getDescription().setTextSize(12f);

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
//        seekBarX.setProgress(12);
//        seekBarY.setProgress(100);

        barChart.setNoDataText("No climate data available for comparison");
        barChart.setNoDataTextColor(R.color.colorAccent);
        Paint paint = barChart.getPaint(Chart.PAINT_INFO);
        paint.setTextSize(32f);

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);
        l.setEnabled(true);
        l.setTextSize(12f);
        l.setWordWrapEnabled(true);
        l.setForm(Legend.LegendForm.CIRCLE);


    }

    private void bindViewPieChart() {

        piechart.setUsePercentValues(true);
        piechart.getDescription().setEnabled(true);
        piechart.getDescription().setTextSize(12f);
        piechart.getDescription().setText("Safety Rating");
        piechart.setExtraOffsets(10, 20, 10, 10);

        piechart.setDragDecelerationFrictionCoef(0.95f);


        piechart.setCenterTextTypeface(tfLight);
//        piechart.setCenterText(generateCenterSpannableText());

        piechart.setDrawHoleEnabled(false);
        piechart.setHoleColor(Color.GRAY);

        piechart.setTransparentCircleColor(Color.BLACK);
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


//        seekBarX.setProgress(4);
//        seekBarY.setProgress(10);

        piechart.animateXY(1400, 1400);
        // piechart.spin(2000, 0, 360);

        Legend l = piechart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);

        l.setFormSize(8f);
        l.setFormToTextSpace(4f);

        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setYOffset(0f);
        l.setXOffset(0f);
        l.setWordWrapEnabled(true);
        l.setEnabled(false);

        // entry label styling
        piechart.setEntryLabelColor(Color.BLACK);
        piechart.setEntryLabelTypeface(tfRegular);
        piechart.setEntryLabelTextSize(10f);

        piechart.setNoDataText("No safety data available for comparison");
        piechart.setNoDataTextColor(R.color.colorAccent);
        Paint paint = piechart.getPaint(Chart.PAINT_INFO);
        paint.setTextSize(32f);

    }

    private void setStackData(@NonNull Map<String, Float> pppData) {
        ArrayList<BarEntry> values = new ArrayList<>();

        List<Float> stackData = new ArrayList<>();
        List<String> stackCity = new ArrayList<>();

        for (Map.Entry<String, Float> stringFloatEntry : pppData.entrySet()) {
            stackData.add(stringFloatEntry.getValue());
            stackCity.add(stringFloatEntry.getKey());
        }

        values.add(new BarEntry(1f, new float[]{stackData.get(0), stackData.get(1)}));

        BarDataSet set1;

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setColors(getColors());
            set1.setStackLabels(new String[]{stackCity.get(0), stackCity.get(1)});
            set1.setValues(values);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Climate Index");
            set1.setDrawIcons(false);
            set1.setColors(getColors());
            set1.setStackLabels(new String[]{stackCity.get(0), stackCity.get(1)});

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueFormatter(new StackedValueFormatter(true, "", 1));
            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(12f);

            barChart.setData(data);
        }


        barChart.setFitBars(true);
        barChart.invalidate();
    }

    private void setPieData(Map<String, Float> qolIndex) {
        ArrayList<PieEntry> entries = new ArrayList<>();

//        entries.add(new PieEntry(18.5f, "Green"));
//        entries.add(new PieEntry(26.7f, "Yellow"));
//        entries.add(new PieEntry(24.0f, "Red"));
//        entries.add(new PieEntry(30.8f, "Blue"));
        qolIndex.forEach((k, v) -> entries.add(new PieEntry(v, k)));


        PieDataSet dataSet = new PieDataSet(entries, "Safety Rating");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);

//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);

//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.MATERIAL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
//        data.setValueFormatter(new PercentFormatter(piechart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(tfRegular);

        piechart.setData(data);
        piechart.getData().notifyDataChanged();
        piechart.notifyDataSetChanged();

        // undo all highlights
        piechart.highlightValues(null);

        piechart.invalidate();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //TODO Do something better
        parent.setSelection(0);
    }

    //FIXME Implement Repository and network bound resource
    private void fetchAndUpdateSpinner() {

        AppExecutors.getInstance().diskIO().execute(() -> {
            int count = mDB.cityDao().getRowCount();
            if (count > 0) {
                populateSpinnerFromDB(null);
            } else {
                fetchAndUpdateSpinnerFromAPI();
            }
        });
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
                        AppExecutors.getInstance().diskIO().execute(() -> {
                            mDB.cityDao().insertCityList(cities);
                        });
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
                            if (cityIndices.getName() != null) {

                                AppExecutors.getInstance().diskIO().execute(() -> {
                                    mDB.cityIndicesDao().insertIndices(cityIndices);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Skipping any comparison  on activity start
                                            if (cityIndices.getName().contains(sourceCity)) {
                                                sourceCity = cityIndices.getName();
                                            } else {
                                                selectedCity = cityIndices.getName();
                                                setupSelectedCityFromViewModel(cityIndices.getName());
                                            }
                                        }
                                    });
                                });
                            }

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
        setupSelectedCityFromViewModel(selectedCity);
    }

    @Override
    protected void onPause() {
        disposables.dispose();
        super.onPause();
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
        if (e == null) {
            return;
        }
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
    }


    private int[] getColors() {

        // have as many colors as stack-values per entry
        int[] colors = new int[3];

        System.arraycopy(ColorTemplate.MATERIAL_COLORS, 0, colors, 0, 3);

        return colors;
    }


    private void showNoDataMessage() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.no_data_error)
                .setMessage(R.string.no_data_error_msg)
                .setNegativeButton(R.string.error_dismiss_button, (dialog, which) -> dialog.dismiss()).create().show();
    }

    private void clearChartData() {
        //Clear chart when comparison data not found
        piechart.clear();
        barChart.clear();
        piechart.invalidate();
        barChart.invalidate();
    }

}


