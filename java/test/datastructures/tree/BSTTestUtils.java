package datastructures.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;
// import java.util.stream.Collector;

public class BSTTestUtils {
  private static long seed = new Random().nextLong();
  private static Random random = new Random(seed);

  private static List<Integer> LRAND = random.ints(100).boxed().collect(Collectors.toList());
  private static List<Integer> LDUPS = Arrays.asList(-4, 7, 9, 3, 5, 19, 11);

  private static List<Integer> LRAND_SHUF = new ArrayList<>(LRAND);
  private static List<Integer> LDUPS_SHUF = new ArrayList<>(LDUPS);

  private static Consumer<Integer> DO_NOTHING = n -> {};

  static {
    System.out.println("SEED: " + seed);
    Collections.shuffle(LDUPS_SHUF);
    Collections.shuffle(LRAND_SHUF);
  }

  static <Node extends BST<Integer, Node>.Node> void insert_rand(BST<Integer, Node> tree) {
    insert_rand(tree, DO_NOTHING);
  }

  static <Node extends BST<Integer, Node>.Node> void insert_rand(
      BST<Integer, Node> tree, Consumer<Integer> constraintCheck) {
    for (Integer n : LRAND) {
      assertTrue(tree.insert(n));
      constraintCheck.accept(n);
    }
    for (Integer n : LRAND_SHUF) {
      assertTrue(tree.contains(n));
      constraintCheck.accept(n);
    }
    assertEquals(LRAND.size(), tree.size());
  }

  static <Node extends BST<Integer, Node>.Node> void insert_dup(BST<Integer, Node> tree) {
    insert_dup(tree, DO_NOTHING);
  }

  static <Node extends BST<Integer, Node>.Node> void insert_dup(
      BST<Integer, Node> tree, Consumer<Integer> constraintCheck) {
    for (Integer n : LDUPS) {
      assertTrue(tree.insert(n));
      constraintCheck.accept(n);
    }
    for (Integer n : LDUPS_SHUF) {
      assertTrue(tree.contains(n));
      constraintCheck.accept(n);
    }
    assertEquals(LDUPS.size(), tree.size());

    assertFalse(tree.insert(9));
    constraintCheck.accept(9);
    assertTrue(tree.contains(9));
    constraintCheck.accept(9);
    assertEquals(LDUPS.size(), tree.size());
  }

  static <Node extends BST<Integer, Node>.Node> void delete_rand(BST<Integer, Node> tree) {
    delete_rand(tree, DO_NOTHING);
  }

  static <Node extends BST<Integer, Node>.Node> void delete_rand(
      BST<Integer, Node> tree, Consumer<Integer> constraintCheck) {
    for (Integer n : LRAND) {
      assertTrue(tree.insert(n));
      constraintCheck.accept(n);
    }
    assertEquals(LRAND.size(), tree.size());

    for (Integer n : LRAND_SHUF) {
      assertTrue(tree.delete(n));
      constraintCheck.accept(n);
    }
    assertEquals(0, tree.size());
  }

  static <Node extends BST<Integer, Node>.Node> void delete_dup(BST<Integer, Node> tree) {
    delete_dup(tree, DO_NOTHING);
  }

  static <Node extends BST<Integer, Node>.Node> void delete_dup(
      BST<Integer, Node> tree, Consumer<Integer> constraintCheck) {
    for (Integer n : LDUPS) {
      assertTrue(tree.insert(n));
      constraintCheck.accept(n);
    }
    assertFalse(tree.delete(8));
    constraintCheck.accept(8);
    assertEquals(LDUPS.size(), tree.size());

    for (Integer n : LDUPS_SHUF) {
      assertTrue(tree.delete(n));
      constraintCheck.accept(n);
      assertFalse(tree.delete(n));
      constraintCheck.accept(n);
    }
    assertEquals(0, tree.size());
  }

  static <Node extends BST<Integer, Node>.Node> void traverse(BST<Integer, Node> tree) {
    List<Integer> expected = new ArrayList<>(LRAND);
    Collections.sort(expected);

    List<Integer> actual = new ArrayList<>();
    tree.insertAll(LRAND);
    for (var iter = tree.iterator(); iter.hasNext(); ) {
      actual.add(iter.next());
      iter.remove();
    }

    assertEquals(expected, actual);
    assertEquals(0, tree.size());
  }
}
