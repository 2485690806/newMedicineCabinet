package leesche.smartrecycling.base.websocket;

import androidx.fragment.app.FragmentActivity;

public interface IWebSocketHelper {

    void initServerWebSocket(FragmentActivity activity, String url);
//    void connect(String url);
    boolean send(String msg);
    void disConnect();
}
