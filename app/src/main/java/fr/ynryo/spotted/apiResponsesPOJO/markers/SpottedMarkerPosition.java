package fr.ynryo.spotted.apiResponsesPOJO.markers;

import androidx.annotation.NonNull;

public class SpottedMarkerPosition {
    private double latitude;
    private double longitude;
    private float bearing;
    private boolean atStop;
    private float distanceTraveled;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getBearing() {
        return bearing;
    }

    public boolean isAtStop() {
        return atStop;
    }

    public float getDistanceTraveled() {
        return distanceTraveled;
    }

    @NonNull
    @Override
    public String toString() {
        return "SpottedMarkerPosition{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", bearing=" + bearing +
                '}';
    }
}
