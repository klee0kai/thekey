package com.github.klee0kai.thekey.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.MainActivity
import com.github.klee0kai.thekey.core.utils.common.GlobalJobsCollection
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import com.github.klee0kai.thekey.core.R as CoreR

class UnfinishedJobsService : Service() {

    private val scope = DI.mainThreadScope()
    private val notificationManager by lazy { getSystemService(NOTIFICATION_SERVICE) as NotificationManager }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        scope.launch {
            if (notificationManager.getNotificationChannel(NOT_FINISHED_JOBS_CHANNEL) == null) {
                notificationManager.createNotificationChannel(
                    NotificationChannel(
                        NOT_FINISHED_JOBS_CHANNEL,
                        NOT_FINISHED_JOBS_CHANNEL,
                        NotificationManager.IMPORTANCE_LOW
                    ).apply {
                        description = getString(CoreR.string.not_finished_jobs)
                    }
                )
            }

            val jobDescriptions = GlobalJobsCollection.globalJobs.value
                .map { getText(it.descriptionRes) }
                .joinToString("\n") { it }

            val notification = Notification.Builder(baseContext, NOT_FINISHED_JOBS_CHANNEL)
                .setChannelId(NOT_FINISHED_JOBS_CHANNEL)
                .setContentTitle(getText(CoreR.string.not_finished_jobs))
                .setContentText(jobDescriptions)
                .setSmallIcon(CoreR.drawable.logo)
                .setContentIntent(
                    PendingIntent.getActivity(
                        baseContext,
                        0,
                        Intent(baseContext, MainActivity::class.java),
                        PendingIntent.FLAG_IMMUTABLE,
                    )
                )
                .build()


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOT_FINISHED_JOBS_CHANNEL_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
                )
            } else {
                startForeground(NOT_FINISHED_JOBS_CHANNEL_ID, notification)
            }

            // wait to close self
            GlobalJobsCollection.globalJobs
                .debounce(1.seconds)
                .firstOrNull { jobs -> jobs.isEmpty() }

            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.coroutineContext.cancelChildren()
    }


    companion object {
        const val NOT_FINISHED_JOBS_CHANNEL = "not_finished"
        const val NOT_FINISHED_JOBS_CHANNEL_ID = 1
    }

}