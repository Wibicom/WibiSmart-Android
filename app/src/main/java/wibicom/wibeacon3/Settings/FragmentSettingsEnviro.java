package wibicom.wibeacon3.Settings;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import java.util.UUID;

import wibicom.wibeacon3.R;
import wibicom.wibeacon3.SliderDialog;
import wibicom.wibeacon3.WibiSmartGatt;



public class FragmentSettingsEnviro extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences sharedPreferences;

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


    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if(key.equals("device_name_enviro"))
        {
            EditTextPreference editTextPreference = (EditTextPreference)pref;
            String deviceName = editTextPreference.getText();
            pref.setSummary(deviceName);
            mListener.writeCharacteristic(WibiSmartGatt.getInstance().ADV_PAYLOAD_CHAR_UUID_MOVE, deviceName.getBytes());
        }
        else if(key.equals("connection_intervals_enviro"))
        {
            EditTextPreference editTextPreference = (EditTextPreference)pref;
            int connectionIntervals = Integer.valueOf(editTextPreference.getText());
            pref.setSummary(connectionIntervals + " milliseconds");
        }
        else if(key.equals("accelerometer_checkbox_enviro"))
        {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference)pref;
            byte value[] = {0x00};
            if(checkBoxPreference.isChecked())
                value[0] = 0x01;

            UUID uuid = WibiSmartGatt.getInstance().ACCELEROMETER_CONF_CHAR_UUID_ENVIRO;
            mListener.writeCharacteristic(uuid, value);
        }
        else if(key.equals("accelerometer_period_enviro"))
        {
            SliderDialog sliderPref = (SliderDialog)pref;
            Integer period = sliderPref.getSliderValue();
            byte value[] = {period.byteValue()};
            UUID uuid = WibiSmartGatt.getInstance().ACCELEROMETER_PERIOD_CHAR_UUID_ENVIRO;
            mListener.writeCharacteristic(uuid, value);

            period = period*100;
            pref.setSummary(period + " milliseconds");
        }
        else if(key.equals("weather_checkbox_enviro"))
        {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference)pref;
            byte value[] = {0x00};
            if(checkBoxPreference.isChecked())
                value[0] = 0x01;

            UUID uuid = WibiSmartGatt.getInstance().WEATHER_CONF_CHAR_UUID_ENVIRO;
            mListener.writeCharacteristic(uuid, value);
        }
        else if(key.equals("weather_period_enviro"))
        {
            SliderDialog sliderPref = (SliderDialog)pref;
            Integer period = sliderPref.getSliderValue();
            byte value[] = {period.byteValue()};
            UUID uuid = WibiSmartGatt.getInstance().WEATHER_PERIOD_CHAR_UUID_ENVIRO;
            mListener.writeCharacteristic(uuid, value);

            period = period*100;
            pref.setSummary(period + " milliseconds");
        }
        else if(key.equals("light_checkbox_enviro"))
        {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference)pref;
            byte value[] = {0x00};
            if(checkBoxPreference.isChecked())
                value[0] = 0x01;

            UUID uuid = WibiSmartGatt.getInstance().LIGHT_CONF_CHAR_UUID_ENVIRO;
            mListener.writeCharacteristic(uuid, value);
        }
        else if(key.equals("light_period_enviro"))
        {
            SliderDialog sliderPref = (SliderDialog)pref;
            Integer period = sliderPref.getSliderValue();
            byte value[] = {period.byteValue()};
            UUID uuid = WibiSmartGatt.getInstance().LIGHT_PERIOD_CHAR_UUID_ENVIRO;
            mListener.writeCharacteristic(uuid, value);

            period = period*100;
            pref.setSummary(period + " milliseconds");
        }
    }

    public void setAccelerometerCheckbox(boolean check)
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("accelerometer_checkbox");
        pref.setChecked(check);
    }

    public void setWeatherCheckbox(boolean check)
    {
        CheckBoxPreference pref = (CheckBoxPreference)findPreference("weather_checkbox");
        pref.setChecked(check);
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



}
