package algorithms;

import static org.junit.Assert.*;

import org.junit.*;

public class HungarianTest {

  @Test
  public void max3x3() {
    int[][] weights = {
      {108, 125, 180},
      {150, 135, 175},
      {122, 148, 250}
    };

    assertArrayEquals(new int[] {1, 0, 2}, Hungarian.hungarianMax(weights));
  }

  @Test
  public void max4x4() {
    int[][] weights = {
      {72, 47, 71, 95},
      {51, 89, 81, 60},
      {92, 62, 6, 44},
      {19, 71, 20, 19}
    };

    assertArrayEquals(new int[] {3, 2, 0, 1}, Hungarian.hungarianMax(weights));
  }

  @Test
  public void min3x3() {
    int[][] weights = {
      {18, 33, 21},
      {80, 36, 22},
      {68, 76, 53}
    };

    assertArrayEquals(new int[] {0, 1, 2}, Hungarian.hungarianMin(weights));
  }

  @Test
  public void min4x4() {
    int[][] weights = {
      {82, 83, 69, 92},
      {77, 37, 49, 92},
      {11, 69, 5, 86},
      {8, 9, 98, 23}
    };

    assertArrayEquals(new int[] {2, 1, 0, 3}, Hungarian.hungarianMin(weights));
  }
}
