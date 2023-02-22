package com.gs.bluetoothdemo;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    private Button btnBluetoothState;
    private TextView tvBluetoothStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBluetoothStatus = findViewById(R.id.tv_bluetooth_status);
        btnBluetoothState = findViewById(R.id.btn_change_bt_state);

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = btAdapter.isEnabled();

        Log.d(TAG, "Current BT status : " + isEnabled);
        if (isEnabled) {
            updateUI("Bluetooth is connected", "Turn OFF");
        }

        btnBluetoothState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    if (btAdapter.isEnabled()) {
                        btAdapter.disable();
                    } else {
                        btAdapter.enable();
                    }
                } else {
                    requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                }
            }
        });
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        updateUI("Bluetooth Disconnected", "Turn ON");
                        //Indicates the local Bluetooth adapter is off.
                        break;

                    case BluetoothAdapter.STATE_TURNING_ON:
                        updateUI("Bluetooth Turning ON...", "Turn OFF");
                        //Indicates the local Bluetooth adapter is turning on. However local clients should wait for STATE_ON before attempting to use the adapter.
                        break;

                    case BluetoothAdapter.STATE_ON:
                        updateUI("Bluetooth Connected", "Turn OFF");
                        //Indicates the local Bluetooth adapter is on, and ready for use.
                        break;

                    case BluetoothAdapter.STATE_TURNING_OFF:
                        updateUI("Bluetooth Turning OFF", "Turn ON");
                        //Indicates the local Bluetooth adapter is turning off. Local clients should immediately attempt graceful disconnection of any remote links.
                        break;
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
    }

    private void updateUI(String state, String btnText) {
        tvBluetoothStatus.setText(state);
        btnBluetoothState.setText(btnText);

        if (btnText.equalsIgnoreCase("Turn on")) {
            tvBluetoothStatus.setTextColor(Color.parseColor("#F44336"));
            btnBluetoothState.setBackgroundColor(Color.parseColor("#9C27B0"));
        } else {
            tvBluetoothStatus.setTextColor(Color.parseColor("#4CAF50"));
            btnBluetoothState.setBackgroundColor(Color.parseColor("#FF018786"));
        }
    }
}