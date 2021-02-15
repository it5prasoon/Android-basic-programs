package com.matrix.overlay.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootComplete : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("football ", "BootComplete")
        context.startService(Intent(context, AppCheckServices::class.java))

        /*-------alarm setting after boot again--------*/
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 999, alarmIntent, 0)
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val interval = 86400 * 1000 / 4
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval.toLong(), pendingIntent)
    }
}