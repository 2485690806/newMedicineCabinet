package leesche.smartrecycling.base.entity;

public class RecycleType {

    private int id;
    private String name;
    private int resId;
    private String desc;

    public RecycleType(int id, String name, int resId, String desc) {
        this.id = id;
        this.name = name;
        this.resId = resId;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
