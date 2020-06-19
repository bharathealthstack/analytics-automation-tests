package rds;
import java.sql.Connection;

public class RDSTests {
    RDSConnections rdsConnections=new RDSConnections();
    Connection conn = rdsConnections.getRemoteConnection();

    public void dataCreation(String fileName){
        System.out.println("::: Inside rDSDataIngestionTest :::");
        rdsConnections.credTableQuery(conn,fileName);
        System.out.println(":::Outside rDSDataIngestionTest:::");
    }
    public void dataUpdation(String fileName){
        System.out.println("::: Inside rDSDataUpdationTest :::");
        rdsConnections.credTableQuery(conn,fileName);
        System.out.println(":::Outside rDSDataUpdationTest:::");
    }
    public void dataDeletion(String fileName){
        System.out.println("::: Inside rDSDataDeletionTest :::");
        rdsConnections.credTableQuery(conn,fileName);
        System.out.println(":::Outside rDSDataDeletionTest:::");
    }
    public void selectQuery(String fileName) {
        System.out.println(":::Inside rDSDataSelectQueryTest:::");
        rdsConnections.selectQuery(conn,fileName);
        System.out.println(":::Outside rDSDataSelectQueryTest:::");
    }
}
