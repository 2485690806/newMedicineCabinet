package leesche.smartrecycling.base.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import lombok.Getter;
import lombok.Setter;
import org.greenrobot.greendao.annotation.Generated;

@Entity
@Getter
@Setter
public class QrCodeBinding {

    @Id(autoincrement = true)  // 自增主键
    private Long id;
    private String gridQrCode;   // 格子自身的二维码
    private String itemQrCode;   // 物品的二维码
    private int level;           // 货道层数
    private String gridNumber;      // 格子编号
    private String originalPath; // 原始照片路径
    private String croppedPath;  // 裁剪后照片路径
    private long timestamp;      // 绑定时间戳
    private String bagId;       // 袋子ID，从后台通过物品二维码请求获取

    public QrCodeBinding(String gridQrCode, String itemQrCode, int level, String gridNumber,
                         String originalPath, String croppedPath) {
        this.gridQrCode = gridQrCode;
        this.itemQrCode = itemQrCode;
        this.level = level;
        this.gridNumber = gridNumber;
        this.originalPath = originalPath;
        this.croppedPath = croppedPath;
        this.timestamp = System.currentTimeMillis();
    }

    @Generated(hash = 397581394)
    public QrCodeBinding(Long id, String gridQrCode, String itemQrCode, int level,
            String gridNumber, String originalPath, String croppedPath, long timestamp,
            String bagId) {
        this.id = id;
        this.gridQrCode = gridQrCode;
        this.itemQrCode = itemQrCode;
        this.level = level;
        this.gridNumber = gridNumber;
        this.originalPath = originalPath;
        this.croppedPath = croppedPath;
        this.timestamp = timestamp;
        this.bagId = bagId;
    }

    @Generated(hash = 1038256413)
    public QrCodeBinding() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGridQrCode() {
        return this.gridQrCode;
    }

    public void setGridQrCode(String gridQrCode) {
        this.gridQrCode = gridQrCode;
    }

    public String getItemQrCode() {
        return this.itemQrCode;
    }

    public void setItemQrCode(String itemQrCode) {
        this.itemQrCode = itemQrCode;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getGridNumber() {
        return this.gridNumber;
    }

    public void setGridNumber(String gridNumber) {
        this.gridNumber = gridNumber;
    }

    public String getOriginalPath() {
        return this.originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getCroppedPath() {
        return this.croppedPath;
    }

    public void setCroppedPath(String croppedPath) {
        this.croppedPath = croppedPath;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getBagId() {
        return this.bagId;
    }

    public void setBagId(String bagId) {
        this.bagId = bagId;
    }
 
}