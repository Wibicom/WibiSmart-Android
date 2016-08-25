package wibicom.wibeacon3.Dashboard;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import wibicom.wibeacon3.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentDashboardMove.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentDashboardMove#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDashboardMove extends Fragment {

    TextView temperatureText;
    TextView vSolarText;
    TextView batteryLevelText;

    TextView accelerometerTextX;
    TextView accelerometerTextY;
    TextView accelerometerTextZ;

    //private List<BluetoothGattService> servicesList = new ArrayList<>();
    //private OnFragmentInteractionListener mListener;
    View currentView;

    private RecyclerView recyclerView;
   // private DashboardRecyclerViewAdapter dashboardRecyclerViewAdapter;

    public FragmentDashboardMove() {
        // Required empty public constructor
    }

    public static FragmentDashboardMove newInstance(String param1, String param2) {
        FragmentDashboardMove fragment = new FragmentDashboardMove();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_move, container, false);

        temperatureText = (TextView) view.findViewById(R.id.temperature_data);
        vSolarText = (TextView) view.findViewById(R.id.vSolar_data);
        batteryLevelText = (TextView) view.findViewById(R.id.battery_level);

        accelerometerTextX = (TextView) view.findViewById(R.id.accelerometer_x_move);
        accelerometerTextY = (TextView) view.findViewById(R.id.accelerometer_y_move);
        accelerometerTextZ = (TextView) view.findViewById(R.id.accelerometer_z_move);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }


    public void updateData(int temp, int vsolar, int batteryLevel, float accelerometerX, float accelerometerY, float accelerometerZ)
    {
        float temperature = (float)temp / 4;

        if(temperatureText != null)
            temperatureText.setText(Float.toString(temperature) + " °C");
        if(vSolarText != null)
            vSolarText.setText(Integer.toString(vsolar));
        if(batteryLevelText != null)
            batteryLevelText.setText(Integer.toString(batteryLevel) + " %");


        if(accelerometerTextX != null)
            accelerometerTextX.setText(Float.toString(accelerometerX) + " mg");

        if(accelerometerTextY != null)
            accelerometerTextY.setText(Float.toString(accelerometerY) + " mg");

        if(accelerometerTextZ != null)
            accelerometerTextZ.setText(Float.toString(accelerometerZ) + " mg");
    }


    public interface OnFragmentInteractionListener {

    }
}
