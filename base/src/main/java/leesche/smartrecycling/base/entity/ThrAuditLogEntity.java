package leesche.smartrecycling.base.entity;

import java.util.ArrayList;
import java.util.List;

import leesche.smartrecycling.base.common.Constants;

public class ThrAuditLogEntity {

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getPeriodStartTime() {
        return periodStartTime;
    }

    public void setPeriodStartTime(Long periodStartTime) {
        this.periodStartTime = periodStartTime;
    }

    public Long getPeriodEndTime() {
        return periodEndTime;
    }

    public void setPeriodEndTime(Long periodEndTime) {
        this.periodEndTime = periodEndTime;
    }

    public List<ThrAuditLogItemEntity> getLogMessages() {
        if(logMessages == null) {
            logMessages = new ArrayList<>();
        }
        if(serialNumber == null) {
            serialNumber = Constants.DEVICE_CODE;

        }
        if(timestamp == null) {
            timestamp = System.currentTimeMillis() / 1000;
        }
        if(periodStartTime == null) {
            periodStartTime = System.currentTimeMillis() / 1000;
        }
        if(periodEndTime == null) {
            periodEndTime = System.currentTimeMillis() / 1000;
        }
        return logMessages;
    }

    public void setLogMessages(List<ThrAuditLogItemEntity> logMessages) {
        this.logMessages = logMessages;
    }

    private String serialNumber;
    private Long timestamp;
    private Long periodStartTime;
    private Long periodEndTime;
    private List<ThrAuditLogItemEntity> logMessages = new ArrayList<>();
}
