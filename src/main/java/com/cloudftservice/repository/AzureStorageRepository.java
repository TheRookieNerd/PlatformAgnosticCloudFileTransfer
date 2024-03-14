package com.cloudftservice.repository;

import com.cloudftservice.cloudobject.AzureCloudObject;
import com.cloudftservice.cloudobject.CloudObject;
import com.cloudftservice.config.AzureBlobCloudStorageAuth;
import com.cloudftservice.model.FileTransferRequest;
import com.cloudftservice.model.FilesOutOfSync;
import com.cloudftservice.util.CFTUtil;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static com.cloudftservice.util.CFTUtil.getAzureAuthObject;


public class AzureStorageRepository implements IStorageRepository {

    @Delegate
    private final AzureBlobCloudStorageAuth authObject;

    public AzureStorageRepository() {
        this.authObject = getAzureAuthObject();
    }

    @Override
    public FilesOutOfSync getFilesOutOfSync(String directoryName) throws Exception {
        return CFTUtil.getFilesOutOfSync(directoryName, this);
    }

    @Override
    public boolean uploadFile(FileTransferRequest request) throws URISyntaxException, InvalidKeyException, StorageException, IOException {
        String connectionString = getConnectionString();
        CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);

        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

        CloudBlobContainer container = blobClient.getContainerReference(getStorageName());
        container.createIfNotExists();

        CloudBlockBlob blob = container.getBlockBlobReference(request.getCloudFilePath());
        blob.getProperties().setContentMD5(CFTUtil.getMd5HashForFile(request.getLocalFilePath()));
        File file = new File(request.getLocalFilePath());
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            blob.upload(fileInputStream, file.length());
        }
        return true;
    }

    @Override
    public List<? extends CloudObject> getCloudObjects(String directoryName) {
        return getBlobObsInDir(directoryName);
    }

    @SneakyThrows
    private List<AzureCloudObject> getBlobObsInDir(String blobContainerName) {
        CloudBlobContainer cloudBlobContainer = getCloudBlobContainer();
        Iterable<ListBlobItem> listBlobItems = cloudBlobContainer.listBlobs(blobContainerName);

        List<AzureCloudObject> azureCloudObjects = new ArrayList<>();
        for (ListBlobItem directory : listBlobItems) {
            Deque<CloudBlobDirectory> queue = new ArrayDeque<>();
            queue.add((CloudBlobDirectory) directory);
            while (!queue.isEmpty()) {
                CloudBlobDirectory currentDir = queue.poll();
                for (ListBlobItem listBlob : currentDir.listBlobs()) {
                    if (listBlob instanceof CloudBlobDirectory) {
                        queue.add((CloudBlobDirectory) listBlob);
                        continue;
                    }
                    CloudBlob cloudBlob = (CloudBlob) listBlob;
                    azureCloudObjects.add(new AzureCloudObject(cloudBlob));
                }
            }

        }
        return azureCloudObjects;
    }
}
