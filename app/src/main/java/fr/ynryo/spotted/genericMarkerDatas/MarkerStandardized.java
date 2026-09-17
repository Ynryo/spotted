package fr.ynryo.spotted.genericMarkerDatas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import fr.ynryo.spotted.apiResponsesPOJO.journey.SpottedJourneyDetails;
import fr.ynryo.spotted.apiResponsesPOJO.journey.SpottedStopDetails;
import fr.ynryo.spotted.apiResponsesPOJO.markers.SpottedMarkerData;
import fr.ynryo.spotted.utils.Time;

public class MarkerStandardized {

    // ==================== DONNÉES D'IDENTIFICATION ====================
    private MarkerIdentity markerIdentity;

    // ==================== DONNÉES D'AFFICHAGE ====================
    private MarkerStyle markerStyle;

    // ==================== POSITION ET DIRECTION ====================
    private MarkerPosition markerPosition;

    // ==================== DONNÉES DE VOYAGE ====================
    private MarkerTrip markerTrip;
    private URI networkLogoHref; // Logo du réseau (provenant de l'API journey)

    // ==================== MÉTADONNÉES ====================
    private boolean isFollowed; // Est-ce que l'utilisateur suit ce véhicule?
    private Time createdAt; // Quand ce marqueur a été créé
    private Time lastUpdatedAt; // Quand la position a été mise à jour
    private boolean detailsLoaded; // Les infos détaillées (stops) ont-ils été fetched?

    // ==================== SI TRAIN EN UM ====================
    private MarkerStandardized umA; // Train en UM A
    private MarkerStandardized umB; // Train en UM B

    private final static String TAG = "MarkerStandardized";
    private final static int NETWORK_ID_SNCF = 17;

    // ==================== CONSTRUCTEURS ====================
    public MarkerStandardized() {
        this.markerIdentity = new MarkerIdentity();
        this.markerPosition = new MarkerPosition();
        this.markerStyle = new MarkerStyle();
        this.markerTrip = new MarkerTrip();
        this.createdAt = Time.now();
        this.lastUpdatedAt = Time.now();
        this.isFollowed = false;
        this.detailsLoaded = false;
    }

    // ==================== CONVERSION & FACTORY ====================
    /**
     * Converts a {@link SpottedMarkerData} object into a {@link MarkerStandardized} object with the specified {@link MarkerType}.
     *
     * @param markerData the source {@link SpottedMarkerData} object containing the data to be converted
     * @param type       the {@link MarkerType} to be associated with the resulting {@link MarkerStandardized} object
     * @return a {@link MarkerStandardized} object populated with the data from the given {@link SpottedMarkerData} and the specified {@link MarkerType}
     */
    public static MarkerStandardized createNewMarkerFrom(@NonNull SpottedMarkerData markerData, @NonNull MarkerType type) {
        MarkerStandardized marker = new MarkerStandardized();

        boolean isTrain = (type == MarkerType.TRAIN);
        int lineId = 0;
        if (isTrain && markerData.getVehicleNumber() != null) {
            try {
                lineId = Integer.parseInt(markerData.getVehicleNumber());
            } catch (NumberFormatException ignored) {
            }
        }
        String lineNumber = isTrain ? markerData.getVehicleNumber() : markerData.getLineNumber();

        marker.markerIdentity = new MarkerIdentity(
                type,
                markerData.getId(),
                lineId,
                lineNumber,
                markerData.getNetworkRef()
        );
        if (markerData.getPosition() != null) {
            marker.markerPosition = new MarkerPosition(
                    markerData.getPosition().getLatitude(),
                    markerData.getPosition().getLongitude(),
                    markerData.getPosition().getBearing()
            );
        } else {
            marker.markerPosition = new MarkerPosition();
        }
        marker.markerStyle = new MarkerStyle(
                markerData.getColor(),
                markerData.getFillColor()
        );
        marker.createdAt = Time.now();
        marker.lastUpdatedAt = Time.now();
        marker.detailsLoaded = false;

        return marker;
    }

    // ==================== HYDRATATION DES DONNÉES ====================

    /**
     * Met à jour les détails du véhicule depuis l'objet {@link SpottedJourneyDetails} de la nouvelle API.
     *
     * @param journeyDetails Les détails complets du trajet fournis par l'API Spotted
     */
    public void setJourneyDetails(@NonNull SpottedJourneyDetails journeyDetails) {
        this.markerIdentity.setLineId(journeyDetails.getLineId());
        this.markerTrip.setDestination(journeyDetails.getDestination());
        if (journeyDetails.getNetwork() != null) {
            this.markerIdentity.setNetworkId(journeyDetails.getNetwork().getId());
        }
        if (journeyDetails.getPath() != null) {
            this.markerTrip.setPathRef(journeyDetails.getPath().getId());
        }
        if (journeyDetails.getPosition() != null) {
            this.markerTrip.setAtStop(journeyDetails.getPosition().isAtStop());
            this.markerTrip.setDistanceTraveled(journeyDetails.getPosition().getDistanceTraveled());
        }

        if (journeyDetails.getStops() == null || journeyDetails.getStops().isEmpty())
            return;
        this.markerTrip.getStops().clear();

        for (int i = 0; i < journeyDetails.getStops().size(); i++) {
            SpottedStopDetails stopDetails = journeyDetails.getStops().get(i);

            String rawAimed = stopDetails.getDepartureAimedTime() != null ? stopDetails.getDepartureAimedTime() : stopDetails.getArrivalAimedTime();
            String rawExpected = stopDetails.getDepartureExpectedTime() != null ? stopDetails.getDepartureExpectedTime() : stopDetails.getArrivalExpectedTime();

            Time aimedTime = Time.parse(rawAimed);
            Time expectedTime = Time.parse(rawExpected);
            boolean isRealtime = expectedTime != null;

            Long delay = null;
            if (stopDetails.getDepartureTimeDifference() != null)
                delay = stopDetails.getDepartureTimeDifference().longValue();
            else if (stopDetails.getArrivalTimeDifference() != null)
                delay = stopDetails.getArrivalTimeDifference().longValue();
            else delay = Time.calculateDelayMinutes(aimedTime, expectedTime);

            String stopRef = stopDetails.getStopUIC();
            if (stopRef == null || stopRef.isEmpty()) stopRef = stopDetails.getStopName();

            MarkerStop stop = new MarkerStop(
                    stopRef,
                    stopDetails.getStopName(),
                    delay,
                    isRealtime ? expectedTime : aimedTime,
                    isRealtime, stopDetails.getStopOrder(),
                    stopDetails.getLongitude(),
                    stopDetails.getLatitude(),
                    stopDetails.getDistanceTraveled(),
                    stopDetails.getStopType(),
                    stopDetails.getCallStatus(),
                    stopDetails.getFlags(),
                    this
            );

            if (aimedTime != null || expectedTime != null) {
                Time arrTime = Time.parse(stopDetails.getArrivalExpectedTime() != null ? stopDetails.getArrivalExpectedTime() : stopDetails.getArrivalAimedTime());
                stop.setArrivalTime(arrTime != null ? arrTime : (isRealtime ? expectedTime : aimedTime));
            }

            if (stopDetails.getPlatform() != null && stopDetails.getPlatform().getName() != null) {
                stop.setPlatform(
                        new MarkerStopPlatform(stopDetails.getPlatform().getName(), stopRef, (int) stopDetails.getPlatform().getPercentage())
                );
            }

            this.markerTrip.getStops().add(stop);
        }

        if (journeyDetails.getPath() != null) {
            this.setMarkerDataRoute(journeyDetails.getPath());
        }

        if (journeyDetails.getNetwork() != null && journeyDetails.getNetwork().getLogoHref() != null) {
            this.networkLogoHref = journeyDetails.getNetwork().getLogoHref();
        }

        this.detailsLoaded = true;
        this.lastUpdatedAt = Time.now();
    }

    public URI getNetworkLogoHref() {
        return networkLogoHref;
    }

    // ==================== GETTERS ====================
    // --- Identité ---
    public MarkerType getMarkerType() {
        return markerIdentity.getMarkerType();
    }

    public String getId() {
        return markerIdentity.getId();
    }

    public int getLineId() {
        return markerIdentity.getLineId();
    }

    public String getLineNumber() {
        return markerIdentity.getLineNumber();
    }

    public String getNetworkRef() {
        return markerIdentity.getNetworkRef();
    }

    public int getNetworkId() {
        return markerIdentity.getNetworkId();
    }

    // --- Style ---
    public String getFillColor() {
        return markerStyle.getFillColor();
    }

    public String getTextColor() {
        return markerStyle.getTextColor();
    }

    // --- Position et orientation ---
    public double getLatitude() {
        return markerPosition.getLatitude();
    }

    public double getLongitude() {
        return markerPosition.getLongitude();
    }

    public float getBearing() {
        return markerPosition.getBearing();
    }

    // --- Voyage et arrêts ---
    public String getDestination() {
        return markerTrip.getDestination();
    }

    public String getPathRef() {
        if (isUm() && umA != null && (markerTrip.getPathRef() == null || markerTrip.getPathRef().isEmpty())) {
            return umA.getPathRef();
        }
        return markerTrip.getPathRef();
    }

    public List<MarkerStop> getStops() {
        if (isUm() && umA != null && (markerTrip.getStops() == null || markerTrip.getStops().isEmpty())) {
            return umA.getStops();
        }
        return markerTrip.getStops() != null ? markerTrip.getStops() : new ArrayList<>();
    }

    @Nullable
    public MarkerStop getNextStop() {
        List<MarkerStop> stops = getStops();
        if (stops != null && !stops.isEmpty()) {
            return stops.get(0);
        }
        return null;
    }

    public int getRemainingStopsCount() {
        List<MarkerStop> stops = getStops();
        return stops != null ? stops.size() : 0;
    }

    public boolean isAtStop() {
        return markerTrip.isAtStop();
    }

    public float getDistanceTraveled() {
        return markerTrip.getDistanceTraveled();
    }

    public Object getMarkerDataRoute() {
        return markerTrip.getMarkerDataRoute();
    }

    // --- Métadonnées et statut ---
    public boolean isFollowed() {
        return isFollowed;
    }

    public boolean isDetailsLoaded() {
        return detailsLoaded;
    }

    public Time getCreatedAt() {
        return createdAt;
    }

    public Time getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    // --- Unité Multiple (UM) ---
    public MarkerStandardized getUmA() {
        return umA;
    }

    public MarkerStandardized getUmB() {
        return umB;
    }

    // ==================== SETTERS ====================
    // --- Identité ---
    /**
     * Sets the marker type for the current instance.
     *
     * @param markerType the MarkerType to be set
     */
    public void setMarkerType(MarkerType markerType) {
        this.markerIdentity.setMarkerType(markerType);
    }

    /**
     * Sets the unique identifier for this instance.
     *
     * @param id the identifier to be set
     */
    public void setId(String id) {
        this.markerIdentity.setId(id);
    }

    /**
     * Sets the line identifier for this object.
     *
     * @param lineId the identifier to be assigned to the line
     */
    public void setLineId(int lineId) {
        this.markerIdentity.setLineId(lineId);
    }

    /**
     * Sets the line number to the specified value.
     *
     * @param lineNumber the line number to be set
     */
    public void setLineNumber(String lineNumber) {
        this.markerIdentity.setLineNumber(lineNumber);
    }

    /**
     * Sets the network reference with the provided value.
     *
     * @param networkRef The identifier or reference of the network to be set.
     */
    public void setNetworkRef(String networkRef) {
        this.markerIdentity.setNetworkRef(networkRef);
    }

    /**
     * Sets the network identifier for the current instance.
     *
     * @param networkId the unique identifier of the network to be set
     */
    public void setNetworkId(int networkId) {
        this.markerIdentity.setNetworkId(networkId);
    }

    // --- Style ---
    /**
     * Sets the fill color for the object.
     *
     * @param fillColor the color to use for filling, specified as a string
     */
    public void setFillColor(String fillColor) {
        this.markerStyle.setFillColor(fillColor);
    }

    /**
     * Sets the text color.
     *
     * @param textColor the color to set for the text, specified as a string
     */
    public void setTextColor(String textColor) {
        this.markerStyle.setTextColor(textColor);
    }

    // --- Position et orientation ---
    /**
     * Updates the latitude for the marker and records the current timestamp.
     *
     * @param latitude The new latitude value to set.
     */
    public void setLatitude(double latitude) {
        this.markerPosition.setLatitude(latitude);
        this.lastUpdatedAt = Time.now();
    }

    /**
     * Updates the longitude for the marker and records the current timestamp.
     *
     * @param longitude The new longitude value to set.
     */
    public void setLongitude(double longitude) {
        this.markerPosition.setLongitude(longitude);
        this.lastUpdatedAt = Time.now();
    }

    /**
     * Sets the bearing of the marker and updates the timestamp of the last modification.
     *
     * @param bearing The new bearing value to set in degrees.
     */
    public void setBearing(float bearing) {
        this.markerPosition.setBearing(bearing);
        this.lastUpdatedAt = Time.now();
    }

    /**
     * Updates the position of an object with new latitude, longitude, and bearing values.
     *
     * @param newLatitude  the updated latitude value
     * @param newLongitude the updated longitude value
     * @param newBearing   the updated bearing value in degrees
     */
    public void updatePosition(double newLatitude, double newLongitude, float newBearing) {
        this.markerPosition.setLatitude(newLatitude);
        this.markerPosition.setLongitude(newLongitude);
        this.markerPosition.setBearing(newBearing);
        this.lastUpdatedAt = Time.now();
    }

    // --- Voyage et arrêts ---
    /**
     * Sets the destination for the marker.
     *
     * @param destination The name of the destination.
     */
    public void setDestination(String destination) {
        this.markerTrip.setDestination(destination);
    }

    public void setPathRef(String pathRef) {
        this.markerTrip.setPathRef(pathRef);
    }

    /**
     * Sets the list of stops associated with the marker and updates the detailsLoaded flag
     * based on the presence of valid stop data.
     *
     * @param stops The list of stops to associate with the marker.
     */
    public void setStops(List<MarkerStop> stops) {
        this.markerTrip.setStops(stops);
        this.detailsLoaded = (stops != null && !stops.isEmpty());
    }

    /**
     * Sets the marker data route associated with the marker.
     *
     * @param markerDataRoute The data route object to associate with the marker.
     */
    public void setMarkerDataRoute(Object markerDataRoute) {
        this.markerTrip.setMarkerDataRoute(markerDataRoute);
    }

    // --- Métadonnées et statut ---
    /**
     * Updates the followed status of the marker.
     *
     * @param followed The new followed status to set.
     */
    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    /**
     * Sets the creation timestamp for the marker data.
     *
     * @param createdAt The timestamp indicating when the marker data was created.
     */
    public void setCreatedAt(Time createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Updates the timestamp indicating the last modification time for the marker data.
     *
     * @param lastUpdatedAt The timestamp of the last update.
     */
    public void setLastUpdatedAt(Time lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    /**
     * Sets the detailsLoaded flag.
     *
     * @param detailsLoaded A boolean value representing the loaded status.
     */
    public void setDetailsLoaded(boolean detailsLoaded) {
        this.detailsLoaded = detailsLoaded;
    }

    // --- Unité Multiple (UM) ---
    /**
     * Sets the first unit of a standardized marker forming a multiple-unit train (UM - Unité Multiple).
     *
     * @param umA The first unit of the train as a MarkerStandardized object.
     */
    public void setUmA(MarkerStandardized umA) {
        this.umA = umA;
    }

    /**
     * Sets the second unit of a standardized marker forming a multiple-unit train (UM - Unité Multiple).
     *
     * @param umB The second unit of the train as a MarkerStandardized object.
     */
    public void setUmB(MarkerStandardized umB) {
        this.umB = umB;
    }

    /**
     * Sets a pair of standardized marker units forming a multiple-unit train (UM - Unité Multiple).
     *
     * @param umA The first unit of the train.
     * @param umB The second unit of the train.
     */
    public void setUmPair(MarkerStandardized umA, MarkerStandardized umB) {
        this.setUmA(umA);
        this.setUmB(umB);
    }

    // ==================== MÉTHODES D'ÉTAT & UTILITAIRES ====================
    /**
     * Determines whether the current marker represents a train.
     *
     * @return true if the marker type is TRAIN; false otherwise.
     */
    public boolean isTrain() {
        return markerIdentity.getMarkerType() == MarkerType.TRAIN;
    }

    /**
     * Determines whether the current marker represents a vehicle.
     *
     * @return true if the marker type is BUS_TRAM; false otherwise.
     */
    public boolean isVehicle() {
        return markerIdentity.getMarkerType() == MarkerType.BUS_TRAM;
    }

    /**
     * Determines whether the current marker represents a multiple-unit train (UM - Unité Multiple).
     * A marker is considered a multiple-unit train if it represents a train
     * and both unit components (umA and umB) are non-null.
     *
     * @return true if the marker represents a multiple-unit train; false otherwise.
     */
    public boolean isUm() {
        return isTrain() && umA != null && umB != null;
    }

    @NonNull
    @Override
    public String toString() {
        return "MarkerStandardized{" +
                "markerIdentity=" + markerIdentity +
                ", markerStyle=" + markerStyle +
                ", markerPosition=" + markerPosition +
                ", markerTrip=" + markerTrip +
                ", isFollowed=" + isFollowed +
                ", createdAt=" + createdAt +
                ", lastUpdatedAt=" + lastUpdatedAt +
                ", detailsLoaded=" + detailsLoaded +
                ", umA=" + (umA != null ? umA.getId() : "null") +
                ", umB=" + (umB != null ? umB.getId() : "null") +
                '}';
    }
}