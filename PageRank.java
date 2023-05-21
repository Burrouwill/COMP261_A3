import javafx.util.Pair;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Collection;

/**
 * Implements PageRank algorithm.
 * 
 * compueLinks(Graph graph) generates forward / backward cities for each city from the edge information acquired from graph that it as passed as arg.
 * 
 * printPageRankGraphData(Graph graph) print helper method.
 * 
 * computePageRank(Graph graph) Computes the PageRank values for each city in the graph using the PageRank algorithm. Runs n iterations set by iter field using the standard dampingFactor of 0.85.
 * 
 * @author (wgrbu)
 * @version (Final 22/05/23)
 */
public class PageRank{
    //Fields 
    private static double dampingFactor = 0.85;
    private static int iter = 10;

    /**
     * build the fromLinks and toLinks 
     */
    public static void computeLinks(Graph graph){
        // Add the to/from cities to each city
        for (Edge e : graph.getOriginalEdges()){
            e.fromCity().addToLinks(e.toCity());
            e.toCity().addFromLinks(e.fromCity());
        }
    }

    public static void printPageRankGraphData(Graph graph){
        System.out.println("\nPage Rank Graph");
        for (City city : graph.getCities().values()){
            System.out.print("\nCity: "+city.toString());
            //for each city display the in edges 
            System.out.print("\nIn links to cities:");
            for(City c:city.getFromLinks()){

                System.out.print("["+c.getId()+"] ");
            }
            System.out.print("\nOut links to cities:");
            //for each city display the out edges 
            for(City c: city.getToLinks()){
                System.out.print("["+c.getId()+"] ");
            }
            System.out.println();;
        }    
        System.out.println("=================");
    }

    public static void computePageRank(Graph graph) {
        // Get number of cities in the graph
        double noOfCities = graph.getCities().size();
        // Create a map to store the PageRank values for each city
        Map<City, Double> rankValues = new HashMap<>();
        // Initialize the PageRank values to 0.2 for each node
        graph.getCities().values().forEach(c -> rankValues.put(c, 0.2));
        // Set count to 1
        int count = 1;
        while (count <= iter) {
            // Create a new map to hold the updated PageRank values
            Map<City, Double> newRankValues = new HashMap<>();
            // Iterate over each city in the graph
            for (City n : graph.getCities().values()) {
                // Calculate the initial PageRank value for the current city
                double nRank = (1 - dampingFactor) / noOfCities;
                // Iterate over each city that has a link pointing to the current city (incoming links)
                for (City b : n.getFromLinks()) {
                    // Calculate the share of the neighbor's PageRank value
                    double neighborShare = rankValues.get(b) / b.getToLinks().size();
                    // Add the neighbor's contribution to the current city's PageRank value
                    nRank += dampingFactor * neighborShare;
                }
                // Update the PageRank value for the current city in the new map
                newRankValues.put(n, nRank);
            }
            // Update the rankValues map with the new PageRank values
            rankValues.putAll(newRankValues);
            // Increment the count for the iteration
            count++;
        }
        // Print the rankValues to console
        System.out.println(rankValues);
    }
}
