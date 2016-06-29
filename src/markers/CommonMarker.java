package markers;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

/**
 * This class is a common marker class that define some common feature of the marker
 * @author Yuming
 * 06/21/2016
 */
public abstract class CommonMarker extends SimplePointMarker {

	protected boolean clicked = false;
	/**
	 * Constructor with location parameter.
	 * @param loc
	 */
	public CommonMarker(Location loc) {
		super(loc);
	}
	
	/**
	 * constructor with location parameter and properties.
	 * @param feature
	 */
	public CommonMarker(PointFeature feature) {
		super(feature.getLocation(), feature.getProperties());
	}
	
	
	// Set the status of the click
	public boolean setClicked(boolean state) {
		clicked = state;
		return clicked;
	}
	
	// Return the status of the click
	public boolean getClicked() {
		return clicked;
	}
	
	public void draw(PGraphics pg, float x, float y) {
		
		// If the marker is not being hidden
		if(!hidden) {
			pg.pushStyle();
			// Draw the marker according to the subclass
			drawMarker(pg, x, y);
			// If the marker is selected, show the title of the marker according to the subclass
			if(selected) {
				showTitle(pg, x, y);
			}
			pg.popStyle();
		}
		
	}
	
	/**
	 * Common drawMarker method, will draw marker according to specific marker type, will implement by the subclass
	 * @param pg is the Processing Graphics object
	 * @param x is the X coordinate of the marker
	 * @param y is the Y coordinate of the marker
	 */
	public abstract void drawMarker(PGraphics pg, float x, float y);
	/**
	 * This method will show some information near the marker which mouse is on according to different marker type
	 * @param pg is the Processing Graphics object
	 * @param x is the X coordinate of the marker
	 * @param y is the Y coordinate of the marker
	 */
	public abstract void showTitle(PGraphics pg, float x, float y);
	
}
