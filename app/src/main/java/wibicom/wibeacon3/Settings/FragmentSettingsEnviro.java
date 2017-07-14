package wibicom.wibeacon3.Settings;


import android.app.Activity;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

import wibicom.wibeacon3.MainActivity;
import wibicom.wibeacon3.MqttHandler;
import wibicom.wibeacon3.R;
import wibicom.wibeacon3.SensorData;
import wibicom.wibeacon3.SliderDialog;
import wibicom.wibeacon3.WibiSmartGatt;



public class FragmentSettingsEnviro extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences sharedPreferences;

    PreferenceScreen myPreferenceScreeen;
    PreferenceCategory accelPreference;
    PreferenceCategory weatherPreference;
    PreferenceCategory lightPreference;
    PreferenceCategory CO2Preference;

    boolean nonManualAccelCheckBoxChange = false;
    boolean nonManualWeatherCheckBoxChange = false;
    boolean nonManualLightCheckBoxChange = false;
    boolean nonManualCO2CheckboxChange = false;

    boolean nonManualAccelPeriodChange = false;
    boolean nonManualWeatherPeriodChange = false;
    boolean nonManualLightPeriodChange = false;
    boolean nonManualCO2PeriodChange = false;

    private final static String TAG = FragmentSettingsEnviro.class.getName();

    private OnSettingsEnviroListener mListener;

    public FragmentSettingsEnviro() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences_enviro);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences (getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        syncSettings();

        myPreferenceScreeen = (PreferenceScreen)findPreference("preferenceScreenEnviro");
        accelPreference = (PreferenceCategory)findPreference("preferenceCatergoryAccelerometer");
        weatherPreference = (PreferenceCategory)findPreference("preferenceCatergoryWeather");
        lightPreference = (PreferenceCategory)findPreference("preferenceCatergoryLight");
        CO2Preference = (PreferenceCategory)findPreference("preferenceCatergoryCO2");

    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        SensorData sensor = MainActivity.getInstance().getSensorDataList().get(MainActivity.getInstance().getConnectedDevicePosition());

        if(key.equals("android_device_mac_address"))
        {
            EditTextPreference editTextPreference = (EditTextPreference)pref;
            String androidDeviceId = editTextPreference.getText();
            pref.setSummary(androidDeviceId);
            MqttHandler.getInstance(MainActivity.getInstance()).setDeviceId(androidDeviceId);
            attemptIoTConnection();

        }
        else if(key.equals("iot_auth_token"))
        {
            EditTextPreference editTextPreference = (EditTextPreference)pref;
            String authToken = editTextPreference.getText();
            pref.setSummary(authToken);
            MqttHandler.getInstance(MainActivity.getInstance()).setAuthentificationToken(authToken);
            attemptIoTConnection();
        }
        else if(key.equals("accelerometer_checkbox_enviro"))
        {
            if(!nonManualAccelCheckBoxChange) {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) pref;
                byte value[] = {0x00};
                if (checkBoxPreference.isChecked())
                    value[0] = 0x01;

                sensor.setAccelSensorOn(value[0] == 1);
                UUID uuid = WibiSmartGatt.getInstance().ACCELEROMETER_CONF_CHAR_UUID_ENVIRO;
                Log.d(TAG, "accel checkbox toggled to " + value[0]);
                if (sensor.getHasAccelSensor()) {
                    mListener.writeCharacteristic(uuid, value);
                }
            }
            nonManualAccelCheckBoxChange = false;
        }
        else if(key.equals("accelerometer_period_enviro"))
        {
            SliderDialog sliderPref = (SliderDialog)pref;
            Integer period = sliderPref.getSliderValue();
            Log.d(TAG, "accel period changed to " + period);
            byte value[] = {period.byteValue()};
            UUID uuid = WibiSmartGatt.getInstance().ACCELEROMETER_PERIOD_CHAR_UUID_ENVIRO;
            if(!nonManualAccelPeriodChange && sensor.getHasAccelSensor()) {
                mListener.writeCharacteristic(uuid, value);
                sensor.setLastAccelPeriod(period);
            }

            period = period*100;
            pref.setSummary(period + " milliseconds");
        }
        else if(key.equals("weather_checkbox_enviro"))
        {
            if(!nonManualWeatherCheckBoxChange) {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) pref;
                byte value[] = {0x00};
                if (checkBoxPreference.isChecked())
                    value[0] = 0x01;

                sensor.setWeatherSensorOn(value[0] == 1);
                UUID uuid = WibiSmartGatt.getInstance().WEATHER_CONF_CHAR_UUID_ENVIRO;
                Log.d(TAG, "weather checkbox toggled to " + value[0]);
                if (sensor.getHasWeatherSensor()) {
                    mListener.writeCharacteristic(uuid, value);
                }
            }
            nonManualWeatherCheckBoxChange = false;
        }
        else if(key.equals("weather_period_enviro"))
        {
            SliderDialog sliderPref = (SliderDialog)pref;
            Integer period = sliderPref.getSliderValue();
            Log.d(TAG, "weather period changed to " + period);
            byte value[] = {period.byteValue()};
            UUID uuid = WibiSmartGatt.getInstance().WEATHER_PERIOD_CHAR_UUID_ENVIRO;
            if(!nonManualWeatherPeriodChange && sensor.getHasWeatherSensor()) {
                mListener.writeCharacteristic(uuid, value);
                sensor.setLastWeatherPeriod(period);
            }
            period = period*100;
            pref.setSummary(period + " milliseconds");
        }
        else if(key.equals("light_checkbox_enviro"))
        {
            if(!nonManualLightCheckBoxChange) {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) pref;
                byte value[] = {0x00};
                if (checkBoxPreference.isChecked())
                    value[0] = 0x01;

                sensor.setLighgtSensorOn(value[0] == 1);
                UUID uuid = WibiSmartGatt.getInstance().LIGHT_CONF_CHAR_UUID_ENVIRO;
                Log.d(TAG, "light checkbox toggled to " + value[0]);
                if (sensor.getHasLightSensor()) {
                    mListener.writeCharacteristic(uuid, value);
                }
            }
            nonManualLightCheckBoxChange = false;
        }
        else if(key.equals("light_period_enviro"))
        {
            SliderDialog sliderPref = (SliderDialog)pref;
            Integer period = sliderPref.getSliderValue();
            Log.d(TAG, "light period changed to " + period);
            byte value[] = {period.byteValue()};
            UUID uuid = WibiSmartGatt.getInstance().LIGHT_PERIOD_CHAR_UUID_ENVIRO;
            if(!nonManualLightPeriodChange && sensor.getHasLightSensor()) {
                mListener.writeCharacteristic(uuid, value);
                sensor.setLastLightPeriod(period);
            }
            period = period*100;
            pref.setSummary(period + " milliseconds");
        }
        else if(key.equals("CO2_checkbox_enviro"))
        {
            if(!nonManualCO2CheckboxChange) {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) pref;
                byte value[] = {0x00};
                if (checkBoxPreference.isChecked())
                    value[0] = 0x01;

                sensor.setCO2SensorOn(value[0] == 1);
                UUID uuid = WibiSmartGatt.getInstance().CO2_CONF_CHAR_UUID_ENVIRO;
                Log.d(TAG, "CO2 checkbox toggled to " + value[0]);
                if (sensor.getHasCO2Sensor()) {
                    mListener.writeCharacteristic(uuid, value);
                }
            }
            nonManualCO2CheckboxChange = false;
        }
        else if(key.equals("CO2_period_enviro"))
        {
            SliderDialog sliderPref = (SliderDialog)pref;
            Integer period = sliderPref.getSliderValue();
            Log.d(TAG, "CO2 period changed to " + period);
            byte value[] = {period.byteValue()};
            UUID uuid = WibiSmartGatt.getInstance().CO2_PEROÏOD_CHAR_UUID_ENVIRO;
            if(!nonManualCO2PeriodChange && sensor.getHasCO2Sensor()) {
                mListener.writeCharacteristic(uuid, value);
                sensor.setLastCO2Period(period);
            }

            period = period*100;
            pref.setSummary(period + " milliseconds");
        }
    }

    public void setAccelerometerCheckbox(boolean check)
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("accelerometer_checkbox_enviro");
        if (pref != null)
            pref.setChecked(check);
    }

    public boolean getAccelerometerCheckbox()
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("accelerometer_checkbox_enviro");
        if (pref != null)
            return pref.isChecked();
        return false;
    }

    public void setWeatherCheckbox(boolean check)
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("weather_checkbox_enviro");
        if (pref != null)
        pref.setChecked(check);
    }

    public boolean getWeatherCheckbox()
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("weather_checkbox_enviro");
        if (pref != null)
            return pref.isChecked();
        return false;
    }

    public void setLightCheckbox(boolean check)
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("light_checkbox_enviro");
        if (pref != null)
        pref.setChecked(check);
    }

    public boolean getLightCheckbox()
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("light_checkbox_enviro");
        if (pref != null)
            return pref.isChecked();
        return false;
    }

    public void setCO2Checkbox(boolean check)
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("CO2_checkbox_enviro");
        if (pref != null)
        pref.setChecked(check);
    }

    public boolean getCO2Checkbox()
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("CO2_checkbox_enviro");
        if (pref != null)
            return pref.isChecked();
        return false;
    }


    public int getAccelPeriod() {
        SliderDialog pref = (SliderDialog)findPreference("accelerometer_period_enviro");
        if (pref != null)
            return pref.getSliderValue().intValue();
        return 5;
    }

    public void setAccelPeriod(int val) {
        SliderDialog pref = (SliderDialog)findPreference("accelerometer_period_enviro");
        if (pref != null) {
            pref.setSliderValue(val);
            pref.setSummary((val * 100) + " milliseconds");
        }
    }

    public int getWeatherPeriod() {
        SliderDialog pref = (SliderDialog)findPreference("weather_period_enviro");
        if (pref != null)
            return pref.getSliderValue().intValue();
        return 5;
    }

    public void setWeatherPeriod(int val) {
        SliderDialog pref = (SliderDialog)findPreference("weather_period_enviro");
        if (pref != null) {
            pref.setSliderValue(val);
            pref.setSummary((val * 100) + " milliseconds");
        }
    }

    public int getLightPeriod() {
        SliderDialog pref = (SliderDialog)findPreference("light_period_enviro");
        if (pref != null)
            return pref.getSliderValue().intValue();
        return 5;
    }

    public void setLightPeriod(int val) {
        SliderDialog pref = (SliderDialog)findPreference("light_period_enviro");
        if (pref != null) {
            pref.setSliderValue(val);
            pref.setSummary((val * 100) + " milliseconds");
        }
    }

    public int getCO2Period() {
        SliderDialog pref = (SliderDialog)findPreference("CO2_period_enviro");
        if (pref != null)
            return pref.getSliderValue().intValue();
        return 5;
    }

    public void setCO2Period(int val) {
        SliderDialog pref = (SliderDialog)findPreference("CO2_period_enviro");
        if (pref != null) {
            pref.setSliderValue(val);
            pref.setSummary((val * 100) + " milliseconds");
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsEnviroListener) {
            mListener = (OnSettingsEnviroListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity context)
    {
        super.onAttach(context);
        if (context instanceof OnSettingsEnviroListener) {
            mListener = (OnSettingsEnviroListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnSettingsEnviroListener {
        void writeCharacteristic(UUID uuid, byte[] value);
    }


    public void syncSettings() {
        Log.d(TAG, "entering syncSettings()");
        ArrayList<BluetoothGattService> gattList = new ArrayList<>(MainActivity.getInstance().getSensorDataList().get(MainActivity.getInstance().getConnectedDevicePosition()).getConnecteddeviceGatt().getServices());
        boolean weatherSevice = false;
        boolean CO2Service = false;
        boolean accelerometerService = false;
        boolean lightService = false;

        if(accelPreference != null && myPreferenceScreeen != null) {
            myPreferenceScreeen.addPreference(accelPreference);
            myPreferenceScreeen.addPreference(weatherPreference);
            myPreferenceScreeen.addPreference(CO2Preference);
            myPreferenceScreeen.addPreference(lightPreference);
        }

        WibiSmartGatt gatt = WibiSmartGatt.getInstance();
        for(int i = 0 ; i < gattList.size() ; i++) {
            BluetoothGattService tempService = gattList.get(i);
            if (tempService.getUuid().toString().equals(gatt.CO2_SERVICE_UUID_ENVIRO.toString())) {
                CO2Service = true;
            }
            else if (tempService.getUuid().toString().equals(gatt.WEATHER_SERVICE_UUID_ENVIRO.toString())) {
                weatherSevice = true;
            }
            else if (tempService.getUuid().toString().equals(gatt.ACCELEROMETER_SERVICE_UUID_ENVIRO.toString())) {
                accelerometerService = true;
            }
            else if (tempService.getUuid().toString().equals(gatt.LIGHT_SERVICE_UUID_ENVIRO.toString())) {
                lightService = true;
            }
        }

        Log.d(TAG, ".syncSettings() Results: { accel: "+ accelerometerService + ", weather:" + weatherSevice+ ", CO2:" + CO2Service + ", light:" + lightService+ "}");

        if(myPreferenceScreeen != null) {
            if (!accelerometerService && accelPreference != null) {
                myPreferenceScreeen.removePreference(accelPreference);
            }
            if (!weatherSevice && weatherPreference != null) {
                myPreferenceScreeen.removePreference(weatherPreference);
            }
            if(!lightService && lightPreference != null) {
                myPreferenceScreeen.removePreference(lightPreference);
            }
            if(!CO2Service && CO2Preference != null) {
                myPreferenceScreeen.removePreference(CO2Preference);
            }
        }
        else {
            Log.d(TAG, ".syncSettings() myPreferenceScreen was null");
        }

        SensorData sensor = MainActivity.getInstance().getSensorDataList().get(MainActivity.getInstance().getConnectedDevicePosition());
        nonManualAccelCheckBoxChange = (sensor.getAccelSensorOn() != getAccelerometerCheckbox());
        setAccelerometerCheckbox(sensor.getAccelSensorOn());
        nonManualCO2CheckboxChange = (sensor.getCO2SensorOn() != getCO2Checkbox());
        setCO2Checkbox(sensor.getCO2SensorOn());
        nonManualLightCheckBoxChange = (sensor.getLightSensorOn() != getLightCheckbox());
        setLightCheckbox(sensor.getLightSensorOn());
        nonManualWeatherCheckBoxChange = (sensor.getWeatherSensorOn() != getWeatherCheckbox());
        setWeatherCheckbox(sensor.getWeatherSensorOn());

        nonManualAccelPeriodChange = (sensor.getLastAccelPeriod() != getAccelPeriod());
        setAccelPeriod(sensor.getLastAccelPeriod());
        nonManualCO2PeriodChange = (sensor.getLastCO2Period() != getCO2Period());
        setCO2Period(sensor.getLastCO2Period());
        nonManualLightPeriodChange = (sensor.getLastLightPeriod() != getLightPeriod());
        setLightPeriod(sensor.getLastLightPeriod());
        nonManualWeatherPeriodChange = (sensor.getLastWeatherPeriod() != getWeatherPeriod());
        setWeatherPeriod(sensor.getLastWeatherPeriod());
    }

    private void attemptIoTConnection() {
        Log.d(TAG, "entering .attemptIoTConnection()");
        EditTextPreference idPref = (EditTextPreference)findPreference("android_device_mac_address");
        EditTextPreference tokenPref = (EditTextPreference)findPreference("iot_auth_token");
        String id = idPref.getText();
        String token = tokenPref.getText();
        if (id.length() == 12 && token.length() == 18) {
            MainActivity.getInstance().connectIot();
        }
    }



}
