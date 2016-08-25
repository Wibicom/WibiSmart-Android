package wibicom.wibeacon3.Dashboard;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        return view;
    }

    public void updateData(float temperature, float pressure, float humidity, float accelerometerX, float accelerometerY, float accelerometerZ, int batteryLevel, int rssi, int light)
    {

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
