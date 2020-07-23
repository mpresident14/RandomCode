package datastructures.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public abstract class BST<T extends Comparable<T>, NodeType extends BST<T, NodeType>.Node> implements Iterable<T> {
  protected abstract class Node {
    protected T val;
    protected NodeType left;
    protected NodeType right;

    protected Node(T val) {
      this.val = val;
    }

    boolean isLeaf() {
      return this.left == null && this.right == null;
    }

    public String toString() {
      return val.toString();
    }
  }

  protected NodeType root;
  protected long size;

  public static <N extends BST<Integer, N>.Node> void mainFn(BST<Integer, N> tree) {
    long seed = new Random().nextLong();
    System.out.println("SEED: " + seed);
    Random random = new Random(seed);
    int range = 1000000;
    for (int i = 0; i < range; ++i) {
      tree.insert(random.nextInt(range));
      tree.insert(i);
      tree.delete(random.nextInt(range));
    }
    tree.stats();
  }

  public abstract boolean insert(T value);

  public abstract boolean delete(T val);

  /*
   * Return the minimum value in the subtree rooted by node
   */
  protected T minValue(Node node) {
    Node current = node;
    while (current.left != null) {
      current = current.left;
    }
    return current.val;
  }

  public void insertAll(Iterable<? extends T> iterable) {
    for (T value : iterable) {
      insert(value);
    }
  }

  public void deleteAll(Iterable<? extends T> iterable) {
    for (T value : iterable) {
      delete(value);
    }
  }

  public boolean contains(T val) {
    return containsRec(val, root);
  }

  private boolean containsRec(T val, Node node) {
    if (node == null) {
      return false;
    }

    int comp = val.compareTo(node.val);
    if (comp == 0) {
      return true;
    } else if (comp < 0) {
      return containsRec(val, node.left);
    } else {
      return containsRec(val, node.right);
    }
  }

  /*
   * Rotate the subtree rooted at node to the left and return the new root of the
   * subtree
   *
   * - node.right must be non-null
   */
  protected NodeType rotateLeft(NodeType node) {
    NodeType newRoot = node.right;
    node.right = newRoot.left;
    newRoot.left = node;

    return newRoot;
  }

  /*
   * Rotate the subtree rooted at node to the right and return the new root of the
   * subtree
   *
   * - node.left must be non-null
   */
  protected NodeType rotateRight(NodeType node) {
    NodeType newRoot = node.left;
    node.left = newRoot.right;
    newRoot.right = node;

    return newRoot;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    printSubtree(root, 0, sb);
    return sb.toString();
  }

  private void printSubtree(Node node, int depth, StringBuilder sb) {
    for (int i = 0; i < depth; ++i) {
      sb.append("  ");
    }
    if (node == null) {
      sb.append("null\n");
    } else {
      sb.append(node.toString());
      sb.append('\n');
      printSubtree(node.left, depth + 1, sb);
      printSubtree(node.right, depth + 1, sb);
    }
  }

  public void stats() {
    if (root == null) {
      System.out.println("Tree is empty, no stats");
      return;
    }

    int[] acc = new int[1];
    statsRec(root, 0, acc);

    double pbAvgDepth = pbAvgDepth(size());
    double avgDepth = acc[0] * 1.0 / size();
    System.out.println("# nodes: " + size());
    System.out.println("Perfectly balanced avg depth: " + pbAvgDepth);
    System.out.println("Avg depth: " + avgDepth);
    System.out.println("Ratio: " + avgDepth / pbAvgDepth);
  }

  private void statsRec(Node node, int depth, int[] acc) {
    acc[0] += depth;
    if (node.left != null) {
      statsRec(node.left, depth + 1, acc);
    }
    if (node.right != null) {
      statsRec(node.right, depth + 1, acc);
    }
  }

  private static double pbAvgDepth(long n) {
    long pbHeight = (long) Math.floor(Math.log(n) / Math.log(2));
    long pbHeightPow2 = 1 << pbHeight;
    // https://www.wolframalpha.com/input/?i=sum+d*2%5Ed%2C+d+%3D+0+to+h+-+1
    long completeDepthSum = pbHeightPow2 * pbHeight - 2 * (pbHeightPow2 - 1);
    long lastRowDepthSum = (n - pbHeightPow2 + 1) * pbHeight;
    return (completeDepthSum + lastRowDepthSum) * 1.0 / n;
  }

  public long size() {
    return this.size;
  }

  @Override
  public Iterator<T> iterator() {
    return new MyIterator();
  }

  public class MyIterator implements Iterator<T> {
    private List<Node> path = new ArrayList<>();
    private boolean visitedRight = false;
    private Node toRemove = null;

    private MyIterator() {
      Node currentNode = root;
      while (currentNode != null) {
        path.add(currentNode);
        currentNode = currentNode.left;
      }
    }

    @Override
    public boolean hasNext() {
      return !path.isEmpty();
    }

    @Override
    public T next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      toRemove = path.get(path.size() - 1);
      moveToNext();
      return toRemove.val;
    }

    private void moveToNext() {
      int pathLen = path.size();
      Node last = path.get(pathLen - 1);

      if (!visitedRight && last.right != null) {
        // Middle, go to next inorder node
        Node current = last.right;
        while (current != null) {
          path.add(current);
          current = current.left;
        }
      } else {
        path.remove(pathLen - 1);
        if (pathLen == 1) {
          // Root, we're done
          return;
        }

        Node parent = path.get(pathLen - 2);
        if (parent.left == last) {
          // Left node, parent is next
          visitedRight = false;
        } else {
          // Right node, recurse on parent
          visitedRight = true;
          moveToNext();
        }
      }
    }
  }
}
