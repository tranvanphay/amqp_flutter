package com.tranvanphay.amqp_service.amqp_service.amqp.service;



import static com.tranvanphay.amqp_service.amqp_service.amqp.utils.Constant.ACTION_START_FOREGROUND_SERVICE;
import static com.tranvanphay.amqp_service.amqp_service.amqp.utils.Constant.ACTION_STOP_FOREGROUND_SERVICE;
import static com.tranvanphay.amqp_service.amqp_service.amqp.utils.Constant.NOTIFICATION_ID_FOREGROUNG_SERVICE;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.tranvanphay.amqp_service.amqp_service.amqp.ConnectionInfo;
import com.tranvanphay.amqp_service.amqp_service.amqp.notification.NotificationCreator;
import com.tranvanphay.amqp_service.amqp_service.amqp.utils.Constant;

import org.greenrobot.eventbus.EventBus;

import java.nio.charset.StandardCharsets;


public class MessageService extends Service {

    private final String TAG = MessageService.class.getSimpleName();

    private Thread directThread;

    private ConnectionFactory factory;

    public MessageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_START_FOREGROUND_SERVICE:
                    Bundle extra = intent.getExtras();
                    ConnectionInfo connectionInfo = new ConnectionInfo(extra.getString(Constant.RABBITMQ_HOST),extra.getInt(Constant.RABBITMQ_PORT),extra.getString(Constant.RABBITMQ_USERNAME),extra.getString(Constant.RABBITMQ_PASSWORD),extra.getString(Constant.RABBITMQ_EXCHANGE),extra.getString(Constant.RABBITMQ_KEY));
                    setupConnectionFactory(connectionInfo);
                    startForeground(connectionInfo);
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForeground();
                    break;
            }
        }

        return START_STICKY;
    }

    private void startForeground(ConnectionInfo info) {
        if (factory != null) {
            initThread(info);
        }
        startForeground(NOTIFICATION_ID_FOREGROUNG_SERVICE, NotificationCreator.createForegroundNotification(this));
    }

    private void stopForeground() {
        Log.e(TAG, "Stop foreground service.");
        stopForeground(true);
        stopSelf();
    }

    public void setupConnectionFactory(ConnectionInfo info) {
        Log.d(TAG, "SetupConnectionFactory: successfully");
        factory = new ConnectionFactory();
        try {
            factory.setAutomaticRecoveryEnabled(false);
            factory.setHost(info.getHost());
            factory.setPort(info.getPort());
            factory.setUsername(info.getUserName());
            factory.setPassword(info.getPassword());
            factory.setVirtualHost("/");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        interruptThread(directThread);

        super.onDestroy();
    }

    private void interruptThread(Thread thread) {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    public static void start(Context context,ConnectionInfo info) {
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(ACTION_START_FOREGROUND_SERVICE);
        intent.putExtra(Constant.RABBITMQ_HOST,info.getHost());
        intent.putExtra(Constant.RABBITMQ_PORT,info.getPort());
        intent.putExtra(Constant.RABBITMQ_USERNAME,info.getUserName());
        intent.putExtra(Constant.RABBITMQ_PASSWORD,info.getPassword());
        intent.putExtra(Constant.RABBITMQ_EXCHANGE,info.getExchange());
        intent.putExtra(Constant.RABBITMQ_KEY,info.getKey());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(ACTION_STOP_FOREGROUND_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    private void initThread(ConnectionInfo info) {
        initThread(directThread, info);
    }

    private void initThread(Thread thread, ConnectionInfo info) {
        interruptThread(thread);

        Log.d(TAG, "init channel: " + info.getExchange() + " key:" + info.getKey());
        thread = new MessageThread(info);
        thread.start();
    }

    public class MessageThread extends Thread {
        private ConnectionInfo connectionInfo;

        public MessageThread(ConnectionInfo connectionInfo) {
            this.connectionInfo = connectionInfo;
        }

        @Override
        public void run() {
            try {
                if (factory == null) {
                    setupConnectionFactory(connectionInfo);
                }
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
                channel.basicQos(0, 1, false);
                channel.exchangeDeclare(connectionInfo.getExchange(), "direct", false, true, null);
                AMQP.Queue.DeclareOk q = channel.queueDeclare();
                channel.queueBind(q.getQueue(), connectionInfo.getExchange(), connectionInfo.getKey());

                Consumer consumer = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                        String message = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            message = new String(body, StandardCharsets.UTF_8);
                        }
                        if (!TextUtils.isEmpty(message)) {
                            Log.d(TAG, " Received message: " + message);
                            Log.d(TAG, " Current thread id: " + getId());
                            Log.d(TAG, "exchange: " + connectionInfo.getExchange() + "  key: " + connectionInfo.getKey());
                            EventBus.getDefault().post(message);
                        }
                    }
                };

                channel.basicConsume(q.getQueue(), true, consumer);
            } catch (Exception e1) {
                Log.e(TAG, "Connection broken dr: " + e1.getMessage());
            }
            Log.i(TAG, "Thread running on " + getId());
        }
    }
}
