package com.tranvanphay.amqp_service.amqp_service.amqp.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.tranvanphay.amqp_service.amqp_service.R;
import com.tranvanphay.amqp_service.amqp_service.amqp.MainApplication;


public class NotificationCreator {
    private final String TAG = NotificationCreator.class.getSimpleName();
    public static final String NOTIFICATION_NEW_RECEIPT = "RECEIPT_CHANNEL";
    public static final String NOTIFICATION_FOREGROUND_SERVICE = "NOTIFICATION_FOREGROUND_SERVICE";
    public static final int REQUEST_ORDER_CODE = 0x10000;

    private static NotificationCreator sNotification = null;
    private Context mContext;
    private NotificationManager mNotificationManager;

    private NotificationCreator() {
        mContext = MainApplication.getAppContext();
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static synchronized NotificationCreator getInstance() {
        if (sNotification == null) {
            sNotification = new NotificationCreator();
        }
        return sNotification;
    }

    public void addNotification(int tag, String title, String message, PendingIntent contentIntent, PendingIntent pendingAccept, PendingIntent pendingDecline) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIFICATION_NEW_RECEIPT)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText(message)
                .setContentIntent(contentIntent)
                .setAutoCancel(false);

//        builder.addAction(R.drawable.my_location, mContext.getString(R.string.accept), pendingAccept);
//        builder.addAction(R.drawable.my_location, mContext.getString(R.string.decline), pendingDecline);

        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);



        mNotificationManager.notify(tag, builder.build());
    }

    public void addNotification(int tag, String title, String message, PendingIntent contentIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIFICATION_NEW_RECEIPT)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title)
                        .bigText(message))
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mNotificationManager.notify(tag, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createNotificationChannel() {
        NotificationChannel messageChannel = new NotificationChannel(NOTIFICATION_NEW_RECEIPT,
                mContext.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);

//        if (mAppPrefs.getPrefSound() != null) {
//            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            //String soundUri = CommonUtils.getSoundsInApp().get(mAppPrefs.getPrefSound());
//            if (alarmSound != null) {
//                AudioAttributes attr = new AudioAttributes.Builder()
//                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                        .build();
//                messageChannel.setSound(alarmSound, attr);
//            } else {
//                messageChannel.setSound(null, null);
//            }
//        }

        messageChannel.enableVibration(true);
        messageChannel.enableLights(true);
        messageChannel.setShowBadge(true);
        mNotificationManager.createNotificationChannel(messageChannel);
    }

    public void cancel(int tag) {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(tag);
        }
    }

    public void cancelAll() {
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
    }

    public static Notification createForegroundNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel serviceChannel = new NotificationChannel(NOTIFICATION_FOREGROUND_SERVICE,
                    context.getString(R.string.notification_service_channel_name), NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(serviceChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_FOREGROUND_SERVICE)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_description))
                .setSmallIcon(R.drawable.ic_notification);

        return builder.build();
    }
}
