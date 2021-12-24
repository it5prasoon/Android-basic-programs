# Login Example with MVVM, DataBinding With LiveData ( Kotlin Implementation)

This is a very simple **Login Example** using **MVVM pattern and DataBinding and LiveData** in Android. It takes input
from the UI using DataBinding **"@="**, stores it in LiveData and displays back on the UI.

This example is for those who want to learn the easiest way to get data from UI. This is useful in many ways such as
Saving Development Time, Code Reusability, Validations etc. No wonder it is being used all over Android Community.

So let's get started on using these technologies together in a single application:

1. What is MVVM?
2. What is DataBinding?
3. What is LiveData?
4. Implementation Step-by-Step
5. Conclusion

## **What is MVVM?**

**Answer:** MVVM is a design pattern for organizing GUI applications that has become popular on Android.

This concept will introduce you to the main 3 components of MVVM, the View, Model, and ViewModel.

The Model View ViewModel (MVVM) is an architectural pattern used in software engineering that originated from Microsoft
which is specialized in the Presentation Model design pattern. It is based on the Model-view-controller pattern (MVC),
and is targeted at modern UI development platforms (WPF and Silverlight) in which there is a UX developer who has
different requirements than a more "traditional" developer. MVVM is a way of creating client applications that leverages
core features of the WPF platform, allows for simple unit testing of application functionality, and helps developers and
designers work together with less technical difficulties.

**View:** A View is defined in XAML and should not have any logic in the code-behind. It binds to the view-model by only
using data binding.

**Model:** A Model is responsible for exposing data in a way that is easily consumable by WPF. It must implement
INotifyPropertyChanged and/or INotifyCollectionChanged as appropriate.

**ViewModel:** A ViewModel is a model for a view in the application or we can say as abstraction of the view. It exposes
data relevant to the view and exposes the behaviors for the views, usually with Commands.

**Model:**

- Definition, roles and responsibilities.
- What should go in your model layer and what shouldn't.
- Benefits of model isolation and how it affects testing.

**View:**

- Definition, roles and responsibilities.
- How it interacts with the ViewModel.

**ViewModel:**

- Definition, roles and responsibilities.
- How it supports the View, by providing actions and observable state.
- Interactions with the Model.
- Isolation from the View.

<img src="https://image.ibb.co/nmhxNK/2.png" alt="2" />

## **What is DataBinding?**

**Answer:** The Data Binding Library is a support library that allows you to bind UI components in your layouts to data
sources in your app using a declarative format rather than programmatically and many more.

<img src="https://preview.ibb.co/idDKme/3.png" alt="3" />

## **What is LiveData?**

**Answer:** LiveData is an observable data holder class. Unlike a regular observable, LiveData is lifecycle-aware,
meaning it respects the lifecycle of other app components, such as activities, fragments, or services. This awareness
ensures LiveData only updates app component observers that are in an active lifecycle state.

Using LiveData provides the following advantages:

**Ensures your UI matches your data state:**
LiveData follows the observer pattern. LiveData notifies Observer objects when the lifecycle state changes. You can
consolidate your code to update the UI in these Observer objects. Instead of updating the UI every time the app data
changes, your observer can update the UI every time there's a change.

**No memory leaks:**
Observers are bound to Lifecycle objects and clean up after themselves when their associated lifecycle is destroyed.

**No crashes due to stopped activities:**
If the observer's lifecycle is inactive, such as in the case of an activity in the back stack, then it doesn’t receive
any LiveData events.

**No more manual lifecycle handling:**
UI components just observe relevant data and don’t stop or resume observation. LiveData automatically manages all of
this since it’s aware of the relevant lifecycle status changes while observing.

**Always up to date data:**
If a lifecycle becomes inactive, it receives the latest data upon becoming active again. For example, an activity that
was in the background receives the latest data right after it returns to the foreground.

**Proper configuration changes:**
If an activity or fragment is recreated due to a configuration change, like device rotation, it immediately receives the
latest available data.

**Sharing resources:**
You can extend a LiveData object using the singleton pattern to wrap system services so that they can be shared in your
app. The LiveData object connects to the system service once, and then any observer that needs the resource can just
watch the LiveData object. For more information, see Extend LiveData.

<img src="https://preview.ibb.co/bx0qRe/4.png" alt="4" />

## **Implementation Step-by-Step**

### **Step1:** Adding DataBinding and Implementations in your Gradle File:

```Groovy
plugins {
    ...
    id 'kotlin-kapt'
}

android {
        ...
            dataBinding{
                enabled true
            }
        }
```

```Groovy
def lifecycle_version="2.4.0"

dependencies {
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    // Annotation processor
    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")
}
```

### **Step2:** Create a new class for the Model(LoginUser):

```Kotlin

import android.util.Patterns

class LoginUser( val strEmailAddress: String, val strPassword: String) {
    val isEmailValid: Boolean
        get() = Patterns.EMAIL_ADDRESS.matcher(strEmailAddress).matches()
    val isPasswordLengthGreaterThan5: Boolean
        get() = strPassword.length > 5
}
```

### **Step2:** Create a new class for the ViewModel(LoginViewModel):

```Kotlin

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
```

### **Step3:** The View class(MainActivity):

```Kotlin

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
```

### **Finally:** The XML File: (Important Changes Here)

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        xmlns:tools="http://schemas.android.com/tools">

    <data>
        <variable
                name="MainViewModel"
                type="com.matrix.mvvm_kotlin.ui.main.MainViewModel" />
    </data>

    <androidx.core.widget.NestedScrollView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:isScrollContainer="true">

        <androidx.constraintlayout.widget.ConstraintLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                tools:context=".View.MainActivity">

            <TextView
                    android:id="@+id/lblTitle"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_marginEnd="8dp"
                    android:layout_marginStart="8dp"
                    android:layout_marginTop="16dp"
                    android:lineSpacingExtra="8sp"
                    android:text="Login Example Using MVVM, DataBinding with LiveData"
                    android:textAlignment="center"
                    android:textSize="18sp"
                    android:textStyle="bold"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />

            <EditText
                    android:id="@+id/txtEmailAddress"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_marginEnd="32dp"
                    android:layout_marginStart="32dp"
                    android:layout_marginTop="32dp"
                    android:ems="10"
                    android:hint="E-Mail Address"
                    android:inputType="textEmailAddress"
                    android:text="@={MainViewModel.emailAddress}"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toBottomOf="@+id/lblTitle" />

            <EditText
                    android:id="@+id/txtPassword"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_marginEnd="32dp"
                    android:layout_marginStart="32dp"
                    android:layout_marginTop="16dp"
                    android:ems="10"
                    android:hint="Password"
                    android:inputType="textPassword"
                    android:text="@={MainViewModel.password}"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toBottomOf="@+id/txtEmailAddress" />

            <Button
                    android:id="@+id/btnLogin"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_marginEnd="32dp"
                    android:layout_marginStart="32dp"
                    android:layout_marginTop="32dp"
                    android:text="Click to Login"
                    android:onClick="@{(v) -> MainViewModel.onClick(v)}"
                    android:textSize="18sp"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toBottomOf="@+id/txtPassword" />

            <TextView
                    android:id="@+id/lblTagline"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginEnd="8dp"
                    android:layout_marginStart="8dp"
                    android:layout_marginTop="60dp"
                    android:text="See the Results Below From LiveDataBinding"
                    android:textColor="@android:color/background_dark"
                    android:gravity="center"
                    android:textSize="16sp"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toBottomOf="@+id/btnLogin" />

            <TextView
                    android:id="@+id/lblEmailAnswer"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginEnd="8dp"
                    android:layout_marginStart="8dp"
                    android:layout_marginTop="8dp"
                    android:text="---"
                    android:textColor="@android:color/holo_blue_light"
                    android:textSize="20sp"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toBottomOf="@+id/lblTagline" />

            <TextView
                    android:id="@+id/lblPasswordAnswer"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginBottom="32dp"
                    android:layout_marginEnd="8dp"
                    android:layout_marginStart="8dp"
                    android:layout_marginTop="8dp"
                    android:text="---"
                    android:textColor="@android:color/holo_blue_light"
                    android:textSize="20sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toBottomOf="@+id/lblEmailAnswer" />

        </androidx.constraintlayout.widget.ConstraintLayout>

    </androidx.core.widget.NestedScrollView>

</layout>
```

## **Conclusion**

Hopefully this guide should have helped you in making your tasks really easier in terms of many things, such as:
eliminating **findViewById(...)** and many more.



