package hashmap;

import java.util.List;
import java.util.ArrayList;
import java.lang.Integer;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import other.Pair;
import testing.MyAssert;

public class MapOpenAddressing<Key, Value> {
  public enum AddressingType {
    LINEAR,
    QUADRATIC,
    DOUBLE_HASHING
  }
  
  public interface TriFunction<Key> {
    int call(int hash, int hash2, int attempts);
  }

  private static final int INITIAL_BUCKETS = 8;
  private double loadFactor;
  private int collisions;
  private int size;
  private List<Pair<Key, Value>> buckets;
  private TriFunction<Key> getBucketNum;
  private AddressingType type;

  public MapOpenAddressing() {
    this(AddressingType.DOUBLE_HASHING);
  }

  public MapOpenAddressing(double loadFactor) {
    this();
    if (loadFactor > 1) {
      throw new IllegalArgumentException("Load factor must be <= 1");
    }
    this.loadFactor = loadFactor;
  }

  public MapOpenAddressing(AddressingType type) {
    loadFactor = 0.4;
    collisions = 0;
    this.size = 0;
    this.type = type;
    buckets = new ArrayList<>(INITIAL_BUCKETS);
    for (int i = 0; i < INITIAL_BUCKETS; i++) {
      buckets.add(null);
    }

    switch(type) {
      case LINEAR:
        getBucketNum = this::getBucketNumLin;
        break;
      case QUADRATIC:
        getBucketNum = this::getBucketNumQuad;
        break;
      default:
        getBucketNum = this::getBucketNumDoubleHash;
    }
  }

  public MapOpenAddressing(double loadFactor, AddressingType type) {
    this(type);
    if (loadFactor > 1) {
      throw new IllegalArgumentException("Load factor must be <= 1");
    }
    this.loadFactor = loadFactor;
  }

  public void put(Key key, Value value) throws Exception {
    int attempts = 0;
    int hash = key.hashCode();
    int hash2 = 0;
    if (type.equals(AddressingType.DOUBLE_HASHING)) {
      hash2 = hash2(hash);
    }
    int bucketNum = getBucketNum.call(hash, hash2, attempts);
    // Search until it finds an empty bucket
    Pair<Key, Value> pair = buckets.get(bucketNum);
    while (pair != null) {
      // Key already in map
      if (pair.first.equals(key)) {
        pair.second = value;
        return;
      }
      collisions++;
      attempts++;
      bucketNum = getBucketNum.call(hash, hash2, attempts);
      pair = buckets.get(bucketNum);      
    }
    
    // Key not in map yet
    buckets.set(bucketNum, new Pair<>(key, value));
    this.size++;
    if ((double) this.size / buckets.size() > loadFactor) {
      reallocate();
    }
  }

  public Value get(Key key) {
    int attempts = 0;
    int hash = key.hashCode();
    int hash2 = 0;
    if (type.equals(AddressingType.DOUBLE_HASHING)) {
      hash2 = hash2(hash);
    }
    Pair<Key, Value> pair = buckets.get(getBucketNum.call(hash, hash2, attempts));
    // Search until it finds the key or reaches an empty bucket
    while (pair != null) {
      if (pair.first.equals(key)) {
        return pair.second;
      }
      attempts++;
      pair = buckets.get(getBucketNum.call(hash, hash2, attempts));
    }
    return null;
  }

  public boolean containsKey(Key key) {
    return get(key) != null;
  }

  public int size() {
    return this.size;
  }

  public int collisions() {
    return collisions;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < buckets.size(); i++) {
      Pair<Key, Value> pair = buckets.get(i);
      builder.append(String.format("%d: ", i));
      if (pair != null) {
        builder.append(String.format("(%s -> %s); ", pair.first, pair.second));
      }
      builder.append('\n');
    }
    return builder.toString();
  }

  private int getBucketNumLin(int hash, int unused, int i) {
    hash = hash + i;
    if (hash < 0) {
      hash += Integer.MAX_VALUE;
    }
    return hash % buckets.size();
  }

  /* Note: I think this could possibly result in an infinite loop */
  private int getBucketNumQuad(int hash, int unused, int i) {
    hash = hash + i*i;
    if (hash < 0) {
      hash += Integer.MAX_VALUE;
    }
    return hash % buckets.size();
  }

  private int getBucketNumDoubleHash(int hash, int hash2, int i) {
    int doubleHash = hash + i * hash2;
    if (doubleHash < 0) {
      doubleHash += Integer.MAX_VALUE;
    }
    return doubleHash % buckets.size();
  }

  /** 
   * For double hashing. Never returns 0.
   * Note: Since our table size is always a power of 2, we make sure this
   * function always returns an odd number. This ensures that it is always
   * relatively prime to the table size, preventing any infinite loops
   * caused by not searching every bucket.
   * 
   * Proof: If (b, h) = 1, then h1 + hx = h1 + hy (mod b) -> x = y (mod b)
   *    Let b = # buckets and h be the value of hash2. Let (b, h) = 1 
   *    and suppose h1 + hx = h1 + hy (mod b), where h1 is the value of the 
   *    first hash. Then hx = hy (mod b) -> x = y (mod b) by Cancellation Thm.
   *    since (b, h) = 1. 
   * 
   * Furthermore, since the number of attempts will always be fewer than 
   * the number of buckets, we know x,y < b. Thus x = y. By the contrapositive, 
   * if (b, h) = 1, then x != y -> h1 + hx != h1 + hy (mod b), so we will 
   * always try every bucket.
   */
  private int hash2(int hash) {
    int result = 7 - (hash % 7);
    return (result & 1) == 1 ? result : result + 1;
  }

  private void reallocate() throws Exception {
    int newSize = 2 * buckets.size(); // power of two, better if prime though
    List<Pair<Key, Value>> oldBuckets = buckets;
    buckets = new ArrayList<>(newSize);
    for (int i = 0; i < newSize; i++) {
      buckets.add(null);
    }

    this.size = 0;
    for (Pair<Key, Value> pair : oldBuckets) {
      if (pair != null) {
        put(pair.first, pair.second);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    int numItems = 100000;
    Random random = new Random();

    /***********
     * Testing *
     ***********/
    for (double d = 0.1; d < 0.6; d += 0.1) {
      for (AddressingType type : AddressingType.values()) {
      
        MapOpenAddressing<Integer, Integer> myMap = new MapOpenAddressing<>(d, type);
        Set<Integer> addedKeys = new HashSet<>(numItems);

        // Test put, get, containsKey, size
        for (int i = 0; i < numItems; i++) {
          int n = random.nextInt() % 1000000;
          myMap.put(n, i);
          addedKeys.add(n);
          // System.out.println(myMap);
          MyAssert.assertTrue(myMap.containsKey(n), "(i = " + i + ")");
          MyAssert.assertEquals(myMap.get(n), i);
          MyAssert.assertEquals(myMap.size(), addedKeys.size(), "(i = " + i + ")");
        }

        // Test duplicate keys
        for (Integer n : addedKeys) {
          myMap.put(n, 0);
          MyAssert.assertEquals(myMap.size(), addedKeys.size());
          MyAssert.assertEquals(myMap.get(n), 0);
        }
        
        // Print Collisions
        System.out.println(String.format("%.1f", d) + ", " + type.toString() + ": " + myMap.collisions() + " collisions");
      }
      System.out.println();
    }  

    /******************
     * Execution time * 
     ******************/ 
    Set<Integer> set = new HashSet<>(numItems);
    for (int i = 0; i < numItems; i++) {
      set.add(random.nextInt() % 1000000);
    }

    MapOpenAddressing<Integer, Integer> mapLinear 
        = new MapOpenAddressing<>(0.33, AddressingType.LINEAR);
    MapOpenAddressing<Integer, Integer> mapQuadratic 
        = new MapOpenAddressing<>(0.33, AddressingType.QUADRATIC);
    MapOpenAddressing<Integer, Integer> mapDouble 
        = new MapOpenAddressing<>(0.33, AddressingType.DOUBLE_HASHING);
    Map<Integer, Integer> mapJava = new HashMap<>();

    // PUT
    System.out.println("PUT");

    // Linear
    long startTime = System.nanoTime();
    for (Integer n : set) {
      mapLinear.put(n, 0);
    }
    long endTime = System.nanoTime();
    System.out.println("Linear: " + (endTime - startTime) * 1.0 / 1000000000);

    // Quadratic
    startTime = System.nanoTime();
    for (Integer n : set) {
      mapQuadratic.put(n, 0);
    }
    endTime = System.nanoTime();
    System.out.println("Quadratic: " + (endTime - startTime) * 1.0 / 1000000000);

    // Double Hashing
    startTime = System.nanoTime();
    for (Integer n : set) {
      mapDouble.put(n, 0);
    }
    endTime = System.nanoTime();
    System.out.println("Double Hashing: " + (endTime - startTime) * 1.0 / 1000000000);
  
    // Java
    startTime = System.nanoTime();
    for (Integer n : set) {
      mapJava.put(n, 0);
    }
    endTime = System.nanoTime();
    System.out.println("Java: " + (endTime - startTime) * 1.0 / 1000000000);
    System.out.println();

    // GET
    System.out.println("GET");

    // Linear
    startTime = System.nanoTime();
    for (Integer n : set) {
      mapLinear.get(n);
    }
    endTime = System.nanoTime();
    System.out.println("Linear: " + (endTime - startTime) * 1.0 / 1000000000);

    // Quadratic
    startTime = System.nanoTime();
    for (Integer n : set) {
      mapQuadratic.get(n);
    }
    endTime = System.nanoTime();
    System.out.println("Quadratic: " + (endTime - startTime) * 1.0 / 1000000000);

    // Double Hashing
    startTime = System.nanoTime();
    for (Integer n : set) {
      mapDouble.get(n);
    }
    endTime = System.nanoTime();
    System.out.println("Double Hashing: " + (endTime - startTime) * 1.0 / 1000000000);
  
    // Java
    startTime = System.nanoTime();
    for (Integer n : set) {
      mapJava.get(n);
    }
    endTime = System.nanoTime();
    System.out.println("Java: " + (endTime - startTime) * 1.0 / 1000000000);
  }
}