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
import android.os.Binder;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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

    private static final String MODEL_NUMBER_STRING_CHAR_UUID = "00002a24-0000-1000-8000-00805f9b34fb";
    private static final String TI_ACCELEROMETER_DATA_CHAR_UUID = "0000aa81-0000-1000-8000-00805f9b34fb";




    public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
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

    // Advertising settings data
    public int beaconType = 0;
    public int txPower = 0;
    public int advertisingIntervals = 0;
    public int smartPowerMode = 0;
    public byte[] advertisingPayload;

    // Sensors data (nordic)
    public int temperatureNordic = 0;
    public int vSolarNordic = 0;

    // Battery data
    public int batteryLevel = 0;

    // Weather (TI)
    public int[] weatherTi = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    public int weatherConfTi;

    public byte[] accelerometerDatati = {0, 0, 0, 0, 0, 0};

    public char[] modelNumberString = {0};




    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;

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
    }

    public void addToWriteQueue(UUID uuid, byte[] value)
    {
        characteristicToWriteQueue.add(new Pair(uuid, value));
        if(!isBusyWritting)
            writeNextCharacteristicInQueue();

    }

    private void writeNextCharacteristicInQueue()
    {
        if(!characteristicToWriteQueue.isEmpty())
        {
            UUID uuid = characteristicToWriteQueue.peek().first;
            byte[] value = characteristicToWriteQueue.poll().second;

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
        if (characteristic.getUuid().toString().equals(ADV_MODE_CHAR_UUID)) {
            characteristic.setValue(new byte[]{(byte)beaconType});
            mBluetoothGatt.writeCharacteristic(characteristic);
        }
        else if(characteristic.getUuid().toString().equals(ADV_INTERVAL_CHAR_UUID)) {
            characteristic.setValue(new byte[]{(byte)advertisingIntervals});
            mBluetoothGatt.writeCharacteristic(characteristic);
        }
        else if(characteristic.getUuid().toString().equals(ADV_POWER_CHAR_UUID)) {
            characteristic.setValue(new byte[]{(byte)txPower});
            mBluetoothGatt.writeCharacteristic(characteristic);
        }
        else if(characteristic.getUuid().toString().equals(ADV_SMART_CHAR_UUID)) {
            characteristic.setValue(new byte[]{(byte)smartPowerMode});
            mBluetoothGatt.writeCharacteristic(characteristic);
        }
        else if(characteristic.getUuid().toString().equals(ADV_PAYLOAD_CHAR_UUID)) {
            characteristic.setValue(advertisingPayload);
            mBluetoothGatt.writeCharacteristic(characteristic);
        }
    }


    public List<BluetoothGattService> getSupportedGattServices() {
        return mBluetoothGatt.getServices();
    }

    public void connect(final String address) {

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
    }

    public void readRemoteRssi()
    {
        mBluetoothGatt.readRemoteRssi();
    }


    public void disconnect() {
        mBluetoothGatt.disconnect();
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
                broadcastUpdate(ACTION_GATT_CONNECTED);
            }
            else
            {
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                servicesQueue = new LinkedList<>(mBluetoothGatt.getServices());
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);

                if(servicesQueue.size() > 0)
                {
                   readCharacteristics(servicesQueue.poll());
                }
            }
        }
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if(!characteristicQueue.isEmpty()) {
                    writeAdvertisingCharacteristic(characteristicQueue.poll());
                }
            }

            writeNextCharacteristicInQueue();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                addCharacteristicNotifyQueue(characteristic);
                updateCharacteristicValue(characteristic);
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
            updateCharacteristicValue(characteristic);
            broadcastUpdate(ACTION_GATT_NOTIFY);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                SensorData.getInstance().setRssi(rssi);
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
                || characteristic.getUuid().equals(WibiSmartGatt.getInstance().LIGHT_DATA_CHAR_UUID_ENVIRO))
        {
            characteristicSetNotifyQueue.add(characteristic);
        }

    }

    private void updateCharacteristicValue(BluetoothGattCharacteristic characteristic)
    {
        byte byteArray[] = characteristic.getValue();

        // Set the right characteristic
        if (characteristic.getUuid().toString().equals(ADV_MODE_CHAR_UUID))
        {
            beaconType = ((int)byteArray[0]);
        }
        else if(characteristic.getUuid().toString().equals(ADV_INTERVAL_CHAR_UUID))
        {
            advertisingIntervals = ((int)byteArray[0]);
        }
        else if(characteristic.getUuid().toString().equals(ADV_POWER_CHAR_UUID))
        {
            txPower = ((int)byteArray[0]);
        }
        else if(characteristic.getUuid().toString().equals(ADV_SMART_CHAR_UUID))
        {
            smartPowerMode = ((int)byteArray[0]);
        }
        else if(characteristic.getUuid().toString().equals(NORDIC_SENSOR_TEMPERATURE_DATA_UUID))
        {
            temperatureNordic = ((int)byteArray[0]);
        }
        else if(characteristic.getUuid().toString().equals(NORDIC_SENSOR_VSOLAR_DATA_UUID))
        {
            vSolarNordic = byteArray[0] & 0xFF;
        }
        else if(characteristic.getUuid().toString().equals(BATTERY_LEVEL_CHAR_UUID))
        {
            batteryLevel = (byteArray[0] & 0xFF);
        }
        else if(characteristic.getUuid().toString().equals(TI_BAROMETER_DATA_CHAR_UUID))
        {
            for(int i = 0; i < byteArray.length; i++)
            {
                weatherTi[i] = byteArray[i] & 0xFF;
            }
        }
        else if(characteristic.getUuid().toString().equals(TI_BAROMETER_CONF_CHAR_UUID))
        {
            weatherConfTi = ((int)byteArray[0]);
        }
        else if(characteristic.getUuid().toString().equals(MODEL_NUMBER_STRING_CHAR_UUID))
        {
            if(modelNumberString.length != byteArray.length)
                modelNumberString = new char[byteArray.length];
            for(int i = 0; i < byteArray.length; i++)
            {
                modelNumberString[i] = (char)(byteArray[i] & 0xFF);
            }
        }
        else if(characteristic.getUuid().toString().equals(TI_ACCELEROMETER_DATA_CHAR_UUID))
        {
            for(int i = 0; i < byteArray.length; i++)
            {
                accelerometerDatati[i] = byteArray[i];
            }
        }
        else if(characteristic.getUuid().equals(WibiSmartGatt.getInstance().LIGHT_DATA_CHAR_UUID_ENVIRO))
        {
            SensorData.getInstance().setLightLevel(byteArray);
        }
        else if(characteristic.getUuid().toString().equals(NORDIC_SENSOR_ACCELEROMETER_DATA_UUID))
        {
            SensorData.getInstance().setAccelerometer(byteArray);
        }

        else
        {
            updateSettingsValue(characteristic);
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


    }






}
