package leesche.smartrecycling.base.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class UserLoginStyleEntity {

    @Id(autoincrement = true)
    private Long id;

    /**
     * 手机号码
     */
    private String user_phone;

    /**
     * 门牌号
     */
    private String door_plate_num;

    /**
     * IC卡号
     */
    private String ic_card_num;

    /**
     * 用户二维码
     */
    private String qr_code;

    /**
     * 用户类型
     */
    private int user_type;

    @Generated(hash = 494629208)
    public UserLoginStyleEntity(Long id, String user_phone, String door_plate_num,
                                String ic_card_num, String qr_code, int user_type) {
        this.id = id;
        this.user_phone = user_phone;
        this.door_plate_num = door_plate_num;
        this.ic_card_num = ic_card_num;
        this.qr_code = qr_code;
        this.user_type = user_type;
    }

    @Generated(hash = 1356245936)
    public UserLoginStyleEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser_phone() {
        return this.user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getDoor_plate_num() {
        return this.door_plate_num;
    }

    public void setDoor_plate_num(String door_plate_num) {
        this.door_plate_num = door_plate_num;
    }

    public String getIc_card_num() {
        return this.ic_card_num;
    }

    public void setIc_card_num(String ic_card_num) {
        this.ic_card_num = ic_card_num;
    }

    public String getQr_code() {
        return this.qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public int getUser_type() {
        return this.user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    @Override
    public String toString() {
        return "UserLoginStyleEntity{" +
                "id=" + id +
                ", user_phone='" + user_phone + '\'' +
                ", door_plate_num='" + door_plate_num + '\'' +
                ", ic_card_num='" + ic_card_num + '\'' +
                ", qr_code='" + qr_code + '\'' +
                ", user_type=" + user_type +
                '}';
    }
}

