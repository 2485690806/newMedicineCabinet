package leesche.smartrecycling.base.eventbus;

import java.util.ArrayList;
import java.util.List;

public class SerialMsgEvent {

    private int message_id;
    private String content;
    private int operate_from;
    private ArrayList<String> arrayList;
    private int[] integers;
    private String[] columns;

    public SerialMsgEvent(int message_id) {
        this.message_id = message_id;
    }

    public SerialMsgEvent(int message_id, String content) {
        this.message_id = message_id;
        this.content = content;
    }


//    public SerialMsgEvent(int message_id, ArrayList<String> arrayList) {
//        this.message_id = message_id;
//        this.arrayList = arrayList;
//    }

    public SerialMsgEvent(int message_id, String[] columns, int[] integers) {
        this.message_id = message_id;
        this.integers = integers;
        this.columns = columns;
    }

    public String[] getColumns() {
        return columns;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public int[] getIntegers() {
        return integers;
    }

    public void setIntegers(int[] integers) {
        this.integers = integers;
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

//    public ArrayList<String> getArrayList() {
//        return arrayList;
//    }
//
//    public void setArrayList(ArrayList<String> arrayList) {
//        this.arrayList = arrayList;
//    }

}
