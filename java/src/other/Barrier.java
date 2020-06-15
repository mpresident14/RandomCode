package other;

import java.util.concurrent.atomic.AtomicBoolean;

public class Barrier {
  private static AtomicBoolean b = new AtomicBoolean();

  public static void fcn() throws InterruptedException {
    Thread t1 = new Thread(() -> printOdds());
    Thread t2 = new Thread(() -> printEvens());
    t1.start();
    t2.start();
    t1.join();
    t2.join();
  }

  private static void printOdds() {
    for (int i = 1; i < 100; i += 2) {
      while (b.get())
        ;
      System.out.println(i);
      b.set(true);
    }
  }

  private static void printEvens() {
    for (int i = 2; i <= 100; i += 2) {
      while (!b.get())
        ;
      System.out.println(i);
      b.set(false);
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Barrier.fcn();
  }
}
