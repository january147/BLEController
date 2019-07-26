package top.january147.blecontroller.Manager;

import java.util.Dictionary;
import java.util.Hashtable;

public class AppAdapterManager {
    Dictionary<String, Object> adapters;

    public AppAdapterManager() {
        adapters = new Hashtable<>();
    }

    public void putAppAdapter(String name, Object adapter) {
        adapters.put(name, adapter);
    }

    public Object getAppAdapter(String name) {
        return adapters.get(name);
    }
}
