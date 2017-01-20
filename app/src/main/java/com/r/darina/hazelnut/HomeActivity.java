package com.r.darina.hazelnut;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity implements Connection.MqttObserver {
    public static final String APP_PREFERENCES = "SensorsDataSettings";

    private ImageButton mSpringOn;
    private ImageButton mWateringOn;
    private ImageButton mConnectOn;
    private TextView mSoilHumidityData;
    private Connection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mSpringOn = (ImageButton) findViewById(R.id.imageButton_Spring);
        mWateringOn = (ImageButton) findViewById(R.id.imageButton_Pour);
        mConnectOn = (ImageButton) findViewById(R.id.imageButton_Connection);
        mSoilHumidityData = (TextView) findViewById(R.id.soilHumidity);

        addListenersOnButtons();

        mConnection = new Connection(getApplicationContext());
        mConnection.setMqttObserver(this);
        setLastSensorData();

    }

    public void addListenersOnButtons() {
        mSpringOn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mConnection.pub("/hazelnutapp", "getHumidity");
            }

        });

        mWateringOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mConnection.pub("/insidetest", "20");
            }
        });

        mConnectOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

            }
        });

    }

    @Override
    public void onMqttResponse(String msg) {
        mSoilHumidityData.setText(msg + "%");
        saveNewData(msg);
    }

    private void saveNewData(String sensorData) {
        SharedPreferences preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("humidity", sensorData);
        editor.apply();
    }

    private void setLastSensorData() {
        SharedPreferences preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        String humidityData = preferences.getString("humidity" + "%", "--");
        mSoilHumidityData.setText(humidityData);
    }
}