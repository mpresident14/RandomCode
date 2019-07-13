package hashmap;

class Pair<Key, Value> {
  Key key;
  Value value;
  Pair(Key key, Value value) {
    this.key = key;
    this.value = value;
  }

  public Key getKey() {
    return key;
  }

  public Value getValue() {
    return value;
  }
}