package fr.ynryo.spotted.apiResponsesPOJO.path;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpottedPathResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("segments")
    private List<List<SpottedPathPoint>> segments;

    @SerializedName("deviatedSegments")
    private List<List<SpottedPathPoint>> deviatedSegments;

    public String getId() {
        return id;
    }

    public List<List<SpottedPathPoint>> getSegments() {
        return segments;
    }

    public List<List<SpottedPathPoint>> getDeviatedSegments() {
        return deviatedSegments;
    }

    /**
     * Retourne les coordonnees ordonnees du trace principal pour Google Maps.
     */
    public List<LatLng> getMainPathCoordinates() {
        if (segments == null || segments.isEmpty()) {
            return Collections.emptyList();
        }
        List<LatLng> result = new ArrayList<>();
        for (List<SpottedPathPoint> segment : segments) {
            if (segment != null) {
                for (SpottedPathPoint pt : segment) {
                    if (pt != null) {
                        result.add(new LatLng(pt.getLatitude(), pt.getLongitude()));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Retourne la liste des segments devies pour Google Maps.
     */
    public List<List<LatLng>> getCancelledSegmentsCoordinates() {
        if (deviatedSegments == null || deviatedSegments.isEmpty()) {
            return Collections.emptyList();
        }
        List<List<LatLng>> result = new ArrayList<>();
        for (List<SpottedPathPoint> segment : deviatedSegments) {
            if (segment != null) {
                List<LatLng> segmentPoints = new ArrayList<>();
                for (SpottedPathPoint pt : segment) {
                    if (pt != null) {
                        segmentPoints.add(new LatLng(pt.getLatitude(), pt.getLongitude()));
                    }
                }
                if (!segmentPoints.isEmpty()) {
                    result.add(segmentPoints);
                }
            }
        }
        return result;
    }

    public List<SpottedPathPoint> getPoints() {
        if (segments == null || segments.isEmpty()) {
            return Collections.emptyList();
        }
        List<SpottedPathPoint> result = new ArrayList<>();
        for (List<SpottedPathPoint> segment : segments) {
            if (segment != null) {
                result.addAll(segment);
            }
        }
        return result;
    }

    public int getCount() {
        if (segments == null) {
            return 0;
        }
        int total = 0;
        for (List<SpottedPathPoint> segment : segments) {
            if (segment != null) {
                total += segment.size();
            }
        }
        return total;
    }
}