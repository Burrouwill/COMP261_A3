
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

/** 
 * Implementation of Edmonds-Karp algorithm to find augmentation paths and network flow. 
 */

public class EdmondKarp {
    //data structure to maintain a list of forward and reverse edges - forward edges stored at even indices and reverse edges stored at odd indices
    private static Map<String,Edge> edges = new HashMap<>(); 

    public static void computeResidualGraph(Graph graph){
        // Get original edges from the graph
        Collection<Edge> forwardEdges = graph.getOriginalEdges();
        // Add forward Edges to the map
        int edgeCount = 0;
        for (Edge e : forwardEdges){
            // Set the flow to 0
            e.setFlow(0); 
            // Put edge in edges map
            edges.put(edgeCount+"",e);
            // Add edge to edgeIds collections in both the from & to city
            e.fromCity().addEdgeId(edgeCount+"");
            e.toCity().addEdgeId(edgeCount+"");
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
            // Add edge to edgeIds collections in both the from & to city
            e.fromCity().addEdgeId(edgeCount+"");
            e.toCity().addEdgeId(edgeCount+"");
            // Increment Edge ID
            edgeCount+=2;
        }
        //printResidualGraphData(graph);  //may help in debugging
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
     * Udates the flow to reflect bottleneck value.
     */

    public static void updateFlow(ArrayList<String> augPath, int bottleneck) {
        for (String edgeId : augPath) {
            Edge e = edges.get(edgeId);
            e.setFlow(e.flow() + bottleneck);
        }
    }

    /**
     * Calculates the bottleneck of an augmentation path
     */
    public static int bottleneck(ArrayList<String> augPath) {
        int lowestCap = Integer.MAX_VALUE;
        for (String edgeId : augPath) {
            Edge e = edges.get(edgeId);
            // Calculate remaining capacity
            int remainingCap = e.capacity() - e.flow();
            if (remainingCap < lowestCap) {
                lowestCap = remainingCap;
            }
        }
        return lowestCap;
    }

    /** 
     * Finds maximum flow through a graph from start city to sink city. 
     */
    public static ArrayList<Pair<ArrayList<String>, Integer>> calcMaxflows(Graph graph, City from, City to) {
        // Data structure to hold all of the Augmentation Paths
        ArrayList<Pair<ArrayList<String>, Integer>> augmentationPathsAll = new ArrayList<>();
        // Create RG
        computeResidualGraph(graph);
        // While there are more augmentation paths:
        while (true) {
            // Find an augmentation path using the bfs
            Pair<ArrayList<String>, Integer> pair = bfs(graph, from, to);
            // If the path lsit == null --> No more paths left, exit the loop 
            if (pair == null || pair.getKey().isEmpty()) {
                break; // No more augmentation paths found, exit loop
            }
            // If path was not null --> Add it to the list of augmentation paths
            augmentationPathsAll.add(pair);
            // Update the flows of the edges along the path 
            updateFlow(pair.getKey(), pair.getValue());
        }
        return augmentationPathsAll;
    }

    /** 
     * Runs a BFS on the graph from start city (s) --> sink city (t). 
     * Returns an augmentation path along with the bottleneck value for the path.
     */
    public static Pair<ArrayList<String>, Integer> bfs(Graph graph, City s, City t) {
        // HashMap to store the back pointers from each city to the edge that leads to it
        HashMap<String, String> backPointer = new HashMap<>();
        // Queue to perform BFS traversal of the graph
        Queue<City> queue = new ArrayDeque<>();
        // Enqueue the start city
        queue.offer(s);
        while (!queue.isEmpty()) {
            // Dequeue the current city
            City current = queue.poll();
            // Iterate over the outgoing edges from the current city
            for (String edgeId : current.getEdgeIds()) {
                // Get the outgoing edge
                Edge e = edges.get(edgeId);
                // Check if the destination city is not the start city, the edge is available for flow, and the destination city has not been visited yet
                if (!(e.toCity().equals(s)) && backPointer.get(e.toCity().getId()) == null && e.capacity() - e.flow() > 0) {
                    // Store the back pointer from the destination city to the current edge
                    backPointer.put(e.toCity().getId(), edgeId);
                    // If the destination city is the sink city (t), construct the augmentation path
                    if (e.toCity().equals(t)) {
                        ArrayList<String> augmentationPath = new ArrayList<>();
                        // Traverse the back pointers to construct the augmentation path
                        String cityId = t.getId();
                        while (backPointer.containsKey(cityId)) {
                            String edge = backPointer.get(cityId);
                            augmentationPath.add(edge);
                            cityId = edges.get(edge).fromCity().getId();
                        }
                        Collections.reverse(augmentationPath);
                        // Calculate the bottleneck value for the augmentation path
                        int bottleneck = bottleneck(augmentationPath);
                        // Return the augmentation path and the bottleneck value
                        return new Pair<>(augmentationPath, bottleneck);
                    }
                    // Add the the destination city for further exploration
                    queue.offer(e.toCity());
                }
            }
        }
        // If no augmentation path is found, return null
        return null;
    }
}

