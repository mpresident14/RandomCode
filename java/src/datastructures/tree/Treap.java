package datastructures.tree;

import java.util.Random;

public class Treap<T extends Comparable<T>> {

  private static Random random = new Random();

  private class Node {
    private T val;
    private double priority;
    private Node left;
    private Node right;

    Node(T val, double priority) {
      this.val = val;
      this.priority = priority;
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

  public boolean insert(T val) {
    boolean[] inserted = new boolean[1];
    root = insertAtPriority(val, random.nextDouble(), root, inserted);
    if (inserted[0]) {
      ++this.size;
      return true;
    }
    return false;
  }

  /*
   * If the priority is higher than that of current root of this subtree, insert
   * it as the new root of the subtree.
   *
   * - At least one of parent or node will be non-null
   */
  private Node insertAtPriority(T val, double priority, Node node, boolean[] inserted) {
    // NOTE: Could be an issue if priority == node.priority, but not gonna worry
    // about it
    if (node == null || priority > node.priority) {
      return insertAt(val, priority, node, inserted);
    }

    int comp = val.compareTo(node.val);
    if (comp < 0) {
      node.left = insertAtPriority(val, priority, node.left, inserted);
    } else if (comp > 0) {
      node.right = insertAtPriority(val, priority, node.right, inserted);
    }
    return node;
  }

  /*
   * Insert val as the root of the subtree and return the root of the updated
   * subtree
   */
  private Node insertAt(T val, double priority, Node node, boolean[] inserted) {
    if (node == null) {
      inserted[0] = true;
      return new Node(val, priority);
    }

    int comp = val.compareTo(node.val);
    if (comp < 0) {
      // Insert as root of left subtree and rotate right if
      // it was inserted
      Node newLeft = insertAt(val, priority, node.left, inserted);
      if (node.left != newLeft) {
        node.left = newLeft;
        return rotateRight(node); // newLeft
      } else {
        return node;
      }
    } else if (comp > 0) {
      // Insert as root of right subtree and rotate left if
      // it was inserted
      Node newRight = insertAt(val, priority, node.right, inserted);
      if (node.right != newRight) {
        node.right = newRight;
        return rotateLeft(node); // newRight
      } else {
        return node;
      }
    } else {
      // Already in the set
      return node;
    }
  }

  public boolean delete(T val) {
    boolean[] deleted = new boolean[1];
    root = deleteRec(val, root, deleted);
    if (deleted[0]) {
      --this.size;
      return true;
    }
    return false;
  }

  /*
   * Delete val from the subtree with node at the root and return the root of the
   * updated subtree
   */
  private Node deleteRec(T val, Node node, boolean[] deleted) {
    if (node == null) {
      return node;
    }

    int comp = val.compareTo(node.val);
    if (comp == 0) {
      deleted[0] = true;
      return deleteAtLeaf(node);
    } else if (comp < 0) {
      node.left = deleteRec(val, node.left, deleted);
      return node;
    } else {
      node.right = deleteRec(val, node.right, deleted);
      return node;
    }
  }

  /**
   * Rotate the subtree currently rooted at node while maintaining the heap
   * structure. If node is a leaf, returns null (deletes it)
   */
  private Node deleteAtLeaf(Node node) {
    if (node.left == null && node.right == null) {
      // We are deleting a leaf node
      return null;
    } else {
      // Rotate max priority child to the root of the subtree, which pushes node
      // towards the leaves
      boolean leftChildHigher = node.right == null || (node.left != null && node.left.priority > node.right.priority);
      if (leftChildHigher) {
        Node newRoot = rotateRight(node);
        newRoot.right = deleteAtLeaf(node);
        return newRoot;
      } else {
        Node newRoot = rotateLeft(node);
        newRoot.left = deleteAtLeaf(node);
        return newRoot;
      }
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
    double pbHeight = Math.floor(Math.log(this.size) / Math.log(2));
    double pbAvgDepth = pbHeight - 1 + (pbHeight + 1) / ((1 << ((long) pbHeight + 1)) - 1);

    double avgDepth = acc[0] * 1.0 / this.size;
    System.out.println("# nodes: " + this.size);
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

  public long size() {
    return this.size;
  }

  public static void main(String[] args) {
    Treap<Integer> treap = new Treap<>();
    Random random = new Random();
    for (int i = 0; i < 100000; ++i) {
      treap.insert(random.nextInt());
    }
    treap.stats();
  }
}
