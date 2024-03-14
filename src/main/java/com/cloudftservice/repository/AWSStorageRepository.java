package com.cloudftservice.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.cloudftservice.cloudobject.AwsCloudObject;
import com.cloudftservice.cloudobject.CloudObject;
import com.cloudftservice.config.AwsS3BucketCloudStorageAuth;
import com.cloudftservice.model.FileTransferRequest;
import com.cloudftservice.model.FilesOutOfSync;
import com.cloudftservice.util.CFTUtil;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class AWSStorageRepository implements IStorageRepository {

    public AWSStorageRepository() {
        this.authObject = CFTUtil.getAmazonAuthObject(1L);
    }

    @Delegate
    private final AwsS3BucketCloudStorageAuth authObject;


    @Override
    public FilesOutOfSync getFilesOutOfSync(String directoryName) throws Exception {
        return CFTUtil.getFilesOutOfSync(directoryName, this);
    }

    @Override
    public boolean uploadFile(FileTransferRequest request) {
        File file = new File(request.getLocalFilePath());
        AmazonS3 amazonS3Client = getAmazonS3Client();
        PutObjectResult putObjectResult = amazonS3Client.putObject(new PutObjectRequest(getStorageName(), request.getCloudFilePath(), file));
        return putObjectResult != null;
    }

    @Override
    public List<? extends CloudObject> getCloudObjects(String directoryName) {
        return getS3ObjsInDir(directoryName);
    }

    @SneakyThrows
    private List<AwsCloudObject> getS3ObjsInDir(String directoryName) {
        String bucketName = getStorageName();
        AmazonS3 amazonS3Client = getAmazonS3Client();

        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(directoryName + "/");
        ListObjectsV2Result listing = amazonS3Client.listObjectsV2(req);
        return listing.getObjectSummaries()
                .stream()
                .map(AwsCloudObject::new)
                .collect(Collectors.toList());
    }
}
