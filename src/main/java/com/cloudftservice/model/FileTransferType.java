package com.cloudftservice.model;

import com.amazonaws.HttpMethod;
import com.microsoft.azure.storage.blob.SharedAccessBlobPermissions;
import lombok.Getter;

@Getter
public enum FileTransferType {
    DOWNLOAD(HttpMethod.GET, SharedAccessBlobPermissions.READ),
    UPLOAD(HttpMethod.PUT, SharedAccessBlobPermissions.CREATE),
    DELETE(HttpMethod.DELETE, SharedAccessBlobPermissions.DELETE);

    FileTransferType(HttpMethod awsHttpMethod, SharedAccessBlobPermissions azurePermissions) {
        this.awsHttpMethod = awsHttpMethod;
        this.azurePermissions = azurePermissions;
    }

    private final HttpMethod awsHttpMethod;
    private final SharedAccessBlobPermissions azurePermissions;

}
