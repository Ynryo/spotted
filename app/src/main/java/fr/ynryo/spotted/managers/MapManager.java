package fr.ynryo.spotted.managers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.util.function.Consumer;

import fr.ynryo.spotted.MainActivity;
import fr.ynryo.spotted.genericMarkerDatas.MarkerStandardized;

/**
 * Manager dédié à la gestion de la carte Google Maps, de sa configuration et des mouvements de caméra.
 *
 * @author Ynryo
 */
public class MapManager implements OnMapReadyCallback {
    private static final String TAG = "MapManager";
    public static final float DEFAULT_ZOOM = 6f;
    public static final LatLng FRANCE = new LatLng(48.8566, 2.3522);

    private final MainActivity context;
    private final SupportMapFragment mapFragment;
    private final FusedLocationProviderClient fusedLocationClient;

    private GoogleMap googleMap;
    private boolean isMapReady = false;
    private OnMapReadyListener onMapReadyListener;

    public interface OnMapReadyListener {
        void onMapConfigured(@NonNull GoogleMap googleMap);
    }

    public MapManager(@NonNull MainActivity context, SupportMapFragment mapFragment) {
        this.context = context;
        this.mapFragment = mapFragment;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        if (this.mapFragment != null) {
            this.mapFragment.getMapAsync(this);
        }
    }

    public void setOnMapReadyListener(OnMapReadyListener listener) {
        this.onMapReadyListener = listener;
        if (isMapReady && googleMap != null && onMapReadyListener != null) {
            onMapReadyListener.onMapConfigured(googleMap);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.isMapReady = true;

        configureMap();

        if (onMapReadyListener != null) {
            onMapReadyListener.onMapConfigured(googleMap);
        }
    }

    private void configureMap() {
        if (googleMap == null) return;

        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.setBuildingsEnabled(true);

        CameraPosition savedPosition = context.getSaveManager() != null ? context.getSaveManager().loadCameraPosition() : null;
        if (savedPosition != null) {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(savedPosition));
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(FRANCE, DEFAULT_ZOOM));
        }

        // Configuration des UI settings
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        // Configuration des listeners vers MainActivity
        googleMap.setOnCameraIdleListener(context);
        googleMap.setOnMarkerClickListener(context);
        googleMap.setOnMapClickListener(context);
        googleMap.setOnCameraMoveListener(context);
        googleMap.setOnCameraMoveStartedListener(context);

        // Activation de la couche de localisation si permise
        if (hasLocationPermission()) {
            enableMyLocationLayer();
        }
    }

    public boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void enableMyLocationLayer() {
        if (googleMap == null) return;
        try {
            if (hasLocationPermission()) {
                googleMap.setMyLocationEnabled(true);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException enableMyLocationLayer: " + e.getMessage());
        }
    }

    public void getGPSPosition(@NonNull Consumer<Location> callback) {
        if (!hasLocationPermission()) {
            callback.accept(null);
            return;
        }

        try {
            // 1 : cache de la position
            fusedLocationClient.getLastLocation().addOnSuccessListener(context, location -> {
                if (location != null) {
                    callback.accept(location);
                } else {
                    // 2 : cache vide, on demande la position actuelle
                    CancellationTokenSource cts = new CancellationTokenSource();
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.getToken())
                            .addOnSuccessListener(context, callback::accept)
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Erreur getCurrentLocation: " + e.getMessage());
                                callback.accept(null);
                            });
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Erreur getLastLocation: " + e.getMessage());
                callback.accept(null);
            });
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException getGPSPosition: " + e.getMessage());
            callback.accept(null);
        }
    }

    public void initCameraPosition(@Nullable Runnable onAnimationFinished) {
        if (googleMap == null) {
            if (onAnimationFinished != null) onAnimationFinished.run();
            return;
        }

        getGPSPosition(location -> {
            if (location != null) {
                Log.d(TAG, "Position GPS trouvée au lancement : " + location.getLatitude() + ", " + location.getLongitude());
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                animateCamera(userLocation, 15f, 0f, 0f, 1000, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        if (onAnimationFinished != null) onAnimationFinished.run();
                    }

                    @Override
                    public void onCancel() {
                        if (onAnimationFinished != null) onAnimationFinished.run();
                    }
                });
            } else {
                fallbackToSavedPosition(onAnimationFinished);
            }
        });
    }

    private void fallbackToSavedPosition(@Nullable Runnable onAnimationFinished) {
        if (googleMap == null) {
            if (onAnimationFinished != null) onAnimationFinished.run();
            return;
        }
        CameraPosition savedPosition = context.getSaveManager().loadCameraPosition();
        if (savedPosition != null) {
            Log.d(TAG, "Restauration de la CameraPosition sauvegardée");
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(savedPosition), 1000, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    if (onAnimationFinished != null) onAnimationFinished.run();
                }

                @Override
                public void onCancel() {
                    if (onAnimationFinished != null) onAnimationFinished.run();
                }
            });
        } else {
            if (onAnimationFinished != null) onAnimationFinished.run();
        }
    }

    public void saveCurrentPosition() {
        if (googleMap != null && context.getSaveManager() != null) {
            context.getSaveManager().saveCameraPosition(googleMap.getCameraPosition());
        }
    }

    public void centerOnUserLocation() {
        if (googleMap == null) return;

        getGPSPosition(location -> {
            if (location != null) {
                Log.d(TAG, "Position utilisateur trouvée: " + location.getLatitude() + ", " + location.getLongitude());
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                animateCamera(userLocation, 15f, 0f, 0f, 1000);
            } else {
                Log.d(TAG, "Impossible de récupérer la position utilisateur pour le centrage");
            }
        });
    }

    public void centerOnMarker(@NonNull MarkerStandardized markerStandardized, boolean isTilted, boolean isRotated) {
        if (googleMap == null) return;
        float bearing = markerStandardized.getBearing();
        LatLng target = new LatLng(markerStandardized.getLatitude(), markerStandardized.getLongitude());
        animateCamera(target, 17f, isTilted ? 75f : 0f, isRotated ? bearing : 0f, 2000);
    }

    public void centerOnMarker(@NonNull String markerId, boolean isTilted, boolean isRotated) {
        if (context.getMarkerArtist() == null) return;
        Marker marker = context.getMarkerArtist().getActiveMarkers().get(markerId);
        if (marker == null) return;
        MarkerStandardized markerStandardized = (MarkerStandardized) marker.getTag();
        if (markerStandardized == null) return;

        centerOnMarker(markerStandardized, isTilted, isRotated);
    }

    public void animateCamera(@NonNull LatLng target, float zoom, float tilt, float bearing, int durationMs) {
        animateCamera(target, zoom, tilt, bearing, durationMs, null);
    }

    public void animateCamera(@NonNull LatLng target, float zoom, float tilt, float bearing, int durationMs, @Nullable GoogleMap.CancelableCallback callback) {
        if (googleMap == null) return;
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(target)
                .zoom(zoom)
                .tilt(tilt)
                .bearing(bearing)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), durationMs, callback);
    }

    public void resetToNorth() {
        if (googleMap == null) return;
        CameraPosition oldPos = googleMap.getCameraPosition();
        CameraPosition newPos = CameraPosition.builder(oldPos).bearing(0).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(newPos));
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public boolean isMapReady() {
        return isMapReady;
    }
}
