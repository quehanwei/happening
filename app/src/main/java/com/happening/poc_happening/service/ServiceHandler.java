package com.happening.poc_happening.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.happening.IRemoteHappening;
import com.happening.lib.BluetoothDevice;
import com.happening.service.HappeningService;

import com.happening.poc_happening.MyApp;

import java.util.List;


public class ServiceHandler {

    private static ServiceHandler sh = null;
    private Context context = null;

    private IRemoteHappening service;
    private RemoteServiceConnection serviceConnection;

    private ServiceHandler() {
        this.context = MyApp.getAppContext();
    }

    public static ServiceHandler getInstance() {
        if (sh == null)
            sh = new ServiceHandler();
        return sh;
    }

    /**
     * This is our function which binds our activity(MainActivity) to our service(AddService).
     */
    private void initService() {
        serviceConnection = new RemoteServiceConnection();
        Intent i = new Intent(context, HappeningService.class);
        i.setPackage("com.happening.happening_service");
        this.context.startService(i);
        boolean ret = this.context.bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(this.getClass().getSimpleName(), "initService() bound value: " + ret);
    }

    /**
     * This is our function to un-binds this activity from our service.
     */
    private void releaseService() {
        if (serviceConnection != null) {
            context.unbindService(serviceConnection);
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
        return service != null ? true : false;
    }

    public IRemoteHappening getService() {
        return serviceWrapper;
    }

    class RemoteServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IRemoteHappening.Stub.asInterface((IBinder) boundService);
            Toast.makeText(MyApp.getAppContext(), "Service connected", Toast.LENGTH_LONG).show();
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Toast.makeText(MyApp.getAppContext(), "Service disconnected", Toast.LENGTH_LONG).show();
        }
    }

    private final IRemoteHappening.Stub serviceWrapper = new IRemoteHappening.Stub() {

        @Override
        public void addDevice(String name) {
            try {
                service.addDevice(name);
            } catch (RemoteException e) {
            }
        }

        @Override
        public BluetoothDevice getDevice(String name) {
            try {
                return service.getDevice(name);
            } catch (RemoteException e) {
                return null;
            }
        }

        @Override
        public List<BluetoothDevice> getDevices() {
            try {
                return service.getDevices();
            } catch (RemoteException e) {
                return null;
            }
        }

        @Override
        public void enableAdapter() {
            try {
                service.enableAdapter();
            } catch (RemoteException e) {
            }
        }

        @Override
        public void disableAdapter() {
            try {
                service.disableAdapter();
            } catch (RemoteException e) {
            }
        }

        @Override
        public boolean isBtAdapterEnabled() {
            try {
                return service.isBtAdapterEnabled();
            } catch (RemoteException e) {
                return false;
            }
        }

        @Override
        public void startScan() {
            try {
                service.startScan();
            } catch (RemoteException e) {
            }
        }

        @Override
        public void stopScan() {
            try {
                service.stopScan();
            } catch (RemoteException e) {
            }
        }

        @Override
        public void startAdvertising() {
            try {
                service.startAdvertising();
            } catch (RemoteException e) {
            }
        }

        @Override
        public void stopAdvertising() {
            try {
                service.stopAdvertising();
            } catch (RemoteException e) {
            }
        }

        @Override
        public boolean isAdvertisingSupported() {
            try {
                return service.isAdvertisingSupported();
            } catch (RemoteException e) {
                return false;
            }
        }

        @Override
        public void createGattServer() {
            try {
                service.createGattServer();
            } catch (RemoteException e) {
            }
        }

        @Override
        public void stopGattServer() {
            try {
                service.stopGattServer();
            } catch (RemoteException e) {
            }
        }

        @Override
        public void broadcastMessage(String message) {
            try {
                service.broadcastMessage(message);
            } catch (RemoteException e) {
            }
        }
    };
}
