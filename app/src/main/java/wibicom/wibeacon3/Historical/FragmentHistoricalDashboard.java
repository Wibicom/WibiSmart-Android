package wibicom.wibeacon3.Historical;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.HashMap;

import wibicom.wibeacon3.DataHandler;
import wibicom.wibeacon3.MainActivity;
import wibicom.wibeacon3.R;

/**
 * Created by Michael Vaquier on 2017-07-20.
 * This fragment is the view to the Historical Dashboard, It comports sets of webviews and cardviews.
 * The webviews are used to load the html content in them and run javascript code to display the graphs.
 * The cardvies are used to toggle their visibility depending on what data we have to show.
 */

public class FragmentHistoricalDashboard extends Fragment {


    WebView webViewTemperatureGraph;
    WebView webViewHumidityGraph;
    WebView webViewPressureGraph;
    WebView webViewUVGraph;
    WebView webViewSoundGraph;
    WebView webViewCO2Graph;
    WebView webViewSO2Graph;
    WebView webViewCOGraph;
    WebView webViewO3Graph;
    WebView webViewNO2Graph;
    WebView webViewPMGraph;
    WebView webViewAccelerometerGraph;
    WebView webViewLightGraph;
    WebView webViewBatteryGraph;
    WebView webViewRSSIGraph;

    CardView cardViewTemperatureGraph;
    CardView cardViewHumidityGraph;
    CardView cardViewPressureGraph;
    CardView cardViewUVGraph;
    CardView cardViewSoundGraph;
    CardView cardViewCO2Graph;
    CardView cardViewSO2Graph;
    CardView cardViewCOGraph;
    CardView cardViewO3Graph;
    CardView cardViewNO2Graph;
    CardView cardViewPMGraph;
    CardView cardViewAccelerometerGraph;
    CardView cardViewLightGraph;
    CardView cardViewBatteryGraph;
    CardView cardViewRSSIGraph;

    TextView deviceId;
    TextView inputDate;
    Switch querySwitch;
    TextView queryTag;
    TextView message;
    Button buttonSearch;
    LinearLayout historicalInputs;

    ProgressBar progressBar;

    boolean queryingDate = true;

    private final static String TAG = FragmentHistoricalDashboard.class.getName();

    public FragmentHistoricalDashboard() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historical_data, container, false);

        buttonSearch = (Button)view.findViewById(R.id.querryDatabase);
        historicalInputs = (LinearLayout) view.findViewById(R.id.historical_inputs);


        webViewTemperatureGraph = (WebView) view.findViewById(R.id.webviewTemperatureGraph);
        webViewHumidityGraph = (WebView) view.findViewById(R.id.webviewHumidityGraph);
        webViewPressureGraph = (WebView) view.findViewById(R.id.webviewPressureGraph);
        webViewUVGraph = (WebView) view.findViewById(R.id.webviewUVGraph);
        webViewSoundGraph = (WebView) view.findViewById(R.id.webviewSoundGraph);
        webViewCO2Graph = (WebView) view.findViewById(R.id.webviewCO2Graph);
        webViewSO2Graph = (WebView) view.findViewById(R.id.webviewSO2Graph);
        webViewCOGraph = (WebView) view.findViewById(R.id.webviewCOGraph);
        webViewO3Graph = (WebView) view.findViewById(R.id.webviewO3Graph);
        webViewNO2Graph = (WebView) view.findViewById(R.id.webviewNO2Graph);
        webViewPMGraph = (WebView) view.findViewById(R.id.webviewPMGraph);
        webViewAccelerometerGraph = (WebView) view.findViewById(R.id.webviewAccelerometerGraph);
        webViewLightGraph = (WebView) view.findViewById(R.id.webviewLightGraph);
        webViewBatteryGraph = (WebView) view.findViewById(R.id.webviewBatteryGraph);
        webViewRSSIGraph = (WebView) view.findViewById(R.id.webviewRSSIGraph);

        // Enable JavaScript
        webViewTemperatureGraph.getSettings().setJavaScriptEnabled(true);
        webViewHumidityGraph.getSettings().setJavaScriptEnabled(true);
        webViewPressureGraph.getSettings().setJavaScriptEnabled(true);
        webViewUVGraph.getSettings().setJavaScriptEnabled(true);
        webViewSoundGraph.getSettings().setJavaScriptEnabled(true);
        webViewCO2Graph.getSettings().setJavaScriptEnabled(true);
        webViewSO2Graph.getSettings().setJavaScriptEnabled(true);
        webViewCOGraph.getSettings().setJavaScriptEnabled(true);
        webViewO3Graph.getSettings().setJavaScriptEnabled(true);
        webViewNO2Graph.getSettings().setJavaScriptEnabled(true);
        webViewPMGraph.getSettings().setJavaScriptEnabled(true);
        webViewAccelerometerGraph.getSettings().setJavaScriptEnabled(true);
        webViewLightGraph.getSettings().setJavaScriptEnabled(true);
        webViewBatteryGraph.getSettings().setJavaScriptEnabled(true);
        webViewRSSIGraph.getSettings().setJavaScriptEnabled(true);


        // Load the html file
        webViewTemperatureGraph.loadUrl("file:///android_asset/historical-chart.html");
        webViewHumidityGraph.loadUrl("file:///android_asset/historical-chart.html");
        webViewPressureGraph.loadUrl("file:///android_asset/historical-chart.html");
        webViewUVGraph.loadUrl("file:///android_asset/historical-chart.html");
        webViewSoundGraph.loadUrl("file:///android_asset/historical-chart.html");
        webViewCO2Graph.loadUrl("file:///android_asset/historical-chart.html");
        webViewSO2Graph.loadUrl("file:///android_asset/historical-chart.html");
        webViewCOGraph.loadUrl("file:///android_asset/historical-chart.html");
        webViewO3Graph.loadUrl("file:///android_asset/historical-chart.html");
        webViewNO2Graph.loadUrl("file:///android_asset/historical-chart.html");
        webViewPMGraph.loadUrl("file:///android_asset/historical-chart.html");
        webViewAccelerometerGraph.loadUrl("file:///android_asset/historical-chart.html");
        webViewLightGraph.loadUrl("file:///android_asset/historical-chart.html");
        webViewBatteryGraph.loadUrl("file:///android_asset/historical-chart.html");
        webViewRSSIGraph.loadUrl("file:///android_asset/historical-chart.html");


        cardViewTemperatureGraph = (CardView) view.findViewById(R.id.cardviewTemperatureGraph);
        cardViewHumidityGraph = (CardView) view.findViewById(R.id.cardviewHumidityGraph);
        cardViewPressureGraph = (CardView) view.findViewById(R.id.cardviewPressureGraph);
        cardViewUVGraph = (CardView) view.findViewById(R.id.cardviewUVGraph);
        cardViewSoundGraph = (CardView) view.findViewById(R.id.cardviewSoundGraph);
        cardViewAccelerometerGraph = (CardView) view.findViewById(R.id.cardviewAccelerometerGraph);
        cardViewCO2Graph = (CardView) view.findViewById(R.id.cardviewCO2Graph);
        cardViewSO2Graph = (CardView) view.findViewById(R.id.cardviewSO2Graph);
        cardViewCOGraph = (CardView) view.findViewById(R.id.cardviewCOGraph);
        cardViewO3Graph = (CardView) view.findViewById(R.id.cardviewO3Graph);
        cardViewNO2Graph = (CardView) view.findViewById(R.id.cardviewNO2Graph);
        cardViewPMGraph = (CardView) view.findViewById(R.id.cardviewPMGraph);
        cardViewLightGraph = (CardView) view.findViewById(R.id.cardviewLightGraph);
        cardViewBatteryGraph = (CardView) view.findViewById(R.id.cardviewBatteryGraph);
        cardViewRSSIGraph = (CardView) view.findViewById(R.id.cardviewRSSIGraph);

        deviceId = (TextView) view.findViewById(R.id.deviceIdInput);
        inputDate = (TextView) view.findViewById(R.id.dateInput);
        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(queryingDate) {
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getFragmentManager(), "datePicker");
                }
            }
        });

        progressBar = (ProgressBar) view.findViewById(R.id.dataProgress);
        progressBar.setMax(100);
        progressBar.setVisibility(View.GONE);

        message = (TextView) view.findViewById(R.id.historical_dashboard_message);


        queryTag = (TextView) view.findViewById(R.id.queryMode);
        querySwitch = (Switch) view.findViewById(R.id.querySwitch);
        querySwitch.setChecked(true);
        querySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryingDate = querySwitch.isChecked();
                inputDate.setText("");
                if(queryingDate) {
                    queryTag.setText("Query Date");
                    inputDate.setInputType(InputType.TYPE_CLASS_DATETIME);
                    inputDate.setHint("yyyy/mm/dd");
                    message.setText("Input a device ID and Date to see your data");
                }
                else {
                    queryTag.setText("Query Database");
                    inputDate.setInputType(InputType.TYPE_CLASS_TEXT);
                    inputDate.setHint("Database");
                    message.setText("Input a device ID and database to see your data");
                }
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGraph();
            }
        });
        return view;
    }

    public void hideInputs(boolean b) {
        if(historicalInputs != null) {
            if (b) {
                historicalInputs.setVisibility(View.GONE);

            }
            else {
                historicalInputs.setVisibility(View.VISIBLE);
            }
        }
    }

    public void dateSelected(String date) {
        if(inputDate != null) {
            inputDate.setText(date);
        }
    }

    public void updateGraph() {
        cardViewTemperatureGraph.setVisibility(View.GONE);
        cardViewHumidityGraph.setVisibility(View.GONE);
        cardViewPressureGraph.setVisibility(View.GONE);
        cardViewUVGraph.setVisibility(View.GONE);
        cardViewSoundGraph.setVisibility(View.GONE);
        cardViewAccelerometerGraph.setVisibility(View.GONE);
        cardViewCO2Graph.setVisibility(View.GONE);
        cardViewSO2Graph.setVisibility(View.GONE);
        cardViewCOGraph.setVisibility(View.GONE);
        cardViewO3Graph.setVisibility(View.GONE);
        cardViewNO2Graph.setVisibility(View.GONE);
        cardViewPMGraph.setVisibility(View.GONE);
        cardViewLightGraph.setVisibility(View.GONE);
        cardViewBatteryGraph.setVisibility(View.GONE);
        cardViewRSSIGraph.setVisibility(View.GONE);
        String id = deviceId.getText().toString();
        String date = inputDate.getText().toString();
        if(id.length() > 0 && date.length() > 0) {
            if(id.length() == 12) {
                if(queryingDate) {
                    if(date.split("/").length == 3) {
                        String[] data = new String[3];
                        data[0] = "date";
                        data[1] = id;
                        data[2] = date.replaceAll("/", "-");
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.VISIBLE);
                        message.setVisibility(View.GONE);
                        DataHandler.getInstance().requestData(data);
                    }
                    else {
                        message.setText("Input date is not valid...");
                        message.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    String[] data = new String[3];
                    data[0] = "bulk";
                    data[1] = id;
                    data[2] = date;
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.VISIBLE);
                    message.setVisibility(View.GONE);
                    DataHandler.getInstance().requestData(data);
                }
            }
            else {
                message.setText("This is not a valid device ID...");
                message.setVisibility(View.VISIBLE);
            }
        }
        else {
            message.setText("One of the inputs is missing...");
            message.setVisibility(View.VISIBLE);
        }
    }

    public void renderGraphs(HashMap<String,String> csvMap) {
        cardViewTemperatureGraph.setVisibility(View.GONE);
        cardViewHumidityGraph.setVisibility(View.GONE);
        cardViewPressureGraph.setVisibility(View.GONE);
        cardViewAccelerometerGraph.setVisibility(View.GONE);
        cardViewCO2Graph.setVisibility(View.GONE);

        cardViewLightGraph.setVisibility(View.GONE);
        cardViewBatteryGraph.setVisibility(View.GONE);
        cardViewRSSIGraph.setVisibility(View.GONE);

        Log.d(TAG, "Rendering graphs...");
        String csv = csvMap.get("temperature");
        if(!csv.equals("")) {
            csv = "Timestamp,Temperature\\n"+csv;
            webViewTemperatureGraph.loadUrl("javascript:render_graph('Temperature (°C)', '" + csv + "')");
            cardViewTemperatureGraph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("humidity");
        if(!csv.equals("")) {
            csv = "Timestamp,Humidity\\n"+csv;
            webViewHumidityGraph.loadUrl("javascript:render_graph('Humidity (%)', '" + csv + "')");
            cardViewHumidityGraph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("pressure");
        if(!csv.equals("")) {
            csv = "Timestamp,Pressure\\n"+csv;
            webViewPressureGraph.loadUrl("javascript:render_graph('Pressure (mbar)', '" + csv + "')");
            cardViewPressureGraph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("accel");
        if(!csv.equals("")) {
            csv = "Timestamp,x,y,z\\n"+csv;
            webViewAccelerometerGraph.loadUrl("javascript:render_graph('Acceleration (mg)', '" + csv + "')");
            cardViewAccelerometerGraph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("CO2");
        if(!csv.equals("")) {
            csv = "Timestamp,CO2\\n"+csv;
            webViewCO2Graph.loadUrl("javascript:render_graph('CO2 (ppm)', '" + csv + "')");
            cardViewCO2Graph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("rssi");
        if(!csv.equals("")) {
            csv = "Timestamp,RSSI\\n"+csv;
            webViewRSSIGraph.loadUrl("javascript:render_graph('RSSI (dBm)', '" + csv + "')");
            cardViewRSSIGraph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("light");
        if(!csv.equals("")) {
            csv = "Timestamp,Light\\n"+csv;
            webViewLightGraph.loadUrl("javascript:render_graph('Light (mV)', '" + csv + "')");
            cardViewLightGraph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("battery");
        if(!csv.equals("")) {
            csv = "Timestamp,Battery\\n"+csv;
            webViewBatteryGraph.loadUrl("javascript:render_graph('Battery (%)', '" + csv + "')");
            cardViewBatteryGraph.setVisibility(View.VISIBLE);
        }
    }

    public void renderWeatherCO2(HashMap<String,String> csvMap) {
        cardViewTemperatureGraph.setVisibility(View.GONE);
        cardViewHumidityGraph.setVisibility(View.GONE);
        cardViewPressureGraph.setVisibility(View.GONE);
        cardViewCO2Graph.setVisibility(View.GONE);

        Log.d(TAG, "Rendering weather graphs...");
        String csv = csvMap.get("temperature");
        if(!csv.equals("")) {
            csv = "Timestamp,Temperature\\n"+csv;
            webViewTemperatureGraph.loadUrl("javascript:render_graph('Temperature (°C)', '" + csv + "')");
            cardViewTemperatureGraph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("humidity");
        if(!csv.equals("")) {
            csv = "Timestamp,Humidity\\n"+csv;
            webViewHumidityGraph.loadUrl("javascript:render_graph('Humidity (%)', '" + csv + "')");
            cardViewHumidityGraph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("pressure");
        if(!csv.equals("")) {
            csv = "Timestamp,Pressure\\n"+csv;
            webViewPressureGraph.loadUrl("javascript:render_graph('Pressure (mbar)', '" + csv + "')");
            cardViewPressureGraph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("UV");
        if(!csv.equals("")) {
            csv = "Timestamp,UV\\n"+csv;
            webViewUVGraph.loadUrl("javascript:render_graph('UV index', '" + csv + "')");
            cardViewUVGraph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("CO2");
        if(!csv.equals("")) {
            csv = "Timestamp,CO2\\n"+csv;
            webViewCO2Graph.loadUrl("javascript:render_graph('CO2 (ppm)', '" + csv + "')");
            cardViewCO2Graph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("sound");
        if(!csv.equals("")) {
            csv = "Timestamp,soundLevel\\n"+csv;
            webViewSoundGraph.loadUrl("javascript:render_graph('Sound Level (dB)', '" + csv + "')");
            cardViewSoundGraph.setVisibility(View.VISIBLE);
        }
    }

    public void renderLightBatteryGases(HashMap<String,String> csvMap) {
        cardViewLightGraph.setVisibility(View.GONE);
        cardViewBatteryGraph.setVisibility(View.GONE);
        cardViewSO2Graph.setVisibility(View.GONE);
        cardViewCOGraph.setVisibility(View.GONE);
        cardViewO3Graph.setVisibility(View.GONE);
        cardViewNO2Graph.setVisibility(View.GONE);
        cardViewPMGraph.setVisibility(View.GONE);
        Log.d(TAG, "Rendering battery/light graphs...");
        String csv;
        csv = csvMap.get("light");
        if(!csv.equals("")) {
            csv = "Timestamp,Light\\n"+csv;
            webViewLightGraph.loadUrl("javascript:render_graph('Light (mV)', '" + csv + "')");
            cardViewLightGraph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("battery");
        if(!csv.equals("")) {
            csv = "Timestamp,Battery\\n"+csv;
            webViewBatteryGraph.loadUrl("javascript:render_graph('Battery (%)', '" + csv + "')");
            cardViewBatteryGraph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("SO2");
        if(!csv.equals("")) {
            csv = "Timestamp,SO2\\n"+csv;
            webViewSO2Graph.loadUrl("javascript:render_graph('SO<sub>2</sub> (ppm)', '" + csv + "')");
            cardViewSO2Graph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("CO");
        if(!csv.equals("")) {
            csv = "Timestamp,CO\\n"+csv;
            webViewCOGraph.loadUrl("javascript:render_graph('CO (ppm)', '" + csv + "')");
            cardViewCOGraph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("O3");
        if(!csv.equals("")) {
            csv = "Timestamp,O3\\n"+csv;
            webViewO3Graph.loadUrl("javascript:render_graph('O<sub>3</sub> (ppm)', '" + csv + "')");
            cardViewO3Graph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("NO2");
        if(!csv.equals("")) {
            csv = "Timestamp,NO2\\n"+csv;
            webViewNO2Graph.loadUrl("javascript:render_graph('NO<sub>2</sub> (ppm)', '" + csv + "')");
            cardViewNO2Graph.setVisibility(View.VISIBLE);
        }
        csv = csvMap.get("PM");
        if(!csv.equals("")) {
            csv = "Timestamp,PM\\n"+csv;
            webViewPMGraph.loadUrl("javascript:render_graph('PM<sub>2.5</sub> (μg/m<sup>3</sup>)', '" + csv + "')");
            cardViewPMGraph.setVisibility(View.VISIBLE);
        }
    }

    public void renderRSSI(HashMap<String,String> csvMap) {
        cardViewRSSIGraph.setVisibility(View.GONE);
        Log.d(TAG, "Rendering RSSI graphs...");
        String csv;
        csv = csvMap.get("rssi");
        if(!csv.equals("")) {
            csv = "Timestamp,RSSI\\n"+csv;
            webViewRSSIGraph.loadUrl("javascript:render_graph('RSSI (dBm)', '" + csv + "')");
            cardViewRSSIGraph.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
        message.setText("Query completed!");
        message.setVisibility(View.VISIBLE);
    }

    public void renderAccel(HashMap<String,String> csvMap) {
        cardViewAccelerometerGraph.setVisibility(View.GONE);
        Log.d(TAG, "Rendering accel graphs...");
        String csv;
        csv = csvMap.get("accel");
        if(!csv.equals("")) {
            csv = "Timestamp,x,y,z\\n"+csv;
            webViewAccelerometerGraph.loadUrl("javascript:render_graph('Acceleration (mg)', '" + csv + "')");
            cardViewAccelerometerGraph.setVisibility(View.VISIBLE);
        }
    }

    public ProgressBar getProgressBar() { return progressBar; }



}
