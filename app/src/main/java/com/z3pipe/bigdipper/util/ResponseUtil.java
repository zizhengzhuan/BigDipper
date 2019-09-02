package com.z3pipe.bigdipper.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author gxx 2019-3-18
 */
public class ResponseUtil {

    private static ResponseUtil instance;

    public static ResponseUtil getInstance(){
        if (instance == null) {
            synchronized (ResponseUtil.class) {
                if (instance == null) {
                    instance = new ResponseUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 判断服务是否返回成功
     * @param json
     * @return
     */
    public static boolean success(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            return success(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断服务是否返回成功
     * @param json
     * @return
     */
    public static boolean success(JSONObject json) {
        boolean isOK = true;

        if (json.has("error")) {
            isOK = false;
        }

        if (json.has("isSuccess")) {
            isOK = json.optBoolean("isSuccess");
        }

        if (json.has("success")) {
            isOK = json.optBoolean("success");
        }

        if (isOK) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取网络请求错误信息
     * @param json
     * @return
     */
    public static String getErrorMsg(String json) {
        String error = "";
        try {
            JSONObject jsonObject = new JSONObject(json);
            if(jsonObject.has("error")) {
                error = jsonObject.optString("error");
            } else if(jsonObject.has("msg")){
                error = jsonObject.optString("msg");
            } else if(jsonObject.has("message")) {
                error = jsonObject.optString("message");
            } else if(jsonObject.has("data")){
                error = jsonObject.optString("data");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return error;
        }
        return error;
    }

    /**
     * 获取网络请求错误信息
     * @param json
     * @return
     */
    public static String getErrorMsg(JSONObject json) {
        String error = "";
        if(json.has("error")) {
            error = json.optString("error");
        } else if(json.has("msg")){
            error = json.optString("msg");
        } else if(json.has("message")) {
            error = json.optString("message");
        } else if(json.has("data")){
            error = json.optString("data");
        }
        return error;
    }
}
