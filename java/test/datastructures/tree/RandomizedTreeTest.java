package datastructures.tree;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.*;

public class RandomizedTreeTest {

  private static List<Integer> L1 = Arrays.asList(7, 4, 3, 2, 9, 12);
  private static List<Integer> L2 = Arrays.asList(-4, 7, 9, 3, 5, 19, 11);

  private static List<Integer> L1_SHUF = new ArrayList<>(L1);
  private static List<Integer> L2_SHUF = new ArrayList<>(L2);

  static {
    Collections.shuffle(L1_SHUF);
    Collections.shuffle(L2_SHUF);
  }

  private RandomizedTree<Integer> tree;

  @Before
  public void setUp() {
    tree = new RandomizedTree<>();
  }

  @Test
  public void insert_noDups() {
    for (Integer n : L1) {
      assertTrue(tree.insert(n));
    }
    for (Integer n : L1_SHUF) {
      assertTrue(tree.contains(n));
    }
    assertEquals(L1.size(), tree.size());
  }

  @Test
  public void insert_withDup() {
    for (Integer n : L2) {
      assertTrue(tree.insert(n));
    }
    for (Integer n : L2_SHUF) {
      assertTrue(tree.contains(n));
    }
    assertEquals(L2.size(), tree.size());

    assertFalse(tree.insert(9));
    assertTrue(tree.contains(9));
    assertEquals(L2.size(), tree.size());

    assertTrue(tree.insert(8));
    assertTrue(tree.contains(8));
    assertEquals(L2.size() + 1, tree.size());
  }

  @Test
  public void delete_noDups() {
    for (Integer n : L1) {
      assertTrue(tree.insert(n));
    }
    assertEquals(L1.size(), tree.size());

    for (Integer n : L1_SHUF) {
      assertTrue(tree.delete(n));
    }
    assertEquals(0, tree.size());
  }

  @Test
  public void delete_withDup() {
    for (Integer n : L2) {
      assertTrue(tree.insert(n));
    }
    assertFalse(tree.delete(8));
    assertEquals(L2.size(), tree.size());

    for (Integer n : L2_SHUF) {
      assertTrue(tree.delete(n));
      assertFalse(tree.delete(n));
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
    }
  }

  @Test
  public void traverse() {
    List<Integer> expected = new ArrayList<>(L1);
    Collections.sort(expected);

    List<Integer> actual = new ArrayList<>();
    tree.insertAll(L1);
    for (Iterator<Integer> iter = tree.iterator(); iter.hasNext();) {
      actual.add(iter.next());
      iter.remove();
    }

    assertEquals(expected, actual);
    assertEquals(0, tree.size());
  }
}
