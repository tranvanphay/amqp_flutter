import 'dart:developer';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:amqp_service/amqp_service.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  initPlatformState();
  runApp(const MyApp());
  AMQPService();
  AMQPService.streamSubscription.onData((data) {
    log(data);
  });
}

// Platform messages are asynchronous, so we initialize in an async method.
Future<void> initPlatformState() async {
  return await AMQPService.startService(
      host: 'mes.isc21.kr',
      port: 5672,
      username: 'mes',
      password: '6xa3AkzphnvL',
      exchange: 'T-direct',
      key: '5210015');
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Running on: $_platformVersion\n'),
        ),
      ),
    );
  }
}
