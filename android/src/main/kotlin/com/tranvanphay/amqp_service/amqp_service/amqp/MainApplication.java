package com.tranvanphay.amqp_service.amqp_service.amqp;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.tranvanphay.amqp_service.amqp_service.amqp.notification.NotificationCreator;


public class MainApplication extends Application {

    private static Context sContext;


    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCreator.getInstance().createNotificationChannel();
        }
    }

    public static Context getAppContext() {
        return sContext;
    }


}
