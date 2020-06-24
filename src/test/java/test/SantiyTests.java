package test;

import athena.ProcessedLayerTests;
import org.testng.annotations.Test;
import rds.RDSTests;

public class SantiyTests {

    RDSTests rdsTests = new RDSTests();
    ProcessedLayerTests processedLayerTests = new ProcessedLayerTests();
    @Test(priority = 1)
    public void RdsQueryExecutionTests() {
        rdsTests.selectQuery("RdsSanityQueries.csv");
    }

    @Test(priority = 2)
    public void AthenaQueryExecutionTests() {
        processedLayerTests.consultationTest("AthenaSanityQueries.csv");
    }
}
