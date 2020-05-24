package algorithms;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

public class Hungarian {

  private static class AltTree {
    private class Node {
      int vertex;
      boolean isXVertex;
      Set<Node> children;
      Node parent;

      Node(int vertex, boolean isXVertex, Node parent) {
        this.vertex = vertex;
        this.isXVertex = isXVertex;
        this.children = new HashSet<>();
        this.parent = parent;
      }

      @Override
      public boolean equals(Object obj) {
        if (!(obj instanceof Node)) {
          return false;
        }

        Node node = (Node) obj;
        return vertex == node.vertex && isXVertex == node.isXVertex;
      }

      @Override
      public int hashCode() {
        return vertex;
      }
    }

    Node root;

    AltTree(int vertex) {
      this.root = new Node(vertex, true, null);
    }

    /* Adds y as a child of prevX and returns the augmenting path from the root (i.e., u) to y */
    List<Integer> getPath(int prevX, int y) {
      Queue<Node> q = new LinkedList<>();
      q.add(root);
      while (!q.isEmpty()) {
        Node node = q.poll();
        // Found prevX, backtrack through the tree to find the path
        if (node.isXVertex && node.vertex == prevX) {
          List<Integer> augPath = new LinkedList<>();
          augPath.add(0, y);
          Node currentNode = node;
          while (currentNode != null) {
            augPath.add(0, currentNode.vertex);
            currentNode = currentNode.parent;
          }
          return augPath;
        }

        // Continue searching
        node.children.forEach(q::add);
      }

      // Won't get here
      throw new NoSuchElementException(prevX + " was not found");
    }

    /* Adds y as a child of prevX and x as a child of y */
    void addToTree(int prevX, int y, int x) {
      // This is a tree, so no visited set required
      Queue<Node> q = new LinkedList<>();
      q.add(root);
      while (!q.isEmpty()) {
        Node node = q.poll();
        // Found prevX
        if (node.isXVertex && node.vertex == prevX) {
          Node yNode = new Node(y, false, node);
          yNode.children.add(new Node(x, true, yNode));
          node.children.add(yNode);
          return;
        }

        // Continue searching
        node.children.forEach(q::add);
      }
    }
  }

  private static int FREE = -1;

  /* To get the minimum cost, negate the weights and find the maximum */
  public static int[] hungarianMin(int[][] weights) {
    int[][] negWeights = new int[weights.length][];
    for (int i = 0; i < negWeights.length; ++i) {
      negWeights[i] = Arrays.stream(weights[i]).map(w -> -w).toArray();
    }
    return hungarianMax(negWeights);
  }

  public static int[] hungarianMax(int[][] weights) {
    int numJobs = weights.length;

    // Initialize empty matching
    int numMatches = 0;
    int[] xMatching = new int[numJobs];
    int[] yMatching = new int[numJobs];
    Arrays.fill(xMatching, FREE);
    Arrays.fill(yMatching, FREE);

    // Initialize trivial labelling
    int[] xLabels = new int[numJobs];
    int[] yLabels = new int[numJobs];
    for (int xi = 0; xi < numJobs; ++xi) {
      xLabels[xi] = Arrays.stream(weights[xi]).max().getAsInt();
    }

    // Iterate until we find a perfect matching
    while (numMatches != numJobs) {
      Set<Integer> s = new HashSet<>();
      // S = {u}
      int u =
          IntStream.range(0, numJobs).filter(xi -> xMatching[xi] == FREE).findFirst().getAsInt();
      s.add(u);
      Set<Integer> t = new HashSet<>();

      int uLabel = xLabels[u];
      int[] uWeights = weights[u];

      // Initialize alphas for each y in Y
      int[] alphas = new int[numJobs];
      Arrays.setAll(alphas, yi -> uLabel + yLabels[yi] - uWeights[yi]);

      // Calculate initial N_L(S)
      BitSet neighborhoodS = new BitSet(numJobs);
      for (int yi = 0; yi < numJobs; ++yi) {
        if (uLabel + yLabels[yi] == uWeights[yi]) {
          neighborhoodS.set(yi);
        }
      }

      // Initialize alternating tree
      AltTree altTree = new AltTree(u);

      while (true) {
        // N_L(S) == T
        if (t.size() == neighborhoodS.cardinality()
            && t.stream().allMatch(vertex -> neighborhoodS.get(vertex))) {
          int minAlpha =
              IntStream.range(0, numJobs)
                  .filter(yi -> !t.contains(yi))
                  .map(yi -> alphas[yi])
                  .min()
                  .getAsInt();

          s.forEach(x -> xLabels[x] -= minAlpha);
          t.forEach(y -> yLabels[y] += minAlpha);

          // Add all edges with minAlpha to the neighborhood of S
          // Since every x in S decreased by minAlpha, we need to update alphas
          for (int yi = 0; yi < numJobs; ++yi) {
            if (alphas[yi] == minAlpha) {
              neighborhoodS.set(yi);
            }
            alphas[yi] -= minAlpha;
          }
        }

        // N_L(S) != T, find y in N_L(S)\T
        int y =
            IntStream.range(0, numJobs)
                .filter(yi -> neighborhoodS.get(yi) && !t.contains(yi))
                .findFirst()
                .getAsInt();

        // Find an x in S that was adjacent to y
        int yLabel = yLabels[y];
        int prevX = s.stream().filter(x -> xLabels[x] + yLabel == weights[x][y]).findFirst().get();

        int x = yMatching[y];
        if (x == FREE) {
          List<Integer> augPath = altTree.getPath(prevX, y);
          int numEdges = augPath.size() - 1;
          // Unmatch matched edges
          for (int i = 1; i < numEdges; i += 2) {
            // Odd edges are matched (start with y in Y), so unmatch them
            int yi = augPath.get(i);
            int xi = augPath.get(i + 1);
            yMatching[yi] = FREE;
            xMatching[xi] = FREE;
          }
          for (int i = 0; i < numEdges; i += 2) {
            // Even edges are unmatched (start with x in X), so match them
            int xi = augPath.get(i);
            int yi = augPath.get(i + 1);
            xMatching[xi] = yi;
            yMatching[yi] = xi;
          }
          ++numMatches;
          break;
        } else {
          // y is matched
          s.add(x);
          t.add(y);
          // Update N_L(S)
          int xLabel = xLabels[x];
          int[] xWeights = weights[x];
          for (int yi = 0; yi < numJobs; ++yi) {
            if (xLabel + yLabels[yi] - xWeights[yi] == 0) {
              neighborhoodS.set(yi);
            }
          }

          // Update alphas
          for (int yi = 0; yi < numJobs; ++yi) {
            alphas[yi] = Math.min(alphas[yi], xLabel + yLabels[yi] - xWeights[yi]);
          }

          altTree.addToTree(prevX, y, x);
        }
      }
    }

    return xMatching;
  }

  public static void main(String[] args) {
    Random random = new Random();
    int size = 1000;
    int[][] weights = new int[size][size];
    for (int j = 0; j < weights.length; ++j) {
      weights[j] = new int[size];
      Arrays.setAll(weights[j], n -> random.nextInt());
    }

    long startTime = System.nanoTime();
    Hungarian.hungarianMax(weights);
    long endTime = System.nanoTime();

    double durationMs = (endTime - startTime) / 1000000.0;
    System.out.println(durationMs);

    Hungarian.hungarianMax(weights);
  }
}
