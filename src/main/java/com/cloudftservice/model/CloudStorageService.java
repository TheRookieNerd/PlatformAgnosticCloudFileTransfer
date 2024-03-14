package com.cloudftservice.model;

import com.cloudftservice.repository.AWSStorageRepository;
import com.cloudftservice.repository.AzureStorageRepository;
import com.cloudftservice.repository.IStorageRepository;

import java.util.function.Supplier;

public enum CloudStorageService {
    AWS(AWSStorageRepository::new),
    AZURE(AzureStorageRepository::new),
    GCP(() -> null);

    final Supplier<? extends IStorageRepository> storageRepositorySupplier;

    CloudStorageService(Supplier<? extends IStorageRepository> storageRepositorySupplier) {
        this.storageRepositorySupplier = storageRepositorySupplier;
    }

    public IStorageRepository getStorageRepository() {
        return storageRepositorySupplier.get();
    }
}

