package fr.ynryo.spotted.apiResponsesPOJO.path;

import com.google.gson.annotations.SerializedName;

public class SpottedPathPoint {
    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("distance")
    private Double distance;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Double getDistance() {
        return distance;
    }
}
