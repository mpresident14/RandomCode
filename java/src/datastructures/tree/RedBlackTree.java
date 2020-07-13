package datastructures.tree;

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

  void insertRebalance(List<Node> path) {
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
      // Reattach new subroot
      if (path.size() == 3) {
        root = newSubroot;
      } else {
        Node greatGrandparent = path.get(3);
        if (grandparent == greatGrandparent.left) {
          greatGrandparent.left = newSubroot;
        } else {
          greatGrandparent.right = newSubroot;
        }
      }
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
    return true;
  }

  private Node deleteRec(T val, Node node, List<Node> path) {
    return null;
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
  void checkConstraints() {
    if (root == null) {
      return;
    }

    if (root.color == Color.RED) {
      throw new RuntimeException("Red root");
    }
    blackHeight(root, false);
  }

  private long blackHeight(Node node, boolean redParent) {
    if (node == null) {
      return 0;
    }

    boolean isRed = node.color == Color.RED;
    if (redParent && isRed) {
      throw new RuntimeException("Double red");
    }

    long leftBlkHeight = blackHeight(node.left, isRed);
    long rightBlkHeight = blackHeight(node.right, isRed);
    if (leftBlkHeight != rightBlkHeight) {
      throw new RuntimeException("Non-constant black height");
    }

    return node.color == Color.BLACK ? 1 + leftBlkHeight : leftBlkHeight;
  }

  public static void main(String[] args) {
    RedBlackTree<Integer> rb = new RedBlackTree<>();
    Random random = new Random();
    int range = 10000;
    for (int i = 0; i < range; ++i) {
      // avl.delete(random.nextInt(range));
      try {
        int n = random.nextInt(range);
        rb.insert(n);
        rb.checkConstraints();
        rb.insert(i);
        rb.checkConstraints();
      } catch (RuntimeException e) {
        e.printStackTrace();
        System.exit(1);;
      }
    }
    rb.stats();
  }

}
