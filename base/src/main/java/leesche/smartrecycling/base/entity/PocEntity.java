package leesche.smartrecycling.base.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import io.reactivex.rxjava3.annotations.NonNull;

@Entity
public class PocEntity {

    @Id(autoincrement = true)
    private Long id;
    private String order_id;
    private long start_time;
    private long second_start_time;
    private long end_time;
    private long second_end_time;
    private String localVideoPaths;
    private int status;

    @Generated(hash = 566907605)
    public PocEntity(Long id, String order_id, long start_time, long second_start_time,
            long end_time, long second_end_time, String localVideoPaths, int status) {
        this.id = id;
        this.order_id = order_id;
        this.start_time = start_time;
        this.second_start_time = second_start_time;
        this.end_time = end_time;
        this.second_end_time = second_end_time;
        this.localVideoPaths = localVideoPaths;
        this.status = status;
    }

    @Generated(hash = 832147480)
    public PocEntity() {
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

    public long getStart_time() {
        return this.start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return this.end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    @NonNull
    @Override
    public String toString() {
        return "订单号：" + order_id + "    开始时间：" + start_time + "    结束时间：" + end_time
                + "    第二次开始时间：" + second_start_time + "    第二次结束时间：" + second_end_time;
    }

    public long getSecond_start_time() {
        return this.second_start_time;
    }

    public void setSecond_start_time(long second_start_time) {
        this.second_start_time = second_start_time;
    }

    public long getSecond_end_time() {
        return this.second_end_time;
    }

    public void setSecond_end_time(long second_end_time) {
        this.second_end_time = second_end_time;
    }

    public String getLocalVideoPaths() {
        return this.localVideoPaths;
    }

    public void setLocalVideoPaths(String localVideoPaths) {
        this.localVideoPaths = localVideoPaths;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
