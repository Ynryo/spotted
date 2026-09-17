package fr.ynryo.spotted.services;

import java.util.List;

import fr.ynryo.spotted.apiResponsesPOJO.network.SpottedNetworkData;
import fr.ynryo.spotted.apiResponsesPOJO.region.SpottedRegionData;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface de l'API, gère les requêtes et les réponses
 */
public interface BusTrackerApiService {
    @GET("regions")
    Call<List<SpottedRegionData>> getRegions();

    @GET("networks")
    Call<List<SpottedNetworkData>> getNetworks();
}
