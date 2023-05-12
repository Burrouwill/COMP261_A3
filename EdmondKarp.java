
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;
import javafx.util.Pair;
import java.util.List;
import java.util.stream.*;

/** Edmond karp algorithm to find augmentation paths and network flow.
 * 
 * This would include building the supporting data structures:
 * 
 * a) Building the residual graph(that includes original and backward (reverse) edges.)
 *     - maintain a map of Edges where for every edge in the original graph we add a reverse edge in the residual graph.
 *     - The map of edges are set to include original edges at even indices and reverse edges at odd indices (this helps accessing the corresponding backward edge easily)
 *     
 *     
 * b) Using this residual graph, for each city maintain a list of edges out of the city (this helps accessing the neighbours of a node (both original and reverse))

 * The class finds : augmentation paths, their corresponing flows and the total flow
 * 
 * 
 */

public class EdmondKarp {
    // class members

    //data structure to maintain a list of forward and reverse edges - forward edges stored at even indices and reverse edges stored at odd indices
    private static Map<String,Edge> edges = new HashMap<>(); 

    // Augmentation path and the corresponding flow
    private static ArrayList<Pair<ArrayList<String>, Integer>> augmentationPaths =null;

    public static void computeResidualGraph(Graph graph){
        // Get original edges from the graph
        Collection<Edge> forwardEdges = graph.getOriginalEdges();
        // Add forward Edges to the map
        int edgeCount = 0;
        for (Edge e : forwardEdges){
            edges.put(edgeCount+"",e);
            // Increment count for even numbers 
            edgeCount += 2;
        }
        // Set edgeCount for odd  numbers
        edgeCount = 1;
        // For each edge --> Make a reverse edge swapping toCity & fromCity
        for (Edge e : forwardEdges){
            City fromCity = e.fromCity();
            City toCity = e.toCity();
            // New Edge
            Edge reverseEdge =  new Edge(toCity,fromCity,e.transpType(),0,0);
            edges.put(edgeCount+"", reverseEdge);
            // Increment Edge ID
            edgeCount+=2;
        }
        printResidualGraphData(graph);  //may help in debugging
    }

    // Method to print Residual Graph 
    public static void printResidualGraphData(Graph graph){
        System.out.println("\nResidual Graph");
        System.out.println("\n=============================\nCities:");
        for (City city : graph.getCities().values()){
            System.out.print(city.toString());
            // for each city display the out edges 
            for(String eId: city.getEdgeIds()){
                System.out.print("["+eId+"] ");
            }
            System.out.println();
        }
        System.out.println("\n=============================\nEdges(Original(with even Id) and Reverse(with odd Id):");
        edges.forEach((eId, edge)->
                System.out.println("["+eId+"] " +edge.toString()));

        System.out.println("===============");
    }

    //=============================================================================
    //  Methods to access data from the graph. 
    //=============================================================================
    /**
     * Return the corresonding edge for a given key
     */

    public static Edge getEdge(String id){
        return edges.get(id);
    }

    /**
     * Returns the ID of a given edge
     */
    public static String getEdgeId(Edge goal) {
        return edges.entrySet().stream()
        .filter(e -> e.getValue() == goal)
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse(null);
    }

    /** find maximum flow
     * 
     */
    // TODO: Find augmentation paths and their corresponding flows
    public static ArrayList<Pair<ArrayList<String>, Integer>> calcMaxflows(Graph graph, City from, City to) {

        // END TODO
        return augmentationPaths;
    }

    // TODO:Use BFS to find a path from s to t along with the correponding bottleneck flow
    public static Pair<ArrayList<String>, Integer>  bfs(Graph graph, City s, City t) {

        ArrayList<String> augmentationPath = new ArrayList<>();
        HashMap<String,String> backPointer = new HashMap<>(); // DOi need to add null entries? CityID -> Edge ID? 
        Queue<City> queue = new ArrayDeque<>();
        // Offer queue start city
        queue.offer(s);

        //While queue !empty
        while (!queue.isEmpty()){
            //Get current city from queue
            City current = queue.poll();
            // For each outward Edge of Current, Get the ID of the outward edge
            for (String ID : current.getEdgeIds()){ 
                // Get the edge aassociated with the ID from map of edges 
                Edge e = edges.get(ID);
                // If the next city from the edge is not the start & Is not already in the backpointers & capacity != 0
                if (e.toCity() != s && backPointer.get(e.toCity()) == null && e.capacity() != 0){
                    backPointer.put(e.toCity().getId(),getEdgeId(e));
                    // If the sink city has an edge thats not null -> We found a path, need to build it
                    if (backPointer.get(t) != null){
                        // Set first edge to edge associateed with t
                        Edge pathEdge = edges.get(backPointer.get(t.getId()));
                        // While there is still edges on the path, add them to the aug path list
                        while (pathEdge != null){
                            // Add the edge id to aug path
                            augmentationPath.add(getEdgeId(pathEdge));
                            // Set path edge to next edge
                            pathEdge = edges.get(backPointer.get(pathEdge.fromCity().getId()));
                        }
                        // Reverse the 
                        Collections.reverse(augmentationPaths);
                        return augmentationPaths;
                    }
                    queue.offer(e.toCity());
                }
            }

        }

        // END TODO
        return new Pair(null,0);
    }

}

