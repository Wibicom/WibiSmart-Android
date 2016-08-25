package wibicom.wibeacon3.Account;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import wibicom.wibeacon3.R;


public class FragmentAccountInfo extends Fragment {

    private OnFragmentAccountInfoListener mListener;
    private TextView usernameText;
    private Button button;
    SharedPreferences sharedPref;

    public FragmentAccountInfo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_info, container, false);

        button = (Button) view.findViewById(R.id.sign_out_button);
        usernameText = (TextView) view.findViewById(R.id.account_username);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String username = sharedPref.getString("username", "Username");
        usernameText.setText(username);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                sharedPref.edit().putString("authToken", "notconnected").apply();
                sharedPref.edit().putString("username", "username");
                mListener.onSignOut();

            }
        } );

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentAccountInfoListener) {
            mListener = (OnFragmentAccountInfoListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentAccountInfoListener) {
            mListener = (OnFragmentAccountInfoListener) context;
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


    public interface OnFragmentAccountInfoListener {
        void onSignOut();

    }

}
