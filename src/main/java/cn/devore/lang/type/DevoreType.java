package cn.devore.lang.type;

import cn.devore.error.DevoreAssert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DevoreType {
    private static class Neighbour {
        int destination;
        int weight;

        Neighbour(int destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }
    private static final ArrayList<ArrayList<Neighbour>> adjacencyList = new ArrayList<>();
    private static final List<String> types = new ArrayList<>();
    private static int[][] distances;
    private static int vertices = 0;

    private static int[] dijkstra(int source) {
        boolean[] isVisited = new boolean[vertices];
        int[] distance = new int[vertices];
        Arrays.fill(distance, Integer.MAX_VALUE);
        distance[source] = 0;
        for (int vertex = 0; vertex < vertices; ++vertex) {
            int minDistanceVertex = findMinDistanceVertex(distance, isVisited);
            isVisited[minDistanceVertex] = true;
            for (Neighbour neighbour : adjacencyList.get(minDistanceVertex)) {
                int destination = neighbour.destination;
                int weight = neighbour.weight;
                if (!isVisited[destination] && valueAdd(distance[minDistanceVertex], weight) < distance[destination])
                    distance[destination] = valueAdd(distance[minDistanceVertex], weight);
            }
        }
        return distance;
    }

    private static int findMinDistanceVertex(int[] distance, boolean[] isVisited) {
        int minIndex = -1, minDistance = Integer.MAX_VALUE;
        for (int vertex = 0; vertex < vertices; ++vertex) {
            if (!isVisited[vertex] && distance[vertex] <= minDistance) {
                minDistance = distance[vertex];
                minIndex = vertex;
            }
        }
        return minIndex;
    }

    private static int[] bellmanford(int source) {
        int[] distance = new int[vertices];
        Arrays.fill(distance, Integer.MAX_VALUE);
        distance[source] = 0;
        for (int i = 0; i < vertices - 1; ++i) {
            for (int currentVertex = 0; currentVertex < vertices; ++currentVertex)
                for (Neighbour neighbour : adjacencyList.get(currentVertex))
                    if (distance[currentVertex] != Integer.MAX_VALUE
                            && valueAdd(distance[currentVertex], neighbour.weight) < distance[neighbour.destination])
                        distance[neighbour.destination] = valueAdd(distance[currentVertex], neighbour.weight);
        }
        for (int currentVertex = 0; currentVertex < vertices; ++currentVertex)
            for (Neighbour neighbour : adjacencyList.get(currentVertex))
                if (distance[currentVertex] != Integer.MAX_VALUE
                        && valueAdd(distance[currentVertex], neighbour.weight) < distance[neighbour.destination])
                    return null;
        return distance;
    }

    private static int[][] johnsons() {
        ++vertices;
        adjacencyList.add(new ArrayList<>());
        for (int i = 0; i < vertices - 1; ++i)
            adjacencyList.get(vertices - 1).add(new Neighbour(i, 0));
        int[] h = bellmanford(vertices - 1);
        DevoreAssert.typeAssert(h != null, "Bellmanford returns null.");
        for (int u = 0; u < vertices; ++u) {
            ArrayList<Neighbour> neighbours = adjacencyList.get(u);
            for (Neighbour neighbour : neighbours) {
                int v = neighbour.destination;
                int w = neighbour.weight;
                neighbour.weight = valueAdd(w, h[u] - h[v]);
            }
        }
        adjacencyList.remove(vertices - 1);
        --vertices;
        int[][] distances = new int[vertices][];
        for (int s = 0; s < vertices; ++s)
            distances[s] = dijkstra(s);
        for (int u = 0; u < vertices; ++u) {
            for (int v = 0; v < vertices; ++v) {
                if (distances[u][v] == Integer.MAX_VALUE)
                    continue;
                distances[u][v] = valueAdd(distances[u][v], h[v] - h[u]);
            }
        }
        return distances;
    }

    public static void addType(String name) {
        adjacencyList.add(new ArrayList<>());
        ++vertices;
        types.add(name);
    }

    public static void addType(String name, String child) {
        addType(name);
        addType(child);
        paternity(name, child);
    }

    public static void paternity(String father, String child) {
        DevoreAssert.typeAssert(types.contains(father), "Type " + father + " does not exist.");
        DevoreAssert.typeAssert(types.contains(child), "Type " + child + " does not exist.");
        adjacencyList.get(types.indexOf(child)).add(new Neighbour(types.indexOf(father), 1));
    }

    public static void apply() {
        distances = johnsons();
    }

    private static int valueAdd(int v1, int v2) {
        if (v1 == Integer.MAX_VALUE || v2 == Integer.MIN_VALUE)
            return Integer.MAX_VALUE;
        return v1 + v2;
    }

    public static int path(String start, String end) {
        DevoreAssert.typeAssert(types.contains(start), "Type " + start + " does not exist.");
        DevoreAssert.typeAssert(types.contains(end), "Type " + end + " does not exist.");
        return distances[types.indexOf(start)][types.indexOf(end)];
    }

    public static void init() {
        addType("any");
        addType("any", "comparable");
        addType("comparable", "arithmetic");
        addType("arithmetic", "num");
        addType("num", "real");
        addType("real", "int");
        addType("any", "list");
        addType("list", "immutable_list");
        addType("list", "variable_list");
        addType("any", "table");
        addType("list", "immutable_table");
        addType("list", "variable_table");
        addType("any", "string");
        addType("any", "id");
        addType("any", "bool");
        addType("any", "keyword");
        addType("any", "nil");
        addType("any", "undefined");
        addType("any", "function");
        apply();
    }
}
