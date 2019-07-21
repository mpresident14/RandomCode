package hashmap;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Random;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import other.Pair;
import testing.MyAssert;

public class MapSeparateChaining<Key, Value> implements Iterable<Pair<Key, Value>> {
  
  private class MapIterator implements Iterator<Pair<Key, Value>> {
    private int bucket;
    private Iterator<Pair<Key, Value>> iter;
    
    private MapIterator() {
      bucket = 0;
      iter = buckets.get(bucket).iterator();
    }

    @Override
    public boolean hasNext() {
      return !(bucket == buckets.size() - 1 && !iter.hasNext());
    }

    @Override
    public Pair<Key, Value> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      if (iter.hasNext()) {
        return iter.next();
      }
      bucket++;
      iter = buckets.get(bucket).iterator();
      return next();
    }
  }

  private static final int INITIAL_BUCKETS = 10;
  private int loadFactor = 3; // Items per bucket, on average
  private List<Queue<Pair<Key, Value>>> buckets;
  private int size; // Total # of items

  public MapSeparateChaining() {
    this.size = 0;
    this.buckets = new ArrayList<>(INITIAL_BUCKETS);
    for (int i = 0; i < INITIAL_BUCKETS; i++) {
      this.buckets.add(new LinkedList<>());
    }
  }

  public MapSeparateChaining(int loadFactor) {
    this();
    this.loadFactor = loadFactor;
  }

  public void put(Key key, Value value) {    
    Queue<Pair<Key, Value>> bucket = buckets.get(getBucketNum(key));
    for (Pair<Key, Value> pair : bucket) {
      // Key already exists, so update value
      if (pair.first.equals(key)) {
        pair.second = value;
        return;
      }
    }

    bucket.add(new Pair<Key, Value>(key, value));
    size++;
    // Avg bucket size is greater than load factor
    if ((double) size / buckets.size() > loadFactor) {
      reallocate();
    }
  }

  public Value get(Key key) {
    Queue<Pair<Key, Value>> bucket = buckets.get(getBucketNum(key));
    for (Pair<Key, Value> pair : bucket) {
      if (pair.first.equals(key)) {
        return pair.second;
      }
    }
    return null;
  }

  public boolean containsKey(Key key) {
    Queue<Pair<Key, Value>> bucket = buckets.get(getBucketNum(key));
    for (Pair<Key, Value> pair : bucket) {
      if (pair.first.equals(key)) {
        return true;
      }
    }
    return false;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < buckets.size(); i++) {
      Queue<Pair<Key, Value>> bucket = buckets.get(i);
      builder.append(String.format("%d: ", i));
      for (Pair<Key, Value> pair : bucket) {
        builder.append(String.format("(%s -> %s); ", pair.first, pair.second));
      }
      builder.append('\n');
    }
    return builder.toString();
  }

  public int size() {
    return size;
  }

  public Iterator<Pair<Key, Value>> iterator() {
    return new MapIterator();
  }

  /** Double the number buckets and readd all the items */
  private void reallocate() {
    List<Queue<Pair<Key, Value>>> oldBuckets = buckets;
    int newSize = buckets.size() * 2;
    buckets = new ArrayList<>(newSize);
    for (int i = 0; i < newSize; i++) {
      this.buckets.add(new LinkedList<>());
    }

    size = 0;
    for (Queue<Pair<Key, Value>> bucket : oldBuckets) {
      for (Pair<Key, Value> pair : bucket) {
        put(pair.first, pair.second);
      }
    }
  }

  private int getBucketNum(Key key) {
    // Hashcode can be a negative number and % return remainder, which
    // can be negative, so we need absolute value
    int hash = key.hashCode();
    return hash > 0 ? 
        hash % buckets.size() : 
        (hash + Integer.MAX_VALUE) % buckets.size();
  }

  
  public static void main(String[] args) throws Exception {
    MapSeparateChaining<Integer, Integer> myMap = new MapSeparateChaining<>(5);
    int numItems = 100;
    Random random = new Random();
    Set<Integer> addedKeys = new HashSet<>(numItems);

    // Test put, get, containsKey, size
    for (int i = 0; i < numItems; i++) {
      int n = random.nextInt();
      myMap.put(n, i);
      addedKeys.add(n);
      // System.out.println(myMap);
      MyAssert.assertTrue(myMap.containsKey(n));
      MyAssert.assertTrue(myMap.get(n).equals(i));
      MyAssert.assertTrue(myMap.size() == i + 1);
    }

    // Test duplicate keys
    for (Integer n : addedKeys) {
      myMap.put(n, 0);
      MyAssert.assertTrue(myMap.size() == numItems);
      MyAssert.assertTrue(myMap.get(n).equals(0));
    }
    
    // Test iterator
    int count = 0;
    for (Pair<Integer, Integer> pair : myMap) {
      MyAssert.assertTrue(addedKeys.contains(pair.first));
      count++;
    }
    MyAssert.assertTrue(count == numItems);
    System.out.println("SUCCESS");
  }
}