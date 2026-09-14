package fr.ynryo.spotted.services;

import java.util.List;

import fr.ynryo.spotted.apiResponsesPOJO.network.BusTrackerNetworkData;
import fr.ynryo.spotted.apiResponsesPOJO.region.BusTrackerRegionData;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface de l'API, gère les requêtes et les réponses
 */
public interface BusTrackerApiService {
    @GET("regions")
    Call<List<BusTrackerRegionData>> getRegions();

    @GET("networks")
    Call<List<BusTrackerNetworkData>> getNetworks();
}
