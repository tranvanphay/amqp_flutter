import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:amqp_service/amqp_service.dart';

void main() {
  const MethodChannel channel = MethodChannel('amqp_service');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  // test('com.tranvanphay.amqp_service', () async {
  //   expect(await AMQPService.startService(), '42');
  // });
}
