package toolPackage;

//Processing library
import processing.core.PApplet;

//Java utilities libraries
import java.util.ArrayList;
import java.util.List;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

public class EarthQuakeMap extends PApplet{

	private static final long serialVersionUID = 6090104746129831548L;
	private final static float EARTHQUAKE_SEVERE = 5;
	private final static float EARTHQUAKE_MEDIUM = 4;
	private UnfoldingMap map;
	private final static String earthQuakeURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	private List<PointFeature> quakeFeature;
	private List<Marker> markers;
	public void setup() {
		// Set the size of the windows
		size(970, 620, OPENGL);
		// Initialize the map
		map = new UnfoldingMap(this, 10, 10, 950, 600, new Google.GoogleMapProvider());
		// Low zoom level that we can see a lot
		map.zoomLevel(0);
		// Zoom, pan and click event
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// Read data from earthquake feed
		quakeFeature = ParseFeed.parseEarthquake(this, earthQuakeURL);

		// Create simple marker according to the data in List<PointFeature>
		markers = createMarker(quakeFeature);
		
		// Add markers to the map
		map.addMarkers(markers);
		
	}
	
	
	public void draw() {
		
		background(150);
		map.draw();
		// Add legend to the map.
		addLegend();
	}
	
	/**
	 * This function use the location information from point feature to create markers for each location
	 * @param quakeFeature is a list with data type PointFeature, which contains information of the earthquake 
	 * location, such as latitude and longitude, magnitude and title etc.
	 * @return the marker is return as an arraylist
	 */
	private ArrayList<Marker> createMarker(List<PointFeature> quakeFeature) {
		
		// Arraylist to store markers
		ArrayList<Marker> markerList = new ArrayList<Marker>();
		Location loc;
		SimplePointMarker marker;
		// The magnitude of the earthquake in float number
		float quakeMagf;
		for(PointFeature feature : quakeFeature) {
			loc = feature.getLocation();
			if(loc != null) {
				// Create SimplePointMarker for the location with the properties
				marker = new SimplePointMarker(loc, feature.getProperties());
				quakeMagf = Float.parseFloat(marker.getProperty("magnitude").toString());
				// Classify the earthquake into three categories and set different marker sizes and colors according to category
				if(quakeMagf > EARTHQUAKE_SEVERE) {
					// Color red
					marker.setColor(color(255, 0, 0));
					marker.setRadius(18);
				} else if(quakeMagf < EARTHQUAKE_MEDIUM) {
					// Color blue
					marker.setColor(color(0, 0, 255));
					marker.setRadius(8);
				} else {
					// Color yellow
					marker.setColor(color(255, 255, 0));
					marker.setRadius(12);
				}
				markerList.add(marker);
			}
		}
		return markerList;
	}
	
	/**
	 * This function draws a panel contains the legends of the markers.
	 */
	private void addLegend() {
		// Yellow Panel
		fill(color(255, 255, 200));
		rect(20, 340, 160,250);

		// Panel Title 
		fill(color(0, 0 ,0));
		text("Earthquake Key", 45, 380);

		// 5.0+
		fill(color(255, 0, 0));
		ellipse(43, 420, 18, 18);
		fill(color(0, 0 ,0));
		text("5.0+ Magnitude", 60, 425);

		// 4.0+
		fill(color(255, 255, 0));
		ellipse(43, 475, 12, 12);
		fill(color(0, 0 ,0));
		text("4.0+ Magnitude", 60, 480);

		// < 4.0
		fill(color(0, 0, 255));
		ellipse(43, 530, 8, 8);
		fill(color(0, 0 ,0));
		text("< 4.0 Magnitude", 60, 535);
	}
	
}
