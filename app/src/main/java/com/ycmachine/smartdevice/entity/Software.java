package com.ycmachine.smartdevice.entity;

public class Software {

    /** 版本序号 */
    private Long postId;

    /** 版本编码 */
    private int postCode;

    /** 版本名称 */
    private String postName;

    /** 版本更新地址 */
    private String postUrl;

    /** 版本排序 */
    private Integer postSort;

    /** 状态（0正常 1停用） */
    private String status;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public int getPostCode() {
        return postCode;
    }

    public void setPostCode(int postCode) {
        this.postCode = postCode;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public Integer getPostSort() {
        return postSort;
    }

    public void setPostSort(Integer postSort) {
        this.postSort = postSort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Software{" +
                "postId=" + postId +
                ", postCode='" + postCode + '\'' +
                ", postName='" + postName + '\'' +
                ", postUrl='" + postUrl + '\'' +
                ", postSort=" + postSort +
                ", status='" + status + '\'' +
                '}';
    }
}
