package com.cloudftservice.config;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import lombok.Getter;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

@Getter
public class AzureBlobCloudStorageAuth extends CloudStorageAuth {

    public AzureBlobCloudStorageAuth(String accessKey, String storageName, String accountName) {
        super(accessKey, storageName);
        this.accountName = accountName;
    }

    private final String accountName;

    public String getConnectionString() {
        return "DefaultEndpointsProtocol=https;AccountName=" + getAccountName() + ";AccountKey=" + getAccessKey() + ";EndpointSuffix=core.windows.net";
    }

    public CloudBlobContainer getCloudBlobContainer() throws URISyntaxException, InvalidKeyException, StorageException {
        CloudBlobClient blobClient = getCloudBlobClient();
        return blobClient.getContainerReference(getStorageName());
    }

    private CloudBlobClient getCloudBlobClient() throws URISyntaxException, InvalidKeyException {
        final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=" + getAccountName() + ";AccountKey=" + getAccessKey() + ";EndpointSuffix=core.windows.net";
        CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
        return storageAccount.createCloudBlobClient();
    }
}
