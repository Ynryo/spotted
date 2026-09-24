package fr.ynryo.spotted.genericMarkerDatas;

public enum VehicleType {
    TRAIN, BUS_TRAM;

    /**
     * Détermine le type en fonction de l'ID du marqueur.
     * Si l'ID contient "SNCF", c'est un train.
     *
     * @param markerId L'ID du marqueur
     * @return TRAIN si l'ID commence par "SNCF", sinon c'est un BUS_TRAM
     */
    public static VehicleType guessFromMarkerId(String markerId) {
        if (markerId != null && markerId.startsWith("SNCF")) return TRAIN;
        return BUS_TRAM;
    }
}