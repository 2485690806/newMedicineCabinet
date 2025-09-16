package leesche.smartrecycling.base.entity;

import com.facebook.stetho.json.annotation.JsonProperty;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import leesche.smartrecycling.base.utils.PingUtil;

@Entity
public class BarCodeInfo {

    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String barcode;
    private String material;
    private String containerType;
    private Integer lengthMin;
    private Integer lengthMax;
    private Integer heightMin;
    private Integer heightMax;
    private Integer weightMin;
    private Integer weightMax;
    private Integer capacity;
    private Double depositFee;
    @com.google.gson.annotations.SerializedName("DMRecognitionType")
    private String dMRecognitionType;

    @Override
    public String toString() {
        return "name: " + name + "\n" +
                "barcode: " + barcode + "\n" +
                "material: " + material + "\n" +
                "containerType: " + containerType + "\n" +
                "lengthMin: " + lengthMin + "\n" +
                "lengthMax: " + lengthMax + "\n" +
                "heightMin: " + heightMin + "\n" +
                "heightMax: " + heightMax + "\n" +
                "weightMin: " + weightMin + "\n" +
                "weightMax: " + weightMax + "\n" +
                "capacity: " + capacity + "\n" +
                "depositFee: " + depositFee + "\n" +
                "DMRecognitionType: " + dMRecognitionType;
    }

    @Generated(hash = 1482620508)
    public BarCodeInfo(Long id, String name, String barcode, String material,
            String containerType, Integer lengthMin, Integer lengthMax,
            Integer heightMin, Integer heightMax, Integer weightMin,
            Integer weightMax, Integer capacity, Double depositFee,
            String dMRecognitionType) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.material = material;
        this.containerType = containerType;
        this.lengthMin = lengthMin;
        this.lengthMax = lengthMax;
        this.heightMin = heightMin;
        this.heightMax = heightMax;
        this.weightMin = weightMin;
        this.weightMax = weightMax;
        this.capacity = capacity;
        this.depositFee = depositFee;
        this.dMRecognitionType = dMRecognitionType;
    }
    @Generated(hash = 1342676696)
    public BarCodeInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getBarcode() {
        return this.barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    public String getMaterial() {
        return this.material;
    }
    public void setMaterial(String material) {
        this.material = material;
    }
    public String getContainerType() {
        return this.containerType;
    }
    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }
    public Integer getLengthMin() {
        return this.lengthMin;
    }
    public void setLengthMin(Integer lengthMin) {
        this.lengthMin = lengthMin;
    }
    public Integer getLengthMax() {
        return this.lengthMax;
    }
    public void setLengthMax(Integer lengthMax) {
        this.lengthMax = lengthMax;
    }
    public Integer getHeightMin() {
        return this.heightMin;
    }
    public void setHeightMin(Integer heightMin) {
        this.heightMin = heightMin;
    }
    public Integer getHeightMax() {
        return this.heightMax;
    }
    public void setHeightMax(Integer heightMax) {
        this.heightMax = heightMax;
    }
    public Integer getWeightMin() {
        return this.weightMin;
    }
    public void setWeightMin(Integer weightMin) {
        this.weightMin = weightMin;
    }
    public Integer getWeightMax() {
        return this.weightMax;
    }
    public void setWeightMax(Integer weightMax) {
        this.weightMax = weightMax;
    }
    public Integer getCapacity() {
        return this.capacity;
    }
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    public Double getDepositFee() {
        return this.depositFee;
    }
    public void setDepositFee(Double depositFee) {
        this.depositFee = depositFee;
    }
    public String getDMRecognitionType() {
        return this.dMRecognitionType;
    }
    public void setDMRecognitionType(String dMRecognitionType) {
        this.dMRecognitionType = dMRecognitionType;
    }

}
