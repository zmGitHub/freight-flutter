import 'dart:async';

import 'package:flutter/services.dart';

class Freight {
  static const MethodChannel _channel = const MethodChannel('freight');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// 初始化服务
  static Future<Map<String, String>> init(
      String appId, String appSecurity, String eeCode, String env) async {
    final Map<dynamic, dynamic> result = await _channel.invokeMethod("init", {
      "appId": appId,
      "appSecurity": appSecurity,
      "eeCode": eeCode,
      "env": env
    });

    return new Map<String, String>.from(result);
  }

  /// 启用定位
  static Future<Map<String, String>> start(
      List<Map<String, String>> ships) async {
    final Map<dynamic, dynamic> result =
        await _channel.invokeMethod("start", {"ships": ships});

    return new Map<String, String>.from(result);
  }

  /// 结束定位
  static Future<Map<String, String>> stop(
      List<Map<String, String>> ships) async {
    final Map<dynamic, dynamic> result =
        await _channel.invokeMethod("stop", {"ships": ships});

    return new Map<String, String>.from(result);
  }

  /// 初始化高德地图服务
  /// 备注: 仅在iOS平台下调用
  /// @param 高德地图服务key
  static Future<bool> initAmap(String key) async {
    return await _channel.invokeMethod("initAmap", key);
  }
}
