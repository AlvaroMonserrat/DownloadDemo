package com.rrat.downloaddemo.network

class Batch(val urlDir: String, val localDir: String) {
    val hashMapUrl = LinkedHashMap<String, String>()
    fun addUrl(url:String, name: String)
    {
        hashMapUrl[name] = url
    }
}