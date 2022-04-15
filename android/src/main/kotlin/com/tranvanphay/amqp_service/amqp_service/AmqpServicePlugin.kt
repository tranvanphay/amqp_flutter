package com.tranvanphay.amqp_service.amqp_service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import com.tranvanphay.amqp_service.amqp_service.amqp.ConnectionInfo
import com.tranvanphay.amqp_service.amqp_service.amqp.service.MessageService
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.service.ServiceAware
import io.flutter.embedding.engine.plugins.service.ServicePluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/** AmqpServicePlugin */
class AmqpServicePlugin: FlutterPlugin, MethodCallHandler{

  private lateinit var channel : MethodChannel
  private lateinit var context: Context
  private lateinit var eventChannel : EventChannel


  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.tranvanphay.amqp_service")
    channel.setMethodCallHandler(this)
    this.context = flutterPluginBinding.applicationContext

  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "com.tranvanphay.amqp_service") {
      startService(call,result)
    } else {
      result.notImplemented()
    }
  }

  private fun startService(@NonNull call: MethodCall, @NonNull result: Result){
    val arguments = call.arguments as Map<String, String>
      MessageService.start(this.context, ConnectionInfo(arguments["host"] as String,arguments["port"] as Int,arguments["username"] as String,arguments["password"] as String,arguments["exchange"] as String,arguments["key"] as String))
    EventBus.getDefault().register(this);
    channel.invokeMethod("onReceiveMessage","ABC")

    result.success(null)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onMessageEvent(message: String) {
    Log.e("Plugin receive",message)
    channel.invokeMethod("onReceiveMessage",message)
  }


}
