package datastructures.tree;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.*;

public class GenericBSTTest {

  private static List<Integer> L1 = Arrays.asList(7, 4, 3, 2, 9, 12);
  private static List<Integer> L2 = Arrays.asList(-4, 7, 9, 3, 5, 19, 11);

  private static List<Integer> L1_SHUF = new ArrayList<>(L1);
  private static List<Integer> L2_SHUF = new ArrayList<>(L2);

  static {
    Collections.shuffle(L1_SHUF);
    Collections.shuffle(L2_SHUF);
  }

  private GenericBST<Integer> bst;

  @Before
  public void setUp() {
    bst = new GenericBST<>();
  }

  @Test
  public void test_insert() {
    bst.insertAll(L1);

    assertEquals(L1.size(), bst.size());
    assertFalse(bst.insert(9));
    assertEquals(L1.size(), bst.size());
    assertTrue(bst.insert(8));
    assertEquals(L1.size() + 1, bst.size());

    bst = new GenericBST<>();
    bst.insertAll(L2);

    assertEquals(L2.size(), bst.size());
    assertFalse(bst.insert(9));
    assertEquals(L2.size(), bst.size());
    assertTrue(bst.insert(8));
    assertEquals(L2.size() + 1, bst.size());
  }

  @Test
  public void test_delete() {
    bst.insertAll(L1);

    assertFalse(bst.delete(8));
    assertEquals(L1.size(), bst.size());
    for (Integer n : L1_SHUF) {
      assertTrue(bst.delete(n));
    }
    assertEquals(0, bst.size());

    bst.insertAll(L2);

    assertFalse(bst.delete(8));
    assertEquals(L2.size(), bst.size());
    for (Integer n : L2_SHUF) {
      assertTrue(bst.delete(n));
    }
    assertEquals(0, bst.size());
  }
}
