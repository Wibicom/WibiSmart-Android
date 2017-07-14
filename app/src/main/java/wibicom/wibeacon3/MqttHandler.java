package wibicom.wibeacon3;


import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;



public class MqttHandler implements MqttCallback {

    private static MqttHandler instance;
    private MqttAndroidClient mqttClient;
    Context context;

    private static String ORG = "4rxa4d";
    private static String DEVICE_TYPE = "AndroidPhone";
    private static String DEVICE_ID = "";//"1c39476b8dd0";
    private static String TOKEN = "";//"NuOLxPnhOgTj0CAzHS";
    private static String TOPIC_AIR = "iot-2/evt/air/fmt/json";
    private static String TOPIC_ACCEL = "iot-2/evt/accel/fmt/json";
    private static String TOPIC_HEALTH = "iot-2/evt/health/fmt/json";
    private static String TOPIC_BATTERY = "iot-2/evt/battery/fmt/json";
    private static String TOPIC_CO2 = "iot-2/evt/CO2/fmt/json";
    private static String TOPIC_RSSI = "iot-2/evt/location/fmt/json";

    private final static String TAG = MqttHandler.class.getName();

    private MqttHandler(Context context) {
        this.context = context;
    }


    public static MqttHandler getInstance(Context context) {
        if (instance == null) {
            instance = new MqttHandler(context);
        }
        return instance;
    }


    @Override
    public void connectionLost(Throwable throwable) {
        Log.d(TAG, "entering connectionLost()");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        Log.d(TAG, "entering messageArrived()");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        Log.d(TAG, "entering deliveryComplete() with token " + iMqttDeliveryToken + ".");
    }

    public void connect(IMqttActionListener listener) {
        Log.d(TAG, "entering .connect()");
        if (!isConnected()) {
            String iotPort = "1883";
            String iotHost = ORG+".messaging.internetofthings.ibmcloud.com";
            String iotClientId = "d:"+ORG+":"+DEVICE_TYPE+":"+DEVICE_ID;

            String connectionUri = "tcp://" + iotHost + ":" + iotPort;

            if (mqttClient != null) {
                mqttClient.unregisterResources();
                mqttClient = null;
            }

            mqttClient = new MqttAndroidClient(context, connectionUri, iotClientId);
            mqttClient.setCallback(this);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName("use-token-auth");
            options.setPassword(TOKEN.toCharArray());
            Log.d(TAG, "connecting with clientId " + iotClientId + " and connectionURI " + connectionUri + "...");
            mqttClient.connect(options, context, listener);


        }
    }

    public void disconnect(IMqttActionListener listener) {
        if (isConnected()) {
            mqttClient.disconnect(context, listener);
        }
    }

    public void publish(String topic, String jsonFormatString) {
        Log.d(TAG, "entering .publish()");
        if (isConnected()) {
            MqttMessage mqttMsg = new MqttMessage(jsonFormatString.getBytes());
            mqttMsg.setRetained(false);
            mqttMsg.setQos(0);

            try {
                IMqttDeliveryToken token = mqttClient.publish(topic, mqttMsg);
                Log.d(TAG, "Publishing event with topic " + topic + " and message " + jsonFormatString + " with token " + token + ".");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public boolean isConnected() {
        if (mqttClient != null) {
            boolean out = mqttClient.isConnected();
            Log.d(TAG, "entered .isConnected() - returning " + out);
            return out;
        }
        Log.d(TAG, "entered .isConnected() - returning false due to non existing client");
        return false;
    }

    public String getTopicAir() { return TOPIC_AIR; }

    public String getTopicAccel() { return TOPIC_ACCEL; }

    public String getTopicHealth() { return TOPIC_HEALTH; }

    public String getTopicBattery() { return TOPIC_BATTERY; }

    public String getTopicCo2() { return TOPIC_CO2; }

    public String getTopicRssi() { return TOPIC_RSSI; }

    public void setDeviceId(String newId) {
        DEVICE_ID = newId;
    }

    public void setAuthentificationToken(String newAuthToken) {
        TOKEN = newAuthToken;
    }
}