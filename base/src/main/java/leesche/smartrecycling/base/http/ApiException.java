package leesche.smartrecycling.base.http;


public class ApiException extends RuntimeException {

    public static final int SERVER_EXCEPTION = 400;

    public ApiException(int resultCode, String msg) {
        this(getApiExceptionMessage(resultCode, msg));
    }

    public ApiException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
     * 需要根据错误码对错误信息进行一个转换，在显示给用户
     * @param code
     * @return
     */
    private static String getApiExceptionMessage(int code, String msg){
        String message = "";
        switch (code) {
            case SERVER_EXCEPTION:
                message = "服务器异常，请联系管理员";
                break;
            default:
                message = "未知错误";

        }
        return msg;
    }
}

