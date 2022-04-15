import 'dart:async';
import 'dart:developer';

import 'package:flutter/services.dart';

class AMQPService {
  static const MethodChannel _channel =
      MethodChannel('com.tranvanphay.amqp_service');
  static final AMQPService _instance = AMQPService._privateConstructor();
  factory AMQPService() {
    return _instance;
  }
  AMQPService._privateConstructor() {
    _channel.setMethodCallHandler(_handleMethodCall);
  }
  static Future startService(
      {required String host,
      required int port,
      required String username,
      required String password,
      required String exchange,
      required String key}) async {
    return await _channel.invokeMethod(
      'com.tranvanphay.amqp_service',
      {
        'host': host,
        'port': port,
        'username': username,
        'password': password,
        'exchange': exchange,
        'key': key,
      },
    );
  }

  static final _controller = StreamController.broadcast(sync: true);

  static StreamSubscription get streamSubscription =>
      _controller.stream.listen((value) {
        log('Value from controller: $value');
      });

  Future<dynamic> _handleMethodCall(MethodCall call) async {
    switch (call.method) {
      case "onReceiveMessage":
        log("Flutter plg receive::${call.arguments}");
        return _controller.sink.add(call.arguments);
      default:
    }
  }
}
