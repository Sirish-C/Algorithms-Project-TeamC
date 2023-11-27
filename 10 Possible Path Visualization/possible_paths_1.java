package pkg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class possible_paths_1 {

    private Map<String, List<String>> adjacencyList;
    private List<List<String>> distanceData;
    
    public static String constat_start;
    public static String constat_end;
    public static int radius;
    public Map<String, CityData> cityWeatherMap;
    public int maximum_dist;
    public int old_total_distance = 0;
    public int count = 0;
//    display_temperatures temperature;

    public possible_paths_1(List<List<String>> distanceData, String startcity, String endcity, int rad){
    	
    	String[] columns = null;

    	try (BufferedReader br = new BufferedReader(new FileReader("C:/Users/saini/eclipse-workspace/githublatest/pkg/new_DistanceMS2.csv"))) {
    	    String line;
    	    line = br.readLine();
    	    if (line != null) {
    	        columns = line.split(",");
    	        
    	        List<String> rowData = new ArrayList<>();

    	        // Add each column value to the rowData List
    	        for (String column : columns) {
    	            rowData.add(column);
    	        }
    	        
    	     // Add the rowData List to the beginning of the distanceData List
    	        distanceData.add(0, rowData);

    	    }
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    	
        adjacencyList = new HashMap<>();
        this.distanceData = distanceData;
//        this.cityWeatherMap = cityWeatherMap;        
        constat_start = startcity;
        constat_end = endcity;
        radius = rad;
//        this.temperature = temperature;
        buildAdjacencyList();
    }

	private void buildAdjacencyList() {
		
		maximum_dist = getDistance(constat_start, constat_end).getDistance() + radius;
		
		for (List<String> row : distanceData) {
		    String city1 = row.get(0);
		    adjacencyList.put(city1, new ArrayList<>());
		    
		    int distance1 = getDistance(city1, constat_end).getDistance();

		    if (distance1 < maximum_dist) {
		    	
		    	int temp = getDistance(constat_start, city1).getDistance();
		    	int sum = distance1 + temp;
		    	if (sum > maximum_dist) {
		    		continue;
		    	}
		    	
		        
		        for (int i = 1; i < row.size(); i++) {
		            String city2 = distanceData.get(0).get(i);
		            String distanceStr = row.get(i);

		            try {
		                int distance = Integer.parseInt(distanceStr);

		                if (distance > 0) {
		                    adjacencyList.get(city1).add(city2);
		                }
		            } catch (NumberFormatException e) {
		                // Ignore non-numeric entries
		            }
		        }
		    }
		}
    }


    public class DistanceResult {
        private int distance;
        private String nearestCity;

        public DistanceResult(int distance, String nearestCity) {
            this.distance = distance;
            this.nearestCity = nearestCity;
        }

        public int getDistance() {
            return distance;
        }

        public String getNearestCity() {
            return nearestCity;
        }
    }
    

    public DistanceResult getDistance(String startCity, String endCity) {
        // Use the distanceData list to find the distance between startCity and endCity
        for (List<String> row : distanceData) {
            String city1 = row.get(0);
            if (city1.equals(startCity)) {
                for (int i = 1; i < row.size(); i++) {
                    String city2 = distanceData.get(0).get(i);
                    if (city2.equals(endCity)) {
                        try {
                            int distance = Integer.parseInt(row.get(i));
                            return new DistanceResult(distance, ""); // Update the DistanceResult constructor accordingly
                        } catch (NumberFormatException e) {
                            // Handle the exception if distance is not a valid integer
                        }
                    }
                }
            }
        }

        // Return a placeholder DistanceResult in case the distance is not found
        return new DistanceResult(0, ""); // Update the DistanceResult constructor accordingly
    }


 // Change the return type of findAllPaths method
    public List<List<String>> findAllPaths(){
        List<List<String>> allPaths = new ArrayList<>();
        List<String> currentPath = new ArrayList<>();
        List<String> visitedCities = new ArrayList<>();
        List<String> unnecessarycities = new ArrayList<>();
        int[] pathCount = {0}; // Counter for path count
        
        findAllPathsDFS(constat_start, constat_end, constat_end, currentPath, allPaths, visitedCities, 0, pathCount, maximum_dist, unnecessarycities);
        System.out.println("Completed viewing all paths in given restrictions");
        // Return the list of paths
        return allPaths;
    }
    


    private void findAllPathsDFS(String currentCity, String endCity, String final_endCity, List<String> currentPath, List<List<String>> allPaths, List<String> visitedCities, int totalDistance, int[] pathCount, int maximum_dist, List<String> unnecessarycities) {
        currentPath.add(currentCity);
        visitedCities.add(currentCity);
        
        
        //System.out.println(visitedCities);
        if (currentCity.equals(endCity)) {
            // Found a path to the destination
            List<String> sortedPath = new ArrayList<>(currentPath);
            allPaths.add(sortedPath);

            // Print distance and path count for the discovered path
            if (sortedPath.size() > 9 && sortedPath.size() < 11) {
	        	System.out.println();
	        	System.out.println("Path " + count++ + ": " + sortedPath);
	        	System.out.println("Total Distance :" + totalDistance + " mi");
	        	pathCount[0]++;
            }
            
            
            for (int i = 0; i < sortedPath.size() - 1; i++) {
                String fromCity = sortedPath.get(i);
                String toCity = sortedPath.get(i + 1);
                DistanceResult result = getDistance(fromCity, toCity);
                int distance = result.getDistance();

          // UnComment below If you want to display City to City distance Information and Display Temperature Information 
                if (sortedPath.size() > 9 && sortedPath.size() < 11) {
                	System.out.println(fromCity + " -> " + toCity + " (" + distance + " mi) ");
                }
            	//temperature.fetch_display(fromCity, toCity, distance);
                //}
            }
            
        } else {
            List<String> neighbors = adjacencyList.get(currentCity);
           
            
            if (neighbors != null) {            	
                for (String neighbor : neighbors) {
                    if (!visitedCities.contains(neighbor)) {
                        DistanceResult result = getDistance(currentCity, neighbor);
                        int distance = result.getDistance();
                        if ((totalDistance + distance )< maximum_dist) {
                            findAllPathsDFS(neighbor, endCity, final_endCity  ,currentPath, allPaths, visitedCities, totalDistance + distance, pathCount, maximum_dist, unnecessarycities);
                        }
                    }
                }
            }
        }

        // Backtrack
        currentPath.remove(currentPath.size() - 1);
        visitedCities.remove(currentCity);
    }
}