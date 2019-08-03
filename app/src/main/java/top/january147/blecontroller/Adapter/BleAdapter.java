package top.january147.blecontroller.Adapter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class BleAdapter {
    private static final String TAG = "BleAdapter";

    Context context;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothGatt mBluetoothGatt;
    BluetoothLeScanner mBluetoothLeScanner;
    BluetoothGattCharacteristic mChar;
    List<BluetoothDevice> scanResults;

    public boolean mScanning = false;

    public BleAdapter(Context argContext, BluetoothAdapter bluetoothAdapter) {
        mBluetoothAdapter = bluetoothAdapter;
        mBluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        context = argContext;
        scanResults = new ArrayList<>();
    }

    // 列出所有Service及其characteristic的uuid
    public static void listSerivceUUID(BluetoothGatt gatt) {
        List<BluetoothGattService> services = gatt.getServices();
        for(BluetoothGattService service : services) {
            UUID service_uuid = service.getUuid();
            Log.d(TAG, "onServicesDiscovered: service id is " + service_uuid);
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                UUID characteristic_uuid = characteristic.getUuid();
                Log.d(TAG, "onServicesDiscovered: characteristic id is " + characteristic_uuid);
            }
        }
    }

    public void listResult() {
        for (BluetoothDevice device : scanResults) {
            Log.d(TAG, "listResult: " + device.getName() + " " + device.getAddress());
        }
    }

    BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Connected to GATT server.");
                Log.d(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.d(TAG, "onServicesDiscovered received: " + status);

            BluetoothGattService service = gatt.getService( UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                if (characteristic != null) {
                    mChar = characteristic;
                    Log.d(TAG, "onServicesDiscovered: characteristic get");
                    for(BluetoothGattDescriptor des : mChar.getDescriptors()) {
                        Log.d(TAG, "onServicesDiscovered: descriptor :" + des.getUuid());
                    }
                }
                BluetoothGattDescriptor des = mChar.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                Log.d(TAG, "onServicesDiscovered: descriptor set " + des.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE));
                mBluetoothGatt.writeDescriptor(des);
                mBluetoothGatt.setCharacteristicNotification(mChar, true);

            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            byte[] data = characteristic.getValue();
            Log.d(TAG, "onCharacteristicRead: read success" + data);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] data = characteristic.getValue();
            String message;

            try {
                message = new String(data, "ascii");
            } catch (UnsupportedEncodingException e) {
                Log.d(TAG, "onCharacteristicChanged: error coding");
                message = Arrays.toString(data);
            }
            Log.d(TAG, "onCharacteristicChanged: read success:" + message);
        }


    };

    ScanCallback mBleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice mDevice = result.getDevice();
            Log.d(TAG, "onScanResult:" + mDevice.getName());
            if (scanResults.contains(mDevice)) {
                Log.d(TAG, "onScanResult: repeat");
            } else {
                scanResults.add(mDevice);

                Intent broadcastTest = new Intent("ble.SCAN_RESULT");
                broadcastTest.putExtra("message", "find a device");
                broadcastTest.putExtra("device", mDevice.getName());
                context.sendBroadcast(broadcastTest);

                // 一段时间没有接收到广播则删除该项目
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        scanResults.remove(0);
                    }
                }, 1000 * 20);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult result : results) {
                BluetoothDevice mDevice =  result.getDevice();
                Log.d(TAG, "onBatchScanResults:" + mDevice.getName());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d(TAG, "onScanFailed:" + String.valueOf(errorCode));
        }
    };

    public boolean isEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public boolean scanBleDevice(boolean enable) {

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "scanBleDevice: bluetooth not enabled");
            return false;
        }

        Timer mTimer;
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (enable) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothLeScanner.stopScan(mBleScanCallback);
                }
            }, 30 * 1000);
            mScanning = true;
            /*
            ScanSettings.Builder mScanSettingBuilder = new ScanSettings.Builder();
            ScanSettings settings = mScanSettingBuilder
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .setReportDelay(1000).build();
            */
            mBluetoothLeScanner.startScan(mBleScanCallback);
        } else {
            mScanning = false;
            mBluetoothLeScanner.stopScan(mBleScanCallback);
        }
        return true;
    }

    public boolean sendData(String data) {

        if (mChar == null) {
            Log.d(TAG, "sendData: bluetooth connection not ready, can't send data");
            return false;
        }
        //将指令放置进来
        byte[] byte_data = data.getBytes();
        if(byte_data.length > 20) {
            Log.d(TAG, "sendData: data too long to send");
            return false;
        }
        Log.d(TAG, "sendData: " + data);
        mChar.setValue(byte_data);
        //开始写数据
        mBluetoothGatt.writeCharacteristic(mChar);
        return true;
    }

    public boolean disconnect() {
        if (mBluetoothGatt == null) {
            Log.d(TAG, "disconnect: bluetooth not conntected");
            return false;
        }
        mBluetoothGatt.close();
        reset();
        return true;
    }

    public boolean connect(String name) {
        for(BluetoothDevice mDevice : scanResults) {
            if (name.equals(mDevice.getName())) {
                mBluetoothLeScanner.stopScan(mBleScanCallback);
                mBluetoothGatt = mDevice.connectGatt(BleAdapter.this.context, false, gattCallback);
                return true;
            }
        }
        return false;
    }

    private void reset() {
        mBluetoothGatt = null;
        mChar = null;
    }


}
