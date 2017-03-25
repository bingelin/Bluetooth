package com.linjinbin.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import static android.bluetooth.BluetoothDevice.ACTION_FOUND;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt_start;
    private Button bt_seach;
    private TextView tv_devices;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private static final String TAG = "Bingelin";

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    tv_devices.append(device.getName() + ":" + device.getAddress());
                    log(device.getName() + ":" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                toast("搜索完成");
                bt_seach.setText("SEARCH");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registBluetoothBro();
        tv_devices = (TextView) findViewById(R.id.main_tv_devices);
        bt_start = (Button) findViewById(R.id.main_bt_start);
        bt_seach = (Button) findViewById(R.id.main_bt_search);
        bt_start.setOnClickListener(this);
        bt_seach.setOnClickListener(this);
        getCompDevices();


    }

    private void registBluetoothBro() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.setPriority(Integer.MAX_VALUE);
        this.registerReceiver(receiver, filter);
    }

    private void getCompDevices() {
        Set<BluetoothDevice> compDevices = mBluetoothAdapter.getBondedDevices();
        if (compDevices.size() > 0) {
            for (BluetoothDevice device : compDevices) {
                tv_devices.append(device.getName() + ":" + device.getAddress() + "\n");
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_bt_start:
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
                break;
            case R.id.main_bt_search:
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                mBluetoothAdapter.startDiscovery();
                bt_seach.setText("正在搜索......");
                break;
        }
    }

    public void toast(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    public void log(String s) {
        Log.i(TAG, s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
