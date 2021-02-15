package app.privvio.android.data

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable

/*
Edited and written by Prasoon
 */
class AppInfo {
    var name: String? = null
    var packageName: String? = null
    var versionName: String? = null
    var versionCode = 0
    var icon: Drawable? = null
    fun getLaunchIntent(context: Context): Intent? {
        return context.packageManager.getLaunchIntentForPackage(packageName!!)
    }
}