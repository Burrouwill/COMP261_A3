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

    // Map of PageRank : City 
    private static Map<City,Double> cities = new HashMap<>();
    /**
     * build the fromLinks and toLinks 
     */
    public static void computeLinks(Graph graph){
        // Add all cities in the graph to map of City : Page rank key:value pairs
        graph.getCities().values().stream().forEach(c -> cities.put(c, 0.0));

        // Add the to/from edges to each city
        for (Edge e : graph.getOriginalEdges()){
            e.fromCity().addToLinks(e.toCity());
            e.toCity().addFromLinks(e.fromCity());
        }
        //printPageRankGraphData(graph);  ////may help in debugging
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
        // TODO WHAT DO I DO WITH THE RANKED PAGES? 
        // No field in city for storing page rank? Do I use a ma p or something similar in PageRank class? 
        // Do I print it from this method? Where does it go? 

        
        
        
        
    }
}
