package athena;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;

public class ProcessedLayerTests {
    QueryExecution queryExecution=new QueryExecution();
    AthenaClient athenaClient = AthenaClient.builder()
            .region(Region.AP_SOUTH_1)
            .build();
    public void test(String fileName){
        System.out.println(":::Inside athenaDataTest:::");
        queryExecution.submitAthenaQuery(athenaClient,fileName);
        System.out.println(":::Outside athenaDataTest:::");
    }
}
