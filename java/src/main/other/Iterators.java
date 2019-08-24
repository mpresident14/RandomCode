package main.other;

import java.util.List;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Iterator;

public class Iterators {
  private Iterators() {}

  public static void main(String[] args) {
    List<Integer> nums = new ArrayList<>(Arrays.asList(1,2,3,4,5,6));
    for (ListIterator<Integer> iter = nums.listIterator(); iter.hasNext();) {
      // Adds element BEFORE cursor position (iter.previous() == added element)
      iter.add(0);
      System.out.println(Integer.toString(iter.previous()));
      iter.next();
      iter.next();
    }
    System.out.println(Arrays.toString(nums.toArray()));

    for (Iterator<Integer> iter = nums.iterator(); iter.hasNext();) {
      iter.next();
      iter.next();
      // Removes last thing returned by iter.next()
      iter.remove();
    }
    System.out.println(Arrays.toString(nums.toArray()));
  }
}