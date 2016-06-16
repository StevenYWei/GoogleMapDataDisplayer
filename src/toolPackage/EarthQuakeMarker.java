package toolPackage;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

/**
 * This class defines the abstract marker class for the earthquakes
 * @author Yuming
 *
 */
public abstract class EarthQuakeMarker extends SimplePointMarker {

	public static final float EARTHQUAKE_SEVERE = 5;
	public static final float EARTHQUAKE_MEDIUM = 4;
	public static final float BASE_RADIUS = 10;
	public static final float EARTHQUAKE_SHALLOW = 70;
	public static final float EARTHQUAKE_DEEP = 300;
	
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
		super(feature.getLocation(), feature.getProperties());
		float magnitude = Float.parseFloat(feature.getProperty("magnitude").toString());
		if(magnitude > EARTHQUAKE_SEVERE) {
			radius = (float) (BASE_RADIUS * 1.7);
		} else if(magnitude < EARTHQUAKE_MEDIUM) {
			radius = BASE_RADIUS;
		} else {
			radius = (float) (BASE_RADIUS * 1.3);
		}
	}
	
	/**
	 * Draw different shapes of marker on the earthquake location
	 * @param pg is processing graph object
	 * @param x is earthquake x location on the graph
	 * @param y is earthquake y location on the graph
	 */
	public abstract void drawQuakeMarker(PGraphics pg, float x, float y);
	
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
	public void determineColor(PointFeature feature, PGraphics pg) {
		float depth = Float.parseFloat(feature.getStringProperty("depth"));
		if(depth > EARTHQUAKE_DEEP) {
			pg.fill(255, 0, 0);
		} else if(depth < EARTHQUAKE_SHALLOW) {
			pg.fill(255, 255, 0);
		} else {
			pg.fill(0, 0, 255);
		}
	}

}
