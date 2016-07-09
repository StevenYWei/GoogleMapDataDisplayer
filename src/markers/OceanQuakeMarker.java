package markers;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PGraphics;

/**
 * This class defines the quakes occur in the ocean
 * @author Yuming
 * 06/21/2016
 */
public class OceanQuakeMarker extends EarthQuakeMarker {

	/**
	 * Constructor with location information as parameter
	 * @param loc is the location information as type Location
	 */
	public OceanQuakeMarker(Location loc) {
		super(loc);
	}

	/**
	 * Constructor with feature information as parameter
	 * @param feature contains information of the earthquake, depth, magnitude and title etc.
	 */
	public OceanQuakeMarker(PointFeature feature) {
		super(feature);
	}

	/*
	 * Draw a cross on the rectangle
	 * @see toolPackage.EarthQuakeMarker#drawCross(processing.core.PGraphics, float, float)
	 */
	@Override
	public void drawCross(PGraphics pg, float x, float y) {
		pg.fill(0, 0, 0);
		pg.line(x - radius/2, y - radius/2, x + radius/2, y + radius/2);
		pg.line(x - radius/2, y + radius/2, x + radius/2, y - radius/2);

	}

	/*
	 * Draw a rectangle on the ocean quake location
	 * @see toolPackage.EarthQuakeMarker#drawEarthquakeMarker(processing.core.PGraphics, float, float)
	 */
	@Override
	public void drawEarthquakeMarker(PGraphics pg, float x, float y) {
		pg.rect(x - radius/2, y - radius/2, radius, radius);
	}

	/*
	 * This method compare the magnitude of the earthquake maker and output the reverse result
	 * If the magnitude of this earthquake is larger than the feed-in earthquake, than output -1, when 
	 * using Java input built-in sort, will sort in descendant order
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(EarthQuakeMarker o) {
		if(Float.parseFloat(this.getProperty("magnitude").toString()) > Float.parseFloat(o.getProperty("magnitude").toString())) {
			return -1;
		} else if(Float.parseFloat(this.getProperty("magnitude").toString()) < Float.parseFloat(o.getProperty("magnitude").toString())) {
			return 1;
		}
		return 0;
	}
}
