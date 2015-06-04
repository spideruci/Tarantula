package org.spideruci.tarantula;

import org.spideruci.tacoco.coverage.CoverageMatrix;

public class TarantulaDataBuilder {
  
  public static TarantulaData buildFromCoverageMatrix(CoverageMatrix matrix) {
    if(matrix == null) return null;
    
    boolean[][] M = matrix.toBooleanMatrix();
    TarantulaData data = new TarantulaData(M);
    
    boolean[] C = matrix.getCoverableStmts();
    data.setC(C);
    
    boolean[] F = getDefaultBoolArray(false, matrix.getTestCount());
    data.setF(F);
    
    return data;
  }
  
  static boolean[] getDefaultBoolArray(boolean defaultValue, int size) {
    boolean[] array = new boolean[size];
    for(int i = 0; i < array.length; i += 1) {
      array[i] = defaultValue;
    }
    
    return array;
  }

}
