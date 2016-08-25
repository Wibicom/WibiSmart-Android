package wibicom.wibeacon3;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.content.Context;
import android.preference.DialogPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import wibicom.wibeacon3.Settings.FragmentSettingEddyUid;
import wibicom.wibeacon3.Settings.FragmentSettingEddyUrl;
import wibicom.wibeacon3.Settings.FragmentSettingIBeacon;

/**
 * Created by Olivier on 7/25/2016.
 */
public class BeaconDialog extends DialogPreference {

    String message = "";
    TextView mTextValue;

    public BeaconDialog(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.beacon_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        for (int i=0;i<attrs.getAttributeCount();i++) {
            String attr = attrs.getAttributeName(i);
            String val  = attrs.getAttributeValue(i);
            if (attr.equals("dialogMessage")) {

                message = val;
            }
        }
    }

    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();

        TextView messageDialog = (TextView) view.findViewById(R.id.beacon_dialog_message);
        messageDialog.setText(message);
        loadSpinners(view);

        return view;
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {

        }
        super.onDialogClosed(positiveResult);
    }

    private void loadSpinners(final View view)
    {
        // Beacon type spinner.
        final Spinner spinnerBeaconType = (Spinner) view.findViewById(R.id.spinner_beacon_format);
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


                //Fragment fragment = ((Activity) getContext()).getFragmentManager().findFragmentById(R.id.beacon_fragment_container);

                if(true)//fragment != null)
                {

                    FragmentTransaction transaction = ((Activity)getContext()).getFragmentManager().beginTransaction();
                    FragmentSettingIBeacon fragIBeacon = new FragmentSettingIBeacon();
                    transaction.add(R.id.beacon_fragment_container1, fragIBeacon);


//                    switch(beaconType)
//                    {
//                        case 0:
//                            FragmentSettingIBeacon fragIBeacon = new FragmentSettingIBeacon();
//                            transaction.replace(R.id.beacon_fragment_container, fragIBeacon);
//                            break;
//                        case 1:
//                            FragmentSettingEddyUid fragEddyUid = new FragmentSettingEddyUid();
//                            transaction.replace(R.id.beacon_fragment_container, fragEddyUid);
//                            break;
//                        case 2:
//                            FragmentSettingEddyUrl fragEddyUrl = new FragmentSettingEddyUrl();
//                            transaction.replace(R.id.beacon_fragment_container, fragEddyUrl);
//                            break;
//                        case 3:
//                            break;
//                        case 4:
//                            break;
//                        default:
//                            break;
//                    }
                    transaction.commit();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });
    }
}
