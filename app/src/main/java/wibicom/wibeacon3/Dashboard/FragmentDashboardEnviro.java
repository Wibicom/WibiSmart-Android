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
import android.view.MotionEvent;
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

/**
 * @author Michael Vaquier
 * This fragment is the view to the Live Dashboard for enviro devices
 * it is updated whenever we reciecve a notificeation from one of the services from the selected device.
 */

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
    WebView webViewUV;
    WebView webViewUVlabel;
    WebView webViewCO2;
    WebView webViewSO2;
    WebView webViewCO;
    WebView webViewO3;
    WebView webViewNO2;
    WebView webViewPM;
    WebView webViewSound;
    WebView webViewAccelerometer;
    WebView webViewGeneralInfo;

    CardView cardViewTemperature;
    CardView cardViewHumidity;
    CardView cardViewPressure;
    CardView cardViewUV;
    CardView cardViewSound;
    CardView cardViewGases;
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
        View view = inflater.inflate(R.layout.fragment_dashboard_enviro_min, container, false);
//        temperatureText = (TextView) view.findViewById(R.id.weather_ti);
//        humidityText = (TextView) view.findViewById(R.id.humidity_ti);
//        pressureText = (TextView) view.findViewById(R.id.pressure_ti);
//        batteryLevelText = (TextView) view.findViewById(R.id.battery_level_ti);
//        accelerometerTextX = (TextView) view.findViewById(R.id.accelerometer_x_ti);
//        accelerometerTextY = (TextView) view.findViewById(R.id.accelerometer_y_ti);
//        accelerometerTextZ = (TextView) view.findViewById(R.id.accelerometer_z_ti);
//        rssiText = (TextView) view.findViewById(R.id.rssi_enviro);
//        lightText = (TextView) view.findViewById(R.id.light_enviro);
        WebView gasImmage = (WebView) view.findViewById(R.id.webviewGasImmage);
        gasImmage.loadUrl("file:///android_asset/gasImmage.html");


        webViewTemperature = (WebView) view.findViewById(R.id.webviewTemperature);
        webViewTemperature.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        webViewHumidity = (WebView) view.findViewById(R.id.webviewHumidity);
        webViewPressure = (WebView) view.findViewById(R.id.webviewPressure);
        webViewUV = (WebView) view.findViewById(R.id.webviewUV);
        webViewUVlabel = (WebView) view.findViewById(R.id.webviewUVlabel);
        webViewCO2 = (WebView) view.findViewById(R.id.webviewCO2);
        webViewSO2 = (WebView) view.findViewById(R.id.webviewSO2);
        webViewCO = (WebView) view.findViewById(R.id.webviewCO);
        webViewO3 = (WebView) view.findViewById(R.id.webviewO3);
        webViewNO2 = (WebView) view.findViewById(R.id.webviewNO2);
        webViewPM = (WebView) view.findViewById(R.id.webviewPM);
        webViewSound = (WebView) view.findViewById(R.id.webviewSound);
        webViewAccelerometer = (WebView) view.findViewById(R.id.webviewAccelerometer);
        webViewGeneralInfo = (WebView) view.findViewById(R.id.webview_general_info);
        webViewGeneralInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        // Enable JavaScript
        webViewTemperature.getSettings().setJavaScriptEnabled(true);
        webViewHumidity.getSettings().setJavaScriptEnabled(true);
        webViewPressure.getSettings().setJavaScriptEnabled(true);
        webViewUV.getSettings().setJavaScriptEnabled(true);
        webViewUVlabel.getSettings().setJavaScriptEnabled(true);
        webViewCO2.getSettings().setJavaScriptEnabled(true);
        webViewSO2.getSettings().setJavaScriptEnabled(true);
        webViewCO.getSettings().setJavaScriptEnabled(true);
        webViewO3.getSettings().setJavaScriptEnabled(true);
        webViewNO2.getSettings().setJavaScriptEnabled(true);
        webViewPM.getSettings().setJavaScriptEnabled(true);
        webViewSound.getSettings().setJavaScriptEnabled(true);
        webViewAccelerometer.getSettings().setJavaScriptEnabled(true);
        webViewGeneralInfo.getSettings().setJavaScriptEnabled(true);

        // Load the html file
        webViewGeneralInfo.loadUrl("file:///android_asset/enviro_general_info.html");
        webViewTemperature.loadUrl("file:///android_asset/temperature_widget_min.html");
        webViewHumidity.loadUrl("file:///android_asset/humidity_widget_min.html");
        webViewPressure.loadUrl("file:///android_asset/pressure_widget_min.html");
        webViewUV.loadUrl("file:///android_asset/UV_widget_min.html");
        webViewUVlabel.loadUrl("file:///android_asset/UVLabel.html");
        webViewCO2.loadUrl("file:///android_asset/CO2_widget_min.html");
        webViewSO2.loadUrl("file:///android_asset/SO2_widget_min.html");
        webViewCO.loadUrl("file:///android_asset/CO_widget_min.html");
        webViewO3.loadUrl("file:///android_asset/O3_widget_min.html");
        webViewNO2.loadUrl("file:///android_asset/NO2_widget_min.html");
        webViewPM.loadUrl("file:///android_asset/PM_widget_min.html");
        webViewSound.loadUrl("file:///android_asset/sound_widget.html");

        webViewAccelerometer.loadUrl("file:///android_asset/accelerometer_widget_min.html");


        // Display battery level if it was not created when updateData function was called
        //batteryLevelText.setText(Integer.toString(batteryLevel) + " %");
        webViewGeneralInfo.loadUrl("javascript:set_battery(" + Integer.toString(batteryLevel) + ")");

        cardViewTemperature = (CardView) view.findViewById(R.id.cardviewTemperature);
        cardViewHumidity = (CardView) view.findViewById(R.id.cardviewHumidity);
        cardViewPressure = (CardView) view.findViewById(R.id.cardviewPressure);
        cardViewUV = (CardView) view.findViewById(R.id.cardviewUV);
        cardViewSound = (CardView) view.findViewById(R.id.cardviewSound);
        cardViewAccelerometer = (CardView) view.findViewById(R.id.cardviewAccelerometer);
        cardViewGases = (CardView) view.findViewById(R.id.cardviewGases);

        hideSensors();

        return view;
    }

    //This function is called from the main activity when there is a notification from the selected device.
    public void updateData(String name, float temperature, float pressure, float humidity, int UV, float accelerometerX, float accelerometerY, float accelerometerZ, int batteryLevel, int rssi, int light, int CO2, float SO2, float CO, float O3, float NO2, float PM, int sound)
    {
        //webViewTemperature.loadUrl("file:///android_asset/temperature_widget.html");
        //webViewTemperature.loadUrl("javascript:updateTemperature()");
        if(webViewTemperature != null)
            webViewTemperature.loadUrl("javascript:updateTemperature(" + Float.toString(temperature) + ")");
        if(webViewHumidity != null)
            webViewHumidity.loadUrl("javascript:updateHumidity(" + Float.toString(humidity) + ")");
        if(webViewPressure != null)
            webViewPressure.loadUrl("javascript:updatePressure(" + Float.toString(pressure) + ")");
        if (webViewUV != null && webViewUVlabel != null) {
            webViewUV.loadUrl("javascript:updateUV(" + UV + ")");
            webViewUVlabel.loadUrl("javascript:updateUVLabel(" + UV + ")");
        }
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
        if(webViewSO2 != null && webViewCO != null && webViewO3 != null && webViewNO2 != null && webViewPM != null) {
            webViewSO2.loadUrl("javascript:updateSO2(" + Float.toString(SO2) + ")");
            webViewCO.loadUrl("javascript:updateCO(" + Float.toString(CO) + ")");
            webViewO3.loadUrl("javascript:updateO3(" + Float.toString(O3) + ")");
            webViewNO2.loadUrl("javascript:updateNO2(" + Float.toString(NO2) + ")");
            webViewPM.loadUrl("javascript:updatePM(" + Float.toString(PM) + ")");
        }
        if(webViewSound != null) {
            webViewSound.loadUrl("javascript:updateSound(" + sound + ")");
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

    //This function is called at the end of the connection precess of a new devicec and also whenever we select a device. Its purpose is to show only the sensors that the device has.
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
            boolean micService = false;
            if (cardViewGases != null) {
                cardViewGases.setVisibility(View.VISIBLE);
                cardViewTemperature.setVisibility(View.VISIBLE);
                cardViewHumidity.setVisibility(View.VISIBLE);
                cardViewPressure.setVisibility(View.VISIBLE);
                cardViewUV.setVisibility(View.VISIBLE);
                cardViewAccelerometer.setVisibility(View.VISIBLE);
                cardViewSound.setVisibility(View.VISIBLE);
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
                else if (tempService.getUuid().toString().equals(gatt.MIC_SERVICE_UUID_ENVIRO.toString())) {
                    micService = true;
                }

            }

            Log.d(TAG, ".hideSensors() Results for device " + MainActivity.getInstance().getSensorDataList().get(MainActivity.getInstance().getConnectedDevicePosition()).getLocalName() + ": { accel: " + accelerometerService + ", weather:" + weatherSevice +", gases:" + gasesService + ", CO2:" + CO2Service + "}");
            if(!CO2Service && !gasesService && cardViewGases != null) {
                cardViewGases.setVisibility(View.GONE);
            }
            else if(CO2Service && !gasesService && webViewSO2 != null && webViewCO != null && webViewO3 != null && webViewNO2 != null && webViewCO2 != null && cardViewGases != null) {
                webViewCO2.setVisibility(View.VISIBLE);
                webViewSO2.setVisibility(View.GONE);
                webViewCO.setVisibility(View.GONE);
                webViewO3.setVisibility(View.GONE);
                webViewNO2.setVisibility(View.GONE);
                webViewPM.setVisibility(View.GONE);
            }
            else if(!CO2Service && gasesService && webViewSO2 != null && webViewCO != null && webViewO3 != null && webViewNO2 != null && webViewCO2 != null && cardViewGases != null) {
                webViewCO2.setVisibility(View.INVISIBLE);
                webViewSO2.setVisibility(View.VISIBLE);
                webViewCO.setVisibility(View.VISIBLE);
                webViewO3.setVisibility(View.VISIBLE);
                webViewNO2.setVisibility(View.VISIBLE);
                webViewPM.setVisibility(View.VISIBLE);
            }
            else if(CO2Service && gasesService && webViewSO2 != null && webViewCO != null && webViewO3 != null && webViewNO2 != null && webViewCO2 != null && cardViewGases != null) {
                webViewCO2.setVisibility(View.VISIBLE);
                webViewSO2.setVisibility(View.VISIBLE);
                webViewCO.setVisibility(View.VISIBLE);
                webViewO3.setVisibility(View.VISIBLE);
                webViewNO2.setVisibility(View.VISIBLE);
                webViewPM.setVisibility(View.VISIBLE);
            }

            if (!weatherSevice && cardViewTemperature != null && cardViewHumidity != null && cardViewPressure != null) {
                cardViewTemperature.setVisibility(View.GONE);
                cardViewHumidity.setVisibility(View.GONE);
                cardViewPressure.setVisibility(View.GONE);
                cardViewUV.setVisibility(View.GONE);
            }
            if (!accelerometerService && cardViewAccelerometer != null) {
                cardViewAccelerometer.setVisibility(View.GONE);
            }
            if (!micService && cardViewSound != null) {
                cardViewSound.setVisibility(View.GONE);
            }

        }
    }

}
