package toolPackage;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

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
	
	
	public boolean setClicked(boolean state) {
		clicked = state;
		return clicked;
	}
	
	public boolean getClicked() {
		return clicked;
	}
	
	public void draw(PGraphics pg, float x, float y) {
		
		if(!hidden) {
			pg.pushStyle();
			drawMarker(pg, x, y);
			if(selected) {
				showTitle(pg, x, y);
			}
			pg.popStyle();
		}
		
	}
	
	/**
	 * Will implement by the subclass
	 */
	public abstract void drawMarker(PGraphics pg, float x, float y);
	public abstract void showTitle(PGraphics pg, float x, float y);
	
}
