package datastructures.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RedBlackTree<T extends Comparable<T>> extends BST<T, RedBlackTree<T>.Node> {

  private enum Color {
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
    List<Node> path = new ArrayList<>();
    root = deleteRec(val, root, path);

    if (path.get(0) == null) {
      return false;
    }

    deleteRebalance(path);
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

  private void deleteRebalance(List<Node> path) {
    Node deleted = path.get(0);

    // Case 1
    if (deleted.color == Color.RED && deleted.isLeaf()) {
      return;
    }

    // Case 2
    if (isRed(deleted.left)) {
      deleted.left.color = Color.BLACK;
      return;
    } else if (isRed(deleted.right)) {
      deleted.right.color = Color.BLACK;
      return;
    }

    // TODO: Remove when done
    assertEquals(deleted.color, Color.BLACK);
    assertTrue(deleted.isLeaf());

    delRebalanceCase3(path);
  }

  private void delRebalanceCase3(List<Node> path) {
    Node deleted = path.get(0);
    if (deleted == root) {
      return;
    }

    Node parent = path.get(1);
    Node sibling = parent.left == deleted ? parent.right : parent.left;

    if (sibling.color == Color.BLACK) {
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
      Node newSubroot;
      if (deleted == parent.left) {
        newSubroot = super.rotateRight(parent);
      } else {
        newSubroot = super.rotateLeft(parent);
      }
      Node grandparent = path.size() == 2 ? null : path.get(2);
      attachSubroot(newSubroot, parent, grandparent);
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

  private boolean isRed(Node node) {
    return node != null && node.color == Color.RED;
  }

  // Visible for testing
  void checkConstraints() {
    if (root == null) {
      return;
    }

    assertEquals(Color.RED, root.color);
    blackHeight(root, false);
  }

  private long blackHeight(Node node, boolean redParent) {
    if (node == null) {
      return 0;
    }

    boolean isRed = node.color == Color.RED;
    assertFalse(redParent && isRed);

    long leftBlkHeight = blackHeight(node.left, isRed);
    long rightBlkHeight = blackHeight(node.right, isRed);
    assertEquals(leftBlkHeight, rightBlkHeight);

    return node.color == Color.BLACK ? 1 + leftBlkHeight : leftBlkHeight;
  }

  public static void main(String[] args) {
    RedBlackTree<Integer> rb = new RedBlackTree<>();
    Random random = new Random(0);
    int range = 10;
    for (int i = 0; i < range; ++i) {
      rb.insert(random.nextInt(range));
    }

    for (int i = 0; i < range; ++i) {
      System.out.println("DELETE " + i);
      try {
        rb.delete(i);
        rb.checkConstraints();
      } catch (RuntimeException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }

    rb.stats();
  }

}
