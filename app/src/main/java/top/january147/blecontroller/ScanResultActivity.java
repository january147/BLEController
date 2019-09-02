package top.january147.blecontroller;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import top.january147.blecontroller.Adapter.BleAdapter;

public class ScanResultActivity extends AppCompatActivity {

    Timer autoFlashTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        TextView display = findViewById(R.id.textView);
        display.setText("点击上面的按钮刷新");
        setAutoFlash();

    }

    public void showResult() {
        MyApplication app = (MyApplication)getApplication();
        BleAdapter mBle = (BleAdapter) app.getAppAdapterManager().getAppAdapter("adapter.BLE");

        if (mBle != null) {
            mBle.listResult();

            TextView display = findViewById(R.id.textView);
            List<BluetoothDevice> devices = mBle.getScanResults();
            if (devices.size() > 0) {
                display.setText("");
                for (BluetoothDevice device : devices) {
                    String name = device.getName();
                    if (name == null) {
                        name = device.getAddress();
                    }
                    display.append(name + '\n');
                }
            } else {
                display.setText("no device");
            }
        }
    }

    public void setAutoFlash() {
        int interval = 3;
        final Handler handler = new Handler();
        autoFlashTimer = new Timer();
        autoFlashTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //由于timer会使用新的线程来执行任务, 所以不能直接在其中执行更新UI的操作
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ScanResultActivity.this.showResult();
                    }
                });
            }
        }, 1000 * interval, 1000 * interval);
    }

    public void onButtonShowResultClick(View v) {
        showResult();
    }
}
