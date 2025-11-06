package leesche.smartrecycling.base.eventbus;

public class BasicMessageEvent {

    private int message_id;
    private String content;
    private int msg_flag;

    public int getMsg_level() {
        return msg_level;
    }

    public void setMsg_level(int msg_level) {
        this.msg_level = msg_level;
    }

    private int msg_level;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    private Object object;

    public BasicMessageEvent(int message_id) {
        this.message_id = message_id;
    }

    public BasicMessageEvent(int message_id, int msg_flag) {
        this.message_id = message_id;
        this.msg_flag = msg_flag;
    }

    public BasicMessageEvent(int message_id, int msg_flag, int msg_level) {
        this.message_id = message_id;
        this.msg_flag = msg_flag;
        this.msg_level = msg_level;
    }

    public BasicMessageEvent(int message_id, String content) {
        this.message_id = message_id;
        this.content = content;
    }

    public BasicMessageEvent(int message_id, Object object) {
        this.message_id = message_id;
        this.object = object;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public int getMsg_flag() {
        return msg_flag;
    }

    public void setMsg_flag(int msg_flag) {
        this.msg_flag = msg_flag;
    }
}
