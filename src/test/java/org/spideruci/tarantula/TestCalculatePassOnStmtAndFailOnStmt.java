package org.spideruci.tarantula;

import static org.junit.Assert.*;
import static org.spideruci.hamcrest.primitive.EveryInt.*;
import static org.spideruci.tarantula.MatrixStubs.*;

import org.junit.Test;

public class TestCalculatePassOnStmtAndFailOnStmt {
  
  @Test
  public void expect_PassFailOnStmtsLength_Equals_NumOfStmts() {
  //given
    final int numTests = 5;
    final int numStmts = 6;
    final boolean[] L = getSimpleUniformBoolArray(false, numTests);
    final boolean[] B = getSimpleUniformBoolArray(false, numTests);
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    //when
    PassFailPair<int[]> pair = 
        localizer.calculatePassOnStmtAndFailOnStmt(
            numStmts, numTests, B, L, null, null, null);
    //then
    assertEquals(numStmts, pair.pass().length);
    assertEquals(numStmts, pair.fail().length);
  }

  @Test
  public void expect_AllZeroPassAndFailOnStmts_When_AllTestsAreDead_And_ZeroTestsAreBad() {
    //given
    final int numTests = 5;
    final int numStmts = 6;
    final boolean[] L = getSimpleUniformBoolArray(false, numTests);
    final boolean[] B = getSimpleUniformBoolArray(false, numTests);
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    //when
    PassFailPair<int[]> pair = 
        localizer.calculatePassOnStmtAndFailOnStmt(
            numStmts, numTests, B, L, null, null, null);
    //then
    assertThat(pair.pass(), everyInt(0));
    assertThat(pair.fail(), everyInt(0));
  }
  
  @Test
  public void expect_AllZeroPassAndFailOnStmts_When_AllTestsAreLive_And_AllTestsAreBad() {
    //given
    final int numTests = 5;
    final int numStmts = 6;
    final boolean[] L = getSimpleUniformBoolArray(true, numTests);
    final boolean[] B = getSimpleUniformBoolArray(true, numTests);
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    //when
    PassFailPair<int[]> pair = 
        localizer.calculatePassOnStmtAndFailOnStmt(
            numStmts, numTests, B, L, null, null, null);
    //then
    assertThat(pair.pass(), everyInt(0));
    assertThat(pair.fail(), everyInt(0));
  }
  
  @Test
  public void expect_AllZeroPassAndFailOnStmts_When_AllTestsAreDead_And_AllTestsAreBad() {
    //given
    final int numTests = 5;
    final int numStmts = 6;
    final boolean[] L = getSimpleUniformBoolArray(false, numTests);
    final boolean[] B = getSimpleUniformBoolArray(true, numTests);
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    //when
    PassFailPair<int[]> pair = 
        localizer.calculatePassOnStmtAndFailOnStmt(
            numStmts, numTests, B, L, null, null, null);
    //then
    assertThat(pair.pass(), everyInt(0));
    assertThat(pair.fail(), everyInt(0));
  }
  
  @Test
  public void expect_AllZeroPassAndFailOnStmts_When_AllLiveGoodTests_And_NoCovbleStmts() {
    //given
    final int numTests = 5;
    final int numStmts = 6;
    final boolean[] L = getSimpleUniformBoolArray(true, numTests);
    final boolean[] B = getSimpleUniformBoolArray(true, numTests);
    final boolean[] C = getSimpleUniformBoolArray(false, numStmts);
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    //when
    PassFailPair<int[]> pair = 
        localizer.calculatePassOnStmtAndFailOnStmt(
            numStmts, numTests, B, L, C, null, null);
    //then
    assertThat(pair.pass(), everyInt(0));
    assertThat(pair.fail(), everyInt(0));
  }
  
  @Test
  public void expect_AllZeroPassAndFailOnStmts_When_AllLiveGoodTests_And_AllCovbleStmts_And_NoStmtCovered() {
    //given
    final int numTests = 5;
    final int numStmts = 6;
    final boolean[] L = getSimpleUniformBoolArray(true, numTests);
    final boolean[] B = getSimpleUniformBoolArray(true, numTests);
    final boolean[] C = getSimpleUniformBoolArray(true, numStmts);
    final boolean[][] M = getSimpleUniformBoolMatrix(false, numTests, numStmts);
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    //when
    PassFailPair<int[]> pair = 
        localizer.calculatePassOnStmtAndFailOnStmt(
            numStmts, numTests, B, L, C, M, null);
    //then
    assertThat(pair.pass(), everyInt(0));
    assertThat(pair.fail(), everyInt(0));
  }
  
  @Test
  public void expect_AllFailOnStmtsZero_When_AllPassingTests_AllTestsLiveGood_AllStmtsCovered() {
    //given
    final int numTests = 5;
    final int numStmts = 6;
    final boolean[] L = getSimpleUniformBoolArray(true, numTests);
    final boolean[] B = getSimpleUniformBoolArray(false, numTests);
    final boolean[] C = getSimpleUniformBoolArray(true, numStmts);
    final boolean[][] M = getSimpleUniformBoolMatrix(true, numTests, numStmts);
    final boolean[] F = getSimpleUniformBoolArray(false, numTests);
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    //when
    PassFailPair<int[]> pair = 
        localizer.calculatePassOnStmtAndFailOnStmt(
            numStmts, numTests, B, L, C, M, F);
    //then
    assertThat(pair.fail(), everyInt(0));
  }
  
  @Test
  public void expect_AllPassOnStmts_Equals_TestCount_When_AllPassingTests_AllTestsLiveGood_AllStmtsCovered() {
    //given
    final int numTests = 5;
    final int numStmts = 6;
    final boolean[] L = getSimpleUniformBoolArray(true, numTests);
    final boolean[] B = getSimpleUniformBoolArray(false, numTests);
    final boolean[] C = getSimpleUniformBoolArray(true, numStmts);
    final boolean[][] M = getSimpleUniformBoolMatrix(true, numTests, numStmts);
    final boolean[] F = getSimpleUniformBoolArray(false, numTests);
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    //when
    PassFailPair<int[]> pair = 
        localizer.calculatePassOnStmtAndFailOnStmt(
            numStmts, numTests, B, L, C, M, F);
    //then
    assertThat(pair.pass(), everyInt(numTests));
  }
  
  @Test
  public void expect_AllPassOnStmtsZero_When_AllFailingTests_AllTestsLiveGood_AllStmtsCovered() {
    //given
    final int numTests = 5;
    final int numStmts = 6;
    final boolean[] L = getSimpleUniformBoolArray(true, numTests);
    final boolean[] B = getSimpleUniformBoolArray(false, numTests);
    final boolean[] C = getSimpleUniformBoolArray(true, numStmts);
    final boolean[][] M = getSimpleUniformBoolMatrix(true, numTests, numStmts);
    final boolean[] F = getSimpleUniformBoolArray(true, numTests);
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    //when
    PassFailPair<int[]> pair = 
        localizer.calculatePassOnStmtAndFailOnStmt(
            numStmts, numTests, B, L, C, M, F);
    //then
    assertThat(pair.pass(), everyInt(0));
  }
  
  @Test
  public void expect_AllFailOnStmts_Equals_TestCount_When_AllFailingTests_AllTestsLiveGood_AllStmtsCovered() {
    //given
    final int numTests = 5;
    final int numStmts = 6;
    final boolean[] L = getSimpleUniformBoolArray(true, numTests);
    final boolean[] B = getSimpleUniformBoolArray(false, numTests);
    final boolean[] C = getSimpleUniformBoolArray(true, numStmts);
    final boolean[][] M = getSimpleUniformBoolMatrix(true, numTests, numStmts);
    final boolean[] F = getSimpleUniformBoolArray(true, numTests);
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    //when
    PassFailPair<int[]> pair = 
        localizer.calculatePassOnStmtAndFailOnStmt(
            numStmts, numTests, B, L, C, M, F);
    //then
    assertThat(pair.fail(), everyInt(numTests));
  }

}
