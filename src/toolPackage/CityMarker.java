package toolPackage;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

/**
 * This class define the marker of the city, shape and size etc.
 * @author Yuming
 * 
 */
public class CityMarker extends SimplePointMarker {
	
	/**
	 * Constructor with location information as parameter
	 * @param loc is the location information with Location data type
	 */
	public CityMarker(Location loc) {
		super(loc);
	}
	
	/**
	 * Constructor with feature information as parameter
	 * @param feature contains information of the earthquake, depth, magnitude and title etc.
	 */
	public CityMarker(PointFeature feature) {
		super(feature.getLocation(), feature.getProperties());
	}
	
	/*
	 * Draw a triangle on the city
	 * @see de.fhpotsdam.unfolding.marker.SimplePointMarker#draw(processing.core.PGraphics, float, float)
	 */
	public void draw(PGraphics pg, float x, float y) {
		// Save previous drawing style
		pg.pushStyle();
		
		// Set color and draw triangle
		pg.fill(247, 97, 22);
		pg.triangle(x, y - 5, x - 3, y + 2, x + 3, y + 2);
		
		// Restore previous drawing style
		pg.popStyle();
	}
}
