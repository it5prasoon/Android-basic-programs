package com.matrix.overlay.services

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Dialog
import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.matrix.overlay.R
import com.matrix.overlay.preference.SharedPreference
import java.util.*

class AppCheckServices : Service() {
    private var context: Context? = null
    private var timer: Timer? = null
    var imageView: ImageView? = null
    private var windowManager: WindowManager? = null
    private var dialog: Dialog? = null
    lateinit var sharedPreference: SharedPreference
    var pakageName: List<String?>? = null


    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        sharedPreference = SharedPreference()
        pakageName = sharedPreference.getOverlay(this)
        timer = Timer("AppCheckServices")
        timer!!.schedule(updateTask, 1000L, 1000L)


        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        imageView = ImageView(this)
        imageView!!.visibility = View.GONE


        val LAYOUT_FLAG: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)
        params.gravity = Gravity.TOP or Gravity.CENTER
        params.x = applicationContext.resources.displayMetrics.widthPixels / 2
        params.y = applicationContext.resources.displayMetrics.heightPixels / 2
        windowManager!!.addView(imageView, params)
    }


    private val updateTask: TimerTask = object : TimerTask() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun run() {
            pakageName = sharedPreference.getOverlay(context!!)
            if (isConcernedAppIsInForeground) {
                Log.d("isConcernedAppIsInFrgnd", "true : The application is in foreground.")
                if (imageView != null) {
                    imageView!!.post {
                        if (currentApp!! != previousApp) {
                            showPolicyDialog()
                            previousApp = currentApp
                        } else {
                            Log.d("AppCheckService", "currentApp matches previous App")
                        }
                    }
                }
            } else {
                Log.d("isConcernedAppIsInFrgnd", "false")
                if (imageView != null) {
                    imageView!!.post {
                        //                            hideUnlockDialog();
                    }
                }
            }
        }
    }

    fun showPolicyDialog() {
        showDialog()
    }

    private fun showDialog() {
        if (context == null) context = applicationContext
        val layoutInflater = LayoutInflater.from(context)
        @SuppressLint("InflateParams") val promptsView = layoutInflater.inflate(R.layout.popup_unlock, null, false)
        val cancel = promptsView.findViewById<Button>(R.id.cancel)
        cancel.setOnClickListener { dialog!!.dismiss() }
        dialog = Dialog(context!!, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val LAYOUT_FLAG: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        dialog!!.window!!.setType(LAYOUT_FLAG)
        dialog!!.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog!!.setContentView(promptsView)
        dialog!!.window!!.setGravity(Gravity.CENTER)
        dialog!!.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && event.action == KeyEvent.ACTION_UP) {
                val startMain = Intent(Intent.ACTION_MAIN)
                startMain.addCategory(Intent.CATEGORY_HOME)
                startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(startMain)
            }
            true
        }
        dialog!!.show()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        /* We want this service to continue running until it is explicitly
        * stopped, so return sticky.
        */
        return START_STICKY
    }

    val isConcernedAppIsInForeground: Boolean
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
        get() {
            val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            manager.getRunningTasks(5)
            var mpackageName = manager.runningAppProcesses[0].processName
            val usage = context!!.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, time)
            if (stats != null) {
                val runningTask: SortedMap<Long, UsageStats> = TreeMap()
                for (usageStats in stats) {
                    runningTask[usageStats.lastTimeUsed] = usageStats
                }
                if (runningTask.isEmpty()) {
                    Log.d(TAG, "isEmpty Yes")
                    mpackageName = ""
                } else {
                    mpackageName = runningTask[runningTask.lastKey()]!!.packageName
                    Log.d(TAG, "isEmpty No : $mpackageName")
                }
            }
            var i = 0
            while (pakageName != null && i < pakageName!!.size) {
                Log.d("AppCheckService", "pakageName Size " + pakageName!!.size)
                if (mpackageName == pakageName!![i]) {
                    currentApp = pakageName!![i]
                    return true
                }
                i++
            }
            return false
        }

    override fun onDestroy() {
        super.onDestroy()
        timer!!.cancel()
        timer = null
        if (imageView != null) {
            windowManager!!.removeView(imageView)
        }
        try {
            if (dialog != null) {
                if (dialog!!.isShowing) {
                    dialog!!.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val TAG = "AppCheckServices"
        var currentApp: String? = ""
        var previousApp: String? = ""
    }
}