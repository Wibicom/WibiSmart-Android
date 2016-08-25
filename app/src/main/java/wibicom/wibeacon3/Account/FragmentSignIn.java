package wibicom.wibeacon3.Account;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import wibicom.wibeacon3.R;
import wibicom.wibeacon3.VolleySingleton;


public class FragmentSignIn extends Fragment {

    TextView usernameText;
    TextView passwordText;
    Button button;
    private OnFragmentSignInListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        usernameText = (TextView) view.findViewById(R.id.username);
        passwordText = (TextView) view.findViewById(R.id.password);
        button = (Button) view.findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onSumbitButtonPressed(view);

            }
        } );
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentSignInListener) {
            mListener = (OnFragmentSignInListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentSignInListener) {
            mListener = (OnFragmentSignInListener) context;
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

    private void onSumbitButtonPressed(final View view)
    {
        final String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        final String URL = "http://192.168.0.111:8000/api-token-auth/";//"http://192.168.0.101:8000/api-token-auth/";//"http://192.168.43.98:8000/api-token-auth/";//"http://192.168.0.141:8000/api-token-auth/";//;//http://192.168.0.141:8000/api-token-auth/";

        // Send post request to the web app with credentials and receive authentication token.
        HashMap<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(credentials),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.get("token").toString();
                            VolleySingleton.getInstance(view.getContext()).setAuthenticationToken(token);

                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());//getActivity().getSharedPreferences("ACCOUNT_PREF", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("authToken", token);
                            editor.putString("username", username);
                            editor.commit();
                            mListener.onSignIn();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                displayMessage("Nope, try again");
            }
        });

        VolleySingleton.getInstance(getView().getContext()).addToRequestQueue(req);
        //SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

    }

    private void displayMessage(String message)
    {
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }


    public interface OnFragmentSignInListener {
        void onSignIn();

    }
}
