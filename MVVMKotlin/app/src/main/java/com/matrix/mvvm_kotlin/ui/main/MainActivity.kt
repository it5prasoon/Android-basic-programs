package com.matrix.mvvm_kotlin.ui.main

import android.os.Bundle
import android.text.TextUtils
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.matrix.mvvm_kotlin.R
import com.matrix.mvvm_kotlin.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    private var mainViewModel: MainViewModel? = null

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setting up the viewmodel
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Binding thae data
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel


        // Observing changes on LiveData
        mainViewModel!!.getUser()?.observe(this) {

            if (TextUtils.isEmpty(Objects.requireNonNull(it).strEmailAddress)) {
                binding.txtEmailAddress.error = "Enter an E-Mail Address"
                binding.txtEmailAddress.requestFocus()
            } else if (!it.isEmailValid) {
                binding.txtEmailAddress.error = "Enter a Valid E-mail Address"
                binding.txtEmailAddress.requestFocus()
            } else if (TextUtils.isEmpty(Objects.requireNonNull(it).strPassword)) {
                binding.txtPassword.error = "Enter a Password"
                binding.txtPassword.requestFocus()
            } else if (!it.isPasswordLengthGreaterThan5) {
                binding.txtPassword.error = "Enter at least 6 Digit password"
                binding.txtPassword.requestFocus()
            } else {
                binding.lblEmailAnswer.text = it.strEmailAddress
                binding.lblPasswordAnswer.text = it.strPassword
            }
        }


    }
}