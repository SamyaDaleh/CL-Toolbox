package com.github.samyadaleh.cltoolbox.chartparsing.item;

public class Pair<K, V> {

  private final K key;
  private final V value;

  public Pair(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public K getFirst() {
    return key;
  }

  public V getSecond() {
    return value;
  }

  @Override public String toString() {
    return getFirst() + " : " + getSecond();
  }
}

