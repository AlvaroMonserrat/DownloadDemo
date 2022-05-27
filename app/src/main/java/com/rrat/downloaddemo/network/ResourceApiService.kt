package com.rrat.downloaddemo.network

import android.util.Log
import okhttp3.*
import java.io.IOException

class ResourceApiService {

    private val client: OkHttpClient = OkHttpClient()

    fun getResponse(callback: Callback, url: String): Call {
        val request: Request = Request.Builder()
            .url(url)
            .build()

        val call = client.newCall(request)
        call.enqueue(callback)
        return call
    }

    fun getListResponse(urlList: List<String>) {
        Log.i("DOWNLOAD", urlList.size.toString())
        for(url in urlList)
        {
            val request: Request = Request.Builder()
                .url(url)
                .build()
            val call = client.newCall(request)
            call.enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Log.i("DOWNLOAD", e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.i("DOWNLOAD", response.body().toString())
                }

            })
        }
    }

    fun getListResponse(callback: Callback, urlList: MutableSet<String>) {
        Log.i("DOWNLOAD", urlList.size.toString())
        for(url in urlList)
        {
            val request: Request = Request.Builder()
                .url(url)
                .build()
            val call = client.newCall(request)
            call.enqueue(callback)
        }
    }
}