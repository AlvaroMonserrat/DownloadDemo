package com.rrat.downloaddemo.viewmodel

import android.app.DownloadManager
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rrat.downloaddemo.network.ResourceApiService
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okio.Okio
import java.io.File
import java.io.IOException
import kotlin.math.sin

class DownLoadViewModel : ViewModel() {

    private val resourceApiService = ResourceApiService()
    private val listHref = mutableListOf<String>()
    val listName = mutableListOf<String>()

    //Observable data
    val isUrlLoaded = MutableLiveData<Boolean>()
    val isUrlFailed = MutableLiveData<Boolean>()

    fun getResponse(url:String)
    {
        isUrlLoaded.value = false
        isUrlFailed.value = false
        resourceApiService.getResponse(
            object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Log.i("DOWNLOAD", e.toString())
                    isUrlFailed.postValue(true)
                }

                override fun onResponse(call: Call, response: Response) {
                    if(response.isSuccessful)
                    {
                        response.body()?.let { body -> getStringHref(body.string(), url) }
                        isUrlLoaded.postValue(true)
                    }else{
                        Log.i("DOWNLOAD", "Response is not successful")
                        isUrlFailed.postValue(true)
                    }

                }
            }
        , url)
    }

    fun getStringHref(rawResponse: String, url: String?) {
        listHref.clear()
        listName.clear()

        var htmlText = rawResponse
        var indexStartHyperLink = htmlText.indexOf("<a")
        var indexEndHyperLink = htmlText.indexOf("</a>")
        while(indexStartHyperLink != -1){
            val subText: String = htmlText.subSequence(indexStartHyperLink, indexEndHyperLink) as String
            val indexEnd = subText.indexOf(">")
            val name = subText.subSequence(9, indexEnd-1) as String



            if(name.contains("jpg")
                or name.contains("png")
                or name.contains("jpeg")
                or name.contains("bmp")
                or name.contains("JPG")
                or name.contains("JPEG")
                or name.contains("mp3"))
            {
                listHref.add(url+name)
                listName.add(name)
            }

            htmlText = htmlText.drop(indexEndHyperLink + 3)
            indexStartHyperLink = htmlText.indexOf("<a")
            indexEndHyperLink = htmlText.indexOf("</a>")
        }
    }

    fun downloadFiles(context: Context)
    {
        var countDownload = 0
        val contextWrapper = ContextWrapper(context)
        val localDir = contextWrapper.getDir("audios", Context.MODE_PRIVATE)
        for(index in listHref.indices)
        {
            val file = File(localDir.absolutePath + "/" + listName[index])
            if(!file.exists())
            {
                //DOWNLOAD FILE
                Log.i("DOWNLOAD", listHref[index])
            }
        }
        
        resourceApiService.getListResponse(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                countDownload += 1
                Log.i("DOWNLOAD", "FAILURE $countDownload")
            }

            override fun onResponse(call: Call, response: Response) {
                countDownload += 1
                Log.i("DOWNLOAD", "RESPONSE $countDownload")
                val file = File(localDir.absolutePath + "/" + listName[countDownload-1])
                val sink = Okio.buffer(Okio.sink(file))
                response.body()?.source()?.let { sink.writeAll(it) }
                sink.close()
            }
        }, listHref)
    }
}