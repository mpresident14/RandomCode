package other;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class MyThreads {

  Thread t1;
  Thread t2;
  List<Integer> list = new ArrayList<Integer>();
  int length = 0;
  Semaphore mutex = new Semaphore(1, true);

  public MyThreads() {
    for (int i = 0; i < 50; ++i) {
      list.add(i);
      length++;
    }

    t1 =
        new Thread() {
          public void run() {
            try {
              fcnToRun();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        };

    t2 =
        new Thread() {
          public void run() {
            try {
              fcnToRun();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        };
  }

  public void fcnToRun() throws InterruptedException {
    for (int i = 0; i < 20; ++i) {
      mutex.acquire();
      list.remove(length - 1);
      length--;
      mutex.release();
    }
  }

  public void go() {
    t1.start();
    t2.start();
  }

  public static void main(String[] args) {
    for (int i = 0; i < 100; ++i) {
      MyThreads myThreads = new MyThreads();
      myThreads.go();
      try {
        myThreads.t1.join();
        myThreads.t2.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      System.out.println("Success");
    }
  }
}
