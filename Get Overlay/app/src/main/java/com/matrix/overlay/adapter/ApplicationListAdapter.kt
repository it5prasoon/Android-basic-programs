package com.matrix.overlay.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.matrix.overlay.R
import com.matrix.overlay.data.AppInfo
import com.matrix.overlay.preference.SharedPreference
import java.util.*

/*
Edited and written by Prasoon
*/
@SuppressLint("UseSwitchCompatOrMaterialCode")
class ApplicationListAdapter(appInfoList: List<AppInfo?>, context: Context, requiredAppsType: String) : RecyclerView.Adapter<ApplicationListAdapter.ViewHolder>() {
    var installedApps: ArrayList<*> = ArrayList<Any?>()
    private val context: Context
    var sharedPreference: SharedPreference
    var requiredAppsType: String

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        // each data item is just a string in this case
        var applicationName: TextView = v.findViewById<View>(R.id.applicationName) as TextView
        var cardView: CardView = v.findViewById<View>(R.id.card_view) as CardView
        var icon: ImageView = v.findViewById<View>(R.id.icon) as ImageView

        var switchView: Switch = v.findViewById<View>(R.id.switchView) as Switch

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val appInfo = installedApps[position] as AppInfo
        holder.applicationName.text = appInfo.name
        holder.icon.setBackgroundDrawable(appInfo.icon)
        holder.switchView.setOnCheckedChangeListener(null)
        holder.cardView.setOnClickListener(null)
        holder.switchView.isChecked = checkLockedItem(appInfo.packageName)
        holder.switchView.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                //AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Lock Clicked", "lock_clicked", appInfo.getPackageName());
                sharedPreference.addOverlay(context, appInfo.packageName)
            } else {
                //AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Unlock Clicked", "unlock_clicked", appInfo.getPackageName());
                sharedPreference.removeOverlay(context, appInfo.packageName)
            }
        }
        holder.cardView.setOnClickListener { holder.switchView.performClick() }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return installedApps.size
    }

    /*Checks whether a particular app exists in SharedPreferences*/
    private fun checkLockedItem(checkApp: String?): Boolean {
        var check = false
        val locked: List<String?>? = sharedPreference.getOverlay(context)
        if (locked != null) {
            for (lock in locked) {
                if (lock == checkApp) {
                    check = true
                    break
                }
            }
        }
        return check
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    init {
        installedApps = appInfoList as ArrayList<*>
        this.context = context
        this.requiredAppsType = requiredAppsType
        sharedPreference = SharedPreference()
    }
}