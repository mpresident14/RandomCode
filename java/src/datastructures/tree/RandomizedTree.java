package datastructures.tree;

import java.util.Random;

public class RandomizedTree<T extends Comparable<T>> extends BST<T, RandomizedTree<T>.Node> {

  private static Random random = new Random();

  class Node extends BST<T, Node>.Node {
    private long size;

    Node(T val) {
      super(val);
      this.size = 1;
    }
  }

  @Override
  public boolean insert(T val) {
    long oldSize = nodeSize(root);
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
    if (node == null || random.nextLong() % (node.size + 1) == 0) {
      return insertAt(val, node);
    }

    int comp = val.compareTo(node.val);
    if (comp < 0) {
      node.left = insertRandom(val, node.left);
      updateSize(node);
    } else if (comp > 0) {
      node.right = insertRandom(val, node.right);
      updateSize(node);
    }
    return node;
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

  @Override
  public boolean delete(T val) {
    long oldSize = nodeSize(root);
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

  @Override
  protected Node rotateLeft(Node node) {
    Node newRoot = super.rotateLeft(node);
    updateSize(node);
    updateSize(newRoot);
    return newRoot;
  }

  @Override
  protected Node rotateRight(Node node) {
    Node newRoot = super.rotateRight(node);
    updateSize(node);
    updateSize(newRoot);
    return newRoot;
  }

  @Override
  public long size() {
    return nodeSize(root);
  }

  private void updateSize(Node node) {
    node.size = 1 + nodeSize(node.left) + nodeSize(node.right);
  }

  private long nodeSize(Node node) {
    return node == null ? 0 : node.size;
  }

  public static void main(String[] args) {
    RandomizedTree<Integer> tree = new RandomizedTree<>();
    mainFn(tree);
  }
}
