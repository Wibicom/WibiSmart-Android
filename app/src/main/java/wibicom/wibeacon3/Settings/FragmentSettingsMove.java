package wibicom.wibeacon3.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.nio.ByteBuffer;
import java.util.UUID;

import wibicom.wibeacon3.R;
import wibicom.wibeacon3.SliderDialog;
import wibicom.wibeacon3.WibiSmartGatt;


public class FragmentSettingsMove extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    private OnSettingsMoveListener mListener;
    SharedPreferences sharedPreferences;

    public FragmentSettingsMove() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences_move);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences (getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference pref = findPreference(key);

        if(key.equals("device_name_move"))
        {

        }
        else if(key.equals("device_mode_move"))
        {

        }
        else if(key.equals("beacon_format_move"))
        {
            ListPreference listPref = (ListPreference)pref;
            byte[] value = {(byte)((int)Integer.valueOf(listPref.getValue()))};
            mListener.writeCharacteristic(WibiSmartGatt.getInstance().ADV_MODE_CHAR_UUID_MOVE, value);

            switch(Integer.valueOf(listPref.getValue()))
            {
                case 0: setIbeaconPayload();
                    break;
                case 1: setEddystoneUidPayload();
                    break;
                case 2: setEddystoneUrlPayload();
                    break;
                case 3:
                default:
                    break;
            }
        }
        else if(key.equals("advertising_intervals_move"))
        {
            SliderDialog sliderPref = (SliderDialog)pref;
            Integer period = sliderPref.getSliderValue();
            byte value[] = {period.byteValue()};
            UUID uuid = WibiSmartGatt.getInstance().ADV_INTERVAL_CHAR_UUID_MOVE;
            mListener.writeCharacteristic(uuid, value);

            period = period*100;
            pref.setSummary(period + " milliseconds");
        }
        else if(key.equals("tx_power_move"))
        {
            ListPreference listPref = (ListPreference)pref;
            byte[] value = {(byte)((int)Integer.valueOf(listPref.getValue()))};
            mListener.writeCharacteristic(WibiSmartGatt.getInstance().ADV_MODE_CHAR_UUID_MOVE, value);
        }
        else if(key.equals("smart_power_mode_move"))
        {

        }
        else if((key.equals("ibeacon_uuid") || key.equals("ibeacon_major") || key.equals("ibeacon_minor"))
                && ((ListPreference)findPreference("beacon_format_move")).getValue().equals("0"))
        {
            setIbeaconPayload();
        }
        else if((key.equals("eddystone_namespace_id") || key.equals("eddystone_instance_id"))
                && ((ListPreference)findPreference("beacon_format_move")).getValue().equals("1"))
        {
            setEddystoneUidPayload();
        }
        else if((key.equals("eddystone_url") || key.equals("eddystone_url_extension"))
                && ((ListPreference)findPreference("beacon_format_move")).getValue().equals("2"))
        {
            setEddystoneUrlPayload();
        }

    }


    private void setIbeaconPayload()
    {
        EditTextPreference ibeaconUuidPref = (EditTextPreference)findPreference("ibeacon_uuid");
        ibeaconUuidPref.setSummary(ibeaconUuidPref.getText());

        EditTextPreference ibeaconMajorPref = (EditTextPreference)findPreference("ibeacon_major");
        ibeaconMajorPref.setSummary(ibeaconMajorPref.getText());

        EditTextPreference ibeaconMinorPref = (EditTextPreference)findPreference("ibeacon_minor");
        ibeaconMinorPref.setSummary(ibeaconMinorPref.getText());

        byte[] payload = stringToBytes(ibeaconUuidPref.getText());
        int minor = Integer.parseInt(ibeaconMinorPref.getText(), 10);
        int major = Integer.parseInt(ibeaconMajorPref.getText(), 10);

        payload[16] = (byte) ((major >> 8)& 0xFF);
        payload[17] = (byte) (major & 0xFF);
        payload[18] = (byte) ((minor >> 8) & 0xFF);
        payload[19] = (byte) (minor & 0xFF);

        mListener.writeCharacteristic(WibiSmartGatt.getInstance().ADV_PAYLOAD_CHAR_UUID_MOVE, payload);
    }

    private void setEddystoneUidPayload()
    {
        EditTextPreference eddystoneNamespaceIdPref = (EditTextPreference)findPreference("eddystone_namespace_id");
        eddystoneNamespaceIdPref.setSummary(eddystoneNamespaceIdPref.getText());
        EditTextPreference eddystoneInstanceIdPref = (EditTextPreference)findPreference("eddystone_instance_id");
        eddystoneInstanceIdPref.setSummary(eddystoneInstanceIdPref.getText());

        String payload = eddystoneNamespaceIdPref.getText() + eddystoneInstanceIdPref.getText();
        byte[] value = stringToBytes(payload);
        mListener.writeCharacteristic(WibiSmartGatt.getInstance().ADV_PAYLOAD_CHAR_UUID_MOVE, value);
    }

    private void setEddystoneUrlPayload()
    {
        EditTextPreference eddystoneUrlPref = (EditTextPreference)findPreference("eddystone_url");
        String url = eddystoneUrlPref.getText();

        byte extension = Integer.valueOf(((ListPreference)findPreference("eddystone_url_extension")).getValue()).byteValue();
        eddystoneUrlPref.setSummary(eddystoneUrlPref.getText());

        byte[] byteArray = new byte[url.length() + 1];
        byte[] payloadArray = url.getBytes();
        // Assign the URL bytes to the payload
        for(int i = 0; i < url.length(); i++)
        {
            byteArray[i] = payloadArray[i];
        }
        // Add the extension byte at the end of the payload
        byteArray[url.length()] = extension;

        mListener.writeCharacteristic(WibiSmartGatt.getInstance().ADV_PAYLOAD_CHAR_UUID_MOVE, byteArray);
    }

    private byte[] stringToBytes(String payload)
    {
        byte[] byteArray = new byte[20];
        int j = 0;
        for(int i = 0; i < payload.length() - 1; i++)
        {
            byteArray[j] = (byte)Integer.parseInt(payload.substring(i, i+2), 16);
            j++;
            i++;
        }
        return byteArray;
    }

    private void loadSpinners(final View view)
    {
        // Beacon type spinner.
        final Spinner spinnerBeaconType = (Spinner) view.findViewById(R.id.spinner_beacon_type);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterBeaconType = ArrayAdapter.createFromResource(getContext(),
                R.array.beacon_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterBeaconType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerBeaconType.setAdapter(adapterBeaconType);
        spinnerBeaconType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                int beaconType = spinnerBeaconType.getSelectedItemPosition();

                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

                switch(beaconType)
                {
                    case 0:
                        FragmentSettingIBeacon fragIBeacon = new FragmentSettingIBeacon();
                        transaction.replace(R.id.fragment_container, fragIBeacon);
                        break;
                    case 1:
                        FragmentSettingEddyUid fragEddyUid = new FragmentSettingEddyUid();
                        transaction.replace(R.id.fragment_container, fragEddyUid);
                        break;
                    case 2:
                        FragmentSettingEddyUrl fragEddyUrl = new FragmentSettingEddyUrl();
                        transaction.replace(R.id.fragment_container, fragEddyUrl);
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    default:
                        break;
                }
                transaction.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });

        // Tx power spinner.
        Spinner spinnerTxPower = (Spinner) view.findViewById(R.id.spinner_tx_power);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterTxPower = ArrayAdapter.createFromResource(getContext(),
                R.array.tx_power, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterTxPower.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerTxPower.setAdapter(adapterTxPower);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsMoveListener) {
            mListener = (OnSettingsMoveListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity context)
    {
        super.onAttach(context);
        if (context instanceof OnSettingsMoveListener) {
            mListener = (OnSettingsMoveListener) context;
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


    public interface OnSettingsMoveListener {
        void writeCharacteristic(UUID uuid, byte[] value);
    }
}
