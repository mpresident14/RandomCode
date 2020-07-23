package datastructures.tree;

import java.util.ArrayList;
import java.util.List;

public class SplayTree<T extends Comparable<T>> extends BST<T, SplayTree<T>.Node> {
  class Node extends BST<T, Node>.Node {
    Node(T val) {
      super(val);
    }
  }

  @Override
  public boolean contains(T val) {
    List<Node> path = new ArrayList<>();
    if (containsRec(val, root, path)) {
      splay(path.get(0), path.subList(1, path.size()));
      return true;
    }
    return false;
  }

  private boolean containsRec(T val, Node node, List<Node> path) {
    if (node == null) {
      return false;
    }

    int comp = val.compareTo(node.val);
    if (comp == 0) {
      path.add(node);
      return true;
    } else if (comp < 0) {
      boolean ret = containsRec(val, node.left, path);
      path.add(node);
      return ret;
    } else {
      boolean ret = containsRec(val, node.right, path);
      path.add(node);
      return ret;
    }
  }

  @Override
  public boolean insert(T val) {
    // Path is reversed (starts at node, ends at root)
    List<Node> path = new ArrayList<>();
    boolean[] wasInserted = new boolean[1];
    root = insertRec(val, root, path, wasInserted);
    splay(path.get(0), path.subList(1, path.size()));

    if (wasInserted[0]) {
      ++this.size;
      return true;
    }
    return false;
  }

  /* Path is reversed (starts at node, ends at root) */
  Node insertRec(T val, Node node, List<Node> path, boolean[] wasInserted) {
    if (node == null) {
      wasInserted[0] = true;
      Node newNode = new Node(val);
      path.add(newNode);
      return newNode;
    }

    int comp = val.compareTo(node.val);
    if (comp < 0) {
      node.left = insertRec(val, node.left, path, wasInserted);
    } else if (comp > 0) {
      node.right = insertRec(val, node.right, path, wasInserted);
    }

    path.add(node);
    return node;
  }

  private void splay(Node node, List<Node> ancestors) {
    int numAncestors = ancestors.size();
    if (numAncestors == 0) {
      root = node;
    } else if (numAncestors == 1) {
      // Zig/Zag
      Node rootParent = ancestors.get(0);
      if (node == rootParent.left) {
        root = super.rotateRight(rootParent);
      } else {
        root = super.rotateLeft(rootParent);
      }
    } else {
      // Double rotation
      Node parent = ancestors.get(0);
      Node grandparent = ancestors.get(1);
      if (grandparent.left == parent) {
        if (parent.left == node) {
          zigzig(grandparent);
        } else {
          zigzag(grandparent);
        }
      } else {
        if (parent.left == node) {
          zagzig(grandparent);
        } else {
          zagzag(grandparent);
        }
      }

      // Attach the rotated subtree to the parent
      Node subtreeParent = numAncestors == 2 ? null : ancestors.get(2);
      if (subtreeParent == null) {
        root = node;
      } else if (grandparent == subtreeParent.left) {
        subtreeParent.left = node;
      } else {
        subtreeParent.right = node;
      }

      // Contiue moving node towards the root
      splay(node, ancestors.subList(2, numAncestors));
    }
  }

  @Override
  public boolean delete(T val) {
    boolean[] deleted = new boolean[1];
    root = deleteRec(val, root, deleted);
    if (deleted[0]) {
      --this.size;
    }
    return deleted[0];
  }

  /*
   * Deletes val from the subtree rooted at Node and returns the root of the
   * resulting subtree
   */
  private Node deleteRec(T val, Node node, boolean[] deleted) {
    if (node == null) {
      return null;
    }

    int comp = val.compareTo(node.val);
    if (comp == 0) {
      // This is the node to delete
      deleted[0] = true;
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
        node.right = deleteRec(nextInorder, node.right, deleted);
      }
    } else if (comp < 0) {
      node.left = deleteRec(val, node.left, deleted);
    } else {
      node.right = deleteRec(val, node.right, deleted);
    }

    return node;
  }

  private Node zigzig(Node node) {
    return super.rotateRight(super.rotateRight(node));
  }

  private Node zagzag(Node node) {
    return super.rotateLeft(super.rotateLeft(node));
  }

  private Node zigzag(Node node) {
    node.left = super.rotateLeft(node.left);
    return super.rotateRight(node);
  }

  private Node zagzig(Node node) {
    node.right = super.rotateRight(node.right);
    return super.rotateLeft(node);
  }

  public static void main(String[] args) {
    SplayTree<Integer> tree = new SplayTree<>();
    mainFn(tree);
  }
}
