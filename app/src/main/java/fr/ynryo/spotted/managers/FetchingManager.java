package fr.ynryo.spotted.managers;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

import fr.ynryo.spotted.MainActivity;
import fr.ynryo.spotted.apiResponsesPOJO.network.BusTrackerNetworkData;
import fr.ynryo.spotted.apiResponsesPOJO.region.BusTrackerRegionData;
import fr.ynryo.spotted.apiResponsesPOJO.version.YnryoVersionResponse;
import fr.ynryo.spotted.genericMarkerDatas.MarkerStandardized;
import fr.ynryo.spotted.managers.fetchers.BusTrackerFetcher;
import fr.ynryo.spotted.managers.fetchers.SpottedFetcher;
import fr.ynryo.spotted.managers.fetchers.YnryoFetcher;

/**
 * Classe gérant les requêtes et réponses de l'API et les conversions avec MarkerStandardized
 * <p>
 * Author: Ynryo
 */
public class FetchingManager {
    private static final String TAG = "FetchingManager";
    private final MainActivity context;
    private final SpottedFetcher spottedFetcher;
    private final BusTrackerFetcher busTrackerFetcher;
    private final YnryoFetcher ynryoFetcher;

    public FetchingManager(MainActivity context) {
        this.context = context;
        this.spottedFetcher = new SpottedFetcher();
        this.busTrackerFetcher = new BusTrackerFetcher();
        this.ynryoFetcher = new YnryoFetcher();
    }

    // ==================== LISTENERS ====================
    public interface OnMarkersListener {
        void onResponseMarkersListener(List<MarkerStandardized> markerStandardizedList);

        void onErrorMarkersListener(String error);
    }

    public interface OnVehicleDetailsListener {
        void onResponseVehicleDetailsListener(MarkerStandardized markerStandardized);

        void onErrorVehicleDetailsListener(String error);
    }

    public interface OnNetworkListener {
        void onResponseNetworkListener(List<BusTrackerNetworkData> data);

        void onErrorNetworkListener(String error);
    }

    public interface OnRegionsListener {
        void onResponseRegionsListener(List<BusTrackerRegionData> regions);

        void onErrorRegionsListener(String error);
    }

    public interface OnVersionListener {
        void onResponseVersionListener(YnryoVersionResponse version);

        void onErrorVersionListener(String error);
    }

    public interface OnVehicleAliveListener {
        void onResponseVehicleAliveListener(boolean isAlive);

        void onErrorVehicleAliveListener(String error);
    }

    // ==================== FETCH MARKERS (PRINCIPAL) ====================
    public void fetchMarkers(OnMarkersListener listener) {
        fetchMarkers(null, listener);
    }

    public void fetchMarkers(String lineId, OnMarkersListener listener) {
        if (context.getMap() == null) return;
        LatLngBounds bounds = context.getMap().getProjection().getVisibleRegion().latLngBounds;
        spottedFetcher.fetchMarkers(bounds, listener);
    }

    // ==================== FETCH VEHICLE DETAILS ====================
    public void fetchVehicleStopsInfo(MarkerStandardized markerStandardized, OnVehicleDetailsListener listener) {
        spottedFetcher.fetchVehicleStopsInfo(markerStandardized, listener);
    }

    // ==================== FETCH NETWORKS ====================
    public void fetchNetworks(OnNetworkListener listener) {
        busTrackerFetcher.fetchNetworks(listener);
    }

    // ==================== FETCH REGIONS ====================
    public void fetchRegions(OnRegionsListener listener) {
        busTrackerFetcher.fetchRegions(listener);
    }

    // ==================== FETCH VERSION ====================
    public void fetchLatestVersion(OnVersionListener listener) {
        ynryoFetcher.fetchLatestVersion(listener);
    }

    // ==================== FETCH IS ALIVE VERSION ====================
    public void fetchVehicleAlive(String vehicleId, OnVehicleAliveListener listener) {
        spottedFetcher.fetchVehicleAlive(vehicleId, listener);
    }
}