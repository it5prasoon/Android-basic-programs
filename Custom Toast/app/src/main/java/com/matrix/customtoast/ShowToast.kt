package com.matrix.customtoast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager

class ShowToast : AppCompatActivity() {

    private var fragmentManager: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_toast)
        fragmentManager = supportFragmentManager
        if (savedInstanceState == null) {
            fragmentManager!!
                .beginTransaction()
                .replace(R.id.frameContainer, ShowToastFragment(),
                    "Toast Fragment").commit()
        }
    }
}