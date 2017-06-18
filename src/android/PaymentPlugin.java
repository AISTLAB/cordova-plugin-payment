package com.aiesst.payment;

import android.util.Log;

import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ychost on 17-5-18.
 * 威富通支付插件
 */

public class PaymentPlugin extends CordovaPlugin {

    // 支付接口
    private static final String PAY_METHOD_WFT = "doWithWft";
    private static final String PAY_METHOD_APPLE_PAY = "doWithApplePay";

    @Override
    public boolean execute(String action, JSONArray rawArgs, CallbackContext callbackContext) throws JSONException {
        String strParams = rawArgs.getString(0);
        Log.d("PaymentPlugin", "接受参数: " + strParams);
        Map<String, String> mapParams;
        try {
            mapParams = jsonToMap(strParams);
        } catch (Exception e) {
            callbackContext.error("json格式有误");
            return false;
        }

        if(action.equals(PAY_METHOD_WFT)) {
            if (doWithWft(mapParams, callbackContext)) {
                callbackContext.success();
                return true;
            } else {
                callbackContext.error("Unsupported Channel");
                return false;
            }
        } else if(action.equals(PAY_METHOD_APPLE_PAY)) {
            callbackContext.error("Android doesn't support apple pay!");
            return false;
        }

        callbackContext.error("支付方式有误");
        return false;
    }

    /**
     * 威富通支付（服务器版本）
     * @param mapParams 支付参数
     * @param callbackContext 回调
     * @return 结果
     */
    private boolean doWithWft(Map<String, String> mapParams, CallbackContext callbackContext) {

        String tokenId = mapParams.get("token_id");
        String service = mapParams.get("service");
        String appId = mapParams.get("app_id");
        RequestMsg msg = new RequestMsg();
        msg.setTokenId(tokenId);
        msg.setTradeType(service);
        msg.setAppId(appId);

        if(service.equals(MainApplication.PAY_NEW_ZFB_WAP)) {
            PayPlugin.unifiedH5Pay(cordova.getActivity(), msg);
        } else if(service.equals(MainApplication.WX_APP_TYPE)) {
            PayPlugin.unifiedAppPay(cordova.getActivity(), msg);
        } else {
            return false;
        }
        return true;
    }

    /**
     * 将Json字符串转为map
     * @param jsonStr
     * @return
     * @throws Exception
     */
    Map<String, String> jsonToMap(String jsonStr) throws Exception {
        JSONObject jsonObj = new JSONObject(jsonStr);
        Iterator<String> nameItr = jsonObj.keys();
        String name;
        Map<String, String> outMap = new HashMap<String, String>();
        while (nameItr.hasNext()) {
            name = nameItr.next();
            outMap.put(name, jsonObj.getString(name));
        }
        return outMap;
    }

    boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

}