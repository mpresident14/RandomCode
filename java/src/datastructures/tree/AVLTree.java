package datastructures.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AVLTree<T extends Comparable<T>> extends BST<T, AVLTree<T>.Node> {
  class Node extends BST<T, Node>.Node {
    private long height;

    Node(T val) {
      super(val);
      this.height = 1;
    }

    private boolean isBalanced() {
      return Math.abs(nodeHeight(left) - nodeHeight(right)) <= 1;
    }

    private void updateHeight() {
      this.height = 1 + Math.max(nodeHeight(left), nodeHeight(right));
    }
  }

  @Override
  public boolean insert(T val) {
    List<Node> path = new ArrayList<>();
    root = insertRec(val, root, path);

    if (path.get(0) == null) {
      return false;
    }

    int pathLen = path.size();
    // Find first imbalanced node on path from leaf to root
    // Start from index 1 so that we can increment heights (but skip newly added
    // node). After the rotation, the rotated subtree height will be the same as
    // before the insertion, so heights of nodes above the subtree do not need
    // adjustment
    for (int i = 1; i < pathLen; ++i) {
      Node node = path.get(i);
      if (!node.isBalanced()) {
        Node rebalanced = rebalance(node, path.get(i - 1), path.get(i - 2));
        // Attach the rebalanced subtree to the parent
        Node parent = i == pathLen - 1 ? null : path.get(i + 1);
        if (parent == null) {
          root = rebalanced;
        } else if (parent.left == node) {
          parent.left = rebalanced;
        } else {
          parent.right = rebalanced;
        }
        break;
      } else {
        // TODO: Break if height does not change
        node.updateHeight();
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
      path.add(node);
    } else if (comp > 0) {
      node.right = insertRec(val, node.right, path);
      path.add(node);
    } else {
      // Already exists in the set
      path.add(null);
    }

    return node;
  }

  @Override
  public boolean delete(T val) {
    List<Node> path = new ArrayList<>();
    root = deleteRec(val, root, path);

    if (!path.isEmpty() && path.get(0) == null) {
      return false;
    }

    int pathLen = path.size();
    // Find first imbalanced node on path from leaf to root
    for (int i = 0; i < pathLen; ++i) {
      Node node = path.get(i);
      if (!node.isBalanced()) {
        Node y = nodeHeight(node.left) > nodeHeight(node.right) ? node.left : node.right;
        Node x = nodeHeight(y.left) >= nodeHeight(y.right) ? y.left : y.right;
        Node rebalanced = rebalance(node, y, x);
        // Attach the rebalanced subtree to the parent
        Node parent = i == pathLen - 1 ? null : path.get(i + 1);
        if (parent == null) {
          root = rebalanced;
        } else if (parent.left == node) {
          parent.left = rebalanced;
        } else {
          parent.right = rebalanced;
        }
      } else {
        // TODO: Break if height does not change
        node.updateHeight();
      }
    }

    --this.size;
    return true;
  }

  private Node deleteRec(T val, Node node, List<Node> path) {
    if (node == null) {
      // Not in the set
      path.add(null);
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
        node.right = deleteRec(nextInorder, node.right, path);
        path.add(node);
      }
    } else if (comp < 0) {
      node.left = deleteRec(val, node.left, path);
      path.add(node);
    } else {
      node.right = deleteRec(val, node.right, path);
      path.add(node);
    }

    return node;
  }

  Node rebalance(Node z, Node y, Node x) {
    if (z.left == y) {
      if (y.left == x) {
        return rotateLL(z);
      } else {
        return rotateLR(z);
      }
    } else {
      if (y.left == x) {
        return rotateRL(z);
      } else {
        return rotateRR(z);
      }
    }
  }

  private Node rotateLL(Node node) {
    Node newRoot = super.rotateRight(node);
    node.updateHeight();
    newRoot.updateHeight();
    return newRoot;
  }

  private Node rotateRR(Node node) {
    Node newRoot = super.rotateLeft(node);
    node.updateHeight();
    newRoot.updateHeight();
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

  @Override
  public void stats() {
    super.stats();
    long pbHeight = (long) Math.floor(Math.log(size()) / Math.log(2)) + 1;
    long height = nodeHeight(root);
    System.out.println("Perfectly balanced height: " + pbHeight);
    System.out.println("Height: " + height);
    System.out.println("Ratio: " + height * 1.0 / pbHeight);
  }

  public static void main(String[] args) {
    AVLTree<Integer> avl = new AVLTree<>();
    Random random = new Random(0);
    int range = 1000000;
    for (int i = 0; i < range; ++i) {
      avl.insert(random.nextInt(range));
      avl.insert(i);
      avl.delete(random.nextInt(range));
    }
    avl.stats();
  }

}
