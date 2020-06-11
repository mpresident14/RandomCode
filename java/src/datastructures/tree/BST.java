package datastructures.tree;

public class BST<T extends Comparable<T>> {
  private class Node {
    T val;
    Node left;
    Node right;

    Node(T val) {
      this.val = val;
      this.left = null;
      this.right = null;
    }
  }

  Node root;
  int size;

  public void insert(Iterable<? extends T> iterable) {
    for (T value : iterable) {
      insert(value);
    }
  }

  public boolean insert(T value) {
    if (root == null) {
      root = new Node(value);
      this.size++;
      return true;
    }

    Node currentNode = root;
    while (true) {
      int comp = value.compareTo(currentNode.val);
      if (comp == 0) {
        return false;
      } else if (comp < 0) {
        if (currentNode.left == null) {
          currentNode.left = new Node(value);
          this.size++;
          return true;
        } else {
          currentNode = currentNode.left;
        }
      } else {
        if (currentNode.right == null) {
          currentNode.right = new Node(value);
          this.size++;
          return true;
        } else {
          currentNode = currentNode.right;
        }
      }
    }
  }

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

  /*
   * Return the minimum value in the subtree rooted by node
   */
  private T minValue(Node node) {
    Node current = node;
    while (current.left != null) {
      current = current.left;
    }
    return current.val;
  }

  public String toString() {
    return toStringHelper(root, 0);
  }

  public String toStringHelper(Node node, int depth) {
    if (node == null) {
      return "";
    }
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < depth; i++) {
      builder.append("  ");
    }
    builder.append(node.val);
    builder.append('\n');
    builder.append(toStringHelper(node.left, depth + 1));
    builder.append(toStringHelper(node.right, depth + 1));
    return builder.toString();
  }

  public int size() {
    return size;
  }
}
