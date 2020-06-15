package other;

/** Hacky way to allow me to set "effectively final" variables in lambdas */
public class Wrapper<T> {
  T obj;

  public T get() {
    return obj;
  }

  public void set(T obj) {
    this.obj = obj;
  }
}
