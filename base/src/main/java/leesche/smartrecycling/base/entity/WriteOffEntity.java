package leesche.smartrecycling.base.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class WriteOffEntity {

    @Id(autoincrement = true)
    private Long id;

    private String date;
    private String time;
    private String code;
    @Generated(hash = 1545395789)
    public WriteOffEntity(Long id, String date, String time, String code) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.code = code;
    }
    @Generated(hash = 1394912528)
    public WriteOffEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getCode() {
        return this.code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "WriteOffEntity{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
