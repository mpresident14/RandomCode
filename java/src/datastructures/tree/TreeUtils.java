package datastructures.tree;

public class TreeUtils {
  public static double pbAvgDepth(long n) {
    long pbHeight = (long) Math.floor(Math.log(n) / Math.log(2));
    long pbHeightPow2 = 1 << pbHeight;
    // https://www.wolframalpha.com/input/?i=sum+d*2%5Ed%2C+d+%3D+0+to+h+-+1
    long completeDepthSum = pbHeightPow2 * pbHeight - 2 * (pbHeightPow2 - 1);
    long lastRowDepthSum = (n - pbHeightPow2 + 1) * pbHeight;
    return (completeDepthSum + lastRowDepthSum) * 1.0 / n;
  }
}
