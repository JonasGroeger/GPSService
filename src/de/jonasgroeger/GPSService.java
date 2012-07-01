package de.jonasgroeger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;

/**
 * This service allowes to register Activities that get a callback with the
 * location from GPS. Currently there is only a dummy GPS location returned.
 * 
 * @author Jonas Gröger
 */
public class GPSService extends Service {
    private static final int                  ONE_SECOND = 1000;
    private Map<Activity, IListenerFunctions> clients    = new ConcurrentHashMap<Activity, IListenerFunctions>();
    private final Binder                      binder     = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();

        // Starts the thread that sends the Location regularly to the clients
        new Thread(gpsRunner).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return (binder);
    }

    private void updateLocationOnClient(final Activity client) {
        // Get the location
        final Location dummyLocation = getRealLocation();
        try {

            // Call the setLocation in the main thread (ui thread) as it updates
            // the ui.
            // If we dont use the handler and just exec the code in the run() we
            // get a CalledFromWrongThreadException
            Handler lo = new Handler(Looper.getMainLooper());
            lo.post(new Runnable() {

                public void run() {
                    IListenerFunctions callback = clients.get(client);
                    callback.setLocation(dummyLocation.getLatitude(), dummyLocation.getLongitude());
                }
            });

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    // You might want to change that thing :)
    private Location getRealLocation() {
        Location dummyLocation = new Location("dummy provider");
        dummyLocation.setLatitude(Math.random());
        dummyLocation.setLongitude(Math.random());
        return dummyLocation;
    }

    private Runnable gpsRunner = new Runnable() {
                                   public void run() {

                                       while (true) {
                                           for (Activity client : clients.keySet()) {
                                               updateLocationOnClient(client);
                                           }

                                           // Wait a little to NOT spam the
                                           // client activity
                                           SystemClock.sleep(ONE_SECOND * 2);
                                       }

                                   }
                               };

    public class LocalBinder extends Binder implements IServiceFunctions {

        // Registers a Activity to receive updates
        public void registerActivity(Activity activity, IListenerFunctions callback) {
            clients.put(activity, callback);
        }

        public void unregisterActivity(Activity activity) {
            clients.remove(activity);
        }

        public Location getLocation() {
            return getRealLocation();
        }
    }
}