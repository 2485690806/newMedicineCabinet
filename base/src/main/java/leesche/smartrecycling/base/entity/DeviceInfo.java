package leesche.smartrecycling.base.entity;

import java.util.List;

public class DeviceInfo {


    /**
     * agentId : 1
     * brandName : 游艺云Test
     * app_function : ["face","pocVideo","icCard","recyclerFace","publicAccount","point","childMode"]
     * showBrandName : y
     * channel_app_config : {"vs_ss_img":"","hs_bg_img":"","hs_ss_img":"","vs_bg_img":""}
     * deviceCode : cslxx80
     * accessToken : 0122f2d4aaa92c895726b9b4ff636ca28c92373a5e47e1e7ee3a97bc8b258c027ff5dbbd687a259091eb4339215a1085
     * deviceId : 11235
     * addressId : 1
     * deviceStatus : y
     * app_config : {"deviceCodeUrl":"https://devcloud.youyiyun.tech/c/recycling/qr/?u=1&d=11235&t=14451","wechatMiniprogramAppImg":"https://youyicloud-v2.oss-cn-shanghai.aliyuncs.com/recycling/agent/mini-wxe448173ee6c6efb8.jpg","welcomeAudioUrl":"https://youyicloud-v2.oss-cn-shanghai.aliyuncs.com/recycling/agent/ogg_new/1_welcome.OGG","pointAppImg":"https://youyicloud-v2.oss-cn-shanghai.aliyuncs.com/recycling/point.jpg","wechatAppImg":"https://youyicloud-v2.oss-cn-shanghai.aliyuncs.com/recycling/agent/mp-wxf9095c7a582baf87.jpg","loginAudioUrl":""}
     * addressName : 佳兆业A栋
     * contactPhone : null
     * app_view_type : 100001
     * brandLogo : http://youyicloud-v2.oss-cn-shanghai.aliyuncs.com/test/recycling/agent/app/brandLogo-gnLa24uC.jpg
     *currencySymbol
     */
    private int agentId;
    private String brandName;
    private String showBrandName;
    private ChannelAppConfigBean channel_app_config;
    private String deviceCode;
    private String accessToken;
    private int deviceId;
    private int addressId;
    private String deviceStatus;
    private AppConfigBean app_config;
    private String addressName;
    private String contactPhone;
    private String latitude;
    private String longitude;
    private int app_view_type;
    private String brandLogo;
    private List<String> app_function;
    private String currencySymbol;

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getShowBrandName() {
        return showBrandName;
    }

    public void setShowBrandName(String showBrandName) {
        this.showBrandName = showBrandName;
    }

    public ChannelAppConfigBean getChannel_app_config() {
        return channel_app_config;
    }

    public void setChannel_app_config(ChannelAppConfigBean channel_app_config) {
        this.channel_app_config = channel_app_config;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public AppConfigBean getApp_config() {
        return app_config;
    }

    public void setApp_config(AppConfigBean app_config) {
        this.app_config = app_config;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getApp_view_type() {
        return app_view_type;
    }

    public void setApp_view_type(int app_view_type) {
        this.app_view_type = app_view_type;
    }

    public String getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }

    public List<String> getApp_function() {
        return app_function;
    }

    public void setApp_function(List<String> app_function) {
        this.app_function = app_function;
    }

    public static class ChannelAppConfigBean {
        /**
         * vs_ss_img :
         * hs_bg_img :
         * hs_ss_img :
         * vs_bg_img :
         */

        private String vs_ss_img;
        private String hs_bg_img;
        private String hs_ss_img;
        private String vs_bg_img;

        public String getVs_ss_img() {
            return vs_ss_img;
        }

        public void setVs_ss_img(String vs_ss_img) {
            this.vs_ss_img = vs_ss_img;
        }

        public String getHs_bg_img() {
            return hs_bg_img;
        }

        public void setHs_bg_img(String hs_bg_img) {
            this.hs_bg_img = hs_bg_img;
        }

        public String getHs_ss_img() {
            return hs_ss_img;
        }

        public void setHs_ss_img(String hs_ss_img) {
            this.hs_ss_img = hs_ss_img;
        }

        public String getVs_bg_img() {
            return vs_bg_img;
        }

        public void setVs_bg_img(String vs_bg_img) {
            this.vs_bg_img = vs_bg_img;
        }
    }

    public static class AppConfigBean {
        /**
         * deviceCodeUrl : https://devcloud.youyiyun.tech/c/recycling/qr/?u=1&d=11235&t=14451
         * wechatMiniprogramAppImg : https://youyicloud-v2.oss-cn-shanghai.aliyuncs.com/recycling/agent/mini-wxe448173ee6c6efb8.jpg
         * welcomeAudioUrl : https://youyicloud-v2.oss-cn-shanghai.aliyuncs.com/recycling/agent/ogg_new/1_welcome.OGG
         * pointAppImg : https://youyicloud-v2.oss-cn-shanghai.aliyuncs.com/recycling/point.jpg
         * wechatAppImg : https://youyicloud-v2.oss-cn-shanghai.aliyuncs.com/recycling/agent/mp-wxf9095c7a582baf87.jpg
         * loginAudioUrl :
         */

        private String deviceCodeUrl;
        private String wechatMiniprogramAppImg;
        private String welcomeAudioUrl;
        private String pointAppImg;
        private String wechatAppImg;
        private String loginAudioUrl;
        private String feedBackUrl;

        public String getDeviceCodeUrl() {
            return deviceCodeUrl;
        }

        public void setDeviceCodeUrl(String deviceCodeUrl) {
            this.deviceCodeUrl = deviceCodeUrl;
        }

        public String getWechatMiniprogramAppImg() {
            return wechatMiniprogramAppImg;
        }

        public void setWechatMiniprogramAppImg(String wechatMiniprogramAppImg) {
            this.wechatMiniprogramAppImg = wechatMiniprogramAppImg;
        }

        public String getWelcomeAudioUrl() {
            return welcomeAudioUrl;
        }

        public void setWelcomeAudioUrl(String welcomeAudioUrl) {
            this.welcomeAudioUrl = welcomeAudioUrl;
        }

        public String getPointAppImg() {
            return pointAppImg;
        }

        public void setPointAppImg(String pointAppImg) {
            this.pointAppImg = pointAppImg;
        }

        public String getWechatAppImg() {
            return wechatAppImg;
        }

        public void setWechatAppImg(String wechatAppImg) {
            this.wechatAppImg = wechatAppImg;
        }

        public String getLoginAudioUrl() {
            return loginAudioUrl;
        }

        public void setLoginAudioUrl(String loginAudioUrl) {
            this.loginAudioUrl = loginAudioUrl;
        }

        public String getFeedBackUrl() {
            return feedBackUrl;
        }

        public void setFeedBackUrl(String feedBackUrl) {
            this.feedBackUrl = feedBackUrl;
        }
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }
}
