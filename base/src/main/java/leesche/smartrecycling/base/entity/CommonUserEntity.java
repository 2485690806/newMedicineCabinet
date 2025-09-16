package leesche.smartrecycling.base.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CommonUserEntity {

    @Id(autoincrement = true)  // 自增主键
    private Long id;  // 必须是Long类型（兼容GreenDAO）

    /**
     * app_user_token : 8f66d8dff932d648bc5631228784919f36eb51fb56bb849bddcbba9f8bfccc27
     * agentId : 1
     * user_type : user
     * app_recycler_token : app_recycler_token
     * role : role
     * headImgUrl : https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLIjn4vga5nPKoY6djblIAPxK0kjh4q2uQeAkED3I0YPNu7XzFteInVuVdIDcuTDWsEZCmia3GvdNA/132
     * phone : 13566986335
     * nickName : 突击手战术
     *licensePlateNumber: 车牌号
     *icCard：IC卡号
     * tareOrder : {"buckleMoney":null,"grossWeight":2.78,"money":null,"tareId":2,"orderId":"202001111206201578715580211","tare":null,"price":null,"buckleWater":null,"typeName":null,"count":null,"bucklePoint":null,"typeCode":null}
     *
     * loginType 登录方式
     * loginValue 登录凭证
     * startTimeStamp 投递起始时间戳
     * endTimeStamp 投递结束时间戳
     */

    private String app_user_token;
    private int agentId;
    private int userId;
    private String user_type;
    private String app_recycler_token;
    private String role;
    private String headImgUrl;
    private String phone;
    private String nickName;
    private String licensePlateNumber = "";
    private String icCard = "";
    private String type = "";

    @Transient  // 这个字段不会被持久化到数据库
    private TareOrderBean tareOrder;
    private int weightValue;
    private String userAddress;
    private int totalPoint;
    private String loginType;
    private String loginValue;
    private long startTimeStamp;
    private long endTimeStamp;

    //第三方添加字段
    @Transient  // 这个字段不会被持久化到数据库
    private String userType;
    @Transient  // 这个字段不会被持久化到数据库
    private String appUserToken;

    @Generated(hash = 1275775341)
    public CommonUserEntity(Long id, String app_user_token, int agentId, int userId, String user_type, String app_recycler_token, String role, String headImgUrl, String phone, String nickName, String licensePlateNumber, String icCard,
            String type, int weightValue, String userAddress, int totalPoint, String loginType, String loginValue, long startTimeStamp, long endTimeStamp) {
        this.id = id;
        this.app_user_token = app_user_token;
        this.agentId = agentId;
        this.userId = userId;
        this.user_type = user_type;
        this.app_recycler_token = app_recycler_token;
        this.role = role;
        this.headImgUrl = headImgUrl;
        this.phone = phone;
        this.nickName = nickName;
        this.licensePlateNumber = licensePlateNumber;
        this.icCard = icCard;
        this.type = type;
        this.weightValue = weightValue;
        this.userAddress = userAddress;
        this.totalPoint = totalPoint;
        this.loginType = loginType;
        this.loginValue = loginValue;
        this.startTimeStamp = startTimeStamp;
        this.endTimeStamp = endTimeStamp;
    }

    @Generated(hash = 477540891)
    public CommonUserEntity() {
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getAppUserToken() {
        return appUserToken;
    }

    public void setAppUserToken(String appUserToken) {
        this.appUserToken = appUserToken;
    }

    public int getWeightValue() {
        return weightValue;
    }

    public void setWeightValue(int weightValue) {
        this.weightValue = weightValue;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getApp_user_token() {
        return app_user_token;
    }

    public void setApp_user_token(String app_user_token) {
        this.app_user_token = app_user_token;
    }

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getApp_recycler_token() {
        return app_recycler_token;
    }

    public void setApp_recycler_token(String app_recycler_token) {
        this.app_recycler_token = app_recycler_token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickName() {
        return nickName;
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }

    public String getIcCard() {
        return icCard;
    }

    public void setIcCard(String icCard) {
        this.icCard = icCard;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public TareOrderBean getTareOrder() {
        return tareOrder;
    }

    public void setTareOrder(TareOrderBean tareOrder) {
        this.tareOrder = tareOrder;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public int getTotalPoint() {
        return totalPoint;
    }

    public void setTotalPoint(int totalPoint) {
        this.totalPoint = totalPoint;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getLoginValue() {
        return loginValue;
    }

    public void setLoginValue(String loginValue) {
        this.loginValue = loginValue;
    }

    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public long getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setEndTimeStamp(long endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }



    @Override
    public String toString() {
        return "CommonUserEntity{" +
                "app_user_token='" + app_user_token + '\'' +
                ", agentId=" + agentId +
                ", userId=" + userId +
                ", user_type='" + user_type + '\'' +
                ", app_recycler_token='" + app_recycler_token + '\'' +
                ", role='" + role + '\'' +
                ", headImgUrl='" + headImgUrl + '\'' +
                ", phone='" + phone + '\'' +
                ", nickName='" + nickName + '\'' +
                ", licensePlateNumber='" + licensePlateNumber + '\'' +
                ", icCard='" + icCard + '\'' +
                ", tareOrder=" + tareOrder +
                ", weightValue=" + weightValue +
                ", userAddress='" + userAddress + '\'' +
                ", totalPoint=" + totalPoint +
                ", loginType='" + loginType + '\'' +
                ", loginValue='" + loginValue + '\'' +
                ", startTimeStamp=" + startTimeStamp +
                ", endTimeStamp=" + endTimeStamp +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
