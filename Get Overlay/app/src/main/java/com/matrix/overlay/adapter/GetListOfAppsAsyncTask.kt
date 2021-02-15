package com.matrix.overlay.adapter

import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiConfiguration.AuthAlgorithm.strings
import android.os.AsyncTask
import com.matrix.overlay.data.AppInfo
import com.matrix.overlay.fragments.AllAppFragment
import com.matrix.overlay.preference.SharedPreference
import com.matrix.overlay.utils.SomeConstants
import java.util.*

/*
Edited and written by Prasoon
*/
open class GetListOfAppsAsyncTask(var container: AllAppFragment?) : AsyncTask<String?, Void?, List<AppInfo>?>() {
    override fun doInBackground(vararg params: String?): List<AppInfo>? {
        val requiredAppsType = strings[0]
        val list = getListOfInstalledApp(container!!.activity)
        val sharedPreference = SharedPreference()
        val lockedFilteredAppList: MutableList<AppInfo> = ArrayList()
        val unlockedFilteredAppList: MutableList<AppInfo> = ArrayList()
        var flag: Boolean
        if (requiredAppsType == SomeConstants.LOCKED || requiredAppsType == SomeConstants.UNLOCKED) {
            for (i in list!!.indices) {
                flag = true
                if (sharedPreference.getOverlay(container!!.activity!!) != null) {
                    for (j in sharedPreference.getOverlay(container!!.activity!!)!!.indices) {
                        if (list[i].packageName!! == sharedPreference.getOverlay(container!!.activity!!)!![j]) {
                            lockedFilteredAppList.add(list[i])
                            flag = false
                        }
                    }
                }
                if (flag) {
                    unlockedFilteredAppList.add(list[i])
                }
            }
            if (requiredAppsType == SomeConstants.LOCKED) {
                list.clear()
                list.addAll(lockedFilteredAppList)
            } else if (requiredAppsType == SomeConstants.UNLOCKED) {
                list.clear()
                list.addAll(unlockedFilteredAppList)
            }
        }
        return list
    }

    override fun onPreExecute() {
        super.onPreExecute()
        container!!.showProgressBar()
    }

    override fun onPostExecute(appInfos: List<AppInfo>?) {
        super.onPostExecute(appInfos)
        if (container != null && container!!.activity != null) {
            container!!.hideProgressBar()
            container!!.updateData(appInfos)
        }
    }

    companion object {
        /**
         * get the list of all installed applications in the device
         * @return ArrayList of installed applications or null
         **/
        fun getListOfInstalledApp(context: Context?): MutableList<AppInfo>? {
            val packageManager = context!!.packageManager
            val installedApps = ArrayList<AppInfo>()
            val apps = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)

            /**
             * we have to extract apps which are on database from
             * list of installed application on the device done using some algorithms.
             **/
            if (apps.isNotEmpty()) {
                for (i in apps.indices) {
                    val p = apps[i]
                    try {
                        if (null != packageManager.getLaunchIntentForPackage(p.packageName)) {
                            // appInfo = packageManager.getApplicationInfo(p.packageName, 0);
                            val app = AppInfo()
                            app.name = p.applicationInfo.loadLabel(packageManager).toString()
                            app.packageName = p.packageName
                            app.versionName = p.versionName
                            app.versionCode = p.versionCode
                            app.icon = p.applicationInfo.loadIcon(packageManager)
                            installedApps.add(app)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                return installedApps
            }
            return null
        }
    }

    fun searchTree() {

    }
}