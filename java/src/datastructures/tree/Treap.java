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
    double priority = random.nextDouble();
    if (root == null) {
      root = new Node(val, priority);
      ++this.size;
      return true;
    } else {
      if (insertAtPriority(val, priority, root, null, /* irrelevant */ false)) {
        ++this.size;
        return true;
      }
      return false;
    }
  }

  /*
   * If the priority is higher than that of current root of this subtree, insert
   * it as the new root of the subtree.
   *
   * - At least one of parent or node will be non-null
   */
  private boolean insertAtPriority(T val, double priority, Node node, Node parent, boolean isLeftChild) {
    // NOTE: Could be an issue if priority == node.priority, but not gonna worry about it
    if (node == null || priority > node.priority) {
      return insertAt(val, priority, node, parent, isLeftChild);
    }

    int comp = val.compareTo(node.val);
    if (comp < 0) {
      return insertAtPriority(val, priority, node.left, node, true);
    } else if (comp > 0) {
      return insertAtPriority(val, priority, node.right, node, false);
    } else {
      // Already in the set
      return false;
    }
  }

  /*
   * Insert val as the root of the subtree currently rooted by node.
   *
   * - At least one of parent or node will be non-null
   */
  private boolean insertAt(T val, double priority, Node node, Node parent, boolean isLeftChild) {
    if (node == null) {
      if (isLeftChild) {
        parent.left = new Node(val, priority);
      } else {
        parent.right = new Node(val, priority);
      }
      return true;
    }

    int comp = val.compareTo(node.val);
    if (comp < 0) {
      // Insert as root of left subtree and rotate right
      if (insertAt(val, priority, node.left, node, true)) {
        rotateRight(node, parent);
        return true;
      }
      return false;
    } else if (comp > 0) {
      // Insert as root of right subtree and rotate left
      if (insertAt(val, priority, node.right, node, false)) {
        rotateLeft(node, parent);
        return true;
      }
      return false;
    } else {
      // Already in the set
      return false;
    }
  }

  public boolean delete(T val) {
    if (deleteRec(val, root, null)) {
      --this.size;
      return true;
    }
    return false;
  }

  private boolean deleteRec(T val, Node node, Node parent) {
    if (node == null) {
      return false;
    }

    int comp = val.compareTo(node.val);
    if (comp == 0) {
      deleteAtLeaf(node, parent);
      return true;
    } else if (comp < 0) {
      return deleteRec(val, node.left, node);
    } else {
      return deleteRec(val, node.right, node);
    }
  }

  /**
   * Rotate node all the way to leaf and delete it while maintaining the heap
   */
  private void deleteAtLeaf(Node node, Node parent) {
    if (node.left == null && node.right == null) {
      // We are deleting a leaf node
      if (parent == null) {
        root = null;
      } else if (node == parent.left) {
        parent.left = null;
      } else {
        parent.right = null;
      }
    } else {
      // Rotate max priority child to the root of the subtree, which pushes node
      // towards the leaves
      boolean leftChildHigher = node.right == null || (node.left != null && node.left.priority > node.right.priority);
      if (leftChildHigher) {
        Node newParent = node.left;
        rotateRight(node, parent);
        deleteAtLeaf(node, newParent);
      } else {
        Node newParent = node.right;
        rotateLeft(node, parent);
        deleteAtLeaf(node, newParent);
      }
    }
  }

  /*
   * Rotate the subtree rooted at node to the left - node.right must be non-null
   */
  private void rotateLeft(Node node, Node parent) {
    Node newRoot = node.right;
    node.right = newRoot.left;
    newRoot.left = node;
    if (parent == null) {
      root = newRoot;
    } else if (parent.left == node) {
      parent.left = newRoot;
    } else {
      parent.right = newRoot;
    }
  }

  /*
   * Rotate the subtree rooted at node to the right - node.left must be non-null
   */
  private void rotateRight(Node node, Node parent) {
    Node newRoot = node.left;
    node.left = newRoot.right;
    newRoot.right = node;
    if (parent == null) {
      root = newRoot;
    } else if (parent.left == node) {
      parent.left = newRoot;
    } else {
      parent.right = newRoot;
    }
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
