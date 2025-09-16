package leesche.smartrecycling.base.common;

public class EventType {

    public interface BasicEvent {
        int BASE = 0x00;
        int REBOOT_APP = BASE + 1;
        int REBOOT_SYS = REBOOT_APP + 1;
        int UPDATE_APP = REBOOT_SYS + 1;
        int SNAP_CAMERA_NUM = UPDATE_APP + 1;
        int GET_DEVICE_INFO = SNAP_CAMERA_NUM + 1;
        int PROCESS_WEBSOCKET_MSG = GET_DEVICE_INFO + 1;
        int HEAT_STATUS = PROCESS_WEBSOCKET_MSG + 1;
        int USER_HINT_INFO = HEAT_STATUS + 1;
    }


}
