package mapView;

import processing.core.PApplet;

import java.util.List;
import java.util.HashMap;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.providers.Google.*;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.marker.Marker;
//import de.fhpotsdam.unfolding.geo.Location;

/**
 * This class draw a map showing the different life expectancy among countries in the world with different color.
 * @author Yuming
 * Based on UCSD demos.
 */
public class LifeExpectancyMap extends PApplet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UnfoldingMap myMap;
	private GoogleMapProvider mapProvider;
	private HashMap<String, Float> lifeExptCountry;
	private List<Feature> countries;
	private List<Marker> countryMarkers;
	
	public void setup() {
		// The size of the User interface
		size(800, 600);
		this.background(150, 150, 150);
		// Initialized the map provider
		mapProvider = new GoogleMapProvider();
		// Initialize new map
		myMap = new UnfoldingMap(this, 50, 50, 800, 600, mapProvider);
		// Interact with map
		MapUtils.createDefaultEventDispatcher(this, myMap);
		// Define the location according to latitude and longitude
//		Location loc = new Location(39.948617f, -75.155929f);
		// Zoom the map to defined location
//		myMap.zoomAndPanTo(zoomLvl, loc);
		
		// Read the the life expectancy and countries
		lifeExptCountry = getLifeExpectancyAndCountry("LifeExpectancyWorldBankModule3.csv");
		// Read the geometric data of each country
		countries = GeoJSONReader.loadData(this, "countries.geo.json");
		// Create Markers using the geometric data
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		// Add marker to the map
		myMap.addMarkers(countryMarkers);
		
		// Add color to countries according the life expectancy
		addColorToCountry();
	}
	
	public void draw() {
		myMap.draw();
	}
	
	/**
	 * This function reads the file contains all the country information and corresponding life expectancy and 
	 * return with the country code and corresponding life expectancy
	 * @param fileNames which is a CVS file contains all the country information and corresponding life expectancy
	 * @return HashMap<String, Float> contains country code and life expectancy
	 */
	public HashMap<String, Float> getLifeExpectancyAndCountry(String fileNames) {
		HashMap<String, Float> lifeExptCountryList = new HashMap<String, Float>();
		
		// Read all rows in to the String Array
		String[] rows = loadStrings(fileNames);
		// Extract the information split by "," from each row
		for(String row : rows) {
			String[] cols = row.split(",");
			// Check whether the row is valid
			if((cols.length == 6)&&(!cols[5].equals(".."))) {
				// Put the country and the life expectancy into the HashMap
				lifeExptCountryList.put(cols[4], Float.parseFloat(cols[5]));
			}
		}
		return lifeExptCountryList;
	}
	
	/**
	 * This function add different color to the shape of the country according the life expectancy
	 */
	public void addColorToCountry() {
		 for(Marker marker : countryMarkers) {
			 String countryID = marker.getId();
			 // Find the key in the HashMap which has corresponding country key and life expectancy information
			 if(lifeExptCountry.containsKey(countryID)) {
				 float lifeExpValue = lifeExptCountry.get(countryID);
				 // Convert the float information into a integer within an integer range
				 int colorLvl = (int) map(lifeExpValue, 40, 90, 10, 255);
				 // Set the marker's color according to the colorLvl
				 marker.setColor(color(255 - colorLvl, colorLvl, 100));
			 } else {
				 // If the countryID not found, use default color
				 marker.setColor(color(100, 100, 100));
			 }
		 }
		 
	}
}
