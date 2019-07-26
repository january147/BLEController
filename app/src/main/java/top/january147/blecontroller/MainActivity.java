package top.january147.blecontroller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import top.january147.blecontroller.Adapter.BleAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    BleAdapter mBle;

    final int REQUEST_ENABLE_BT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableBluetoothAdapter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: activity return, request code is " + requestCode);
        Log.d(TAG, "onActivityResult: activity return, result code is " + resultCode);
        if (requestCode == REQUEST_ENABLE_BT) {
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
        BluetoothManager mBluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = mBluetoothManager.getAdapter();
        //确保蓝牙在设备上可用并且已启用, 如果没有启用，
        //显示一个对话框，请求用户启用蓝牙的权限。
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        mBle = new BleAdapter(this, mBluetoothAdapter);
        MyApplication app = (MyApplication)getApplication();
        app.getAppAdapterManager().putAppAdapter("adapter.BLE", mBle);
    }


    public void onButtonConnectClick(View v) {
        if (mBle.mScanning) {
            mBle.scanBleDevice(false);
            Log.d(TAG, "onButtonConnectClick: stop scan");

        } else {

            mBle.scanBleDevice(true);
            Log.d(TAG, "onButtonConnectClick: start scan");
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
        enableBluetoothAdapter();
        Log.d(TAG, "onButtonEnableBluetoothClick: ");
    }

    public void onButtonTestClick(View v) {
        Intent ssIntent = new Intent(this, BleIntentService.class);
        ssIntent.setAction("ACTION_CONNECT");
        startService(ssIntent);
    }

}
