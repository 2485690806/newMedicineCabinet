package leesche.smartrecycling.base.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Containers {
    @Id(autoincrement = true)  // 自增主键
    private Long id;  // 必须是Long类型（兼容GreenDAO）
    private String attemptId;
    private Long timestamp;
    private String accepted;
    private Integer returnCode;
    private String barcode;
    private Integer length;
    private Integer height;
    private Integer weight;
    private String containerType;

    private String material;
    private String logoRecognized;
    private String dmCodeRecognized;
    private String dmRead;
    private Double depositFee;
    private String name = "";
    private String picture = "";

    private String typeCode = "";

    private Integer aiX;
    private Integer aiY;
    private Integer aiW;
    private Integer aiH;
    private Integer metalFrequency = 0;

    private Integer lengthMin;
    private Integer lengthMax;
    private Integer heightMin;
    private Integer heightMax;
    private Integer weightMin;
    private Integer weightMax;

    private boolean currentDetect = false;  // 是否为当前检测
    private boolean doorDetect = false;  // 是否为门检测

    public boolean isCurrentDetect() {
        return currentDetect;
    }

    public void setCurrentDetect(boolean currentDetect) {
        this.currentDetect = currentDetect;
    }

    public boolean isDoorDetect() {
        return doorDetect;
    }

    public void setDoorDetect(boolean doorDetect) {
        this.doorDetect = doorDetect;
    }


    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public Integer getAiH() {
        return aiH;
    }

    public void setAiH(Integer aiH) {
        this.aiH = aiH;
    }

    public Integer getAiW() {
        return aiW;
    }

    public void setAiW(Integer aiW) {
        this.aiW = aiW;
    }

    public Integer getAiX() {
        return aiX;
    }

    public void setAiX(Integer aiX) {
        this.aiX = aiX;
    }

    public Integer getAiY() {
        return aiY;
    }

    public void setAiY(Integer aiY) {
        this.aiY = aiY;
    }

    public String getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(String attemptId) {
        this.attemptId = attemptId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getContainerType() {
        return containerType;
    }

    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }

    public Integer getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Integer cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Double getDepositFee() {
        return depositFee;
    }

    public void setDepositFee(Double depositFee) {
        this.depositFee = depositFee;
    }

    public String getDmCodeRecognized() {
        return dmCodeRecognized;
    }

    public void setDmCodeRecognized(String dmCodeRecognized) {
        this.dmCodeRecognized = dmCodeRecognized;
    }

    public String getDmRead() {
        return dmRead;
    }

    public void setDmRead(String dmRead) {
        this.dmRead = dmRead;
    }

    public Integer getGpuUsage() {
        return gpuUsage;
    }

    public void setGpuUsage(Integer gpuUsage) {
        this.gpuUsage = gpuUsage;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getHeightMax() {
        return heightMax;
    }

    public void setHeightMax(Integer heightMax) {
        this.heightMax = heightMax;
    }

    public Integer getHeightMin() {
        return heightMin;
    }

    public void setHeightMin(Integer heightMin) {
        this.heightMin = heightMin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getLengthMax() {
        return lengthMax;
    }

    public void setLengthMax(Integer lengthMax) {
        this.lengthMax = lengthMax;
    }

    public Integer getLengthMin() {
        return lengthMin;
    }

    public void setLengthMin(Integer lengthMin) {
        this.lengthMin = lengthMin;
    }

    public String getLogoRecognized() {
        return logoRecognized;
    }

    public void setLogoRecognized(String logoRecognized) {
        this.logoRecognized = logoRecognized;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Integer getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(Integer memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public Integer getMetalFrequency() {
        return metalFrequency;
    }

    public void setMetalFrequency(Integer metalFrequency) {
        this.metalFrequency = metalFrequency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNpuUsage() {
        return npuUsage;
    }

    public void setNpuUsage(String npuUsage) {
        this.npuUsage = npuUsage;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(Integer returnCode) {
        this.returnCode = returnCode;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getWeightMax() {
        return weightMax;
    }

    public void setWeightMax(Integer weightMax) {
        this.weightMax = weightMax;
    }

    public Integer getWeightMin() {
        return weightMin;
    }

    public void setWeightMin(Integer weightMin) {
        this.weightMin = weightMin;
    }

    public boolean getCurrentDetect() {
        return this.currentDetect;
    }

    public boolean getDoorDetect() {
        return this.doorDetect;
    }

    private Integer cpuUsage;
    private Integer gpuUsage;

    private String npuUsage;
    private Integer memoryUsage;

    @Generated(hash = 1720453334)
    public Containers(Long id, String attemptId, Long timestamp, String accepted,
            Integer returnCode, String barcode, Integer length, Integer height,
            Integer weight, String containerType, String material,
            String logoRecognized, String dmCodeRecognized, String dmRead,
            Double depositFee, String name, String picture, String typeCode,
            Integer aiX, Integer aiY, Integer aiW, Integer aiH,
            Integer metalFrequency, Integer lengthMin, Integer lengthMax,
            Integer heightMin, Integer heightMax, Integer weightMin,
            Integer weightMax, boolean currentDetect, boolean doorDetect,
            Integer cpuUsage, Integer gpuUsage, String npuUsage,
            Integer memoryUsage) {
        this.id = id;
        this.attemptId = attemptId;
        this.timestamp = timestamp;
        this.accepted = accepted;
        this.returnCode = returnCode;
        this.barcode = barcode;
        this.length = length;
        this.height = height;
        this.weight = weight;
        this.containerType = containerType;
        this.material = material;
        this.logoRecognized = logoRecognized;
        this.dmCodeRecognized = dmCodeRecognized;
        this.dmRead = dmRead;
        this.depositFee = depositFee;
        this.name = name;
        this.picture = picture;
        this.typeCode = typeCode;
        this.aiX = aiX;
        this.aiY = aiY;
        this.aiW = aiW;
        this.aiH = aiH;
        this.metalFrequency = metalFrequency;
        this.lengthMin = lengthMin;
        this.lengthMax = lengthMax;
        this.heightMin = heightMin;
        this.heightMax = heightMax;
        this.weightMin = weightMin;
        this.weightMax = weightMax;
        this.currentDetect = currentDetect;
        this.doorDetect = doorDetect;
        this.cpuUsage = cpuUsage;
        this.gpuUsage = gpuUsage;
        this.npuUsage = npuUsage;
        this.memoryUsage = memoryUsage;
    }

    @Generated(hash = 1085968231)
    public Containers() {
    }


}