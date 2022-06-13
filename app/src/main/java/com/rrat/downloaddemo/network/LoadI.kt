package com.rrat.downloaddemo.network

interface LoadI {
    fun onSuccess(count: Int, total: Int)
}