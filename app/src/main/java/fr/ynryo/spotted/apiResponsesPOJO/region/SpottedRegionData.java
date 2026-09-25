package fr.ynryo.spotted.apiResponsesPOJO.region;

import androidx.annotation.NonNull;

public class SpottedRegionData {
    private int id;
    private String name;

    public SpottedRegionData(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return "SpottedRegionData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
