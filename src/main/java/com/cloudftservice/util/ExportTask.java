package com.cloudftservice.util;

import com.cloudftservice.model.CloudStorageService;
import com.cloudftservice.model.FileTransferRequest;
import com.cloudftservice.model.FileTransferType;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.concurrent.Callable;

@Log
public class ExportTask implements Callable<String> {
    private final File file;
    private final CloudStorageService cloudStorageService;

    public ExportTask(File file, CloudStorageService cloudStorageService) {
        this.file = file;
        this.cloudStorageService = cloudStorageService;
    }

    private void uploadFileToCloud() throws Exception {
        String cloudPath = getCloudPath();
        FileTransferRequest fileTransferRequest = new FileTransferRequest(file.getName(), file.getParent(), cloudPath, FileTransferType.UPLOAD, this.cloudStorageService);
        fileTransferRequest.upload();
    }

    private String getCloudPath() {
        return FilenameUtils.separatorsToUnix(file.getParent().replace(System.getProperty("server.dir") + File.separator, ""));
    }

    @SneakyThrows
    @Override
    public String call() {
        log.info("LOGGING FROM EXPORT TASK, Running in thread " + Thread.currentThread().getName() + "for file " + file.getAbsolutePath());
        uploadFileToCloud();
        return "";
    }
}
