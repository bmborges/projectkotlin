package dev.brunomborges.projectkotlin.services

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import dev.brunomborges.projectkotlin.R

class NotificationService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val notification: NotificationCompat.Builder = NotificationCompat.Builder(context, "INFNET")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Sua tarefa mais nova vence agora!")
                .setContentText("Fique atento pois sua próxima tarefa vencerá agora!!!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(0, notification.build())
        }
    }


}