package fr.ynryo.spotted.artists;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.ynryo.spotted.MainActivity;
import fr.ynryo.spotted.R;
import fr.ynryo.spotted.apiResponsesPOJO.path.SpottedPathPoint;
import fr.ynryo.spotted.apiResponsesPOJO.path.SpottedPathResponse;
import fr.ynryo.spotted.genericMarkerDatas.MarkerStandardized;
import fr.ynryo.spotted.genericMarkerDatas.MarkerStop;

public class RouteArtist {
    private final static String TAG = "RouteArtist";
    private final MainActivity context;
    private String currentMarkerId;
    private Polyline currentRoutePolyline;
    private final List<Polyline> cancelledRoutePolylines = new ArrayList<>();
    private final List<Marker> stopMarkers = new ArrayList<>();

    public RouteArtist(MainActivity context) {
        this.context = context;
    }

    public void drawVehicleRoute(MarkerStandardized mData) {
        if (mData == null || mData.getMarkerDataRoute() == null) return;

        PolylineOptions options = new PolylineOptions()
                .width(16f)
                .color(Color.parseColor(mData.getFillColor() != null ? mData.getFillColor() : "#424242"))
                .geodesic(true)
                .zIndex(2.0f);

        boolean pointsAdded = false;

        remove();
        if (mData.getMarkerDataRoute() instanceof SpottedPathResponse) {
            SpottedPathResponse path = (SpottedPathResponse) mData.getMarkerDataRoute();
            List<LatLng> mainPoints = path.getMainPathCoordinates();
            if (mainPoints != null && !mainPoints.isEmpty()) {
                options.addAll(mainPoints);
                pointsAdded = true;
            }

            List<List<LatLng>> cancelledSegments = path.getCancelledSegmentsCoordinates();
            if (cancelledSegments != null && !cancelledSegments.isEmpty()) {
                // Motif pour la rubalise : tirets jaunes alternés avec le fond noir
                List<PatternItem> yellowDashedPattern = Arrays.asList(new Dash(30), new Gap(30));
                for (List<LatLng> segment : cancelledSegments) {
                    if (segment != null && !segment.isEmpty()) {
                        // 1. Couche inférieure : Bande noire continue
                        PolylineOptions blackBase = new PolylineOptions()
                                .width(16f)
                                .color(Color.parseColor("#1A1A1A"))
                                .geodesic(true)
                                .zIndex(2.4f);
                        blackBase.addAll(segment);
                        cancelledRoutePolylines.add(context.getMap().addPolyline(blackBase));

                        // 2. Couche supérieure : Tirets jaune vif alternés (effet rubalise de chantier)
                        PolylineOptions yellowStripes = new PolylineOptions()
                                .width(16f)
                                .color(Color.parseColor("#FFD600"))
                                .pattern(yellowDashedPattern)
                                .geodesic(true)
                                .zIndex(2.5f);
                        yellowStripes.addAll(segment);
                        cancelledRoutePolylines.add(context.getMap().addPolyline(yellowStripes));
                    }
                }
            }
        }
        if (pointsAdded) {
            currentMarkerId = mData.getId();
            currentRoutePolyline = context.getMap().addPolyline(options);
            drawStopCircles(mData);
        }
    }

    private void drawStopCircles(MarkerStandardized mData) {
        for (Marker m : stopMarkers) m.remove();
        stopMarkers.clear();

        List<MarkerStop> stops = mData.getStops();
        if (stops == null || stops.isEmpty()) return;

        for (MarkerStop stop : stops) {
            if (stop.getLatitude() == 0 && stop.getLongitude() == 0) continue;

            BitmapDescriptor icon = createStopIcon(stop.getStopName(), mData);

            Marker marker = context.getMap().addMarker(new MarkerOptions()
                    .position(new LatLng(stop.getLatitude(), stop.getLongitude()))
                    .icon(icon)
                    .anchor(0.0f, 0.5f) // label à droite du point
                    .flat(false)
                    .zIndex(3f));
            if (marker != null) stopMarkers.add(marker);
        }
    }

    private BitmapDescriptor createStopIcon(String stopName, MarkerStandardized mData) {
        View view = LayoutInflater.from(context).inflate(R.layout.stop_marker, null);

        //point
        View dot = view.findViewById(R.id.stop_dot);
        GradientDrawable dotDrawable = new GradientDrawable();
        dotDrawable.setShape(GradientDrawable.OVAL);
        dotDrawable.setColor(Color.WHITE);
        dotDrawable.setStroke((int) (3 * context.getResources().getDisplayMetrics().density), mData.getFillColor() != null ? Color.parseColor(mData.getFillColor()) : Color.parseColor("#424242"));
        dot.setBackground(dotDrawable);

        //background label
        TextView tvName = view.findViewById(R.id.stop_name);
        tvName.setText(stopName);
        tvName.setTextColor(mData.getTextColor() != null ? Color.parseColor(mData.getTextColor()) : Color.parseColor("#424242"));
        GradientDrawable labelDrawable = new GradientDrawable();
        labelDrawable.setShape(GradientDrawable.RECTANGLE);
        labelDrawable.setColor(mData.getFillColor() != null ? Color.parseColor(mData.getFillColor()) : Color.parseColor("#424242"));
        labelDrawable.setAlpha(220);
        labelDrawable.setCornerRadius(8);
        tvName.setBackground(labelDrawable);

        //to bitmap
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(
                view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void remove() {
        if (currentRoutePolyline != null) {
            currentRoutePolyline.remove();
            currentRoutePolyline = null;
        }
        for (Polyline p : cancelledRoutePolylines) {
            p.remove();
        }
        cancelledRoutePolylines.clear();
        for (Marker m : stopMarkers) m.remove();
        stopMarkers.clear();
        currentMarkerId = null;
    }

    public boolean hasRoute() {
        return currentRoutePolyline != null || !cancelledRoutePolylines.isEmpty();
    }

    public boolean isDrew(String markerId) {
        return currentMarkerId != null && currentMarkerId.equals(markerId);
    }

    public String getCurrentMarkerId() {
        return currentMarkerId;
    }
}