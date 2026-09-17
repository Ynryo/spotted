package fr.ynryo.spotted.apiResponsesPOJO.markers;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpottedMarkersResponse {
    @SerializedName("count")
    private int count;

    @SerializedName("data")
    private List<SpottedMarkerData> data;

    @SerializedName("datetime")
    private String datetime;

    public int getCount() {
        return count;
    }

    public List<SpottedMarkerData> getData() {
        return data;
    }

    public String getDatetime() {
        return datetime;
    }

    @NonNull
    @Override
    public String toString() {
        return "SpottedMarkersResponse{" +
                "count=" + count +
                ", dataCount=" + (data != null ? data.size() : 0) +
                ", datetime='" + datetime + '\'' +
                '}';
    }
}
