package datastructures.tree;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.junit.*;

public class AVLTreeTest {

  private static List<Integer> L1 = Arrays.asList(7, 4, 3, 2, 9, 12);
  private static List<Integer> L2 = Arrays.asList(-4, 7, 9, 3, 5, 19, 11);

  private static List<Integer> L1_SHUF = new ArrayList<>(L1);
  private static List<Integer> L2_SHUF = new ArrayList<>(L2);

  static {
    Collections.shuffle(L1_SHUF);
    Collections.shuffle(L2_SHUF);
  }

  private AVLTree<Integer> tree;

  @Before
  public void setUp() {
    tree = new AVLTree<>();
  }

  @Test
  public void insert_noDups() {
    tree.insertAll(L1);
    for (Integer n : L1_SHUF) {
      assertTrue(tree.contains(n));
    }
    assertEquals(L1.size(), tree.size());
    checkBalance(tree);
  }

  @Test
  public void insert_withDup() {
    tree.insertAll(L2);
    for (Integer n : L2_SHUF) {
      assertTrue(tree.contains(n));
    }
    assertEquals(L2.size(), tree.size());
    checkBalance(tree);

    assertFalse(tree.insert(9));
    assertTrue(tree.contains(9));
    assertEquals(L2.size(), tree.size());
    checkBalance(tree);

    assertTrue(tree.insert(8));
    assertTrue(tree.contains(8));
    assertEquals(L2.size() + 1, tree.size());
    checkBalance(tree);
  }

  @Test
  public void delete_noDup() {
    tree.insertAll(L1);
    assertEquals(L1.size(), tree.size());
    checkBalance(tree);

    for (Integer n : L1_SHUF) {
      assertTrue(tree.delete(n));
      checkBalance(tree);
    }
    assertEquals(0, tree.size());
  }

  @Test
  public void delete_withDup() {
    tree.insertAll(L2);
    assertFalse(tree.delete(8));
    assertEquals(L2.size(), tree.size());

    for (Integer n : L2_SHUF) {
      assertTrue(tree.delete(n));
      assertFalse(tree.delete(n));
      checkBalance(tree);
    }
    assertEquals(0, tree.size());
  }

  @Test
  public void insertDelete_random() {
    Random random = new Random();
    int range = 1000;
    for (int i = 0; i < range; ++i) {
      int n = random.nextInt(range);
      tree.insert(n);
      assertTrue(tree.contains(n));

      tree.insert(i);
      assertTrue(tree.contains(i));

      n = random.nextInt(range);
      tree.delete(n);
      assertFalse(tree.contains(n));

      checkBalance(tree);
    }
  }

  public <T extends Comparable<T>> void checkBalance(AVLTree<T> avl) {
    if (avl.root == null) {
      return;
    }

    Queue<AVLTree<T>.Node> q = new LinkedList<>();
    q.add(avl.root);
    while (!q.isEmpty()) {
      AVLTree<T>.Node node = q.poll();
      assertTrue(node.isBalanced());
      if (node.left != null) {
        q.add(node.left);
      }
      if (node.right != null) {
        q.add(node.right);
      }
    }
  }
}
