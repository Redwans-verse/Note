package com.example.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkHelper {

  const val NO_INTERNET_MESSAGE = "ইন্টারনেট সংযোগ নেই। অনুগ্রহ করে ইন্টারনেট সংযোগ পরীক্ষা করে আবার চেষ্টা করুন।"

  fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return true
    val activeNetwork = connectivityManager.activeNetwork ?: return true
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return true
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
  }
}
