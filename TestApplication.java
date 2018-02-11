package lnc.paym.Util;

import android.app.Application;

import lnc.paym.RestAPI.MyReceiver;

public class TestApplication extends Application {
 
    private static TestApplication mInstance;
 
    @Override
    public void onCreate() {
        super.onCreate();
 
        mInstance = this;
    }
 
    public static synchronized TestApplication getInstance() {
        return mInstance;
    }
 
    public void setConnectionListener(MyReceiver.ConnectionReceiverListener listener) {
        MyReceiver.connectionReceiverListener = listener;
    }
}