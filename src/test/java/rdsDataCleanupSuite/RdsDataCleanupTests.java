package rdsDataCleanupSuite;

import org.testng.annotations.Test;
import rds.RDSTests;

public class RdsDataCleanupTests {
    RDSTests rdsTests = new RDSTests();
    @Test(priority = 1)
    public void dataCleanUpTests() {
        rdsTests.DataCED("RdsCleanupQueries.csv");
    }
}
