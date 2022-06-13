package com.rrat.downloaddemo.viewmodel

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rrat.downloaddemo.network.Batch
import com.rrat.downloaddemo.network.LoadI
import com.rrat.downloaddemo.network.ResourceApiService
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okio.Okio
import java.io.File
import java.io.IOException
import java.util.concurrent.CountDownLatch

class DownLoadViewModel : ViewModel() {

    private var batchList: MutableList<Batch>? = mutableListOf()
    private val resourceApiService = ResourceApiService()

    //Observable data
    val isUrlLoaded = MutableLiveData<Boolean>()
    val isUrlFailed = MutableLiveData<Boolean>()

    val isBatchLoaded = MutableLiveData<Boolean>()
    val isBatchFailed = MutableLiveData<Boolean>()

    var countDownload = 0
    var countBatchDownload = 0

    fun addForDownload(url:String, localDir: String)
    {
        batchList?.add(Batch(url, localDir))
        //listUrlBase.add(url)
    }


    fun startDownload() {
        isUrlLoaded.postValue(false)
        isUrlFailed.postValue(false)
        countDownload = 0

        var countDownLatch: CountDownLatch

        if(batchList?.size!! > 0)
        {
            for(batch in batchList!!)
            {
                batch.hashMapUrl.clear()

                countDownLatch  = CountDownLatch(1)

                Log.i("DOWNLOAD", batch.urlDir)
                resourceApiService.getResponse(
                    object : Callback{
                        override fun onFailure(call: Call, e: IOException) {
                            countDownload +=1
                            isUrlFailed.postValue(true)
                            countDownLatch.countDown()
                            Log.i("DOWNLOAD", "Number Failed: $countDownload")
                        }

                        override fun onResponse(call: Call, response: Response) {

                            if(response.isSuccessful)
                            {
                                response.body()?.let { body -> addListToBatch(body.string(), batch) }
                                countDownload +=1
                                //Log.i("DOWNLOAD", "Number OK: $countDownload Local Dir: ${batch.listName}")
                                countDownLatch.countDown()
                                if(countDownload == batchList?.size!!)
                                {
                                    isUrlLoaded.postValue(true)
                                    countDownload = 0
                                }

                            }else{
                                Log.i("DOWNLOAD", "Response is not successful")
                                isUrlFailed.postValue(true)
                                countDownLatch.countDown()
                            }
                        }
                    }
                    , batch.urlDir)
                countDownLatch.await()
            }
        }


    }

    fun addListToBatch(rawResponse: String, batch: Batch){

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
                batch.addUrl(batch.urlDir+name, name)
            }

            htmlText = htmlText.drop(indexEndHyperLink + 3)
            indexStartHyperLink = htmlText.indexOf("<a")
            indexEndHyperLink = htmlText.indexOf("</a>")
        }

    }

    fun downloadBatch(context: Context, load: LoadI) {
        val contextWrapper = ContextWrapper(context)
        var countDownLatch: CountDownLatch
        isBatchLoaded.postValue(false)
        isBatchFailed.postValue(false)
        countBatchDownload = 0

        if(batchList?.size!! > 0)
        {
            for(batch in batchList!!)
            {
                val localDir = contextWrapper.getDir(batch.localDir, Context.MODE_PRIVATE)
                val sizeBatch = batch.hashMapUrl.size
                countDownLatch  = CountDownLatch(sizeBatch)
                for((name, url) in batch.hashMapUrl) {

                    //CHECK IF FILE EXIST
                    val newFile = File(localDir.absolutePath + "/" + name)
                    if(!newFile.exists())
                    {
                        resourceApiService.getResponse(
                            object : Callback{
                                override fun onFailure(call: Call, e: IOException) {
                                    countBatchDownload +=1
                                    isBatchFailed.postValue(true)
                                    Log.i("DOWNLOAD", "Number Failed: $countBatchDownload")
                                    if(countBatchDownload == sizeBatch)
                                    {
                                        Log.i("DOWNLOAD", "TOTAL: $sizeBatch")
                                        //isBatchLoaded.postValue(true)
                                        countBatchDownload = 0
                                    }
                                    countDownLatch.countDown()
                                }

                                override fun onResponse(call: Call, response: Response) {

                                    if(response.isSuccessful)
                                    {

                                        //SAVE
                                        val newFile = File(localDir.absolutePath + "/" + name)

                                        Log.i("DOWNLOAD", "Number OK: $countBatchDownload URL: $url path: ${localDir.absolutePath} name: $name")
                                        if(!newFile.exists())
                                        {
                                            val sink = Okio.buffer(Okio.sink(newFile))
                                            response.body()?.source()?.let { sink.writeAll(it) }
                                            sink.close()
                                        }
                                        load.onSuccess(countBatchDownload, sizeBatch)


                                        countBatchDownload +=1
                                        if(countBatchDownload == sizeBatch)
                                        {
                                            Log.i("DOWNLOAD", "TOTAL: $sizeBatch")
                                            //isBatchLoaded.postValue(true)
                                            countBatchDownload = 0

                                        }
                                        countDownLatch.countDown()
                                    }else{
                                        Log.i("DOWNLOAD", "Response is not successful")
                                        isBatchFailed.postValue(true)
                                        countDownLatch.countDown()
                                    }
                                }
                            }
                            , url)
                    }else{
                        countBatchDownload +=1
                        Log.i("DOWNLOAD", "FILE EXITS: $name")
                        countDownLatch.countDown()
                    }


                }
                countDownLatch.await()
                Log.i("DOWNLOAD", "FINISH BATCH $sizeBatch")
            }
            Log.i("DOWNLOAD", "FINISH DOWNLOAD")
            isBatchLoaded.postValue(true)
        }

    }


}