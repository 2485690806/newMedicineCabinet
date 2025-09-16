package leesche.smartrecycling.base.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class UploadRunningLogEntity {


    @Id(autoincrement = true)
    Long orderId;
    String status;
    String reason;
    Long preOrderId;
    Long timestamp;
    String remark;
    @Generated(hash = 1739499837)
    public UploadRunningLogEntity(Long orderId, String status, String reason,
            Long preOrderId, Long timestamp, String remark) {
        this.orderId = orderId;
        this.status = status;
        this.reason = reason;
        this.preOrderId = preOrderId;
        this.timestamp = timestamp;
        this.remark = remark;
    }
    @Generated(hash = 455912175)
    public UploadRunningLogEntity() {
    }
    public Long getOrderId() {
        return this.orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getReason() {
        return this.reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public Long getPreOrderId() {
        return this.preOrderId;
    }
    public void setPreOrderId(Long preOrderId) {
        this.preOrderId = preOrderId;
    }
    public Long getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    public String getRemark() {
        return this.remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
