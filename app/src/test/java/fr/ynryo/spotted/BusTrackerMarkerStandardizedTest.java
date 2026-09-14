package fr.ynryo.spotted;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import fr.ynryo.spotted.apiResponsesPOJO.markers.BusTrackerMarkerData;
import fr.ynryo.spotted.genericMarkerDatas.MarkerStandardized;
import fr.ynryo.spotted.genericMarkerDatas.MarkerType;

public class BusTrackerMarkerStandardizedTest {

    @Test
    public void testCreateNewMarkerFromWithNullOrEmptyFieldsDoesNotThrow() {
        BusTrackerMarkerData busTrackerMarkerData = new BusTrackerMarkerData();
        MarkerType type = MarkerType.guessFromMarkerId(busTrackerMarkerData.getId());

        MarkerStandardized standardized = MarkerStandardized.createNewMarkerFrom(busTrackerMarkerData, type);
        assertNotNull(standardized);
        assertNull(standardized.getId());
        assertNull(standardized.getLineNumber());
        assertEquals("", standardized.getNetworkRef());
    }

    @Test
    public void testDefaultConstructorDoesNotThrowOnGettersAndSetters() {
        MarkerStandardized marker = new MarkerStandardized();
        assertNotNull(marker.getId());
        assertEquals(0.0, marker.getLatitude(), 0.0001);
        assertEquals(0.0, marker.getLongitude(), 0.0001);
        assertEquals(0.0f, marker.getBearing(), 0.0001f);
        assertEquals("#424242", marker.getFillColor());
        assertEquals("#FFFFFF", marker.getTextColor());

        marker.setId("SNCF::12345");
        assertEquals("SNCF::12345", marker.getId());
        marker.setLatitude(48.8566);
        assertEquals(48.8566, marker.getLatitude(), 0.0001);
    }

    @Test
    public void testDestinationAndPathRefsForSimpleTrain() {
        MarkerStandardized marker = new MarkerStandardized();
        marker.setDestination("Paris Gare de Lyon");
        marker.setPathRef("path_123");

        assertEquals("Paris Gare de Lyon", marker.getDestination());

        assertEquals("path_123", marker.getPathRef());
        assertEquals(1, marker.getPathRefs().size());
        assertEquals("path_123", marker.getPathRefs().get(0));
    }

    @Test
    public void testDestinationAndPathRefsForUmTrain() {
        MarkerStandardized trainA = new MarkerStandardized();
        trainA.setMarkerType(MarkerType.TRAIN);
        trainA.setDestination("Nantes");
        trainA.setPathRef("path_A");

        MarkerStandardized trainB = new MarkerStandardized();
        trainB.setMarkerType(MarkerType.TRAIN);
        trainB.setDestination("Rennes");
        trainB.setPathRef("path_B");

        MarkerStandardized um = new MarkerStandardized();
        um.setMarkerType(MarkerType.TRAIN);
        um.setUmPair(trainA, trainB);

        assertEquals("Nantes/Rennes", um.getDestination());

        assertEquals(2, um.getPathRefs().size());
        assertEquals("path_A", um.getPathRefs().get(0));
        assertEquals("path_B", um.getPathRefs().get(1));

        // Test déduplication si même destination
        trainB.setDestination("Nantes");
        assertEquals("Nantes", um.getDestination());
    }

    @Test
    public void testAssembleRouteUmCall() {
        fr.ynryo.spotted.apiResponsesPOJO.bus.BusTrackerVehiclePath pathA = new fr.ynryo.spotted.apiResponsesPOJO.bus.BusTrackerVehiclePath();
        fr.ynryo.spotted.apiResponsesPOJO.bus.BusTrackerVehiclePath pathB = new fr.ynryo.spotted.apiResponsesPOJO.bus.BusTrackerVehiclePath();

        fr.ynryo.spotted.apiResponsesPOJO.bus.BusTrackerVehiclePath result =
                fr.ynryo.spotted.managers.um.TrainUmProcessor.assembleRouteUm(pathA, pathB);

        assertNotNull(result);
    }
}
