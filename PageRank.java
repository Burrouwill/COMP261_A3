import javafx.util.Pair;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Collection;

/**
 * Write a description of class PageRank here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class PageRank{
    //class members 
    private static double dampingFactor = 0.85;
    private static int iter = 10;
    private static double noOfCities;

    // Map of PageRank : City 
    private static Map<City,Double> cities = new HashMap<>();
    /**
     * build the fromLinks and toLinks 
     */
    public static void computeLinks(Graph graph){
        // Calculate intitial PageRank value
        noOfCities = graph.getCities().size();

        // Add all cities in the graph to map of City : Page rank key:value pairs
        graph.getCities().values().stream().forEach(c -> cities.put(c, 1.0 / noOfCities));

        // Add the to/from edges to each city
        for (Edge e : graph.getOriginalEdges()){
            e.fromCity().addToLinks(e.toCity());
            e.toCity().addFromLinks(e.fromCity());
        }
        printPageRankGraphData(graph);  ////may help in debugging
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
    Map<City, Double> rankValues = new HashMap<>();

    // Initialize the PageRank values to 0.2 for each node
    graph.getCities().values().forEach(c -> rankValues.put(c, 0.2));

    int count = 1;
    while (count <= iter) {
        Map<City, Double> newRankValues = new HashMap<>();

        for (City n : graph.getCities().values()) {
            double nRank = (1 - dampingFactor) / noOfCities;

            for (City b : n.getFromLinks()) {
                double neighborShare = rankValues.get(b) / b.getToLinks().size();
                nRank += dampingFactor * neighborShare;
            }

            newRankValues.put(n, nRank);
        }

        rankValues.putAll(newRankValues);
        count++;
    }

    
    for (double d : rankValues.values()){
        System.out.println(d*5);
        // Off by factor of 5, why is this the case? 
    }
    //System.out.println(rankValues);
    // O
}


}
