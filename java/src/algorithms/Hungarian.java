package algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Stack;
import java.util.stream.IntStream;

import other.Pair;

public class Hungarian {

  private static int FREE = -1;

  public static int[] hungarian(int[][] weights) {
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
      int u = -1;
      for (int xi = 0; xi < numJobs; ++xi) {
        if (xMatching[xi] == FREE) {
          u = xi;
          s.add(xi);
          break;
        }
      }
      // T = {}
      Set<Integer> t = new HashSet<>();

      // Initialize alphas for each y in Y
      int[] alphas = new int[numJobs];
      for (int yi = 0; yi < numJobs; ++yi) {
        alphas[yi] = xLabels[u] + yLabels[yi] - weights[u][yi];
      }

      // Calculate initial N_L(S)
      BitSet neighborhoodS = new BitSet(numJobs);
      int uLabel = xLabels[u];
      int[] uWeights = weights[u];
      for (int yi = 0; yi < numJobs; ++yi) {
        if (uLabel + yLabels[yi] == uWeights[yi]) {
          neighborhoodS.set(yi);
        }
      }

      // Initialize augmenting path
      List<Integer> augPath = new ArrayList<>();
      augPath.add(u);

      while (true) {
        // N_L(S) == T
        if (t.size() == neighborhoodS.cardinality()
          && t.stream().allMatch(vertex -> neighborhoodS.get(vertex))) {
          int minAlpha =
              IntStream
                  .range(0, numJobs)
                  .filter(yi -> !t.contains(yi))
                  .map(yi -> alphas[yi])
                  .min()
                  .getAsInt();

          for (int x : s) {
            xLabels[x] -= minAlpha;
          }
          for (int y : t) {
            yLabels[y] += minAlpha;
          }

          // Add all edges with minAlpha to the neighborhood of S
          for (int yi = 0; yi < numJobs; ++yi) {
            if (alphas[yi] == minAlpha) {
              neighborhoodS.set(yi);
            }
          }

          // Since every x in S decreased by minAlpha, we need to update alphas
          for (int i = 0; i < numJobs; ++i) {
            alphas[i] -= minAlpha;
          }
        }

        // N_L(S) != T, find y in N_L(S)\T
        int y = -1;
        for (int yi = 0; yi < numJobs; ++yi) {
          if (neighborhoodS.get(yi) && !t.contains(yi)) {
            y = yi;
            break;
          }
        }

        // Find the x that gave minAlpha (TODO: should store this)
        int prevX = -1;
        for (int x : s) {
          if (alphas[y] == xLabels[x] + yLabels[y] - weights[x][y]) {
            prevX = x;
          }
        }

        int x = yMatching[y];
        if (x == FREE) {
          // y is free
          backtrackAugPath(augPath, prevX);
          augPath.add(y);
          int numEdges = augPath.size() - 1;
          // Unmatch matched edges
          for (int i = 1; i < numEdges; i += 2) {
            // Odd edges are matched (start with y in Y), so unmatch them
            int yi = augPath.get(i);
            int xi = augPath.get(i+1);
            yMatching[yi] = FREE;
            xMatching[xi] = FREE;
          }
          for (int i = 0; i < numEdges; i += 2) {
              // Even edges are unmatched (start with x in X), so match them
            int xi = augPath.get(i);
            int yi = augPath.get(i+1);
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
            alphas[yi] = Math.min(alphas[yi], xLabels[x] + yLabels[yi] - weights[x][yi]);
          }

          backtrackAugPath(augPath, prevX);
          augPath.add(y);
          augPath.add(x);
        }
      }


    }

    return xMatching;
  }


  /* Remove vertices from the end of the augmenting path until we reach prevX */
  private static void backtrackAugPath(List<Integer> augPath, int prevX) {
    ListIterator<Integer> iter = augPath.listIterator(augPath.size());
    while (iter.previous() != prevX) {
      // remove x_i
      iter.remove();
      iter.previous();
      // remove y_i
      iter.remove();
    }
  }


  public static void main(String[] args) {
    int[][] weights = {
        {108,125,180},
        {150,135,175},
        {122,148,250}};
    System.out.println(Arrays.toString(hungarian(weights)));
  }
}
