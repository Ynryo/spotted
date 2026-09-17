package fr.ynryo.spotted.managers.fetchers;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import fr.ynryo.spotted.apiResponsesPOJO.network.SpottedNetworkData;
import fr.ynryo.spotted.apiResponsesPOJO.region.SpottedRegionData;
import fr.ynryo.spotted.managers.FetchingManager;
import fr.ynryo.spotted.services.ApiClientFactory;
import fr.ynryo.spotted.services.BusTrackerApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fetcher dédié aux appels API du fournisseur bus-tracker.fr
 */
public class BusTrackerFetcher {
    private static final String TAG = "BusTrackerFetcher";
    private static final String BASE_URL = "https://bus-tracker.fr/api/";

    private final BusTrackerApiService apiService;

    public BusTrackerFetcher() {
        this.apiService = ApiClientFactory.createService(BASE_URL, BusTrackerApiService.class);
    }

    /**
     * Récupère la liste de l'ensemble des réseaux de transport disponibles.
     *
     * @param listener Callback notifié avec la liste des {@link SpottedNetworkData}
     */
    public void fetchNetworks(FetchingManager.OnNetworkListener listener) {
        try {
            apiService.getNetworks().enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<List<SpottedNetworkData>> call, @NonNull Response<List<SpottedNetworkData>> response) {
                    if (listener == null) return;
                    if (response.isSuccessful() && response.body() != null) {
                        listener.onResponseNetworkListener(response.body());
                    } else {
                        Log.e(TAG, "fetchNetworks code erreur: " + response.code());
                        listener.onErrorNetworkListener("Code erreur: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<SpottedNetworkData>> call, @NonNull Throwable t) {
                    Log.e(TAG, "fetchNetworks échec: " + t.getMessage(), t);
                    if (listener == null) return;
                    listener.onErrorNetworkListener(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "fetchNetworks exception: " + e.getMessage(), e);
            if (listener == null) return;
            listener.onErrorNetworkListener(e.getMessage());
        }
    }

    /**
     * Récupère la liste des régions disponibles sur Bus-Tracker.
     *
     * @param listener Callback notifié avec la liste des {@link SpottedRegionData}
     */
    public void fetchRegions(FetchingManager.OnRegionsListener listener) {
        try {
            apiService.getRegions().enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<List<SpottedRegionData>> call, @NonNull Response<List<SpottedRegionData>> response) {
                    if (listener == null) return;
                    if (response.isSuccessful() && response.body() != null) {
                        listener.onResponseRegionsListener(response.body());
                    } else {
                        Log.e(TAG, "fetchRegions code erreur: " + response.code());
                        listener.onErrorRegionsListener("Erreur régions: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<SpottedRegionData>> call, @NonNull Throwable t) {
                    Log.e(TAG, "fetchRegions échec: " + t.getMessage(), t);
                    if (listener == null) return;
                    listener.onErrorRegionsListener(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "fetchRegions exception: " + e.getMessage(), e);
            if (listener == null) return;
            listener.onErrorRegionsListener(e.getMessage());
        }
    }
}
