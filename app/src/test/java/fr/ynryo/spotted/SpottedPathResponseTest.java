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
    public void testDeserializeSegmentsAndDeviatedSegments() {
        String json = "{"
                + "\"segments\":[[{\"latitude\":46.581367,\"longitude\":0.335801,\"distance\":null},{\"latitude\":46.58119,\"longitude\":0.33578,\"distance\":12.5}]],"
                + "\"deviatedSegments\":[[{\"latitude\":49.349405,\"longitude\":1.095599},{\"latitude\":49.349433,\"longitude\":1.095506}]]"
                + "}";

        Gson gson = new Gson();
        SpottedPathResponse response = gson.fromJson(json, SpottedPathResponse.class);

        assertNotNull(response);
        assertNotNull(response.getSegments());
        assertNotNull(response.getDeviatedSegments());

        // Main path LatLng
        List<LatLng> mainCoordinates = response.getMainPathCoordinates();
        assertEquals(2, mainCoordinates.size());
        assertEquals(46.581367, mainCoordinates.get(0).latitude, 0.00001);
        assertEquals(0.335801, mainCoordinates.get(0).longitude, 0.00001);
        assertEquals(46.58119, mainCoordinates.get(1).latitude, 0.00001);
        assertEquals(0.33578, mainCoordinates.get(1).longitude, 0.00001);

        // Cancelled / deviated segments LatLng
        List<List<LatLng>> deviatedSegments = response.getCancelledSegmentsCoordinates();
        assertEquals(1, deviatedSegments.size());
        List<LatLng> firstDeviated = deviatedSegments.get(0);
        assertEquals(2, firstDeviated.size());
        assertEquals(49.349405, firstDeviated.get(0).latitude, 0.00001);
        assertEquals(1.095599, firstDeviated.get(0).longitude, 0.00001);

        // Flattened getPoints()
        List<SpottedPathPoint> points = response.getPoints();
        assertEquals(2, points.size());
        assertEquals(46.581367, points.get(0).getLatitude(), 0.00001);
        assertNull(points.get(0).getDistance());
        assertEquals(Double.valueOf(12.5), points.get(1).getDistance());

        // Count
        assertEquals(2, response.getCount());
    }
}