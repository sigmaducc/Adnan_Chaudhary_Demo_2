package com.example.matrimony.core.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityNetworkMonitor @Inject constructor(
	private val connectivityManager: ConnectivityManager
) : NetworkMonitor {

	override val isOnline: LiveData<Boolean> by lazy {
		val live = MutableLiveData<Boolean>()

		val current = connectivityManager.activeNetwork?.let { network ->
			val caps = connectivityManager.getNetworkCapabilities(network)
			caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
				caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
		} ?: false
		live.postValue(current)

		val callback = object : ConnectivityManager.NetworkCallback() {
			override fun onCapabilitiesChanged(network: android.net.Network, networkCapabilities: NetworkCapabilities) {
				val online = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
					networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
				live.postValue(online)
			}
			override fun onLost(network: android.net.Network) {
				live.postValue(false)
			}
		}

		connectivityManager.registerDefaultNetworkCallback(callback)

		live
	}
}