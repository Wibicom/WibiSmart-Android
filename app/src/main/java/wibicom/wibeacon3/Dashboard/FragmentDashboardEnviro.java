package wibicom.wibeacon3.Dashboard;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import wibicom.wibeacon3.R;


public class FragmentDashboardEnviro extends Fragment {

    TextView batteryLevelText;
    TextView temperatureText;
    TextView humidityText;
    TextView pressureText;
    TextView accelerometerTextX;
    TextView accelerometerTextY;
    TextView accelerometerTextZ;
    TextView rssiText;
    TextView lightText;

    int batteryLevel;

    WebView webViewTemperature;
    WebView webViewHumidity;
    WebView webViewPressure;

    WebView webViewAccelerometer;



    public FragmentDashboardEnviro() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_enviro, container, false);
        temperatureText = (TextView) view.findViewById(R.id.weather_ti);
        humidityText = (TextView) view.findViewById(R.id.humidity_ti);
        pressureText = (TextView) view.findViewById(R.id.pressure_ti);
        batteryLevelText = (TextView) view.findViewById(R.id.battery_level_ti);
        accelerometerTextX = (TextView) view.findViewById(R.id.accelerometer_x_ti);
        accelerometerTextY = (TextView) view.findViewById(R.id.accelerometer_y_ti);
        accelerometerTextZ = (TextView) view.findViewById(R.id.accelerometer_z_ti);
        rssiText = (TextView) view.findViewById(R.id.rssi_enviro);
        lightText = (TextView) view.findViewById(R.id.light_enviro);

        batteryLevelText.setText(Integer.toString(batteryLevel) + " %");

        webViewTemperature = (WebView) view.findViewById(R.id.webviewTemperature);
        webViewHumidity = (WebView) view.findViewById(R.id.webviewHumidity);
        webViewPressure = (WebView) view.findViewById(R.id.webviewPressure);
        webViewAccelerometer = (WebView) view.findViewById(R.id.webviewAccelerometer);


        // Enable JavaScript
        webViewTemperature.getSettings().setJavaScriptEnabled(true);
        webViewHumidity.getSettings().setJavaScriptEnabled(true);
        webViewPressure.getSettings().setJavaScriptEnabled(true);
        webViewAccelerometer.getSettings().setJavaScriptEnabled(true);

        // Load the html file
        webViewTemperature.loadUrl("file:///android_asset/temperature_widget.html");
        webViewHumidity.loadUrl("file:///android_asset/humidity_widget.html");
        webViewPressure.loadUrl("file:///android_asset/pressure_widget.html");
        webViewAccelerometer.loadUrl("file:///android_asset/accelerometer_widget.html");



        return view;
    }

    public void updateData(float temperature, float pressure, float humidity, float accelerometerX, float accelerometerY, float accelerometerZ, int batteryLevel, int rssi, int light)
    {
        //webViewTemperature.loadUrl("file:///android_asset/temperature_widget.html");
        //webViewTemperature.loadUrl("javascript:updateTemperature()");
        if(webViewTemperature != null)
            webViewTemperature.loadUrl("javascript:updateTemperature(" + Float.toString(temperature) + ")");
        if(webViewHumidity != null)
            webViewHumidity.loadUrl("javascript:updateHumidity(" + Float.toString(humidity) + ")");
        if(webViewPressure != null)
            webViewPressure.loadUrl("javascript:updatePressure(" + Float.toString(pressure) + ")");

        if(webViewAccelerometer != null)
            webViewAccelerometer.loadUrl("javascript:set_accelerometer_data(" + Float.toString(accelerometerX) + ", " + Float.toString(accelerometerY) + ", " + Float.toString(accelerometerZ) + ")");

        if(batteryLevelText != null)
            batteryLevelText.setText(Integer.toString(batteryLevel) + " %");
        else
            this.batteryLevel = batteryLevel;

        if(rssiText != null)
        {
            String text;
            if(rssi > -65)
                text = "Near";
            else if(rssi > -80)
                text = "Mid range";
            else
                text = "Far";
            rssiText.setText(text);
        }

        if(lightText != null)
            lightText.setText(Integer.toString(light));

        if(temperatureText != null)
            temperatureText.setText(Float.toString(temperature)+ " °C");

        if(humidityText != null)
            humidityText.setText(Float.toString(humidity) + " %");

        if(pressureText != null)
            pressureText.setText(Float.toString(pressure) + " mBar");

        if(accelerometerTextX != null)
            accelerometerTextX.setText(Float.toString(accelerometerX) + " mg");

        if(accelerometerTextY != null)
            accelerometerTextY.setText(Float.toString(accelerometerY) + " mg");

        if(accelerometerTextZ != null)
            accelerometerTextZ.setText(Float.toString(accelerometerZ) + " mg");


    }

}
