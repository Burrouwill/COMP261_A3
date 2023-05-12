
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
    private static final int INF = Integer.MAX_VALUE;

    //data structure to maintain a list of forward and reverse edges - forward edges stored at even indices and reverse edges stored at odd indices
    private static Map<String,Edge> edges = new HashMap<>(); 

    // Augmentation path and the corresponding flow
    //private static ArrayList<Pair<ArrayList<String>, Integer>> augmentationPathsAll = new ArrayList<Pair<ArrayList<String>, Integer>> ();

    public static void computeResidualGraph(Graph graph){
        // Get original edges from the graph
        Collection<Edge> forwardEdges = graph.getOriginalEdges();
        // Add forward Edges to the map
        int edgeCount = 0;
        for (Edge e : forwardEdges){
            // Set the flow to 0
            e.setFlow(0); 
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

    /**
     * Udates the flow 
     */
    
    public static void updateFlow(ArrayList<String> augPath, int pathFlow) {
        for (String edgeId : augPath) {
            Edge e = edges.get(edgeId);
            e.setFlow(e.flow() + pathFlow);
        }
    }
    
    /**
     * Calculates the bottleneck of an augmentation path
     */
    public static int bottleneck(ArrayList<String> augPath){
        int lowestCap = Integer.MAX_VALUE;
        for (String edgeId : augPath){
            Edge e = edges.get(edgeId);
            int edgeCap = e.capacity(); // Need to check if this is correct, should it be remaining capacity? or a different metric
            if (edgeCap < lowestCap){
                lowestCap = edgeCap;
            }
        }
        return lowestCap;
    }

    /** 
     * Find maximum flow
     */
    // TODO: Find augmentation paths and their corresponding flows

    public static ArrayList<Pair<ArrayList<String>, Integer>> calcMaxflows(Graph graph, City from, City to) {
        ArrayList<Pair<ArrayList<String>, Integer>> augmentationPathsAll = new ArrayList<>();
        int maxFlow = 0;

        while (true) {
            Pair<ArrayList<String>, Integer> pair = bfs(graph, from, to);
            if (pair == null || pair.getKey().isEmpty()) {
                break; // No more augmentation paths found, exit loop
            }

            ArrayList<String> augPath = pair.getKey();
            int pathFlow = pair.getValue();

            maxFlow += pathFlow;
            augmentationPathsAll.add(new Pair<>(new ArrayList<>(augPath), maxFlow));

            updateFlow(augPath, pathFlow);
        }

        return augmentationPathsAll;
    }

    
    
    /** 
     * ***** NAME HERE ****
     */
    // TODO:Use BFS to find a path from s to t along with the correponding bottleneck flow

    public static Pair<ArrayList<String>, Integer> bfs(Graph graph, City s, City t) {
        HashMap<String, String> backPointer = new HashMap<>();
        Queue<City> queue = new ArrayDeque<>();
        queue.offer(s);

        while (!queue.isEmpty()) {
            City current = queue.poll();
            for (String edgeId : current.getEdgeIds()) {
                Edge e = edges.get(edgeId);
                if (e.toCity() != s && backPointer.get(e.toCity().getId()) == null && e.capacity() > 0) {
                    backPointer.put(e.toCity().getId(), edgeId);
                    if (e.toCity() == t) {
                        ArrayList<String> augmentationPath = new ArrayList<>();
                        int bottleneck = Integer.MAX_VALUE;
                        String cityId = t.getId();

                        while (backPointer.containsKey(cityId)) {
                            String edge = backPointer.get(cityId);
                            augmentationPath.add(edge);
                            bottleneck = Math.min(bottleneck, edges.get(edge).capacity() - edges.get(edge).flow());
                            cityId = edges.get(edge).fromCity().getId();
                        }

                        Collections.reverse(augmentationPath);
                        return new Pair<>(augmentationPath, bottleneck);
                    }
                    queue.offer(e.toCity());
                }
            }
        }

        return null;
    }

}

