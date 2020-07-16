package datastructures.tree;

import java.util.Random;

public class Treap<T extends Comparable<T>> extends BST<T, Treap<T>.Node> {
  class Node extends BST<T, Node>.Node {
    private double priority;

    Node(T val, double priority) {
      super(val);
      this.priority = priority;
    }
  }

  private static Random random = new Random();

  public boolean insert(T val) {
    boolean[] inserted = new boolean[1];
    root = insertAtPriority(val, random.nextDouble(), root, inserted);
    if (inserted[0]) {
      ++this.size;
      return true;
    }
    return false;
  }

  /*
   * If the priority is higher than that of current root of this subtree, insert
   * it as the new root of the subtree.
   *
   * - At least one of parent or node will be non-null
   */
  private Node insertAtPriority(T val, double priority, Node node, boolean[] inserted) {
    // NOTE: Could be an issue if priority == node.priority, but not gonna worry
    // about it
    if (node == null || priority > node.priority) {
      return insertAt(val, priority, node, inserted);
    }

    int comp = val.compareTo(node.val);
    if (comp < 0) {
      node.left = insertAtPriority(val, priority, node.left, inserted);
    } else if (comp > 0) {
      node.right = insertAtPriority(val, priority, node.right, inserted);
    }
    return node;
  }

  /*
   * Insert val as the root of the subtree and return the root of the updated
   * subtree
   */
  private Node insertAt(T val, double priority, Node node, boolean[] inserted) {
    if (node == null) {
      inserted[0] = true;
      return new Node(val, priority);
    }

    int comp = val.compareTo(node.val);
    if (comp < 0) {
      // Insert as root of left subtree and rotate right if
      // it was inserted
      Node newLeft = insertAt(val, priority, node.left, inserted);
      if (node.left != newLeft) {
        node.left = newLeft;
        return rotateRight(node); // newLeft
      } else {
        return node;
      }
    } else if (comp > 0) {
      // Insert as root of right subtree and rotate left if
      // it was inserted
      Node newRight = insertAt(val, priority, node.right, inserted);
      if (node.right != newRight) {
        node.right = newRight;
        return rotateLeft(node); // newRight
      } else {
        return node;
      }
    } else {
      // Already in the set
      return node;
    }
  }

  @Override
  public boolean delete(T val) {
    boolean[] deleted = new boolean[1];
    root = deleteRec(val, root, deleted);
    if (deleted[0]) {
      --this.size;
      return true;
    }
    return false;
  }

  /*
   * Delete val from the subtree with node at the root and return the root of the
   * updated subtree
   */
  private Node deleteRec(T val, Node node, boolean[] deleted) {
    if (node == null) {
      return node;
    }

    int comp = val.compareTo(node.val);
    if (comp == 0) {
      deleted[0] = true;
      return deleteAtLeaf(node);
    } else if (comp < 0) {
      node.left = deleteRec(val, node.left, deleted);
      return node;
    } else {
      node.right = deleteRec(val, node.right, deleted);
      return node;
    }
  }

  /**
   * Rotate the subtree currently rooted at node while maintaining the heap structure. If node is a
   * leaf, returns null (deletes it)
   */
  private Node deleteAtLeaf(Node node) {
    if (node.left == null && node.right == null) {
      // We are deleting a leaf node
      return null;
    } else {
      // Rotate max priority child to the root of the subtree, which pushes node
      // towards the leaves
      boolean leftChildHigher =
          node.right == null || (node.left != null && node.left.priority > node.right.priority);
      if (leftChildHigher) {
        Node newRoot = rotateRight(node);
        newRoot.right = deleteAtLeaf(node);
        return newRoot;
      } else {
        Node newRoot = rotateLeft(node);
        newRoot.left = deleteAtLeaf(node);
        return newRoot;
      }
    }
  }

  public static void main(String[] args) {
    Treap<Integer> tree = new Treap<>();
    mainFn(tree);
  }
}
