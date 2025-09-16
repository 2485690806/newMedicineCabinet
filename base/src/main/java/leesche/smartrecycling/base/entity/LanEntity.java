package leesche.smartrecycling.base.entity;

public class LanEntity {

    private String lanName;
    private boolean isChecked;
    private String lanFlag;

    public String getLanName() {
        return lanName;
    }

    public void setLanName(String lanName) {
        this.lanName = lanName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getLanFlag() {
        return lanFlag;
    }

    public void setLanFlag(String lanFlag) {
        this.lanFlag = lanFlag;
    }

    public LanEntity(String lanName, boolean isChecked, String lanFlag) {
        this.lanName = lanName;
        this.isChecked = isChecked;
        this.lanFlag = lanFlag;
    }
}
