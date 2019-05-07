package com.appdev.schoudhary.wittylife.ui.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.database.AppDatabase;
import com.appdev.schoudhary.wittylife.model.City;
import com.appdev.schoudhary.wittylife.model.CityIndices;
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
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;

public class ComparisonActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, OnChartValueSelectedListener, TextView.OnEditorActionListener {
    private static final String TAG = ComparisonActivity.class.getSimpleName();
    private ActionBar actionBar;
    private ProgressBar mLoadingIndicator;
    private AutoCompleteTextView autoTextView;

    private MenuItem autoTextItem;

    private String sourceCity;
    private String selectedCity;
    private String currentSelection;

    private static AppDatabase mDB;
    private List<String> cityRecords;

    private PieChart piechart;
    private BarChart barChart;

    private CompositeDisposable disposables = new CompositeDisposable();

    private Typeface tfLight;
    private Typeface tfRegular;

    private Boolean spinnerTouched = false;
    private FirebaseAnalytics mFirebaseAnalytics;


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

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //FIXME Hack to force onCreateOptionMenu() to load spinner data
        invalidateOptionsMenu();

        // Check for existing state after configuration change and restore the layout
        if (savedInstanceState != null) {
            // Restore value of members from saved state
//            cityRecords = savedInstanceState.getStringArrayList("cityRecords");
            selectedCity = savedInstanceState.getString("selectedCity");
            sourceCity = savedInstanceState.getString("sourceCity");
            currentSelection = savedInstanceState.getString("currentSelection");
            /**
             * Updating Chart UI from View Model
             */
            setupSelectedCityFromViewModel(selectedCity);
//            fetchAndUpdateSpinner();
        } else {
            Intent intentFromHome = getIntent();
            if (intentFromHome != null) {
                if (intentFromHome.hasExtra(Intent.EXTRA_TEXT)) {
                    sourceCity = intentFromHome.getStringExtra(Intent.EXTRA_TEXT);
                    /**
                     * Fetching destination ranking from API on destination details loading
                     */
                    fetchAndUpdateIndices(sourceCity);
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
//        outState.putStringArrayList("cityRecords", (ArrayList<String>) cityRecords);
        outState.putString("sourceCity", sourceCity);
        outState.putString("selectedCity", selectedCity);
        outState.putString("currentSelection", currentSelection);

        Log.d(TAG, "Saving cityRecords in bundle during orientation change");

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        cityRecords = savedInstanceState.getStringArrayList("cityRecords");
        //FIXME Use viewmodel to save UI state
        sourceCity = savedInstanceState.getString("sourceCity");
        selectedCity = savedInstanceState.getString("selectedCity");
        currentSelection = savedInstanceState.getString("currentSelection");

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
                runOnUiThread(() -> populateSpinnerData());
            }
        });
    }

    private void populateSpinnerData() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cityRecords);

        autoTextView.setAdapter(adapter);

        if (currentSelection != null) {
            autoTextView.setText(currentSelection);
        }


//        autoTextView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d(TAG, getString(R.string.real_selection));
//                spinnerTouched = true;
//                return false;
//            }
//        });


//        autoTextView.setOnItemSelectedListener(ComparisonActivity.this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //FIXME Update menu icon color to white
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.autocomplete_spinner, menu);
        autoTextItem = menu.findItem(R.id.auto_menu);
        View v = autoTextItem.getActionView();

        autoTextView = v.findViewById(R.id.auto_complete);
        autoTextView.setThreshold(3);

        autoTextView.setTextColor(getResources().getColor(R.color.icons, null));

        autoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoTextView.showDropDown();
            }
        });

        autoTextView.setOnItemClickListener(ComparisonActivity.this);
        autoTextView.setOnEditorActionListener(ComparisonActivity.this);


        /** Setting an action listener */
//        autoTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
//                    Toast.makeText(getBaseContext(), "Search : " + v.getText(), Toast.LENGTH_SHORT).show();
//                }
//                return false;
//            }
//        });

//        autoTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//
//                View autoview = getCurrentFocus();
//                if(autoview != null) {
//                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//
//                    inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                }
//
//                currentSelection = (String) parent.getItemAtPosition(position);
//                String selectedItem = (String) parent.getItemAtPosition(position);
//
//                Bundle bundle = new Bundle();
//                bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, selectedItem);
//                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
//
//                fetchAndUpdateIndices(selectedItem);
//
//            }
//        });

        autoTextItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });

//        //FIXME Hack to avoid NPE on spinner object on config change
        if (autoTextView.getAdapter() == null || autoTextView.getAdapter().getCount() == 0) {
            fetchAndUpdateSpinner();
        }
        return true;
    }


//    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        currentSelection = position;
//        String selectedItem = (String) parent.getSelectedItem();
//
//
//
//        //FIXME Should live in ViewModel, check in Db first then API
//        if (spinnerTouched) {
//
//            Bundle bundle = new Bundle();
//            bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, selectedItem );
//            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
//
////            SearchBottomSheetDialogFragment addPhotoBottomDialogFragment =
////                    SearchBottomSheetDialogFragment.newInstance();
////            addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
////                    "search_dialog_fragment");
//
//            fetchAndUpdateIndices(selectedItem);
//        }
//        spinnerTouched = false;


//        CustomLiveData liveData = new CustomLiveData(mDB.cityIndicesDao().loadCityByName(sourceCity),
//                mDB.cityIndicesDao().loadCityByName(selectedCity));
//
//        LiveData<CityIndices> indices =  Transformations.switchMap(liveData ,
//                cityRecords -> bindChartingView(cityRecords.first, cityRecords.second));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
            currentSelection = v.getText().toString();

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, currentSelection);
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
            fetchAndUpdateIndices(currentSelection);
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        View autoview = getCurrentFocus();
        if(autoview != null) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            inputManager.hideSoftInputFromWindow(autoview.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        currentSelection = (String) parent.getItemAtPosition(position);
        String selectedItem = (String) parent.getItemAtPosition(position);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, selectedItem);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);

        fetchAndUpdateIndices(selectedItem);
    }

    /**
     * Fetch and update DB with city indices data
     *
     * @param cityName
     */
    private void fetchAndUpdateIndices(String cityName) {
        final CityIndicesViewModel viewModel = ViewModelProviders.of(this).get(CityIndicesViewModel.class);

        viewModel.getCityIndex(cityName).observe(this, new android.arch.lifecycle.Observer<CityIndices>() {
            @Override
            public void onChanged(@Nullable CityIndices cityIndices) {
                if (cityIndices != null) {
                    // Skipping any comparison  on activity start
                    if (cityIndices.getName().toLowerCase().contains(sourceCity.toLowerCase())) {
                        sourceCity = cityIndices.getName();
                    } else {
                        selectedCity = cityIndices.getName();
                    }
                } else {
                    Toast.makeText(getBaseContext(), "No result for : " + cityName  , Toast.LENGTH_SHORT).show();

                }
            }
        });
        viewModel.getIsLoading().observe(this, new android.arch.lifecycle.Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean loading) {
                if (loading) {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                } else {
                    mLoadingIndicator.setVisibility(View.GONE);
                    setupSelectedCityFromViewModel(selectedCity);
                }
            }
        });
    }

    private void setupSelectedCityFromViewModel(String currentCity) {
        final CityIndicesViewModel viewModel = ViewModelProviders.of(this).get(CityIndicesViewModel.class);
        viewModel.loadCity(currentCity);

        viewModel.getCityIndices().observe(this, new android.arch.lifecycle.Observer<CityIndices>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onChanged(@Nullable CityIndices selectedCityIndices) {
                if (selectedCityIndices != null && sourceCity != null) {

                    mDB.cityIndicesDao().loadCityByName(sourceCity).observe(ComparisonActivity.this, new android.arch.lifecycle.Observer<CityIndices>() {
                        @Override
                        public void onChanged(@Nullable CityIndices sourceCityIndices) {
                            if (selectedCityIndices.getSafetyIndex() != null
                                    && selectedCityIndices.getClimateIndex() != null
                                    && sourceCityIndices != null
                                    && !selectedCity.equals(sourceCity)) {

                                bindChartingView(sourceCityIndices, selectedCityIndices);

                            } else {
                                clearChartData();
                            }
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
        barChart.getDescription().setText(getString(R.string.bar_chart_description));
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
        Typeface type = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");

        barChart.setNoDataText(getString(R.string.no_barchart_data_message));
        barChart.setNoDataTextColor(R.color.colorAccent);
        barChart.setNoDataTextTypeface(type);
        Paint paint = barChart.getPaint(Chart.PAINT_INFO);
        paint.setTextSize(40f);

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
        piechart.getDescription().setText(getString(R.string.piechart_descripttion));
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

        Typeface type = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        piechart.setNoDataText(getString(R.string.no_piechart_data_message));
        piechart.setNoDataTextColor(R.color.colorAccent);
        piechart.setNoDataTextTypeface(type);
        Paint paint = piechart.getPaint(Chart.PAINT_INFO);
        paint.setTextSize(40f);

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
            set1 = new BarDataSet(values, getString(R.string.bar_chart_description));
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


        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.piechart_descripttion));

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




    //FIXME Implement Repository and network bound resource
    private void fetchAndUpdateSpinner() {

        AppExecutors.getInstance().diskIO().execute(() -> {
            int count = mDB.cityDao().getRowCount();
            if (count > 0) {
                populateSpinnerFromDB(null);
            } else {
                final CityIndicesViewModel viewModel = ViewModelProviders.of(this).get(CityIndicesViewModel.class);
                viewModel.getCityList().observe(this, new android.arch.lifecycle.Observer<List<City>>() {
                    @Override
                    public void onChanged(@Nullable List<City> cityList) {
                        if (cityList != null) {
                            cityList.forEach(city -> cityRecords.add(city.getCity()));
                        }
                        populateSpinnerData();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
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


    private void clearChartData() {
        //Clear chart when comparison data not found
        piechart.clear();
        barChart.clear();
        piechart.invalidate();
        barChart.invalidate();
    }


}


