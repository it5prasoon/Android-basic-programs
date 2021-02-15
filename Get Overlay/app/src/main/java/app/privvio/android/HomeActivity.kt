package app.privvio.android

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import app.privvio.android.fragments.AllAppFragment
import app.privvio.android.utils.SomeConstants

/*
Edited and written by Prasoon
*/   class HomeActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    var editor: Editor? = null
    var fragmentManager: FragmentManager? = null
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        sharedPreferences = getSharedPreferences(SomeConstants.MyPREFERENCES, MODE_PRIVATE)
        editor = sharedPreferences.edit()
        fragmentManager = supportFragmentManager
        supportActionBar!!.setTitle("All Applications")
        val f: Fragment = AllAppFragment.newInstance(SomeConstants.ALL_APPS)
        fragmentManager!!.beginTransaction().replace(R.id.fragment_container, f).commit()
    }
}