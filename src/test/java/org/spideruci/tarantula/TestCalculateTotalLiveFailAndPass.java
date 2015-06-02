package org.spideruci.tarantula;

import static org.hamcrest.core.CombinableMatcher.both;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.*;
import static org.spideruci.tarantula.MatrixStubs.getSimpleUniformBoolArray;

import org.junit.Test;

public class TestCalculateTotalLiveFailAndPass {
  
  // all tests are live and no tests are bad

  @Test
  public void expect_ZeroFailingTests_And_AllTestsToPass_WhenNoTestsFail() {
    //given
    final int numTests = 4;
    final boolean[] F = getSimpleUniformBoolArray(false, numTests);
    final boolean[] L = getSimpleUniformBoolArray(true, numTests);
    final boolean[] B = getSimpleUniformBoolArray(false, numTests);
    final TarantulaFaultLocalizer faultlocalizer = new TarantulaFaultLocalizer();
    //when
    PassFailPair<Integer> pair = 
        faultlocalizer.calculateTotalLiveFailAndPass(numTests, B, L, F);
    final int originalPassingTests = pair.pass();
    final int originalFailingTests = pair.fail();
    //then
    assertEquals(0, originalFailingTests);
    assertEquals(numTests, originalPassingTests);
  }
  
  @Test
  public void expect_AllFailingTests_And_NoTestsToPass_WhenAllTestsFail() {
    //given
    final int numTests = 4;
    final boolean[] F = getSimpleUniformBoolArray(true, numTests);
    final boolean[] L = getSimpleUniformBoolArray(true, numTests);
    final boolean[] B = getSimpleUniformBoolArray(false, numTests);
    final TarantulaFaultLocalizer faultlocalizer = new TarantulaFaultLocalizer();
    //when
    PassFailPair<Integer> pair = 
        faultlocalizer.calculateTotalLiveFailAndPass(numTests, B, L, F);
    final int originalPassingTests = pair.pass();
    final int originalFailingTests = pair.fail();
    //then
    assertEquals(numTests, originalFailingTests);
    assertEquals(0, originalPassingTests);
  }
  
  @Test
  public void expect_FailingAndPassingTests_MoreThan0_And_LessThanTestCount_WhenAtLeastOneTestFails() {
    //given
    final int numTests = 4;
    final boolean[] F = getSimpleUniformBoolArray(false, numTests);
    F[0] = true; // first test fails
    final boolean[] L = getSimpleUniformBoolArray(true, numTests);
    final boolean[] B = getSimpleUniformBoolArray(false, numTests);
    final TarantulaFaultLocalizer faultlocalizer = new TarantulaFaultLocalizer();
    //when
    PassFailPair<Integer> pair = 
        faultlocalizer.calculateTotalLiveFailAndPass(numTests, B, L, F);
    final int originalPassingTests = pair.pass();
    final int originalFailingTests = pair.fail();
    //then
    assertThat(originalFailingTests, 
        both(greaterThan(0)).and(lessThan(numTests)));
    assertThat(originalPassingTests, 
        both(greaterThan(0)).and(lessThan(numTests)));
  }
  
  @Test
  public void expect_FailingAndPassingTests_MoreThan0_And_LessThanTestCount_WhenAtLeastOneTestPasses() {
    //given
    final int numTests = 4;
    final boolean[] F = getSimpleUniformBoolArray(true, numTests);
    F[0] = false; // first test passes
    final boolean[] L = getSimpleUniformBoolArray(true, numTests);
    final boolean[] B = getSimpleUniformBoolArray(false, numTests);
    final TarantulaFaultLocalizer faultlocalizer = new TarantulaFaultLocalizer();
    //when
    PassFailPair<Integer> pair = 
        faultlocalizer.calculateTotalLiveFailAndPass(numTests, B, L, F);
    final int originalPassingTests = pair.pass();
    final int originalFailingTests = pair.fail();
    //then
    assertThat(originalFailingTests, 
        both(greaterThan(0)).and(lessThan(numTests)));
    assertThat(originalPassingTests, 
        both(greaterThan(0)).and(lessThan(numTests)));
  }
  
  @Test
  public void expect_OneFailingTest_And_NminusOnePasingTests_WhenOneTestFails() {
    //given
    final int numTests = 4;
    final boolean[] F = getSimpleUniformBoolArray(false, numTests);
    F[0] = true; // first test fails
    final boolean[] L = getSimpleUniformBoolArray(true, numTests);
    final boolean[] B = getSimpleUniformBoolArray(false, numTests);
    final TarantulaFaultLocalizer faultlocalizer = new TarantulaFaultLocalizer();
    //when
    PassFailPair<Integer> pair = 
        faultlocalizer.calculateTotalLiveFailAndPass(numTests, B, L, F);
    final int originalPassingTests = pair.pass();
    final int originalFailingTests = pair.fail();
    //then
    assertEquals(1, originalFailingTests);
    assertEquals(numTests - 1, originalPassingTests);
  }
  
  @Test
  public void expect_OnePassingTest_And_NminusOneFailingTests_WhenOneTestPasses() {
    //given
    final int numTests = 4;
    final boolean[] F = getSimpleUniformBoolArray(true, numTests);
    F[0] = false; // first test fails
    final boolean[] L = getSimpleUniformBoolArray(true, numTests);
    final boolean[] B = getSimpleUniformBoolArray(false, numTests);
    final TarantulaFaultLocalizer faultlocalizer = new TarantulaFaultLocalizer();
    //when
    PassFailPair<Integer> pair = 
        faultlocalizer.calculateTotalLiveFailAndPass(numTests, B, L, F);
    final int originalPassingTests = pair.pass();
    final int originalFailingTests = pair.fail();
    //then
    assertEquals(numTests - 1, originalFailingTests);
    assertEquals(1, originalPassingTests);
  }

}