package datastructures.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;

public class RedBlackTreeTest {
  private static RedBlackTree<Integer> tree;
  private static Consumer<Integer> constraintCheck = n -> checkConstraints();

  private static void checkConstraints() {
    assertTrue(tree.isBlack(tree.root));
    blackHeight(tree.root, false);
  }

  private static long blackHeight(RedBlackTree<Integer>.Node node, boolean redParent) {
    if (node == null) {
      return 0;
    }

    boolean isRed = tree.isRed(node);
    assertFalse(redParent && isRed);

    long leftBlkHeight = blackHeight(node.left, isRed);
    long rightBlkHeight = blackHeight(node.right, isRed);
    assertEquals(leftBlkHeight, rightBlkHeight);

    return tree.isBlack(node) ? 1 + leftBlkHeight : leftBlkHeight;
  }

  @Before
  public void setUp() {
    tree = new RedBlackTree<>();
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
