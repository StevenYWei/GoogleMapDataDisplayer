package toolPackage;

//Processing library
import processing.core.PApplet;

//Java utilities libraries
import java.util.ArrayList;
import java.util.List;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoDataReader;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
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
	private List<PointFeature> earthquakeFeature;
	private List<Marker> earthquakeMarkers;
	private List<Marker> countryMarkers;
	private List<Marker> cityMarkers;
	private List<Feature> countryFeature;
	private List<Feature> cityFeature;
	private String cityDataFile = "city-data.json";
	private String countryDataFile = "countries.geo.json";
	
	public void setup() {
		// Set the size of the windows
		size(1200, 650, OPENGL);
		// Initialize the map
		map = new UnfoldingMap(this, 200, 10, 950, 620, new Google.GoogleMapProvider());
		// Low zoom level that we can see a lot
		map.zoomLevel(0);
		// Zoom, pan and click event
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// Read data from earthquake feed
		earthquakeFeature = ParseFeed.parseEarthquake(this, earthQuakeURL);
		
		// Read country data from RSS feed;
		countryFeature = GeoJSONReader.loadData(this, countryDataFile);
		countryMarkers = MapUtils.createSimpleMarkers(countryFeature);
		
		// Read city data from RSS feed;
		cityFeature = GeoJSONReader.loadData(this, cityDataFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature feature : cityFeature) {
			cityMarkers.add(new CityMarker((PointFeature) feature));
		}
		
		// Create earthquake markers according to the data in List<PointFeature>
		earthquakeMarkers = new ArrayList<Marker>();
		createEarthquakeMarker(earthquakeFeature);
		
		// Add markers to the map
		map.addMarkers(earthquakeMarkers);
		map.addMarkers(cityMarkers);
	}
	
	public void draw() {
		
		background(150);
		map.draw();
		// Add legend to the map.
		addLegend();
	}
	
	/**
	 * This function creates the earthquake markers according to the earthquake features and 
	 * categorize into LandQuake and OceanQuake
	 * @param quakeFeatures is a list with data type PointFeature, which contains information of the earthquake 
	 * location, such as latitude and longitude, magnitude and title etc.
	 * @return
	 */
	public void createEarthquakeMarker(List<PointFeature> quakeFeatures) {
		for(PointFeature feature : quakeFeatures) {
			if(isInCountry(feature)) {
				earthquakeMarkers.add(new LandQuakeMarker(feature));
			} else {
				earthquakeMarkers.add(new OceanQuakeMarker(feature));
			}
		}
	}
	/**
	 * This function use the location information from point feature to create simple point markers for each location
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
		float earthquakeMagnitude;
		for(PointFeature feature : quakeFeature) {
			loc = feature.getLocation();
			if(loc != null) {
				// Create SimplePointMarker for the location with the properties
				marker = new SimplePointMarker(loc, feature.getProperties());
				earthquakeMagnitude = Float.parseFloat(marker.getProperty("magnitude").toString());
				// Classify the earthquake into three categories and set different marker sizes and colors according to category
				if(earthquakeMagnitude > EARTHQUAKE_SEVERE) {
					// Color red
					marker.setColor(color(255, 0, 0));
					marker.setRadius(18);
				} else if(earthquakeMagnitude < EARTHQUAKE_MEDIUM) {
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
	 * This function returns whether the location is inland
	 * @param feature contains information about the earthquake.
	 * @return true is it's inland, false if it's not inland.
	 */
	public boolean isLand(PointFeature feature) {

		return isInCountry(feature);
	}
	
	/**
	 * This function takes in the earthquake feature and determines whether the quake happens inland
	 * or in the ocean, if it's inland, assigns it to corresponding country and add one more count on
	 * the earthquakeCount feature of the country marker.
	 * @param feature contains information about the earthquake.
	 * @return true is it is inland, false in the ocean
	 */
	public boolean isInCountry(PointFeature feature) {
		
		Location loc = feature.getLocation();
		for(Marker marker : countryMarkers) {
			if(marker.getClass() == MultiMarker.class) {
				for(Marker subMultiMarker : ((MultiMarker) marker).getMarkers()) {
					if(((AbstractShapeMarker) subMultiMarker).isInsideByLocation(loc)) {
						feature.addProperty("country", marker.getProperty("name"));
						if(marker.getProperty("earthquakeCount") == null) {
							marker.setProperty("earthquakeCount", (int) 1);
						} else {
							marker.setProperty("earthquakeCount", (int) marker.getProperty("earthquakeCount") + 1);
						}
						return true;
					}
				}
			} else {
				if(((AbstractShapeMarker) marker).isInsideByLocation(loc)) {
					feature.addProperty("country", marker.getProperty("name"));
					if(marker.getProperty("earthquakeCount") == null) {
						marker.setProperty("earthquakeCount", (int) 1);
					} else {
						marker.setProperty("earthquakeCount", (int) marker.getProperty("earthquakeCount") + 1);
					}
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * This function draws a panel contains the legends of the markers.
	 */
	private void addLegend() {
		// Yellow Panel
		fill(color(255, 255, 200));
		rect(20, 10, 160, 300);

		// Panel Title 
		fill(color(0, 0 ,0));
		text("Earthquake Key", 45, 50);

		// 5.0+
		fill(color(255, 0, 0));
		ellipse(43, 90, 18, 18);
		fill(color(0, 0 ,0));
		text("5.0+ Magnitude", 60, 93);

		// 4.0+
		fill(color(255, 255, 0));
		ellipse(43, 126, 12, 12);
		fill(color(0, 0 ,0));
		text("4.0+ Magnitude", 60, 129);

		// < 4.0
		fill(color(0, 0, 255));
		ellipse(43, 160, 8, 8);
		fill(color(0, 0 ,0));
		text("< 4.0 Magnitude", 60, 162);
		
		//
		fill(color(0, 0, 0));
		text("Size - Magnitude", 43, 200);
		
		// Shallow
		fill(color(255, 255, 0));
		ellipse(43, 230, 15, 15);
		fill(color(0, 0, 0));
		text("Shallow", 60, 233);
		
		// Intermediate
		fill(color(0, 0, 255));
		ellipse(43, 260, 15, 15);
		fill(color(0, 0, 0));
		text("Intermediate", 60, 263);
		
		// Deep
		fill(color(255, 0, 0));
		ellipse(43, 290, 15, 15);
		fill(color(0, 0, 0));
		text("Deep", 60, 293);
		
	}
	
}
