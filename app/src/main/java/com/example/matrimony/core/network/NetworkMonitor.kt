package com.example.matrimony.core.network

import androidx.lifecycle.LiveData

interface NetworkMonitor {
	val isOnline: LiveData<Boolean>
}