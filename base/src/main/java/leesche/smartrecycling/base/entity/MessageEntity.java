package leesche.smartrecycling.base.entity;

public class MessageEntity {

    public static final int TYPE_RECEIVE = 0;
    public static final int TYPE_SEND    = 1;

    //识别码ID
    private int identify_id;
    //功能码
    private byte func_code;
    //序号
    private int serial_num;
    //内容长度
    private int content_length;
    //内容
    private String content;
    //内容bytes
    private byte[] content_bytes;
    //箱子编号
    private  int boxCode;
    //原始数据
    private String rawDataStr;

    public static int getTypeReceive() {
        return TYPE_RECEIVE;
    }

    public static int getTypeSend() {
        return TYPE_SEND;
    }

    public int getIdentify_id() {
        return identify_id;
    }

    public void setIdentify_id(int identify_id) {
        this.identify_id = identify_id;
    }

    public byte getFunc_code() {
        return func_code;
    }

    public void setFunc_code(byte func_code) {
        this.func_code = func_code;
    }

    public int getSerial_num() {
        return serial_num;
    }

    public void setSerial_num(int serial_num) {
        this.serial_num = serial_num;
    }

    public int getContent_length() {
        return content_length;
    }

    public void setContent_length(int content_length) {
        this.content_length = content_length;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageEntity() {

    }

    public byte[] getContent_bytes() {
        return content_bytes;
    }

    public void setContent_bytes(byte[] content_bytes) {
        this.content_bytes = content_bytes;
    }

    public int getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(int boxCode) {
        this.boxCode = boxCode;
    }

    public String getRawDataStr() {
        return rawDataStr;
    }

    public void setRawDataStr(String rawDataStr) {
        this.rawDataStr = rawDataStr;
    }

    @Override
    public String toString() {
        return "识别码ID: " + identify_id + "  功能码：" + func_code
                + "  序号：" + serial_num + "  内容长度： " + content_length + "  接收内容："
                + content;
    }
}
