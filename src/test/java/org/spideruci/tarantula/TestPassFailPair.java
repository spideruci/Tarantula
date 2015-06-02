package org.spideruci.tarantula;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestPassFailPair {

  @Test
  public void passShouldReturnPassValue() {
    // given
    int passValue = 100;
    PassFailPair<Integer> pair = new PassFailPair<Integer>(passValue, null);
    // when
    int actualPassValue = pair.pass();
    //then
    assertEquals(passValue, actualPassValue);
  }

  @Test
  public void failShouldReturnFailValue() {
    // given
    int failValue = 100;
    PassFailPair<Integer> pair = new PassFailPair<Integer>(null, failValue);
    // when
    int actualFailValue = pair.fail();
    //then
    assertEquals(failValue, actualFailValue);
  }
  
  @Test
  public void passShouldReturnNullWhenPassValueIsNotSet() {
    // given
    PassFailPair<Integer> pair = new PassFailPair<Integer>(null, null);
    // when
    Integer actualPassValue = pair.pass();
    //then
    assertNull(actualPassValue);
  }

  @Test
  public void failShouldReturnNullWhenFailValueIsNotSet() {
    // given
    PassFailPair<Integer> pair = new PassFailPair<Integer>(null, null);
    // when
    Integer actualFailValue = pair.fail();
    //then
    assertNull(actualFailValue);
  }

}
