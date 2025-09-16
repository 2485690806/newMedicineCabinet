package leesche.smartrecycling.base.entity;

import java.util.List;

public class ThrFileUploadEntity {

    public List<ThrFileUploadItemEntity> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<ThrFileUploadItemEntity> fileNames) {
        this.fileNames = fileNames;
    }

    List<ThrFileUploadItemEntity> fileNames;
}
