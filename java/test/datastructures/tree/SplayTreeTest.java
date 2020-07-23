package datastructures.tree;

import static org.junit.Assert.assertEquals;

import java.util.function.Consumer;
import org.junit.*;

public class SplayTreeTest {
  private static SplayTree<Integer> tree;
  private static Consumer<Integer> constraintCheck = n -> assertEquals(n, tree.root.val);

  @Before
  public void setUp() {
    tree = new SplayTree<>();
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
    BSTTestUtils.delete_rand(tree);
  }

  @Test
  public void delete_withDup() {
    BSTTestUtils.delete_dup(tree);
  }

  @Test
  public void traverse() {
    BSTTestUtils.traverse(tree);
  }
}
