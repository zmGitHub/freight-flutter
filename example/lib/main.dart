import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:freight/freight.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await Freight.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  Future<void> initLocationService() async {
    try {
      Map<String, String> result = await Freight.init(
          "com.sk95.logistic",
          "8c4e5a617fcb47288f8de6c44fb29c30e31843b6329c4d388d32fc2d23e702ed",
          "37100937",
          "debug");
      debugPrint("[DEMO] init结果: " + result.toString());

      setState(() {
        _platformVersion = result["message"];
      });
    } catch (e) {
      debugPrint("[DEMO] init失败: " + e.toString());
    }
  }

  Future<void> startLocationService() async {
    try {
      Map<String, String> result = await Freight.start([
        {
          "shippingNoteNumber": "123",
          "serialNumber": "1",
          "startCountrySubdivisionCode": "110101000000",
          "endCountrySubdivisionCode": "110102000000",
        }
      ]);
      debugPrint("[DEMO] start结果: " + result.toString());

      setState(() {
        _platformVersion = result["message"];
      });
    } catch (e) {
      debugPrint("[DEMO] start失败: " + e.toString());
    }
  }

  Future<void> stopLocationService() async {
    try {
      Map<String, String> result = await Freight.stop([
        {
          "shippingNoteNumber": "123",
          "serialNumber": "1",
          "startCountrySubdivisionCode": "110101000000",
          "endCountrySubdivisionCode": "110102000000",
        }
      ]);
      debugPrint("[DEMO] stop结果: " + result.toString());

      setState(() {
        _platformVersion = result["message"];
      });
    } catch (e) {
      debugPrint("[DEMO] stop失败: " + e.toString());
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
            child: Column(
          children: <Widget>[
            Center(
              child: Row(
                children: <Widget>[
                  RaisedButton(
                    child: Text("初始化服务"),
                    onPressed: () async {
                      await initLocationService();
                    },
                  ),
                  RaisedButton(
                    child: Text("开始定位"),
                    onPressed: () async {
                      await startLocationService();
                    },
                  ),
                  RaisedButton(
                    child: Text("结束定位"),
                    onPressed: () async {
                      await stopLocationService();
                    },
                  ),
                ],
              ),
            ),
            Text("Running on: $_platformVersion\n"),
          ],
        )),
      ),
    );
  }
}
