package wibicom.wibeacon3.Account;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import wibicom.wibeacon3.R;
import wibicom.wibeacon3.VolleySingleton;

public class AccountActivity extends AppCompatActivity implements FragmentSignIn.OnFragmentSignInListener, FragmentAccountInfo.OnFragmentAccountInfoListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Account");

        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("authToken", "notconnected").equals("notconnected"))
            getFragmentManager().beginTransaction().replace(R.id.fragment_account_container, new FragmentSignIn()).commit();
        else
            getFragmentManager().beginTransaction().replace(R.id.fragment_account_container, new FragmentAccountInfo()).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onSignIn()
    {
        getFragmentManager().beginTransaction().replace(R.id.fragment_account_container, new FragmentAccountInfo()).commit();
    }

    public void onSignOut()
    {
        getFragmentManager().beginTransaction().replace(R.id.fragment_account_container, new FragmentSignIn()).commit();
    }


}

