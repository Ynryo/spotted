package fr.ynryo.spotted.services;

import fr.ynryo.spotted.apiResponsesPOJO.markers.SpottedMarkersResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface Retrofit pour l'API Spotted (https://api.spotted.ynryo.fr/v1/)
 */
public interface SpottedApiService {
    /**
     * Récupère les véhicules en direct dans une zone géographique (bounding box)
     *
     * @param swLat Latitude Sud-Ouest
     * @param swLon Longitude Sud-Ouest
     * @param neLat Latitude Nord-Est
     * @param neLon Longitude Nord-Est
     */
    @GET("markers")
    Call<SpottedMarkersResponse> getMarkers(
            @Query("swLat") double swLat,
            @Query("swLon") double swLon,
            @Query("neLat") double neLat,
            @Query("neLon") double neLon
    );

    /**
     * Récupère la fiche détaillée d'un trajet de véhicule (arrêts, horaires, retards, polyline GPS)
     *
     * @param journeyId Identifiant unique du trajet (url-encodé)
     */
    @GET("journeys/{journeyId}")
    Call<fr.ynryo.spotted.apiResponsesPOJO.journey.SpottedJourneyDetails> getJourneyDetails(
            @retrofit2.http.Path(value = "journeyId", encoded = true) String journeyId
    );

    /**
     * Récupère le tracé géographique (polyline GPS) d'une ligne
     *
     * @param pathRef Identifiant unique de la route / chemin (url-encodé)
     */
    @GET("paths/{pathRef}")
    Call<fr.ynryo.spotted.apiResponsesPOJO.path.SpottedPathResponse> getPath(
            @retrofit2.http.Path(value = "pathRef", encoded = true) String pathRef
    );
}
