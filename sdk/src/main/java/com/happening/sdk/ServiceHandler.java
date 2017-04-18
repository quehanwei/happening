package com.happening.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.happening.IAsyncCallback;
import com.happening.IRemoteHappening;
import com.happening.service.HappeningService;

public class ServiceHandler {

    private static ServiceHandler sh = null;
    private RemoteServiceConnection serviceConnection;
    private IRemoteHappening service;
    private CallbackInterface onClientDiscoverCallback = null;

    private ServiceHandler() {
    }

    public static ServiceHandler getInstance() {
        if (sh == null)
            sh = new ServiceHandler();
        return sh;
    }

    private Context getContext() {
        return HappeningClient.getHappeningClient().getAppContext();
    }

    /**
     * This is our function which binds our activity(MainActivity) to our service(AddService).
     */
    private void initService() {
        serviceConnection = new RemoteServiceConnection();
        Intent i = new Intent(this.getContext(), HappeningService.class);
        i.setPackage("com.happening.happening_service");
        this.getContext().startService(i);
        boolean ret = this.getContext().bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(this.getClass().getSimpleName(), "initService() bound value: " + ret);
    }

    /**
     * This is our function to un-binds this activity from our service.
     */
    private void releaseService() {
        if (serviceConnection != null) {
            getContext().unbindService(serviceConnection);
            serviceConnection = null;
            service = null;
            Log.d(this.getClass().getSimpleName(), "releaseService(): unbound.");
        }
    }

    public void startService() {
        if (!isRunning())
            Log.d("jojo", "INIT start");
        initService();
    }

    public void stopService() {
        if (isRunning())
            releaseService();
    }

    public Boolean isRunning() {
        return service != null;
    }

    public void registerDeviceDiscover(CallbackInterface callback) {
        try {
            service.methodOne(mCallback);
            this.onClientDiscoverCallback = callback;
//            Log.d("jojo", String.valueOf(this.onClientDiscoverCallback));
        } catch (RemoteException e) {
        }
    }

    IAsyncCallback.Stub mCallback = new IAsyncCallback.Stub() {
        public void handleResponse(String name) throws RemoteException {
            Log.d("jojo", name);
            discover();
//            onClientDiscoverCallback.onClientDiscovered(name);
        }
    };

    void discover() {
        Log.d("jojo", String.valueOf(this.onClientDiscoverCallback));
    }

    class RemoteServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IRemoteHappening.Stub.asInterface((IBinder) boundService);
            Toast.makeText(HappeningClient.getHappeningClient().getAppContext(), "Service connected", Toast.LENGTH_LONG).show();
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Toast.makeText(HappeningClient.getHappeningClient().getAppContext(), "Service disconnected", Toast.LENGTH_LONG).show();
        }
    }

}
