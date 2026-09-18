package fr.ynryo.spotted.genericMarkerDatas;

import androidx.annotation.NonNull;

public class MarkerIdentity {
    private VehicleType vehicleType; // Type du véhicule (train ou bus/tram)
    private String id; // ID unique (numéro train ou id bus tracker)
    private int lineId; // Numéro de ligne (vehicleNumber pour train et lineNumber pour le reste)
    private String lineNumber; // Numéro de ligne pour l'affichage
    private String networkRef; // Référence réseau (ex: "SNCF", "RATP")
    private int networkId; // ID numérique du réseau (pour fetch logo)

    public MarkerIdentity() {
        this.vehicleType = VehicleType.BUS_TRAM;
        this.id = "";
        this.lineId = 0;
        this.lineNumber = "";
        this.networkRef = "";
        this.networkId = 0;
    }

    public MarkerIdentity(VehicleType vehicleType, String id, int lineId, String lineNumber, String networkRef) {
        this.vehicleType = vehicleType;
        this.id = id;
        this.lineId = lineId;
        this.lineNumber = lineNumber;
        this.networkRef = networkRef;
    }

    public MarkerIdentity(VehicleType vehicleType, String id, int lineId, String lineNumber, String networkRef, int networkId) {
        this(vehicleType, id, lineId, lineNumber, networkRef);
        this.networkId = networkId;
    }

    public VehicleType getMarkerType() {
        return vehicleType;
    }

    public String getId() {
        return id;
    }

    public int getLineId() {
        return lineId;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public String getNetworkRef() {
        return networkRef;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setMarkerType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setNetworkRef(String networkRef) {
        this.networkRef = networkRef;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    @NonNull
    @Override
    public String toString() {
        return "MarkerIdentity{" +
                "vehicleType=" + vehicleType +
                ", id='" + id + '\'' +
                ", lineId=" + lineId +
                ", lineNumber='" + lineNumber + '\'' +
                ", networkRef='" + networkRef + '\'' +
                ", networkId=" + networkId +
                '}';
    }
}
