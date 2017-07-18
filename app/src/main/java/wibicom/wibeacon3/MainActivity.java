package wibicom.wibeacon3;


import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;


import java.lang.String;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.TimeZone;
import java.util.UUID;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cloudant.sync.documentstore.AttachmentException;
import com.cloudant.sync.documentstore.ConflictException;
import com.cloudant.sync.documentstore.DocumentBodyFactory;
import com.cloudant.sync.documentstore.DocumentNotFoundException;
import com.cloudant.sync.documentstore.DocumentRevision;
import com.cloudant.sync.documentstore.DocumentStore;
import com.cloudant.sync.documentstore.DocumentStoreException;
import com.cloudant.sync.query.Query;
import com.cloudant.sync.query.QueryResult;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

//import org.altbeacon.beacon.*;
import org.altbeacon.bluetooth.BleAdvertisement;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.json.JSONException;
import org.json.JSONObject;


import wibicom.wibeacon3.Account.AccountActivity;
import wibicom.wibeacon3.Dashboard.FragmentDashboard;
import wibicom.wibeacon3.Dashboard.FragmentDashboardEnviro;
import wibicom.wibeacon3.Dashboard.FragmentDashboardMove;
import wibicom.wibeacon3.Scanner.FragmentScanner;
import wibicom.wibeacon3.Settings.FragmentSettings;
import wibicom.wibeacon3.Settings.FragmentSettingsEnviro;
import wibicom.wibeacon3.Settings.FragmentSettingsMove;


/**
 * @desc This class is the main activity of the app. It holds the logic for
 * the scanner, the dashboard and the device settings.
 * @author Olivier Tessier-Lariviere
 */
@TargetApi(21)
public class MainActivity extends AppCompatActivity implements /*BeaconConsumer, RangeNotifier,*/ FragmentScanner.OnListFragmentInteractionListener, FragmentLocalStorage.OnLocalStorageInteractionListener, FragmentPushToCloud.OnPushToCloudInteractionListener, FragmentSettingsEnviro.OnSettingsEnviroListener, FragmentSettingsMove.OnSettingsMoveListener {//, FragmentDashboardMove.OnFragmentInteractionListener {

    private static MainActivity ourInstance;

    //private BeaconManager beaconManager;
    private DrawerLayout mDrawerLayout;
    //Collection<Beacon> beaconsInRange;

    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeService mBluetoothLeService;

    Queue<BluetoothGattCharacteristic> serviceQueue;

    private boolean isScanStarted = false;
    private boolean isConnected = false;
    private String isConnectingProcessWith = null;

    FragmentScanner fragmentScanner;
    FragmentDashboard fragmentDashboard;
    FragmentDashboardMove fragmentDashboardMove;
    FragmentDashboardEnviro fragmentDashboardEnviro;

    FragmentSettings fragmentSettings;
    FragmentSettingsMove fragmentSettingsMove;
    FragmentSettingsEnviro fragmentSettingsEnviro;

    BluetoothDevice connectedDevice;
    List<BluetoothGatt> disconnectedDevicesGattList = new ArrayList<BluetoothGatt>();
    List<BluetoothDevice> connectedDevices = new ArrayList<>();
    List<SensorData> sensorDataList = new ArrayList<>();

    int connectedDevicePosition;
    int attemptConnectionPosition;

    boolean isTransmittingToCloud;


    Adapter viewPagerAdapter;
    ViewPager viewPager;


    private GoogleApiClient client;

    private MqttHandler mMqttHandler;

    DataHandler myDataHandler;
    boolean isStoringLocally;


    private final static String TAG = MainActivity.class.getName();

    public static final MainActivity getInstance() {
        return ourInstance;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ourInstance = this;
        setContentView(R.layout.activity_ranging );

        setupUi();

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        //startService(gattServiceIntent);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        mMqttHandler = mMqttHandler.getInstance(this);

        myDataHandler = DataHandler.getInstance();
    }



    private void setupUi() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24px);
        ab.setDisplayHomeAsUpEnabled(true);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(5);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_bluetooth_searching_white_24px);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_dashboard_white_24px);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_settings_white_24px);
    }

    private void setupViewPager(ViewPager viewPager) {
        fragmentScanner = new FragmentScanner();

        fragmentDashboard = new FragmentDashboard();
        fragmentDashboardMove = new FragmentDashboardMove();
        fragmentDashboardEnviro = new FragmentDashboardEnviro();

        fragmentSettings = new FragmentSettings();
        fragmentSettingsMove = new FragmentSettingsMove();
        fragmentSettingsEnviro = new FragmentSettingsEnviro();

        viewPagerAdapter = new Adapter(getFragmentManager());

        viewPagerAdapter.setFragmentScanner(fragmentScanner);
        viewPagerAdapter.setFragmentDashboard(fragmentDashboard);
        viewPagerAdapter.setFragmentSetting(fragmentSettings);
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void setupBluetooth()
    {
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        filters = new ArrayList<ScanFilter>();
    }

    private void setMoveUi()
    {
        if(!fragmentDashboard.isDashboardMove)
            fragmentDashboard.setDashboardMove(fragmentDashboardMove);

        if(!fragmentSettings.isSettingsMove)
            fragmentSettings.setSettingsMove(fragmentSettingsMove);
    }

    private void setEnviroUi()
    {
        if(!fragmentDashboard.isDashboardEnviro)
            fragmentDashboard.setDashboardEnviro(fragmentDashboardEnviro);

        if(!fragmentSettings.isSettingsEnviro)
            fragmentSettings.setSettingsEnviro(fragmentSettingsEnviro);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(false);
                        int id = menuItem.getItemId();
                        if(id == R.id.account_drawer)
                        {
                            Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
                            startActivity(intent);
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_push_to_cloud:
                Fragment fragmentPushToCloud =  FragmentPushToCloud.newInstance(isTransmittingToCloud);
                getFragmentManager().beginTransaction().add(android.R.id.content, fragmentPushToCloud,"fragmentPushToCloud").commit();
                break;
            case R.id.action_store_locally:
                Fragment fragmentStoreLocally = FragmentLocalStorage.newInstance(isStoringLocally);
                getFragmentManager().beginTransaction().add(android.R.id.content, fragmentStoreLocally,"fragmentStoreLocally").commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            mBluetoothLeService.initialize();
            startScan();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            fragmentScanner.updateList(result.getDevice(), result.getRssi());
        }
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {

            }
        }

        @Override
        public void onScanFailed(int errorCode) {
        }
    };

    public void startScan()//View view)
    {
        if (!isScanStarted) {
            mBluetoothAdapter.getBluetoothLeScanner().startScan(filters, settings, leScanCallback);
            isScanStarted = true;
        } else {
            mBluetoothLeScanner.stopScan(leScanCallback);
            mBluetoothLeScanner.startScan(leScanCallback);
        }
    }



    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mBluetoothLeService.discoverServices();
                isConnected = true;
                Snackbar.make(findViewById(android.R.id.content), "Connected!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                fragmentScanner.onConnect(connectedDevice, attemptConnectionPosition);
                attemptConnectionPosition = 0;



            }
            else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                if(disconnectedDevicesGattList.size()  > 0) {
                    BluetoothGatt thisDisconnectionGatt = disconnectedDevicesGattList.remove(0);
                    if(thisDisconnectionGatt.getDevice().getAddress().equals(isConnectingProcessWith)) {
                        isConnectingProcessWith = null;
                    }
                    Log.d(TAG, "device " + thisDisconnectionGatt.getDevice().getName() + " has been notified as disconnected.");
                    isConnected = false;
                    Snackbar.make(findViewById(android.R.id.content), thisDisconnectionGatt.getDevice().getName() + " Disconnected", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    ArrayList<BluetoothDevice> connectedList = (ArrayList)connectedDevices;
                    int position = connectedList.indexOf(thisDisconnectionGatt.getDevice());
                    if(position != -1) {
                        connectedDevices.remove(position);
                        sensorDataList.remove(position);
                        mBluetoothLeService.deleteGattAtPosition(position);
                        mBluetoothLeService.clearWritingQueue();
                        fragmentScanner.onDisconnect(thisDisconnectionGatt.getDevice());

                        if (connectedList.size() > 0) {
                            int newPos = connectedList.indexOf(connectedDevice);
                            if (connectedDevicePosition == position) {
                                connectedDevicePosition = 0;
                                connectedDevice = connectedDevices.get(0);
                                mBluetoothLeService.setSelectedGatt(0);
                                fragmentScanner.onSelect(0);
                                updateDashboard();
                            } else if (newPos != -1) {
                                connectedDevicePosition = newPos;
                                connectedDevice = connectedDevices.get(newPos);
                                mBluetoothLeService.setSelectedGatt(newPos);
                                fragmentScanner.onSelect(newPos);
                                updateDashboard();
                            }
                        }
                        else {
                            fragmentDashboard.setDashboardInitial(fragmentDashboardEnviro, fragmentDashboardMove);
                            fragmentSettings.setSettingsInitial(fragmentSettingsMove, fragmentSettingsEnviro);
                        }
                    }
                }
            }
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Snackbar.make(findViewById(android.R.id.content), "Services discovered", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                fragmentDashboardEnviro.hideSensors();
                fragmentSettingsEnviro.syncSettings();
            }
            else if(BluetoothLeService.ACTION_GATT_NOTIFY.equals(action)) {
                updateDashboard();
            }
            else if(BluetoothLeService.ACTION_GATT_DONE_CONNECTING.equals(action))
            {
                fragmentDashboardEnviro.hideSensors();
                fragmentSettingsEnviro.syncSettings();
                isConnectingProcessWith = null;
                Snackbar.make(findViewById(android.R.id.content), mBluetoothLeService.getDeviceName() + " ready to be used.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    };

    private void updateDashboard()
    {
        Log.d(TAG, "entering .updateDasboard()");
        if(sensorDataList.size() > connectedDevicePosition) {
            SensorData sensor = sensorDataList.get(connectedDevicePosition);
            if (sensor != null) {
                char[] modelNumberString = sensor.getModelNumberString();
                if (isCharArrayEquals(modelNumberString, mBluetoothLeService.MOVE_MODEL_NUMBER_STRING)) {
                    Log.d(TAG, ".updateDashboard() device " + sensor.getLocalName() + " calling setMoveUi()");
                    setMoveUi();

                    fragmentDashboardMove.updateData(sensor.getTemperatureNordic(), sensor.getvSolarNordic(), sensor.getBatteryLevel(),
                            sensor.getAccelerometerX(), sensor.getAccelerometerY(), sensor.getAccelerometerZ());

                } else if (isCharArrayEquals(modelNumberString, mBluetoothLeService.ENVIRO_MODEL_NUMBER_STRING) || isCharArrayEquals(modelNumberString, mBluetoothLeService.ENVIRO_MODEL_NUMBER_STRING_TEMP)) {
                    Log.d(TAG, ".updateDashboard() device " + sensor.getLocalName() + " calling setEnviroUi()");
                    mBluetoothLeService.readRemoteRssi();
                    setEnviroUi();

                    fragmentDashboardEnviro.updateData(sensor.getLocalName(), sensor.getTemperatureEnviro(), sensor.getPressureEnviro(), sensor.getHumidityEnviro(),
                            sensor.getAccelerometerX(), sensor.getAccelerometerY(), sensor.getAccelerometerZ(),
                            sensor.getBatteryLevel(), sensor.getRssi(), sensor.getLightEnviro(), sensor.getCO2Enviro());
                }
            } else {
                Log.d(TAG, ".updateDashboard() sensorData was not found.");
            }
        }
    }

    private void updateSettings()
    {

    }


    private boolean isCharArrayEquals(char[] charArray1, char[] charArray2)
    {
        if(charArray1.length != charArray2.length)
            return false;

        for(int i = 0; i < charArray1.length; i++) {
            if (charArray1[i] != charArray2[i])
                return false;
        }
        return true;
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_NOTIFY);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_READ);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DONE_CONNECTING);
        return intentFilter;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupBluetooth();
//        beaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
//        // Detect the main identifier (UID) frame:
//        beaconManager.getBeaconParsers().add(new BeaconParser().
//                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
//        // Detect the telemetry (TLM) frame:
//        beaconManager.getBeaconParsers().add(new BeaconParser().
//                setBeaconLayout("x,s:0-1=feaa,m:2-2=00,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));
//        // Detect the URL frame:
//        beaconManager.getBeaconParsers().add(new BeaconParser().
//                setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));
//        // Detect iBeacon format
//        beaconManager.getBeaconParsers().add(new BeaconParser().
//                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
//        // Detect some format
//        beaconManager.getBeaconParsers().add(new BeaconParser().
//                setBeaconLayout("m:0-1=A700,i:4-19,i:20-21,i:22-23,p:24-24"));
//
//        beaconManager.bind(this);
    }

    //@Override
    //public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

//        beaconsInRange = beacons;
//        String beaconName = "Unknown";
//
//        for (final Beacon oneBeacon : beaconsInRange) {
//            if (oneBeacon.getServiceUuid() == 0xfeaa) {
//
//                switch (oneBeacon.getBeaconTypeCode()) {
//                    case 0x00:
//                        logToDisplay("Beacon type: Eddystone UID", R.id.beaconType);
//                        logToDisplay("Namespace ID: " + oneBeacon.getId1() + "\nInstance ID: " + oneBeacon.getId2(), R.id.info);
//                        beaconName = "Eddystone UID";
//                        break;
//                    case 0x10:
//                        logToDisplay("Beacon type: Eddystone URL", R.id.beaconType);
//                        String url = UrlBeaconUrlCompressor.uncompress(oneBeacon.getId1().toByteArray());
//                        logToDisplay("URL: " + url, R.id.info);
//                        beaconName = "Eddystone URL";
//                        break;
//                    case 0x20:
//                        // logToDisplay("Beacon type: Eddystone TLM", R.id.beaconType);
//                        beaconName = "Eddystone TLM";
//                        break;
//                    default: //logToDisplay("Beacon type: Bad Eddystone", R.id.beaconType);
//                        break;
//                }
//            }
//            else if (oneBeacon.getManufacturer() == 0x004c) {
//                logToDisplay("Beacon type: Apple iBeacon", R.id.beaconType);
//                logToDisplay("UUID: " + oneBeacon.getId1() + " \nMajor: " + oneBeacon.getId2() +  " \nMinor: " + oneBeacon.getId3(), R.id.info);
//                beaconName = "iBeacon";
//            }
//            else if(oneBeacon.getManufacturer() == 0x00A7)
//            {
//                logToDisplay("Beacon type: Wibeacon (light)", R.id.beaconType);
//
//
//
//                byte lightData[] = oneBeacon.getId1().toByteArray();
//
//                int vBat = (((lightData[1] & 0xFF) << 8) | lightData[0] & 0xFF);
//                double vbatMv = (vBat - 16.626)/274.25;
//                int vSolar = ((lightData[3] & 0xFF) << 8) | (lightData[2] & 0xFF);
//
//                logToDisplay("UUID: " + oneBeacon.getId1() + "\n\nVbat: " + vbatMv + "\nVsolar: " + vSolar, R.id.info);
//            }
//
//            if (oneBeacon.getExtraDataFields().size() > 0) {
//                long telemetryVersion = oneBeacon.getExtraDataFields().get(0);
//                long batteryMilliVolts = oneBeacon.getExtraDataFields().get(1);
//                long temp = oneBeacon.getExtraDataFields().get(2);
//                long pduCount = oneBeacon.getExtraDataFields().get(3);
//                long uptime = oneBeacon.getExtraDataFields().get(4);
//                // logToDisplay("Distance: " + temp + " deg", R.id.temp);
//            }
//
//            logToDisplay("Distance: " + oneBeacon.getDistance() + " m", R.id.distance);
//            sendNotification(beaconName);
//
//        }
  // }

    //@Override
    //public void onBeaconServiceConnect() {
//        Identifier myBeaconNamespaceId = null;//Identifier.parse("0x2f234454f4911ba9ffa6");
//        Identifier myBeaconInstanceId = null; //Identifier.parse("0x000000000001");
//        final Region region = new Region("all-beacons-region", myBeaconNamespaceId, myBeaconInstanceId, null);//Identifier.parse("00EEAAAABBBBCCCCDDDDEEEE0102030405060000"), null, null);
//
//        beaconManager.setRangeNotifier(this);
//        try {
//            beaconManager.startRangingBeaconsInRegion(region);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    //}


    private void logToDisplay(final String line, final int id) {
        runOnUiThread(new Runnable() {
            public void run() {
                TextView textView = (TextView) MainActivity.this.findViewById(id);
                textView.setText(line);
            }
        });
    }

//    public void displayBeacons(View view) {
//        logToDisplay("Beacon type: ", R.id.beaconType);
//        logToDisplay("Info: ", R.id.info);
//        logToDisplay("Distance: ", R.id.distance);
//
//    }
//
//    private void sendNotification(String notificationContent)
//    {
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentTitle("Beacon is near")
//                        .setContentText(notificationContent);
//        // Creates an explicit intent for an Activity in your app
//        Intent resultIntent = new Intent(this, MainActivity.class);
//
//        // The stack builder object will contain an artificial back stack for the
//        // started Activity.
//        // This ensures that navigating backward from the Activity leads out of
//        // your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        // Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(MainActivity.class);
//        // Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        int mId = 0;
//        // mId allows you to update the notification later on.
//        mNotificationManager.notify(mId, mBuilder.build());
//
//    }
//
//    public void switchToSettings(View view) {
//        Intent intent = new Intent(this, SettingActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    public void switchToMenu(View view) {
//        Intent intent = new Intent(this, MenuActivity.class);
//        startActivity(intent);
//        finish();
//    }

    @Override
    public void onListFragmentInteraction(String localName, String adress, int position) {
        Log.d(TAG, "entering .onListFragmentIteraction()");
        //BluetoothDevice bluetoothDevice = deviceList.get(pos);
        // if(!isConnected)
        // {
        if(isConnectingProcessWith == null) {
            Snackbar.make(findViewById(android.R.id.content), "Connecting to " + localName + "...", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            Log.d(TAG, "entering .onListFragmentIneraction() connecting to " + localName);
            mBluetoothLeScanner.stopScan(leScanCallback);
            mBluetoothLeService.connect(adress);
            isConnectingProcessWith = adress;
            attemptConnectionPosition = position;
            //}
        }
        else {
            Snackbar.make(findViewById(android.R.id.content), "Wait till all your devices are ready to be used.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            Log.d(TAG, ".onListFragmentIteraction() was not ready");
        }

    }

    /*@Override
    public void onConnectedListFragmentInteraction(BluetoothDevice device, int position) {
        Log.d(TAG, "entering onConnectedListFragmentIteraction() for device " + device.getName() + " at position "+ position + ".");
        connectedDevicePosition = position;
        connectedDevice = connectedDevices.get(position);
        mBluetoothLeService.setSelectedGatt(position);
        fragmentScanner.onSelect(position);
        Snackbar.make(findViewById(android.R.id.content), "You have now selected " + device.getName() + "!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }*/

    @Override
    public void onConnectedListFragmentInteraction(String localName, String adress) {
        Log.d(TAG, "entering onConnectedListFragmentIteraction() for device " + localName + " with adress " + adress);
        if (isConnectingProcessWith == null) {
            int position = 0;
            for (BluetoothDevice thisDevice : connectedDevices) {
                if (thisDevice.getAddress().equals(adress) && thisDevice.getName().equals(localName)) {
                    position = connectedDevices.indexOf(thisDevice);
                    break;
                }
            }
            connectedDevicePosition = position;
            connectedDevice = connectedDevices.get(position);
            mBluetoothLeService.setSelectedGatt(position);
            fragmentScanner.onSelect(position);
            Snackbar.make(findViewById(android.R.id.content), "You have now selected " + localName + "!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            fragmentDashboardEnviro.hideSensors();
            fragmentSettingsEnviro.syncSettings();
            updateDashboard();
        }
        else {
            Snackbar.make(findViewById(android.R.id.content), "Wait till all your devices are ready to be used.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            Log.d(TAG, ".onConnectedListFragmentIteraction() was not ready");
        }
    }

    @Override
    public void onDisconnectionRequest(String localName, String adress) {
        Log.d(TAG, "entering onDisconnectionRequest() for device " + localName);
        mBluetoothLeService.disconnect(localName, adress);
    }

    @Override
    public void writeCharacteristic(UUID uuid, byte[] value)
    {
        Log.d(TAG, "entering writeCharacteristic() for uuid " + uuid.toString() + " and value " + value[0] + ".");
        mBluetoothLeService.addToWriteQueue(uuid, value);
    }


    @Override
    public void refreshScan() {
        startScan();
    }

    @Override
    public void onSwitchInteraction(boolean switchOn) {
        isTransmittingToCloud = switchOn;

    }

    @Override
    public void onSwitchInteractionLocalStorage(boolean switchOn) {
        isStoringLocally = switchOn;
    }



    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();

        private Fragment fragmentScanner;
        private Fragment fragmentDashboard;
        private Fragment fragmentSetting;
        boolean isDashboardChanged = false;

        private final List<String> mFragmentTitles = new ArrayList<>();
        private final FragmentManager fm;

        public Adapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public void setFragmentScanner(Fragment fragment)
        {
            fragmentScanner = fragment;
        }
        public void setFragmentDashboard(Fragment fragment)
        {
            fragmentDashboard = fragment;
        }
        public void setFragmentSetting(Fragment fragment)
        {
            fragmentSetting = fragment;
        }

        @Override
        public Fragment getItem(int position) {

            switch(position)
            {
                case 0: return fragmentScanner;
                case 1: return fragmentDashboard;
                case 2: return fragmentSetting;
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitles.get(position);
//        }
    }

    private class UploadDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                /*return*/ uploadToUrl();
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
            return "hello";
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

        }
    }

    private void uploadToUrl() throws IOException
    {
//        OutputStream outputStream = null;
//
//
//        try {
//            URL url = new URL("http://posttestserver.com/post.php");//"http://www.wibicom.com/");
//            byte[] byteArray = {0x0, 0x1, 0x2, 0x3, 0x4};
//
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(10000 /* milliseconds */);
//            conn.setConnectTimeout(15000 /* milliseconds */);
//            //conn.setDoInput(true);
//            conn.setDoOutput(true);
//            //conn.setRequestMethod("POST");
//            conn.connect();
//            int response = conn.getResponseCode();
//
//            outputStream = conn.getOutputStream();
//            outputStream.write(byteArray);
//
//
//        } finally {
//            if(outputStream != null)
//            {
//                outputStream.close();
//            }
//        }



    }

    public void newConnection(BluetoothDevice device) {
        Log.d(TAG, "entering newConnection() for device " + device.getName());
        connectedDevicePosition = connectedDevices.size();
        connectedDevice = device;
        connectedDevices.add(connectedDevicePosition, device);
        sensorDataList.add(connectedDevicePosition, new SensorData(device.getName(), device.getAddress()));
    }

    public SensorData getSensorDataWithAdress(String adress, String localName) {
        for(SensorData sensorDataInstance : sensorDataList) {
            if(sensorDataInstance.getLocalName().equals(localName) && sensorDataInstance.getAdress().equals(adress)) {
                return sensorDataInstance;
            }
        }
        return null;
    }

    public void connectIot() {
        Log.d(TAG, "entering .connectIoT()");
        Log.d(TAG, "MQTT connecting...");
        mMqttHandler.connect(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d(TAG, "MQTT connected");
                Snackbar.make(findViewById(android.R.id.content), "Connected to Watson IoT!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d(TAG, "MQTT connection failure");
                exception.printStackTrace();
                Snackbar.make(findViewById(android.R.id.content), "Connection to Watson IoT failed...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void displaySnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public List<SensorData> getSensorDataList() {
        return sensorDataList;
    }

    public int getConnectedDevicePosition() {
        return connectedDevicePosition;
    }

    public boolean getIsTransmittingToCloud() {
        return isTransmittingToCloud;
    }

    public boolean getIsStoringLocally() { return isStoringLocally; }

    public String getIsConnectingProcessWith() { return isConnectingProcessWith; }

    public void addDisconnectedDeviceToGattList(BluetoothGatt gatt) {
        disconnectedDevicesGattList.add(gatt);
    }


}

