package toolPackage;

//Java utilities libraries
import java.util.ArrayList;
import java.util.List;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
//Parsing library
import parsing.ParseFeed;
//Processing library
import processing.core.PApplet;

public class EarthQuakeMap extends PApplet{

	private static final long serialVersionUID = 6090104746129831548L;
	
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
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
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
		
		if(lastSelected != null) {
			lastSelected.draw(map);
		}
	}
	
	/*
	 * This method hides all other markers except the selected quake marker and the impacted city
	 * @see processing.core.PApplet#mouseClicked()
	 */
	@Override
	public void mouseClicked() {
		
	}
	
	/*
	 * This method shows the tile when the mouse moves on a marker
	 * @see processing.core.PApplet#mouseMoved()
	 */
	@Override
	public void mouseMoved() {
		
		if(lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		}
		selectMarkerHovering(earthquakeMarkers);
		selectMarkerHovering(cityMarkers);
		
	}
	
	/**
	 * This function helps to determine whether the mouse within one of the marker area, if yes,
	 * show the title of the marker.
	 * @param mouseX is the X coordinate of the mouse
	 * @param mouseY is the Y coordinate of the mouse
	 */
	public void selectMarkerHovering(List<Marker> markers) {
		for(Marker marker : markers) {
			if(marker.isInside(map, mouseX, mouseY)) {
				lastSelected = (CommonMarker) marker;
				lastSelected.setSelected(true);
				return;
			}
		}
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
		rect(20, 10, 150, 340);

		// Panel Title 
		fill(color(0, 0 ,0));
		text("Earthquake Key", 42, 50);

		// City Marker
		fill(color(255, 0, 0));
		triangle(44, 83, 38, 95, 49, 95);
		fill(color(0, 0 ,0));
		text("City Marker", 60, 93);

		// Land quake
		fill(color(255, 255, 255));
		ellipse(44, 126, 12, 12);
		fill(color(0, 0 ,0));
		text("Land Quake", 60, 129);

		// Ocen Quake
		fill(color(255, 255, 255));
		rect(40, 153, 10, 10);
		fill(color(0, 0 ,0));
		text("Ocen Quake", 60, 162);
		
		//
		fill(color(0, 0, 0));
		text("Size - Magnitude", 40, 200);
		
		// Shallow
		fill(color(255, 255, 0));
		ellipse(46, 230, 15, 15);
		fill(color(0, 0, 0));
		text("Shallow", 63, 233);
		
		// Intermediate
		fill(color(0, 0, 255));
		ellipse(46, 260, 15, 15);
		fill(color(0, 0, 0));
		text("Intermediate", 63, 263);
		
		// Deep
		fill(color(255, 0, 0));
		ellipse(46, 290, 15, 15);
		fill(color(0, 0, 0));
		text("Deep", 63, 293);
		
		// Past Hour
		fill(color(255, 255, 255));
		ellipse (46, 320, 15, 15);
		line(36, 310, 56, 330);
		line(36, 330, 56, 310);
		fill(color(0, 0, 0));
		text("Past Hour", 63, 323);
	}
	
}
