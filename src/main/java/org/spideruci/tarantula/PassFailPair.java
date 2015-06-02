package org.spideruci.tarantula;

public class PassFailPair<T> {
  
  private final T pass;
  private final T fail;
  
  public PassFailPair(T pass, T fail) {
    this.pass = pass;
    this.fail = fail;
  }
  
  public T pass() {
    return pass;
  }
  
  public T fail() {
    return fail;
  }
  
}
