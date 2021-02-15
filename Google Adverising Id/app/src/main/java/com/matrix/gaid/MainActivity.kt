package com.matrix.gaid

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        getUIDs()

    }




    private fun getUIDs() {

        val task: AsyncTask<Void?, Void?, String?> = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<Void?, Void?, String?>() {
            override fun doInBackground(vararg params: Void?): String? {
                var idInfo: AdvertisingIdClient.Info? = null
                try {
                    idInfo = AdvertisingIdClient.getAdvertisingIdInfo(applicationContext)
                } catch (e: GooglePlayServicesNotAvailableException) {
                    e.printStackTrace()
                } catch (e: GooglePlayServicesRepairableException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                var advertId: String? = null
                try {
                    advertId = idInfo!!.id
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
                return advertId
            }

            override fun onPostExecute(advertId: String?) {
                val toastDurationInMilliSeconds = 1000
                val toastCountDown: CountDownTimer

                val toast: Toast = Toast.makeText(applicationContext, advertId, Toast.LENGTH_LONG)
                toastCountDown = object : CountDownTimer(toastDurationInMilliSeconds.toLong(), 1000 /*Tick duration*/) {
                    override fun onTick(millisUntilFinished: Long) {
                        toast.show()
                    }

                    override fun onFinish() {
                        toast.cancel()
                    }
                }

                toast.show();
                toastCountDown.start();
                val textView: TextView = findViewById(R.id.gaid)
                textView.text = advertId

            }

        }
        task.execute()
    }


}