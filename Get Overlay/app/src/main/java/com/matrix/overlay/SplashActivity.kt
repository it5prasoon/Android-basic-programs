package com.matrix.overlay

import android.app.AppOpsManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.matrix.overlay.services.AppForegroundCheckService


/*
Edited and written by Prasoon
*/
class SplashActivity : AppCompatActivity() {
    var context: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext
        setContentView(R.layout.activity_splash)
        checkPermissions()
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val dialogFragment = OverlayPermissionDialogFragment()
                dialogFragment.show(supportFragmentManager, "Overlay Permission")
            } else if (!hasUsageStatsPermission()) {
                val ft = supportFragmentManager.beginTransaction()
                val dialogFragment = UsageAcessDialogFragment()
                ft.add(dialogFragment, null)
                ft.commitAllowingStateLoss()
            } else {
                startService()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //No call for super().
        super.onSaveInstanceState(outState)
    }

    private fun startService() {
        val input = "Get Overlay is Running!!"
        val serviceIntent = Intent(this, AppForegroundCheckService::class.java)
        serviceIntent.putExtra("inputExtra", input)
        ContextCompat.startForegroundService(this, serviceIntent)
        Handler().postDelayed({
            val i = Intent(this@SplashActivity, HomeActivity::class.java)
            startActivity(i)
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }

     fun stopService(v: View?) {
        val serviceIntent = Intent(this, AppForegroundCheckService::class.java)
        stopService(serviceIntent)
    }

//    fun startService() {
//        startService(Intent(this@SplashActivity, AppCheckServices::class.java))
//        try {
//            val alarmIntent = Intent(context, AlarmReceiver::class.java)
//            val manager = context!!.getSystemService(ALARM_SERVICE) as AlarmManager
//            val pendingIntent = PendingIntent.getBroadcast(context, 999, alarmIntent, 0)
//            val interval = 86400 * 1000 / 4
//            manager?.cancel(pendingIntent)
//            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval.toLong(), pendingIntent)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /** check if received result code
         * is equal our requested code for draw permission   */
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("SplashActivity", "cp 1")
        checkPermissions()
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        var mode = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(), packageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    class OverlayPermissionDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(activity!!)
            builder.setMessage(R.string.ovarlay_permission_description)
                    .setTitle("Overlay Permission")
                    .setPositiveButton("Allow") { dialog, id -> // FIRE ZE MISSILES!
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + activity!!.packageName))
                        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE)
                    }

            // Create the AlertDialog object and return it
            return builder.create()
        }
    }

    class UsageAcessDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(activity!!)
            builder.setMessage(R.string.usage_data_access_description)
                    .setTitle("Usage Access Permission")
                    .setPositiveButton("Allow") { dialog, id -> // FIRE ZE MISSILES!
                        startActivityForResult(
                                Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                                MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS)
                    }

            // Create the AlertDialog object and return it
            return builder.create()
        }
    }

    companion object {
        private const val SPLASH_TIME_OUT = 1000
        var OVERLAY_PERMISSION_REQ_CODE = 1234
        var MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 12345
    }
}