package com.cloudftservice.cloudobject;

import com.microsoft.azure.storage.blob.CloudBlob;
import lombok.experimental.Delegate;

import java.util.Base64;

public class AzureCloudObject implements CloudObject{

    public AzureCloudObject(CloudBlob cloudObject) {
        this.cloudObject = cloudObject;
    }

    @Delegate
    CloudBlob cloudObject;

    @Override
    public String getMd5Hash() {
        String base64String = getProperties().getContentMD5();
        byte[] byteArray = Base64.getDecoder().decode(base64String);
        return bytesToHex(byteArray).toLowerCase();
    }

    private String bytesToHex(byte[] byteArray) {
        byte[] bytes = {-1, 0, 1, 2, 3 };
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
