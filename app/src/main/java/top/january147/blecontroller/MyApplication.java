package top.january147.blecontroller;

import android.app.Application;

import top.january147.blecontroller.Manager.AppAdapterManager;

public class MyApplication extends Application {
    private AppAdapterManager aam;

    public MyApplication() {
        super();
        aam = new AppAdapterManager();
    }

    public AppAdapterManager getAppAdapterManager() {
        return aam;
    }
}
