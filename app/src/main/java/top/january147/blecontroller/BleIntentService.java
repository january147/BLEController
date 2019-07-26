package top.january147.blecontroller;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import top.january147.blecontroller.Adapter.BleAdapter;

public class BleIntentService extends IntentService {
    private static final String TAG = "BleIntentService";


    public BleIntentService() {
        super("BleIntentService");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch(intent.getAction()) {
            case "ACTION_CONNECT":
                Log.d(TAG, "onHandleIntent: acion_connect");
                MyApplication app = (MyApplication)getApplication();
                BleAdapter mBle = (BleAdapter) app.getAppAdapterManager().getAppAdapter("adapter.BLE");
                mBle.scanBleDevice(true);
                break;
            default:
                Log.d(TAG, "onHandleIntent: unknown actoin");
        }
    }


}
