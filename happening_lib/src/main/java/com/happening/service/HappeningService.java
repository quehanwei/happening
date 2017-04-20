package com.happening.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.happening.lib.BluetoothDevice;
import com.happening.lib.IRemoteDeviceService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kaischulz on 22.01.17.
 */
public class HappeningService extends Service {

    private final IRemoteDeviceService.Stub mBinder = new IRemoteDeviceService.Stub() {

        List<BluetoothDevice> devices = Collections.synchronizedList(new ArrayList<BluetoothDevice>());

        @Override
        public void addDevice(String name) throws RemoteException {

            // check if device is already in device list
            for (BluetoothDevice d : devices) {
                Log.d("name", d.getName());
                if (d.getName().equals(name)) {
                    Log.d("already in list", name);
                    return;
                }
            }

            // add if not in device list
            BluetoothDevice device = new BluetoothDevice(name);
            devices.add(device);
            Log.d("add device", name);

        }

        @Override
        public BluetoothDevice getDevice(String name) throws RemoteException {
            for (BluetoothDevice device : devices) {
                if (device.getName().equalsIgnoreCase(name)) {
                    Log.d("get device", device.toString());
                    return device;
                }
            }
            return null;
        }

        @Override
        public List<BluetoothDevice> getDevices() throws RemoteException {
            Log.d("get device", String.valueOf(devices));
            return devices;
        }

    };

    /**
     * indicates how to behave if the service is killed
     */
    int mStartMode = START_STICKY;
    /**
     * indicates whether onRebind should be used
     */
    boolean mAllowRebind = true;

    /**
     * Called when the service is being created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this.getClass().getSimpleName(), "onCreate " + this.toString());
    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(this.getClass().getSimpleName(), "onStartCommand");
        return mStartMode;
    }

    /**
     * Called when all clients have unbound with unbindService()
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(this.getClass().getSimpleName(), "onUnbind");
        return mAllowRebind;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {
        Log.d(this.getClass().getSimpleName(), "onRebind");
    }

    /**
     * Called when The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {
        Log.d(this.getClass().getSimpleName(), "onDestroy");
    }

    /**
     * A client is binding to the service with bindService()
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(this.getClass().getSimpleName(), "onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }

}