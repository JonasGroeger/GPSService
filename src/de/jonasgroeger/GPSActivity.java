package de.jonasgroeger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import apt.tutorial.two.R;

/**
 * This activity gets updated by the GPSService regularly (every 2s) with GPS
 * coordinates. It can also query for the location itself.
 * 
 * @author Jonas Gröger
 */
public class GPSActivity extends Activity {

    // Interface stuff
    private TextView          lat;
    private TextView          lon;
    private Button            getLocation;
    private TextView          time;

    // The service
    private IServiceFunctions service = null;

    // The service connection to talk to the service
    private ServiceConnection svcConn = new ServiceConnection() {

                                          // We register ourselves to the
                                          // service so that we can receive
                                          // updates
                                          public void onServiceConnected(ComponentName className, IBinder binder) {
                                              service = (IServiceFunctions) binder;

                                              try {
                                                  service.registerActivity(GPSActivity.this, listener);
                                              } catch (Throwable t) {
                                              }
                                          }

                                          public void onServiceDisconnected(ComponentName className) {
                                              service = null;
                                          }
                                      };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Interface related
        lat = (TextView) findViewById(R.id.latText);
        lon = (TextView) findViewById(R.id.lonText);
        getLocation = (Button) findViewById(R.id.getLocation);
        time = (TextView) findViewById(R.id.timeText);

        // If button is clicked, update the interface
        getLocation.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Location location = service.getLocation();
                setInterface(location.getLatitude(), location.getLongitude());

            }
        });
        lat.setText("asd");

        // Create and start the service
        startService(new Intent(this, GPSService.class));
        bindService(new Intent(this, GPSService.class), svcConn, BIND_AUTO_CREATE);
    }

    // Sets the UI to the values provided and adds the current date aswell
    private void setInterface(double lati, double loni) {
        lat.setText("Lat: " + lati);
        lon.setText("Lon: " + loni);

        time.setText(getCurrentTime());
    }

    private String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar.getInstance().setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Deactivate updates to us so that we dont get callbacks no more.
        service.unregisterActivity(this);

        // Finally stop the service
        unbindService(svcConn);
    }

    // This is essentially the callback that the service uses to notify us about
    // changes.
    private IListenerFunctions listener = new IListenerFunctions() {
                                            public void setLocation(double lat, double lon) {
                                                setInterface(lat, lon);
                                            }
                                        };
}