package org.spideruci.tarantula;

import static org.junit.Assert.*;
import static org.spideruci.hamcrest.primitive.IsBooleanArrayContaining.*;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.CombinableMatcher.both;

import static org.spideruci.tarantula.MatrixStubs.*;


import org.junit.Ignore;
import org.junit.Test;

public class TarantulaFaultLocalizerTest {

  @Test
  public void expect_NoBadTests_When_EachTestCovers_AtleastOneStmt() {
    //given
    final int numOrigTests = 3;
    final int numStmts = 4;
    boolean[][] M = getSimpleUniformBoolMatrix(true, numOrigTests, numStmts);
    TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    //when
    boolean[] B = localizer.calculateBadTestCoverage(numStmts, numOrigTests, M);
    //then
    assertThat(B, not(hasTrue()));
  }
  
  @Test
  public void expect_AllBadTests_When_NoTestCovers_NoStmts() {
    //given
    final int numOrigTests = 3;
    final int numStmts = 4;
    boolean[][] M = getSimpleUniformBoolMatrix(false, numOrigTests, numStmts);
    TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    //when
    boolean[] B = localizer.calculateBadTestCoverage(numStmts, numOrigTests, M);
    //then
    assertThat(B, not(hasFalse()));
  }
  
  @Test
  public void expect_BothGoodAndBadTests_When_OneTestCovers_NoStmts() {
    //given
    final int numOrigTests = 3;
    final int numStmts = 4;
    boolean[][] M = getSimpleUniformBoolMatrix(true, numOrigTests, numStmts);
    TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    for(int i = 0; i < numStmts; i += 1) {
      M[0][i] = false;
    }
    //when
    boolean[] B = localizer.calculateBadTestCoverage(numStmts, numOrigTests, M);
    //then
    assertThat(B, both(hasFalse()).and(hasTrue()));
  }

  @Test @Ignore
  public void testCalculatePassOnStmtAndFailOnStmt() {
    fail("Not yet implemented");
  }

  @Test @Ignore
  public void testCalculatePassRatioAndFailRatio() {
    fail("Not yet implemented");
  }

  @Test @Ignore
  public void testCalculateSuspiciousnessAndConfidence() {
    fail("Not yet implemented");
  }
  


}
