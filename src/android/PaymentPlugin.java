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

    //支付方式-> 苹果支付，威富通支付方式
    private enum PayMethod {
        applePay("applePay"),
        swiftPay("doSwiftPay");
        private final String method;

        PayMethod(String method) {
            this.method = method;
        }
    }

    //威富通支付渠道->微信支付，阿里支付
    private enum SwiftPayChannel {
        wxPay("wxPay"),
        aliPay("aliPay");
        private final String method;

        SwiftPayChannel(String method) {
            this.method = method;
        }

    }


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

        if (action.toUpperCase().equals(PayMethod.swiftPay.name().toUpperCase())) {
            return this.doSwiftPay(mapParams, callbackContext);
        } else if (action.toUpperCase().equals(PayMethod.applePay.name().toUpperCase())) {
            return doApplePay(mapParams, callbackContext);
        }
        callbackContext.error("支付方式有误");
        return false;
    }

    /**
     * 威富通支付
     *
     * @param mapParams       支付参数
     * @param callbackContext 回调
     * @return
     */
    private boolean doSwiftPay(Map<String, String> mapParams, CallbackContext callbackContext) {
        //支付token
        String tokenId = mapParams.get("tokenId");
        if (isNullOrEmpty(tokenId)) {
            callbackContext.error("tokenId 有误");
            return false;
        }
        //支付渠道
        String channel = mapParams.get("channel");
        RequestMsg msg = new RequestMsg();
        msg.setTokenId(tokenId);
        //微信支付
        if (channel.toUpperCase().equals(SwiftPayChannel.wxPay.name().toUpperCase())) {
            msg.setTradeType(MainApplication.WX_APP_TYPE);
            //微信需要AppId
            String appId = mapParams.get("appId");
            if (isNullOrEmpty(appId)) {
                callbackContext.error("微信AppId 有误");
                return false;
            }
            msg.setAppId(appId);
            //支付宝支付
        } else if (channel.toUpperCase().equals(SwiftPayChannel.aliPay.name().toUpperCase())) {
            msg.setTradeType(MainApplication.ZFB_APP_TYPE);
        } else {
            callbackContext.error("威富通支付渠道channel有误");
            return false;
        }
        //发起支付
        PayPlugin.unifiedAppPay(cordova.getActivity(), msg);
        return true;
    }

    /**
     * 苹果支付
     * @param mapParams
     * @param callbackContext
     * @return
     */
    private boolean doApplePay(Map<String, String> mapParams, CallbackContext callbackContext) {
        return false;
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
