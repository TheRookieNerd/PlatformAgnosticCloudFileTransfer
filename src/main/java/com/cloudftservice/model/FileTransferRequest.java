package com.cloudftservice.model;

import com.cloudftservice.repository.IStorageRepository;
import com.cloudftservice.util.CFTUtil;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
public class FileTransferRequest {
    public FileTransferRequest(String fileName, String localPath, String cloudPath, FileTransferType fileTransferType, CloudStorageService cloudStorageService) {
        this(fileName, localPath, cloudPath, fileTransferType, cloudStorageService.getStorageRepository());
    }

    private FileTransferRequest(String fileName, String localPath, String cloudPath, FileTransferType fileTransferType, IStorageRepository storageRepository) {
        this.fileName = fileName;
        this.cloudPath = cloudPath;
        this.fileTransferType = fileTransferType;
        this.storageRepository = storageRepository;
        this.localPath = localPath;
    }


    private final String fileName;

    private final String localPath;

    private final String cloudPath;

    private final IStorageRepository storageRepository;

    private final FileTransferType fileTransferType;

    public String getLocalFilePath() {
        return getLocalPath() + File.separator + getFileName();
    }

    public String getCloudFilePath() {
        return getCloudPath().isEmpty() ? getFileName() : getCloudPath() + "/" + getFileName();
    }

    public void upload() throws Exception {
        getStorageRepository().uploadFile(this);
    }
}
