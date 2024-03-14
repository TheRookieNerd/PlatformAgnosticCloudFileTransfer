package com;

import com.cloudftservice.cloudobject.CloudObject;
import com.cloudftservice.model.CloudStorageService;
import com.cloudftservice.repository.AzureStorageRepository;
import com.cloudftservice.model.FilesOutOfSync;
import com.cloudftservice.util.ExportLocalFilesToCloudTask;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        String relPath = "recorded_files";
        Thread t = new Thread(new ExportLocalFilesToCloudTask(relPath, CloudStorageService.AZURE));
        t.start();

        FilesOutOfSync filesOutOfSync = CloudStorageService.AZURE.getStorageRepository().getFilesOutOfSync(relPath);

    }
}
