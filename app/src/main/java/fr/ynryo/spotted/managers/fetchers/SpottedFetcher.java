package fr.ynryo.spotted.managers.fetchers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLngBounds;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import fr.ynryo.spotted.apiResponsesPOJO.journey.SpottedJourneyDetails;
import fr.ynryo.spotted.apiResponsesPOJO.markers.SpottedMarkerData;
import fr.ynryo.spotted.apiResponsesPOJO.markers.SpottedMarkersResponse;
import fr.ynryo.spotted.genericMarkerDatas.MarkerStandardized;
import fr.ynryo.spotted.genericMarkerDatas.VehicleType;
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
     * Convertit une liste de POJO {@link SpottedMarkerData} en une liste d'objets métier {@link MarkerStandardized}.
     *
     * @param markerDataList Liste brute des marqueurs issue de la réponse API
     * @return Liste d'objets {@link MarkerStandardized} typés
     */
    private List<MarkerStandardized> convertMarkerDataList(List<SpottedMarkerData> markerDataList) {
        List<MarkerStandardized> result = new ArrayList<>();
        if (markerDataList == null || markerDataList.isEmpty()) {
            return result;
        }

        for (SpottedMarkerData markerData : markerDataList) {
            try {
                VehicleType type = VehicleType.guessFromMarkerId(markerData.getId());
                MarkerStandardized standardized = MarkerStandardized.createNewMarkerFrom(markerData, type);
                result.add(standardized);
            } catch (Exception e) {
                Log.e(TAG, "Erreur conversion marker -> MarkerStandardized: " + e.getMessage());
            }
        }

        return result;
    }

    /**
     * Récupère les détails d'un véhicule (arrêts, horaires, retards, etc.).
     *
     * @param markerStandardized Le marqueur du véhicule dont on souhaite charger les détails
     * @param listener           Callback notifié avec le marqueur enrichi ou l'erreur survenue
     */
    public void fetchVehicleStopsInfo(MarkerStandardized markerStandardized, FetchingManager.OnVehicleDetailsListener listener) {
        try {
            String encodedId = URLEncoder.encode(markerStandardized.getId(), "UTF-8");
            apiService.getJourneyDetails(encodedId).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<SpottedJourneyDetails> call, @NonNull Response<SpottedJourneyDetails> response) {
                    if (listener == null) return;
                    if (response.isSuccessful() && response.body() != null) {
                        markerStandardized.setJourneyDetails(response.body());
                        listener.onResponseVehicleDetailsListener(markerStandardized);
                    } else {
                        Log.e(TAG, "fetchVehicleStopsInfo code erreur: " + response.code());
                        listener.onErrorVehicleDetailsListener(String.valueOf(response.code()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SpottedJourneyDetails> call, @NonNull Throwable t) {
                    Log.e(TAG, "fetchVehicleStopsInfo échec: " + t.getMessage(), t);
                    if (listener == null) return;
                    listener.onErrorVehicleDetailsListener(t.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "fetchVehicleStopsInfo exception: " + e.getMessage(), e);
            if (listener == null) return;
            listener.onErrorVehicleDetailsListener(e.getMessage());
        }
    }

    /**
     * Vérifie si un véhicule est toujours actif / en circulation sur l'API (réponse HTTP 200).
     *
     * @param vehicleId L'identifiant du véhicule à vérifier
     * @param listener  Callback notifié avec un booléen (true si le véhicule est toujours actif)
     */
    public void fetchVehicleAlive(String vehicleId, FetchingManager.OnVehicleAliveListener listener) {
        try {
            String encodedId = URLEncoder.encode(vehicleId, "UTF-8");
            apiService.getJourneyDetails(encodedId).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<SpottedJourneyDetails> call, @NonNull Response<SpottedJourneyDetails> response) {
                    if (listener == null) return;
                    listener.onResponseVehicleAliveListener(response.isSuccessful());
                }

                @Override
                public void onFailure(@NonNull Call<SpottedJourneyDetails> call, @NonNull Throwable t) {
                    Log.e(TAG, "fetchVehicleAlive échec: " + t.getMessage(), t);
                    if (listener == null) return;
                    listener.onErrorVehicleAliveListener(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "fetchVehicleAlive exception: " + e.getMessage(), e);
            if (listener == null) return;
            listener.onErrorVehicleAliveListener(e.getMessage());
        }
    }
}
