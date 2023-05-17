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
    private static double dampingFactor = .85;
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
        graph.getCities().values().stream().forEach(c -> cities.put(c, noOfCities));

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

    public static void computePageRank(Graph graph){
        // Set count to 0
        int count = 1;
        Map<City, Double> newRankValues = new HashMap<>();

        while (count <= iter) {
            for (City c : cities.keySet()) {
                double cRank = 0;
                for (City n : c.getFromLinks()) {
                    double neighbourShare = cities.get(n) / n.getToLinks().size();
                    cRank += neighbourShare;
                }
                cRank = ((1 - dampingFactor) / noOfCities) + dampingFactor * cRank;

                newRankValues.put(c, cRank);
            }

            cities.putAll(newRankValues);
            newRankValues.clear();
            count++;
        }

        System.out.println(cities);
    }
}
