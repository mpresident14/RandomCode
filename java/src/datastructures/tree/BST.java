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
  static int r = 0;

  public BST() {
    this.root = null;
    this.size = 0;
  }

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

  public boolean delete(T value) {
    boolean isDeleted = delete(value, root, null);
    if (isDeleted) {
      this.size--;
    }
    return isDeleted;
  }

  private boolean delete(T value, Node current, Node parent) {
    if (current == null) {
      return false;
    }

    int comp = value.compareTo(current.val);
    if (comp == 0) {
      // Node to remove is a leaf, just remove it
      if (isLeaf(current)) {
        setParentChildToNodeChild(parent, current, null);
        return true;
      }

      // Node to remove has one child, set parent to child
      if (current.left == null) {
        setParentChildToNodeChild(parent, current, current.right);
        return true;
      }
      if (current.right == null) {
        setParentChildToNodeChild(parent, current, current.left);
        return true;
      }

      // If node to remove has two children, find inorder successor (right
      // once, then continuously left), copy it into the place of node to be
      // removed, and delete inorder successor. This could equivalently be
      // done with inorder predecessor: left once, then continuously right.
      Node nextInorderParent = current;
      Node nextInorder = current.right;
      while (nextInorder.left != null) {
        nextInorderParent = nextInorder;
        nextInorder = nextInorder.left;
      }
      current.val = nextInorder.val;
      return delete(nextInorder.val, nextInorder, nextInorderParent);

      } else if (comp < 0) {
      return delete(value, current.left, current);
    } else {
      return delete(value, current.right, current);
    }
  }

  private void setParentChildToNodeChild(Node parent, Node node, Node nodeChild) {
    if (parent == null) {
      this.root = nodeChild;
    } else if (parent.left != null && parent.left.val.equals(node.val)) {
      parent.left = nodeChild;
    } else {
      parent.right = nodeChild;
    }
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

  private boolean isLeaf(Node node) {
    return node.left == null && node.right == null;
  }
}
