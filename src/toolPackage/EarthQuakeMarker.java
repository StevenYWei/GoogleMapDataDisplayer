package toolPackage;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PGraphics;

/**
 * This class defines the abstract marker class for the earthquakes
 * @author Yuming
 *
 */
public abstract class EarthQuakeMarker extends CommonMarker {

	public static final float EARTHQUAKE_SEVERE = 5;
	public static final float EARTHQUAKE_MEDIUM = 4;
	public static final float BASE_RADIUS = 10;
	public static final float EARTHQUAKE_SHALLOW = 70;
	public static final float EARTHQUAKE_DEEP = 300;
	
	public abstract void drawEarthquakeMarker(PGraphics pg, float x, float y);
	
	/**
	 * Constructor with location information as parameter
	 * @param loc is the location information with Location data type
	 */
	public EarthQuakeMarker(Location loc) {
		super(loc);
	}
	
	/**
	 * Constructor with feature information as parameter
	 * @param feature contains information of the earthquake, depth, magnitude and title etc.
	 */
	public EarthQuakeMarker(PointFeature feature) {
		super(feature);
		float magnitude = Float.parseFloat(feature.getProperty("magnitude").toString());
		if(magnitude > EARTHQUAKE_SEVERE) {
			radius = (float) (BASE_RADIUS * 2);
		} else if(magnitude < EARTHQUAKE_MEDIUM) {
			radius = BASE_RADIUS;
		} else {
			radius = (float) (BASE_RADIUS * 1.5);
		}
	}
	
	public void drawMarker(PGraphics pg, float x, float y) {
		
		// Save original style
		pg.pushStyle();
		// Determine the color
		determineColor(pg);
		//Draw quake marker based on subclass type implementation
		drawEarthquakeMarker(pg, x, y);
		
		// If the earthquake happens within 1 hour, draw a cross on the marker
		if(super.getProperty("age").equals("Past Hour")) {
			drawCross(pg, x, y);
		}
		
		// Reverse to the original style
		pg.popStyle();	
	}
	
	/**
	 * Draw cross on the marker
	 * @param pg is processing graph object
	 * @param x is earthquake x location on the graph
	 * @param y is earthquake y location on the graph
	 */
	public abstract void drawCross(PGraphics pg, float x, float y);
	
	/**
	 * This determines the color of the marker based on the depth of the earthquake
	 * @param feature contains information of the earthquake, depth, magnitude and title etc.
	 * @param pg is processing graph object
	 */
	public void determineColor(PGraphics pg) {
		float depth = Float.parseFloat(getProperty("depth").toString());
		if(depth > EARTHQUAKE_DEEP) {
			pg.fill(255, 0, 0);
		} else if(depth < EARTHQUAKE_SHALLOW) {
			pg.fill(255, 255, 0);
		} else {
			pg.fill(0, 0, 255);
		}
	}
	
	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		String earthquakeInfo = this.getProperty("title").toString();
		pg.fill(0,0,0);
		pg.text(earthquakeInfo, x + 15, y);	
		pg.line(x + 15,  y + 4, x + 15 + earthquakeInfo.length()*6, y + 4);
	}
}
