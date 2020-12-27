package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var downloadManager: DownloadManager
    private var downloadID: Long = 0
    private val TAG = "MainActivity"

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
        ) as NotificationManager

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            when(radio_group.checkedRadioButtonId){
                R.id.glide_radio_button -> download(URL_GLIDE)
                R.id.project_radio_button -> download(URL_PROJECT)
                R.id.retrofit_radio_button -> download(URL_RETROFIT)
                else -> Toast.makeText(this, "Please select the file to download", Toast.LENGTH_SHORT).show()
            }
            custom_button.buttonState = ButtonState.Loading
        }

        createChannel(
                getString(R.string.download_notification_channel_id),
                getString(R.string.download_notification_channel_name)
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if(downloadID == id) {
                Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
            }
            var finishDownload = false
            var progress: Int

            val contentIntent = Intent(applicationContext, DetailActivity::class.java)
            when(radio_group.checkedRadioButtonId){
                R.id.glide_radio_button -> {
                    contentIntent.putExtra(FILENAME_EXTRA,getString(R.string.glide_filename))
                }
                R.id.project_radio_button -> {
                    contentIntent.putExtra(FILENAME_EXTRA,getString(R.string.project_filename))
                }
                R.id.retrofit_radio_button -> {
                    contentIntent.putExtra(FILENAME_EXTRA,getString(R.string.retrofit_filename))
                }
            }

            while (!finishDownload){
                val cursor: Cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
                if(cursor.moveToFirst()){
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    when (status) {
                        DownloadManager.STATUS_FAILED -> {
                            finishDownload = true
                            contentIntent.putExtra(STATUS_EXTRA,"Failed")
                            pendingIntent = PendingIntent.getActivity(
                                    applicationContext,
                                    NOTIFICATION_ID,
                                    contentIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            )
                            notificationManager.sendNotification(getString(R.string.notification_description), applicationContext,pendingIntent)
                        }
                        DownloadManager.STATUS_PAUSED -> {
                        }
                        DownloadManager.STATUS_PENDING -> {
                        }
                        DownloadManager.STATUS_RUNNING -> {
                            val total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            if (total >= 0) {
                                val downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                progress = (downloaded * 100L / total).toInt()


                            }
                        }
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            progress = 100

                            finishDownload = true
                            custom_button.buttonState = ButtonState.Completed

                            contentIntent.putExtra(STATUS_EXTRA,"Success")
                            pendingIntent = PendingIntent.getActivity(
                                    applicationContext,
                                    NOTIFICATION_ID,
                                    contentIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            )
                            notificationManager.sendNotification(getString(R.string.notification_description), applicationContext,pendingIntent)
                        }
                    }
                }
            }
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

    }

    companion object {
        private const val URL_PROJECT =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_GLIDE = "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_RETROFIT = "https://github.com/square/retrofit/archive/master.zip"

        private const val CHANNEL_ID = "channelId"
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Time for breakfast"
            val notificationManager = getSystemService(
                    NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
