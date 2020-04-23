package net.freight.freight;

import android.content.Context;

import androidx.annotation.NonNull;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.HashMap;
import java.util.Map;

/**
 * FreightPlugin
 */
public class FreightPlugin implements FlutterPlugin, MethodCallHandler {

    private Context context;
    private MethodChannel channel;
    private AMapLocationClient locationClient;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        onAttachedToEngine(flutterPluginBinding.getApplicationContext(), flutterPluginBinding.getBinaryMessenger());
    }

    private void onAttachedToEngine(Context context, BinaryMessenger messenger) {
        this.context = context;
        channel = new MethodChannel(messenger, "freight");
        channel.setMethodCallHandler(this);
    }

    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    public static void registerWith(Registrar registrar) {
        final FreightPlugin instance = new FreightPlugin();
        instance.onAttachedToEngine(registrar.context(), registrar.messenger());
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
                    locationClient = new AMapLocationClient(this.context);

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
        context = null;
        channel = null;
    }
}
