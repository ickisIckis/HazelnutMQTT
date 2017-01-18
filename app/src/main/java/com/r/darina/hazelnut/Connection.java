package com.r.darina.hazelnut;

import android.content.Context;

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

    private String broker;
    private String user;
    private String password;
    private String clientId;

    String recivedMessage;
    private MqttObserver mObserver;

    public Connection(Context context) {
        this.context = context;
        broker = ConnectionConstants.BROKER_URI;
        user = ConnectionConstants.USER;
        password = ConnectionConstants.PASS;
        clientId = "androidSampleClient";
        initMqttConnection();
    }

    public void setMqttObserver(MqttObserver aObserver) {
        mObserver = aObserver;
    }

    private void initMqttConnection() {
        client = new MqttAndroidClient(context, broker, clientId);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection was lost!");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                recivedMessage = new String(message.getPayload());
                System.out.println("Message Arrived!: " + topic + ": " + recivedMessage);
                setNewMessage(recivedMessage);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("Delivery Complete!");
            }
        });

        options = new MqttConnectOptions();
        options.setUserName(user);
        options.setPassword(password.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Connection Success!");
                    try {
                        System.out.println("Subscribing to /iskra");
                        client.subscribe("/test", 0);
                        System.out.println("Subscribed to /iskra");
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
            System.out.println("Subscribing to " + topic);
            client.subscribe(topic, 0);
            System.out.println("Subscribed to " + topic);
        } catch (MqttException ex) {

        }
    }

    public void pub(String topic, String message) {
        System.out.println("Publishing message..");
        try {
            client.publish(topic, new MqttMessage(message.getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void setNewMessage(String msg) {
        // if value has changed notify observers
        if (!recivedMessage.equals(msg)) {
            System.out.println("Message changed to : " + msg);
            recivedMessage = msg;

            // mark as value changed
            mObserver.onMqttResponse(msg);
            System.out.println("MUST BE NOTIFIED");
        }
    }

    public interface MqttObserver {
        public void onMqttResponse(String msg);
    }
}