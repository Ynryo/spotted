package fr.ynryo.spotted.apiResponsesPOJO.path;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpottedPathResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("path")
    private PathData path;

    @SerializedName("cancelled")
    private CancelledData cancelled;

    @SerializedName("points")
    private List<SpottedPathPoint> points;

    @SerializedName("count")
    private int count;

    public static class PathData {
        @SerializedName("p")
        private List<List<Double>> p;

        public List<List<Double>> getP() {
            return p;
        }

        public List<LatLng> toLatLngList() {
            List<LatLng> result = new ArrayList<>();
            if (p != null) {
                for (List<Double> coord : p) {
                    if (coord != null && coord.size() >= 2 && coord.get(0) != null && coord.get(1) != null) {
                        result.add(new LatLng(coord.get(0), coord.get(1)));
                    }
                }
            }
            return result;
        }
    }

    public static class CancelledData {
        @SerializedName("segments")
        private List<List<List<Double>>> segments;

        public List<List<List<Double>>> getSegments() {
            return segments;
        }

        public List<List<LatLng>> toSegmentLatLngLists() {
            List<List<LatLng>> result = new ArrayList<>();
            if (segments != null) {
                for (List<List<Double>> segment : segments) {
                    if (segment != null) {
                        List<LatLng> segmentPoints = new ArrayList<>();
                        for (List<Double> coord : segment) {
                            if (coord != null && coord.size() >= 2 && coord.get(0) != null && coord.get(1) != null) {
                                segmentPoints.add(new LatLng(coord.get(0), coord.get(1)));
                            }
                        }
                        if (!segmentPoints.isEmpty()) {
                            result.add(segmentPoints);
                        }
                    }
                }
            }
            return result;
        }
    }

    public String getId() {
        return id;
    }

    public PathData getPath() {
        return path;
    }

    public CancelledData getCancelled() {
        return cancelled;
    }

    /**
     * Retourne les coordonnées ordonnées du tracé principal pour Google Maps.
     */
    public List<LatLng> getMainPathCoordinates() {
        if (path != null) {
            return path.toLatLngList();
        }
        if (points != null) {
            List<LatLng> result = new ArrayList<>();
            for (SpottedPathPoint pt : points) {
                result.add(new LatLng(pt.getLatitude(), pt.getLongitude()));
            }
            return result;
        }
        return Collections.emptyList();
    }

    /**
     * Retourne la liste des segments annulés / déviés pour Google Maps.
     */
    public List<List<LatLng>> getCancelledSegmentsCoordinates() {
        if (cancelled != null) {
            return cancelled.toSegmentLatLngLists();
        }
        return Collections.emptyList();
    }

    public List<SpottedPathPoint> getPoints() {
        if (points != null) {
            return points;
        }
        if (path != null && path.getP() != null) {
            List<SpottedPathPoint> converted = new ArrayList<>();
            for (List<Double> coord : path.getP()) {
                if (coord != null && coord.size() >= 2 && coord.get(0) != null && coord.get(1) != null) {
                    Double dist = coord.size() > 2 ? coord.get(2) : null;
                    converted.add(new SpottedPathPoint(coord.get(0), coord.get(1), dist));
                }
            }
            return converted;
        }
        return Collections.emptyList();
    }

    public int getCount() {
        return count;
    }
}
