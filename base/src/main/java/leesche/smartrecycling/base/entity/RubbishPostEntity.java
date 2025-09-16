package leesche.smartrecycling.base.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Arrays;

@Entity
public class RubbishPostEntity {

    @Id(autoincrement = true)  // 自增主键
    private Long id;  // 必须是Long类型（兼容GreenDAO）
    private long doorOpenId;
    private String boxCode;
    private int lockNO = 0; //锁编号
    private String typeCode;
    private int count;
    private String typeName;
    private double price;
    private String unit;
    private int cost;
    private String errMsg;
    @Transient
    private String[] images;
    private String localPath;
    private double minPostingValue = 0;
    private int dailyPostingCount = 0;
    private int userOneTimeCount = 0;
    private long startDeliveryTime = 0;
    private String userId;
    private String orderId;
    @Transient
    private boolean isNeedWeighting = true;
    @Transient
    private int depositStatus = -1;

    //第三方添加参数
    private String units;
    private String barcode = "1111111111111";

    public RubbishPostEntity() {
    }

    public RubbishPostEntity(long openDoorId, String boxCode, String typeCode, int count
            , String typeName, double price, String unit, int cost) {
        this.doorOpenId = openDoorId;
        this.boxCode = boxCode;
        this.typeCode = typeCode;
        this.count = count;
        this.typeName = typeName;
        this.price = price;
        this.unit = unit;
        this.cost = cost;
    }

    public RubbishPostEntity(long doorOpenId, String boxCode, String typeCode, int count
            , String typeName, double price, String unit, int cost, String[] images) {
        this.doorOpenId = doorOpenId;
        this.boxCode = boxCode;
        this.typeCode = typeCode;
        this.count = count;
        this.typeName = typeName;
        this.price = price;
        this.unit = unit;
        this.cost = cost;
        this.images = images;
    }

    @Generated(hash = 1455032434)
    public RubbishPostEntity(Long id, long doorOpenId, String boxCode, int lockNO,
            String typeCode, int count, String typeName, double price, String unit, int cost,
            String errMsg, String localPath, double minPostingValue, int dailyPostingCount,
            int userOneTimeCount, long startDeliveryTime, String userId, String orderId,
            String units, String barcode) {
        this.id = id;
        this.doorOpenId = doorOpenId;
        this.boxCode = boxCode;
        this.lockNO = lockNO;
        this.typeCode = typeCode;
        this.count = count;
        this.typeName = typeName;
        this.price = price;
        this.unit = unit;
        this.cost = cost;
        this.errMsg = errMsg;
        this.localPath = localPath;
        this.minPostingValue = minPostingValue;
        this.dailyPostingCount = dailyPostingCount;
        this.userOneTimeCount = userOneTimeCount;
        this.startDeliveryTime = startDeliveryTime;
        this.userId = userId;
        this.orderId = orderId;
        this.units = units;
        this.barcode = barcode;
    }

    public long getStartDeliveryTime() {
        return startDeliveryTime;
    }

    public void setStartDeliveryTime(long startDeliveryTime) {
        this.startDeliveryTime = startDeliveryTime;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public int getLockNO() {
        return lockNO;
    }

    public void setLockNO(int lockNO) {
        this.lockNO = lockNO;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public long getOpenDoorId() {
        return doorOpenId;
    }

    public void setOpenDoorId(long openDoorId) {
        this.doorOpenId = openDoorId;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public void setImages(String path) {
        this.images[images.length] = path;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public double getMinPostingValue() {
        return minPostingValue;
    }

    public void setMinPostingValue(double minPostingValue) {
        this.minPostingValue = minPostingValue;
    }

    public int getDailyPostingCount() {
        return dailyPostingCount;
    }

    public void setDailyPostingCount(int dailyPostingCount) {
        this.dailyPostingCount = dailyPostingCount;
    }

    public int getUserOneTimeCount() {
        return userOneTimeCount;
    }

    public void setUserOneTimeCount(int userOneTimeCount) {
        this.userOneTimeCount = userOneTimeCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    public long getDoorOpenId() {
        return this.doorOpenId;
    }

    public void setDoorOpenId(long doorOpenId) {
        this.doorOpenId = doorOpenId;
    }

    public boolean isNeedWeighting() {
        return isNeedWeighting;
    }

    public void setNeedWeighting(boolean needWeighting) {
        isNeedWeighting = needWeighting;
    }

    public int getDepositStatus() {
        return depositStatus;
    }

    public void setDepositStatus(int depositStatus) {
        this.depositStatus = depositStatus;
    }

    @Override
    public String toString() {
        return "RubbishPostEntity{" +
                "boxCode='" + boxCode + '\'' +
                ", lockNO=" + lockNO +
                ", count=" + count +
                ", typeName='" + typeName + '\'' +
                ", cost=" + cost +
                ", images=" + Arrays.toString(images) +
                ", isNeedWeighting=" + isNeedWeighting +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
