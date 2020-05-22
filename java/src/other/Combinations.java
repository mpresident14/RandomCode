package other;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/* BIG IDEA
 * 
 * f(list, csize) = {list[0] + each of f(list[1:], csize-1)} +
 *                  {list[1] + each of f(list[2:], csize-1)} +
 *                  ... +
 *                  {list[n-csize] + each of f(list[n-csize+1], csize-1)}
 * 
 * Example: 
 * f([1,2,3,4,5], 2) = {1 + each of f([2,3,4,5], 1)} +
 *                     {2 + each of f([3,4,5], 1)} +
 *                     {3 + each of f([4,5], 1)} +
 *                     {4 + each of f([5], 1)}
 */


class Combinations {

  public static List<List<Integer>> getCombinations(List<Integer> list, int comboSize) {
    List<List<Integer>> result = new ArrayList<>();
    if (comboSize == 0) {
      result.add(new ArrayList<>());
      return result;
    }

    for (int i = 0; i <= list.size() - comboSize; i++) {
      List<List<Integer>> round = getCombinations(list.subList(i + 1, list.size()), comboSize - 1);
      for (int j = 0; j < round.size(); j++) {
        round.get(j).add(list.get(i));
      }
      result.addAll(round);
    }
    return result;
  }

  public static void main(String[] args) {
    List<Integer> list = Arrays.asList(1,2,3,4,5);
    List<List<Integer>> combos = getCombinations(list, 3);
    System.out.println(combos);
  }
}
