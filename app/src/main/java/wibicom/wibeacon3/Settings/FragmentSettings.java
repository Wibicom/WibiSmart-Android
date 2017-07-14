package wibicom.wibeacon3.Settings;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import wibicom.wibeacon3.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSettings extends Fragment {

    public boolean isSettingsMove = false;
    public boolean isSettingsEnviro = false;

    TextView notConnectedMessage;

    public FragmentSettings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        notConnectedMessage = (TextView) view.findViewById(R.id.settings_message);
        return view;
    }

    public void setSettingsEnviro(FragmentSettingsEnviro fragment)
    {
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_settings_container, fragment).commit();
        isSettingsEnviro = true;
        isSettingsMove = false;
        notConnectedMessage.setVisibility(View.INVISIBLE);
    }

    public void setSettingsMove(FragmentSettingsMove fragment)
    {
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_settings_container, fragment).commit();
        isSettingsEnviro = false;
        isSettingsMove = true;
        notConnectedMessage.setVisibility(View.INVISIBLE);
    }

    public void setSettingsInitial(FragmentSettingsMove fragmentSetttingMove, FragmentSettingsEnviro fragmentSettingsEnviro) {
        if(isSettingsEnviro) {
            getChildFragmentManager().beginTransaction().remove(fragmentSettingsEnviro).commit();
        }
        else if(isSettingsMove) {
            getChildFragmentManager().beginTransaction().remove(fragmentSetttingMove).commit();
        }
        isSettingsEnviro = false;
        isSettingsMove = false;
        notConnectedMessage.setVisibility(View.VISIBLE);
    }


}
