package athenaTestSuite;

import athena.ProcessedLayerTests;
import org.testng.annotations.Test;

public class AthenaTests {
    ProcessedLayerTests processedLayerTests = new ProcessedLayerTests();
    @Test(priority = 1)
    public void athenaSelectionTest() {
        processedLayerTests.consultationTest("AthenSelectionQueries.csv");
    }
}
