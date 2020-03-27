package com.example.gateway.utils;

public class RequestResult {

    //请求参数不完整 InvalidParameters
    public static final Integer INVALIDPARAMETERS  = 1001;
    public static final Integer CODE_SUCCESS = 1;
    public static final Integer CODE_FAILD =0;

    public static final String MSG_SUCCESS = "请求成功";
    public static final String MSG_FAILD = "请求失败";

    private int code = CODE_SUCCESS;
    private String msg = MSG_SUCCESS;
    private Object result;

    public RequestResult() {}

    public RequestResult(int code) {
        this.code = code;
        if (code == CODE_SUCCESS) {
            this.msg = MSG_SUCCESS;
        } else if (code == CODE_FAILD) {
            this.msg = MSG_FAILD;
        }
    }

    public RequestResult(Object result) {
        this.result = result;
    }

    public RequestResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RequestResult(int code, String msg, Object result) {
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }


}