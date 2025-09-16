package leesche.smartrecycling.base.entity;

public class UnitEntity {

    public static int TYPE_WEIGHT = 6;
    public static int WEIGHT_MEASURE = 1;
    public static int WEIGHT_ZEROING = 2;

    public static int TYPE_AI = 17;
    public static int AI_GET_DIMENSION = 0;
    public static int AI_UPLOAD_TO_SERVER = 1;

    /**
     * 编号
     */
    private int unit_no;

    /**
     *类型
     */
    private int unit_type;

    /**
     *名称
     */
    private String unit_name;
    private String value;

    public UnitEntity(int unit_no, String unit_name, String value) {
        this.unit_no = unit_no;
        this.unit_name = unit_name;
        this.value = value;
    }

    public UnitEntity(int unit_no, int unit_type, String unit_name) {
        this.unit_no = unit_no;
        this.unit_type = unit_type;
        this.unit_name = unit_name;
    }

    public int getUnit_no() {
        return unit_no;
    }

    public void setUnit_no(int unit_no) {
        this.unit_no = unit_no;
    }

    public int getUnit_type() {
        return unit_type;
    }

    public void setUnit_type(int unit_type) {
        this.unit_type = unit_type;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
