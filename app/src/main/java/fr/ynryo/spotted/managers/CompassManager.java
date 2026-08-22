package fr.ynryo.spotted.managers;

import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import fr.ynryo.spotted.MainActivity;
import fr.ynryo.spotted.R;

public class CompassManager {
    private final MainActivity context;
    private final ImageView needleLayer;

    public CompassManager(MainActivity context) {
        this.context = context;
        this.needleLayer = context.findViewById(R.id.compass_needle);

        FloatingActionButton fabCompass = context.findViewById(R.id.compass);
        fabCompass.setOnClickListener(view -> mapToNorth());
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
