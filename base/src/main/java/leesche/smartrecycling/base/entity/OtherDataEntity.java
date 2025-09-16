package leesche.smartrecycling.base.entity;

public class OtherDataEntity {

    private int boxCode;
    private String typeCode;
    private String typeName;
    private int overflowDegree;
    private String alarmType;
    private String alarmStatus;

    public int getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(int boxCode) {
        this.boxCode = boxCode;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getOverflowDegree() {
        return overflowDegree;
    }

    public void setOverflowDegree(int overflowDegree) {
        this.overflowDegree = overflowDegree;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(String alarmStatus) {
        this.alarmStatus = alarmStatus;
    }
}
