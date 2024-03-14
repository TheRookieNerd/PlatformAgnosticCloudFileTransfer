package com.cloudftservice.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class CloudStorageAuth {
    private String accessKey;
    private String storageName; // bucket name or blob name

//    public abstract String generatePreSignedUrl(String path, FileTransferType fileTransferType) throws Exception;
}
