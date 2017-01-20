package com.r.darina.hazelnut;

import android.content.Context;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


/**
 * Created by Darina on 12.01.17.
 */

public class Connection {
    private Context context;

    MqttAndroidClient client;
    private MqttConnectOptions options;

    private String mBroker;
    private String mUser;
    private String mPassword;
    private String mClientID;

    String recivedMessage;
    private MqttObserver mObserver;

    public Connection(Context context) {
        this.context = context;
        mBroker = ConnectionConstants.BROKER_URI;
        mUser = ConnectionConstants.USER;
        mPassword = ConnectionConstants.PASS;
        mClientID = "androidSampleClient";
        initMqttConnection();
    }

    public void setMqttObserver(MqttObserver aObserver) {
        mObserver = aObserver;
    }

    private void initMqttConnection() {
        client = new MqttAndroidClient(context, mBroker, mClientID);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(context, "connection was lost", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                recivedMessage = new String(message.getPayload());
                setNewMessage(recivedMessage);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Toast.makeText(context, "delivered!", Toast.LENGTH_SHORT).show();
            }
        });

        options = new MqttConnectOptions();
        options.setUserName(mUser);
        options.setPassword(mPassword.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    try {
                        client.subscribe("/iskra", 0);
                        client.subscribe("/insidetest", 0);
                    } catch (MqttException ex) {
                        System.out.println(ex);
                    }

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("Connection Failure!");
                }
            });
        } catch (MqttException ex) {
            System.out.println(ex);
        }
    }

    public void sub(String topic) {
        try {
            client.subscribe(topic, 0);
        } catch (MqttException ex) {

        }
    }

    public void pub(String topic, String message) {
        try {
            client.publish(topic, new MqttMessage(message.getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void setNewMessage(String msg) {
        recivedMessage = msg;
        mObserver.onMqttResponse(msg);
    }

    public interface MqttObserver {
        public void onMqttResponse(String msg);
    }
}