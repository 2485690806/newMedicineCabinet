package leesche.smartrecycling.base.entity;

public class ThrFileUploadItemEntity {

    public String getPicType() {
        return picType;
    }

    public void setPicType(String picType) {
        this.picType = picType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String picType;
    private String url;
}
