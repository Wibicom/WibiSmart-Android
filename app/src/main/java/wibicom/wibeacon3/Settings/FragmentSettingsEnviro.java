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
    PreferenceCategory gasesPreference;

    boolean nonManualAccelCheckBoxChange = false;
    boolean nonManualWeatherCheckBoxChange = false;
    boolean nonManualLightCheckBoxChange = false;
    boolean nonManualCO2CheckboxChange = false;
    boolean nonManualSO2CheckBoxChange = false;
    boolean nonManualCOCheckBoxChange = false;
    boolean nonManualO3CheckBoxChange = false;
    boolean nonManualNO2CheckBoxChange = false;


    boolean nonManualAccelPeriodChange = false;
    boolean nonManualWeatherPeriodChange = false;
    boolean nonManualLightPeriodChange = false;
    boolean nonManualCO2PeriodChange = false;
    boolean nonManualGasesPeriodChange = false;

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
        gasesPreference = (PreferenceCategory)findPreference("preferenceCatergoryGases");

    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        SensorData sensor = MainActivity.getInstance().getSensorDataList().get(MainActivity.getInstance().getConnectedDevicePosition());

        switch (key) {
            case "android_device_mac_address": {
                EditTextPreference editTextPreference = (EditTextPreference) pref;
                String androidDeviceId = editTextPreference.getText();
                pref.setSummary(androidDeviceId);
                MqttHandler.getInstance(MainActivity.getInstance()).setDeviceId(androidDeviceId);
                attemptIoTConnection();
                break;
            }
            case "iot_auth_token": {
                EditTextPreference editTextPreference = (EditTextPreference) pref;
                String authToken = editTextPreference.getText();
                pref.setSummary(authToken);
                MqttHandler.getInstance(MainActivity.getInstance()).setAuthentificationToken(authToken);
                attemptIoTConnection();
                break;
            }
            case "accelerometer_checkbox_enviro": {
                if (!nonManualAccelCheckBoxChange) {
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
                break;
            }
            case "accelerometer_period_enviro": {
                SliderDialog sliderPref = (SliderDialog) pref;
                Integer period = sliderPref.getSliderValue();
                Log.d(TAG, "accel period changed to " + period);
                byte value[] = {period.byteValue()};
                UUID uuid = WibiSmartGatt.getInstance().ACCELEROMETER_PERIOD_CHAR_UUID_ENVIRO;
                if (!nonManualAccelPeriodChange && sensor.getHasAccelSensor()) {
                    mListener.writeCharacteristic(uuid, value);
                    sensor.setLastAccelPeriod(period);
                }
                nonManualAccelPeriodChange = false;
                period = period * 100;
                pref.setSummary(period + " milliseconds");
                break;
            }
            case "weather_checkbox_enviro": {
                if (!nonManualWeatherCheckBoxChange) {
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
                break;
            }
            case "weather_period_enviro": {
                SliderDialog sliderPref = (SliderDialog) pref;
                Integer period = sliderPref.getSliderValue();
                Log.d(TAG, "weather period changed to " + period);
                byte value[] = {period.byteValue()};
                UUID uuid = WibiSmartGatt.getInstance().WEATHER_PERIOD_CHAR_UUID_ENVIRO;
                if (!nonManualWeatherPeriodChange && sensor.getHasWeatherSensor()) {
                    mListener.writeCharacteristic(uuid, value);
                    sensor.setLastWeatherPeriod(period);
                }
                nonManualWeatherPeriodChange = false;
                period = period * 100;
                pref.setSummary(period + " milliseconds");
                break;
            }
            case "light_checkbox_enviro": {
                if (!nonManualLightCheckBoxChange) {
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
                break;
            }
            case "light_period_enviro": {
                SliderDialog sliderPref = (SliderDialog) pref;
                Integer period = sliderPref.getSliderValue();
                Log.d(TAG, "light period changed to " + period);
                byte value[] = {period.byteValue()};
                UUID uuid = WibiSmartGatt.getInstance().LIGHT_PERIOD_CHAR_UUID_ENVIRO;
                if (!nonManualLightPeriodChange && sensor.getHasLightSensor()) {
                    mListener.writeCharacteristic(uuid, value);
                    sensor.setLastLightPeriod(period);
                }
                nonManualLightPeriodChange = false;
                period = period * 100;
                pref.setSummary(period + " milliseconds");
                break;
            }
            case "CO2_checkbox_enviro": {
                if (!nonManualCO2CheckboxChange) {
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
                break;
            }
            case "CO2_period_enviro": {
                SliderDialog sliderPref = (SliderDialog) pref;
                Integer period = sliderPref.getSliderValue();
                Log.d(TAG, "CO2 period changed to " + period);
                byte value[] = {period.byteValue()};
                UUID uuid = WibiSmartGatt.getInstance().CO2_PEROÏOD_CHAR_UUID_ENVIRO;
                if (!nonManualCO2PeriodChange && sensor.getHasCO2Sensor()) {
                    mListener.writeCharacteristic(uuid, value);
                    sensor.setLastCO2Period(period);
                }
                nonManualCO2PeriodChange = false;
                period = period * 100;
                pref.setSummary(period + " milliseconds");
                break;
            }
            case "SO2_checkbox_enviro": {
                if (!nonManualSO2CheckBoxChange) {
                    CheckBoxPreference checkBoxPreferenceSO2 = (CheckBoxPreference) pref;
                    byte value[] = {0};

                    if (getSO2Checkbox()) {
                        value[0] += 1;
                    }
                    if (getCOCheckbox()) {
                        value[0] += 2;
                    }
                    if (getO3Checkbox()) {
                        value[0] += 4;
                    }
                    if (getNO2Checkbox()) {
                        value[0] += 8;
                    }
                    sensor.setSO2SensorOn(checkBoxPreferenceSO2.isChecked());
                    UUID uuid = WibiSmartGatt.getInstance().SPEC_CONF_CHAR_UUID_ENVIRO;
                    Log.d(TAG, "SO2 checkbox toggled to " + value[0]);
                    if (sensor.getHasGasesSensor()) {
                        mListener.writeCharacteristic(uuid, value);
                    }
                }
                nonManualSO2CheckBoxChange = false;
                break;
            }
            case "CO_checkbox_enviro": {
                if (!nonManualCOCheckBoxChange) {
                    CheckBoxPreference checkBoxPreferenceCO = (CheckBoxPreference) pref;
                    byte value[] = {0};

                    if (getSO2Checkbox()) {
                        value[0] += 1;
                    }
                    if (getCOCheckbox()) {
                        value[0] += 2;
                    }
                    if (getO3Checkbox()) {
                        value[0] += 4;
                    }
                    if (getNO2Checkbox()) {
                        value[0] += 8;
                    }
                    sensor.setCOSensorOn(checkBoxPreferenceCO.isChecked());
                    UUID uuid = WibiSmartGatt.getInstance().SPEC_CONF_CHAR_UUID_ENVIRO;
                    Log.d(TAG, "CO checkbox toggled to " + value[0]);
                    if (sensor.getHasGasesSensor()) {
                        mListener.writeCharacteristic(uuid, value);
                    }
                }
                nonManualCOCheckBoxChange = false;
                break;
            }
            case "O3_checkbox_enviro": {
                if (!nonManualO3CheckBoxChange) {
                    CheckBoxPreference checkBoxPreferenceO3 = (CheckBoxPreference) pref;
                    byte value[] = {0};

                    if (getSO2Checkbox()) {
                        value[0] += 1;
                    }
                    if (getCOCheckbox()) {
                        value[0] += 2;
                    }
                    if (getO3Checkbox()) {
                        value[0] += 4;
                    }
                    if (getNO2Checkbox()) {
                        value[0] += 8;
                    }
                    sensor.setO3SensorOn(checkBoxPreferenceO3.isChecked());
                    UUID uuid = WibiSmartGatt.getInstance().SPEC_CONF_CHAR_UUID_ENVIRO;
                    Log.d(TAG, "O3 checkbox toggled to " + value[0]);
                    if (sensor.getHasGasesSensor()) {
                        mListener.writeCharacteristic(uuid, value);
                    }
                }
                nonManualO3CheckBoxChange = false;
                break;
            }
            case "NO2_checkbox_enviro": {
                if (!nonManualNO2CheckBoxChange) {
                    CheckBoxPreference checkBoxPreferenceNO2 = (CheckBoxPreference) pref;
                    byte value[] = {0};

                    if (getSO2Checkbox()) {
                        value[0] += 1;
                    }
                    if (getCOCheckbox()) {
                        value[0] += 2;
                    }
                    if (getO3Checkbox()) {
                        value[0] += 4;
                    }
                    if (getNO2Checkbox()) {
                        value[0] += 8;
                    }
                    sensor.setNO2SensorOn(checkBoxPreferenceNO2.isChecked());
                    UUID uuid = WibiSmartGatt.getInstance().SPEC_CONF_CHAR_UUID_ENVIRO;
                    Log.d(TAG, "NO2 checkbox toggled to " + value[0]);
                    if (sensor.getHasGasesSensor()) {
                        mListener.writeCharacteristic(uuid, value);
                    }
                }
                nonManualNO2CheckBoxChange = false;
                break;
            }
            case "gases_period_enviro": {
                SliderDialog sliderPref = (SliderDialog) pref;
                Integer period = sliderPref.getSliderValue();
                Log.d(TAG, "gases period changed to " + period);
                byte value[] = {period.byteValue()};
                UUID uuid = WibiSmartGatt.getInstance().SPEC_PEROÏOD_CHAR_UUID_ENVIRO;
                if (!nonManualGasesPeriodChange && sensor.getHasGasesSensor()) {
                    mListener.writeCharacteristic(uuid, value);
                    sensor.setLastGasesPeriod(period);
                }
                nonManualGasesPeriodChange = false;
                period = period * 100;
                pref.setSummary(period + " milliseconds");
                break;
            }
            default:
                break;

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

    public void setSO2Checkbox(boolean check)
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("SO2_checkbox_enviro");
        if (pref != null)
            pref.setChecked(check);
    }

    public boolean getSO2Checkbox()
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("SO2_checkbox_enviro");
        if (pref != null)
            return pref.isChecked();
        return false;
    }


    public void setCOCheckbox(boolean check)
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("CO_checkbox_enviro");
        if (pref != null)
            pref.setChecked(check);
    }

    public boolean getCOCheckbox()
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("CO_checkbox_enviro");
        if (pref != null)
            return pref.isChecked();
        return false;
    }


    public void setO3Checkbox(boolean check)
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("O3_checkbox_enviro");
        if (pref != null)
            pref.setChecked(check);
    }

    public boolean getO3Checkbox()
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("O3_checkbox_enviro");
        if (pref != null)
            return pref.isChecked();
        return false;
    }


    public void setNO2Checkbox(boolean check)
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("NO2_checkbox_enviro");
        if (pref != null)
            pref.setChecked(check);
    }

    public boolean getNO2Checkbox()
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("NO2_checkbox_enviro");
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

    public int getGasesPeriod() {
        SliderDialog pref = (SliderDialog)findPreference("gases_period_enviro");
        if (pref != null)
            return pref.getSliderValue().intValue();
        return 5;
    }

    public void setGasesPeriod(int val) {
        SliderDialog pref = (SliderDialog)findPreference("gases_period_enviro");
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
        boolean gasesService = false;

        if(accelPreference != null && myPreferenceScreeen != null) {
            myPreferenceScreeen.addPreference(accelPreference);
            myPreferenceScreeen.addPreference(weatherPreference);
            myPreferenceScreeen.addPreference(CO2Preference);
            myPreferenceScreeen.addPreference(lightPreference);
            myPreferenceScreeen.addPreference(gasesPreference);
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
            else if (tempService.getUuid().toString().equals(gatt.SPEC_SERVICE_UUID_ENVIRO.toString())) {
                gasesService = true;
            }
        }

        Log.d(TAG, ".syncSettings() Results: { accel: "+ accelerometerService + ", weather:" + weatherSevice+ ", CO2:" + CO2Service + ", gases:" + gasesService + ", light:" + lightService+ "}");

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
            if(!gasesService && gasesPreference != null) {
                myPreferenceScreeen.removePreference(gasesPreference);
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
        nonManualSO2CheckBoxChange = (sensor.getSO2SensorOn() != getSO2Checkbox());
        setSO2Checkbox(sensor.getSO2SensorOn());
        nonManualCOCheckBoxChange = (sensor.getCOSensorOn() != getCOCheckbox());
        setCOCheckbox(sensor.getCOSensorOn());
        nonManualO3CheckBoxChange = (sensor.getO3SensorOn() != getO3Checkbox());
        setO3Checkbox(sensor.getO3SensorOn());
        nonManualNO2CheckBoxChange = (sensor.getNO2SensorOn() != getNO2Checkbox());
        setNO2Checkbox(sensor.getNO2SensorOn());

        nonManualAccelPeriodChange = (sensor.getLastAccelPeriod() != getAccelPeriod());
        setAccelPeriod(sensor.getLastAccelPeriod());
        nonManualCO2PeriodChange = (sensor.getLastCO2Period() != getCO2Period());
        setCO2Period(sensor.getLastCO2Period());
        nonManualLightPeriodChange = (sensor.getLastLightPeriod() != getLightPeriod());
        setLightPeriod(sensor.getLastLightPeriod());
        nonManualWeatherPeriodChange = (sensor.getLastWeatherPeriod() != getWeatherPeriod());
        setWeatherPeriod(sensor.getLastWeatherPeriod());
        nonManualGasesPeriodChange = (sensor.getLastGasesPeriod() != getGasesPeriod());
        setGasesPeriod(sensor.getLastGasesPeriod());
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
