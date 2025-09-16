package leesche.smartrecycling.base.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

@Entity
public class DeliveryRecordEntity {


    @Id(autoincrement = true)
    private Long id;

    /**
     * 投递订单ID
     */
    private String order_id;

    /**
     * 投递内容
     */
    private String delivery_content;


    /**
     * 用户Token
     */
    private String user_token;

    /**
     * 当前投递时间
     */
    private String date_time;

    /**
     * 当前投递时间(毫)秒数
     */
    private long time_mills;

    /**
     * 登录类型
     */
    private String loginType;

    /**
     * 登录值
     */
    private String loginValue;
    @NotNull
    private Integer uploadStatus = 0;

    private String rvmDetail;

    private Integer toPostService = 0; // 0:我们服务器，1：第三方服务器
    @Generated(hash = 640119370)
    public DeliveryRecordEntity(Long id, String order_id, String delivery_content,
            String user_token, String date_time, long time_mills, String loginType,
            String loginValue, @NotNull Integer uploadStatus, String rvmDetail,
            Integer toPostService) {
        this.id = id;
        this.order_id = order_id;
        this.delivery_content = delivery_content;
        this.user_token = user_token;
        this.date_time = date_time;
        this.time_mills = time_mills;
        this.loginType = loginType;
        this.loginValue = loginValue;
        this.uploadStatus = uploadStatus;
        this.rvmDetail = rvmDetail;
        this.toPostService = toPostService;
    }

    @Generated(hash = 1876518963)
    public DeliveryRecordEntity() {
    }
    public Integer getToPostService() {
        return toPostService;
    }

    public void setToPostService(Integer toPostService) {
        this.toPostService = toPostService;
    }



    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrder_id() {
        return this.order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getDelivery_content() {
        return this.delivery_content;
    }

    public void setDelivery_content(String delivery_content) {
        this.delivery_content = delivery_content;
    }

    public String getUser_token() {
        return this.user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }

    public String getDate_time() {
        return this.date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public long getTime_mills() {
        return this.time_mills;
    }

    public void setTime_mills(long time_mills) {
        this.time_mills = time_mills;
    }

    public String getLoginType() {
        return this.loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getLoginValue() {
        return this.loginValue;
    }

    public void setLoginValue(String loginValue) {
        this.loginValue = loginValue;
    }

    public Integer getUploadStatus() {
        return this.uploadStatus;
    }

    public void setUploadStatus(Integer uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getRvmDetail() {
        return this.rvmDetail;
    }

    public void setRvmDetail(String rvmDetail) {
        this.rvmDetail = rvmDetail;
    }


}
