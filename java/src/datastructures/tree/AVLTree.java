package datastructures.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AVLTree<T extends Comparable<T>> extends BST<T, AVLTree<T>.Node> {
  class Node extends BST<T, Node>.Node {
    private long height;

    Node(T val) {
      super(val);
    }

    private boolean isBalanced() {
      return Math.abs(nodeHeight(left) - nodeHeight(right)) <= 1;
    }
  }

  @Override
  public boolean insert(T val) {
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
        insertRebalance(node, path.get(i - 1), path.get(i - 2));
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
        rotateRight(z);
      } else {
        rotateLR(z);
      }
    } else {
      if (y.left == x) {
        rotateRL(z);
      } else {
        rotateLeft(z);
      }
    }
  }

  @Override
  public boolean delete(T val) {
    return false;
  }

  private Node rotateLR(Node node) {
    node.left = rotateLeft(node.left);
    return rotateRight(node);
  }

  private Node rotateRL(Node node) {
    node.right = rotateRight(node.right);
    return rotateLeft(node);
  }

  private long nodeHeight(Node node) {
    return node == null ? 0 : node.height;
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
