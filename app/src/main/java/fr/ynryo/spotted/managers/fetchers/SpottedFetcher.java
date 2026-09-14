package fr.ynryo.spotted.managers.fetchers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLngBounds;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import fr.ynryo.spotted.apiResponsesPOJO.journey.SpottedJourneyDetails;
import fr.ynryo.spotted.apiResponsesPOJO.markers.BusTrackerMarkerData;
import fr.ynryo.spotted.apiResponsesPOJO.markers.SpottedMarkersResponse;
import fr.ynryo.spotted.apiResponsesPOJO.path.SpottedPathResponse;
import fr.ynryo.spotted.genericMarkerDatas.MarkerStandardized;
import fr.ynryo.spotted.genericMarkerDatas.MarkerType;
import fr.ynryo.spotted.managers.FetchingManager;
import fr.ynryo.spotted.services.ApiClientFactory;
import fr.ynryo.spotted.services.SpottedApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fetcher dédié aux appels vers l'API d'agrégation Spotted (https://api.spotted.ynryo.fr/v1/)
 */
public class SpottedFetcher {
    private static final String TAG = "SpottedFetcher";
    private static final String BASE_URL = "https://api.spotted.ynryo.fr/v1/";

    private final SpottedApiService apiService;

    public SpottedFetcher() {
        this.apiService = ApiClientFactory.createService(BASE_URL, SpottedApiService.class);
    }

    /**
     * Récupère la liste des véhicules visibles dans une zone géographique délimitée (bounds)
     * et les convertit en objets standardisés {@link MarkerStandardized}.
     *
     * @param bounds   Les limites géographiques de la vue courante de la carte (sud-ouest et nord-est)
     * @param listener Callback notifié avec la liste des marqueurs standardisés ou l'erreur survenue
     */
    public void fetchMarkers(LatLngBounds bounds, FetchingManager.OnMarkersListener listener) {
        if (bounds == null) {
            Log.w(TAG, "fetchMarkers: bounds sont null");
            if (listener == null) return;
            listener.onErrorMarkersListener("Bounds are null");
            return;
        }

        apiService.getMarkers(
                bounds.southwest.latitude,
                bounds.southwest.longitude,
                bounds.northeast.latitude,
                bounds.northeast.longitude
        ).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<SpottedMarkersResponse> call, @NonNull Response<SpottedMarkersResponse> response) {
                if (listener == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    List<MarkerStandardized> standardizedMarkers = convertMarkerDataList(response.body().getData());
                    listener.onResponseMarkersListener(standardizedMarkers);
                } else {
                    Log.e(TAG, "fetchMarkers: code erreur HTTP " + response.code());
                    listener.onErrorMarkersListener("Erreur réponse: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<SpottedMarkersResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "fetchMarkers échec: " + t.getMessage(), t);
                if (listener == null) return;
                listener.onErrorMarkersListener(t.getMessage());
            }
        });
    }

    /**
     * Convertit une liste de POJO {@link BusTrackerMarkerData} en une liste d'objets métier {@link MarkerStandardized}.
     *
     * @param markerDataList Liste brute des marqueurs issue de la réponse API
     * @return Liste d'objets {@link MarkerStandardized} typés
     */
    private List<MarkerStandardized> convertMarkerDataList(List<BusTrackerMarkerData> markerDataList) {
        List<MarkerStandardized> result = new ArrayList<>();
        if (markerDataList == null || markerDataList.isEmpty()) {
            return result;
        }

        for (BusTrackerMarkerData markerData : markerDataList) {
            try {
                MarkerType type = MarkerType.guessFromMarkerId(markerData.getId());
                MarkerStandardized standardized = MarkerStandardized.createNewMarkerFrom(markerData, type);
                result.add(standardized);
            } catch (Exception e) {
                Log.e(TAG, "Erreur conversion marker -> MarkerStandardized: " + e.getMessage());
            }
        }

        return result;
    }
}
