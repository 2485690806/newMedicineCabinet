package leesche.smartrecycling.base.entity;

import android.graphics.Bitmap;

import java.util.List;

public class AIBottlePrintEntity {

    private Bitmap bitmap;
    private String desc;
    private String phoneNo;
    private String deviceNo;
    private String qrCode;
    private double totalMoney;
    private String orderId;
    private String currencySymbol = "￥";
    private List<DepositItem> depositItems;
    private String printTime;
    private String writeOffNumberMark;
    private String titleText = "";
    private String totalText = "";
    private String desc2;
    private String userName = "";
    private String sn;
    private String site;
    private String address;
    private Bitmap bottomBitmap;
    private boolean isDisplayPoint = false;
    private boolean isDisplayDSR = true;
    private String itemText;
    private String DSRText;
    private String pointsText;
    private String remark;
    private String additionalExplanatoryText;
    public String getAdditionalExplanatoryText() {
        return additionalExplanatoryText;
    }

    public void setAdditionalExplanatoryText(String additionalExplanatoryText) {
        this.additionalExplanatoryText = additionalExplanatoryText;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public List<DepositItem> getDepositItems() {
        return depositItems;
    }

    public void setDepositItems(List<DepositItem> depositItems) {
        this.depositItems = depositItems;
    }

    public String getPrintTime() {
        return printTime;
    }

    public void setPrintTime(String printTime) {
        this.printTime = printTime;
    }

    public static class DepositItem {
        private String itemName;
        private Integer itemQuantity;
        private double itemPrice;
        private double itemAmount;

        public DepositItem(String itemName, Integer itemQuantity, double itemPrice, double itemAmount) {
            this.itemName = itemName;
            this.itemQuantity = itemQuantity;
            this.itemPrice = itemPrice;
            this.itemAmount = itemAmount;
        }

        public String getItemName() {
            return itemName;
        }

        public Integer getItemQuantity() {
            return itemQuantity;
        }

        public double getItemPrice() {
            return itemPrice;
        }

        public double getItemAmount() {
            return itemAmount;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getWriteOffNumberMark() {
        return writeOffNumberMark;
    }

    public void setWriteOffNumberMark(String writeOffNumberMark) {
        this.writeOffNumberMark = writeOffNumberMark;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getTotalText() {
        return totalText;
    }

    public void setTotalText(String totalText) {
        this.totalText = totalText;
    }

    public String getDesc2() {
        return desc2;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Bitmap getBottomBitmap() {
        return bottomBitmap;
    }

    public void setBottomBitmap(Bitmap bottomBitmap) {
        this.bottomBitmap = bottomBitmap;
    }

    public boolean isDisplayPoint() {
        return isDisplayPoint;
    }

    public void setDisplayPoint(boolean displayPoint) {
        isDisplayPoint = displayPoint;
    }

    public boolean isDisplayDSR() {
        return isDisplayDSR;
    }

    public void setDisplayDSR(boolean displayDSR) {
        isDisplayDSR = displayDSR;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    public String getDSRText() {
        return DSRText;
    }

    public void setDSRText(String DSRText) {
        this.DSRText = DSRText;
    }

    public String getPointsText() {
        return pointsText;
    }

    public void setPointsText(String pointsText) {
        this.pointsText = pointsText;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
