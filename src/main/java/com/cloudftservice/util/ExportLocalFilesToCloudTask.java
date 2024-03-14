package com.cloudftservice.util;

import com.cloudftservice.model.CloudStorageService;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Log
public class ExportLocalFilesToCloudTask implements Runnable{
    private final String localDirectoryPath;
    private final CloudStorageService cloudStorageService;

    public ExportLocalFilesToCloudTask(String relPath, CloudStorageService cloudStorageService) {
        Validate.notNull(cloudStorageService);
        Validate.notNull(relPath);
        this.localDirectoryPath = System.getProperty("server.dir") + File.separator + relPath;
        this.cloudStorageService = cloudStorageService;
    }

    public void exportFiles() throws Exception {
        List<File> filesToUpload = traverseThroughDir();
        uploadFilesInParallel(filesToUpload);
    }

    private List<File> traverseThroughDir() {
        List<File> filesToUpload = new ArrayList<>();

        File dirToSync = new File(localDirectoryPath);

        Deque<File> queue = new ArrayDeque<>();
        queue.offer(dirToSync);
        while (!queue.isEmpty()) {
            File currentFile = queue.poll();
            File[] filesInsideCurrFile = Optional.ofNullable(currentFile.listFiles()).orElse(new File[]{});
            for (File file : filesInsideCurrFile) {
                if (file.isDirectory()) {
                    queue.add(file);
                    continue;
                }
                filesToUpload.add(file);
            }
        }
        return filesToUpload;
    }

    private void uploadFilesInParallel(List<? extends File> filesToUpload) throws ExecutionException, InterruptedException {
        ExecutorService executorService = new CFTThreadPoolExecutor(10);
        List<Future<String>> futures = filesToUpload.stream()
                .map(file -> executorService.submit(new ExportTask(file, this.cloudStorageService)))
                .collect(Collectors.toList());
        for (Future<String> future : futures) {
            future.get();
        }
        executorService.shutdownNow();
    }

    @SneakyThrows
    @Override
    public void run() {
       exportFiles();
    }
}
