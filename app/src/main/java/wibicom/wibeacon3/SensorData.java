package wibicom.wibeacon3;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

/**
 * @desc This class transform the received byte into readable data
 * @author Olivier Tessier-Lariviere
 */
public class SensorData {

    private String localName;
    private String adress;

    // Advertising settings data
    public int beaconType = 0;
    public int txPower = 0;
    public int advertisingIntervals = 0;
    public int smartPowerMode = 0;
    public byte[] advertisingPayload;

    //move data
    private int temperatureNordic = 0;
    private int vSolarNordic = 0;

    //enviro data
    private float temperatureEnviro = 0;
    private float pressureEnviro = 0;
    private float humidityEnviro = 0;

    private int CO2Enviro = 0;
    private int SO2Enviro = 0;
    private int COEnviro = 0;
    private int O3Enviro = 0;
    private int NO2Enviro = 0;

    private float accelerometerX = 0;
    private float accelerometerY = 0;
    private float accelerometerZ = 0;

    private int rssi = 0;

    private int lightLevel = 0;

    private int batteryLevel = 0;

    //these variables are here to keep track of what sensor is on or off.
    private boolean accelSensorOn = false;
    private boolean weatherSensorOn = false;
    private boolean lightSensorOn = false;
    private boolean CO2SensorOn = false;
    private boolean SO2SensorOn = false;
    private boolean COSensorOn = false;
    private boolean O3SensorOn = false;
    private boolean NO2SensorOn = false;

    private boolean hasAccelSensor = false;
    private boolean hasWeatherSensor = false;
    private boolean hasLightSensor = false;
    private boolean hasCO2Sensor = false;
    private boolean hasGasesSensor = false;

    private int lastAccelPeriod = 5;
    private int lastLightPeriod = 10;
    private int lastWeatherPeriod = 50;
    private int lastCO2Period = 50;
    private int lastGasesPeriod = 150;

    public char[] modelNumberString = {0};

    public SensorData(String localName, String adress) {
        this.localName = localName;
        this.adress = adress;
    }


    private BluetoothGatt connecteddeviceGatt;

    public void setConnecteddeviceGatt(BluetoothGatt gatt) {
        connecteddeviceGatt = gatt;
    }

    public BluetoothGatt getConnecteddeviceGatt() {
        return connecteddeviceGatt;
    }


    public void setWeatherEnviro(int[] weather)
    {
        temperatureEnviro = Math.round((float)(weather[2] * 0x10000 + weather[1] * 0x100 + weather[0]) / 100 * 10)/10f;
        pressureEnviro = Math.round((float)(weather[5] * 0x10000 + weather[4] * 0x100 + weather[3]) / 100 * 10)/10f;
        humidityEnviro = Math.round(((float)(weather[8] * 0x10000 + weather[7] * 0x100 + weather[6])) / (float)Math.pow(2, 10) * 10 * 10)/10f;
    }


    public void setCO2Enviro(byte[] CO2)
    {
        //Log.d("CO222", CO2.length+"");
        //Log.d("CO222", CO2[0]+ " "+ CO2[1]+ " "+ CO2[2]+ " "+ CO2[3]+ " "+ CO2[4]+ " "+ CO2[4]+ " "+ CO2[5]+ " "+ CO2[6]+ " "+ CO2[7]+ " "+ CO2[8]+ " "+ CO2[9]+ " "+ CO2[10]+ " "+ CO2[11]+ " "+ CO2[12]+ " "+ CO2[13]+ " "+ CO2[14]+ " "+ CO2[15]);
        CO2Enviro = Math.round(((int)((CO2[3]-48) * 10000 + (CO2[4]-48)*1000 + (CO2[5]-48)*100 + (CO2[6]-48)*10 + (CO2[7]-48))) * 10)/10;
    }

    public void setAccelerometer(byte[] accelerometerData)
    {
        accelerometerX = Math.round((float)(accelerometerData[1] * 0x100 + (char)(accelerometerData[0] & 0xFF)) * 0.488f * 10)/10f;
        accelerometerY = Math.round((float)(accelerometerData[3] * 0x100 + accelerometerData[2]) * 0.488f * 10)/10f;
        accelerometerZ = Math.round((float)(accelerometerData[5] * 0x100 + accelerometerData[4]) * 0.488f * 10)/10f;
    }


    public void setGases(byte[] gasesData) {
        SO2Enviro = (int) Math.round(((Integer.parseInt(gasesData[7] + "" + gasesData[6] + "" + gasesData[5] + "" + gasesData[4], 16) * Math.pow(10, -6)) - 1.25)/0.0045852);
        COEnviro = (int) Math.round(((Integer.parseInt(gasesData[7] + "" + gasesData[6] + "" + gasesData[5] + "" + gasesData[4], 16) * Math.pow(10, -6)) - 1.25)/0.0008940);
        O3Enviro = (int) Math.round(((Integer.parseInt(gasesData[7] + "" + gasesData[6] + "" + gasesData[5] + "" + gasesData[4], 16) * Math.pow(10, -6)) - 1.25)/-0.0055475);
        NO2Enviro = (int) Math.round(((Integer.parseInt(gasesData[7] + "" + gasesData[6] + "" + gasesData[5] + "" + gasesData[4], 16) * Math.pow(10, -6)) - 1.25)/-0.0165620);
    }

    public void setBatteryLevel(int battery)
    {
        batteryLevel = battery;
    }

    public void setLightLevel(byte[] byteArray)
    {
        lightLevel = ((char)(byteArray[1] & 0xFF) * 0x100 + (char)(byteArray[0] & 0xFF));
    }

    public int getBeaconType() { return beaconType; }

    public void setBeaconType(int val) { beaconType = val; }

    public int getTxPower() { return txPower; }

    public void setTxPower(int val) { txPower = val; }

    public int getAdvertisingIntervals() { return advertisingIntervals; }

    public void setAdvertisingIntervals(int val) { advertisingIntervals = val; }

    public int getSmartPowerMode() { return smartPowerMode; }

    public void setSmartPowerMode(int val) { smartPowerMode = val; }

    public void setRssi(int rssi)
    {
        this.rssi = rssi;
    }

    public int getRssi()
    {
        return rssi;
    }

    public int getLightEnviro()
    {
        return  lightLevel;
    }

    public float getTemperatureEnviro()
    {
        return temperatureEnviro;
    }

    public float getPressureEnviro()
    {
        return pressureEnviro;
    }

    public float getHumidityEnviro()
    {
        return humidityEnviro;
    }

    public int getCO2Enviro() { return CO2Enviro; }

    public int getSO2Enviro() { return SO2Enviro; }

    public int getCOEnviro() { return COEnviro; }

    public int getO3Enviro() { return O3Enviro; }

    public int getNO2Enviro() { return NO2Enviro; }

    public int getBatteryLevel()
    {
        return batteryLevel;
    }

    public float getAccelerometerX(){ return accelerometerX; }

    public float getAccelerometerY(){ return accelerometerY; }

    public float getAccelerometerZ(){ return accelerometerZ; }

    public int getTemperatureNordic() { return temperatureNordic; }

    public void setTemperatureNordic(int val) { temperatureNordic = val;}

    public int getvSolarNordic() { return vSolarNordic; }

    public void setVSolarNordic(int val) { vSolarNordic = val; }

    public byte[] getAdvertisingPayload() { return advertisingPayload; }

    public void setAdvertisingPayload(byte[] data) { advertisingPayload = data; }

    public char[] getModelNumberString() { return modelNumberString; }

    public void setModelNumberString(char[] data) { modelNumberString = data; }

    public String getAdress() { return adress; }

    public String getLocalName() { return localName; }

    public boolean getAccelSensorOn() { return accelSensorOn; }

    public void setAccelSensorOn(boolean b) { accelSensorOn = b; }

    public boolean getWeatherSensorOn() { return weatherSensorOn; }

    public void setWeatherSensorOn(boolean b) { weatherSensorOn = b; }

    public boolean getLightSensorOn() { return lightSensorOn; }

    public void  setLighgtSensorOn(boolean b) { lightSensorOn = b; }

    public boolean getCO2SensorOn() { return CO2SensorOn; }

    public void setCO2SensorOn(boolean b) { CO2SensorOn = b; }

    public boolean getSO2SensorOn() { return SO2SensorOn; }

    public void setSO2SensorOn(boolean b) { SO2SensorOn = b; }

    public boolean getCOSensorOn() { return COSensorOn; }

    public void setCOSensorOn(boolean b) { COSensorOn = b; }

    public boolean getO3SensorOn() { return O3SensorOn; }

    public void setO3SensorOn(boolean b) { O3SensorOn = b; }

    public boolean getNO2SensorOn() { return NO2SensorOn; }

    public void setNO2SensorOn(boolean b) { NO2SensorOn = b; }

    public boolean getHasAccelSensor() { return hasAccelSensor; }

    public void setHasAccelSensor(boolean b) { hasAccelSensor = b; }

    public boolean getHasWeatherSensor() { return hasWeatherSensor; }

    public void setHasWeatherSensor(boolean b) { hasWeatherSensor = b; }

    public boolean getHasLightSensor() { return hasLightSensor; }

    public void  setHasLightSensor(boolean b) { hasLightSensor = b; }

    public boolean getHasCO2Sensor() { return hasCO2Sensor; }

    public void setHasCO2Sensor(boolean b) { hasCO2Sensor = b; }

    public boolean getHasGasesSensor() { return hasGasesSensor; }

    public void setHasGasesSensor(boolean b) { hasGasesSensor = b; }

    public int getLastAccelPeriod() { return lastAccelPeriod; }

    public void setLastAccelPeriod(int d) { lastAccelPeriod = d; }

    public int getLastWeatherPeriod() { return lastWeatherPeriod; }

    public void setLastWeatherPeriod(int d) { lastWeatherPeriod = d; }

    public int getLastLightPeriod() { return lastLightPeriod; }

    public void setLastLightPeriod(int d) { lastLightPeriod = d; }

    public int getLastCO2Period() { return lastCO2Period; }

    public void setLastCO2Period(int d) { lastCO2Period = d; }

    public int getLastGasesPeriod() { return lastGasesPeriod; }

    public void setLastGasesPeriod(int d) { lastGasesPeriod = d; }



}
