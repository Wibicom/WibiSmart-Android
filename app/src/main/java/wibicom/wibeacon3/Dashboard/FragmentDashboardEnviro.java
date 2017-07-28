package wibicom.wibeacon3.Dashboard;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.app.Fragment;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import wibicom.wibeacon3.BluetoothLeService;
import wibicom.wibeacon3.MainActivity;
import wibicom.wibeacon3.R;
import wibicom.wibeacon3.SensorData;
import wibicom.wibeacon3.WibiSmartGatt;


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
    WebView webViewCO2;
    WebView webViewAccelerometer;
    WebView webViewGeneralInfo;

    CardView cardViewTemperature;
    CardView cardViewHumidity;
    CardView cardViewPressure;
    CardView cardViewCO2;
    CardView cardViewAccelerometer;

    private final static String TAG = FragmentDashboardEnviro.class.getName();

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
//        temperatureText = (TextView) view.findViewById(R.id.weather_ti);
//        humidityText = (TextView) view.findViewById(R.id.humidity_ti);
//        pressureText = (TextView) view.findViewById(R.id.pressure_ti);
//        batteryLevelText = (TextView) view.findViewById(R.id.battery_level_ti);
//        accelerometerTextX = (TextView) view.findViewById(R.id.accelerometer_x_ti);
//        accelerometerTextY = (TextView) view.findViewById(R.id.accelerometer_y_ti);
//        accelerometerTextZ = (TextView) view.findViewById(R.id.accelerometer_z_ti);
//        rssiText = (TextView) view.findViewById(R.id.rssi_enviro);
//        lightText = (TextView) view.findViewById(R.id.light_enviro);



        webViewTemperature = (WebView) view.findViewById(R.id.webviewTemperature);
        webViewHumidity = (WebView) view.findViewById(R.id.webviewHumidity);
        webViewPressure = (WebView) view.findViewById(R.id.webviewPressure);
        webViewCO2 = (WebView) view.findViewById(R.id.webviewCO2);
        webViewAccelerometer = (WebView) view.findViewById(R.id.webviewAccelerometer);
        webViewGeneralInfo = (WebView) view.findViewById(R.id.webview_general_info);

        // Enable JavaScript
        webViewTemperature.getSettings().setJavaScriptEnabled(true);
        webViewHumidity.getSettings().setJavaScriptEnabled(true);
        webViewPressure.getSettings().setJavaScriptEnabled(true);
        webViewCO2.getSettings().setJavaScriptEnabled(true);
        webViewAccelerometer.getSettings().setJavaScriptEnabled(true);
        webViewGeneralInfo.getSettings().setJavaScriptEnabled(true);

        // Load the html file
        webViewTemperature.loadUrl("file:///android_asset/temperature_widget.html");
        webViewHumidity.loadUrl("file:///android_asset/humidity_widget.html");
        webViewPressure.loadUrl("file:///android_asset/pressure_widget.html");
        webViewCO2.loadUrl("file:///android_asset/CO2_widget.html");
        webViewAccelerometer.loadUrl("file:///android_asset/accelerometer_widget.html");
        webViewGeneralInfo.loadUrl("file:///android_asset/enviro_general_info.html");

        // Display battery level if it was not created when updateData function was called
        //batteryLevelText.setText(Integer.toString(batteryLevel) + " %");
        webViewGeneralInfo.loadUrl("javascript:set_battery(" + Integer.toString(batteryLevel) + ")");

        cardViewTemperature = (CardView) view.findViewById(R.id.cardviewTemperature);
        cardViewHumidity = (CardView) view.findViewById(R.id.cardviewHumidity);
        cardViewPressure = (CardView) view.findViewById(R.id.cardviewPressure);
        cardViewAccelerometer = (CardView) view.findViewById(R.id.cardviewAccelerometer);
        cardViewCO2 = (CardView) view.findViewById(R.id.cardviewCO2);

        hideSensors();

        return view;
    }

    public void updateData(String name, float temperature, float pressure, float humidity, float accelerometerX, float accelerometerY, float accelerometerZ, int batteryLevel, int rssi, int light, int CO2)
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

        if(webViewGeneralInfo != null) {
            webViewGeneralInfo.loadUrl("javascript:set_battery(" + Integer.toString(batteryLevel) + ")");
            webViewGeneralInfo.loadUrl("javascript:set_rssi(" + Integer.toString(rssi) + ")");
            webViewGeneralInfo.loadUrl("javascript:set_light(" + light + ")");
            webViewGeneralInfo.loadUrl("javascript:set_sensor_name('" + name + "')");
        }
        if(webViewCO2 != null) {
            webViewCO2.loadUrl("javascript:updateCO2(" + Integer.toString(CO2) + ")");
        }
        else
            this.batteryLevel = batteryLevel;
//        if(batteryLevelText != null) {
//            batteryLevelText.setText(Integer.toString(batteryLevel) + " %");
//
//        }
//        else
//            this.batteryLevel = batteryLevel;

//        if(rssiText != null)
//        {
//            String text;
//            if(rssi > -65)
//                text = "Near";
//            else if(rssi > -80)
//                text = "Mid range";
//            else
//                text = "Far";
//            rssiText.setText(text);
//
//        }
//
//        if(lightText != null) {
//            lightText.setText(Integer.toString(light));
//
//        }

//        if(temperatureText != null)
//            temperatureText.setText(Float.toString(temperature)+ " °C");
//
//        if(humidityText != null)
//            humidityText.setText(Float.toString(humidity) + " %");
//
//        if(pressureText != null)
//            pressureText.setText(Float.toString(pressure) + " mBar");
//
//        if(accelerometerTextX != null)
//            accelerometerTextX.setText(Float.toString(accelerometerX) + " mg");
//
//        if(accelerometerTextY != null)
//            accelerometerTextY.setText(Float.toString(accelerometerY) + " mg");
//
//        if(accelerometerTextZ != null)
//            accelerometerTextZ.setText(Float.toString(accelerometerZ) + " mg");


    }

    public void hideSensors()
    {
        Log.d(TAG, "entering .hideSensors()");
        SensorData sensor = MainActivity.getInstance().getSensorDataList().get(MainActivity.getInstance().getConnectedDevicePosition());
        if (sensor != null && sensor.getConnecteddeviceGatt() != null) {
            ArrayList<BluetoothGattService> gattList = new ArrayList<>(MainActivity.getInstance().getSensorDataList().get(MainActivity.getInstance().getConnectedDevicePosition()).getConnecteddeviceGatt().getServices());
            boolean weatherSevice = false;
            boolean CO2Service = false;
            boolean accelerometerService = false;
            boolean gasesService = false;
            if (cardViewCO2 != null) {
                cardViewCO2.setVisibility(View.VISIBLE);
                cardViewTemperature.setVisibility(View.VISIBLE);
                cardViewHumidity.setVisibility(View.VISIBLE);
                cardViewPressure.setVisibility(View.VISIBLE);
                cardViewAccelerometer.setVisibility(View.VISIBLE);
            }

            WibiSmartGatt gatt = WibiSmartGatt.getInstance();
            for (int i = 0; i < gattList.size(); i++) {
                BluetoothGattService tempService = gattList.get(i);
                if (tempService.getUuid().toString().equals(gatt.CO2_SERVICE_UUID_ENVIRO.toString())) {
                    CO2Service = true;
                    sensor.setHasCO2Sensor(true);
                } else if (tempService.getUuid().toString().equals(gatt.WEATHER_SERVICE_UUID_ENVIRO.toString())) {
                    weatherSevice = true;
                    sensor.setHasWeatherSensor(true);
                } else if (tempService.getUuid().toString().equals(gatt.ACCELEROMETER_SERVICE_UUID_ENVIRO.toString())) {
                    accelerometerService = true;
                    sensor.setHasAccelSensor(true);
                } else if (tempService.getUuid().toString().equals(gatt.LIGHT_SERVICE_UUID_ENVIRO.toString())) {
                    sensor.setHasLightSensor(true);
                } else if (tempService.getUuid().toString().equals(gatt.SPEC_SERVICE_UUID_ENVIRO.toString())) {
                    sensor.setHasGasesSensor(true);
                    gasesService = true;
                }

            }

            Log.d(TAG, ".hideSensors() Results for device " + MainActivity.getInstance().getSensorDataList().get(MainActivity.getInstance().getConnectedDevicePosition()).getLocalName() + ": { accel: " + accelerometerService + ", weather:" + weatherSevice +", gases:" + gasesService + ", CO2:" + CO2Service + "}");

            if (!CO2Service && cardViewCO2 != null) {
                cardViewCO2.setVisibility(View.GONE);
            }
            if (!weatherSevice && cardViewTemperature != null && cardViewHumidity != null && cardViewPressure != null) {
                cardViewTemperature.setVisibility(View.GONE);
                cardViewHumidity.setVisibility(View.GONE);
                cardViewPressure.setVisibility(View.GONE);
            }
            if (!accelerometerService && cardViewAccelerometer != null) {
                cardViewAccelerometer.setVisibility(View.GONE);
            }

        }
    }

}
