package com.matrix.overlay.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matrix.overlay.R
import com.matrix.overlay.adapter.ApplicationListAdapter
import com.matrix.overlay.adapter.GetListOfAppsAsyncTask
import com.matrix.overlay.data.AppInfo
import java.util.*

/*
Edited and written by Prasoon
*/
class AllAppFragment : Fragment() {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var progressDialog: ProgressDialog? = null
    var list: MutableList<AppInfo>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_all_apps, container, false)
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage("Loading Apps ...")
        mRecyclerView = v.findViewById<View>(R.id.my_recycler_view) as RecyclerView
        mRecyclerView!!.setHasFixedSize(true)
        list = ArrayList()
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        mRecyclerView!!.layoutManager = mLayoutManager
        mAdapter = activity?.let { requiredAppsType?.let { it1 -> ApplicationListAdapter(list as ArrayList<AppInfo>, it, it1) } }
        mRecyclerView!!.adapter = mAdapter
        val task = GetListOfAppsAsyncTask(this)
        task.execute(requiredAppsType)
        return v
    }

    fun showProgressBar() {
        mRecyclerView!!.visibility = View.INVISIBLE
        progressDialog!!.show()
    }

    fun hideProgressBar() {
        progressDialog!!.dismiss()
        mRecyclerView!!.visibility = View.VISIBLE
    }

    fun updateData(list: List<AppInfo>?) {
        this.list!!.clear()
        this.list!!.addAll(list!!)
        mAdapter!!.notifyDataSetChanged()
    }

    companion object {
        var requiredAppsType: String? = null
        fun newInstance(requiredApps: String?): AllAppFragment {
            requiredAppsType = requiredApps
            return AllAppFragment()
        }
    }
}