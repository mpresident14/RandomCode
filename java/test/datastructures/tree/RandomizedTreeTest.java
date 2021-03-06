package datastructures.tree;

import org.junit.Before;
import org.junit.Test;

public class RandomizedTreeTest {
  private RandomizedTree<Integer> tree;

  @Before
  public void setUp() {
    tree = new RandomizedTree<>();
  }

  @Test
  public void insert_rand() {
    BSTTestUtils.insert_rand(tree);
  }

  @Test
  public void insert_dup() {
    BSTTestUtils.insert_dup(tree);
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
