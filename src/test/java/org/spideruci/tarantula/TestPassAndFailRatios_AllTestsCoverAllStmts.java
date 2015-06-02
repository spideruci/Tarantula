package org.spideruci.tarantula;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.spideruci.hamcrest.primitive.EveryDouble.everyDouble;
import static org.spideruci.tarantula.MatrixStubs.getSimpleUniformBoolArray;
import static org.spideruci.tarantula.MatrixStubs.getSimpleUniformBoolMatrix;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestPassAndFailRatios_AllTestsCoverAllStmts {
  
  @Parameters
  public static Collection<Object[]> data() {
    ArrayList<Object[]> counters = new ArrayList<Object[]>();
    int[] array = new int[] {10, 20, 30, 40, 50};
    for(int i : array ) {
      counters.add(new Object[] {i, 1});
      counters.add(new Object[] {i, i/2});
      counters.add(new Object[] {i, i - 1});
    }
    
    return counters;
  }
  
  private int totalTestCount;
  private int failingTestCount;
  
  public TestPassAndFailRatios_AllTestsCoverAllStmts(
      int totalTestCount, int failingTestCount) {
    this.totalTestCount = totalTestCount;
    this.failingTestCount = failingTestCount;
  }

  @Test
  public void expect_PassAndFailRatios_Equals_One_When_NonZeroTestsFailAndPass_AllTestsCoverAllStmts_AllTestLiveGood() {
    //given
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    final int numTests = totalTestCount;
    final boolean[] B = getSimpleUniformBoolArray(false, numTests);
    final boolean[] L = getSimpleUniformBoolArray(true, numTests);
    
    // and, Tests Pass, except 1
    final boolean[] F = getSimpleUniformBoolArray(false, numTests);
    for(int i = 0; i < failingTestCount; i += 1) {
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

}
