package datastructures.tree;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;

public class AVLTreeTest {
  private static AVLTree<Integer> tree;
  private static Consumer<Integer> constraintCheck = n -> checkBalance();

  private static void checkBalance() {
    if (tree.root == null) {
      return;
    }

    Queue<AVLTree<Integer>.Node> q = new LinkedList<>();
    q.add(tree.root);
    while (!q.isEmpty()) {
      AVLTree<Integer>.Node node = q.poll();
      assertTrue(node.isBalanced());
      if (node.left != null) {
        q.add(node.left);
      }
      if (node.right != null) {
        q.add(node.right);
      }
    }
  }

  @Before
  public void setUp() {
    tree = new AVLTree<>();
  }

  @Test
  public void insert_rand() {
    BSTTestUtils.insert_rand(tree, constraintCheck);
  }

  @Test
  public void insert_dup() {
    BSTTestUtils.insert_dup(tree, constraintCheck);
  }

  @Test
  public void delete_rand() {
    BSTTestUtils.delete_rand(tree, constraintCheck);
  }

  @Test
  public void delete_withDup() {
    BSTTestUtils.delete_dup(tree, constraintCheck);
  }

  @Test
  public void traverse() {
    BSTTestUtils.traverse(tree);
  }
}
