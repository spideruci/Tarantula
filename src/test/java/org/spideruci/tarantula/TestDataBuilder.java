package org.spideruci.tarantula;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.core.IsEqual.*;
import static org.spideruci.hamcrest.primitive.EveryBool.everyBool;

import org.junit.Test;
import org.spideruci.tacoco.coverage.CoverageMatrix;

public class TestDataBuilder {

  @Test
  public void canHandleNulls() {
    //given
    CoverageMatrix matrix = null;
    //when
    TarantulaData data = TarantulaDataBuilder.buildFromCoverageMatrix(matrix);
    //then
    assertNull(data);
  }
  
  @Test
  public void expectThereToBeNoFailingTestsInData_When_ZeroTestsAndStmts() {
    //given
    final int testcount = 0;
    final int stmtcount = 0;
    CoverageMatrix matrix = mock(CoverageMatrix.class);
    when(matrix.toBooleanMatrix()).thenReturn(new boolean[testcount][stmtcount]);
    when(matrix.getCoverableStmts()).thenReturn(new boolean[stmtcount]);
    when(matrix.getTestCount()).thenReturn(testcount);
    //when
    TarantulaData data = TarantulaDataBuilder.buildFromCoverageMatrix(matrix);
    //then
    assertThat(data.getF(), everyBool(equalTo(false)));
  }

  
  @Test
  public void expectThereToBeNoFailingTestsInData_When_NonZeroTestsAndStmts() {
    //given
    final int testcount = 10;
    final int stmtcount = 20;
    CoverageMatrix matrix = mock(CoverageMatrix.class);
    when(matrix.toBooleanMatrix()).thenReturn(new boolean[testcount][stmtcount]);
    when(matrix.getCoverableStmts()).thenReturn(new boolean[stmtcount]);
    when(matrix.getTestCount()).thenReturn(testcount);
    //when
    TarantulaData data = TarantulaDataBuilder.buildFromCoverageMatrix(matrix);
    //then
    assertThat(data.getF(), everyBool(equalTo(false)));
  }
}
