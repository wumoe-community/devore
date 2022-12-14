package cn.devore.lang;

import cn.devore.exception.DevoreAssert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DevoreType {
    private static final List<List<Neighbour>> adjacencyList = new ArrayList<>();
    private static final List<String> _types = new ArrayList<>();
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
        DevoreAssert.runtimeAssert(h != null, "Bellmanford returns null.");
        for (int u = 0; u < vertices; ++u) {
            List<Neighbour> neighbours = adjacencyList.get(u);
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
        _types.add(name);
    }

    public static void addType(String name, String child) {
        if (!_types.contains(name))
            addType(name);
        if (!_types.contains(child))
            addType(child);
        paternity(name, child);
    }

    public static void paternity(String father, String child) {
        DevoreAssert.typeAssert(_types.contains(father), "Type " + father + " does not exist.");
        DevoreAssert.typeAssert(_types.contains(child), "Type " + child + " does not exist.");
        adjacencyList.get(_types.indexOf(child)).add(new Neighbour(_types.indexOf(father), 1));
    }

    public static void apply() {
        distances = johnsons();
    }

    private static int valueAdd(int v1, int v2) {
        if (v1 == Integer.MAX_VALUE || v2 == Integer.MIN_VALUE)
            return Integer.MAX_VALUE;
        return v1 + v2;
    }

    private static int path(String start, String end) {
        DevoreAssert.typeAssert(_types.contains(start), "Type " + start + " does not exist.");
        DevoreAssert.typeAssert(_types.contains(end), "Type " + end + " does not exist.");
        return distances[_types.indexOf(start)][_types.indexOf(end)];
    }

    public static int check(String des, String anc) {
        String[] types = anc.split("\\|");
        int path = Integer.MAX_VALUE;
        for (String type : types)
            path = Math.min(path, path(des, type));
        return path;
    }

    public static void init() {
        addType("any");
        addType("any", "comparable");
        addType("comparable", "arithmetic");
        addType("arithmetic", "num");
        addType("num", "complex");
        addType("num", "real");
        addType("real", "int");
        addType("any", "list");
        addType("list", "immutable_list");
        addType("list", "variable_list");
        addType("any", "table");
        addType("table", "immutable_table");
        addType("table", "variable_table");
        addType("any", "string");
        addType("any", "id");
        addType("any", "bool");
        addType("any", "keyword");
        addType("any", "nil");
        addType("any", "undefined");
        addType("any", "function");
        addType("any", "structure");
        apply();
    }

    private static class Neighbour {
        int destination;
        int weight;

        Neighbour(int destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }
}
