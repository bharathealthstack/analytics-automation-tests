package s3;
import java.io.IOException;

public class S3Tests {
    S3Operations s3Operations = new S3Operations();

    public void test1 ()throws IOException

    {
        System.out.println(":::Inside s3DataTest:::");
        System.out.println(":::Listing buckets:::");
//        s3Operations.listAllBuckets();
        System.out.println(":::Parquet file:::");
//        s3Operations.getObject("test");
        s3Operations.downloadS3File();
        System.out.println(":::Outside s3DataTest:::");
    }

}
