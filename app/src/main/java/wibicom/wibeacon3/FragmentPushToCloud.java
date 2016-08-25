package wibicom.wibeacon3;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

/**
 * @desc This class is a fragment that hold the logic of the push to cloud dialog
 * @author Olivier Tessier-Lariviere
 */
public class FragmentPushToCloud extends Fragment {

    private OnPushToCloudInteractionListener mListener;

    private Switch switchCloud;
    static boolean switchState = false;

    public FragmentPushToCloud() {
        // Required empty public constructor
    }


    public static FragmentPushToCloud newInstance(boolean switchOn) {
        FragmentPushToCloud fragment = new FragmentPushToCloud();
        switchState = switchOn;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_push_to_cloud, container, false);
        Button buttonOk = (Button)view.findViewById(R.id.button_ok);
        switchCloud = (Switch)view.findViewById(R.id.switch_cloud);
        switchCloud.setChecked(switchState);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finnish();
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPushToCloudInteractionListener) {
            mListener = (OnPushToCloudInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPushToCloudInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity context)
    {
        super.onAttach(context);
        if (context instanceof OnPushToCloudInteractionListener) {
            mListener = (OnPushToCloudInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPushToCloudInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void finnish()
    {
        mListener.onSwitchInteraction(switchCloud.isChecked());
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }



    public interface OnPushToCloudInteractionListener {
        void onSwitchInteraction(boolean state);
    }
}
