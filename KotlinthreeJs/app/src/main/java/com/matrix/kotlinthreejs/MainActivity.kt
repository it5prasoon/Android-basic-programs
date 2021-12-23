package com.matrix.kotlinthreejs

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import com.google.android.material.button.MaterialButton


class MainActivity : AppCompatActivity() {


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getWebview()
    }


    @SuppressLint("SetJavaScriptEnabled")
    fun getWebview() {
        val webView: WebView = findViewById(R.id.web_view)
        val callJavascript: MaterialButton = findViewById(R.id.call_javascript)

        val assetLoader: WebViewAssetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(this))
            .build()

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.allowContentAccess = true
        webView.settings.allowFileAccess = true

        webView.addJavascriptInterface(WebAppInterface(this), "android")
        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html")

        callJavascript.setOnClickListener {
            // Method 1
            webView.loadUrl("javascript:myFunction();")

            // Method 2
//            webView.evaluateJavascript("" +
//                    "(function() " +
//                    "{ " +
//                    "const myEvent = new Event('myevent'); " +
//                    "document.dispatchEvent(myEvent); " +
//                    "})();"
//            ) { }
        }
    }
}