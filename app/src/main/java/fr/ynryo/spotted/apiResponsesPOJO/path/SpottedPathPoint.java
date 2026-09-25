package fr.ynryo.spotted.apiResponsesPOJO.path;

import com.google.gson.annotations.SerializedName;

public class SpottedPathPoint {
    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("distance")
    private Double distance;

    public SpottedPathPoint() {
    }

    public SpottedPathPoint(double latitude, double longitude, Double distance) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

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
