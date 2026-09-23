package fr.ynryo.spotted;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.junit.Test;

import java.util.List;

import fr.ynryo.spotted.apiResponsesPOJO.path.SpottedPathPoint;
import fr.ynryo.spotted.apiResponsesPOJO.path.SpottedPathResponse;

public class SpottedPathResponseTest {

    @Test
    public void testDeserializeCompactPathAndCancelledSegments() {
        String json = "{"
                + "\"path\":{\"p\":[[47.6868,-3.00771,null],[47.68679,-3.00771,1.1],[47.68679,-3.0077,1.9]]},"
                + "\"cancelled\":{\"segments\":[[[47.67956,-3.00355],[47.67945,-3.00522]]]}"
                + "}";

        Gson gson = new Gson();
        SpottedPathResponse response = gson.fromJson(json, SpottedPathResponse.class);

        assertNotNull(response);
        assertNotNull(response.getPath());
        assertNotNull(response.getCancelled());

        // Main path LatLng
        List<LatLng> mainCoordinates = response.getMainPathCoordinates();
        assertEquals(3, mainCoordinates.size());
        assertEquals(47.6868, mainCoordinates.get(0).latitude, 0.00001);
        assertEquals(-3.00771, mainCoordinates.get(0).longitude, 0.00001);
        assertEquals(47.68679, mainCoordinates.get(1).latitude, 0.00001);

        // Cancelled segments LatLng
        List<List<LatLng>> cancelledSegments = response.getCancelledSegmentsCoordinates();
        assertEquals(1, cancelledSegments.size());
        List<LatLng> firstSegment = cancelledSegments.get(0);
        assertEquals(2, firstSegment.size());
        assertEquals(47.67956, firstSegment.get(0).latitude, 0.00001);
        assertEquals(-3.00355, firstSegment.get(0).longitude, 0.00001);
        assertEquals(47.67945, firstSegment.get(1).latitude, 0.00001);

        // Backward-compatible getPoints()
        List<SpottedPathPoint> points = response.getPoints();
        assertEquals(3, points.size());
        assertEquals(47.6868, points.get(0).getLatitude(), 0.00001);
        assertNull(points.get(0).getDistance());
        assertEquals(Double.valueOf(1.1), points.get(1).getDistance());
    }
}
