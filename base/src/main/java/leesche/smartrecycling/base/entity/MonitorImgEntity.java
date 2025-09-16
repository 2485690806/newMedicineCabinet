package leesche.smartrecycling.base.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class MonitorImgEntity {

    @Id(autoincrement = true)
    private Long id;

    private String orderId;
    private String monitorAddress;
    private String imgLocalSavePath;
    private String ossObjectKey;
    private int haveUploadCount;
    private String lastUploadErrorInfo;

    private Integer toService = 0; // 0: 上传我们服务器，1：上传第三方

    @Generated(hash = 785358109)
    public MonitorImgEntity(Long id, String orderId, String monitorAddress,
            String imgLocalSavePath, String ossObjectKey, int haveUploadCount,
            String lastUploadErrorInfo, Integer toService) {
        this.id = id;
        this.orderId = orderId;
        this.monitorAddress = monitorAddress;
        this.imgLocalSavePath = imgLocalSavePath;
        this.ossObjectKey = ossObjectKey;
        this.haveUploadCount = haveUploadCount;
        this.lastUploadErrorInfo = lastUploadErrorInfo;
        this.toService = toService;
    }

    @Generated(hash = 1520686609)
    public MonitorImgEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMonitorAddress() {
        return this.monitorAddress;
    }

    public void setMonitorAddress(String monitorAddress) {
        this.monitorAddress = monitorAddress;
    }

    public String getImgLocalSavePath() {
        return this.imgLocalSavePath;
    }

    public void setImgLocalSavePath(String imgLocalSavePath) {
        this.imgLocalSavePath = imgLocalSavePath;
    }

    public String getOssObjectKey() {
        return this.ossObjectKey;
    }

    public void setOssObjectKey(String ossObjectKey) {
        this.ossObjectKey = ossObjectKey;
    }

    public int getHaveUploadCount() {
        return this.haveUploadCount;
    }

    public void setHaveUploadCount(int haveUploadCount) {
        this.haveUploadCount = haveUploadCount;
    }

    public String getLastUploadErrorInfo() {
        return this.lastUploadErrorInfo;
    }

    public void setLastUploadErrorInfo(String lastUploadErrorInfo) {
        this.lastUploadErrorInfo = lastUploadErrorInfo;
    }

    public Integer getToService() {
        return this.toService;
    }

    public void setToService(Integer toService) {
        this.toService = toService;
    }


}
