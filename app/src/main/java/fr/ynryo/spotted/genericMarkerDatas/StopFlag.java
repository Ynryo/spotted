package fr.ynryo.spotted.genericMarkerDatas;

public enum StopFlag {
    NO_DROPOFF("Montée uniquement"),
    NO_PICKUP("Descente uniquement"),
    BOTH("");

    private final String displayName;

    StopFlag(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}