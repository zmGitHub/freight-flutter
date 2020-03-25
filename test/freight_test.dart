import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:freight/freight.dart';

void main() {
  const MethodChannel channel = MethodChannel('freight');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Freight.platformVersion, '42');
  });
}
