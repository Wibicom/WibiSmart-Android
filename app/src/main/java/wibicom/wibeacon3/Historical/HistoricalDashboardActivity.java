package wibicom.wibeacon3.Historical;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    public void dataReady(HashMap<String ,String> csvMap) {
        fragmentHistoricalDashboard.renderGraphs(csvMap);
    }

}
