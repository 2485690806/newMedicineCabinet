package leesche.smartrecycling.base.entity;

import java.util.List;

public class RubbishType {


    /**
     * backgroundColor : #E2F50A
     * code : 04
     * detail_type : [{"unit":"","code":"can","name":"罐头","icon":"//youyicloud-v2.oss-cn-shanghai.aliyuncs.com/recycling/type-detail-icon/can.png","typeId":"1-04","id":11},{"unit":"","code":"wire","name":"电线","icon":"//youyicloud-v2.oss-cn-shanghai.aliyuncs.com/recycling/type-detail-icon/wire.png","typeId":"1-04","id":12},{"unit":"","code":"pan","name":"平底锅","icon":"//youyicloud-v2.oss-cn-shanghai.aliyuncs.com/recycling/type-detail-icon/pan.png","typeId":"1-04","id":13},{"unit":"","code":"metal_tablewale","name":"金属餐具","icon":"//youyicloud-v2.oss-cn-shanghai.aliyuncs.com/recycling/type-detail-icon/metal_tablewale.png","typeId":"1-04","id":14}]
     * icon : https://youyicloud-v2.oss-cn-shanghai.aliyuncs.com/recycling/type-icon/04-1.png
     * auditThreshold : 1
     * box : [{"currentHeigth":133,"compressSwitch":"n","currentHeight":133,"takePhotoSwitch":"y","fillValue":0,"closeBoxSwitch":"n","overflowDegree":143,"boxCode":"1"}]
     * unit : kg
     * minPostingValue : 0
     * comNumber : 1
     * price : 10
     * typeOpenTimeArea :
     * name : 金属
     * id : 1-04
     * dailyPostingCount : null
     * userOneTimeCount ：null
     * 客户端增加一些参数
     * count: 实时重量
     * doorOpenStatus: 箱门打开状态
     * isClick:是否可点击
     * displayPrice 显示价格
     */

    private String backgroundColor;
    private String code;
    private String icon;
    private int auditThreshold;
    private String unit;
    private int minPostingValue = 0;
    private int comNumber;
    private int price;
    private String typeOpenTimeArea;
    private String name;
    private String id;
    private int dailyPostingCount = 0;
    private int userOneTimeCount = 0;
    private List<DetailTypeBean> detail_type;
    private List<BoxBean> box;

    private int count = 0;
    private boolean doorOpenStatus = false;
    private boolean isClick = true;
    private double displayPrice = 0;

    private String timePeriodPrice;

    public String getTimePeriodPrice() {
        return timePeriodPrice;
    }

    public void setTimePeriodPrice(String timePeriodPrice) {
        this.timePeriodPrice = timePeriodPrice;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getAuditThreshold() {
        return auditThreshold;
    }

    public void setAuditThreshold(int auditThreshold) {
        this.auditThreshold = auditThreshold;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getMinPostingValue() {
        return minPostingValue;
    }

    public void setMinPostingValue(int minPostingValue) {
        this.minPostingValue = minPostingValue;
    }

    public int getComNumber() {
        return comNumber;
    }

    public void setComNumber(int comNumber) {
        this.comNumber = comNumber;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getTypeOpenTimeArea() {
        return typeOpenTimeArea;
    }

    public void setTypeOpenTimeArea(String typeOpenTimeArea) {
        this.typeOpenTimeArea = typeOpenTimeArea;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<DetailTypeBean> getDetail_type() {
        return detail_type;
    }

    public void setDetail_type(List<DetailTypeBean> detail_type) {
        this.detail_type = detail_type;
    }

    public List<BoxBean> getBox() {
        return box;
    }

    public void setBox(List<BoxBean> box) {
        this.box = box;
    }

    public static class DetailTypeBean {
        /**
         * unit :
         * code : can
         * name : 罐头
         * icon : //youyicloud-v2.oss-cn-shanghai.aliyuncs.com/recycling/type-detail-icon/can.png
         * typeId : 1-04
         * id : 11
         */

        private String unit;
        private String code;
        private String name;
        private String icon;
        private String typeId;
        private int id;

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getTypeId() {
            return typeId;
        }

        public void setTypeId(String typeId) {
            this.typeId = typeId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class BoxBean {
        /**
         * currentHeigth : 133
         * compressSwitch : n
         * currentHeight : 133
         * takePhotoSwitch : y
         * fillValue : 0
         * closeBoxSwitch : n
         * overflowDegree : 143
         * openOtherBoxSwitch
         * boxCode : 1
         */

        private int currentHeigth;
        private String compressSwitch;
        private int currentHeight;
        private String takePhotoSwitch;
        private int fillValue;
        private String closeBoxSwitch;
        private String openOtherBoxSwitch;
        private int overflowDegree;
        private  int currentCapacity = 0;
        private String boxCode;

        public int getCurrentCapacity() {
            return currentCapacity;
        }

        public void setCurrentCapacity(int currentCapacity) {
            this.currentCapacity = currentCapacity;
        }

        public int getCurrentHeigth() {
            return currentHeigth;
        }

        public void setCurrentHeigth(int currentHeigth) {
            this.currentHeigth = currentHeigth;
        }

        public String getCompressSwitch() {
            return compressSwitch;
        }

        public void setCompressSwitch(String compressSwitch) {
            this.compressSwitch = compressSwitch;
        }

        public int getCurrentHeight() {
            return currentHeight;
        }

        public void setCurrentHeight(int currentHeight) {
            this.currentHeight = currentHeight;
        }

        public String getTakePhotoSwitch() {
            return takePhotoSwitch;
        }

        public void setTakePhotoSwitch(String takePhotoSwitch) {
            this.takePhotoSwitch = takePhotoSwitch;
        }

        public int getFillValue() {
            return fillValue;
        }

        public void setFillValue(int fillValue) {
            this.fillValue = fillValue;
        }

        public String getCloseBoxSwitch() {
            return closeBoxSwitch;
        }

        public void setCloseBoxSwitch(String closeBoxSwitch) {
            this.closeBoxSwitch = closeBoxSwitch;
        }

        public String getOpenOtherBoxSwitch() {
            return openOtherBoxSwitch;
        }

        public void setOpenOtherBoxSwitch(String openOtherBoxSwitch) {
            this.openOtherBoxSwitch = openOtherBoxSwitch;
        }

        public int getOverflowDegree() {
            return overflowDegree;
        }

        public void setOverflowDegree(int overflowDegree) {
            this.overflowDegree = overflowDegree;
        }

        public String getBoxCode() {
            return boxCode;
        }

        public void setBoxCode(String boxCode) {
            this.boxCode = boxCode;
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isDoorOpenStatus() {
        return doorOpenStatus;
    }

    public void setDoorOpenStatus(boolean doorOpenStatus) {
        this.doorOpenStatus = doorOpenStatus;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public double getDisplayPrice() {
        return displayPrice;
    }

    public void setDisplayPrice(double displayPrice) {
        this.displayPrice = displayPrice;
    }
}
