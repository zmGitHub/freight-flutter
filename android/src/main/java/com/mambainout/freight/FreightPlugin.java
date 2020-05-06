package com.mambainout.freight;

import android.app.Activity;

import androidx.annotation.NonNull;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hdgq.locationlib.LocationOpenApi;
import com.hdgq.locationlib.entity.ShippingNoteInfo;
import com.hdgq.locationlib.listener.OnResultListener;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FreightPlugin
 */
public class FreightPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

    private AMapLocationClient locationClient;
    private Activity activity;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        final MethodChannel channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "freight");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull final Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("init")) {
            String appId = call.argument("appId");
            String appSecurity = call.argument("appSecurity");
            String eeCode = call.argument("eeCode");
            String env = call.argument("env");
            LocationOpenApi.init(activity, appId, appSecurity, eeCode, env, new OnResultListener() {
                @Override
                public void onSuccess() {
                    HashMap json = new HashMap<String, String>();
                    json.put("code", "0");
                    json.put("message", "成功");
                    result.success(json);
                }

                @Override
                public void onFailure(String code, String message) {
                    result.error(code, message, null);
                }
            });
        } else if (call.method.equals("start")) {
            List<Map<String, String>> ships = call.argument("ships");
            ShippingNoteInfo[] shipList = new ShippingNoteInfo[ships.size()];
            for (int i = 0; i < shipList.length; i++) {
                Log.d("ship", ships.get(i).toString());
                ShippingNoteInfo ship = new ShippingNoteInfo();
                ship.setShippingNoteNumber(ships.get(i).get("shippingNoteNumber"));
                ship.setSerialNumber(ships.get(i).get("serialNumber"));
                ship.setStartCountrySubdivisionCode(ships.get(i).get("startCountrySubdivisionCode"));
                ship.setEndCountrySubdivisionCode(ships.get(i).get("endCountrySubdivisionCode"));
                shipList[i] = ship;
            }

            LocationOpenApi.start(this.activity, shipList, new OnResultListener() {
                @Override
                public void onSuccess() {
                    HashMap json = new HashMap<String, String>();
                    json.put("code", "0");
                    json.put("message", "start成功");
                    result.success(json);
                }

                @Override
                public void onFailure(String code, String message) {
                    result.error(code, message, null);

                }
            });
        } else if (call.method.equals("stop")) {
            List<Map<String, String>> ships = call.argument("ships");
            ShippingNoteInfo[] shipList = new ShippingNoteInfo[ships.size()];
            for (int i = 0; i < shipList.length; i++) {
                Log.d("ship", ships.get(i).toString());
                ShippingNoteInfo ship = new ShippingNoteInfo();
                ship.setShippingNoteNumber(ships.get(i).get("shippingNoteNumber"));
                ship.setSerialNumber(ships.get(i).get("serialNumber"));
                ship.setStartCountrySubdivisionCode(ships.get(i).get("startCountrySubdivisionCode"));
                ship.setEndCountrySubdivisionCode(ships.get(i).get("endCountrySubdivisionCode"));
                shipList[i] = ship;
            }

            LocationOpenApi.stop(this.activity, shipList, new OnResultListener() {
                @Override
                public void onSuccess() {
                    HashMap json = new HashMap<String, String>();
                    json.put("code", "0");
                    json.put("message", "stop成功");
                    result.success(json);
                }

                @Override
                public void onFailure(String code, String message) {
                    result.error(code, message, null);
                }
            });
        } else if (call.method.equals("initAmap")) {
            Log.d("freight.Android", "initAmap");

            synchronized (this) {
                if (locationClient == null) {
                    // 初始化client
                    locationClient = new AMapLocationClient(this.activity);

                    // 新建定位参数
                    AMapLocationClientOption option = new AMapLocationClientOption();
                    // 定位场景 签到
                    option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
                    // 定位模式 高精度
                    option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                    // 单次定位
                    option.setOnceLocation(true);
                    // 地址描述 否
                    option.setNeedAddress(false);

                    // 设置定位参数
                    locationClient.setLocationOption(option);
                }
            }

            result.success(true);
        } else if (call.method.equals("getLocation")) {
            Log.d("freight.Android", "getLocation");

            synchronized (this) {
                final AMapLocationListener listener = new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {
                        Log.d("freight.Android.getLocation", "定位结果..." + aMapLocation);
                        Map<String, Double> map = new HashMap<>();
                        map.put("latitude", aMapLocation.getLatitude());
                        map.put("longitude", aMapLocation.getLongitude());

                        result.success(map);

                        locationClient.stopLocation();
                    }
                };

                locationClient.setLocationListener(listener);
                locationClient.startLocation();
            }
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        if (activity == null) {
            activity = binding.getActivity();
        }
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    }

    @Override
    public void onDetachedFromActivity() {
    }
}
