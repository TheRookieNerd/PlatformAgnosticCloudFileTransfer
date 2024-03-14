package com.cloudftservice.config;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.cloudftservice.model.FileTransferType;
import com.cloudftservice.util.CFTUtil;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

@Getter
public class AwsS3BucketCloudStorageAuth extends CloudStorageAuth {
    private final String secretKey;
    private final Regions region;

    public AwsS3BucketCloudStorageAuth(String accessKey, String storageName, String secretKey, Regions region) {
        super(accessKey, storageName);
        this.secretKey = secretKey;
        this.region = region;
    }


    private void checkConnection(AmazonS3 amazonS3Client) throws SdkClientException {
        // listBuckets() throws if
        // bucket does not exist
        // access key id is invalid
        // secret access key is invalid
        if (CollectionUtils.isEmpty(amazonS3Client.listBuckets())) {
            throw new IllegalArgumentException("No buckets created");
        }
    }

    public AmazonS3 getAmazonS3Client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(getAccessKey(), getSecretKey());
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(getRegion())
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}