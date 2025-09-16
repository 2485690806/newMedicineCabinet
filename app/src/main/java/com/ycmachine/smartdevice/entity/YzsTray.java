package com.ycmachine.smartdevice.entity;


/**
 * 托盘对象 yzs_tray
 *
 * @author ljh
 * @date 2025-03-01
 */
public class YzsTray
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 绑定的设备号 */
    private Long deviceId;

    /** 托盘号 */
    private Long box;

    /** 托盘状态 */
    private Integer trayStatus;

    /** 价格牌状态 */
    private Integer priceCardStatus;

    /** 出售的商品 */
    private Long goodsId;

    /** 称重盘总重 */
    private Long czpTotalWeight;

    /** 商品总重 */
    private Long goodsTotalWeight;

    /** 异常总重 */
    private Long exceptionTotalWeight;


    //逻辑删除（0：不删除，1：删除）
    private Integer isDelete;


    // 称重模块设备号
    private String weighModuleId;

    // 数码电子价签设备号
    private String digitalEslId;

    // 是否不加入分账，0：加入分账，1：不加入分账
    private String unSplit;

    // 所属柜机，0：左边柜机，1：右边柜机，每个柜机最大5个托盘
    private String isDeviceNum;


    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setBox(Long box)
    {
        this.box = box;
    }

    public Long getBox()
    {
        return box;
    }
    public void setTrayStatus(Integer trayStatus)
    {
        this.trayStatus = trayStatus;
    }

    public Integer getTrayStatus()
    {
        return trayStatus;
    }
    public void setPriceCardStatus(Integer priceCardStatus)
    {
        this.priceCardStatus = priceCardStatus;
    }

    public Integer getPriceCardStatus()
    {
        return priceCardStatus;
    }
    public void setGoodsId(Long goodsId)
    {
        this.goodsId = goodsId;
    }

    public Long getGoodsId()
    {
        return goodsId;
    }
    public void setCzpTotalWeight(Long czpTotalWeight)
    {
        this.czpTotalWeight = czpTotalWeight;
    }

    public Long getCzpTotalWeight()
    {
        return czpTotalWeight;
    }
    public void setGoodsTotalWeight(Long goodsTotalWeight)
    {
        this.goodsTotalWeight = goodsTotalWeight;
    }

    public Long getGoodsTotalWeight()
    {
        return goodsTotalWeight;
    }
    public void setExceptionTotalWeight(Long exceptionTotalWeight)
    {
        this.exceptionTotalWeight = exceptionTotalWeight;
    }

    public Long getExceptionTotalWeight()
    {
        return exceptionTotalWeight;
    }
    public void setIsDelete(Integer isDelete)
    {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete()
    {
        return isDelete;
    }


    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getWeighModuleId() {
        return weighModuleId;
    }

    public void setWeighModuleId(String weighModuleId) {
        this.weighModuleId = weighModuleId;
    }

    public String getDigitalEslId() {
        return digitalEslId;
    }

    public void setDigitalEslId(String digitalEslId) {
        this.digitalEslId = digitalEslId;
    }

    public String getUnSplit() {
        return unSplit;
    }

    public void setUnSplit(String unSplit) {
        this.unSplit = unSplit;
    }

    public String getIsDeviceNum() {
        return isDeviceNum;
    }

    public void setIsDeviceNum(String isDeviceNum) {
        this.isDeviceNum = isDeviceNum;
    }
}
