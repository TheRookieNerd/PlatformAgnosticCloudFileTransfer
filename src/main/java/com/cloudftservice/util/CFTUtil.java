package com.cloudftservice.util;

import com.amazonaws.regions.Regions;
import com.cloudftservice.cloudobject.CloudObject;
import com.cloudftservice.config.AwsS3BucketCloudStorageAuth;
import com.cloudftservice.config.AzureBlobCloudStorageAuth;
import com.cloudftservice.model.FileTransferRequest;
import com.cloudftservice.model.FilesOutOfSync;
import com.cloudftservice.repository.IStorageRepository;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;


@Log
public class CFTUtil {

    public final static String BASE_DIR = System.getProperty("server.dir") + File.separator;

    public static Set<String> traverseThroughDirAndGetFiles(String localFilePath) {
        Set<String> localFiles = new HashSet<>();
        File dirToSync = new File(localFilePath);

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
                String relPath = file.getAbsolutePath().replace(BASE_DIR, "");
                relPath = FilenameUtils.separatorsToUnix(relPath);
                localFiles.add(relPath);
            }
        }

        return localFiles;
    }


    public static Date getExpiryTime() {
        return new Date(System.currentTimeMillis() + 3600000); // Expiry in 1 hour (in milliseconds)
    }

    @SneakyThrows
    public static AwsS3BucketCloudStorageAuth getAmazonAuthObject(long userId) {
        return new AwsS3BucketCloudStorageAuth("", "", "", Regions.DEFAULT_REGION);
    }

    public static AzureBlobCloudStorageAuth getAzureAuthObject() {
        return getAzureAuthObject("file-transfer-test");
    }

    public static AzureBlobCloudStorageAuth getAzureAuthObject(String blobContainerName) {
        return new AzureBlobCloudStorageAuth("", blobContainerName, "");
    }

    public static boolean isFileReadable(String filePath) {
        Path path = Paths.get(filePath);
        return Files.exists(path) && Files.isReadable(path);
    }


    public static String getMd5HashForFile(String path) throws IOException {
        Path filePath = Paths.get(path);
        return DigestUtils.md5Hex(Files.readAllBytes(filePath));
    }

    public static FilesOutOfSync getFilesOutOfSync(String directoryName, IStorageRepository repository) throws Exception {
        String localFilePath = BASE_DIR + directoryName;

        Set<String> localFilesRelPaths = traverseThroughDirAndGetFiles(localFilePath);

        List<? extends CloudObject> cloudObjects = repository.getCloudObjects(directoryName);
        Map<String, CloudObject> pathVsCloudObj = cloudObjects.stream().collect(Collectors.toMap(CloudObject::getName, cloudObject -> cloudObject));
        Set<String> cloudFileRelPaths = pathVsCloudObj.keySet();

        Set<String> unknownLocalFiles = new HashSet<>(localFilesRelPaths);
        unknownLocalFiles.removeAll(cloudFileRelPaths);

        Set<String> unknownCloudFiles = new HashSet<>(cloudFileRelPaths);
        unknownCloudFiles.removeAll(localFilesRelPaths);

        Set<String> filesOutOfSync = getFilesOutOfSync(localFilesRelPaths, pathVsCloudObj);

        return new FilesOutOfSync(unknownLocalFiles, unknownCloudFiles, filesOutOfSync);
    }

    private static Set<String> getFilesOutOfSync(Set<String> localFilesRelPaths, Map<String, ? extends CloudObject> pathVsCloudObj) throws IOException {
        Set<String> filesOutOfSync = new HashSet<>();
        for (String localFileRelPath : localFilesRelPaths) {
            String localHash = CFTUtil.getMd5HashForFile(BASE_DIR + localFileRelPath);
            CloudObject cloudObject = pathVsCloudObj.get(localFileRelPath);
            if (!localHash.equals(cloudObject.getMd5Hash())) {
                filesOutOfSync.add(localFileRelPath);
            }
        }
        return filesOutOfSync;

    }
}

