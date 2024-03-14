package com.cloudftservice.cloudobject;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.experimental.Delegate;

public class AwsCloudObject implements CloudObject {
    public AwsCloudObject(S3ObjectSummary cloudObject) {
        this.cloudObject = cloudObject;
    }

    @Delegate
    S3ObjectSummary cloudObject;

    @Override
    public String getName() {
        return getKey();
    }

    @Override
    public String getMd5Hash() {
        return getETag();
    }
}
