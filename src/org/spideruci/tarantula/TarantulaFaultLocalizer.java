package org.spideruci.tarantula;

public class TarantulaFaultLocalizer {
  
  public static final int PASS = 0;
  public static final int FAIL = 1;
  public static final int SUSPICIOUSNESS = 0;
  public static final int CONFIDENCE = 1;
  
  public double[][] compute(TarantulaData data, boolean isBCalculated) {
    int numStmts = data.getNumStmts();
    int numOrigTests = data.getOrigNumTests();
    boolean[][] M = data.getM();
    
    if (!isBCalculated) {
      boolean[] B = calculateBadTestCoverage(numStmts, numOrigTests, M);
      data.setB(B);
    }
    
    boolean[] F = data.getF();
    
    @SuppressWarnings("unused")
    int[] origFailAndPass = calculateOrigFailAndPass(numOrigTests, F);
    
    boolean[] L = data.getL();
    boolean[] B = data.getB();
    int[] totalLivePassFail = 
        calculateTotalLiveFailAndPass(numOrigTests, B, L, F);
    int totalLivePass = totalLivePassFail[PASS];
    int totalLiveFail = totalLivePassFail[FAIL];
    
    boolean[] C = data.getC();
    int[][] passAndFailOnStmt = 
        calculatePassOnStmtAndFailOnStmt(numStmts, numOrigTests, B, L, C, M, F);
    int[] passOnStmt = passAndFailOnStmt[PASS];
    int[] failOnStmt = passAndFailOnStmt[FAIL];
    
    double[][] passAndFailRatio = 
        calculatePassRatioAndFailRatio(
            numStmts, totalLiveFail, totalLiveFail, passOnStmt, failOnStmt);
    double[] passRatio = passAndFailRatio[PASS];
    double[] failRatio = passAndFailRatio[FAIL];
    
    double[][] suspiciousnessAndConfidence = 
        calculateSuspiciousnessAndConfidence(
            numStmts, totalLivePass, totalLiveFail, passRatio, failRatio);

    return suspiciousnessAndConfidence;
  }

  private boolean[] calculateBadTestCoverage(
      int numStmts, int numOrigTests, boolean[][] M) {
    boolean[] B = new boolean[numOrigTests];
    for (int i = 0; i < numOrigTests; i++) {
      B[i] = true;
      for (int j = 0; j < numStmts; j++) {
        if (M[i][j]) { /*
                 * if there is a statement covered for this test
                 * case
                 */
          B[i] = false; // this is not a bad test case
          break; // no need to look further at this test case
        }
      }
    }
    return B;
  }
  
  private int[] calculateTotalLiveFailAndPass(
      int numOrigTests, boolean[] B, boolean[] L, boolean[] F) {
    int totalLiveFail = 0;
    int totalLivePass = 0;
    for (int i = 0; i < numOrigTests; i++) {
      if (L[i]) {
        if (!B[i]) {
          if (F[i])
            totalLiveFail++;
          else
            totalLivePass++;
        }
      }
    }
    // System.out.println("livepass="+ totalLivePass + "\tlivefail=" +
    // totalLiveFail + "\n");
    return new int[] { totalLivePass, totalLiveFail };
  }

  private int[] calculateOrigFailAndPass(int numOrigTests, boolean[] F) {
    int totalOrigFail = 0;
    int totalOrigPass = 0;
    for (int i = 0; i < numOrigTests; i++) {
      if (F[i])
        totalOrigFail++;
      else
        totalOrigPass++;
    }
    // System.out.println("origpass="+ totalOrigPass + "\torigfail=" +
    // totalOrigFail + "\n");
    
    return new int[] {totalOrigPass, totalOrigFail};
  }

  private int[][] calculatePassOnStmtAndFailOnStmt(
      int numStmts, int numOrigTests,
      boolean[] B, boolean[] L, boolean[] C, boolean[][] M, boolean[] F) {

    int[] passOnStmt = new int[numStmts];
    int[] failOnStmt = new int[numStmts];

    // first only consider live test cases
    for (int i = 0; i < numOrigTests; i++) {
      if (!B[i]) { // if this isn't a dead test case (seg fault)
        if (L[i]) { // if this test case is live
          for (int j = 0; j < numStmts; j++) {
            if (C[j]) {
              if (M[i][j]) {
                if (F[i])
                  failOnStmt[j]++;
                else
                  passOnStmt[j]++;
              }
            }
          }
        }
      }
    }
    
    return new int[][] { passOnStmt, failOnStmt };
  }

  private double[][] calculatePassRatioAndFailRatio(
      int numStmts, int totalLivePass, int totalLiveFail,
      int[] passOnStmt, int[] failOnStmt) {

    double[] passRatio = new double[numStmts];
    double[] failRatio = new double[numStmts];

    for (int i = 0; i < numStmts; i++) {
      // System.out.print("line " + i + "\t");
      if (totalLivePass == 0) {
        passRatio[i] = 0d;
      } else {
        passRatio[i] = (double) passOnStmt[i] / (double) totalLivePass;
        // System.out.print("numPass=" + passOnStmt[i] + "\t");
      }

      if (totalLiveFail == 0) {
        failRatio[i] = 0d;
      } else {
        failRatio[i] = (double) failOnStmt[i] / (double) totalLiveFail;
        // System.out.print("numFail=" + failOnStmt[i] + "\t");
      }
      // System.out.println();
    }
    
    return new double[][] { passRatio, failRatio };
  }

  private double[][] calculateSuspiciousnessAndConfidence(
      int numStmts, int totalLivePass, int totalLiveFail,
      double[] passRatio, double[] failRatio) {
    double[] suspiciousness = new double[numStmts];
    double[] confidence = new double[numStmts];

    for (int i = 0; i < numStmts; i++) {
      
      if ((totalLiveFail == 0) && (totalLivePass == 0)) {
        suspiciousness[i] = -1d;
        confidence[i] = -1d;
      } else if ((failRatio[i] == 0d) && (passRatio[i] == 0d)) {
        suspiciousness[i] = -1d;
        confidence[i] = -1d;
      } else {
        suspiciousness[i] = failRatio[i]
            / (failRatio[i] + passRatio[i]);
        confidence[i] = Math.max(failRatio[i], passRatio[i]);
      }
    }
    
    return new double[][] { suspiciousness, confidence };
  }

}
