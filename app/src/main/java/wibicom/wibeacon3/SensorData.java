package wibicom.wibeacon3;

import java.text.DecimalFormat;
import java.util.Vector;

/**
 * @desc This class transform the received byte into readable data
 * @author Olivier Tessier-Lariviere
 */
public class SensorData {

    private float temperatureEnviro = 0;
    private float pressureEnviro = 0;
    private float humidityEnviro = 0;

    private float accelerometerX = 0;
    private float accelerometerY = 0;
    private float accelerometerZ = 0;

    private int rssi = 0;

    private float temperatureMove = 0;
    private int lightLevel = 0;

    private int batteryLevel = 0;



    private static SensorData ourInstance = new SensorData();

    public static SensorData getInstance() {
        return ourInstance;
    }

    private SensorData() {
    }



    public void setWeatherEnviro(int[] weather)
    {
        temperatureEnviro = Math.round((float)(weather[2] * 0x10000 + weather[1] * 0x100 + weather[0]) / 100 * 10)/10f;
        pressureEnviro = Math.round((float)(weather[5] * 0x10000 + weather[4] * 0x100 + weather[3]) / 100 * 10)/10f;
        humidityEnviro = Math.round(((float)(weather[8] * 0x10000 + weather[7] * 0x100 + weather[6])) / (float)Math.pow(2, 10) * 10 * 10)/10f;
    }

    public void setAccelerometer(byte[] accelerometerData)
    {
        accelerometerX = Math.round((float)(accelerometerData[1] * 0x100 + (char)(accelerometerData[0] & 0xFF)) * 0.488f * 10)/10f;
        accelerometerY = Math.round((float)(accelerometerData[3] * 0x100 + accelerometerData[2]) * 0.488f * 10)/10f;
        accelerometerZ = Math.round((float)(accelerometerData[5] * 0x100 + accelerometerData[4]) * 0.488f * 10)/10f;
    }

    public void setBatteryLevel(int battery)
    {
        batteryLevel = battery;
    }

    public void setLightLevel(byte[] byteArray)
    {
        lightLevel = ((char)(byteArray[1] & 0xFF) * 0x100 + (char)(byteArray[0] & 0xFF));
    }

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

    public int getBatteryLevel()
    {
        return batteryLevel;
    }

    public int getLightLevel()
    {
        return lightLevel;
    }

    public float getAccelerometerX(){ return accelerometerX; }

    public float getAccelerometerY(){ return accelerometerY; }

    public float getAccelerometerZ(){ return accelerometerZ; }



}
