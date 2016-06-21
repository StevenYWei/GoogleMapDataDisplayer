package toolPackage;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PGraphics;

/**
 * This class define the marker of the city, shape and size etc.
 * @author Yuming
 * 
 */
public class CityMarker extends CommonMarker {
	
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
		super(feature);
	}
	

	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		// Save previous drawing style
		pg.pushStyle();

		// Set color and draw triangle
		pg.fill(247, 97, 22);
		pg.triangle(x, y - 7, x - 4, y + 3, x + 4, y + 3);

		// Restore previous drawing style
		pg.popStyle();
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		if(this.isSelected()) {
			String cityInfo = this.getProperty("name").toString() + "-" + this.getProperty("country").toString() +  "-" + this.getProperty("population").toString();
			pg.fill(0,0,0);
			pg.text(cityInfo,x + 15, y);
			pg.line(x + 15, y + 4, x + 15 + cityInfo.length()*6, y + 4);
		}
	}
}
