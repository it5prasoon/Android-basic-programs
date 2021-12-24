package com.matrix.mvvm_kotlin.ui.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.matrix.mvvm_kotlin.model.LoginUser


class MainViewModel : ViewModel() {

    var emailAddress = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    private var userMutableLiveData: MutableLiveData<LoginUser>? = null

    fun getUser(): MutableLiveData<LoginUser>? {
        if (userMutableLiveData == null) {
            userMutableLiveData = MutableLiveData()
        }
        return userMutableLiveData
    }

    fun onClick(view: View?) {
        val loginUser = LoginUser(emailAddress.value!!, password.value!!)
        userMutableLiveData!!.value = loginUser
    }
}