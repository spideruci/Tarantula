package org.spideruci.tarantula;
/*******************************************************************************
 * Copyright (c) 2010 James A. Jones jim@jamesajones.net
 ******************************************************************************/

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Externalizable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Calculates suspiciousness and confidence values according to the Tarantula
 * fault-localization technique.
 * 
 * The usage mode is to create a coverage matrix that specifies which program
 * elements are executed by which test cases. In terms of this implementation, I
 * call each program element a statement ("stmt"), but they can refer to any
 * program element or event that could happen or not within an execution. This
 * coverage matrix is encoded as a two-dimensional array (or, equivalently in
 * Java means an array of arrays) where the first dimension is indexed by the
 * test case number and the second dimension is indexed by the statement number
 * (or any program event identifier).
 * 
 * The algorithm is also informed by a few other arrays:
 * 
 * F[] is indexed by test case number. F specifies which test cases
 * fail�true=fail, false=pass.
 * 
 * L[] is indexed by test case number. L specifies which test cases should be
 * used. This array was created to allow a large universe of test cases to be
 * loaded and several subsets of test cases could be included or excluded from
 * the total set. This allows easy and fast experimentation with various subset
 * test suites.
 * 
 * S[] is indexed by program element number. S specifies the bug identifier for
 * that line. This array is optional. This is for experimentation purposes where
 * multiple faults are placed in the subject program.
 * 
 * C[] is indexed by program element number. C specifies which
 * statements/program elements are coverable and which are not (for example, a
 * blank line or a comment line).
 * 
 * B[] is indexed by test case number. B specifies which test cases' coverage
 * are problematic. This was created as a work-around to problems that I had
 * with coverage reports that were incomplete due to segmentation faults in the
 * subject program. Specifying a test case as a "bad coverage" test case means
 * that it will not be used for calculation regardless of whether this test case
 * is "live" (according to the L array). You can explicitly set this or you can
 * leave this alone. The calculateBadTestCoverage() method sets this array for
 * you based on whether a test case has absolutely no coverage � no program
 * elements being executed.
 * 
 * Once these arrays are set, the compute() method should be called, which then
 * calls a series of other calculate... methods. Instead you can directly call
 * the other calculate... methods. After compute() has been called, the results
 * can be found in the suspiciousness[] and confidence[] arrays (which are each
 * indexed by program element number). For both suspiciousness[] and
 * confidence[] arrays, a -1 is used for program elements that can't be assigned
 * (probably because of an uncoverable entity).
 * 
 * @author James A. Jones jim@jamesajones.net
 * 
 */
public class TarantulaData implements Externalizable {

  /**
   * coverage matrix -- [test][stmt]
   */
	private boolean[][] M;

	/**
	 * failing test cases -- [test]
	 */
	private boolean[] F;

	/**
	 * live test cases -- [test]
	 */
	private boolean[] L;

	/**
	 * fault numbers -- [stmt]
	 */
	private int[] S;

	/**
	 * coverable statements -- [stmt]
	 */
	private boolean[] C;

	/**
	 * bad coverage (no coverage information, usually due to
	 * a segmentation fault) -- [test]
	 */
	private boolean[] B; 

	private int numOrigTests;

	private int numStmts;

	private int numFaults;

	private int totalLiveFail;

	private int totalLivePass;

	private int totalOrigFail;

	private int totalOrigPass;

	private int[] passOnStmt; /*
							 * p(s), for every s and considering liveness of
							 * test cases -- [stmt]
							 */

	private int[] failOnStmt; /*
							 * f(s), for every s and considering liveness of
							 * test cases -- [stmt]
							 */

	private double[] passRatio; // p(s)/total_live_p, for every s -- [stmt]

	private double[] failRatio; // f(s)/total_live_f, for every f -- [stmt]

	private double[] suspiciousness; // -- [stmt]

	private double[] confidence; // -- [stmt]

	private String directory; /* the directory to store the coverage matrix file */

	public TarantulaData() {
	}

	/**
	 * @param M 
	 * coverage matrix -- [test][stmt]
	 */
	public TarantulaData(boolean[][] M) {
		this.M = M;

		numOrigTests = M.length;
		numStmts = numOrigTests == 0 ? 0 : M[0].length;

		// initialize so that all test cases are live
		L = new boolean[numOrigTests];
		for (int i = 0; i < L.length; i++) {
			L[i] = true;
		}

	}

	public void setDirectory(String dirStr) {
		directory = dirStr;
	}

	public void setC(boolean[] C) {
		this.C = C;
	}

	public void setF(boolean[] F) {
		this.F = F;
	}

	public void setL(boolean[] L) {
		this.L = L;
	}
	
  public void setB(boolean[] B) {
    this.B = B;
  }

	public void setS(int[] S) {
		this.S = S;
		numFaults = 0;
		for (int i = 0; i < S.length; i++) {
			if (S[i] > numFaults)
				numFaults = S[i];
		}
	}
	
	/**
	 * @return coverage matrix -- [test][stmt]
	 */
	public boolean[][] getM() {
		return M;
	}

	/**
	 * @return live test cases -- [test]
	 */
	public boolean[] getL() {
		return L;
	}

	/**
	 * @return failing test cases -- [test]
	 */
	public boolean[] getF() {
		return F;
	}
	
	/**
	 * @return 
	 * bad coverage (no coverage information, usually due to
   * a segmentation fault) -- [test]
	 */
	public boolean[] getB() {
    return this.B;
  }
	
	/**
	 * @return coverable statements -- [stmt]
	 */
	public boolean[] getC() {
	  return this.C;
	}

	public int[] getPassOnStmt() {
		return passOnStmt;
	}

	public int[] getFailOnStmt() {
		return failOnStmt;
	}

	public double[] getSuspiciousness() {
		return suspiciousness;
	}

	public double[] getConfidence() {
		return confidence;
	}

	public boolean isTestPassed(int zeroBasedTestNumber) {
		return !F[zeroBasedTestNumber];
	}

	public boolean isStmtCoverable(int zeroBasedLineNumber) {
		return C[zeroBasedLineNumber];
	}

	public int getOrigNumTests() {
		return numOrigTests;
	}

	public int getOrigTotalFail() {
		return totalOrigFail;
	}

	public int getOrigTotalPass() {
		return totalOrigPass;
	}

	public int getLiveNumTests() {
		return totalLiveFail + totalLivePass;
	}

	public int getLiveTotalFail() {
		return totalLiveFail;
	}

	public int getLiveTotalPass() {
		return totalLivePass;
	}

	public int getNumStmts() {
		return numStmts;
	}

	public int getNumFaults() {
		return numFaults;
	}

	public int getFaultNum(int zeroBasedLineNum) {
		return S[zeroBasedLineNum];
	}

	public int getNumUncoverableStmts() {
		int c = 0;
		for (int i = 0; i < C.length; i++) {
			if (!C[i])
				c++;
		}
		return c;
	}
	
	
	public void compute() {
	  TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
	  boolean isBCalculated = false;
	  localizer.compute(this, isBCalculated);
  }
	
  public void calculateOrigFailAndPass() {
    totalOrigFail = 0;
    totalOrigPass = 0;
    for (int i = 0; i < numOrigTests; i++) {
      if (F[i])
        totalOrigFail++;
      else
        totalOrigPass++;
    }
  }
	
	/** ******** Externalizable required methods ******************* */
	public void writeExternal(ObjectOutput obj_out) throws IOException {

		String pwd = null;
		if (directory == null)
			pwd = System.getProperty("user.dir");
		else
			pwd = directory;

		// FileWriter unzipped_out = new FileWriter(pwd + "/cov_matrix.txt");

		// Reference to our zip file
		FileWriter out = null;
		String covMatrixFileName = pwd + "/cov_matrix";
		try {
			out = new FileWriter(covMatrixFileName);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		// write a comment that describes the file format
		out.write("0 Coverage matrix file\n");
		out.write("0 Record descriptions:\n");
		out.write("0      0 comment\n");
		out
				.write("0      1 <number of test cases> <number of sourcefiles> <number of bugs>\n");
		out.write("0      2 <source file name> <number of statements>\n");
		out
				.write("0      3 <test case num (0-based)> <coverage vector> (1=covered, 0=uncovered)\n");
		out.write("0      4 <vector of pass/fail info (1=failed, 0=passed)\n");
		out
				.write("0      5 <vector of liveness of test cases (1=live, 0=dead)\n");
		out
				.write("0      6 <vector of fault numbers (0=nonfaulty, 1,2,3...F=fault numbers (16 faults max))\n");
		out
				.write("0      7 <vector of coverability of statements (1=coverable, 0=uncoverable)\n");
		out.write("0\n");

		// write the number test cases, number of source files, number of bugs
		out.write(("1 " + numOrigTests + " 1 " + numFaults + "\n"));

		// for each source file
		{
			// write source file name and number of statements
			out.write(("2 space.c " + numStmts + "\n"));

			// write the coverage matrix
			for (int i = 0; i < M.length; i++) {
				out.write(("3 " + i + " "));
				boolean[] vector = M[i];
				for (int j = 0; j < vector.length; j++) {
					if (vector[j])
						out.write("1");
					else
						out.write("0");
				}
				out.write("\n");

			}

		}

		// write the pass/fail info
		out.write("4 ");
		for (int i = 0; i < F.length; i++) {
			if (F[i])
				out.write("1");
			else
				out.write("0");
		}
		out.write("\n");

		// write the liveness of test cases info
		out.write("5 ");
		for (int i = 0; i < L.length; i++) {
			if (L[i])
				out.write("1");
			else
				out.write("0");
		}
		out.write("\n");

		// write the bug number info
		out.write("6 ");
		for (int i = 0; i < S.length; i++) {
			if ((S[i] >= 0) && (S[i] <= 9)) {
				out.write(("" + S[i]));
			} else if (S[i] == 10) {
				out.write("a");
			} else if (S[i] == 11) {
				out.write("b");
			} else if (S[i] == 12) {
				out.write("c");
			} else if (S[i] == 13) {
				out.write("d");
			} else if (S[i] == 14) {
				out.write("e");
			} else if (S[i] == 15) {
				out.write("f");
			} else {
				System.err.println("Bug number out of range: " + S[i]);
				System.exit(1);
			}
		}
		out.write("\n");

		// write the coverability of each statement
		out.write("7 ");
		for (int i = 0; i < C.length; i++) {
			if (C[i])
				out.write("1");
			else
				out.write("0");
		}
		out.write("\n");

		try {
			out.close();
//			new CmdExec("/bin/rm -f " + covMatrixFileName + ".gz",
//					new ReentrantLock());
//			new CmdExec("/sw/bin/gzip " + covMatrixFileName,
//					new ReentrantLock());
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	public void writeExternal1(ObjectOutput obj_out) throws IOException {

		String pwd = null;
		if (directory == null)
			pwd = System.getProperty("user.dir");
		else
			pwd = directory;

		// FileWriter unzipped_out = new FileWriter(pwd + "/cov_matrix.txt");

		// Reference to our zip file
		FileOutputStream dest = new FileOutputStream(pwd + "/cov_matrix.gz");

		// Wrap our destination zipfile with a ZipOutputStream
		GZIPOutputStream out = new GZIPOutputStream(new BufferedOutputStream(
				dest));

		// write a comment that describes the file format
		out.write("0 Coverage matrix file\n".getBytes());
		out.write("0 Record descriptions:\n".getBytes());
		out.write("0      0 comment\n".getBytes());
		out
				.write("0      1 <number of test cases> <number of sourcefiles> <number of bugs>\n"
						.getBytes());
		out.write("0      2 <source file name> <number of statements>\n"
				.getBytes());
		out
				.write("0      3 <test case num (0-based)> <coverage vector> (1=covered, 0=uncovered)\n"
						.getBytes());
		out.write("0      4 <vector of pass/fail info (1=failed, 0=passed)\n"
				.getBytes());
		out
				.write("0      5 <vector of liveness of test cases (1=live, 0=dead)\n"
						.getBytes());
		out
				.write("0      6 <vector of fault numbers (0=nonfaulty, 1,2,3...F=fault numbers (16 faults max))\n"
						.getBytes());
		out
				.write("0      7 <vector of coverability of statements (1=coverable, 0=uncoverable)\n"
						.getBytes());
		out.write("0\n".getBytes());

		// write the number test cases, number of source files, number of bugs
		out.write(("1 " + numOrigTests + " 1 " + numFaults + "\n").getBytes());

		// for each source file
		{
			// write source file name and number of statements
			out.write(("2 space.c " + numStmts + "\n").getBytes());

			// write the coverage matrix
			for (int i = 0; i < M.length; i++) {
				out.write(("3 " + i + " ").getBytes());
				boolean[] vector = M[i];
				for (int j = 0; j < vector.length; j++) {
					if (vector[j])
						out.write("1".getBytes());
					else
						out.write("0".getBytes());
				}
				out.write("\n".getBytes());

			}

		}

		// write the pass/fail info
		out.write("4 ".getBytes());
		for (int i = 0; i < F.length; i++) {
			if (F[i])
				out.write("1".getBytes());
			else
				out.write("0".getBytes());
		}
		out.write("\n".getBytes());

		// write the liveness of test cases info
		out.write("5 ".getBytes());
		for (int i = 0; i < L.length; i++) {
			if (L[i])
				out.write("1".getBytes());
			else
				out.write("0".getBytes());
		}
		out.write("\n".getBytes());

		// write the bug number info
		out.write("6 ".getBytes());
		for (int i = 0; i < S.length; i++) {
			if ((S[i] >= 0) && (S[i] <= 9)) {
				out.write(("" + S[i]).getBytes());
			} else if (S[i] == 10) {
				out.write("a".getBytes());
			} else if (S[i] == 11) {
				out.write("b".getBytes());
			} else if (S[i] == 12) {
				out.write("c".getBytes());
			} else if (S[i] == 13) {
				out.write("d".getBytes());
			} else if (S[i] == 14) {
				out.write("e".getBytes());
			} else if (S[i] == 15) {
				out.write("f".getBytes());
			} else {
				System.err.println("Bug number out of range: " + S[i]);
				System.exit(1);
			}
		}
		out.write("\n".getBytes());

		// write the coverability of each statement
		out.write("7 ".getBytes());
		for (int i = 0; i < C.length; i++) {
			if (C[i])
				out.write("1".getBytes());
			else
				out.write("0".getBytes());
		}
		out.write("\n".getBytes());
		out.close();

	}

	public void readExternal(ObjectInput obj_in) throws IOException,
			ClassNotFoundException {

		final int BUFFER = 2048;

		String pwd = null;
		if (directory == null)
			pwd = System.getProperty("user.dir");
		else
			pwd = directory;

		// read in the coverage matrix from zip file to a string
		FileInputStream dest = new FileInputStream(pwd + "/cov_matrix.gz");
		GZIPInputStream in = new GZIPInputStream(new BufferedInputStream(dest));
		StringBuffer strBuf = new StringBuffer();
		while (true) {
			byte[] b = new byte[BUFFER];
			int returnVal = in.read(b, 0, BUFFER);
			String newStr = new String(b);
			int index = newStr.indexOf('\0');
			String newerStr = null;
			if (index == -1)
				newerStr = newStr;
			else
				newerStr = newStr.substring(0, index);
			strBuf.append(newerStr);
			if (returnVal == -1)
				break;
		}

		in.close();

		// parse the coverage matrix string
		String covMatrixStr = strBuf.toString();
		StringTokenizer lineTokenizer = new StringTokenizer(covMatrixStr, "\n");
		while (lineTokenizer.hasMoreTokens()) {
			String line = lineTokenizer.nextToken();

			// parse the record id
			StringTokenizer t = new StringTokenizer(line, " ");
			if (!t.hasMoreTokens())
				continue;
			String recordIDStr = t.nextToken();
			int recordID = Integer.parseInt(recordIDStr);

			switch (recordID) {
			case 0:
				break;
			case 1:
				String numTestsStr = t.nextToken();
				numOrigTests = Integer.parseInt(numTestsStr);

				String numSourceFilesStr = t.nextToken();

				String numFaultsStr = t.nextToken();
				numFaults = Integer.parseInt(numFaultsStr);

				// initialize coverage matrix
				M = new boolean[numOrigTests][];

				break;

			case 2:
				String sourceFileName = t.nextToken();

				String numStmtsStr = t.nextToken();
				numStmts = Integer.parseInt(numStmtsStr);

				break;

			case 3:
				String testCaseNumStr = t.nextToken();
				int testCaseNum = Integer.parseInt(testCaseNumStr);

				M[testCaseNum] = new boolean[numStmts];

				String covVectorStr = t.nextToken();
				char[] covVector = covVectorStr.toCharArray();
				for (int i = 0; i < covVector.length; i++) {
					M[testCaseNum][i] = (covVector[i] == '1') ? true : false;
				}

				break;

			case 4:
				F = new boolean[numOrigTests];
				String failVecStr = t.nextToken();
				char[] failVec = failVecStr.toCharArray();
				for (int i = 0; i < failVec.length; i++) {
					F[i] = (failVec[i] == '1') ? true : false;
				}
				calculateOrigFailAndPass();

				break;

			case 5:
				L = new boolean[numOrigTests];
				String liveVecStr = t.nextToken();
				char[] liveVec = liveVecStr.toCharArray();
				for (int i = 0; i < liveVec.length; i++) {
					L[i] = (liveVec[i] == '1') ? true : false;
				}

				break;

			case 6:
				S = new int[numStmts];
				String faultNumsStr = t.nextToken();
				char[] faultNums = faultNumsStr.toCharArray();
				for (int i = 0; i < faultNums.length; i++) {
					S[i] = Integer.parseInt(String.valueOf(faultNums[i]));
				}

				break;

			case 7:
				C = new boolean[numStmts];
				String coverVecStr = t.nextToken();
				char[] coverVec = coverVecStr.toCharArray();
				for (int i = 0; i < coverVec.length; i++) {
					C[i] = (coverVec[i] == '1') ? true : false;
				}

				break;

			default:
				System.err
						.println("Warning: Don't know record type, ignoring: "
								+ line);
				break;
			}
		}

		compute();

	}
}
