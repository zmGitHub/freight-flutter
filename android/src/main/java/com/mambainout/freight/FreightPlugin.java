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
import com.hdgq.locationlib.listener.OnResultListener;

import java.util.HashMap;
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
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        if (activity == null) {
            activity = binding.getActivity();
        }
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
    }

    @Override
    public void onDetachedFromActivity() {
    }
}
