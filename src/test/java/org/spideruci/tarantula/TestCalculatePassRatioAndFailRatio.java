package org.spideruci.tarantula;

import static org.junit.Assert.*;
import static org.spideruci.tarantula.MatrixStubs.*;
import static org.spideruci.hamcrest.primitive.EveryDouble.everyDouble;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.Test;

public class TestCalculatePassRatioAndFailRatio {

  @Test
  public void expect_AllPassRatioOnes_AllFailRatioZeros_When_AllTestsPass() {
    //given
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    final int numTests = 5;
    final int numStmts = 7;
    final int totalLivePass = numTests;
    final int totalLiveFail = numTests - totalLivePass;
    final int[] passOnStmt = getSimpleUniformIntArray(totalLivePass, numStmts);
    final int[] failOnStmt = getSimpleUniformIntArray(totalLiveFail, numStmts);
    //when
    PassFailPair<double[]> pair = 
        localizer.calculatePassRatioAndFailRatio(
            numStmts, totalLivePass, totalLiveFail, passOnStmt, failOnStmt);
    //then
    assertThat(pair.pass(), everyDouble(equalTo(1.0)));
    assertThat(pair.fail(), everyDouble(equalTo(0.0)));
  }
  
  @Test
  public void expect_AllFailRatioOnes_AllPassRatioZeros_When_AllTestsFail() {
    //given
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    final int numTests = 5;
    final int numStmts = 7;
    final int totalLivePass = 0;
    final int totalLiveFail = numTests - totalLivePass;
    final int[] passOnStmt = getSimpleUniformIntArray(totalLivePass, numStmts);
    final int[] failOnStmt = getSimpleUniformIntArray(totalLiveFail, numStmts);
    //when
    PassFailPair<double[]> pair = 
        localizer.calculatePassRatioAndFailRatio(
            numStmts, totalLivePass, totalLiveFail, passOnStmt, failOnStmt);
    //then
    assertThat(pair.pass(), everyDouble(equalTo(0.0)));
    assertThat(pair.fail(), everyDouble(equalTo(1.0)));
  }
  
  @Test
  public void expect_PassAndFailRatiosAsOne_When_EqualTestsPassAndFail_AllTestsCoverAllStmts_AllTestLiveGood() {
    //given
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    final int numTests = 4;
    final boolean[] B = getSimpleUniformBoolArray(false, numTests);
    final boolean[] L = getSimpleUniformBoolArray(true, numTests);
    
    // and, passing half the tests
    final boolean[] F = getSimpleUniformBoolArray(false, numTests);
    for(int i = 0; i < F.length/2; i += 1) {
      F[i] = true;
    }
    
    //and, computed total live passing and failing tests
    PassFailPair<Integer> liveCountPair = 
        localizer.calculateTotalLiveFailAndPass(numTests, B, L, F);
    final int totalLivePass = liveCountPair.pass();
    final int totalLiveFail = liveCountPair.fail();
    
    //and, computing passOn- and failOn- counts for each stmt
    final int numStmts = 7;
    final boolean[] C = getSimpleUniformBoolArray(true, numStmts);
    final boolean[][] M = getSimpleUniformBoolMatrix(true, numTests, numStmts);
    PassFailPair<int[]> passAndFailOnStmtPair =
        localizer.calculatePassOnStmtAndFailOnStmt(numStmts, numTests, B, L, C, M, F);
    final int[] passOnStmt = passAndFailOnStmtPair.pass();
    final int[] failOnStmt = passAndFailOnStmtPair.fail();
    
    //when
    PassFailPair<double[]> pair = 
        localizer.calculatePassRatioAndFailRatio(
            numStmts, totalLivePass, totalLiveFail, passOnStmt, failOnStmt);
    //then
    assertThat(pair.pass(), everyDouble(equalTo(1.0)));
    assertThat(pair.fail(), everyDouble(equalTo(1.0)));
  }
  
  @Test
  public void expect_PassAndFailRatiosAsOne_When_2TestsPassAnd1TestFails_AllTestsCoverAllStmts_AllTestLiveGood() {
    //given
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    final int numTests = 3;
    final boolean[] B = getSimpleUniformBoolArray(false, numTests);
    final boolean[] L = getSimpleUniformBoolArray(true, numTests);
    
    // and, 2 Tests Pass, 1 Test Fails
    final boolean[] F = getSimpleUniformBoolArray(false, numTests);
    F[0] = true;
    
    //and, computed total live passing and failing tests
    PassFailPair<Integer> liveCountPair = 
        localizer.calculateTotalLiveFailAndPass(numTests, B, L, F);
    final int totalLivePass = liveCountPair.pass();
    final int totalLiveFail = liveCountPair.fail();
    
    //and, computing passOn- and failOn- counts for each stmt
    final int numStmts = 7;
    final boolean[] C = getSimpleUniformBoolArray(true, numStmts);
    final boolean[][] M = getSimpleUniformBoolMatrix(true, numTests, numStmts);
    PassFailPair<int[]> passAndFailOnStmtPair =
        localizer.calculatePassOnStmtAndFailOnStmt(numStmts, numTests, B, L, C, M, F);
    final int[] passOnStmt = passAndFailOnStmtPair.pass();
    final int[] failOnStmt = passAndFailOnStmtPair.fail();
    
    //when
    PassFailPair<double[]> pair = 
        localizer.calculatePassRatioAndFailRatio(
            numStmts, totalLivePass, totalLiveFail, passOnStmt, failOnStmt);
    //then
    assertThat(pair.pass(), everyDouble(equalTo(1.0)));
    assertThat(pair.fail(), everyDouble(equalTo(1.0)));
  }

}
