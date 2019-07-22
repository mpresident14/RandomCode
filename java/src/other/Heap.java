package other;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Collection;
import java.util.Arrays;

/** Min Heap (or custom Comparator) */
class Heap<T> {
  private Comparator<? super T> comparator;
  private List<T> arr;

  @SuppressWarnings("unchecked")
  public Heap() {
    comparator = (a, b) -> ((Comparable<? super T>) a).compareTo(b);
    arr = new ArrayList<>();
  }

  public Heap(Comparator<? super T> comparator) {
    this.comparator = comparator;
    arr = new ArrayList<>();
  }

  public void insert(T item) {
    arr.add(item);
    bubbleUp(item, arr.size() - 1);
  }

  public void insert(Collection<? extends T> items) {
    for (T item : items) {
      insert(item);
    }
  }

  public T getTop() {
    if (arr.isEmpty()) {
      return null;
    }

    if (arr.size() == 1) {
      return arr.remove(0);
    }

    // Grab root, put last node as the new root, bubble it down
    T top = arr.get(0);
    T last = arr.remove(arr.size() - 1);
    arr.set(0, last);
    bubbleDown(last, 0);
    return top;
  }

  /** Continually swap child with parent if child is greater. */
  private void bubbleUp(T child, int childIndex) {
    T parent = getParent(childIndex);
    while (parent != null && comparator.compare(child, parent) < 0) {
      swapChildWithParent(childIndex);
      childIndex = (childIndex - 1) / 2;
      parent = getParent(childIndex);
    }
  }

  /** Continually swap parent with greater child if parent is less. */
  private void bubbleDown(T parent, int parentIndex) {
    Integer swapChildIndex = getSwapChildIndex(parent, parentIndex);
    while (swapChildIndex != null) {
      swapChildWithParent(swapChildIndex);
      swapChildIndex = getSwapChildIndex(parent, swapChildIndex);
    }
  }

  /** 
   * Determine if lesser child is less than parent and return
   * its index. Return null if no such child.
   */
  private Integer getSwapChildIndex(T parent, int parentIndex) {
    Integer childIndex = null;
    T left = getLeftChild(parentIndex);
    T right = getRightChild(parentIndex);
    T lesserChild = null;
    // If left is null, right is also null b/c tree is balanced
    if (left == null) {
      return null;
    }

    if (right == null || comparator.compare(left, right) < 0) {
      lesserChild = left;
      childIndex = 2 * parentIndex + 1;
    } else {
      lesserChild = right;
      childIndex = 2 * parentIndex + 2;
    }

    // Return null if parent is less than the lesser child
    return comparator.compare(parent, lesserChild) < 0 ? null : childIndex;
  }

  private void swapChildWithParent(int childIndex) {
    int parentIndex = (childIndex - 1) / 2;
    T temp = arr.get(parentIndex);
    arr.set(parentIndex, arr.get(childIndex));
    arr.set(childIndex, temp);
  }

  private T getParent(int index) {
    if (index == 0) {
      return null;
    }
    return arr.get((index - 1) / 2);
  }

  private T getLeftChild(int index) {
    int childIndex = 2 * index + 1;
    return childIndex < arr.size() ? arr.get(childIndex) : null;
  }

  private T getRightChild(int index) {
    int childIndex = 2 * index + 2;
    return childIndex < arr.size() ? arr.get(childIndex) : null;
  }

  public static void main(String[] args) {
    List<Integer> nums = new ArrayList<>(Arrays.asList(4,3,6,10,7));
    // Least to greatest
    Heap<Integer> heap = new Heap<>();
    heap.insert(nums);
    Integer top;
    while ((top = heap.getTop()) != null) {
      System.out.println(top);
    }
    System.out.println();

    // Greatest to least
    heap = new Heap<>((a, b) -> Integer.compare(b, a));
    heap.insert(nums);
    while ((top = heap.getTop()) != null) {
      System.out.println(top);
    }
  }
}