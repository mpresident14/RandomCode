package datastructures.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class RandomTree<T extends Comparable<T>> {

  private static Random random = new Random();

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
    if (root == null) {
      root = new Node(val);
      return true;
    } else {
      return insertRandom(val, root, null, /* irrelevant */ false);
    }
  }

  /*
   * Insert val as the root of the subtree currently rooted by node with
   * probability 1 / (node.size + 1). With probaility node.size / (node.size + 1),
   * recurse on the appropriate subtree. At least one of parent or node will be
   * non-null
   */
  private boolean insertRandom(T val, Node node, Node parent, boolean isLeftChild) {
    if (node == null || random.nextInt(node.size + 1) == 0) {
      return insertAt(val, node, parent, isLeftChild);
    }

    int comp = val.compareTo(node.val);
    if (comp < 0) {
      if (insertRandom(val, node.left, node, true)) {
        ++node.size;
        return true;
      }
      return false;
    } else if (comp > 0) {
      if (insertRandom(val, node.right, node, false)) {
        ++node.size;
        return true;
      }
      return false;
    } else {
      return false;
    }
  }

  /*
   * Insert val as the root of the subtree currently rooted by node At least one
   * of parent or node will be non-null
   */
  private boolean insertAt(T val, Node node, Node parent, boolean isLeftChild) {
    if (node == null) {
      if (isLeftChild) {
        parent.left = new Node(val);
      } else {
        parent.right = new Node(val);
      }
      return true;
    }

    int comp = val.compareTo(node.val);
    if (comp < 0) {
      // Insert as root of left subtree and rotate right
      if (insertAt(val, node.left, node, true)) {
        ++node.size;
        rotateRight(node, parent);
        return true;
      }
      return false;
    } else if (comp > 0) {
      // Insert as root of right subtree and rotate left
      if (insertAt(val, node.right, node, false)) {
        ++node.size;
        rotateLeft(node, parent);
        return true;
      }
      return false;
    } else {
      // Already in the set
      return false;
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

    node.size = 1 + (node.left == null ? 0 : node.left.size) + (node.right == null ? 0 : node.right.size);
    newRoot.size = 1 + node.size + (newRoot.right == null ? 0 : newRoot.right.size);
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

    node.size = 1 + (node.left == null ? 0 : node.left.size) + (node.right == null ? 0 : node.right.size);
    newRoot.size = 1 + node.size + (newRoot.left == null ? 0 : newRoot.left.size);
  }

  public boolean delete(T val) {
    return deleteRec(val, root, null);
  }

  private boolean deleteRec(T val, Node node, Node parent) {
    if (node == null) {
      return false;
    }

    int comp = val.compareTo(node.val);
    if (comp == 0) {
      if (node.right == null) {
        // node.right is null, so just slide left subtree up
        if (parent == null) {
          root = node.left;
        } else if (node == parent.left) {
          parent.left = node.left;
        } else {
          parent.right = node.left;
        }
      } else {
        // Find next inorder node and replace deleted node with it
        Node nextInorder = nextInorder(node);
        if (nextInorder != node.right) {
          nextInorder.right = node.right;
        }
        nextInorder.left = node.left;
        if (parent == null) {
          root = nextInorder;
        } else if (parent.left == node) {
          parent.left = nextInorder;
        } else {
          parent.right = nextInorder;
        }
      }
      return true;
    } else if (comp < 0) {
      return deleteRec(val, node.left, node);
    } else {
      return deleteRec(val, node.right, node);
    }
  }

  /* Recurse to the left subtree until the left child is null.
   * Also update the parent of the next inorder node */
  private Node nextInorder(Node node) {
    if (node.right.left == null) {
      return node.right;
    }

    return nextInorderRec(node.right.left, node.right);
  }

  private Node nextInorderRec(Node node, Node parent) {
    if (node.left == null) {
      parent.left = node.right;
      return node;
    } else {
      return nextInorderRec(node.left, node);
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

  public int size() {
    return root == null ? 0 : root.size;
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
    System.out.println("Ratio: " + avgDepth/pbAvgDepth);
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
