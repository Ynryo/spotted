package fr.ynryo.spotted.managers.um;

import fr.ynryo.spotted.apiResponsesPOJO.bus.BusTrackerVehiclePath;
import fr.ynryo.spotted.genericMarkerDatas.MarkerStandardized;
import fr.ynryo.spotted.genericMarkerDatas.MarkerType;

public class TrainUmProcessor {
    private final static String TAG = "TrainUmProcessor";

    /**
     * Builds a unified marker by combining data from two MarkerStandardized instances.
     * The unified marker includes a composite ID, average location, and combined metadata.
     *
     * @param trainA the first MarkerStandardized instance to be merged
     * @param trainB the second MarkerStandardized instance to be merged
     * @return a MarkerStandardized instance representing the unified marker created from the input instances
     */
    public static MarkerStandardized buildUmMarker(MarkerStandardized trainA, MarkerStandardized trainB) {
        MarkerStandardized um = new MarkerStandardized();
        String idA = trainA.getId();
        String idB = trainB.getId();
        um.setId(idA.compareTo(idB) <= 0 ? idA + "/" + idB : idB + "/" + idA);

        um.setMarkerType(MarkerType.TRAIN);
        um.setUmPair(trainA, trainB);

        um.setLatitude((trainA.getLatitude() + trainB.getLatitude()) / 2.0);
        um.setLongitude((trainA.getLongitude() + trainB.getLongitude()) / 2.0);
        um.setBearing((trainA.getBearing() + trainB.getBearing()) / 2.0f);
        um.setFillColor(trainA.getFillColor());
        um.setTextColor(trainA.getTextColor());
        um.setLineNumber(trainA.getLineNumber() + "/" + trainB.getLineNumber());
        um.setNetworkRef(trainA.getNetworkRef());

        return um;
    }

    /**
     * Fusionne deux tracés de véhicule (lignes géographiques) pour une unité multiple (UM).
     *
     * @param routeA le tracé de la première rame
     * @param routeB le tracé de la deuxième rame
     * @return le tracé fusionné prêt à être affiché
     */
    public static BusTrackerVehiclePath assembleRouteUm(BusTrackerVehiclePath routeA, BusTrackerVehiclePath routeB) {
        // TODO: Implémenter la fusion des géométries des deux tracés
        if (routeA != null) return routeA;
        return routeB;
    }
}
