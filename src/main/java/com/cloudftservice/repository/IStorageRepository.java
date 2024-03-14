package com.cloudftservice.repository;


import com.cloudftservice.cloudobject.CloudObject;
import com.cloudftservice.model.FileTransferRequest;
import com.cloudftservice.model.FilesOutOfSync;

import java.util.List;

public interface IStorageRepository {

    FilesOutOfSync getFilesOutOfSync(String directoryName) throws Exception;

    boolean uploadFile(FileTransferRequest request) throws Exception;

    List<? extends CloudObject> getCloudObjects(String directoryName);

}
