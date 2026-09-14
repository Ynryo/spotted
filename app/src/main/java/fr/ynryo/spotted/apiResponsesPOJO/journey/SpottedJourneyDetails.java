package fr.ynryo.spotted.apiResponsesPOJO.journey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import fr.ynryo.spotted.apiResponsesPOJO.markers.BusTrackerMarkerPosition;
import fr.ynryo.spotted.apiResponsesPOJO.network.BusTrackerNetworkData;
import fr.ynryo.spotted.apiResponsesPOJO.path.SpottedPathResponse;

public class SpottedJourneyDetails {
    @SerializedName("id")
    private String id;

    @SerializedName("lineId")
    private int lineId;

    @SerializedName("vehicleNumber")
    private String vehicleNumber;

    @SerializedName("vehicleType")
    private String vehicleType;

    @SerializedName("destination")
    private String destination;

    @SerializedName("serviceDate")
    private String serviceDate;

    @SerializedName("stops")
    private List<SpottedStopDetails> stops;

    @SerializedName("position")
    private BusTrackerMarkerPosition position;

    @SerializedName("network")
    private BusTrackerNetworkData network;

    @SerializedName("path")
    private SpottedPathResponse path;

    @SerializedName("updatedAt")
    private String updatedAt;

    public String getId() {
        return id;
    }

    public int getLineId() {
        return lineId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getDestination() {
        return destination;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public List<SpottedStopDetails> getStops() {
        return stops;
    }

    public BusTrackerMarkerPosition getPosition() {
        return position;
    }

    public BusTrackerNetworkData getNetwork() {
        return network;
    }

    public SpottedPathResponse getPath() {
        return path;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
