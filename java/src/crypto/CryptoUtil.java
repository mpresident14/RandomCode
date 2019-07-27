package crypto;

public class CryptoUtil {
  private CryptoUtil() {};

  public static String byteArrayToHexString(byte[] bytes) {
    int len = bytes.length;
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < len; i++) {
      builder.append(Integer.toHexString(Byte.toUnsignedInt(bytes[i])));
    }
    return builder.toString();
  }

  public static byte[] hexStringToByteArray(String str) {
    int len = str.length();
    byte[] result = new byte[(len + 1) / 2];
    int i = 0;
    int j = 0;
    
    if (len % 2 == 1) {
      result[0] = (byte) Short.parseShort(str.substring(0, 1), 16);
      i++;
      j++;
    }
    
    for (; i < len; i += 2) {
      // Have to use short and cast to byte because the java byte is 
      // unsigned, and it interprets the string as a positive value, 
      // so it doesn't fit into the positive range of a byte.
      result[j] = (byte) Short.parseShort(str.substring(i, i + 2), 16);
      j++;
    }

    return result;
  }

  public static String byteArrayToAsciiString(byte[] bytes) {
    int len = bytes.length;
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < len; i++) {
      builder.append((char) bytes[i]);
    }
    return builder.toString();
  }

  public static byte[] asciiStringToByteArray(String str) {
    int len = str.length();
    byte[] result = new byte[len];
    for (int i = 0; i < len; i++) {
      result[i] = (byte) str.charAt(i);
    }
    return result;
  }

  /** 
   * Ex: 
   * 0x81e0 XOR 0xaf = 0x2ee0
   */
  public static byte[] xor(byte[] b1, byte[] b2) {
    byte[] longer;
    byte[] shorter;
    if (b1.length > b2.length) {
      longer = b1;
      shorter = b2;
    } else {
      longer = b2;
      shorter = b1;
    }

    byte[] result = new byte[longer.length];
    int i;
    for (i =0; i < shorter.length; i++) {
      result[i] = (byte) (shorter[i] ^ longer[i]);
    }
    for (; i < longer.length; i++) {
      result[i] = longer[i];
    }
    return result;
  }

  /** 
   * Ex: 
   * 0x81e0 XOR 0xaf = 0x2e
   */
  public static byte[] xorTruncate(byte[] b1, byte[] b2) {
    int minSize = b1.length < b2.length ? b1.length : b2.length;
    byte[] result = new byte[minSize];
    for (int i = 0; i < minSize; i++) {
      result[i] = (byte) (b1[i] ^ b2[i]);
    }
    return result;
  }

  public static void printBytes(byte[] b) {
    int len = b.length;
    for (int i = 0; i < len; i++) { 
      System.out.format(" 0x%02X", b[i]); 
    } 
    System.out.println('\n');
  }
}