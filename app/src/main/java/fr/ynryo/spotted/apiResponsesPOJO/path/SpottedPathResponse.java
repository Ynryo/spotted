package fr.ynryo.spotted.apiResponsesPOJO.path;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpottedPathResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("points")
    private List<SpottedPathPoint> points;

    @SerializedName("count")
    private int count;

    public String getId() {
        return id;
    }

    public List<SpottedPathPoint> getPoints() {
        return points;
    }

    public int getCount() {
        return count;
    }
}
