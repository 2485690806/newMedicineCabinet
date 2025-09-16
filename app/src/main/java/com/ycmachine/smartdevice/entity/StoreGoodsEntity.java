package com.ycmachine.smartdevice.entity;


/**
 * 门店商品信息对象 yzs_store_goods
 *
 * @author hxb
 * @date 2023-03-16
 */

public class StoreGoodsEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;


    /** 所属门店id */
    private Long storeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Double getVipPrice() {
        return vipPrice;
    }

    public void setVipPrice(Double vipPrice) {
        this.vipPrice = vipPrice;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Integer getSpecifications() {
        return specifications;
    }

    public void setSpecifications(Integer specifications) {
        this.specifications = specifications;
    }

    public Integer getPieceWork() {
        return pieceWork;
    }

    public void setPieceWork(Integer pieceWork) {
        this.pieceWork = pieceWork;
    }

    /** 商品名称 */
    private String name;

    /** 图片url */
    private String imageUrl;

    /** 原价 */
    private Double costPrice;

    /** 售价 */
    private Double salePrice;


    private Double vipPrice;


    private String units;

    private Integer specifications;



    // 计件方式，0：非固定重量（称重）1：固定重量（计件）
    private Integer pieceWork;

    public Integer getIsSpecial() {
        return isSpecial;
    }

    public void setIsSpecial(Integer isSpecial) {
        this.isSpecial = isSpecial;
    }




    private Integer isSpecial; // 是否特价商品，0：否，1：是







}
