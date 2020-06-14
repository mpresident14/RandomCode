package datastructures.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AVLTree<T extends Comparable<T>> {
  private class Node {
    T val;
    long height;
    Node left;
    Node right;

    Node(T val) {
      this.val = val;
    }

    private boolean isBalanced() {
      return Math.abs(nodeHeight(left) - nodeHeight(right)) <= 1;
    }
  }

  private Node root;
  private long size;

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

  public void insertAll(Iterable<? extends T> iterable) {
    for (T value : iterable) {
      insert(value);
    }
  }

  boolean insert(T val) {
    List<Node> path = new ArrayList<>();
    root = insertRec(val, root, path);
    path.add(root);

    if (path.get(0) == null) {
      return false;
    }


    int pathLen = path.size();
    // Find first imbalanced node on path from leaf to root
    for (int i = 2; i < pathLen; ++i) {
      Node node = path.get(i);
      if (!node.isBalanced()) {
        insertRebalance(node, path.get(i-1), path.get(i-2));
        break;
      }
    }


    ++this.size;
    return true;
  }

  Node insertRec(T val, Node node, List<Node> path) {
    if (node == null) {
      Node newNode = new Node(val);
      path.add(newNode);
      return newNode;
    }

    int comp = val.compareTo(node.val);
    if (comp < 0) {
      node.left = insertRec(val, node.left, path);
      path.add(node.left);
    } else if (comp > 0) {
      node.right = insertRec(val, node.right, path);
      path.add(node.right);
    } else {
      // Already exists in the set
      path.add(null);
    }

    return node;
  }


  void insertRebalance(Node z, Node y, Node x) {
    if (z.left == y) {
      if (y.left == x) {
        rotateLL(z);
      } else {
        rotateLR(z);
      }
    } else {
      if (y.left == x) {
        rotateRL(z);
      } else {
        rotateRR(z);
      }
    }
  }


  /*
   * Rotate the subtree rooted at node to the left and return the new root of the
   * subtree
   *
   * - node.right must be non-null
   */
  private Node rotateRR(Node node) {
    Node newRoot = node.right;
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
  private Node rotateLL(Node node) {
    Node newRoot = node.left;
    node.left = newRoot.right;
    newRoot.right = node;

    return newRoot;
  }

  private Node rotateLR(Node node) {
    node.left = rotateRR(node.left);
    return rotateLL(node);
  }

  private Node rotateRL(Node node) {
    node.right = rotateLL(node.right);
    return rotateRR(node);
  }

  private long nodeHeight(Node node) {
    return node == null ? 0 : node.height;
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
      sb.append(node.val.toString());
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

    double pbAvgDepth = TreeUtils.pbAvgDepth(this.size);
    double avgDepth = acc[0] * 1.0 / this.size;
    System.out.println("# nodes: " + this.size);
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

  public long size() {
    return this.size;
  }

  public static void main(String[] args) {
    AVLTree<Integer> avl = new AVLTree<>();
    Random random = new Random();
    for (int i = 0; i < 100000; ++i) {
      avl.insert(random.nextInt());
    }
    avl.stats();
  }
}
