package datastructures.tree;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.*;

public class TreapTest {

  private static List<Integer> L1 = Arrays.asList(7, 4, 3, 2, 9, 12);
  private static List<Integer> L2 = Arrays.asList(-4, 7, 9, 3, 5, 19, 11);

  private static List<Integer> L1_SHUF = new ArrayList<>(L1);
  private static List<Integer> L2_SHUF = new ArrayList<>(L2);

  static {
    Collections.shuffle(L1_SHUF);
    Collections.shuffle(L2_SHUF);
  }

  private Treap<Integer> tree;

  @Before
  public void setUp() {
    tree = new Treap<>();
  }

  @Test
  public void insert_noDups() {
    tree.insertAll(L1);
    for (Integer n : L1_SHUF) {
      assertTrue(tree.contains(n));
    }
    assertEquals(L1.size(), tree.size());
  }

  @Test
  public void insert_withDup() {
    tree.insertAll(L2);
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
  public void delete_noDup() {
    tree.insertAll(L1);
    assertEquals(L1.size(), tree.size());

    for (Integer n : L1_SHUF) {
      assertTrue(tree.delete(n));
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
    }
    assertEquals(0, tree.size());
  }
}
