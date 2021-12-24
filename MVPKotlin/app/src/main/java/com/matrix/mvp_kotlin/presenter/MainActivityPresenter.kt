package com.matrix.mvp_kotlin.presenter

import com.matrix.mvp_kotlin.model.User


class MainActivityPresenter(view: View) {

    private val user: User = User("", "")
    private val view: View

    fun updateFullName(fullName: String?) {
        if (fullName != null) {
            user.fullName = fullName
        }
//        Log.d("FullName", "updateFullName: $fullName")
        view.updateUserInfoTextView(user.toString())
    }

    fun updateEmail(email: String?) {
        if (email != null) {
            user.email = email
        }
        view.updateUserInfoTextView(user.toString())
    }

    interface View {
        fun updateUserInfoTextView(info: String?)
        fun showProgressBar()
        fun hideProgressBar()
    }

    init {
        this.view = view
    }
}
