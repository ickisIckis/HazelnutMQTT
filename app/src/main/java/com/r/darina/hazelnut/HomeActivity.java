package com.r.darina.hazelnut;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class HomeActivity extends AppCompatActivity implements Connection.MqttObserver {
    public static final String APP_PREFERENCES = "hazelnutsettings";
    public static final String APP_PREFERENCES_HUMIDITY = "100";
    private SharedPreferences mSettings;

    private ImageButton mSpringOn;
    private ImageButton mWateringOn;
    private ImageButton mConnectOn;
    private TextView mSoilHumidityData;
    private Connection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        if (mSettings.contains(APP_PREFERENCES_HUMIDITY)) {
            mSoilHumidityData.setText(mSettings.getString(APP_PREFERENCES_HUMIDITY, "??"));
        }

        mSpringOn = (ImageButton) findViewById(R.id.imageButton_Spring);
        mWateringOn = (ImageButton) findViewById(R.id.imageButton_Pour);
        mConnectOn = (ImageButton) findViewById(R.id.imageButton_Connection);
        mSoilHumidityData = (TextView) findViewById(R.id.soilHumidity);

        addListenersOnButtons();

        mConnection = new Connection(getApplicationContext());
        mConnection.setMqttObserver(this);

    }

    public void addListenersOnButtons() {
        mSpringOn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mConnection.pub("/test", "hello from connection class");
                Toast.makeText(HomeActivity.this,
                        "Spring!", Toast.LENGTH_SHORT).show();

            }

        });

        mWateringOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(HomeActivity.this,
                        "pour", Toast.LENGTH_SHORT).show();
            }
        });

        mConnectOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Toast.makeText(HomeActivity.this,
                        "click!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onMqttResponse(String msg) {
        System.out.println("!!!!!!!!!");
        SharedPreferences.Editor editor = mSettings.edit();
        System.out.println("Update called with Arguments: " + msg);
    }

}