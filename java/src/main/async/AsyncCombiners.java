package async;

/** 
 * Defines interfaces for combining AsyncOperations using AsyncGraph::combine. 
 * Essentially java.util.Function::apply with more arguments.
 * */
public class AsyncCombiners {
  private AsyncCombiners() {}

  public interface AsyncCombiners3<T1, T2, T3, R> {
    R apply(T1 t1, T2 t2, T3 t3);
  }

  public interface AsyncCombiners4<T1, T2, T3, T4, R> {
    R apply(T1 t1, T2 t2, T3 t3, T4 t4);
  }

  public interface AsyncCombiners5<T1, T2, T3, T4, T5, R> {
    R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);
  }
}