package org.spideruci.tarantula;

public final class MatrixStubs {
  public static boolean[][] getSimpleUniformBoolMatrix(
      boolean uniformValue, int numTests, int numStmts) {
    boolean[][] mat = new boolean[numTests][numStmts];
    for(int i = 0; i < numTests; i += 1) {
      for(int j = 0; j < numStmts; j += 1) {
        mat[i][j] = uniformValue;
      }
    }
    return mat;
  }
  
  public static boolean[] getSimpleUniformBoolArray(
      boolean uniformValue, int size) {
    boolean [] array = new boolean[size];
    for(int i = 0; i < size; i += 1) {
      array[i] = uniformValue;
    }
    return array;
  }
  
  public static int[] getSimpleUniformIntArray(
      int uniformValue, int size) {
    int[] array = new int[size];
    for(int i = 0; i < size; i += 1) {
      array[i] = uniformValue;
    }
    return array;
  }
}
