package com.example.matrimony.core.images

import android.widget.ImageView

interface ImageLoader {
	fun load(target: ImageView, url: String?, placeholderResId: Int? = null)
}