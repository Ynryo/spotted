package fr.ynryo.spotted.apiResponsesPOJO.journey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpottedStopDetails {
    @SerializedName("stopName")
    private String stopName;

    @SerializedName("stopOrder")
    private int stopOrder;

    @SerializedName("stopUIC")
    private String stopUIC;

    @SerializedName("stopType")
    private String stopType;

    @SerializedName("arrivalAimedTime")
    private String arrivalAimedTime;

    @SerializedName("arrivalExpectedTime")
    private String arrivalExpectedTime;

    @SerializedName("arrivalTimeDifference")
    private Integer arrivalTimeDifference;

    @SerializedName("stopDuration")
    private Integer stopDuration;

    @SerializedName("departureAimedTime")
    private String departureAimedTime;

    @SerializedName("departureExpectedTime")
    private String departureExpectedTime;

    @SerializedName("departureTimeDifference")
    private Integer departureTimeDifference;

    @SerializedName("callStatus")
    private String callStatus;

    @SerializedName("distanceTraveled")
    private double distanceTraveled;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("platform")
    private SpottedStopPlatform platform;

    @SerializedName("flags")
    private List<String> flags;

    public String getStopName() {
        return stopName;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public String getStopUIC() {
        return stopUIC;
    }

    public String getStopType() {
        return stopType;
    }

    public String getArrivalAimedTime() {
        return arrivalAimedTime;
    }

    public String getArrivalExpectedTime() {
        return arrivalExpectedTime;
    }

    public Integer getArrivalTimeDifference() {
        return arrivalTimeDifference;
    }

    public Integer getStopDuration() {
        return stopDuration;
    }

    public String getDepartureAimedTime() {
        return departureAimedTime;
    }

    public String getDepartureExpectedTime() {
        return departureExpectedTime;
    }

    public Integer getDepartureTimeDifference() {
        return departureTimeDifference;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public double getDistanceTraveled() {
        return distanceTraveled;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public SpottedStopPlatform getPlatform() {
        return platform;
    }

    public List<String> getFlags() {
        return flags;
    }
}
