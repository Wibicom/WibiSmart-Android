package wibicom.wibeacon3;

import java.util.UUID;

import static java.util.UUID.fromString;

/**
 * @desc This class holds the bluetooth gatt information of the Enviro device and Move device.
 * @author Olivier Tessier-Lariviere
 */
public class WibiSmartGatt {
    private static WibiSmartGatt ourInstance = new WibiSmartGatt();

    public static WibiSmartGatt getInstance() {
        return ourInstance;
    }

    private WibiSmartGatt() {
    }


    public UUID ACCELEROMETER_SERVICE_UUID_ENVIRO = fromString("0000aa80-0000-1000-8000-00805f9b34fb");
    public UUID ACCELEROMETER_DATA_CHAR_UUID_ENVIRO = fromString("0000aa81-0000-1000-8000-00805f9b34fb");
    public UUID ACCELEROMETER_CONF_CHAR_UUID_ENVIRO = UUID.fromString("0000aa82-0000-1000-8000-00805f9b34fb");
    public UUID ACCELEROMETER_PERIOD_CHAR_UUID_ENVIRO = fromString("0000aa83-0000-1000-8000-00805f9b34fb");

    public UUID WEATHER_SERVICE_UUID_ENVIRO = UUID.fromString("0000aa40-0000-1000-8000-00805f9b34fb");
    public UUID WEATHER_DATA_CHAR_UUID_ENVIRO = UUID.fromString("0000aa41-0000-1000-8000-00805f9b34fb");
    public UUID WEATHER_CONF_CHAR_UUID_ENVIRO = UUID.fromString("0000aa42-0000-1000-8000-00805f9b34fb");
    public UUID WEATHER_PERIOD_CHAR_UUID_ENVIRO = UUID.fromString("0000aa44-0000-1000-8000-00805f9b34fb");

    public UUID LIGHT_SERVICE_UUID_ENVIRO = UUID.fromString("0000aa20-0000-1000-8000-00805f9b34fb");
    public UUID LIGHT_DATA_CHAR_UUID_ENVIRO = UUID.fromString("0000aa21-0000-1000-8000-00805f9b34fb");
    public UUID LIGHT_CONF_CHAR_UUID_ENVIRO = UUID.fromString("0000aa22-0000-1000-8000-00805f9b34fb");
    public UUID LIGHT_PERIOD_CHAR_UUID_ENVIRO = UUID.fromString("0000aa23-0000-1000-8000-00805f9b34fb");

    public UUID ADV_SERVICE_UUID_MOVE = UUID.fromString("00001520-1212-EFDE-1523-785FEABCD123");
    public UUID ADV_MODE_CHAR_UUID_MOVE = UUID.fromString("00003020-1212-efde-1523-785feabcd123");
    public UUID ADV_INTERVAL_CHAR_UUID_MOVE = UUID.fromString("00003021-1212-efde-1523-785feabcd123");
    public UUID ADV_POWER_CHAR_UUID_MOVE = UUID.fromString("00003022-1212-efde-1523-785feabcd123");
    public UUID ADV_SMART_CHAR_UUID_MOVE = UUID.fromString("00003023-1212-efde-1523-785feabcd123");
    public UUID ADV_PAYLOAD_CHAR_UUID_MOVE = UUID.fromString("00003024-1212-efde-1523-785feabcd123");




//    public UUID
//
//    ADV_SERVICE_UUID = fromString("00001520-1212-EFDE-1523-785FEABCD123"),
//    ADV_MODE_CHAR_UUID = fromString("00003020-1212-efde-1523-785feabcd123"),
//    ADV_INTERVAL_CHAR_UUID = fromString("00003021-1212-efde-1523-785feabcd123"),
//    ADV_POWER_CHAR_UUID = fromString("00003022-1212-efde-1523-785feabcd123"),
//    ADV_SMART_CHAR_UUID = fromString("00003023-1212-efde-1523-785feabcd123"),
//    ADV_PAYLOAD_CHAR_UUID = fromString("00003024-1212-efde-1523-785feabcd123"),
//
//    BATTERY_SERVICE_UUID = fromString("180F"),
//    BATTERY_LEVEL_CHAR_UUID = fromString("2A19"),
//
//    SENSORS_SERVICE_UUID = fromString("00001620-1212-efde-1523-785feabcd123"),
//    SENSOR_TEMPERATURE_UUID = fromString("00003120-1212-efde-1523-785feabcd123"),
//    SENSOR_VSOLAR_UUID = fromString("00003121-1212-efde-1523-785feabcd123"),
//
//    ACCELEROMETER_SERVICE_UUID_ENVIRO = fromString("0000aa80-0000-1000-8000-00805f9b34fb"),
//    ACCELEROMETER_DATA_CHAR_UUID_ENVIRO = fromString("0000aa81-0000-1000-8000-00805f9b34fb"),
//    ACCELEROMETER_CONF_CHAR_UUID_ENVIRO = UUID.fromString("0000aa82-0000-1000-8000-00805f9b34fb"),
//    ACCELEROMETER_PERIOD_CHAR_UUID_ENVIRO = fromString("0000aa83-0000-1000-8000-00805f9b34fb"),
//
//    WEATHER_SERVICE_UUID = UUID.fromString("0000aa40-0000-1000-8000-00805f9b34fb"),
//    WEATHER_DATA_CHAR_UUID = UUID.fromString("0000aa41-0000-1000-8000-00805f9b34fb"),
//    WEATHER_CONF_CHAR_UUID_ENVIRO = UUID.fromString("0000aa42-0000-1000-8000-00805f9b34fb"),
//    WEATHER_PERIOD_CHAR_UUID = UUID.fromString("0000aa44-0000-1000-8000-00805f9b34fb");










}
