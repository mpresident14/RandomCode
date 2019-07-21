package other;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;

/** Directed, Weighted Graph */
class ArrayGraph<Vertex> {
  private Map<Vertex, Integer> indexToVertexMap;
  private List<Vertex> vertexToIndexList;
  // grid[i][j] != null means edge from i -> j
  // All weights are integers
  private List<List<Integer>> grid;
  private int numVertices;
  private int numEdges;

  public ArrayGraph() {
    indexToVertexMap = new HashMap<>();
    vertexToIndexList = new ArrayList<>();
    grid = new ArrayList<>();
    numVertices = 0;
    numEdges = 0;
  }

  public void addVertex(Vertex v) {
    indexToVertexMap.put(v, numVertices);
    vertexToIndexList.add(v);
    grid.add(new ArrayList<>(numVertices + 1));
    List<Integer> addedRow = grid.get(numVertices);
    for (int i = 0; i < numVertices; i++) {
      addedRow.add(null);
    }
    for (List<Integer> vertexRow : grid) {
      vertexRow.add(null);
    }
    
    numVertices++;
  }

  public boolean addEdge(Vertex v1, Vertex v2, int weight) {
    Integer v1Index = indexToVertexMap.get(v1);
    Integer v2Index = indexToVertexMap.get(v2);
    if (v1Index == null || v2Index == null) {
      return false;
    }

    grid.get(v1Index).set(v2Index, weight);
    numEdges++;
    return true;
  }

  // Returns null if no edge
  public Integer getWeight(Vertex v1, Vertex v2) {
    Integer v1Index = indexToVertexMap.get(v1);
    Integer v2Index = indexToVertexMap.get(v2);
    if (v1Index == null || v2Index == null) {
      return null;
    }

    return grid.get(v1Index).get(v2Index);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (List<Integer> row : grid) {
      for (Integer weight : row) {
        sb.append(String.format("%-7s", weight));
      }
      sb.append('\n');
      sb.append('\n');
    }
    return sb.toString();
  }

  public List<Vertex> djikstra(Vertex v1, Vertex v2) {
    if (getIndex(v1) == null || getIndex(v2) == null) {
      return null;
    }

    boolean foundV2 = false;
    // Vertex -> (shortestPathLen, prevVertexOnShortestPath)
    Map<Vertex, Pair<Integer, Vertex>> shortestPathSoFar = new HashMap<>();
    Queue<Vertex> q = new LinkedList<>();
    shortestPathSoFar.put(v1, new Pair<>(0, null));
    q.offer(v1);

    while (!q.isEmpty()) {
      System.out.println(shortestPathSoFar); // For debugging
      Vertex polled = q.poll();
      int polledPathLength = shortestPathSoFar.get(polled).first;
      List<Integer> row = grid.get(getIndex(polled));
      for (int i = 0; i < numVertices; i++) {
        Integer weight = row.get(i);
        // Add all adj vertices and update shortest path length and previous vertex if nec.
        if (weight == null) {
          continue;
        }  

        int newCurrentPathLength = polledPathLength + weight;
        // If we found our target and this path being searched is longer than
        // that path, then we can stop looking down this path
        if (foundV2 && shortestPathSoFar.get(v2).first <= newCurrentPathLength) {
          continue;
        }
        
        Vertex current = getVertex(i);
        Pair<Integer, Vertex> currentPathPair = shortestPathSoFar.get(current);
        // Haven't visited this vertex yet
        if (currentPathPair == null) {
          shortestPathSoFar.put(current, new Pair<>(newCurrentPathLength, polled));
          // No need to follow this path anymore if we already found v2
          if (current.equals(v2)) {
            foundV2 = true;
          } else {
            q.offer(current);
          }
        } else if (newCurrentPathLength < currentPathPair.first) {
            shortestPathSoFar.put(current, new Pair<>(newCurrentPathLength, polled));
        }
      }
    }  

    LinkedList<Vertex> shortestPath = new LinkedList<>();
    shortestPath.addFirst(v2);
    Pair<Integer, Vertex> nextToAdd = shortestPathSoFar.get(v2);
    while (nextToAdd.second != null) {
      shortestPath.addFirst(nextToAdd.second);
      nextToAdd = shortestPathSoFar.get(nextToAdd.second);
    }

    return shortestPath;
  }

  private Integer getIndex(Vertex v) {
    return indexToVertexMap.get(v);
  }

  private Vertex getVertex(int i) {
    return vertexToIndexList.get(i);
  }

  public static void main(String[] args) {
    ArrayGraph<String> g = new ArrayGraph<>();
    g.addVertex("A");
    g.addVertex("B");
    g.addVertex("C");
    g.addVertex("D");
    g.addVertex("E");
    g.addVertex("F");

    g.addEdge("A", "B", 7);
    g.addEdge("A", "C", 5);
    g.addEdge("B", "D", 6);
    g.addEdge("C", "E", 3);
    g.addEdge("C", "F", 10);
    g.addEdge("D", "F", 3);
    
    System.out.println(g.djikstra("A", "D"));
  }
}