package top.january147.blecontroller;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import top.january147.blecontroller.Adapter.BleAdapter;

public class ScanResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

    }

    public void onButtonShowResultClick(View v) {
        MyApplication app = (MyApplication)getApplication();
        BleAdapter mBle = (BleAdapter) app.getAppAdapterManager().getAppAdapter("adapter.BLE");

        if (mBle != null) {
            mBle.listResult();
        }
    }
}
