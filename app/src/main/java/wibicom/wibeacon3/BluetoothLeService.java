package wibicom.wibeacon3;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;

import com.cloudant.sync.documentstore.AttachmentException;
import com.cloudant.sync.documentstore.ConflictException;
import com.cloudant.sync.documentstore.DocumentBodyFactory;
import com.cloudant.sync.documentstore.DocumentRevision;
import com.cloudant.sync.documentstore.DocumentStoreException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TimeZone;
import java.util.UUID;


/**
 * @desc This class is a sevice that manage all the bluetooth operations for the MainActivity.
 * @author Olivier Tessier-Lariviere
 */
public class BluetoothLeService extends Service {

    private static final String ADV_SERVICE_UUID = "00001520-1212-EFDE-1523-785FEABCD123";
    private static final String ADV_MODE_CHAR_UUID = "00003020-1212-efde-1523-785feabcd123";
    private static final String ADV_INTERVAL_CHAR_UUID = "00003021-1212-efde-1523-785feabcd123";
    private static final String ADV_POWER_CHAR_UUID = "00003022-1212-efde-1523-785feabcd123";
    private static final String ADV_SMART_CHAR_UUID = "00003023-1212-efde-1523-785feabcd123";
    private static final String ADV_PAYLOAD_CHAR_UUID = "00003024-1212-efde-1523-785feabcd123";

    private static final String BATTERY_SERVICE_UUID = "0000180F-0000-1000-8000-00805F9B34FB";
    private static final String BATTERY_LEVEL_CHAR_UUID = "00002a19-0000-1000-8000-00805f9b34fb";

    private static final String SENSORS_SERVICE_UUID = ("00001620-1212-efde-1523-785feabcd123");
    private static final String NORDIC_SENSOR_TEMPERATURE_DATA_UUID = ("00003120-1212-efde-1523-785feabcd123");
    private static final String NORDIC_SENSOR_TEMPERATURE_DESCRIPTOR_UUID = ("00003120-1212-efde-1523-785feabcd123");
    private static final String NORDIC_SENSOR_VSOLAR_DATA_UUID = ("00003121-1212-efde-1523-785feabcd123");
    private static final String NORDIC_SENSOR_VSOLAR_DESCRIPTOR_UUID = ("00003121-1212-efde-1523-785feabcd123");
    private static final String NORDIC_SENSOR_ACCELEROMETER_DATA_UUID = ("00003122-1212-efde-1523-785feabcd123");

    private static final String TI_BAROMETER_SERVICE_UUID = "000AA40-0000-1000-8000-00805f9b34fb";
    private static final String TI_BAROMETER_DATA_CHAR_UUID = "0000aa41-0000-1000-8000-00805f9b34fb";
    private static final String TI_BAROMETER_CONF_CHAR_UUID = "0000aa42-0000-1000-8000-00805f9b34fb";
    private static final String TI_BAROMETER_CAL_CHAR_UUID = "0000aa42-0000-1000-8000-00805f9b34fb";
    private static final String TI_BAROMETER_PERI_CHAR_UUID = "0000aa42-0000-1000-8000-00805f9b34fb";

    private static final String SPEC_SERVICE_UUID = "0000bb40-0000-1000-8000-00805f9b34fb";
    private static final String SPEC_DATA_CHAR_UUID = "0000bb41-0000-1000-8000-00805f9b34fb";
    private static final String SPEC_CONF_CHAR_UUID = "0000bb42-0000-1000-8000-00805f9b34fb";
    private static final String SPEC_PEROÏOD_CHAR_UUID = "0000bb44-0000-1000-8000-00805f9b34fb";


    private static final String CO2_SERVICE_UUID = "0000cc30-0000-1000-8000-00805f9b34fb";
    private static final String CO2_DATA_CHAR_UUID = "0000cc31-0000-1000-8000-00805f9b34fb";
    private static final String CO2_CONF_CHAR_UUID = "0000cc32-0000-1000-8000-00805f9b34fb";
    private static final String CO2_PEROÏOD_CHAR_UUID = "0000cc34-0000-1000-8000-00805f9b34fb";

    private static final String MODEL_NUMBER_STRING_CHAR_UUID = "00002a24-0000-1000-8000-00805f9b34fb";
    private static final String TI_ACCELEROMETER_DATA_CHAR_UUID = "0000aa81-0000-1000-8000-00805f9b34fb";




    public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DONE_CONNECTING = "ACTION_GATT_DONE_CONNECTING";
    public final static String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_GATT_NOTIFY = "ACTION_GATT_NOTIFY";
    public final static String ACTION_GATT_READ = "ACTION_GATT_READ";
    public final static String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "EXTRA_DATA";


    public final static char[] MOVE_MODEL_NUMBER_STRING = {'0', '0', '0', '1'};
    public final static char[] ENVIRO_MODEL_NUMBER_STRING = {'E', 'n', 'v', 'i', 'r', 'o'};
    public final static char[] ENVIRO_MODEL_NUMBER_STRING_TEMP = {'M', 'o', 'd', 'e', 'l', ' ', 'N', 'u', 'm', 'b', 'e', 'r'};

    boolean isBusyWritting = false;


    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private List<BluetoothGatt> mBluetoothGattList = new ArrayList<BluetoothGatt>();

    Queue<Pair<UUID, byte[]>> characteristicToWriteQueue = new LinkedList<>();
    Queue<BluetoothGattService> servicesQueue;

    Queue<BluetoothGattCharacteristic> characteristicQueue;

    Queue<BluetoothGattCharacteristic> characteristicSetNotifyQueue = new LinkedList<>();
    List<BluetoothDevice> deviceList;
    List<String> deviceNameList;

    public String getBluetoothAdress()
    {
        return mBluetoothGatt.getDevice().getAddress();
    }
    public String getDeviceName()
    {
        return mBluetoothGatt.getDevice().getName();
    }

    private final static String TAG = BluetoothLeService.class.getName();

    public int onStartCommand(Intent intent, int flags, int startId) {

        deviceList = new ArrayList<>();
        deviceNameList = new ArrayList<>();
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        return START_STICKY;
    }

    public void initialize() {

        if (mBluetoothManager == null)
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }


    public void readCharacteristics(BluetoothGattService service) {
        characteristicQueue = new LinkedList<>(service.getCharacteristics());
        if(!characteristicQueue.isEmpty())
            mBluetoothGatt.readCharacteristic(characteristicQueue.poll());
        else if(!servicesQueue.isEmpty())
            readCharacteristics(servicesQueue.poll());
    }

    public void writeAdvertisingCharacteristics() {
        BluetoothGattService advertisingService = mBluetoothGatt.getService(UUID.fromString(ADV_SERVICE_UUID));
        characteristicQueue = new LinkedList<>(advertisingService.getCharacteristics());
        if(!characteristicQueue.isEmpty()) {
            writeAdvertisingCharacteristic(characteristicQueue.poll());
        }
    }

    public void writeCharacteristicsDescriptor() {
        if(!characteristicSetNotifyQueue.isEmpty()) {
            BluetoothGattCharacteristic characteristic = characteristicSetNotifyQueue.poll();
            mBluetoothGatt.setCharacteristicNotification(characteristic, true);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
        else {
            broadcastUpdate(ACTION_GATT_DONE_CONNECTING);
        }
    }

    public void addToWriteQueue(UUID uuid, byte[] value)
    {
        Log.d(TAG, "entering addToWriteQueue() for uuid " + uuid.toString() + " and value " + value[0] + ".");
        characteristicToWriteQueue.add(new Pair(uuid, value));
        if(!isBusyWritting) {
            Log.d(TAG, "addToWriteQueue() not busy writing, calling writeNextCharacteristicInQueue()");
            writeNextCharacteristicInQueue();
        }
        else {
            if(characteristicQueue != null)
            Log.d(TAG, "addToWriteQueue() was busy writing, current Queue length " + characteristicToWriteQueue.size());
        }

    }

    private void writeNextCharacteristicInQueue()
    {
        Log.d(TAG, "entering writeNextCharacteristicInQueue()");
        if(!characteristicToWriteQueue.isEmpty())
        {
            UUID uuid = characteristicToWriteQueue.peek().first;
            byte[] value = characteristicToWriteQueue.poll().second;
            Log.d(TAG, "writing " + value[0] + " on uuid " + uuid.toString() + " for device " + mBluetoothGatt.getDevice().getName() + ".");
            BluetoothGattCharacteristic characteristic = findCharacteristic(uuid);
            if(characteristic != null)
            {
                characteristic.setValue(value);
                mBluetoothGatt.writeCharacteristic(characteristic);
            }

            isBusyWritting = true;
        }
        else
        {
            isBusyWritting = false;
        }
    }

    private BluetoothGattCharacteristic findCharacteristic(UUID uuid)
    {
        for(BluetoothGattService service : mBluetoothGatt.getServices())
            for(BluetoothGattCharacteristic aCharacteristic : service.getCharacteristics())
                if(aCharacteristic.getUuid().toString().equals(uuid.toString()))
                    return aCharacteristic;
        return null;
    }

    private void writeAdvertisingCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        if(MainActivity.getInstance().getSensorDataList().size() > MainActivity.getInstance().getConnectedDevicePosition()) {
            SensorData sensor = MainActivity.getInstance().getSensorDataList().get(MainActivity.getInstance().getConnectedDevicePosition());
            if(sensor != null) {
                if (characteristic.getUuid().toString().equals(ADV_MODE_CHAR_UUID)) {
                    characteristic.setValue(new byte[]{(byte) sensor.getBeaconType()});
                    mBluetoothGatt.writeCharacteristic(characteristic);
                } else if (characteristic.getUuid().toString().equals(ADV_INTERVAL_CHAR_UUID)) {
                    characteristic.setValue(new byte[]{(byte) sensor.getAdvertisingIntervals()});
                    mBluetoothGatt.writeCharacteristic(characteristic);
                } else if (characteristic.getUuid().toString().equals(ADV_POWER_CHAR_UUID)) {
                    characteristic.setValue(new byte[]{(byte) sensor.getTxPower()});
                    mBluetoothGatt.writeCharacteristic(characteristic);
                } else if (characteristic.getUuid().toString().equals(ADV_SMART_CHAR_UUID)) {
                    characteristic.setValue(new byte[]{(byte) sensor.getSmartPowerMode()});
                    mBluetoothGatt.writeCharacteristic(characteristic);
                } else if (characteristic.getUuid().toString().equals(ADV_PAYLOAD_CHAR_UUID)) {
                    characteristic.setValue(sensor.getAdvertisingPayload());
                    mBluetoothGatt.writeCharacteristic(characteristic);
                }
            }
        }
    }


    public List<BluetoothGattService> getSupportedGattServices() {
        return mBluetoothGatt.getServices();
    }

    public void connect(final String address) {

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        MainActivity.getInstance().newConnection(device);
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        mBluetoothGattList.add(mBluetoothGatt);
    }

    public void readRemoteRssi()
    {
        for(BluetoothGatt gatt: mBluetoothGattList) {
            gatt.readRemoteRssi();
        }
    }


    public void disconnect(String localName, String adress) {
        Log.d(TAG, "entering .disconnect() for device " + localName);
        BluetoothGatt gattToDisConnect = null;
        for(BluetoothGatt gatt : mBluetoothGattList) {
            if(gatt.getDevice().getName().equals(localName) && gatt.getDevice().getAddress().equals(adress)) {
                gattToDisConnect = gatt;
                Log.d(TAG, ".disconnect() found device to disconnect");
                break;
            }
        }
        if (gattToDisConnect != null)
        gattToDisConnect.disconnect();
    }

    public void discoverServices()
    {
        mBluetoothGatt.discoverServices();
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(newState == BluetoothGatt.STATE_CONNECTED)
            {
                if(MainActivity.getInstance().getIsConnectingProcessWith() != null && MainActivity.getInstance().getIsConnectingProcessWith().equals(gatt.getDevice().getAddress())) {
                    Log.d(TAG, "device " + gatt.getDevice().getName() + "state change to connected");
                    broadcastUpdate(ACTION_GATT_CONNECTED);
                }
                else {
                    Log.d(TAG, "device " + gatt.getDevice().getName() + " is not supposed to connect now" );
                    gatt.disconnect();
                }
            }
            else
            {
                Log.d(TAG, "device " + gatt.getDevice().getName() + "state change to DISconnected");
                MainActivity.getInstance().addDisconnectedDeviceToGattList(gatt);
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                servicesQueue = new LinkedList<>(mBluetoothGatt.getServices());
                MainActivity.getInstance().getSensorDataList().get(MainActivity.getInstance().getConnectedDevicePosition()).setConnecteddeviceGatt(gatt);
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);


                if(servicesQueue.size() > 0)
                {
                   readCharacteristics(servicesQueue.poll());
                }
            }
        }
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "entering .onCharacteristicWrite()");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, ".onCharacteristicWrite() successfully written characteristic " + characteristic.getUuid().toString() + " for device " + gatt.getDevice().getName() + ".");
                if(!characteristicQueue.isEmpty()) {
                    writeAdvertisingCharacteristic(characteristicQueue.poll());
                }
            }

            writeNextCharacteristicInQueue();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "entering .onCharacteristicRead() for device " + gatt.getDevice().getName() + " with adress " + gatt.getDevice().getAddress() + " for characteristic " + characteristic.getUuid() + ".");
                addCharacteristicNotifyQueue(characteristic);
                updateCharacteristicValue(gatt, characteristic);
            }
            // Read the next characteristic in the queue & broadcast when queue is empty.
            if(characteristicQueue.isEmpty())
            {
                if(servicesQueue.size() > 0)
                {
                    //readService(servicesQueue.poll());
                    readCharacteristics(servicesQueue.poll());
                }
                else
                {
                    broadcastUpdate(ACTION_GATT_NOTIFY);
                    writeCharacteristicsDescriptor();

                }
            }
            else
            {
                mBluetoothGatt.readCharacteristic(characteristicQueue.poll());
            }


        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
        {
            writeCharacteristicsDescriptor();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "entering .onCharacteristicChanged() for device " + gatt.getDevice().getName() + " with adress " + gatt.getDevice().getAddress() + " for characteristic " + characteristic.getUuid() + ".");
            updateCharacteristicValue(gatt, characteristic);
            if(gatt.getDevice().getAddress().equals(mBluetoothGatt.getDevice().getAddress())) {
                Log.d(TAG, "onCharacteristicChanged() broatcasting to main, current selected device " + mBluetoothGatt.getDevice().getName());
                broadcastUpdate(ACTION_GATT_NOTIFY);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "entering .onReadRemoteRssi() for device " + gatt.getDevice().getName() + " with adress " + gatt.getDevice().getAddress() + " with rssi " + rssi + ".");
                MainActivity.getInstance().getSensorDataList().get(MainActivity.getInstance().getConnectedDevicePosition()).setRssi(rssi);

                /*if(MainActivity.getInstance().getIsTransmittingToCloud() && MqttHandler.getInstance(MainActivity.getInstance()).isConnected()) {
                    String message = "{'deviceId' : " + gatt.getDevice().getAddress().replaceAll(":", "").toLowerCase() + ", 'localName' : '" + gatt.getDevice().getName() + "', 'd' : { 'rssi' : '" + rssi + "' }}";
                    MqttHandler mqttHandler = MqttHandler.getInstance(null);
                    Log.d(TAG, "publishing rssi for device " + gatt.getDevice().getName() + " with adress " + gatt.getDevice().getAddress() + ".");
                    mqttHandler.publish(mqttHandler.getTopicRssi(), message);
                }*/
                if(MainActivity.getInstance().getIsStoringLocally() || MainActivity.getInstance().getIsTransmittingToCloud()) {
                    Map<String, Object> body = new HashMap<String, Object>();
                    Map<String, Object> data = new HashMap<String, Object>();
                    Map<String, Object> d = new HashMap<String, Object>();
                    d.put("rssi", rssi);
                    data.put("d", d);
                    body.put("deviceId", gatt.getDevice().getAddress().replaceAll(":", "").toLowerCase());
                    body.put("localName", gatt.getDevice().getName());
                    data.put("localName", gatt.getDevice().getName());
                    body.put("eventType", "location");
                    body.put("timestamp", getCurrentGMTTime());
                    body.put("data", data);
                    if(MainActivity.getInstance().getIsStoringLocally()) {
                        DataHandler.getInstance().storeObject(body);
                    }
                    if(MainActivity.getInstance().getIsTransmittingToCloud()) {
                        DataHandler.getInstance().pushOneFile(body);
                    }

                }
            }
        }

    };

    private void addCharacteristicNotifyQueue(BluetoothGattCharacteristic characteristic)
    {
        if(characteristic.getUuid().toString().equals(NORDIC_SENSOR_TEMPERATURE_DATA_UUID)
                || characteristic.getUuid().toString().equals(NORDIC_SENSOR_VSOLAR_DATA_UUID)
                || characteristic.getUuid().toString().equals(BATTERY_LEVEL_CHAR_UUID)
                || characteristic.getUuid().toString().equals(TI_BAROMETER_DATA_CHAR_UUID)
                || characteristic.getUuid().toString().equals(TI_ACCELEROMETER_DATA_CHAR_UUID)
                || characteristic.getUuid().toString().equals(NORDIC_SENSOR_ACCELEROMETER_DATA_UUID)
                || characteristic.getUuid().equals(WibiSmartGatt.getInstance().LIGHT_DATA_CHAR_UUID_ENVIRO)
                || characteristic.getUuid().equals(WibiSmartGatt.getInstance().CO2_DATA_CHAR_UUID_ENVIRO)
                || characteristic.getUuid().equals(WibiSmartGatt.getInstance().SPEC_DATA_CHAR_UUID_ENVIRO))
        {
            characteristicSetNotifyQueue.add(characteristic);
        }

    }

    private void updateCharacteristicValue(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
    {
        SensorData thisSensorData = MainActivity.getInstance().getSensorDataWithAdress(gatt.getDevice().getAddress(), gatt.getDevice().getName());
        if(thisSensorData == null) {
            gatt.disconnect();
            Log.d(TAG, ".updateCharacteristicValue() device " + gatt.getDevice().getName() + " is not supposed to be connected. Disconnecting...");
        }
        else {
            boolean transmitToCloud = MainActivity.getInstance().getIsTransmittingToCloud();
            boolean storingLocally = MainActivity.getInstance().getIsStoringLocally();
            Log.d(TAG, "entering updateCharacteristicValue() for characteristic " + characteristic.getUuid().toString() + " for device " + thisSensorData.getLocalName() + ", isTransmitingToCloud: " + transmitToCloud + ".");
            byte byteArray[] = characteristic.getValue();
            // Set the right characteristic
            if (characteristic.getUuid().toString().equals(ADV_MODE_CHAR_UUID)) {
                thisSensorData.setBeaconType(((int) byteArray[0]));
            } else if (characteristic.getUuid().toString().equals(ADV_INTERVAL_CHAR_UUID)) {
                thisSensorData.setAdvertisingIntervals(((int) byteArray[0]));
            } else if (characteristic.getUuid().toString().equals(ADV_POWER_CHAR_UUID)) {
                thisSensorData.setTxPower(((int) byteArray[0]));
            } else if (characteristic.getUuid().toString().equals(ADV_SMART_CHAR_UUID)) {
                thisSensorData.setSmartPowerMode(((int) byteArray[0]));
            } else if (characteristic.getUuid().toString().equals(NORDIC_SENSOR_TEMPERATURE_DATA_UUID)) {
                thisSensorData.setTemperatureNordic(((int) byteArray[0]));
            } else if (characteristic.getUuid().toString().equals(NORDIC_SENSOR_VSOLAR_DATA_UUID)) {
                thisSensorData.setVSolarNordic(byteArray[0] & 0xFF);

                /*if (transmitToCloud && MqttHandler.getInstance(MainActivity.getInstance()).isConnected() && (byteArray[0] & 0xFF) != 0) {
                    String message = "{'deviceId' : " + gatt.getDevice().getAddress().replaceAll(":", "").toLowerCase() + ", 'localName' : '" + gatt.getDevice().getName() + "', 'd' : { 'light' : '" + (byteArray[0] & 0xFF) + "' }}";
                    Log.d(TAG, "publishing light for device " + gatt.getDevice().getName() + " with adress " + gatt.getDevice().getAddress() + ".");
                    MqttHandler.getInstance(MainActivity.getInstance()).publish(MqttHandler.getInstance(MainActivity.getInstance()).getTopicHealth(), message);
                }*/
            } else if (characteristic.getUuid().toString().equals(BATTERY_LEVEL_CHAR_UUID)) {
                int batteryLevel = (byteArray[0] & 0xFF);
                thisSensorData.setBatteryLevel(batteryLevel);
                /*if (transmitToCloud && MqttHandler.getInstance(MainActivity.getInstance()).isConnected()) {
                    String message = "{'deviceId' : " + gatt.getDevice().getAddress().replaceAll(":", "").toLowerCase() + ", 'localName' : '" + gatt.getDevice().getName() + "', 'd' : { 'batteryLevel' : '" + batteryLevel + "' }}";
                    Log.d(TAG, "publishing battery for device " + gatt.getDevice().getName() + " with adress " + gatt.getDevice().getAddress() + ".");
                    MqttHandler.getInstance(MainActivity.getInstance()).publish(MqttHandler.getInstance(MainActivity.getInstance()).getTopicBattery(), message);
                }*/
                if (storingLocally || transmitToCloud) {
                    Map<String, Object> body = new HashMap<String, Object>();
                    Map<String, Object> data = new HashMap<String, Object>();
                    Map<String, Object> d = new HashMap<String, Object>();
                    d.put("batteryLevel", batteryLevel);
                    data.put("d", d);
                    body.put("deviceId", gatt.getDevice().getAddress().replaceAll(":", "").toLowerCase());
                    body.put("localName", gatt.getDevice().getName());
                    body.put("eventType", "battery");
                    body.put("timestamp", getCurrentGMTTime());
                    body.put("data", data);
                    if(storingLocally) {
                        DataHandler.getInstance().storeObject(body);
                    }
                    if(transmitToCloud) {
                        DataHandler.getInstance().pushOneFile(body);
                    }
                }

            } else if (characteristic.getUuid().toString().equals(TI_BAROMETER_DATA_CHAR_UUID)) {
                int[] weatherTi = new int[9];
                for (int i = 0; i < byteArray.length; i++) {
                    weatherTi[i] = byteArray[i] & 0xFF;
                }
                thisSensorData.setWeatherEnviro(weatherTi);

                float temperature = thisSensorData.getTemperatureEnviro();
                float humidity = thisSensorData.getHumidityEnviro();
                float pressure = thisSensorData.getPressureEnviro();
                /*if (transmitToCloud && MqttHandler.getInstance(MainActivity.getInstance()).isConnected() && (weather[0] != 0 || weather[1] != 0 || weather[2] != 0)) {
                    String message = "{'deviceId' : " + gatt.getDevice().getAddress().replaceAll(":", "").toLowerCase() + ", 'localName' : '" + gatt.getDevice().getName() + "', 'd' : { 'temperature' : '" + weather[0] + "', 'pressure' : '" + weather[1] + "', 'humidity' : '" + weather[2] + "' }}";
                    Log.d(TAG, "publishing weather for device " + gatt.getDevice().getName() + " with adress " + gatt.getDevice().getAddress() + ".");
                    MqttHandler.getInstance(MainActivity.getInstance()).publish(MqttHandler.getInstance(MainActivity.getInstance()).getTopicAir(), message);
                }*/
                if((transmitToCloud || storingLocally) && (temperature != 0 || pressure != 0 || humidity != 0)) {
                    Map<String, Object> body = new HashMap<String, Object>();
                    Map<String, Object> data = new HashMap<String, Object>();
                    Map<String, Object> d = new HashMap<String, Object>();
                    d.put("temperature", temperature);
                    d.put("pressure", pressure);
                    d.put("humidity", humidity);
                    data.put("d", d);
                    body.put("deviceId", gatt.getDevice().getAddress().replaceAll(":", "").toLowerCase());
                    body.put("localName", gatt.getDevice().getName());
                    body.put("eventType", "air");
                    body.put("timestamp", getCurrentGMTTime());
                    body.put("data", data);
                    if(storingLocally) {
                        DataHandler.getInstance().storeObject(body);
                    }
                    if(transmitToCloud) {
                        DataHandler.getInstance().pushOneFile(body);
                    }

                }
            }

            else if (characteristic.getUuid().toString().equals(MODEL_NUMBER_STRING_CHAR_UUID)) {
                char[] modelNumberString = thisSensorData.getModelNumberString();
                if (modelNumberString.length != byteArray.length)
                    modelNumberString = new char[byteArray.length];
                for (int i = 0; i < byteArray.length; i++) {
                    modelNumberString[i] = (char) (byteArray[i] & 0xFF);
                }
                thisSensorData.setModelNumberString(modelNumberString);
            } else if (characteristic.getUuid().toString().equals(TI_ACCELEROMETER_DATA_CHAR_UUID)) {
                byte[] accelerometerDatati = new byte[6];
                for (int i = 0; i < byteArray.length; i++) {
                    accelerometerDatati[i] = byteArray[i];
                }
                thisSensorData.setAccelerometer(accelerometerDatati);

                float X = thisSensorData.getAccelerometerX();
                float Y = thisSensorData.getAccelerometerY();
                float Z = thisSensorData.getAccelerometerZ();
                /*if (transmitToCloud && MqttHandler.getInstance(MainActivity.getInstance()).isConnected() && (accelerometerData[0] != 0 || accelerometerData[1] != 0 || accelerometerData[2] != 0)) {
                    String message = "{'deviceId' : " + gatt.getDevice().getAddress().replaceAll(":", "").toLowerCase() + ", 'localName' : '" + gatt.getDevice().getName() + "', 'd' : { 'x' : '" + accelerometerData[0] + "', 'y' : '" + accelerometerData[1] + "', 'z' : '" + accelerometerData[2] + "' }}";
                    Log.d(TAG, "publishing accel for device " + gatt.getDevice().getName() + " with adress " + gatt.getDevice().getAddress() + ".");
                    MqttHandler.getInstance(MainActivity.getInstance()).publish(MqttHandler.getInstance(MainActivity.getInstance()).getTopicAccel(), message);
                }*/
                if ((transmitToCloud || storingLocally) && (X != 0 || Y != 0 || Z != 0)) {
                    Map<String, Object> body = new HashMap<String, Object>();
                    Map<String, Object> data = new HashMap<String, Object>();
                    Map<String, Object> d = new HashMap<String, Object>();
                    d.put("x", X);
                    d.put("y", Y);
                    d.put("z", Z);
                    data.put("d", d);
                    body.put("deviceId", gatt.getDevice().getAddress().replaceAll(":", "").toLowerCase());
                    body.put("localName", gatt.getDevice().getName());
                    body.put("eventType", "accel");
                    body.put("timestamp", getCurrentGMTTime());
                    body.put("data", data);
                    if(storingLocally) {
                        DataHandler.getInstance().storeObject(body);
                    }
                    if(transmitToCloud) {
                        DataHandler.getInstance().pushOneFile(body);
                    }
                }
            } else if (characteristic.getUuid().equals(WibiSmartGatt.getInstance().LIGHT_DATA_CHAR_UUID_ENVIRO)) {
                thisSensorData.setLightLevel(byteArray);

                int lightLevel = thisSensorData.getLightEnviro();
                /*if (transmitToCloud && MqttHandler.getInstance(MainActivity.getInstance()).isConnected() && lightLevel != 0) {
                    String message = "{'deviceId' : " + gatt.getDevice().getAddress().replaceAll(":", "").toLowerCase() + ", 'localName' : '" + gatt.getDevice().getName() + "', 'd' : { 'light' : '" + lightLevel + "' }}";
                    Log.d(TAG, "publishing light for device " + gatt.getDevice().getName() + " with adress " + gatt.getDevice().getAddress() + ".");
                    MqttHandler.getInstance(MainActivity.getInstance()).publish(MqttHandler.getInstance(MainActivity.getInstance()).getTopicHealth(), message);
                }*/
                if ((transmitToCloud || storingLocally) && lightLevel != 0) {
                    Map<String, Object> body = new HashMap<String, Object>();
                    Map<String, Object> data = new HashMap<String, Object>();
                    Map<String, Object> d = new HashMap<String, Object>();
                    d.put("light", lightLevel);
                    data.put("d", d);
                    body.put("deviceId", gatt.getDevice().getAddress().replaceAll(":", "").toLowerCase());
                    body.put("localName", gatt.getDevice().getName());
                    body.put("eventType", "health");
                    body.put("timestamp", getCurrentGMTTime());
                    body.put("data", data);
                    if(storingLocally) {
                        DataHandler.getInstance().storeObject(body);
                    }
                    if(transmitToCloud) {
                        DataHandler.getInstance().pushOneFile(body);
                    }
                }
            } else if (characteristic.getUuid().toString().equals(NORDIC_SENSOR_ACCELEROMETER_DATA_UUID)) {
                thisSensorData.setAccelerometer(byteArray);
            } else if (characteristic.getUuid().equals(WibiSmartGatt.getInstance().CO2_DATA_CHAR_UUID_ENVIRO)) {
                if (byteArray[0] == 0xaa && byteArray[0] == 0xaa) {
                    MainActivity.getInstance().confirmationCO2Calibration(gatt.getDevice());
                }
                else {
                    thisSensorData.setCO2Enviro(byteArray);

                    int CO2 = thisSensorData.getCO2Enviro();
                    if ((transmitToCloud || storingLocally) && CO2 > 0) {
                        Map<String, Object> body = new HashMap<String, Object>();
                        Map<String, Object> data = new HashMap<String, Object>();
                        Map<String, Object> d = new HashMap<String, Object>();
                        d.put("CO2", CO2);
                        data.put("d", d);
                        body.put("deviceId", gatt.getDevice().getAddress().replaceAll(":", "").toLowerCase());
                        body.put("localName", gatt.getDevice().getName());
                        body.put("eventType", "CO2");
                        body.put("timestamp", getCurrentGMTTime());
                        body.put("data", data);
                        if (storingLocally) {
                            DataHandler.getInstance().storeObject(body);
                        }
                        if (transmitToCloud) {
                            DataHandler.getInstance().pushOneFile(body);
                        }
                    }
                }
            } else if (characteristic.getUuid().equals(WibiSmartGatt.getInstance().SPEC_DATA_CHAR_UUID_ENVIRO)) {
                thisSensorData.setGases(byteArray);

                float SO2 = thisSensorData.getSO2Enviro();
                float CO = thisSensorData.getCOEnviro();
                float O3 = thisSensorData.getO3Enviro();
                float NO2 = thisSensorData.getNO2Enviro();
                if ((transmitToCloud || storingLocally) && (SO2 != 0 || CO != 0 || O3 != 0 || NO2 != 0)) {
                    Map<String, Object> body = new HashMap<String, Object>();
                    Map<String, Object> data = new HashMap<String, Object>();
                    Map<String, Object> d = new HashMap<String, Object>();
                    d.put("SO2", SO2);
                    d.put("CO", CO);
                    d.put("O3", O3);
                    d.put("NO2", NO2);
                    data.put("d", d);
                    body.put("deviceId", gatt.getDevice().getAddress().replaceAll(":", "").toLowerCase());
                    body.put("localName", gatt.getDevice().getName());
                    body.put("eventType", "gases");
                    body.put("timestamp", getCurrentGMTTime());
                    body.put("data", data);
                    if(storingLocally) {
                        DataHandler.getInstance().storeObject(body);
                    }
                    if(transmitToCloud) {
                        DataHandler.getInstance().pushOneFile(body);
                    }
                }
            } else {
                updateSettingsValue(characteristic);
            }
        }

    }


    private void updateSettingsValue(BluetoothGattCharacteristic characteristic)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        byte byteArray[] = characteristic.getValue();

        if(characteristic.getUuid().equals(WibiSmartGatt.getInstance().ACCELEROMETER_CONF_CHAR_UUID_ENVIRO))
        {
            editor.putBoolean("accelerometer_checkbox_enviro", (int)byteArray[0] == 1);
            editor.apply();
        }
        else if(characteristic.getUuid().equals(WibiSmartGatt.getInstance().ACCELEROMETER_PERIOD_CHAR_UUID_ENVIRO))
        {
//            int period = ((char)(byteArray[0] & 0xFF));
//            editor.putString("accelerometer_period_enviro", Integer.toString(period));
//            editor.apply();
        }
        else if(characteristic.getUuid().equals(WibiSmartGatt.getInstance().WEATHER_CONF_CHAR_UUID_ENVIRO))
        {
            editor.putBoolean("weather_checkbox_enviro", (int)byteArray[0] == 1);
            editor.apply();
        }
        else if(characteristic.getUuid().equals(WibiSmartGatt.getInstance().WEATHER_PERIOD_CHAR_UUID_ENVIRO))
        {
//            int period = ((char)(byteArray[0] & 0xFF));
//            editor.putString("weather_period_enviro", Integer.toString(period));
//            editor.apply();
        }
        else if(characteristic.getUuid().equals(WibiSmartGatt.getInstance().LIGHT_CONF_CHAR_UUID_ENVIRO))
        {
            editor.putBoolean("light_checkbox_enviro", (int)byteArray[0] == 1);
            editor.apply();
        }
        else if(characteristic.getUuid().equals(WibiSmartGatt.getInstance().LIGHT_PERIOD_CHAR_UUID_ENVIRO))
        {
//            int period = ((char)(byteArray[0] & 0xFF));
//            editor.putString("light_period_enviro", Integer.toString(period));
//            editor.apply();
        }
        else if(characteristic.getUuid().equals(WibiSmartGatt.getInstance().CO2_CONF_CHAR_UUID_ENVIRO))
        {
            editor.putBoolean("CO2_checkbox_enviro", (int)byteArray[0] == 1);
            editor.apply();
        }
        else if(characteristic.getUuid().equals(WibiSmartGatt.getInstance().CO2_PEROÏOD_CHAR_UUID_ENVIRO))
        {
//            int period = ((char)(byteArray[0] & 0xFF));
//            editor.putString("CO2_period_enviro", Integer.toString(period));
//            editor.apply();
        }


    }

    public void clearWritingQueue() {
        Log.d(TAG, "entering .clearWritingQueue()");
        int counter = 0;
        if(characteristicToWriteQueue != null) {
            while (!characteristicToWriteQueue.isEmpty()) {
                characteristicToWriteQueue.remove();
                counter++;
            }
        }
        if (characteristicQueue != null) {
            while (!characteristicQueue.isEmpty()) {
                characteristicQueue.remove();
            }
        }
        if(servicesQueue != null) {
            while (!servicesQueue.isEmpty()) {
                servicesQueue.remove();
            }
        }
        if (characteristicSetNotifyQueue != null) {
            while (!characteristicSetNotifyQueue.isEmpty()) {
                characteristicSetNotifyQueue.remove();
            }
        }
        isBusyWritting = false;
        Log.d(TAG, ".clearWritingQueue() cleared " + counter + " writes.");
    }

    public void deleteGattAtPosition(int pos) {
        mBluetoothGattList.remove(pos);
    }

    public void setSelectedGatt(int position) {
        Log.d(TAG, "entering setSelectedGatt()");
        if(mBluetoothGattList != null && position < mBluetoothGattList.size()) {
            Log.d(TAG, "setSelectedGatt() at position "+ position + ", list size = "+ mBluetoothGattList.size());
            mBluetoothGatt = mBluetoothGattList.get(position);
        }
    }


    public String getCurrentGMTTime() {
        String out;
        Date date = new Date();
        char[] miliseconds = (date.getTime()+"").toCharArray();

        String[] fullDate = date.toGMTString().split(" ");
        out = fullDate[2] + "-";
        switch (fullDate[1]) {
            case "Jan":
                out += "01-";
                break;
            case "Feb":
                out += "02-";
                break;
            case "Mar":
                out += "03-";
                break;
            case "Apr":
                out += "04-";
                break;
            case "May":
                out += "05-";
                break;
            case "Jun":
                out += "06-";
                break;
            case "Jul":
                out += "07-";
                break;
            case "Aug":
                out += "08-";
                break;
            case "Sep":
                out += "09-";
                break;
            case "Oct":
                out += "10-";
                break;
            case "Nov":
                out += "11-";
                break;
            case "Dec":
                out += "12-";
                break;
        }
        if(fullDate[0].length() == 1) {
            out += "0" + fullDate[0];
        }
        else {
            out += fullDate[0];
        }
        out += "T" + fullDate[3] + "." + miliseconds[10] + miliseconds[11] + miliseconds[12] + "Z";


        return out;
    }




}
