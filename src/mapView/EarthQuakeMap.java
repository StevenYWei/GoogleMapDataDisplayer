package mapView;

//Java utilities libraries
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractMarker;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import markers.CityMarker;
import markers.CommonMarker;
import markers.EarthQuakeMarker;
import markers.LandQuakeMarker;
import markers.OceanQuakeMarker;
//Parsing library
import parsing.ParseFeed;
//Processing library
import processing.core.PApplet;

/**
 * This class builds a Google Map that shows earthquake and city data
 * @author Yuming
 * 07/08/2016
 */
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
		
		//
		sortAndPrint(10);
	}
	
	public void draw() {
		
		background(150);
		map.draw();
		// Add legend to the map.
		addLegend();
		
		// If clicked on a earthquake marker and there are city inside the impact circle, 
		// then draw a line between the city and the earthquake marker
		if(lastClicked != null) {
			drawLineQuakeToCity();
			drawImpactCircle();
		}
	}
	
	/**
	 * This method print out the top numOfRecordToPrint record in descendant order.
	 * @param numOfRecordToPrint is the record number to print out
	 */
	public void sortAndPrint(int numOfRecordToPrint) {
		Object[] quakeArray = earthquakeMarkers.toArray();
		Arrays.sort(quakeArray);
		
		for(int i = 0; i < numOfRecordToPrint; i++) {
			System.out.println(quakeArray[i]);
		}
	}
	/*
	 * This method hides all other markers except the selected quake marker and the impacted city
	 * @see processing.core.PApplet#mouseClicked()
	 */
	@Override
	public void mouseClicked() {
		if(lastClicked != null) {
			lastClicked.setClicked(false);
			lastClicked = null;
			unhideAllMarkers();
		} else {
			// Find out which earthquake marker is clicked
			EarthQuakeMarker quake = selectEarthquakeMarkers(earthquakeMarkers);
			if(quake != null) {
				// Hide all the earthquake markers except the clicked one.
				hideEarthquakeMarkers(earthquakeMarkers, quake);
				// Hide all the city markers except the cities in the impact circle
				hideCityMarkers(cityMarkers);
		}
		}
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
		// Check whether the mouse is on one of the earthquake markers or city markers
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
				// If the mouse is inside the marker region, then set it to selected
				lastSelected = (CommonMarker) marker;
				lastSelected.setSelected(true);
				return;
			}
		}
	}
	
	/**
	 * This method find out which earthquake is being clicked
	 * @param earthquakeMarkers contains all the earthquakes information parsed from the link
	 * @return An EarthQuakeMarker type marker
	 */
	public EarthQuakeMarker selectEarthquakeMarkers(List<Marker> earthquakeMarkers) {
		for(Marker earthquakeMarker : earthquakeMarkers) {
			if(earthquakeMarker.isInside(map, mouseX, mouseY)) {
				// If the mouse is inside the marker region, then return it
				return (EarthQuakeMarker) earthquakeMarker;
			}
		}
		return null;
	}
	
	/**
	 * This method hide all the earthquake markers except the clicked earthquake marker
	 * @param earthquakeMarkers contains all the earthquakes information parsed from the link
	 * @param quake is the earthquake marker being clicked
	 */
	public void hideEarthquakeMarkers(List<Marker> earthquakeMarkers, EarthQuakeMarker quake) {
		for(Marker earthquakeMarker : earthquakeMarkers) {
			if(earthquakeMarker.equals(quake)) {
				earthquakeMarker.setHidden(false);
				lastClicked = (CommonMarker) earthquakeMarker;
			} else {
				earthquakeMarker.setHidden(true);
			}
		}
	}
	
	/**
	 * 
	 * @param cityMarkers contains all the city information parsed from the file
	 */
	public void hideCityMarkers(List<Marker> cityMarkers) {
		for(Marker cityMarker : cityMarkers) {
			if(lastClicked != null) {
				// Check the distance between the city and the earthquake to see whether the city is in the impact circle
				if(cityMarker.getDistanceTo(lastClicked.getLocation()) < ((EarthQuakeMarker) lastClicked).getImpactDistance()) {
					cityMarker.setHidden(false);
				} else {
					cityMarker.setHidden(true);
				}
			}
		}
	}
	
	/**
	 * Show all the earthquake and city markers
	 */
	public void unhideAllMarkers() {
		for(Marker marker : earthquakeMarkers) {
			marker.setHidden(false);
		}
		for(Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
	}
	
	/**
	 * If the city is inside the impact circle, draw a line between the city and the earthquake
	 */
	public void drawLineQuakeToCity() {
		for(Marker cityMarker : cityMarkers) {
			if(cityMarker.getDistanceTo(lastClicked.getLocation()) < ((EarthQuakeMarker)lastClicked).getImpactDistance()) {
				line(((AbstractMarker)cityMarker).getScreenPosition(map).x, ((AbstractMarker)cityMarker).getScreenPosition(map).y, lastClicked.getScreenPosition(map).x, lastClicked.getScreenPosition(map).y);
			}
		}
	}
	
	/**
	 * Draw a circle whose center is the location of the earthquake marker. To be fixed.
	 */
	public void drawImpactCircle() {
		float xLoc, yLoc, impactDist;
		xLoc = lastClicked.getScreenPosition(map).x;
		yLoc = lastClicked.getScreenPosition(map).y;
		impactDist = Float.parseFloat(((EarthQuakeMarker)lastClicked).getProperty("magnitude").toString()) * 15;
		noFill();
		ellipse(xLoc, yLoc, impactDist, impactDist);
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
			// Check if the country marker is a multimarker
			if(marker.getClass() == MultiMarker.class) {
				for(Marker subMultiMarker : ((MultiMarker) marker).getMarkers()) {
					// In the multimarker, check whether the location is inside
					if(((AbstractShapeMarker) subMultiMarker).isInsideByLocation(loc)) {
						// If is inside the country, then and the country name to the pointfeature
						feature.addProperty("country", marker.getProperty("name"));
						if(marker.getProperty("earthquakeCount") == null) {
							// If there is no earthquakeCount property in the country marker, set it to 1 since here we found one earthquake in the country
							marker.setProperty("earthquakeCount", (int) 1);
						} else {
							// If there is earthquakeCount property, then increase the number
							marker.setProperty("earthquakeCount", (int) marker.getProperty("earthquakeCount") + 1);
						}
						return true;
					}
				}
			} else {
				// If the country marker is not a multimarker, then check the location to see if it's inside the country
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
