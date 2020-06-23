package test;

import athena.ProcessedLayerTests;
import org.testng.annotations.Test;
import rds.RDSTests;
import s3.S3Tests;

public class SwasthAnalyticsTests {
    S3Tests s3Tests = new S3Tests();
    RDSTests rdsTests = new RDSTests();
    ProcessedLayerTests processedLayerTests = new ProcessedLayerTests();

    //Data setup
    @Test(priority = 1)
    public void dataSetUpTest() {
/*        rdsTests.dataCreation("/Users/msuren1/Desktop/rds/src/test/java/storedFiles/RdsIngestionQueries.csv");
        rdsTests.selectQuery("/Users/msuren1/Desktop/rds/src/test/java/storedFiles/RdsIngestionSelectionQueries.csv");*/
    }

    @Test(priority = 2)
    public void updationTest() {
//        rdsTests.dataUpdation("/Users/msuren1/Desktop/rds/src/test/java/storedFiles/RdsUpdationQueries.csv");
//        rdsTests.selectQuery("/Users/msuren1/Desktop/rds/src/test/java/storedFiles/RdsUpdationSelectionQueries.csv");
    }

    @Test(priority = 3)
    public void deletionTest() {
//        rdsTests.dataDeletion("/Users/msuren1/Desktop/rds/src/test/java/storedFiles/RdsDeletionQueries.csv");
//        rdsTests.selectQuery("/Users/msuren1/Desktop/rds/src/test/java/storedFiles/RdsDeletionSelectionQueries.csv");

    }

    @Test(priority = 4)
    public void athenaSelectionTest() {
//        processedLayerTests.test("/Users/msuren1/Desktop/rds/src/test/java/storedFiles/AthenaSanityQueries.csv");
    }

    //step 4 : Recorrect the data
    @Test(priority = 5)
    public void dataCleanUpTests() {
//        rdsTests.selectQuery("/Users/msuren1/Desktop/rds/src/test/java/storedFiles/RdsCleanupQueries.csv");
    }

    @Test(priority = 6)
    public void RdsQueryExecutionTests() {
        rdsTests.selectQuery("src/test/java/StoredFiles/RdsSanityQueries.csv");
    }

    @Test(priority = 7)
    public void AthenaQueryExecutionTests() {
//        processedLayerTests.test1("/Users/msuren1/Desktop/rds/src/test/java/storedFiles/AthenaSanityQueries.csv");
    }
}
