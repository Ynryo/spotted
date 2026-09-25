package fr.ynryo.spotted;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import fr.ynryo.spotted.apiResponsesPOJO.markers.SpottedMarkerData;
import fr.ynryo.spotted.genericMarkerDatas.MarkerStandardized;
import fr.ynryo.spotted.genericMarkerDatas.VehicleType;

public class SpottedMarkerStandardizedTest {

    @Test
    public void testCreateNewMarkerFromWithNullOrEmptyFieldsDoesNotThrow() {
        SpottedMarkerData markerData = new SpottedMarkerData();
        VehicleType type = VehicleType.guessFromMarkerId(markerData.getId());

        MarkerStandardized standardized = MarkerStandardized.createNewMarkerFrom(markerData, type);
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
}
