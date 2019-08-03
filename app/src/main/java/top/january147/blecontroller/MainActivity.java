package top.january147.blecontroller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import top.january147.blecontroller.Adapter.BleAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    BleAdapter mBle;
    BleBroadcastReceiver mBleBroadcastReceiver;

    final int AT_REQUEST_ENABLE_BT = 1;


    class BleBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: find device: " + intent.getStringExtra("device"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enableBluetoothAdapter();
        IntentFilter bleBroadcastfilter = new IntentFilter("ble.SCAN_RESULT");
        mBleBroadcastReceiver = new BleBroadcastReceiver();
        registerReceiver(mBleBroadcastReceiver, bleBroadcastfilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBleBroadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: activity return, request code is " + requestCode);
        Log.d(TAG, "onActivityResult: activity return, result code is " + resultCode);
        if (requestCode == AT_REQUEST_ENABLE_BT) {
            switch(resultCode) {
                case 0:
                    Toast.makeText(this, "您拒绝了开启蓝牙的请求， 请在设置中自行打开蓝牙", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(this, "蓝牙已开启", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "未知结果", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public void enableBluetoothAdapter() {
        MyApplication app = (MyApplication)getApplication();
        mBle = (BleAdapter) app.getAppAdapterManager().getAppAdapter("adapter.BLE");

        // BleAdapter已经初始化过了
        if ( mBle != null) {
            Log.d(TAG, "enableBluetoothAdapter: Ble adapter has already set up");
            return;
        }

        // 初始化BleAdapter
        BluetoothManager mBluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = mBluetoothManager.getAdapter();
        //确保蓝牙在设备上可用并且已启用, 如果没有启用，
        //显示一个对话框，请求用户启用蓝牙的权限。

        // 设备不支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplication(), "该设备不支持蓝牙, 应用即将退出", Toast.LENGTH_SHORT).show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000 * 3);
            return;
        }

        mBle = new BleAdapter(this, mBluetoothAdapter);
        app.getAppAdapterManager().putAppAdapter("adapter.BLE", mBle);

        // 蓝牙未开启,请求开启蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, AT_REQUEST_ENABLE_BT);
        }
    }


    public void onButtonScanClick(View v) {
        if (mBle.mScanning) {
            mBle.scanBleDevice(false);
            Log.d(TAG, "onButtonScanClick: stop scan");

        } else {

            mBle.scanBleDevice(true);
            Log.d(TAG, "onButtonScanClick: start scan");
        }

    }
    
    public void onButtonSendClick(View v) {
        mBle.sendData("hello\n");
        Log.d(TAG, "onButtonSendClick: ");
    }
    
    public void onButtonReadClick(View v) {
        Log.d(TAG, "onButtonReadClick:");
    }

    public void onButtonForwardClick(View v) {
        mBle.sendData("forward\n");
        Log.d(TAG, "onButtonForwardClick: ");
    }

    public void onButtonBackwardClick(View v) {
        mBle.sendData("backward\n");
        Log.d(TAG, "onButtonBackwardClick: ");
    }

    public void onButtonStopClick(View v) {
        mBle.sendData("stop\n");
        Log.d(TAG, "onButtonStopClick: ");
    }

    public void onButtonDisConnectClick(View v) {
        mBle.disconnect();
        Log.d(TAG, "onButtonDisConnectClick: ");
    }

    public void onButtonEnableBluetoothClick(View v) {
        // 蓝牙未开启,请求开启蓝牙
        if (mBle.isEnabled()) {
            Toast.makeText(this, "蓝牙已开启, 无需重复开启", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, AT_REQUEST_ENABLE_BT);
        Log.d(TAG, "onButtonEnableBluetoothClick: ");
    }

    public void onButtonTestClick(View v) {
        Intent ssIntent = new Intent(this, BleIntentService.class);
        ssIntent.setAction("ACTION_CONNECT");
        startService(ssIntent);
    }

    public void onButtonScanResultClick(View v) {
        Intent startScanResultIntent = new Intent(this, ScanResultActivity.class);
        startActivity(startScanResultIntent);
    }

    public void onButtonToLeftClick(View v) {
        mBle.sendData("to_left\n");
        Log.d(TAG, "onButtonToLeftClick: ");
    }

    public void onButtonToRightClick(View v) {
        mBle.sendData("to_right\n;");
        Log.d(TAG, "onButtonToRightClick: ");
    }

    public void onButtonConnectClick(View v) {
        mBle.connect("bleTrans");
    }


}
