package datastructures;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Comparator;

import other.Pair;

/** Directed, Weighted Graph */
class ArrayGraph<Vertex> {
  private Map<Vertex, Integer> indexToVertexMap;
  private List<Vertex> vertexToIndexList;
  // grid[i][j] != null means edge from i -> j
  // All weights are integers
  private List<List<Integer>> grid;
  private int numVertices;
  private int numEdges;
  private BiFunction<Vertex, Vertex, Integer> weightCalculator;

  public ArrayGraph() {
    indexToVertexMap = new HashMap<>();
    vertexToIndexList = new ArrayList<>();
    grid = new ArrayList<>();
    numVertices = 0;
    numEdges = 0;
  }

  public ArrayGraph(BiFunction<Vertex, Vertex, Integer> weightCalculator) {
    this();
    this.weightCalculator = weightCalculator;
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

  public boolean addEdge(Vertex v1, Vertex v2) {
    if (weightCalculator == null) {
      throw new UnsupportedOperationException(
          "This ArrayGraph has no weight calculator.");
    }
    return addEdge(v1, v2, weightCalculator.apply(v1, v2));
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

  /** 
   * Same as djikstra, but instead of a FIFO queue, it uses a 
   * priority queue to try the path with the least value of f(v) + g(v),
   * where f(v) is length of path so far, and g(v) is an admissible 
   * heuristic. A heuristic is an estimate of the cost of the path between
   * the some vertex and the goal vertex. For a heuristic to be admissible,
   * it must never overestimate the actual length of the path.
   */
  public List<Vertex> aStar(Vertex v1, Vertex v2, BiFunction<Vertex, Vertex, Integer> heuristic) {
    // Vertex -> (shortestPathLen, prevVertexOnShortestPath)
    Map<Vertex, Pair<Integer, Vertex>> shortestPathSoFar = new HashMap<>();
    return findPath(
      v1, 
      v2, 
      shortestPathSoFar, 
      new PriorityQueue<>(
          (u, v) -> 
              Integer.compare(
                  shortestPathSoFar.get(u).first + heuristic.apply(u, v2), 
                  shortestPathSoFar.get(v).first + heuristic.apply(v, v2))));
  }

  /** 
   * Same as djikstra, but instead of a FIFO queue, it uses a 
   * priority queue to try the path with the least weight so far.
   */
  public List<Vertex> uniformCostSearch(Vertex v1, Vertex v2) {
    // Vertex -> (shortestPathLen, prevVertexOnShortestPath)
    Map<Vertex, Pair<Integer, Vertex>> shortestPathSoFar = new HashMap<>();
    return findPath(
      v1, 
      v2, 
      shortestPathSoFar, 
      new PriorityQueue<>(
          Comparator.comparingInt(
              vertex -> shortestPathSoFar.get(vertex).first)));
  }

  public List<Vertex> djikstra(Vertex v1, Vertex v2) {
    // Vertex -> (shortestPathLen, prevVertexOnShortestPath)
    Map<Vertex, Pair<Integer, Vertex>> shortestPathSoFar = new HashMap<>();
    return findPath(v1, v2, shortestPathSoFar, new LinkedList<>());
  }

  private List<Vertex> findPath(
    Vertex v1, 
    Vertex v2, 
    Map<Vertex, Pair<Integer, Vertex>> shortestPathSoFar, 
    Queue<Vertex> q) {
    if (getIndex(v1) == null || getIndex(v2) == null) {
      return null;
    }

    boolean foundV2 = false;
    shortestPathSoFar.put(v1, new Pair<>(0, null));
    q.offer(v1);

    while (!q.isEmpty()) {
      System.out.println();
      System.out.println(q); // For debugging
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
    ArrayGraph<Character> g = new ArrayGraph<>((c1, c2) -> Math.abs(c1 - c2));
    g.addVertex('A');
    g.addVertex('C');
    g.addVertex('G');
    g.addVertex('M');
    g.addVertex('N');
    g.addVertex('T');
    g.addVertex('Z');

    // Weight is distance between letters (ex: C - A = 2)
    // See Comparator arg to constructor above
    g.addEdge('C', 'A');
    g.addEdge('A', 'M');
    g.addEdge('M', 'C');
    g.addEdge('C', 'T');
    g.addEdge('C', 'N');
    g.addEdge('T', 'G');
    g.addEdge('T', 'M');
    g.addEdge('M', 'T');
    g.addEdge('N', 'Z');
    g.addEdge('G', 'Z');
    
    System.out.println("DJIKSTRA");
    System.out.println(g.djikstra('A', 'Z'));
    System.out.println();

    System.out.println("UNIFORM COST SEARCH");
    System.out.println(g.uniformCostSearch('A', 'Z'));
    System.out.println();

    // The heuristic is the distance between letters
    // Distance between any vertex and the goal vertex will always be 
    // <= actual length of shortest path between them, so the heuristic 
    // is admissible.
    System.out.println("A STAR SEARCH");
    System.out.println(g.aStar('A', 'Z', (c1, c2) -> Math.abs(c1 - c2)));
    System.out.println();
  }
}