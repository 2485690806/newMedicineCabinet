package leesche.smartrecycling.base.entity;

public class ThrRecyclerEntity {
    private String id;
    private String nickName;
    private String loginValue;
    private String loginType;
    private String account;
    private String userType;
    private String role;
    private String appRecyclerToken;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getLoginValue() {
        return loginValue;
    }

    public void setLoginValue(String loginValue) {
        this.loginValue = loginValue;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAppRecyclerToken() {
        return appRecyclerToken;
    }

    public void setAppRecyclerToken(String appRecyclerToken) {
        this.appRecyclerToken = appRecyclerToken;
    }
}
