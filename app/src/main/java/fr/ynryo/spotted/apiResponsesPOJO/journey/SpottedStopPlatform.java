package fr.ynryo.spotted.apiResponsesPOJO.journey;

import com.google.gson.annotations.SerializedName;

public class SpottedStopPlatform {
    @SerializedName("name")
    private String name;

    @SerializedName("percentage")
    private Object percentage;

    public String getName() {
        return name;
    }

    public double getPercentage() {
        if (percentage instanceof Number) {
            return ((Number) percentage).doubleValue();
        }
        if (percentage instanceof String) {
            try {
                return Double.parseDouble((String) percentage);
            } catch (Exception ignored) {
            }
        }
        return 100.0;
    }
}
