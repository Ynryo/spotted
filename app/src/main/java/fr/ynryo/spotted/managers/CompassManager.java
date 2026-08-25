package fr.ynryo.spotted.managers;

import android.widget.FrameLayout;
import android.widget.ImageView;

import fr.ynryo.spotted.MainActivity;
import fr.ynryo.spotted.R;

public class CompassManager {
    private final MainActivity context;
    private final ImageView needleLayer;

    public CompassManager(MainActivity context) {
        this.context = context;
        this.needleLayer = context.findViewById(R.id.compass_needle);

        FrameLayout flCompass = context.findViewById(R.id.compass);
        flCompass.setOnClickListener(view -> mapToNorth());
    }

    public void updateAzimuth(float azimuth) {
        if (needleLayer != null) {
            needleLayer.setRotation(-azimuth);
        }
    }

    public void mapToNorth() {
        if (context.getMapManager() != null) {
            context.getMapManager().resetToNorth();
        }
    }
}
