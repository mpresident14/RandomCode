package datastructures.tree;

import other.Pair;

public class AVL<T extends Comparable<T>> {
  private class Node<R> {
    R val;
    Node<R> left = null;
    Node<R> right = null;
    int height = 1;
  
    Node(R val) {
      this.val = val;
    }
  }

  private Node<T> root = null;
  private int size = 0;

  public void insert(Iterable<? extends T> iterable) {
    for (T value : iterable) {
      insert(value);
    }
  }

  boolean insert(T value) {
    if (root == null) {
      root = new Node<>(value);
      return true;
    }
    if (!insert(root, value)) {
      return false;
    }
    
    Pair<Node<T>, Node<T>> pair = incrementHeightsAndGetUnbalanced(value);
    Node<T> unbalanced = pair.first;
    Node<T> unbalancedParent = pair.second;
    boolean isLeftChild1 = isLeftChild(unbalancedParent, unbalanced);
    
    Node<T> unbalancedChild;
    boolean isLeftChild2;
    if (value.compareTo(unbalanced.val) < 0) {
      unbalancedChild = unbalanced.left;
      isLeftChild2 = true;
    } else {
      unbalancedChild = unbalanced.left;
      isLeftChild2 = false;
    }

    boolean isLeftChild3 = value.compareTo(unbalancedChild.val) < 0;

    if (isLeftChild2) {
      if (isLeftChild3) {
        rotateRight(unbalanced, unbalancedParent, isLeftChild1);
      } else {
        rotateLeft(unbalancedChild, unbalanced, isLeftChild2);
        rotateRight(unbalanced, unbalancedParent, isLeftChild1);
      }
    } else {
      if (isLeftChild3) {
        rotateLeft(unbalanced, unbalancedParent, isLeftChild1);
      } else {
        rotateRight(unbalancedChild, unbalanced, isLeftChild2);
        rotateLeft(unbalanced, unbalancedParent, isLeftChild1);
      }
    }
    return true;
  }

  /** Regular BST insert */
  private boolean insert(Node<T> current, T value) {
    while (true) {
      int comp = value.compareTo(current.val);
      if (comp == 0) {
        return false;
      } else if (comp < 0) {
        if (current.left == null) {
          current.left = new Node<>(value);
          this.size++;
          return true;
        } else {
          current = current.left;
        }
      } else {
        if (current.right == null) {
          current.right = new Node<>(value);
          this.size++;
          return true;
        } else {
          current = current.right;
        }
      }
    }
  }

  /** 
   * Returns first Node that is unbalanced
   * (height of left and right differ by more than one) and its parent.
   * Both are null if no Node is unbalanced.
   */
  private Pair<Node<T>, Node<T>> incrementHeightsAndGetUnbalanced(T value) {
    Node<T> unbalanced = null;
    Node<T> unbalancedParent = null;
    Node<T> current = root;
    Node<T> currentParent = null;

    while (current.val != value) {
      current.height++;
      int comp = value.compareTo(current.val);
      if (comp < 0) {
        if (Math.abs(getHeight(current.left) + 1 - getHeight(current.right)) > 1) {
          unbalanced = current;
          unbalancedParent = currentParent;
        }
        current = current.left;
      } else {
        if (Math.abs(getHeight(current.right) + 1 - getHeight(current.left)) > 1) {
          unbalanced = current;
          unbalancedParent = currentParent;
        }
        current = current.right;
      }
    }

    return new Pair<>(unbalanced, unbalancedParent);
  }

  void rotateRight(Node<T> node, Node<T> parent, boolean isLeftChild) {
    if (node.left == null) {
      return;
    }

    if (node == root) {
      root = node.left;
      node.left = node.left.right;
      root.right = node; 
      updateHeight(node);
      updateHeight(root);
    } else if (isLeftChild) {
      parent.left = node.left;
      node.left = node.left.right;
      parent.left.right = node; 
      updateHeight(node);
      updateHeight(parent.left);
      updateHeight(parent);
    } else {
      parent.right = node.left;
      node.left = node.left.right;
      parent.right.right = node; 
      updateHeight(node);
      updateHeight(parent.right);
      updateHeight(parent);
    }
  }

  void rotateLeft(Node<T> node, Node<T> parent, boolean isLeftChild) {
    if (node.right == null) {
      return;
    }

    if (node == root) {
      root = node.right;
      node.right = node.right.left;
      root.left = node; 
      updateHeight(node);
      updateHeight(root);
    } else if (isLeftChild) {
      parent.left = node.right;
      node.right = node.right.left;
      parent.left.left = node; 
      updateHeight(node);
      updateHeight(parent.left);
      updateHeight(parent);
    } else {
      parent.right = node.right;
      node.right = node.right.left;
      parent.right.left = node; 
      updateHeight(node);
      updateHeight(parent.right);
      updateHeight(parent);
    }
  }

  void updateHeight(Node<T> node) {
    node.height = Math.max(getHeight(node.left), getHeight(node.right)) + 1;
  }

  int getHeight(Node<T> node) {
    return node == null ? 0 : node.height;
  }

  private boolean isLeftChild(Node<T> parent, Node<T> child) {
    return parent.left == child;
  }

  public int size() {
    return this.size();
  }
}