package com.cloudftservice.model;

import lombok.Getter;

import java.util.Set;

@Getter
public class FilesOutOfSync {
    public FilesOutOfSync(Set<String> unknownLocalFiles, Set<String> unknownCloudFiles, Set<String> filesChanged) {
        this.unknownLocalFiles = unknownLocalFiles;
        this.unknownCloudFiles = unknownCloudFiles;
        this.filesChanged = filesChanged;
    }

    private final Set<String> unknownLocalFiles;
    private final Set<String> unknownCloudFiles;

    private final Set<String> filesChanged;

    @Override
    public String toString() {
        return "FilesOutOfSync{" +
                "unknownLocalFiles=" + unknownLocalFiles +
                ", unknownCloudFiles=" + unknownCloudFiles +
                ", filesChanged=" + filesChanged +
                '}';
    }
}
