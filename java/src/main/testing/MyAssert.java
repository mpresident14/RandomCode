package testing;

public class MyAssert {
  private MyAssert() {}

  public static void assertTrue(boolean b) throws Exception {
    if (!b) {
      throw new Exception("Expected true");
    }
  }

  public static void assertTrue(boolean b, String errorMsg) throws Exception {
    if (!b) {
      throw new Exception("Expected true: " + errorMsg);
    }
  }

  public static void assertEquals(Object actual, Object expected) throws Exception {
    if (expected == null || !expected.equals(actual)) {
      throw new Exception("Expected: " + expected + ". Got: " + actual);
    }
  }

  public static void assertEquals(Object actual, Object expected, String errorMsg) throws Exception {
    if (expected == null || !expected.equals(actual)) {
      throw new Exception("Expected: " + expected + ". Got: " + actual + ". " + errorMsg);
    }
  }
}