package leesche.smartrecycling.base.entity;

public class HttpResult<T> {

    private String code;
    private String msg;
    private Object error_code;
    private T result;
    private String cause;
    private String token;
    private boolean succeed;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getError_code() {
        return error_code;
    }

    public void setError_code(Object error_code) {
        this.error_code = error_code;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("code=" + code + " msg=" + msg );
        if (null != result) {
            sb.append(" subjects:" + result.toString());
        }
        return sb.toString();
    }
}
