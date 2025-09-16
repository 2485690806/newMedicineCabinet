package leesche.smartrecycling.base.entity;

public class  UserInfoEntity {


    /**
     * app_user_token : ceda220543608fc8bfd6c5118f3f9b7016b84ff3e103c6a96dfb834ca1a931d1
     * headImgUrl : http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLnv45fJyFvxhb2gygz6aanKRTKN8D5R4M4hnb5loQaHnRHXTojShJusibqNk87aS4vWJ6qgLqoRrQ/132
     * phone : 17603034755
     * nickName : 肩吾
     * point:
     */

    private String app_user_token;
    private String headImgUrl;
    private String phone;
    private String nickName;
    private int point;

    public String getApp_user_token() {
        return app_user_token;
    }

    public void setApp_user_token(String app_user_token) {
        this.app_user_token = app_user_token;
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

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
