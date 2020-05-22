package datastructures.tree;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class AVLTest {

  private static List<Integer> L1 = Arrays.asList(7, 4, 3, 2, 9, 12);
  private static List<Integer> L2 = Arrays.asList(-4, 7, 9, 3, 5, 19, 11);
  
  private static List<Integer> L1_SHUF = new ArrayList<>(L1);
  private static List<Integer> L2_SHUF = new ArrayList<>(L2);
  static {
    Collections.shuffle(L1_SHUF);
    Collections.shuffle(L2_SHUF);
  }
  
  private AVL<Integer> avl;

  @Test
  public void test_insert() {
    avl.insert(L1);

    assertEquals(L1.size(), avl.size());
    assertFalse(avl.insert(9));
    assertEquals(L1.size(), avl.size());
    assertTrue(avl.insert(8));
    assertEquals(L1.size() + 1, avl.size());
  }
}