package async;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class AsyncOperationTest {

  @Test
  public void test_createImmediateAsync() {
    Integer expected = 8;
    long mainThreadId = getThreadId();

    AsyncOperation<Integer> asyncOp = 
        AsyncGraph
            .createImmediateAsync(expected);

    assertResults(
        asyncOp,
        Arrays.asList(
            actual -> assertEquals(actual, expected),
            actual -> assertFalse(getThreadId() == mainThreadId)));      
  }

//   @Test
//   public void test_createVoidAsync() {
//     long mainThreadId = getThreadId();

//     AsyncOperation<Void> asyncOp = 
//         AsyncGraph
//             .createVoidAsync();

//     assertResults(
//         asyncOp,
//         Arrays.asList(
//             actual -> assertNull(actual),
//             actual -> assertFalse(getThreadId() == mainThreadId)));       
//   }

  @Test
  public void test_createAsync() {
    String expected = "string";
    long mainThreadId = getThreadId();
    
    AsyncOperation<String> asyncOp = 
        AsyncGraph
            .createAsync(() -> expected);

    assertResults(
        asyncOp,
        Arrays.asList(
            actual -> assertEquals(actual, expected),
            actual -> assertFalse(getThreadId() == mainThreadId))); 
  }

  @Test
  public void test_then() {
    AsyncOperation<Integer> asyncOp = 
        AsyncGraph
            .createImmediateAsync(5)
            .then(num -> num + 1);

    assertResult(
        asyncOp, 
        actual -> assertEquals(actual, (Integer) 6));
  }

  @Test
  public void test_thenCollapse() {
    String string = "Hello";
    Function<String, AsyncOperation<Integer>> fnReturnAsyncOp =
        str -> 
            AsyncGraph
                .createImmediateAsync(str)
                .then(String::length);

    AsyncOperation<Integer> asyncOp = 
      AsyncGraph
          .createAsync(() -> string)
          .thenCollapse(str -> fnReturnAsyncOp.apply(str));

    assertResult(
        asyncOp, 
        actual -> assertEquals((int) actual, string.length()));
  }

  @Test
  public void test_thenConsume() {
    List<String> strList = new ArrayList<>(Arrays.asList("a", "b", "c"));
    AsyncOperation<Void> asyncOp = 
        AsyncGraph
            .createImmediateAsync("a")
            .thenConsume(str -> strList.remove(str));

    assertResults(
        asyncOp,
        Arrays.asList( 
            actual -> assertArrayEquals(new String[] {"b", "c"}, strList.toArray(new String[2])),
            actual -> assertNull(actual)));
  }

  @Test
  public void test_thenConsumeCollapse() {
    List<String> strList = new ArrayList<>(Arrays.asList("a", "b", "c"));
    Function<String, AsyncOperation<Void>> fnReturnAsyncOp = 
        str -> 
            AsyncGraph
                .createAsync(() -> 
                    {
                        strList.remove(str);
                        return null;
                    });
    
    AsyncOperation<Void> asyncOp = 
        AsyncGraph
            .createAsync(() -> "a")
            .thenConsumeCollapse(str -> fnReturnAsyncOp.apply(str));

    assertResults(
        asyncOp,
        Arrays.asList( 
            actual -> assertArrayEquals(new String[] {"b", "c"}, strList.toArray(new String[2])),
            actual -> assertNull(actual)));
  }

  @Test
  public void test_thenReturnVoid() {
    AsyncOperation<Void> asyncOp = 
        AsyncGraph
            .createImmediateAsync(5)
            .thenReturnVoid();

    assertResult(
        asyncOp, 
        actual -> assertNull(actual));
  }

  @Test
  public void test_peek() {
    List<String> strList = new ArrayList<>(Arrays.asList("a", "b", "c"));
    AsyncOperation<String> asyncOp = 
        AsyncGraph
            .createImmediateAsync("a")
            .peek(str -> strList.remove(str))
            .then(str -> str + "bc");

    assertResults(
        asyncOp,
        Arrays.asList( 
            actual -> assertArrayEquals(new String[] {"b", "c"}, strList.toArray(new String[2])),
            actual -> assertEquals(actual, "abc")));
  }

  @Test
  public void test_peekCollapse() {
    List<String> strList = new ArrayList<>(Arrays.asList("a", "b", "c"));
    
    Function<String, AsyncOperation<Void>> fnReturnAsyncOp = 
        str -> 
            AsyncGraph
                .createAsync(() -> 
                    {
                        strList.remove(str);
                        return null;
                    });

    AsyncOperation<String> asyncOp = 
        AsyncGraph
            .createAsync(() -> "a")
            .peekCollapse(str -> fnReturnAsyncOp.apply(str));

    assertResults(
        asyncOp,
        Arrays.asList( 
            actual -> assertArrayEquals(new String[] {"b", "c"}, strList.toArray(new String[2])),
            actual -> assertEquals(actual, "a")));
  }

  private static <T> void assertResult(AsyncOperation<T> asyncOp, Consumer<T> assertion) {
    assertResults(asyncOp, Arrays.asList(assertion));
  }

  private static <T> void assertResults(AsyncOperation<T> asyncOp, List<Consumer<T>> assertions) {
    AsyncGraph.runAsync(
      asyncOp, 
      actual -> 
          {
              for (Consumer<T> assertion : assertions) {
                assertion.accept(actual);
              }
          });
  }

  private static long getThreadId() {
      return Thread.currentThread().getId();
  }
}