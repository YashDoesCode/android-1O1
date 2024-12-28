package com.keeper.lolock.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.keeper.lolock.R;
import com.keeper.lolock.ui.MainActivity;

import java.util.concurrent.TimeUnit;

public final class PasswordExpiryWorker extends Worker {

    public static final String WORK_NAME = "KeeperLoLockPasswordExpiryCheck";
    private static final String CHANNEL_ID = "keeper_lolock_security_channel";
    private static final int NOTIFICATION_ID = 1001;

    public PasswordExpiryWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SecurityPreferences prefs = new SecurityPreferences(getApplicationContext());
        if (prefs.isRotationDue()) {
            sendExpiryNotification();
        }
        return Result.success();
    }

    private void sendExpiryNotification() {
        Context context = getApplicationContext();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notif_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Periodic 6-month security reminder notifications");
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(context.getString(R.string.notif_expiry_title))
                .setContentText(context.getString(R.string.notif_expiry_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notif_expiry_body)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        manager.notify(NOTIFICATION_ID, builder.build());
    }

    public static void schedulePeriodicCheck(Context context) {
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                PasswordExpiryWorker.class,
                24,
                TimeUnit.HOURS
        )
                .setConstraints(new Constraints.Builder().build())
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
        );
    }
}
