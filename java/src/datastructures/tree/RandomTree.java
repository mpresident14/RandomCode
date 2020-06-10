package datastructures.tree;

import java.util.Random;

public class RandomTree<T extends Comparable<T>> {

  private static Random random = new Random(5);

  private class Node {
    private T val;
    private int size;
    private Node left;
    private Node right;

    Node(T val) {
      this.val = val;
      this.size = 1;
    }
  }

  private Node root;

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

  public boolean insert(T val) {
    int oldSize = nodeSize(root);
    root = insertRandom(val, root);
    return root.size != oldSize;
  }

  /*
   * Insert val as the root of the subtree currently rooted by node with
   * probability 1 / (node.size + 1). With probability node.size / (node.size +
   * 1), recurse on the appropriate subtree. At least one of parent or node will
   * be non-null
   */
  private Node insertRandom(T val, Node node) {
    if (node == null || random.nextInt(node.size + 1) == 0) {
      return insertAt(val, node);
    }

    int comp = val.compareTo(node.val);
    if (comp < 0) {
      node.left = insertRandom(val, node.left);
      updateSize(node);
      return node;
    } else if (comp > 0) {
      node.right = insertRandom(val, node.right);
      updateSize(node);
      return node;
    } else {
      return node;
    }
  }

  /*
   * Insert val as the root of the subtree and return the root of the updated
   * subtree
   */
  private Node insertAt(T val, Node node) {
    if (node == null) {
      return new Node(val);
    }

    int comp = val.compareTo(node.val);
    if (comp < 0) {
      // Insert as root of left subtree and rotate right if
      // it was inserted
      Node newLeft = insertAt(val, node.left);
      if (node.left != newLeft) {
        node.left = newLeft;
        ++node.size;
        return rotateRight(node); // newLeft
      } else {
        return node;
      }
    } else if (comp > 0) {
      // Insert as root of right subtree and rotate left if
      // it was inserted
      Node newRight = insertAt(val, node.right);
      if (node.right != newRight) {
        node.right = newRight;
        ++node.size;
        return rotateLeft(node); // newRight
      } else {
        return node;
      }
    } else {
      // Already in the set
      return node;
    }
  }

  /*
   * Rotate the subtree rooted at node to the left and return the new root of the
   * subtree
   *
   * - node.right must be non-null
   */
  private Node rotateLeft(Node node) {
    Node newRoot = node.right;
    node.right = newRoot.left;
    newRoot.left = node;

    updateSize(node);
    updateSize(newRoot);

    return newRoot;
  }

  /*
   * Rotate the subtree rooted at node to the right and return the new root of the
   * subtree
   *
   * - node.left must be non-null
   */
  private Node rotateRight(Node node) {
    Node newRoot = node.left;
    node.left = newRoot.right;
    newRoot.right = node;

    updateSize(node);
    updateSize(newRoot);

    return newRoot;
  }


  public boolean delete(T val) {
    int oldSize = nodeSize(root);
    root = deleteRec(val, root);
    return oldSize != nodeSize(root);
  }

  /*
   * Deletes val from the subtree rooted at Node and returns the root of the
   * resulting subtree
   */
  private Node deleteRec(T val, Node node) {
    if (node == null) {
      return null;
    }

    int comp = val.compareTo(node.val);
    if (comp == 0) {
      // This is the node to delete
      if (node.left == null) {
        // Just slide the right child up
        return node.right;
      } else if (node.right == null) {
        // Just slide the left child up
        return node.left;
      } else {
        // Find next inorder node and replace deleted node with it
        T nextInorder = minValue(node.right);
        node.val = nextInorder;
        node.right = deleteRec(nextInorder, node.right);
      }
    } else if (comp < 0) {
      node.left = deleteRec(val, node.left);
    } else {
      node.right = deleteRec(val, node.right);
    }

    updateSize(node);
    return node;
  }

  /*
   * Return the minimum value in the subtree rooted by node
   */
  private T minValue(Node node) {
    Node current = node;
    while (current.left != null) {
      current = current.left;
    }
    return current.val;
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

  public int size() {
    return nodeSize(root);
  }

  private void updateSize(Node node) {
    node.size = 1 + nodeSize(node.left) + nodeSize(node.right);
  }

  private int nodeSize(Node node) {
    return node == null ? 0 : node.size;
  }

  public void stats() {
    if (root == null) {
      System.out.println("Tree is empty, no stats");
      return;
    }

    int[] acc = new int[1];
    statsRec(root, 0, acc);
    double pbHeight = Math.floor(Math.log(root.size) / Math.log(2));
    double pbAvgDepth = pbHeight - 1 + (pbHeight + 1) / ((1 << ((long) pbHeight + 1)) - 1);

    double avgDepth = acc[0] * 1.0 / root.size;
    System.out.println("# nodes: " + root.size);
    System.out.println("Perfectly balanced avg depth (approx): " + pbAvgDepth);
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

  public static void main(String[] args) {
    RandomTree<Integer> rt = new RandomTree<>();
    Random random = new Random();
    for (int i = 0; i < 100000; ++i) {
      rt.insert(random.nextInt());
    }
    rt.stats();
  }
}
