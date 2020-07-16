package datastructures.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RedBlackTree<T extends Comparable<T>> extends BST<T, RedBlackTree<T>.Node> {

  // Visible for testing
  enum Color {
    RED, BLACK;
  }

  class Node extends BST<T, Node>.Node {
    // New nodes always start out red
    private Color color = Color.RED;

    Node(T val) {
      super(val);
    }

    @Override
    public String toString() {
      return new StringBuilder(val.toString()).append(": ").append(color == Color.RED ? "R" : "B").toString();
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

    insertRebalance(path);
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

  private void insertRebalance(List<Node> path) {
    Node inserted = path.get(0);

    // Case 1
    if (inserted == root) {
      root.color = Color.BLACK;
      return;
    }

    // Case 2
    Node parent = path.get(1);
    if (parent.color == Color.BLACK) {
      return;
    }

    Node grandparent = path.get(2);
    Node uncle = grandparent.left == parent ? grandparent.right : grandparent.left;
    if (uncle == null || uncle.color == Color.BLACK) {
      // Case 3a
      Node newSubroot = avlRotate(grandparent, parent, inserted);
      newSubroot.color = Color.BLACK;
      newSubroot.left.color = Color.RED;
      newSubroot.right.color = Color.RED;
      Node greatGrandparent = path.size() == 3 ? null : path.get(3);
      attachSubroot(newSubroot, grandparent, greatGrandparent);
    } else {
      // Case 3b
      parent.color = Color.BLACK;
      uncle.color = Color.BLACK;
      grandparent.color = Color.RED;
      insertRebalance(path.subList(2, path.size()));
    }
  }

  @Override
  public boolean delete(T val) {
    // LinkedList because we need insertion at index 2 in case 3
    List<Node> path = new LinkedList<>();
    deleteRec(val, root, path);

    if (path.get(0) == null) {
      return false;
    }

    deleteRebalance(path);
    --this.size;
    return true;
  }

  /*
   * Returns the path to the node that we actually end up deleting. Unlike the
   * other trees, we don't actually delete the node here. Instead we delete it in
   * deleteRebalance because it makes the recursion for case 3 more elegant.
   */
  private void deleteRec(T val, Node node, List<Node> path) {
    if (node == null) {
      // Not in the set
      path.add(null);
      return;
    }

    int comp = val.compareTo(node.val);
    if (comp == 0) {
      // This is not the node to delete
      if (!(node.left == null || node.right == null)) {
        // Find next inorder node and replace deleted node with it
        T nextInorder = minValue(node.right);
        node.val = nextInorder;
        deleteRec(nextInorder, node.right, path);
      }
    } else if (comp < 0) {
      deleteRec(val, node.left, path);

    } else {
      deleteRec(val, node.right, path);
    }

    path.add(node);
  }

  private void deleteRebalance(List<Node> path) {
    Node deleted = path.get(0);
    Node parent = path.size() == 1 ? null : path.get(1);

    // Case 1
    if (deleted.color == Color.RED && deleted.isLeaf()) {
      // Actually remove the node
      attachSubroot(null, deleted, parent);
    } else if (isRed(deleted.left)) {
      // Case 2
      deleted.left.color = Color.BLACK;
      // Actually remove the node
      attachSubroot(deleted.left, deleted, parent);
      return;
    } else if (isRed(deleted.right)) {
      // Case 2
      deleted.right.color = Color.BLACK;
      // Actually remove the node
      attachSubroot(deleted.right, deleted, parent);
      return;
    } else {
      // Case 3
      delRebalanceCase3(path);
      // Actually remove the node
      attachSubroot(null, deleted, parent);
    }
  }

  private void delRebalanceCase3(List<Node> path) {
    Node deleted = path.get(0);
    if (deleted == root) {
      return;
    }

    Node parent = path.get(1);
    // Is deleted node was left child, then sibling is the right, and vice versa
    // (we know sibling must exist because deleted node was black)
    boolean isLeftChild = deleted == parent.left;
    Node sibling = isLeftChild ? parent.right : parent.left;

    if (isBlack(sibling)) {
      Node redNephew = null;
      if (isRed(sibling.left)) {
        redNephew = sibling.left;
      } else if (isRed(sibling.right)) {
        redNephew = sibling.right;
      }

      if (redNephew != null) {
        // Case 3a
        Node newSubroot = avlRotate(parent, sibling, redNephew);
        newSubroot.color = parent.color;
        newSubroot.left.color = Color.BLACK;
        newSubroot.right.color = Color.BLACK;
        Node grandparent = path.size() == 2 ? null : path.get(2);
        attachSubroot(newSubroot, parent, grandparent);
      } else {
        // Case 3b
        boolean shouldRecurse = parent.color == Color.BLACK;
        sibling.color = Color.RED;
        parent.color = Color.BLACK;
        if (shouldRecurse) {
          delRebalanceCase3(path.subList(1, path.size()));
        }
      }
    } else {
      // Case 3c
      parent.color = Color.RED;
      sibling.color = Color.BLACK;
      Node newSubroot = isLeftChild ? super.rotateLeft(parent) : super.rotateRight(parent);
      Node grandparent = path.size() == 2 ? null : path.get(2);
      attachSubroot(newSubroot, parent, grandparent);
      // Fix the path because of the rotation
      path.add(2, sibling);
      // Will hit case 3a or 3b
      delRebalanceCase3(path);
    }

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

  Node avlRotate(Node z, Node y, Node x) {
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
    return newRoot;
  }

  private Node rotateRR(Node node) {
    Node newRoot = super.rotateLeft(node);
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

  // Visible for testing
  boolean isRed(Node node) {
    return node != null && node.color == Color.RED;
  }

  // Visible for testing
  boolean isBlack(Node node) {
    return !isRed(node);
  }

  public static void main(String[] args) {
    RedBlackTree<Integer> tree = new RedBlackTree<>();
    mainFn(tree);
  }
}
