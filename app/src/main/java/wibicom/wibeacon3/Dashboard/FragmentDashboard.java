package wibicom.wibeacon3.Dashboard;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import wibicom.wibeacon3.R;


public class FragmentDashboard extends Fragment {

    public boolean isDashboardMove = false;
    public boolean isDashboardEnviro = false;
    TextView notConnectedMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        notConnectedMessage = (TextView) view.findViewById(R.id.dashboard_message);

        return view;
    }

    public void setDashboardEnviro(FragmentDashboardEnviro fragment)
    {
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_dashboard_container, fragment).commit();
        isDashboardEnviro = true;
        isDashboardMove = false;
        notConnectedMessage.setVisibility(View.INVISIBLE);
    }

    public void setDashboardMove(FragmentDashboardMove fragment)
    {
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_dashboard_container, fragment).commit();
        isDashboardEnviro = false;
        isDashboardMove = true;
        notConnectedMessage.setVisibility(View.INVISIBLE);
    }

    public void setDashboardInitial(FragmentDashboardEnviro fragmentDashboardEnviro, FragmentDashboardMove fragmentDashboardMove)
    {
        if(isDashboardEnviro) {
            getChildFragmentManager().beginTransaction().remove(fragmentDashboardEnviro).commit();
        }
        else if(isDashboardMove) {
            getChildFragmentManager().beginTransaction().remove(fragmentDashboardMove).commit();
        }
        isDashboardEnviro = false;
        isDashboardMove = false;
        notConnectedMessage.setVisibility(View.VISIBLE);
    }
}
