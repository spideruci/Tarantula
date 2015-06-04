package org.spideruci.tarantula;

import static org.spideruci.tarantula.TarantulaFaultLocalizer.SUSPICIOUSNESS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.spideruci.tacoco.coverage.CoverageMatrix;
import org.spideruci.tacoco.reporting.CoverageJsonReader;
import org.spideruci.tacoco.reporting.data.SourceFileCoverage.LineCoverageFormat;

import com.google.gson.stream.JsonReader;

public class Tarantula {
  
  public static void main(String[] args) throws IOException {
    File jsonFile = new File(args[0]);
    LineCoverageFormat covFormat = CoverageJsonReader.readCoverageFormat(jsonFile);
    InputStreamReader jsonIn = 
        new InputStreamReader(new FileInputStream(jsonFile));
    JsonReader jsonreader = new JsonReader(jsonIn);
    CoverageJsonReader reader = new CoverageJsonReader(jsonreader);;
    CoverageMatrix covMat = reader.read(covFormat);
    TarantulaData data = TarantulaDataBuilder.buildFromCoverageMatrix(covMat);
    TarantulaFaultLocalizer localizer = new TarantulaFaultLocalizer();
    double[][] suspiciousnessAndConfidence = localizer.compute(data, false);
    double[] suspiciousness = suspiciousnessAndConfidence[SUSPICIOUSNESS];
    
    for(int i = 0; i < suspiciousness.length; i += 1) {
      if(i != 0 && i % 10 == 0) {
        System.out.println();
      }
      
      System.out.printf("%f ", suspiciousness[i]);
    }
  }

}
