package datastructures.tree;

class Node<T> {
  T val;
  Node<T> left;
  Node<T> right;

  Node(T val) {
    this.val = val;
    this.left = null;
    this.right = null;
  }
}