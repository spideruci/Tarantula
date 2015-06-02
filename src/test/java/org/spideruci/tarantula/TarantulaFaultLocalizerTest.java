package org.spideruci.tarantula;

import static org.junit.Assert.*;
import static org.spideruci.hamcrest.primitive.IsBooleanArrayContaining.*;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.CombinableMatcher.both;

import org.junit.Ignore;
import org.junit.Test;

public class TarantulaFaultLocalizerTest {

  @Test
  public void expectNoBadTestsWhenEachTestCoversAtleastOneStmt() {
    boolean[][] M = getSimpleUniformBoolMatrix(true);
    TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    int numOrigTests = M.length;
    int numStmts = M[0].length;
    
    boolean[] B = localizer.calculateBadTestCoverage(numStmts, numOrigTests, M);
    
    assertThat(B, not(hasTrue()));
  }
  
  @Test
  public void expectAllBadTestsWhenNoTestCoversNoStmts() {
    boolean[][] M = getSimpleUniformBoolMatrix(false);
    TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    int numOrigTests = M.length;
    int numStmts = M[0].length;
    
    boolean[] B = localizer.calculateBadTestCoverage(numStmts, numOrigTests, M);
    
    assertThat(B, not(hasFalse()));
  }
  
  @Test
  public void expectGoodAndBadTestsWhenOneTestCoversNoStmts() {
    boolean[][] M = getSimpleUniformBoolMatrix(true);
    
    TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    int numOrigTests = M.length;
    int numStmts = M[0].length;
    for(int i = 0; i < numStmts; i += 1) {
      M[0][i] = false;
    }
    
    
    boolean[] B = localizer.calculateBadTestCoverage(numStmts, numOrigTests, M);
    
    assertThat(B, both(hasFalse()).and(hasTrue()));
  }

  @Test @Ignore
  public void testCalculateTotalLiveFailAndPass() {
    fail("Not yet implemented");
  }

  @Test @Ignore
  public void testCalculateOrigFailAndPass() {
    fail("Not yet implemented");
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
  
  private static boolean[][] getSimpleUniformBoolMatrix(boolean uniformValue) {
    boolean[][] mat = new boolean[][] {
        {uniformValue, uniformValue, uniformValue, uniformValue},
        {uniformValue, uniformValue, uniformValue, uniformValue},
        {uniformValue, uniformValue, uniformValue, uniformValue}
    };
    return mat;
  }

}
