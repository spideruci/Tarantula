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
    PassFailPair<Integer> origTests = calculateOrigFailAndPass(numOrigTests, F);
    
    boolean[] L = data.getL();
    boolean[] B = data.getB();
    PassFailPair<Integer> totalLiveTests = 
        calculateTotalLiveFailAndPass(numOrigTests, B, L, F);
    int totalLivePass = totalLiveTests.pass();
    int totalLiveFail = totalLiveTests.fail();
    
    boolean[] C = data.getC();
    PassFailPair<int[]> passAndFailOnStmt = 
        calculatePassOnStmtAndFailOnStmt(numStmts, numOrigTests, B, L, C, M, F);
    int[] passOnStmt = passAndFailOnStmt.pass();
    int[] failOnStmt = passAndFailOnStmt.fail();
    
    PassFailPair<double[]> passAndFailRatio = 
        calculatePassRatioAndFailRatio(
            numStmts, totalLiveFail, totalLiveFail, passOnStmt, failOnStmt);
    double[] passRatio = passAndFailRatio.pass();
    double[] failRatio = passAndFailRatio.fail();
    
    double[][] suspiciousnessAndConfidence = 
        calculateSuspiciousnessAndConfidence(
            numStmts, totalLivePass, totalLiveFail, passRatio, failRatio);

    return suspiciousnessAndConfidence;
  }

  boolean[] calculateBadTestCoverage(
      int numStmts, int numOrigTests, boolean[][] M) {
    boolean[] B = new boolean[numOrigTests];
    for (int i = 0; i < numOrigTests; i++) {
      B[i] = true;
      for (int j = 0; j < numStmts; j++) {
        if (!M[i][j]) continue; 
        // if there is a statement covered for this test case
        B[i] = false; // this is not a bad test case
        break; // no need to look further at this test case
      }
    }
    return B;
  }
  
  PassFailPair<Integer> calculateTotalLiveFailAndPass(
      int numOrigTests, boolean[] B, boolean[] L, boolean[] F) {
    int totalLiveFail = 0;
    int totalLivePass = 0;
    for (int i = 0; i < numOrigTests; i++) {
      if(!L[i]) continue;
      if(B[i]) continue;
      if (F[i]) {
        totalLiveFail++;
      }
      else {
        totalLivePass++;
      }
    }
    return new PassFailPair<Integer>( totalLivePass, totalLiveFail );
  }

  PassFailPair<Integer> calculateOrigFailAndPass(
      int numOrigTests, boolean[] F) {
    int totalOrigFail = 0;
    int totalOrigPass = 0;
    for (int i = 0; i < numOrigTests; i++) {
      if (F[i])
        totalOrigFail++;
      else
        totalOrigPass++;
    }
    
    return new PassFailPair<Integer>(totalOrigPass, totalOrigFail);
  }

  PassFailPair<int[]> calculatePassOnStmtAndFailOnStmt(
      int numStmts, int numOrigTests,
      boolean[] B, boolean[] L, boolean[] C, boolean[][] M, boolean[] F) {

    int[] passOnStmt = new int[numStmts];
    int[] failOnStmt = new int[numStmts];

    // first only consider live test cases
    for (int i = 0; i < numOrigTests; i++) {
      if (B[i]) continue; // if this isn't a dead test case (seg fault)
      if (L[i]) continue; // if this test case is live
      for (int j = 0; j < numStmts; j++) {
        if (!C[j]) continue;
        if(!M[i][j]) continue;
        if (F[i])
          failOnStmt[j]++;
        else
          passOnStmt[j]++;
      }
    }
    
    return new PassFailPair<int[]>(passOnStmt, failOnStmt);
  }

  PassFailPair<double[]> calculatePassRatioAndFailRatio(
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
    
    return new PassFailPair<double[]>(passRatio, failRatio);
  }

  double[][] calculateSuspiciousnessAndConfidence(
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
