package datastructures.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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

  boolean insert(T val) {
    List<Node> path = new ArrayList<>();
    path.add(root);
    root = insertRec(val, root, path);

    int pathLen = path.size();
    if (path.get(pathLen - 1) == null) {
      return false;
    }


    for (int i = pathLen - 1; i >= 0; --i) {
      Node node = path.get(i);
      if (!node.isBalanced()) {
        insertRebalance(node, path.get(i+1), path.get(i+2));
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
    return null;
  }

  private Node rotateRL(Node node) {
    return null;
  }

  private long nodeHeight(Node node) {
    return node == null ? 0 : node.height;
  }
}
