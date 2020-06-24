package rds;
import java.sql.Connection;

public class RDSTests {
    RDSConnections rdsConnections=new RDSConnections();
    Connection conn = rdsConnections.getRemoteConnection();

    public void DataCED(String fileName){
        System.out.println("::: Inside rDSDataIngestionTest/rDSDataUpdationTest/rDSDataDeletionTest :::");
        rdsConnections.credTableQuery(conn,fileName);
        System.out.println(":::Outside rDSDataIngestionTest/rDSDataUpdationTest/rDSDataDeletionTest :::");
    }
    public void selectQuery(String fileName) {
        System.out.println(":::Inside rDSDataSelectQueryTest:::");
        rdsConnections.selectQuery(conn,fileName);
        System.out.println(":::Outside rDSDataSelectQueryTest:::");
    }
}
