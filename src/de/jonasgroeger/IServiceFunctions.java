package de.jonasgroeger;

import android.app.Activity;
import android.location.Location;

/**
 * Interface for the Service.
 * 
 * @author Jonas Gröger
 */
public interface IServiceFunctions {
    void registerActivity(Activity activity, IListenerFunctions callback);

    void unregisterActivity(Activity activity);

    Location getLocation();
}