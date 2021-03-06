package wibicom.wibeacon3.Historical;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.HashMap;

import wibicom.wibeacon3.DataHandler;
import wibicom.wibeacon3.R;

/**
 * Created by Michael Vaquier on 2017-07-20.
 */

public class HistoricalDashboardActivity extends AppCompatActivity {

    private static HistoricalDashboardActivity ourInstance;


    FragmentHistoricalDashboard fragmentHistoricalDashboard = new FragmentHistoricalDashboard();

    public static HistoricalDashboardActivity getInstance() { return ourInstance; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ourInstance = this;
        setContentView(R.layout.activity_historical_dashboard);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Historical Dashboard");

        getFragmentManager().beginTransaction().replace(R.id.fragment_historical_dashboard_container, fragmentHistoricalDashboard).commit();

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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getFragmentManager().beginTransaction().replace(R.id.fragment_historical_dashboard_container, fragmentHistoricalDashboard).commit();
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            fragmentHistoricalDashboard.hideInputs(false);
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fragmentHistoricalDashboard.hideInputs(true);
        }
    }

    public void dataReady(String type, HashMap<String ,String> csvMap) {
        switch (type) {
            case "weather/CO2":
                fragmentHistoricalDashboard.renderWeatherCO2(csvMap);
                break;
            case "battery/light/gases":
                fragmentHistoricalDashboard.renderLightBatteryGases(csvMap);
                break;
            case "accel":
                fragmentHistoricalDashboard.renderAccel(csvMap);
                break;
            case "RSSI":
                fragmentHistoricalDashboard.renderRSSI(csvMap);
        }
    }

    public FragmentHistoricalDashboard getFragmentHistoricalDashboard() { return fragmentHistoricalDashboard; }

}
