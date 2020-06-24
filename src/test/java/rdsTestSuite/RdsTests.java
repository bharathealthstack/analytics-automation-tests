package rdsTestSuite;

import org.testng.annotations.Test;
import rds.RDSTests;

public class RdsTests {
    RDSTests rdsTests = new RDSTests();

    @Test(priority = 1)
    public void dataSetUpTest() {
        rdsTests.DataCED("RdsIngestionQueries.csv");
        rdsTests.selectQuery("RdsIngestionSelectionQueries.csv");
    }

    @Test(priority = 2)
    public void updationTest() {
        rdsTests.DataCED("RdsUpdationQueries.csv");
        rdsTests.selectQuery("RdsUpdationSelectionQueries.csv");
    }

    @Test(priority = 3)
    public void deletionTest() {
        rdsTests.DataCED("RdsDeletionQueries.csv");
        rdsTests.selectQuery("RdsDeletionSelectionQueries.csv");
    }
}
