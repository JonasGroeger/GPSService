package de.jonasgroeger;

/**
 * The client must have one Object implementing this interface to get callbacks
 * from the service. This is the callback.
 * 
 * @author Jonas Gröger
 */
public interface IListenerFunctions {

    void setLocation(double lat, double lon);
}