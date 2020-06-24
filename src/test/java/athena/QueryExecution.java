package athena;

import org.testng.Assert;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.*;
import software.amazon.awssdk.services.athena.paginators.GetQueryResultsIterable;
import software.amazon.awssdk.services.athena.paginators.ListQueryExecutionsIterable;
import utils.QueryReaderUtils;

import java.util.Iterator;
import java.util.List;

public class QueryExecution {
    QueryReaderUtils queryReaderUtils=new QueryReaderUtils();
    /**
     * Submits a list of queries to Athena and returns the execution ID of the query.
     */
    public void submitAthenaQuery(AthenaClient athenaClient, String fileName) {
        int i;
        List<Row> results;
        StartQueryExecutionResponse startQueryExecutionResponse=null;
        try {
            // The QueryExecutionContext allows us to set the Database.
            QueryExecutionContext queryExecutionContext = QueryExecutionContext.builder()
                    .database(Constants.ATHENA_DEFAULT_DATABASE).build();
            // The result configuration specifies where the results of the query should go in S3 and encryption options
            ResultConfiguration resultConfiguration = ResultConfiguration.builder()
                    // You can provide encryption options for the output that is written.
                    // .withEncryptionConfiguration(encryptionConfiguration)
                    .outputLocation(Constants.ATHENA_OUTPUT_BUCKET).build();
            // Create the StartQueryExecutionRequest to send to Athena which will start the query.
            List<List<String>> queryArray = queryReaderUtils.queryReader(fileName)  ;
            List<String> queryFromArray;
            Iterator iterator = queryArray.iterator();
            while(iterator.hasNext()) {
                System.out.println(iterator.next());
            }
            System.out.println(":::Query Exection:::");
            for ( i= 0; i < queryArray.size(); i++) {
                queryFromArray = queryArray.get(i);
                System.out.println(queryFromArray.get(0));
                StartQueryExecutionRequest startQueryExecutionRequest = StartQueryExecutionRequest.builder()
                        .queryString(queryFromArray.get(0))
                        .queryExecutionContext(queryExecutionContext)
                        .resultConfiguration(resultConfiguration).build();
                startQueryExecutionResponse = athenaClient.startQueryExecution(startQueryExecutionRequest);
                waitForQueryToComplete(athenaClient, startQueryExecutionResponse.queryExecutionId());
                results=processResultRows(athenaClient, startQueryExecutionResponse.queryExecutionId());
                System.out.println(results.get(1));
                String result=results.get(1).data().toString();
                System.out.println(result);
                String finalResult=(result.substring(result.lastIndexOf('=')+1)
                        .replaceAll("\\)","")
                        .replaceAll("]",""));
                System.out.println(finalResult);
                if(queryFromArray.get(1)!=null){
                    Assert.assertEquals(finalResult, queryFromArray.get(1));
                    System.out.println("Assertion complete");
                }
            }
        } catch (AthenaException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Wait for an Athena query to complete, fail or to be cancelled. This is done by polling Athena over an
     * interval of time. If a query fails or is cancelled, then it will throw an exception.
     */

    public static void waitForQueryToComplete(AthenaClient athenaClient, String queryExecutionId) {
        try {
            GetQueryExecutionRequest getQueryExecutionRequest = GetQueryExecutionRequest.builder()
                    .queryExecutionId(queryExecutionId).build();
            GetQueryExecutionResponse getQueryExecutionResponse;
            boolean isQueryStillRunning = true;
            while (isQueryStillRunning) {
                getQueryExecutionResponse = athenaClient.getQueryExecution(getQueryExecutionRequest);
                String queryState = getQueryExecutionResponse.queryExecution().status().state().toString();
                if (queryState.equals(QueryExecutionState.FAILED.toString())) {
                    throw new RuntimeException("Query Failed to run with Error Message: " + getQueryExecutionResponse
                            .queryExecution().status().stateChangeReason());
                } else if (queryState.equals(QueryExecutionState.CANCELLED.toString())) {
                    throw new RuntimeException("Query was cancelled.");
                } else if (queryState.equals(QueryExecutionState.SUCCEEDED.toString())) {
                    isQueryStillRunning = false;
                } else {
                    // Sleep an amount of time before retrying again.
                    Thread.sleep(Constants.SLEEP_AMOUNT_IN_MS);
                }
                System.out.println("Current Status is: " + queryState);
            }
        }
        catch(InterruptedException e){
            System.out.println("waitForQueryToComplete Exception: " + e);
        }
    }

    /**
     * This code calls Athena and retrieves the results of a query.
     * The query must be in a completed state before the results can be retrieved and
     * paginated. The first row of results are the column headers.
     */
    public static List<Row> processResultRows(AthenaClient athenaClient, String queryExecutionId) {
        List<Row> results = null;
        try {

            GetQueryResultsRequest getQueryResultsRequest = GetQueryResultsRequest.builder()
                    // Max Results can be set but if its not set,
                    // it will choose the maximum page size
                    // As of the writing of this code, the maximum value is 1000
                    // .withMaxResults(1000)
                    .queryExecutionId(queryExecutionId).build();
            GetQueryResultsIterable getQueryResultsResults = athenaClient.getQueryResultsPaginator(getQueryResultsRequest);

            for (GetQueryResultsResponse result : getQueryResultsResults) {
                List<ColumnInfo> columnInfoList = result.resultSet().resultSetMetadata().columnInfo();
                results = result.resultSet().rows();
                processRow(results, columnInfoList);
                return results;
            }

        } catch (AthenaException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return results;
    }

    private static void processRow(List<Row> row, List<ColumnInfo> columnInfoList) {
        //Write out the data
        for (Row myRow : row) {
            List<Datum> allData = myRow.data();
            for (Datum data : allData) {
                System.out.println("The value of the column is "+data.varCharValue());
            }
        }
    }
    public static void stopAthenaQuery(AthenaClient athenaClient, String sampleQueryExecutionId){
        try {
            // Submit the stop query Request
            StopQueryExecutionRequest stopQueryExecutionRequest = StopQueryExecutionRequest.builder()
                    .queryExecutionId(sampleQueryExecutionId).build();
            StopQueryExecutionResponse stopQueryExecutionResponse = athenaClient.stopQueryExecution(stopQueryExecutionRequest);
            // Ensure that the query was stopped
            GetQueryExecutionRequest getQueryExecutionRequest = GetQueryExecutionRequest.builder()
                    .queryExecutionId(sampleQueryExecutionId).build();
            GetQueryExecutionResponse getQueryExecutionResponse = athenaClient.getQueryExecution(getQueryExecutionRequest);
            if (getQueryExecutionResponse.queryExecution()
                    .status()
                    .state()
                    .equals(QueryExecutionState.CANCELLED)) {
                // Query was cancelled.
                System.out.println("Query has been cancelled");
            }
        } catch (AthenaException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public static void listQueryIds(AthenaClient athenaClient) {
        try {
            // Build the request
            ListQueryExecutionsRequest listQueryExecutionsRequest = ListQueryExecutionsRequest.builder().build();
            // Get the list results.
            ListQueryExecutionsIterable listQueryExecutionResponses = athenaClient.listQueryExecutionsPaginator(listQueryExecutionsRequest);
            for (ListQueryExecutionsResponse listQueryExecutionResponse : listQueryExecutionResponses) {
                List<String> queryExecutionIds = listQueryExecutionResponse.queryExecutionIds();
                System.out.println("\n" +queryExecutionIds);
            }
        } catch (AthenaException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
