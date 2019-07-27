package other;

import org.junit.*;
import static org.junit.Assert.*;

import java.beans.Transient;
import java.util.Arrays;
import java.util.Random;


public class OneTimePadBreakerTest {

  private static final Random random = new Random();
  private static final byte[] KEY = new byte[100];
  static {
      random.nextBytes(KEY);
  }
  private static final String MSG1 = "Hello what is going on bro";
  private static final String MSG2 = "coolio i REALLY hope this works";
  private static final String MSG3 = "This is just random text";
  private static final String MSG4 = "What is happening right now";
  private static final String MSG5 = "I really want some chicken teriyaki";

  private static final String C1 = 
      byteArrayToHexString(
          OneTimePadBreaker.xorTruncate(stringToByteArray(MSG1), KEY));
  private static final String C2 = 
      byteArrayToHexString(
          OneTimePadBreaker.xorTruncate(stringToByteArray(MSG2), KEY));
  private static final String C3 = 
      byteArrayToHexString(
          OneTimePadBreaker.xorTruncate(stringToByteArray(MSG3), KEY));
  private static final String C4 = 
      byteArrayToHexString(
          OneTimePadBreaker.xorTruncate(stringToByteArray(MSG4), KEY));
  private static final String C5 = 
      byteArrayToHexString(
          OneTimePadBreaker.xorTruncate(stringToByteArray(MSG5), KEY));
  
          private static final OneTimePadBreaker breaker = 
      new OneTimePadBreaker(new String[] {C1, C2, C3, C4, C5});

  private static final byte[] B1 = new byte[] {(byte) 0xaf, (byte) 0x45, (byte) 0x25, (byte) 0x21, (byte) 0xdc};
  private static final byte[] B2 = new byte[] {(byte) 0x39, (byte) 0x20, (byte) 0x4f, (byte) 0xad, (byte) 0xcb, (byte) 0xff, (byte) 0x35};
  private static final byte[] B1xorB2 = new byte[] {(byte) 0x96, (byte) 0x65, (byte) 0x6a, (byte) 0x8c, (byte) 0x17};
  @Test
  public void test_xorCharIsValidLetter() {
    assertTrue(OneTimePadBreaker.xorCharIsValidLetter((byte) 0x2E, 'A'));
    assertFalse(OneTimePadBreaker.xorCharIsValidLetter((byte) 0xAE, 'A'));
  }

  @Test
  public void test_xorTruncate() {
    byte[] actual = OneTimePadBreaker.xorTruncate(B1, B2);
    assertArrayEquals(actual, B1xorB2);
  }

  @Test
  public void test_getXoredMessages() {
    byte[] actual = OneTimePadBreaker.getXoredMessages(0);
    assertArrayEquals(actual, B1xorB2);
  }

  // @Test
  // public void test_findInstancesOfLetterInMessage() {
  //   for (int i = 0; i < 5; i++) {
  //     char[] decryption = breaker.findInstancesOfLetterInMessage(i);
  //     System.out.println(decryption);
  //     assertTrue(
  //         new String(decryption)
  //             .chars()
  //             .noneMatch(c -> c == ' '));
  //   }
  // }

  private static byte[] stringToByteArray(String str) {
    byte[] result = new byte[str.length()];
    for (int i = 0; i < str.length(); i++) {
      result[i] = (byte) str.charAt(i);
    }
    return result;
  }

  private static String byteArrayToHexString(byte[] b) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < b.length; i++) {
      builder.append(Integer.toHexString(b[i]));
    }

    return builder.toString();
  }
}