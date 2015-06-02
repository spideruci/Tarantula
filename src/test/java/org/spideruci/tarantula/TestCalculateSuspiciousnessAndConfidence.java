package org.spideruci.tarantula;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.spideruci.hamcrest.primitive.EveryDouble.everyDouble;
import static org.spideruci.tarantula.MatrixStubs.*;

import org.junit.Test;

public class TestCalculateSuspiciousnessAndConfidence {
  
  @Test
  public void expect_ZeroSuspiciousnessAndConfidence_WithNoStatements() {
    //given
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    final int numTests = 0;
    //when
    double[][] snc = 
        localizer.calculateSuspiciousnessAndConfidence(
            numTests, -1, -1, null, null);
    //then
    assertThat(snc[0], everyDouble(equalTo(0.0)));
    assertThat(snc[1], everyDouble(equalTo(0.0)));
  }
  
  @Test
  public void expect_Minus1_SuspiciousnessAndConfidence_WithZeroPassFailCounts() {
    //given
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    final int numTests = 10;
    final int totalLivePass = 0;
    final int totalLiveFail = 0;
    //when
    double[][] snc = 
        localizer.calculateSuspiciousnessAndConfidence(
            numTests, totalLivePass, totalLiveFail, null, null);
    //then
    assertThat(snc[0], everyDouble(equalTo(-1.0)));
    assertThat(snc[1], everyDouble(equalTo(-1.0)));
  }
  
  @Test
  public void expect_ZeroSuspiciousness_WithAllTestsPassing() {
    //given
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    final int numTests = 10;
    final int numStmts = 100;
    final int totalLivePass = 10;
    final int totalLiveFail = 0;
    final double[] passRatio = getSimpleUniformDoubleArray(1.0, numStmts);
    final double[] failRatio = getSimpleUniformDoubleArray(0.0, numStmts);
    //when
    double[][] snc = 
        localizer.calculateSuspiciousnessAndConfidence(
            numTests, totalLivePass, totalLiveFail, passRatio, failRatio);
    //then
    assertThat(snc[0], everyDouble(equalTo(0.0)));
  }
  
  @Test
  public void expect_MaximumSuspiciousness_WithAllTestsFailing() {
    //given
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    final int numTests = 10;
    final int numStmts = 100;
    final int totalLivePass = 0;
    final int totalLiveFail = 10;
    final double[] passRatio = getSimpleUniformDoubleArray(0.0, numStmts);
    final double[] failRatio = getSimpleUniformDoubleArray(1.0, numStmts);
    //when
    double[][] snc = 
        localizer.calculateSuspiciousnessAndConfidence(
            numTests, totalLivePass, totalLiveFail, passRatio, failRatio);
    //then
    assertThat(snc[0], everyDouble(equalTo(1.0)));
  }
  
  @Test
  public void expect_Point5Suspiciousness_WithEqualTestsPassingAndFailing() {
    //given
    final TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    final int numTests = 20;
    final int numStmts = 100;
    final int totalLivePass = 10;
    final int totalLiveFail = 10;
    final double[] passRatio = getSimpleUniformDoubleArray(1.0, numStmts);
    final double[] failRatio = getSimpleUniformDoubleArray(1.0, numStmts);
    //when
    double[][] snc = 
        localizer.calculateSuspiciousnessAndConfidence(
            numTests, totalLivePass, totalLiveFail, passRatio, failRatio);
    //then
    assertThat(snc[0], everyDouble(equalTo(0.5)));
  }
}
