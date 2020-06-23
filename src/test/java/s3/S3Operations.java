package s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.apache.parquet.example.data.simple.SimpleGroup;
import utils.Parquet;
import utils.ParquetReaderUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.apache.hadoop.ipc.Client.LOG;

public class S3Operations {
    Regions clientRegion = Regions.AP_SOUTH_1;
    AWSCredentials credentials = new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"),System.getenv("AWS_SECRET_ACCESS_KEY"));
    final AmazonS3 s3Client = AmazonS3ClientBuilder.standard().
            withCredentials(new AWSStaticCredentialsProvider(credentials)).
            withRegion(clientRegion).build();
    String bucketName = System.getenv("S3_BUCKET_NAME");
    String key = System.getenv("S3_KEY");
    S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;
    ParquetReaderUtils parquetReaderUtils = new ParquetReaderUtils();

    public void listAllBuckets() {
        List<Bucket> buckets = s3Client.listBuckets();
        System.out.println("Your Amazon S3 buckets are:");
        for (Bucket b : buckets) {
            System.out.println("* " + b.getName());
        }
    }
    public List<String> listKeysInDirectory(String bucketName, String prefix) {
        String delimiter = "/";
        if (!prefix.endsWith(delimiter)) {
            prefix += delimiter;
        }

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucketName).withPrefix(prefix)
                .withDelimiter(delimiter);
        ObjectListing objects = s3Client.listObjects(listObjectsRequest);
        return objects.getCommonPrefixes();
    }

    public void downloadS3File(){
//        File file = new File("");
//        s3Client.getObject(new GetObjectRequest(bucketName,key),file);
        TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(s3Client).build();
        Date lastUpdatedTime;
        ObjectListing listing = s3Client.listObjects(bucketName, key);
//        List<ObjectListing> listing = listKeysInDirectory(bucketName, key);
        List<S3ObjectSummary> summaries = listing.getObjectSummaries();
        System.out.println(summaries);
        String fileName="Test.json";
        List<Date> date = new ArrayList<Date>();
        for (S3ObjectSummary os: summaries) {
//            date.add(os.getLastModified());
            date.add(os.getLastModified());
            System.out.println(os.getKey());
            System.out.println(os.getLastModified());
        }
        lastUpdatedTime = Collections.max(date);
        System.out.println(":::Last updated time:::"+lastUpdatedTime);
        for (S3ObjectSummary os: summaries) {
            if(os.getLastModified().equals(lastUpdatedTime)) {
                try {
                    Download multipleFileDownload = transferManager.download(bucketName, key, new File(fileName));
                    while (multipleFileDownload.isDone() == false) {
                        Thread.sleep(1000);
                    }
                }catch(InterruptedException i){
                    LOG.error("Exception Occurred while downloading the file ",i);
                }
            }
        }
    }

    public void getObject(String fileName) throws IOException{
        try {

            System.out.println("Downloading an object");
            fullObject = s3Client.getObject(new GetObjectRequest(bucketName, key));
            System.out.println("Content-Type: " + fullObject.getObjectMetadata().getContentType());
            System.out.println("Content: ");
            displayTextInputStream(fullObject.getObjectContent());

            // Get a range of bytes from an object and print the bytes.
            GetObjectRequest rangeObjectRequest = new GetObjectRequest(bucketName, key)
                    .withRange(0, 9);
            objectPortion = s3Client.getObject(rangeObjectRequest);
            System.out.println("Printing bytes retrieved.");
            displayTextInputStream(objectPortion.getObjectContent());

            // Get an entire object, overriding the specified response headers, and print the object's content.
            ResponseHeaderOverrides headerOverrides = new ResponseHeaderOverrides()
                    .withCacheControl("No-cache")
                    .withContentDisposition("attachment; filename="+fileName);
            GetObjectRequest getObjectRequestHeaderOverride = new GetObjectRequest(bucketName, key)
                    .withResponseHeaders(headerOverrides);
            headerOverrideObject = s3Client.getObject(getObjectRequestHeaderOverride);
            displayTextInputStream(headerOverrideObject.getObjectContent());
//            writeToFile(headerOverrideObject.getObjectContent());
            downloadS3File();
            System.out.println(":::getParquetFile:::");
            try {
                Parquet parquet = parquetReaderUtils.getParquetData("/Users/msuren1/Desktop/rds/src/test/java/storedFiles/test.parquet");
                SimpleGroup simpleGroup = parquet.getData().get(0);
                Long storedString = simpleGroup.getLong("consultation_id", 0);
                System.out.println(storedString);
            }
            catch (IOException e){
                System.out.println(e);
            }
            System.out.println(":::getParquetFile completed:::");
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
        catch (IOException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
        finally {
            // To ensure that the network connection doesn't remain open, close any open input streams.
            if (fullObject != null) {
                fullObject.close();
            }
            if (objectPortion != null) {
                objectPortion.close();
            }
            if (headerOverrideObject != null) {
                headerOverrideObject.close();
            }
        }
    }
    private static void displayTextInputStream(InputStream input) throws IOException {
//        File file="";
        // Read the text input stream one line at a time and display each line.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
    private static void writeToFile(InputStream input) throws IOException {
        System.out.println(":::Writing file locally :::");
        try {
            FileOutputStream out = new FileOutputStream("/Users/msuren1/Desktop/rds/src/test/java/storedFiles/test.parquet");
            byte[] buffer = new byte[1024];
            int read;
            while ((read = input.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.close();
        }catch (IOException ioException) {
            ioException.printStackTrace();
        }
        System.out.println(":::Writing file locally completed:::");
    }
}

