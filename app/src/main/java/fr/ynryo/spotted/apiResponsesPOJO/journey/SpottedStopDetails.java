package fr.ynryo.spotted.apiResponsesPOJO.journey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import fr.ynryo.spotted.genericMarkerDatas.StopFlag;
import fr.ynryo.spotted.genericMarkerDatas.StopStatus;
import fr.ynryo.spotted.genericMarkerDatas.StopType;

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

    public StopType getStopType() {
        switch (stopType) {
            case "START":
                return StopType.START;
            case "INTERMEDIATE":
                return StopType.INTERMEDIATE;
            case "SPLIT":
                return StopType.SPLIT;
            case "END":
                return StopType.END;
        }
        return StopType.INTERMEDIATE;
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

    public StopStatus getCallStatus() {
        switch (callStatus) {
            case "SCHEDULED":
                return StopStatus.SCHEDULED;
            case "UNSCHEDULED":
                return StopStatus.UNSCHEDULED;
            case "SKIPPED":
                return StopStatus.SKIPPED;
        }
        return StopStatus.SCHEDULED;
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

    public List<StopFlag> getFlags() {
        List<StopFlag> stopFlags = new ArrayList<>();
        for (String flag : flags) {
            switch (flag) {
                case "NO_PICKUP":
                    stopFlags.add(StopFlag.NO_PICKUP);
                case "NO_DROPOFF":
                    stopFlags.add(StopFlag.NO_DROPOFF);
                default:
                    stopFlags.add(StopFlag.BOTH);
            }
        }
        return stopFlags;
    }
}
