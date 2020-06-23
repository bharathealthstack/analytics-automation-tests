package rds;

import org.apache.log4j.Logger;
import org.testng.Assert;
import utils.QueryReaderUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import static java.sql.DriverManager.getConnection;

public class RDSConnections {
    static Logger logger;
    QueryReaderUtils queryReaderUtils=new QueryReaderUtils();
//    static Connection conn = getRemoteConnection();

    public Connection getRemoteConnection() {
        System.out.println("::: Inside getRemoteConnection() :::");
            String dbName = System.getenv("RDS_DB_NAME");
            String userName = System.getenv("RDS_USERNAME");
            String password = System.getenv("RDS_PASSWORD");
            String hostname = System.getenv("RDS_HOSTNAME");
            String port = System.getenv("RDS_PORT");
        if(hostname!=null)
    {
        System.out.println("Connection starting...");
        try {
            Connection conn;
            String jdbcUrl = "jdbc:mysql://" + hostname + ":" +
                    port +
                    "/" + dbName +
                    "?user=" + userName + "&password=" + password;
            System.out.println(":::JDBC URL:::"+jdbcUrl);
            // Load the JDBC driver
            try {
                System.out.println("Loading driver...");
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("Driver loaded!");

            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Cannot find the driver in the classpath!", e);
            }
            System.out.println("::: JDBC URL:::"+jdbcUrl);
            conn = getConnection(jdbcUrl);
            System.out.println("Connection completed...");
            return conn;
        } catch (SQLException e) {
            System.out.println("Connection Exception...");
            logger.warn(e.toString());
        }
    }
    return null;
}
    public void credTableQuery(Connection conn,String fileName) {
        Statement setupStatement = null;
        int i;
        try {
            // Create connection to RDS DB instance
            // Connection conn = getRemoteConnection();
            // Create a table and write two rows
            setupStatement = conn.createStatement();
            List<List<String>> queryArray = queryReaderUtils.queryReader(fileName);
            List<String> queryFromArray;
            Iterator iterator = queryArray.iterator();
            while(iterator.hasNext()) {
                System.out.println(iterator.next());
            }
            System.out.println(":::Query Exection:::");
            for ( i= 0; i < queryArray.size(); i++) {
                queryFromArray=queryArray.get(i);
                setupStatement.addBatch(queryFromArray.get(0));
                setupStatement.executeBatch();
            }
            setupStatement.close();

        } catch (SQLException ex) {
            // Handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public void selectQuery(Connection conn,String fileName){
        Statement readStatement = null;
        ResultSet resultSet = null;
        String results="";
        int i;
        try {
            readStatement = conn.createStatement();
            List<List<String>> queryArray = queryReaderUtils.queryReader(fileName);
            List<String> queryFromArray;
            Iterator iterator = queryArray.iterator();
            while(iterator.hasNext()) {
                System.out.println(iterator.next());
            }
            System.out.println(":::Query Exection:::");
            for ( i= 0; i < queryArray.size(); i++) {
                queryFromArray=queryArray.get(i);
                System.out.println(queryFromArray);
                    System.out.println(queryFromArray.get(0));
                resultSet = readStatement.executeQuery(queryFromArray.get(0));
                while(resultSet.next()){
                    System.out.println(queryFromArray.get(1));
                    results = resultSet.getString(queryFromArray.get(1));
                    System.out.println("Count of Consultation ID : "+ results);
                }
                if(queryFromArray.get(2)!=null){
                    Assert.assertEquals(results, queryFromArray.get(2));
                }
                System.out.println("Result: " + results);
            }
            readStatement.close();
            conn.close();
        } catch (SQLException ex) {
            // Handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

}
