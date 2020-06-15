package datastructures;

import java.util.LinkedList;
import java.util.Queue;

class RunningAverageQueue {

  private static int MAXSIZE = 5;
  private double mAvg = 0;
  private int mSize = 0;
  private Queue<Integer> mQ = new LinkedList<>();

  public void add(int n) {
    mQ.offer(n);
    if (mSize == MAXSIZE) {
      mAvg += (n - mQ.remove()) * 1.0 / mSize;
    } else {
      mSize++;
      mAvg += (n - mAvg) / mSize;
    }
  }

  public double getAvg() {
    return mAvg;
  }

  public static void main(String[] args) {
    RunningAverageQueue myQueue = new RunningAverageQueue();
    myQueue.add(1);
    System.out.println("avg = " + myQueue.getAvg());
    myQueue.add(2);
    System.out.println("avg = " + myQueue.getAvg());
    myQueue.add(3);
    System.out.println("avg = " + myQueue.getAvg());
    myQueue.add(4);
    System.out.println("avg = " + myQueue.getAvg());
    myQueue.add(5);
    System.out.println("avg = " + myQueue.getAvg());
    myQueue.add(7);
    System.out.println("avg = " + myQueue.getAvg());
  }
}
