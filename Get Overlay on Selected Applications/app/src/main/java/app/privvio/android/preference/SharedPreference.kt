package app.privvio.android.preference

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.google.gson.Gson
import app.privvio.android.utils.SomeConstants
import java.util.*

/*
Edited and written by Prasoon
*/
class SharedPreference {
    fun saveOverlay(context: Context, lockedApp: List<String?>?) {
        val editor: Editor
        val settings: SharedPreferences = context.getSharedPreferences(SomeConstants.MyPREFERENCES,
                Context.MODE_PRIVATE)
        editor = settings.edit()
        val gson = Gson()
        val jsonLockedApp = gson.toJson(lockedApp)
        editor.putString(LOCKED_APP, jsonLockedApp)
        editor.apply()
    }

    fun addOverlay(context: Context, app: String?) {
        var lockedApp: MutableList<String?>? = getOverlay(context)
        if (lockedApp == null) lockedApp = ArrayList()
        lockedApp.add(app)
        saveOverlay(context, lockedApp)
    }

    fun removeOverlay(context: Context, app: String?) {
        val locked = getOverlay(context)
        if (locked != null) {
            locked.remove(app)
            saveOverlay(context, locked)
        }
    }

    fun getOverlay(context: Context): ArrayList<String?>? {
        var locked: List<String?>?
        val settings: SharedPreferences = context.getSharedPreferences(SomeConstants.MyPREFERENCES,
                Context.MODE_PRIVATE)
        if (settings.contains(LOCKED_APP)) {
            val jsonLocked = settings.getString(LOCKED_APP, null)
            val gson = Gson()
            val lockedItems = gson.fromJson(jsonLocked,
                    Array<String>::class.java)
            locked = listOf(*lockedItems)
            locked = ArrayList(locked)
        } else return null
        return locked
    }

    companion object {
        const val LOCKED_APP = "locked_app"
    }
}