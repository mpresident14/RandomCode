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

    // Visible for testing
    boolean isBalanced() {
      return Math.abs(nodeHeight(left) - nodeHeight(right)) <= 1;
    }

    private void updateHeight() {
      this.height = 1 + Math.max(nodeHeight(left), nodeHeight(right));
    }
  }

  @Override
  public boolean insert(T val) {
    // Path is reversed (starts at node, ends at root)
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
        attachSubroot(rebalanced, node, parent);
        break;
      } else {
        long origHeight = node.height;
        node.updateHeight();
        // We can stop once a node's height does not change b/c then none of the
        // ancestors' heights will have changed either
        if (node.height == origHeight) {
          break;
        }
      }
    }

    ++this.size;
    return true;
  }

  /* Path is reversed (starts at node, ends at root) */
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

    if (path.get(0) == null) {
      return false;
    }

    int pathLen = path.size();
    // Find first imbalanced node on path from deleted node to root
    for (int i = 1; i < pathLen; ++i) {
      Node node = path.get(i);
      long origHeight = node.height;
      if (!node.isBalanced()) {
        // See writeup for explanation
        Node y = null;
        Node x = null;
        if (nodeHeight(node.left) > nodeHeight(node.right)) {
          y = node.left;
          x = nodeHeight(y.left) >= nodeHeight(y.right) ? y.left : y.right;
        } else {
          y = node.right;
          x = nodeHeight(y.right) >= nodeHeight(y.left) ? y.right : y.left;
        }
        Node rebalanced = rebalance(node, y, x);
        // Attach the rebalanced subtree to the parent
        Node parent = i == pathLen - 1 ? null : path.get(i + 1);
        attachSubroot(rebalanced, node, parent);
        // We can stop once a node's height does not change b/c then none of the
        // ancestors' heights will have changed either
        if (rebalanced.height == origHeight) {
          break;
        }
      } else {
        node.updateHeight();
        // We can stop once a node's height does not change b/c then none of the
        // ancestors' heights will have changed either
        if (node.height == origHeight) {
          break;
        }
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
        path.add(node);
        // Just slide the right child up
        return node.right;
      } else if (node.right == null) {
        path.add(node);
        // Just slide the left child up
        return node.left;
      } else {
        // Find next inorder node and replace deleted node with it
        T nextInorder = minValue(node.right);
        node.val = nextInorder;
        node.right = deleteRec(nextInorder, node.right, path);
      }
    } else if (comp < 0) {
      node.left = deleteRec(val, node.left, path);

    } else {
      node.right = deleteRec(val, node.right, path);
    }

    path.add(node);
    return node;
  }

  void attachSubroot(Node newSubroot, Node oldSubroot, Node parent) {
    if (parent == null) {
      root = newSubroot;
    } else {
      if (oldSubroot == parent.left) {
        parent.left = newSubroot;
      } else {
        parent.right = newSubroot;
      }
    }
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
    AVLTree<Integer> tree = new AVLTree<>();
    mainFn(tree);
  }

}
